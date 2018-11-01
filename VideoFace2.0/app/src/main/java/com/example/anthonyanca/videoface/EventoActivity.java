package com.example.anthonyanca.videoface;

import android.media.Image;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class EventoActivity extends AppCompatActivity {

    private Eventos UnEvento;
    private String Number;
    private String userEmail;
    private String userId;
    private TextView txtUnEventoTitle, txtUnEventoDescription;
    private ImageView imgUnEvento;
    private Button btnInscribir;

    private Firebase RootRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.eventolayout);

        UnEvento = (Eventos) getIntent().getSerializableExtra("ObjEvento");
        Number = (String) getIntent().getSerializableExtra("number");
        userEmail = (String) getIntent().getSerializableExtra("email");
        userId = (String) getIntent().getSerializableExtra("idGmail");

        txtUnEventoTitle = findViewById(R.id.txtEventoTitle);
        txtUnEventoDescription = findViewById(R.id.txtEventoDescription);
        imgUnEvento = findViewById(R.id.imgEvento);
        btnInscribir = findViewById(R.id.btnIncripción);

        txtUnEventoTitle.setText(UnEvento.getName());
        txtUnEventoDescription.setText(UnEvento.getDescription());

        String Ruta = "https://videoface-8f1a4.firebaseio.com/Eventos/" + Number;

        RootRef = new Firebase(Ruta);

        btnInscribir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase invitado = RootRef.child("Invitados" + userId);


                invitado.child("correo").setValue(userEmail);
                invitado.child("asistencia").setValue("no");

                Toast.makeText(EventoActivity.this, "Se acaba de registra al evento: " + UnEvento.getName(),Toast.LENGTH_LONG).show();

                //Toast.makeText(EventoActivity.this, Number,Toast.LENGTH_LONG).show();
            }
        });
    }
}
