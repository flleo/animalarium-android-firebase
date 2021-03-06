package com.example.fede.animalarium;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class HotelActivity extends AppCompatActivity {

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocSnippets docSnippets = new DocSnippets(db, this);
    ProgressDialog progressDialog;
    //

    CalendarView calendarView;
    public ArrayList<ReservaHotel> reservas = new ArrayList<>();
    public ArrayList<Contacto> contactos = new ArrayList<>();
    private ReservasAdapter adaptador;
    private ListView listado;
    String viene = "";
    Contacto contacto = null;
    ReservaHotel reservaHotel;
    Date fecha = new Date();
    DateFormat formatFecha = new SimpleDateFormat("dd-MM-yyyy");
    private ListIterator<DocumentSnapshot> listIterator = null;
    String fechaS;
    private int reservasSize;
    private int i;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        context = this;

        listado = (ListView) findViewById(R.id.reservas);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("...recuperando reservas...");
        //Autentificamos usuarios para firebase
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
        } else {
            signInAnonymously();
        }
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        //

        calendarView = (CalendarView) findViewById(R.id.calendarView_Hotel);



            viene = getIntent().getExtras().getString("VIENE");

            switch (viene) {
                case "main_activity":
                    break;
                default:
                    contacto = (Contacto) ComunicadorContacto.getContacto();
                    reservaHotel = (ReservaHotel) ComunicadorReserva.getReserva();
                    viene = "";
                    break;
            }


        fecha.setTime(calendarView.getDate());
        fechaS = new SimpleDateFormat("dd-MM-yyyy").format(fecha);
        try {
            fecha = formatFecha.parse(fechaS);
            recuperarFirebase(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //inicimosAdaptador();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                //inicimosAdaptador();
                String mes = String.valueOf(month + 1);
                fechaS = dayOfMonth + "-" + mes + "-" + year;

                try {
                    fecha = formatFecha.parse(fechaS);
                    //   Log.e("fechaS****",String.valueOf(fecha));
                    recuperarFirebase(fecha);
                    fecha = fecha;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


        });


    }

    private void recuperarFirebase(Date fecha) {
        contactos.clear();
        reservas.clear();
        docSnippets.getReservas(fecha);
        progressDialog.show();
    }



    public void bindeaYAñadeReservaHotel(DocumentSnapshot doc,int i,int reservasSize) {
        this.reservasSize = reservasSize;
        this.i = i;
        ReservaHotel con = new ReservaHotel(
                doc.getId(),
                doc.getString("idContacto"),
                doc.getDate("fechaInicio"),
                doc.getDate("fechaFin"),
                doc.getDouble("precio"),
                doc.getDouble("noches"),
                doc.getDouble("coste"),
                doc.getBoolean("pagado")

        );
        Log.e("ReservaHotel_ha", doc.toString());
        reservas.add(con);
        docSnippets.getContactoConId(doc.getString("idContacto"));


    }

    public void bindeaYAñadeContacto(DocumentSnapshot doc) {
        Contacto con = new Contacto(
                doc.getId(),
                Uri.parse(doc.getString("foto")),
                doc.getString("mascota"),
                doc.getString("raza"),
                doc.getString("tamaño"),
                doc.getString("telefono1"),
                doc.getString("telefono2"),
                doc.getString("propietario"));
        contactos.add(con);
        Log.e("contacto_ha",con.toString());
        inicimosAdaptador();

    }

    public void inicimosAdaptador() {
        // Inicializamos el adapter
        Log.e("hotelActivity","iniciamos adaptador contactos size_"+contactos.size()+"____iniciamos adaptador_reservas size_"+reservas.size());
        adaptador = new ReservasAdapter(this, reservas, contactos);
        listado.setAdapter(adaptador);
        progressDialog.dismiss();

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {

                ComunicadorReserva.setReservas(reservas);
                ComunicadorReserva.setReserva(reservas.get(position));
                ComunicadorContacto.setContacto(contactos.get(position));

                Intent i = new Intent(getApplicationContext(), FormularioHotelActivity.class);
                i.putExtra("VIENE","hotel_activity");
                startActivity(i);

            }
        });

    }

    public void añadirHotel(View view) {

        Intent i = new Intent(getApplicationContext(), ContactosActivity.class);
        i.putExtra("FECHA" , fechaS);
        i.putExtra("VIENE","hotel_activity");
        startActivity(i);
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
                        Log.e("PeluqueriasActivity", "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    private void intent() {
        Intent intent=null;
        switch (viene){

            case "main_activity":
                intent = new Intent(context, MainActivity.class);
                break;
            case "formulario_hotel_activity":
                intent = new Intent(context,FormularioHotelActivity.class);
                break;

        }
        Log.e("vine233",viene);
        intent.putExtra("VIENE","hotel_activity");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        intent();


    }
}
