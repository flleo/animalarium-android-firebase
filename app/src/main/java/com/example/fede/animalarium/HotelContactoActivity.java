package com.example.fede.animalarium;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.List;

public class HotelContactoActivity extends AppCompatActivity {

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

    Contacto contacto;
    String viene;
    private List<ReservaHotel> reservas;
    private MisReservasAdapter adaptador;
    private ListView listado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_contacto);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("...recuperando sus citas...");

        contacto = (Contacto) ComunicadorContacto.getContacto();


            viene = getIntent().getExtras().getString("VIENE");
            switch (viene) {
                case "formulario_activity":
                    recuperarFirebase();
                    break;
                case "formulario_hotel_activity":
                    reservas = ComunicadorReserva.getReservas();
                    inicimosAdaptador();
                    break;

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


    public void añadir_hotel(View view) {
        Intent formulario = new Intent(getApplicationContext(), FormularioHotelActivity.class);
        startActivity(formulario);
    }

    private void recuperarFirebase() {

        docSnippets.getReservasPorContacto();
        progressDialog.show();
    }

    public void bindeaYAñadeReserva(DocumentSnapshot doc) {
        System.out.println(contacto.get_id() + "*****************************");
        Log.e("idcontacto", contacto.get_id());

        ReservaHotel con = new ReservaHotel(
                doc.getId(),
                contacto.get_id(),
                doc.getDate("fechaInicio"),
                doc.getDate("fechaFin"),
                doc.getDouble("precio"),
                doc.getDouble("noches"),
                doc.getDouble("coste"),
                doc.getBoolean("pagado")
        );
        reservas.add(con);

        inicimosAdaptador();

    }


    public void inicimosAdaptador() {
        adaptador = new MisReservasAdapter(this, reservas);
        listado = (ListView) findViewById(R.id.reservas_contacto);
        listado.setAdapter(adaptador);
        progressDialog.dismiss();
        ComunicadorReserva.setReservas(reservas);
        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {
                ComunicadorReserva.setReserva(reservas.get(position));
                Intent i = new Intent(getApplicationContext(), FormularioHotelActivity.class);
                i.putExtra("VIENE", "hotel_contacto_activity");

                startActivity(i);
            }
        });

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

        ComunicadorReserva.setReserva(null);

        Intent menu = new Intent(getApplicationContext(), FormularioActivity.class);
        menu.putExtra("VIENE","hotel_contacto_activity");
        startActivity(menu);

    }


}
