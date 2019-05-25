package com.example.fede.animalarium;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class PeluqueriasActivity extends AppCompatActivity {

    private static Activity context;
    private static Uri uri;
    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocSnippets docSnippets = new DocSnippets(db,this);
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    ProgressDialog progressDialog;
    //

    CalendarView calendarView;
    public ArrayList<CitaPeluqueria> citas = new ArrayList<>();
    public ArrayList<Contacto> contactos = new ArrayList<>();
    private CitasAdapter adaptador;
    private ListView listado;
    String viene="";
    static Contacto contacto = null;
    CitaPeluqueria cita ;
    Date fecha = new Date();
    DateFormat formatFecha = new SimpleDateFormat("dd-MM-yyyy");
    private ListIterator<DocumentSnapshot> peluqueriasLI;
    String fecha1="";
    static ArrayList<String> fotos = new ArrayList<String>();
    static ArrayList<Uri> uris = new ArrayList<Uri>();
    private ArrayList<String> fotosS = new ArrayList<>();
    private static String fotoS="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peluquerias);

        context = this;
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

        calendarView = (CalendarView) findViewById(R.id.calendarView_peluquerias);
        listado = (ListView) findViewById(R.id.citas);

        fecha.setTime(calendarView.getDate());
        fecha1 = fecha.getDate()+"-"+(fecha.getMonth()+1)+"-"+"20"+String.valueOf(fecha.getYear()).substring(1);

        contacto = (Contacto) ComunicadorContacto.getContacto();
        cita = (CitaPeluqueria) ComunicadorCita.getObjeto();

        viene = getIntent().getExtras().getString("VIENE");

        recuperarFirebase(fecha);
        inicimosAdaptador();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                citas.clear();
                contactos.clear();

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
        docSnippets.getCitas(fecha);
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
                    ComunicadorContacto.setContacto(contacto);
                    formulario = new Intent(getApplicationContext(), FormularioCitaActivity.class);
                    formulario.putExtra("VIENE", "peluquerias_activity");
                } else {
                    formulario = new Intent(getApplicationContext(), ContactosActivity.class);
                    ComunicadorCita.setObjeto(null);
                    formulario.putExtra("VIENE", "peluquerias_activity");
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
        CitaPeluqueria con = new CitaPeluqueria();
        con.set_id(doc.getId());
        try{
            con.set_idContacto(doc.getString("idContacto"));
        } catch (RuntimeException e){
            con.set_idContacto(doc.getDocumentReference("idContacto").getId());
            Log.e("dcorevere-idcontacot",doc.getDocumentReference("idContacto").getId());
        }

        con.setFecha(doc.getDate("fecha"));
        con.setTarifa(doc.getDouble("tarifa"));
        con.setTrabajo(doc.getString("trabajo"));

        Log.e("cita",con.toString());
        citas.add(con);
        docSnippets.getContactoConId(con.get_idContacto());


    }




    public void bindeaYAñadeContacto(DocumentSnapshot doc) {
        fotosS.add(doc.getString("foto"));
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
            inicimosAdaptador();
        } else {
            bindeaYAñadeCita(peluqueriasLI.next());
        }

    }

    public void inicimosAdaptador(){
        adaptador = new CitasAdapter(this,citas,contactos);
        listado.setAdapter(adaptador);
        progressDialog.dismiss();

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> ada, View v, int position, long arg3) {

                ComunicadorCita.setObjeto(citas.get(position));
                contacto = contactos.get(position);

                if(viene.equalsIgnoreCase("main_activity")){
                    fotoS = fotosS.get(position);
                    grabaFotoMobil();
                } else {
                    ComunicadorContacto.setContacto(contactos.get(position));
                    Intent i = new Intent(context, FormularioCitaActivity.class);
                    i.putExtra("VIENE","peluquerias_activity");
                    startActivity(i);
                }
            }
        });
    }

    //RECUPERAMOS FOTO DESDE FIREBASE
    private void grabaFotoMobil() {

        try {
            final File localFile = File.createTempFile("images", "jpg");
            StorageReference storageRef = storage.getReferenceFromUrl("gs://animalarium-android-6eb93.appspot.com/external/images/media").child(fotoS);
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    uri = getImageUri(bitmap);
                    contacto.setFoto(uri);
                    ComunicadorContacto.setContacto(contacto);
                    Intent i = new Intent(context, FormularioCitaActivity.class);
                    i.putExtra("VIENE","peluquerias_activity");
                    startActivity(i);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("FAllo_cargarFoto", exception.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){}
    }

    private static Uri getImageUri(Bitmap bitmap) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
            uri = Uri.parse(path);
            return uri;
        }
        return null;
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
