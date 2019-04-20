package com.example.fede.animalarium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ContactosActivity extends AppCompatActivity {


    static SplashScreenActivity splashScreenActivity;
    static Activity context;
    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    ProgressDialog progressDialog;

    //
    EditText buscador;
    ArrayList<String> mascotas = new ArrayList<>();
    private static ContactosAdapter adaptador;
    private static ListView listado;
    // private Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Date fecha = new Date();
    static String fechaS = "";
    static String viene = "contactos";
    private static Uri uri;
    private int cantidadTotalContactos;
    private static Contacto contacto;
    static ArrayList<Contacto> contactos = new ArrayList<>();
    private ComunicadorContacto comunicadorContacto = new ComunicadorContacto();
    private int n = 1;
    private static List<File> localFiles = new ArrayList<>();
    DocSnippets docSnippets;
    static DocumentSnapshot contactoDS;
    private int m = -1;
    private static ListIterator<DocumentSnapshot> contactosLI;
    //FECHA
    DateFormat dfFecha = new SimpleDateFormat("dd-MM-yyyy");
    static ArrayList<String> fotos = new ArrayList<>();
    static ArrayList<Uri> uris = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        context = this;
        docSnippets = new DocSnippets(db, this);
        progressDialog = docSnippets.progressDialog;

        try {
            viene = getIntent().getExtras().getString("VIENE");
            //FECHA
            fechaS = getIntent().getExtras().getString("FECHA");
            Log.e("fechaS_ca", fechaS);
            fecha = dfFecha.parse(fechaS);
        } catch (NullPointerException e) {
        } catch (NumberFormatException e1) {
            Log.e("contactosActiviry", "No recogimos fechaS");
        } catch (ParseException e2) {
            Log.e("parseExceptionContactosActivity", e2.getMessage());
        }

        buscador = (EditText) findViewById(R.id.buscador);
        listado = (ListView) findViewById(R.id.contactos);
        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {

                contacto = contactos.get(position);

                ComunicadorContacto.setObjeto(contacto);

                Intent intent = null;
                if (!viene.equalsIgnoreCase("")) {
                    switch (viene) {
                        case "main_activity":
                            intent = new Intent(context, FormularioActivity.class);
                            break;
                        case "contactos":
                            intent = new Intent(context, FormularioActivity.class);
                            intent.putExtra("FECHA", fechaS);
                            break;
                        case "peluquerias":
                            intent = new Intent(context, FormularioCitaActivity.class);
                            intent.putExtra("FECHA", fechaS);
                            break;
                        case "hotel_activity":
                            intent = new Intent(context, FormularioHotelActivity.class);
                            intent.putExtra("FECHA", fechaS);
                            break;
                    }

                    intent.putExtra("VIENE", viene);

                    startActivity(intent);

                }

            }
        });

        //Autentificamos usuarios para firebase
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        iniciamosAdaptador();


    }

    public static void setContactos(List<DocumentSnapshot> documents, Activity activity, String viene, ProgressDialog progressDialog) {
        context = activity;
        setViene(viene);
        contactos.clear();
        contactosLI = documents.listIterator();
        contactoDS = contactosLI.next();
        cargarFoto(progressDialog);
    }

    public void buscar(View view) {

        progressDialog.dismiss();
        contactos.clear();
        String mascota = buscador.getText().toString();
        if (!mascota.equalsIgnoreCase("")) {
            //mascota = String.valueOf(mascota.charAt(0));
            docSnippets.getContactosParaNombre(mascota);
           // progressDialog.show();

        } else {
            docSnippets.getContactos();
          //  progressDialog.show();
        }
    }

    private void recuperarFirebase() {

        contactos.clear();
        docSnippets.getContactos();
        progressDialog.show();
    }

    public static Uri cargarFoto(final ProgressDialog progressDialog) {
        uri = null;
        try {
            final String foto = contactoDS.getString("foto");
            StorageReference storageRef = storage.getReferenceFromUrl("gs://animalarium-android-6eb93.appspot.com/external/images/media").child(foto);
            if (!fotos.contains(foto)) {
                fotos.add(foto);
                //  Log.e("foto",document.getString("foto"));
                final File localFile = File.createTempFile("images", "jpg");
                localFiles.add(localFile);
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        uri = getImageUri(bitmap);
                        uris.add(uri);
                        bindeaYAñadeContacto(uri,progressDialog);

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("FAllo_cargarFoto", exception.getMessage());

                    }
                });
            } else {
                uri = uris.get(uris.size() - 1);
                bindeaYAñadeContacto(uri,progressDialog);
            }
        } catch (IOException e) {
            Log.e("FAllo", "IOException");
        }

        return uri;

    }

  /*public Uri cargarFoto() {
        uri = null;
        try {

            //m++;
            final String foto = contactoDS.getString("foto");
            StorageReference storageRef = storage.getReferenceFromUrl("gs://animalarium-android-6eb93.appspot.com/external/images/media").child(foto);
            fotos.add(foto);
            //  Log.e("foto",document.getString("foto"));
            final File localFile = File.createTempFile("images", "jpg");
            localFiles.add(localFile);
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    uri = getImageUri(bitmap);
                    bindeaYAñadeContacto(uri);

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("FAllo_cargarFoto", exception.getMessage());

                }
            });
        } catch (IOException e) {
            Log.e("FAllo", "IOException");
        }

        return uri;

    }
*/

    private static Uri getImageUri(Bitmap bitmap) {

        if (splashScreenActivity != null) context = splashScreenActivity;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
            uri = Uri.parse(path);
            return uri;
        }
        return null;
    }


    public static void bindeaYAñadeContacto(Uri uri,ProgressDialog progressDialog) {

        Contacto con = new Contacto(
                contactoDS.getId(),
                uri,
                contactoDS.getString("mascota"),
                contactoDS.getString("raza"),
                contactoDS.getString("tamaño"),
                contactoDS.getString("telefono1"),
                contactoDS.getString("telefono2"),
                contactoDS.getString("propietario"));

        contactos.add(con);
        //Continuamos bucle paar siguienter contacto
        try {
            contactoDS = contactosLI.next();
            cargarFoto(progressDialog);
        } catch (NoSuchElementException e) {
            ComunicadorContacto.setObjects(contactos);
            progressDialog.dismiss();
            switch (viene){
                case "splash_screen":
                    context.startActivity(new Intent().setClass(context, MainActivity.class));

                    break;
                default:
                    iniciamosAdaptador();
                    progressDialog.dismiss();
                    break;
            }


        }
    }


    //LISTADO
    private static void iniciamosAdaptador() {
        // Inicializamos el adapter
        adaptador = new ContactosAdapter(context, ComunicadorContacto.getObjects());

        listado.setAdapter(adaptador);



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //iniciamosAdaptador();
                }
                break;
        }
    }

    public void añadirContacto(View view) {
        comunicadorContacto.setObjeto(null);
        Intent formulario = new Intent(getApplicationContext(), FormularioActivity.class);
        startActivity(formulario);
    }





    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("ContactosActivity", "signInAnonymously:FAILURE", exception);
                    }
                });
    }


    @Override
    public void onBackPressed() {

        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures");
        borraRecursivamente(dir);


        Intent menu = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(menu);

    }

    void borraRecursivamente(File archivoODirectorio) {
        if (archivoODirectorio.isDirectory()) {
            Log.e("diree", archivoODirectorio.getPath() + " Es directorio");
            for (File hijos : archivoODirectorio.listFiles()) {
                Log.e("hola", "hijos");
                Log.e("existe?", String.valueOf(hijos.delete()));
            }
            // borraRecursivamente(hijos);
        } else {
            Log.e("diree", archivoODirectorio.getPath() + " NO Es directorio, es foto");
            Log.e("borrado?", String.valueOf(archivoODirectorio.delete()));
        }

    }

    public static String getViene() {
        return viene;
    }

    public static void setViene(String viene) {
        ContactosActivity.viene = viene;
    }
}
