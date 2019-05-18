package com.example.fede.animalarium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class PropietariosActivity extends AppCompatActivity {

    static SplashScreenActivity splashScreenActivity;
    static Activity context;
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



    }

    public void añadirPropietario(View view) {
    }

    public void buscar(View view) {
    }
}
