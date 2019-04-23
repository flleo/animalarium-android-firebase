package com.example.fede.animalarium;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class PeluqueriasActivity extends AppCompatActivity {

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocSnippets docSnippets = new DocSnippets(db,this);
    ProgressDialog progressDialog;
    //

    CalendarView calendarView;
    public ArrayList<CitaPeluqueria> citas = new ArrayList<>();
    public ArrayList<Contacto> contactos = new ArrayList<>();
    private CitasAdapter adaptador;
    private ListView listado;
    String viene="";
    Contacto contacto = null;
    CitaPeluqueria cita ;
    Date fecha = new Date();
    DateFormat formatFecha = new SimpleDateFormat("dd-MM-yyyy");
    private ListIterator<DocumentSnapshot> peluqueriasLI;
    String fecha1="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peluquerias);

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("...recuperando citas...");
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

        calendarView = (CalendarView) findViewById(R.id.calendarView_Peluquerias);
        listado = (ListView) findViewById(R.id.citas);

        fecha.setTime(calendarView.getDate());
        fecha1 = fecha.getDate()+"-"+(fecha.getMonth()+1)+"-"+"20"+String.valueOf(fecha.getYear()).substring(1);



        try{
            viene = getIntent().getExtras().getString("VIENE");
            switch(viene){
                case "main_activity":break;
                default:
                    contacto = (Contacto) ComunicadorContacto.getObjeto();
                    cita = (CitaPeluqueria) ComunicadorCita.getObjeto();
                    break;

            };

        } catch (NullPointerException e1){
            contacto = (Contacto) ComunicadorContacto.getObjeto();
            cita = (CitaPeluqueria) ComunicadorCita.getObjeto();
            viene="";
        }




        recuperarFirebase(fecha);
        inicimosAdaptador();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                citas = new ArrayList<>();
                contactos = new ArrayList<>();

                //inicimosAdaptador();
                String mes = String.valueOf(month+1);
                fecha1 = dayOfMonth + "-" + mes + "-" + year;

                try {
                    Date date = formatFecha.parse(fecha1);
                    Log.e("fechaS****",String.valueOf(date));
                    recuperarFirebase(date);
                    fecha = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


        });

    }

    private void recuperarFirebase(Date fecha) {
        docSnippets.getCita(fecha);
        progressDialog.show();
    }






    public void añadirCita(View view) {
        Intent formulario;
        switch (viene) {
            case "cita_cambiar_fecha":
                Log.e("fecha_cita_cambiar\"", fecha.toString());
                Log.e("CitaTrabajo", cita.getTrabajo());
                fecha.setHours(cita.getFecha().getHours());
                fecha.setMinutes(cita.getFecha().getMinutes());
                cita.setFecha(fecha);
                ComunicadorCita.setObjeto(cita);
                formulario = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                formulario.putExtra("VIENE", "peluquerias_cambiar_fecha");
                break;
            default:
                ComunicadorCita.setObjeto(null);
                if (contacto != null) {
                    ComunicadorContacto.setObjeto(contacto);
                    formulario = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                    formulario.putExtra("VIENE", "peluquerias");
                } else {
                    formulario = new Intent(getApplicationContext(), ContactosActivity.class);
                    ComunicadorCita.setObjeto(null);
                    formulario.putExtra("VIENE", "peluquerias");
                }
                break;
        }
        Log.e("peluquerias_activity_fecha1",fecha1);
        formulario.putExtra("FECHA", fecha1);

        startActivity(formulario);

    }

    public void setPeluquerias(List<DocumentSnapshot> documents) {

        peluqueriasLI = documents.listIterator();
        bindeaYAñadeCita(peluqueriasLI.next());
    }

    public void bindeaYAñadeCita(DocumentSnapshot doc) {

        CitaPeluqueria con = new CitaPeluqueria(
                doc.getId(),
                doc.getString("idContacto"),
                doc.getDate("fecha"),
                doc.getString("trabajo"),
                doc.getDouble("tarifa")
        );
        Log.e("idcita",doc.getId());
        citas.add(con);
        docSnippets.getContactoConId(con.get_idContacto());


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
        Log.e("contacto",con.get_id()+con.getMascota());
        if(citas.size()==contactos.size() && !peluqueriasLI.hasNext()) {
            Log.e("citas.size=",String.valueOf(citas.size()));
            Log.e("contactos.size=",String.valueOf(contactos.size()));

            inicimosAdaptador();
            //Para que se recarguen los datos sin necesidad de scrolling
          //  inicimosAdaptador();
        } else {

            bindeaYAñadeCita(peluqueriasLI.next());
        }
        //inicimosAdaptador();

    }

    public void inicimosAdaptador(){
        // Inicializamos el adapter
        adaptador = new CitasAdapter(this,citas,contactos);
        listado.setAdapter(adaptador);
        progressDialog.dismiss();

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {

                ComunicadorCita.setObjeto(citas.get(position));
                ComunicadorContacto.setObjeto(contactos.get(position));

                Intent i = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                i.putExtra("VIENE","peluquerias_activity");
                startActivity(i);
            }
        });

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
                        Log.e("PeluqueriasActivity", "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    @Override
    public void onBackPressed() {

        Intent menu = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(menu);

    }



}
