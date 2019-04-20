package com.example.fede.animalarium;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FormularioHotelActivity extends AppCompatActivity {

    //Firesbase
    private static final String TAG = "FormularioHotelActivity";
    private static final String KEY_ID_CONTACTO = "idContacto";
    private static final String KEY_FECHA_INICIO = "fechaInicio";
    private static final String KEY_FECHA_FIN = "fechaFin";
    private static final String KEY_PRECIO = "precio";
    private static final String KEY_NOCHES = "noches";
    private static final String KEY_COSTE = "coste";
    private static final String KEY_PAGADO = "pagado";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference contactoRef = db.collection("hoteles").document();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;

    //

    CalendarView calendarView;
    TextView fechainicio, fechafin, costestancia, numNoches;
    Button selFechaInicio, selFechaFin, añadir, actualizar, eliminar;
    Spinner precio, pagado;
    String fechaS = "", viene = "", precioS = "";
    Boolean pagadoB = false;
    Date fecha = new Date(), dateInicio = new Date(), dateFin = new Date();
    int noches, costeEstancia, precioInt;
    Contacto contacto;
    ReservaHotel reservaHotel;
    DocSnippets docSnippets;
    DateFormat formatFecha = new SimpleDateFormat("dd-MM-yyyy");
    Intent intent = null;
    private HashMap<String, Object> map;
    DocumentReference reserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_hotel);


        docSnippets = new DocSnippets(db, this);
        añadir = findViewById(R.id.button_hotel_añadir);
        actualizar = findViewById(R.id.button_hotel_actualizar);
        eliminar = findViewById(R.id.button_hotel_eliminar);
        selFechaInicio = findViewById(R.id.button_hotel_fecha_inicio);
        fechainicio = findViewById(R.id.fechainicio_formulario_hotel);
        fechafin = findViewById(R.id.fechafin_formulario_hotel);
        numNoches = findViewById(R.id.numero_noches_formulario_hotel);
        costestancia = findViewById(R.id.costestancia_formulario_hotel);
        precio = findViewById(R.id.precio_formulario_hotel);
        pagado = findViewById(R.id.pagado_formulario_hotel);
        calendarView = findViewById(R.id.calendarView_formulario_hotel);
        fecha.setTime(calendarView.getDate());

        contacto = (Contacto) ComunicadorContacto.getObjeto();
        reservaHotel = (ReservaHotel) ComunicadorReserva.getObjeto();

        if (reservaHotel != null && contacto != null) {
            reserva = db.collection("hoteles").document(reservaHotel.getId());
            bindeaReserva(reservaHotel);
        } else if (contacto == null) {
            añadir.setEnabled(false);
            intent = new Intent(getApplicationContext(), ContactosActivity.class);
            startActivity(intent);
        } else {
            actualizar.setEnabled(false);
            eliminar.setEnabled(false);
            añadir.setEnabled(false);
        }


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String mes = String.valueOf(month + 1);

                fechaS = dayOfMonth + "-" + mes + "-" + year;
                try {
                    fecha = formatFecha.parse(fechaS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        });

        Log.e("fecha formulaHotelActiv", fecha.toString());
        dateInicio.setTime(fecha.getTime());
        dateFin.setTime(fecha.getTime());

    }

    private void bindeaReserva(ReservaHotel reservaHotel) {

        fechainicio.setText(formatFecha.format(reservaHotel.getFechaInicio()));
        fechafin.setText(formatFecha.format(reservaHotel.getFechaFin()));
        precioS = String.valueOf(reservaHotel.getPrecio().intValue());
        switch (precioS) {
            case "12":
                precio.setSelection(0);
                break;
            default:
                precio.setSelection(1);
                break;
        }
        costestancia.setText(String.valueOf(reservaHotel.getCoste().intValue()));
        numNoches.setText(String.valueOf(reservaHotel.getNoches().intValue()));
        pagadoB = reservaHotel.getPagado();
        if (pagadoB = false) pagado.setSelection(0);
        else pagado.setSelection(1);
    }

    public void recogeReserva() {
        reservaHotel.setFechaInicio(dateInicio);
        reservaHotel.setFechaFin(dateFin);
        reservaHotel.setPrecio(precioInt);
        reservaHotel.setCoste(costeEstancia);
        reservaHotel.setNoches(noches);
        reservaHotel.setPagado(pagadoB);
    }

    public void bindeaFechaInicio(View view) {

        fechainicio.setText(fechaS);
        dateInicio.setTime(fecha.getTime());
        noches();
    }


    public void bindeaFechaFin(View view) {

        fechafin.setText(fechaS);
        dateFin.setTime(fecha.getTime());
        noches();

    }

    private void noches() {
        long dif = (dateFin.getTime() - dateInicio.getTime());
        Date tiempo = new Date();
        tiempo.setTime(dif);
        noches = (int) tiempo.getTime() / (3600000 * 24);
        numNoches.setText(String.valueOf(noches));

    }

    public void calcular(View view) {
        precioInt = Integer.valueOf(precio.getSelectedItem().toString());
        costeEstancia = precioInt * noches;
        costestancia.setText(String.valueOf(costeEstancia));
        if (reservaHotel == null)
            añadir.setEnabled(true);
    }

    public void eliminarHotel(View view) {

       reserva.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

           @Override

           public void onSuccess(Void aVoid) {
               Toast.makeText(FormularioHotelActivity.this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
           }

       });


        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_seguro_eliminar_reserva);
            builder.setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.collection("hoteles").document(reservaHotel.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "Reserva eliminada, con éxito", Toast.LENGTH_SHORT);
                            toast1.show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toast1 =
                                            Toast.makeText(getApplicationContext(),
                                                    "La reserva no se pudo eliminar", Toast.LENGTH_SHORT);
                                    toast1.show();
                                }
                            });
                    Intent intent = new Intent(getApplicationContext(), HotelActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            builder.create();
            builder.show();

        } catch (NullPointerException e) {
            Log.e("No se pudo eliminar ", "index=" + Integer.valueOf(reservaHotel.getId()));

        }

    }

    public void actualizarHotel(View view) {
        recogeReserva();
        mapeaReserva();

        reserva.update(KEY_FECHA_INICIO, reservaHotel.getFechaInicio());
        reserva.update(KEY_FECHA_FIN, reservaHotel.getFechaFin());
        reserva.update(KEY_PRECIO, reservaHotel.getPrecio());
        reserva.update(KEY_COSTE, reservaHotel.getCoste());
        reserva.update(KEY_NOCHES, reservaHotel.getNoches());
        reserva.update(KEY_PAGADO, reservaHotel.getPagado())
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FormularioHotelActivity.this, "Reserva acutalizada con éxito", Toast.LENGTH_SHORT).show();
                    }

                });
    }


    public void añadirHotel(View view) {

        String pagadoS = pagado.getSelectedItem().toString();
        switch (pagadoS) {
            case "Sí":
                pagadoB = true;
                break;
            case "No":
                pagadoB = false;
                break;
        }

        reservaHotel = new ReservaHotel(null, contacto.get_id(), dateInicio, dateFin, precioInt, noches, costeEstancia, pagadoB);

        mapeaReserva();
        //Firebase
        contactoRef.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FormularioHotelActivity.this, "Cita añadida", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), HotelActivity.class);
                        startActivity(intent);
                        ComunicadorContacto.setObjeto(null);
                        ComunicadorReserva.setObjeto(null);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormularioHotelActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        });

    }

    public void mapeaReserva() {

        map = new HashMap<>();

        map.put(KEY_ID_CONTACTO, reservaHotel.getId_contacto());
        map.put(KEY_FECHA_INICIO, reservaHotel.getFechaInicio());
        map.put(KEY_FECHA_FIN, reservaHotel.getFechaFin());
        map.put(KEY_NOCHES, reservaHotel.getNoches());
        map.put(KEY_COSTE, reservaHotel.getCoste());
        map.put(KEY_PRECIO, reservaHotel.getPrecio());
        map.put(KEY_PAGADO, reservaHotel.getPagado());

    }
}
