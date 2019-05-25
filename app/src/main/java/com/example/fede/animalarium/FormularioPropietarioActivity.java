package com.example.fede.animalarium;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FormularioPropietarioActivity extends AppCompatActivity {
    //Firesbase
    private static final String TAG = "FormularioActivity";
    private static final String KEY_FOTO = "foto";
    private static final String KEY_MASCOTA = "mascota";
    private static final String KEY_RAZA = "raza";
    private static final String KEY_TAMAÑO = "tamaño";
    private static final String KEY_TELEFONO1 = "telefono1";
    private static final String KEY_TELEFONO2 = "telefono2";
    private static final String KEY_PROPIETARIO = "propietario";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference contactosRef = db.collection("contactos").document();
    DocumentReference citasRef = db.collection("citas").document();
    DocumentReference reservasRef = db.collection("hoteles").document();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;

    //

    ImageButton imageButton ;
    Button mascotas,phone1,phone2;
    EditText nombre,telefono1,telefono2,email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_propietario);

        imageButton = findViewById(R.id.formulario_propietario_imageButtonFormulario);
        mascotas = findViewById(R.id.formulario_propietario_susMascotas_button);
        phone1 = findViewById(R.id.formulario_propietario_phone1);
        phone2 = findViewById(R.id.formulario_propietario_phone2);
        nombre = findViewById(R.id.formulario_propietario_nombre);
        telefono1 = findViewById(R.id.formulario_propietario_telefono1);
        telefono2 = findViewById(R.id.formulario_propietario_telefono2);
        email = findViewById(R.id.formulario_propietario_email);



    }

    public void susMascotas(View view) {

    }

    public void llamada1(View view) {
    }

    public void llamada2(View view) {
    }

    public void añadir(View view) {
    }

    public void actualizar(View view) {
    }

    public void eliminarContacto(View view) {
    }

    public void seleccionaImagen(View view) {
    }
}
