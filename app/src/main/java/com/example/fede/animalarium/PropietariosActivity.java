package com.example.fede.animalarium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class PropietariosActivity extends AppCompatActivity {

    static SplashScreenActivity splashScreenActivity;
    static Context context;
    private static String foto;
    private static int i;
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
    static String viene = "";
    private static Uri uri;
    private static Contacto contacto;
    private static Propietario propietario;
    static ArrayList<Contacto> contactos = new ArrayList<>();
    static ArrayList<Propietario> propietarios = new ArrayList<>();
    private ComunicadorContacto comunicadorContacto = new ComunicadorContacto();
    private ComunicadorPropietario comunicadorPropietario = new ComunicadorPropietario();
    private int n = 1;
    private static List<File> localFiles = new ArrayList<>();
    DocSnippets docSnippets;
    static DocumentSnapshot contactoDS;
    private int m = -1;
    private static ListIterator<DocumentSnapshot> contactosLI;
    //FECHA
    DateFormat dfFecha = new SimpleDateFormat("dd-MM-yyyy");
    static ArrayList<String> fotos = new ArrayList<String>();
    static ArrayList<Uri> uris = new ArrayList<Uri>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propietarios);

        context = this;
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
                        intent = new Intent(context, FormularioPropietarioActivity.class);
                        break;

                }
                intent.putExtra("VIENE", "contactos_activity");
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
        adaptador = new ContactosAdapter(context, ComunicadorContacto.getContactos());

        listado.setAdapter(adaptador);


    }

    public void buscarPropietarios(View view) {
        docSnippets.getPropietarios();
    }

    public void añadirPropietario(View view) {
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


}
