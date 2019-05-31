package com.example.fede.animalarium;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
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
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PeluqueriasContactoActivity extends AppCompatActivity {


    private ArrayList<CitaPeluqueria> citas = new ArrayList<>();
    private MisCitasAdapter adaptador;
    private ListView listado;
    SimpleDateFormat sdf;
    String _id;
    String fecha, viene;
    Contacto contacto;


    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets = new DocSnippets(db, this);
    private ArrayList<String> fotos = new ArrayList<>();
    ProgressDialog progressDialog;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peluquerias_contacto);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("...recuperando sus citas...");

        contacto = (Contacto) ComunicadorContacto.getContacto();

        try {
            viene = getIntent().getExtras().getString("VIENE");
            switch (viene) {
                case "formulario_activity":
                    recuperarFirebase();
                    break;
                case "formulario_cita_activity":
                    citas = ComunicadorCita.getSusCitas();
                    inicimosAdaptador();
                    break;
                case "contactos_activity":
                    recuperarFirebase();
                    break;


            }
        } catch (IllegalArgumentException e) {
            Log.e("viene_peluquerias_contacto_activity", e.getMessage());
        }

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


    }

    private void recuperarFirebase() {

        docSnippets.getPeluqueriasPorContacto();
        progressDialog.show();
    }


    public void bindeaYAñadeCita(DocumentSnapshot doc) {
        System.out.println(contacto.get_id() + "*****************************");
        Log.e("idcontacto", contacto.get_id());

        CitaPeluqueria con = new CitaPeluqueria(
                doc.getId(),
                contacto.get_id(),
                doc.getDate("fecha"),
                doc.getString("trabajo"),
                doc.getDouble("tarifa")
        );
        citas.add(con);

        inicimosAdaptador();

    }


    public void inicimosAdaptador() {
        adaptador = new MisCitasAdapter(this, citas);
        listado = (ListView) findViewById(R.id.peluquerias_contacto);
        listado.setAdapter(adaptador);
        progressDialog.dismiss();
        ComunicadorCita.setSusCitas(citas);
        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {
                ComunicadorCita.setObjeto(citas.get(position));
                Intent i = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                i.putExtra("VIENE", "peluquerias_contacto_activity");
                startActivity(i);
            }
        });

    }


    public void añadirCita(View view) {
        Intent formulario;
        switch (viene) {
            case "peluquerias_activity":
                formulario = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                formulario.putExtra("FECHA", fecha);
                break;
            case "formulario_cita_activity":
                formulario = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                break;
            default:
                formulario = new Intent(getApplicationContext(), PeluqueriasActivity.class);
                break;
        }

        formulario.putExtra("VIENE", "peluquerias_contacto_activity");
        startActivity(formulario);
    }

    private void intent() {
        Intent intent = null;
        switch (viene) {
            case "formulario_cita_activity":
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case "peluquerias_activity":
                intent = new Intent(getApplicationContext(), PeluqueriasActivity.class);
                ComunicadorContacto.setContacto(null);
                break;
            case "contactos_activity":
                intent = new Intent(getApplicationContext(), ContactosActivity.class);
                break;
            default:
                intent = new Intent(getApplicationContext(), ContactosActivity.class);
                break;
        }
        intent.putExtra("VIENE", "peluquerias_contacto_activity");
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

    @Override
    public void onBackPressed() {

        intent();

    }


}
