package com.example.anthonyanca.videoface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {



    //private LinearLayout listaEventos;
    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private Button boton;
    public ListView listViewEventos;

    private ArrayList<Eventos> ListEventos;

    private GoogleApiClient googleApiClient;

    //private FirebaseFirestore dbEventos;
    private DatabaseReference dbEventos;
    private Firebase dRef;


    private ArrayList<String> nomEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //listaEventos = (LinearLayout) findViewById(R.id.eventos);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        photoImageView.setVisibility(View.VISIBLE);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);
        listViewEventos = (ListView) findViewById(R.id.lvEventos);

        ArrayList<botonesEventos> btnEventos = new ArrayList<botonesEventos>();

        ListEventos = new ArrayList<>();

        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,nomEventos);

        dbEventos = FirebaseDatabase.getInstance().getReference("Eventos");
        //dRef = new Firebase("https://videoface-8f1a4.firebaseio.com/Eventos");





        /*dbEventos.collection("Eventos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        dbEventos.collection("Eventos/"+document.getData().toString()).document().get().
                        nomEventos.add(document.getData().toString());
                        cantidadEventos++;
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Error al Oobtener la lista de evntos",Toast.LENGTH_SHORT).show();
                }
            }
        });*/




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();



        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        dbEventos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListEventos.clear();
                for(DataSnapshot eventSnapshot : dataSnapshot.getChildren()){

                    Eventos Eventos = eventSnapshot.getValue(Eventos.class);

                    ListEventos.add(Eventos);
                }
                AdaptadorEventos Adaptador =  new AdaptadorEventos(MainActivity.this, ListEventos);
                listViewEventos.setAdapter(Adaptador);

                listViewEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, EventoActivity.class);
                        intent.putExtra("ObjEvento", ListEventos.get(position));
                        String pos = Integer.toString(position);
                        intent.putExtra("number", pos);
                        intent.putExtra("email", emailTextView.getText().toString());
                        intent.putExtra("idGmail", idTextView.getText().toString());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){

            GoogleSignInAccount account = result.getSignInAccount();

            if(account.getDisplayName() == null)
            {
                String sincorreo = account.getEmail() + "(Sin Nombre)";
                nameTextView.setText(sincorreo);
            }
            else
            {
                nameTextView.setText(account.getDisplayName());
            }
            emailTextView.setText(account.getEmail());
            idTextView.setText(account.getId());

            String email = emailTextView.getText().toString();
            String password = idTextView.getText().toString();

            if(account.getPhotoUrl()== null){
                //photoImageView.setVisibility(View.INVISIBLE);
                Glide.with(this).load("https://i.pinimg.com/originals/60/99/f3/6099f305983371dadaceae99f5c905bf.png").centerCrop().into(photoImageView);
            }
            else {

                Glide.with(this).load(account.getPhotoUrl()).centerCrop().into(photoImageView);
            }
            photoImageView.setVisibility(View.VISIBLE);

        } else {
            goLogInScreen();
        }
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this,LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logOut(View view) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()){
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void revoke(View view) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_revoke, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    class botonesEventos{
        public String nombre;

        public botonesEventos(String nombre) {
            this.nombre = nombre;
        }


    }
}
