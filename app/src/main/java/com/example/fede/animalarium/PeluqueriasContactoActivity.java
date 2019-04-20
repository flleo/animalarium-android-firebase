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
import android.widget.Toast;

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

public class PeluqueriasContactoActivity extends AppCompatActivity {


    CalendarView calendarView;
    private ArrayList<CitaPeluqueria> citas  = new ArrayList<>();
    private MisCitasAdapter adaptador;
    private ListView listado;
    private Context context;
    SimpleDateFormat sdf;
    String _id;
    String fecha,viene;
    Contacto contacto;


    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef ;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets = new DocSnippets(db, this);
    private ArrayList<String> fotos = new ArrayList<>();
    ProgressDialog progressDialog;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peluquerias_contacto);

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("...recuperando sus citas...");

        contacto = (Contacto) ComunicadorContacto.getObjeto();

        try{
            viene = getIntent().getExtras().getString("VIENE");
            switch (viene){
                case "contactos": break;
                case "peluquerias": fecha = getIntent().getExtras().getString("FECHA");break;
            }
        } catch (IllegalArgumentException e){
            Log.e("viene_peluquerias_contacto_activity",e.getMessage());
        }

        if(!contacto.get_id().equalsIgnoreCase("")){
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

            recuperarFirebase();

        } else {
            Toast toast1 =  Toast.makeText(getApplicationContext(),
                            "Debes crear primero, el contacto", Toast.LENGTH_SHORT);
            toast1.show();
        }


    }

    private void recuperarFirebase() {

        docSnippets.getPeluqueriasPorContacto();
        progressDialog.show();
    }


    public void bindeaYAñadeCita(DocumentSnapshot doc) {
        System.out.println(contacto.get_id()+"*****************************");
        Log.e("idcontacto",contacto.get_id());

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



    public void inicimosAdaptador(){
        adaptador = new MisCitasAdapter(this,citas);
        listado = (ListView) findViewById(R.id.peluquerias_contacto);
        listado.setAdapter(adaptador);
        progressDialog.dismiss();

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {
                ComunicadorCita.setObjeto(citas.get(position));
                Intent i = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                i.putExtra("VIENE","peluqueriasContacto");

                startActivity(i);
            }
        });

    }



    public void añadirCita(View view) {
        Intent formulario;
        if(viene.equalsIgnoreCase("peluquerias")){
            formulario = new Intent(getApplicationContext(),FormularioCitaActivity.class);
            formulario.putExtra("FECHA",fecha);
        } else {
            // ComunicadorContacto.setObjeto(contacto);
            formulario = new Intent(getApplicationContext(), PeluqueriasActivity.class);
        }
        startActivity(formulario);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
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



}
