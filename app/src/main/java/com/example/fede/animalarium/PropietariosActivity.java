package com.example.fede.animalarium;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class PropietariosActivity extends AppCompatActivity {



    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    static ProgressDialog progressDialog;

    //
    EditText buscador;
    private static PropietariosAdapter propietariosAdapter;
    private static ContactosAdapter adaptador;
    private static ListView listado;
    // private Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Date fecha = new Date();
    static String fechaS = "";
    static String viene ="";
    private static Uri uri;
    private static Propietario propietario;
    static ArrayList<Propietario> propietarios = new ArrayList<>();
    private ComunicadorPropietario comunicadorPropietario = new ComunicadorPropietario();
    private int n = 1;
    private static List<File> localFiles = new ArrayList<>();
    DocSnippets docSnippets;
    static DocumentSnapshot propietarioDS;
    private int m = -1;
    static Context context;
    private static String foto;
    private static int i;
    private static ListIterator<DocumentSnapshot> propietariosLI;
    private static SplashScreenActivity splashScreenActivity;
    //FECHA
    DateFormat dfFecha = new SimpleDateFormat("dd-MM-yyyy");
    static ArrayList<String> fotos = new ArrayList<String>();
    static ArrayList<Uri> uris = new ArrayList<Uri>();
    private static PropietariosActivity application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propietarios);

        context = this;
        application = this;
        docSnippets = new DocSnippets(db, this);
        progressDialog = docSnippets.progressDialog;

        viene = getIntent().getExtras().getString("VIENE");


        buscador = (EditText) findViewById(R.id.buscador_propietarios);
        listado = (ListView) findViewById(R.id.listado_propietarios);

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {

                Intent intent = null;

                switch (viene) {

                    case "main_activity":
                        intent = new Intent(getApplicationContext(), FormularioPropietarioActivity.class);
                        break;

                }
                intent.putExtra("VIENE", "propietarios_activity");
                startActivity(intent);
            }
        });

        //Autentificamos usuarios para firebase
        FirebaseUser user = mAuth.getCurrentUser();
        try {
            if (user != null) {
            }
        } catch (NullPointerException e) {
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
        propietariosAdapter = new PropietariosAdapter(context, ComunicadorPropietario.getPropietarios());

        listado.setAdapter(adaptador);


    }

    public void buscarPropietarios(View view) {
        docSnippets.getPropietarios();
    }

    public void añadirPropietario(View view) {
        Intent intent = new Intent(this,FormularioPropietarioActivity.class);
        intent.putExtra("VIENE","propietarios_activity");
        startActivity(intent);
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

    public static void setPropietarios(List<DocumentSnapshot> documents, SplashScreenActivity splashScreenActivity, String splash_screen, ProgressDialog progressDialog) {
        context = splashScreenActivity;
        setViene(splash_screen);
        setProgressDialog(progressDialog);
        propietarios.clear();
        propietariosLI = documents.listIterator();
        if (propietariosLI.hasNext()) {
            propietarioDS = propietariosLI.next();
            cargarFoto();
        }


    }

    public static void cargarFoto() {

        uri = null;
        foto = propietarioDS.getString("foto");

        for (i = 0; i < fotos.size(); i++)
            if (fotos.get(i).equalsIgnoreCase(foto)) {
                uri = uris.get(i);
                bindeaYAñadePropietario(uri);
            }
        if (i == fotos.size()) if (uri == null) grabaFotoMobil();

    }

    //RECUPERAMOS FOTO DESDE FIREBASE
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
                    bindeaYAñadePropietario(uri);
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

    public static void bindeaYAñadePropietario(Uri uri) {

        propietarios.add(bindeaPropietarioDS());



        //Continuamos bucle paar siguienter contacto
        try {
            propietarioDS = propietariosLI.next();
            cargarFoto();
        } catch (NoSuchElementException e) {
            ComunicadorPropietario.setPropietarios(propietarios);
            ComunicadorPropietario.setUris(uris);
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


    private static Propietario bindeaPropietarioDS() {
        Propietario pro = new Propietario(
                propietarioDS.getId(),
                uri,
                propietarioDS.getString("propietario"),
                propietarioDS.getString("telefono1"),
                propietarioDS.getString("telefono2"),
                propietarioDS.getString("email")
        );

        return propietario;
    }


    private static Uri getImageUri(Bitmap bitmap) {

        if (splashScreenActivity != null) context = splashScreenActivity;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(application, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
            uri = Uri.parse(path);
            return uri;
        }
        return null;
    }
    private static void setProgressDialog(ProgressDialog progressDialog) {
        progressDialog = progressDialog;
    }

    private static void setViene(String viene) {
        PropietariosActivity.viene = viene;
    }

    @Override
    public void onBackPressed() {

        Intent menu = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(menu);

    }


}
