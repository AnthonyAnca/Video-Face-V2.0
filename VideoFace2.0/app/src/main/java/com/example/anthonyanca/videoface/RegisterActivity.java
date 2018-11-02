package com.example.anthonyanca.videoface;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.FaceDetector;
import android.media.Image;
import android.net.Uri;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.felipecsl.gifimageview.library.GifImageView;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    String acountName;
    String acountEmail;
    String acountId;

    private  final int VIDEO_REQUEST_CODE = 100;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    public static final String UserName = "email@gmail.com";
    public static final String UserPassword = "password";

    VideoView ressultVideo;

    private FirebaseAuth firebaseAuth;

    Uri videoUri;
    Uri []imgUri;

    private boolean rostro;
    private boolean video;
    private boolean videoExito;

    private StorageReference storageReference;

    File file_video;
    ImageView img1;

    Image []images;

    //private FFmpeg ffmpeg;

    List<Bitmap> ListBitmaps;


    MediaMetadataRetriever mediaMetadataRetriever = null;

    GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ressultVideo = (VideoView)findViewById(R.id.video_view);

        gifImageView = (GifImageView)findViewById(R.id.gifImageView);
        rostro=false;
        video = false;
        videoExito = false;

        storageReference = FirebaseStorage.getInstance().getReference();

        //img1 = (ImageView) findViewById(R.id.img1);

        mediaMetadataRetriever = new MediaMetadataRetriever();

        firebaseAuth = FirebaseAuth.getInstance();

        images = new Image[10];

        imgUri = new Uri[10];

        ListBitmaps = new ArrayList<Bitmap>();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        new RetrieveByteArray().execute("https://thumbs.gfycat.com/VapidDefenselessCero-size_restricted.gif");
        gifImageView.startAnimation();


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

    }
    class RetrieveByteArray extends AsyncTask<String,Void,byte[]>
    {

        @Override
        protected byte[] doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode()== 200)
                {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[10240];
                    while((nRead = in.read(data,0,data.length))!= -1)
                    {
                        buffer.write(data,0,nRead);
                    }
                    buffer.flush();
                    return buffer.toByteArray();
                }
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            gifImageView.setBytes(bytes);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){

            GoogleSignInAccount account = result.getSignInAccount();

            acountName = account.getDisplayName();
            acountEmail = account.getEmail();
            acountId = account.getId();

        } else {
            goLogInScreen();
        }
    }
    private void goLogInScreen() {
        Intent intent = new Intent(this,LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /*public void captureVideo(View view) {
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File videoface = getFilepath();
        videoUri = Uri.fromFile(videoface);

        video = true;

        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(camera_intent,VIDEO_REQUEST_CODE);
    }*/

    /*public File getFilepath()
    {
        File folder = new File("sdcard/videoFace_app");
        if(!folder.exists())
        {
            folder.mkdir();
        }

        File video_file = new File(folder,"rostro.mp4");

        return video_file;
    }*/

    private void uploadFile(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registrando usuario...");
        progressDialog.show();

        if(videoUri !=null) {


                StorageReference riversRef = storageReference.child(acountEmail+"/"+"rostro.mp4");

            riversRef.putFile(videoUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                rostro=true;
                                progressDialog.dismiss();
                                videoExito= true;

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        /*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage((int) progress + "%...");
                            }
                        })*/
                ;


        } else {

            Toast.makeText(getApplicationContext(), "Intentelo de nuevo", Toast.LENGTH_SHORT).show();

        }

        //Toast.makeText(getApplicationContext(), "Registro completado", Toast.LENGTH_SHORT).show();
        firebaseAuth.createUserWithEmailAndPassword(acountEmail, acountId)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Bienvenido " + acountEmail, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplication(),MainActivity.class);
                            startActivity(intent);
                        } else {
                            //if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            //} else {
                            Toast.makeText(RegisterActivity.this, "No se pudo registar el usuario", Toast.LENGTH_LONG).show();
                            //}
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public void Grabar(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            //File sdcard = Environment.getExternalStorageDirectory();
            //file_video = new File(sdcard,"video.mp4");
            //takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,file_video);
            //takeVideoIntent.setType("video/mp4");
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            //captureVideo(view);
            //initFrame();
            //startActivityForResult(takeVideoIntent,0);

        }

    }

    //private void takeFrame() {

        //String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        //long frametime = 200000;

        //int countFace =0;

        //for (int i =0; i<10 ; i++)
        //{
            //Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(frametime, MediaMetadataRetriever.OPTION_CLOSEST);

            //img1.setImageBitmap(bmFrame);

            /*
            Bitmap bmFrame = ListBitmaps.get(i);


            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmFrame.compress(Bitmap.CompressFormat.JPEG,100, bytes);

            Bitmap tempBitmap = Bitmap.createBitmap(bmFrame.getWidth(),bmFrame.getHeight(), Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(bmFrame,0,0,null);

            com.google.android.gms.vision.face.FaceDetector faceDetector = new com.google.android.gms.vision.face.FaceDetector.Builder(getApplicationContext())
                    .setTrackingEnabled(false)
                    .setLandmarkType(com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS)
                    .setMode(com.google.android.gms.vision.face.FaceDetector.FAST_MODE)
                    .build();

            if(!faceDetector.isOperational())
            {
                Toast.makeText(RegisterActivity.this,"No se pudo isntalar el Detector de Rostro en su dispositivo",Toast.LENGTH_SHORT).show();
            }

            Frame frame = new Frame.Builder().setBitmap(bmFrame).build();
            SparseArray<Face> sparseArray = faceDetector.detect(frame);

            if(sparseArray.size()>0)
            {
                countFace++;
            }
            */


            //String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bmFrame, "imagen",null);


            //grantUriPermission(path,imgUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //grantUriPermission(path,imgUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //grantUriPermission(path,imgUri,Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            //grantUriPermission(path,imgUri,Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            //imgUri[i]=Uri.parse(path);


            //FileOutputStream stream = null;

            //file = new FileOutputStream(Environment.getExternalStorageDirectory().toString());
            //boolean compresee = bmFrame.compress(Bitmap.CompressFormat.JPEG,100, images[i] );

            //frametime += 150000;

            //Log.d("CREATION",Long.toString(frametime));
            //Log.d("CREATION",imgUri[i].toString());

            //Toast.makeText(getApplicationContext(),imgUri[i].toString()+ "///" + frametime,Toast.LENGTH_LONG).show();

        //}
        /*if(countFace>=2)
        {
            rostro = true;
        }
        else{
            Toast.makeText(RegisterActivity.this, "No se detectó rostro en el video, por favor intente grabando el video otra vez",Toast.LENGTH_LONG).show();
        }*/

    //}

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

       /* if(requestCode==VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Video subido con éxito", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Fallo al subir el video", Toast.LENGTH_LONG).show();
            }
        }*/

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = data.getData();

            video = true;


            //ressultVideo.setVideoURI(videoUri);
            //ressultVideo.setVisibility(View.VISIBLE);

            //mediaMetadataRetriever.setDataSource(getApplicationContext(),data.getData());

            //ListBitmaps = mediaMetadataRetriever.getFramesAtIndex(0,10);
            //ressultVideo.start();

            //data.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //data.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //data.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //MediaMetadataRetriever tRetriever = new MediaMetadataRetriever();

            //try{
                //tRetriever.setDataSource(getBaseContext(),videoUri);

                //mediaMetadataRetriever.setDataSource(this,videoUri);

                //mediaMetadataRetriever = tRetriever;

                //String DURATION = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            //}catch (RuntimeException e){
             //   e.printStackTrace();
            //    Toast.makeText(RegisterActivity.this,"error",Toast.LENGTH_SHORT).show();
            //}

        }

    }

    public void ok(View view) {
        if(video)
        {
            //takeFrame();
            //if (rostro){
            uploadFile();
            //}
        }
        else{
            Toast.makeText(RegisterActivity.this, "Ha ocurrido un problema con el video, por favor pulse Grabar Video nuevamente", Toast.LENGTH_LONG).show();
        }

        //else{
       //     Toast.makeText(getApplicationContext(),"Error al subir el video",Toast.LENGTH_SHORT).show();
        //}
    }

    private void goMainScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*private  void initFrame(){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(file_video.getAbsolutePath());

            int[] ids_of_images = new int[]{R.id.img1,R.id.img2,R.id.img3,R.id.img4,R.id.img5};
            int looper =100000;

            //img.setImageBitmap(retriever.getFrameAtTime(10000,MediaMetadataRetriever.OPTION_CLOSEST));

            for(int i=0 ;i <5; i++)
            {
                ImageView imageView = (ImageView)findViewById(ids_of_images[i]);

                imageView.setImageBitmap(retriever.getFrameAtTime(looper,MediaMetadataRetriever.OPTION_CLOSEST));

                looper +=100000;
            }

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
    }*/
}
