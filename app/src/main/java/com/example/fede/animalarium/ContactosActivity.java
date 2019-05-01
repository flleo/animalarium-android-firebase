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
    private static String foto;
    private static int i;
    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    static ProgressDialog progressDialog;

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
    static List<String> fotos = new ArrayList<String>();
    static List<Uri> uris = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        context = this;
        docSnippets = new DocSnippets(db, this);
        progressDialog = docSnippets.progressDialog;

        viene = getIntent().getExtras().getString("VIENE");

        switch (viene) {
            case "formulario_activity":
                break;
            case "peluquerias_activity":

                fechaS = getIntent().getExtras().getString("FECHA");
                Log.e("fechaS_ca", fechaS);
                try {
                    fecha = dfFecha.parse(fechaS);
                } catch (ParseException e) {
                    Log.e("Error al parsear la fecha", e.getMessage());
                }
                break;

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
                            viene = "contactos_activity";
                            break;

                        case "formulario_activity":
                            intent = new Intent(context, FormularioActivity.class);
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
        try {
            if (user != null) {   }
        }
        catch (NullPointerException e) {
            signInAnonymously();
        }
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        //

        iniciamosAdaptador();


    }

    //LISTADO
    private static void iniciamosAdaptador() {
        // Inicializamos el adapter
        adaptador = new ContactosAdapter(context, ComunicadorContacto.getObjects());

        listado.setAdapter(adaptador);


    }

    public static void setContactos(List<DocumentSnapshot> documents, Activity activity, String viene, ProgressDialog progressDialog) {
        context = activity;
        setViene(viene);
        setProgressDialog(progressDialog);
        contactos.clear();
        contactosLI = documents.listIterator();
        contactoDS = contactosLI.next();
        cargarFoto();


    }


    public static void cargarFoto() {

        uri = null;
        foto = contactoDS.getString("foto");

        for (i = 0; i < fotos.size(); i++)
            if (fotos.get(i).equalsIgnoreCase(foto)) {
                uri = uris.get(i);
                bindeaYAñadeContacto(uri);
            }
        if (i == fotos.size()) if (uri == null) grabaFotoMobil();

    }

    private static void grabaFotoMobil() {

        try {
            final File localFile = File.createTempFile("images", "jpg");
            localFiles.add(localFile);
            StorageReference storageRef = storage.getReferenceFromUrl("gs://animalarium-android-6eb93.appspot.com/external/images/media").child(foto);
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    uri = getImageUri(bitmap);
                    uris.add(uri);
                    fotos.add(foto);
                    bindeaYAñadeContacto(uri);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("FAllo_cargarFoto", exception.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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


    public static void bindeaYAñadeContacto(Uri uri) {

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
            cargarFoto();
        } catch (NoSuchElementException e) {
            ComunicadorContacto.setObjects(contactos);
            ComunicadorContacto.setUris(uris);
            progressDialog.dismiss();
            switch (viene) {
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
        formulario.putExtra("VIENE", "contactos_activity_añadirContacto");
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

        Intent menu = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(menu);

    }


    public static void setProgressDialog(ProgressDialog progressDialog) {
        ContactosActivity.progressDialog = progressDialog;
    }

    public static void setViene(String viene) {
        ContactosActivity.viene = viene;
    }


}
