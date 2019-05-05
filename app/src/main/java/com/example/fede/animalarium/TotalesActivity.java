package com.example.fede.animalarium;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TotalesActivity extends AppCompatActivity {

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef ;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets = new DocSnippets(db,this);

    Date fecha = new Date();
    EditText total_dia, total_semana,total_mes,total_año;
    EditText total_hotel_dia, total_hotel_semana,total_hotel_mes,total_hotel_año;
    DateFormat formatFecha = new SimpleDateFormat("dd-MM-yyyy");
    CalendarView calendarView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totales);


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
        //

        calendarView = (CalendarView) findViewById(R.id.calendarView_totales);

        //Peluquerias
        total_año = (EditText) findViewById(R.id.total_año);
        total_mes = (EditText) findViewById(R.id.total_mes);
        total_semana = (EditText) findViewById(R.id.total_semana);
        total_dia = (EditText) findViewById(R.id.total_dia);

        //Hoteles
        total_hotel_año = (EditText) findViewById(R.id.total_hotel_año);
        total_hotel_mes = (EditText) findViewById(R.id.total_hotel_mes);
        total_hotel_semana = (EditText) findViewById(R.id.total_hotel_semana);
        total_hotel_dia = (EditText) findViewById(R.id.total_hotel_dia);

        fecha.setTime(calendarView.getDate());
        totales(fecha);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                //inicimosAdaptador();
                String mes = String.valueOf(month+1);
                String fecha1 = dayOfMonth + "-" + mes + "-" + year;

                try {
                    fecha = formatFecha.parse(fecha1);
                    //   Log.e("fechaS****",String.valueOf(fecha));
                    totales(fecha);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void totales(Date date) {
        docSnippets.getTotalesDia(date);
        docSnippets.getTotalesSemana(date);
        docSnippets.getTotalesMes(date);
        docSnippets.getTotalesAño(date);

        docSnippets.getTotalesHotelDia(date);
        docSnippets.getTotalesHotelSemana(date);
        docSnippets.getTotalesHotelMes(date);
        docSnippets.getTotalesHotelAño(date);
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
