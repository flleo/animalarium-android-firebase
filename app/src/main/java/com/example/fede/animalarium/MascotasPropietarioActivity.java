package com.example.fede.animalarium;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MascotasPropietarioActivity extends AppCompatActivity {

    private ArrayList<Mascota> mascotas = new ArrayList<>();
    private MisMascotasAdapter adaptador;
    private ListView listado;
    SimpleDateFormat sdf;
    String _id;
    String fecha, viene;
    Propietario propietario;


    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets = new DocSnippets(db, this);
    private ArrayList<String> fotos = new ArrayList<>();
    ArrayList<Uri> uris = new ArrayList<>();
    ProgressDialog progressDialog;
    private String fotoS="";
    private Uri uri;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascotas_propietario);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("...recuperando sus citas...");

        propietario = ComunicadorPropietario.getPropietario();
        fotos = ComunicadorMascota.getFotos();
        uris = ComunicadorMascota.getUris();

        try {
            viene = getIntent().getExtras().getString("VIENE");
            switch (viene) {
                case "formulario_propietario_activity":
                    recuperarFirebase();
                    break;
                case "formulario_cita_activity":
//                    citas = ComunicadorCita.getSusCitas();
//                    inicimosAdaptador();
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
      //      signInAnonymously();
        }
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);



    }


    public void añadirMascota(View view) {

        Intent intent = new Intent(this,FormularioActivity .class);
        intent.putExtra("VIENE","mascotas_propietario_activity_añadir" );
        startActivity(intent);
    }

    private void recuperarFirebase() {

        docSnippets.getMascotasPorPropietario(propietario);
        progressDialog.show();
    }

    public void bindeaYAñadeMascota(DocumentSnapshot document) {

       /* fotoS = document.getString("foto");

        Mascota mascota = new Mascota(
          document.getId(),
          document.getDocumentReference("idPropietario"),
          bindeaFotoS(),
          document.getString("")
        );

         con.set_id(document.getId());
         con.set_idPropietario(document.getDocumentReference("idPropietario"));
         con.setFoto(document.getString("foto"));
         con.se
                document.getString("foto"),
                document.getString("nombre"),
                document.get
        );


        CitaPeluqueria con = new CitaPeluqueria(
                doc.getId(),
                contacto.get_id(),
                doc.getDate("fecha"),
                doc.getString("trabajo"),
                doc.getDouble("tarifa")
        );
        citas.add(con);

        inicimosAdaptador();*/
    }

    public void inicimosAdaptador() {
        adaptador = new MisMascotasAdapter(this, mascotas);
        listado = (ListView) findViewById(R.id.activity_mascotas_propietario_listado);
        listado.setAdapter(adaptador);
        progressDialog.dismiss();
        ComunicadorPropietario.setMascotas(mascotas);
        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {
                ComunicadorMascota.setMascota(mascotas.get(position));
                Intent i = new Intent(getApplicationContext(), FormularioActivity.class);
                i.putExtra("VIENE", "peluquerias_contacto_activity");
                startActivity(i);
            }
        });

    }

    public Uri bindeaFotoS() {

        uri = null;
        int i;
        for (i = 0; i < fotos.size(); i++)
            if (fotos.get(i).equalsIgnoreCase(fotoS)) {
                uri = uris.get(i);

            }
        return uri;

    }


}
