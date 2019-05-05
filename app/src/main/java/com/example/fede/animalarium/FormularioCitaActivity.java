package com.example.fede.animalarium;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormularioCitaActivity extends AppCompatActivity {

    //Firesbase
    private static final String TAG = "FormularioCitaActivity";
    private static final String KEY_ID_CONTACTO = "idContacto";
    private static final String KEY_FECHA = "fecha";
    private static final String KEY_TRABAJO = "trabajo";
    private static final String KEY_TARIFA = "tarifa";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference contactoRef = db.collection("citas").document();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference storageRef;
    DocSnippets docSnippets = new DocSnippets(db, this);
    //

    Contacto contacto;
    CitaPeluqueria citaPeluqueria;
    private static EditText mascota, raza, telefono1, telefono2, propietario, hora, minutos, tarifa;
    Button añadir, actualizar, eliminar;
    Spinner tamaño, trabajo;
    private static ImageButton foto;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Uri imageUri;
    private int PICK_IMAGE;
    String viene = "", fechaS = "";
    Date fecha = new Date();
    String fotoS;
    Uri uri;
    DateFormat dfFecha = new SimpleDateFormat("dd-MM-yyyy");
    DateFormat dfHora = new SimpleDateFormat("HH:mm");
    private ArrayList<CitaPeluqueria> citas = new ArrayList<>();
    private FormularioCitaActivity context;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_cita);

        context = this;
        progressDialog = new ProgressDialog(context);

        foto = (ImageButton) findViewById(R.id.imageButtonFormulario);
        mascota = (EditText) findViewById(R.id.mascota);
        raza = (EditText) findViewById(R.id.raza);
        telefono1 = (EditText) findViewById(R.id.telefono1);
        telefono2 = (EditText) findViewById(R.id.telefono2);
        propietario = (EditText) findViewById(R.id.propietario);
        hora = (EditText) findViewById(R.id.hora);
        hora.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() == 1) s.insert(0, "0");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        minutos = (EditText) findViewById(R.id.minutos);
        tamaño = (Spinner) findViewById(R.id.tamaño);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource(this, R.array.tamaños, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tamaño.setEnabled(false);
        tamaño.setAdapter(spinner_adapter);
        trabajo = (Spinner) findViewById(R.id.trabajo);
        ArrayAdapter spinner_adapter1 = ArrayAdapter.createFromResource(this, R.array.trabajos, android.R.layout.simple_spinner_item);
        trabajo.setAdapter(spinner_adapter1);
        tarifa = (EditText) findViewById(R.id.tarifa);
        añadir = (Button) findViewById(R.id.formulario_activity_añadir_button);
        actualizar = (Button) findViewById(R.id.formulario_activity_actualizar_button);
        eliminar = (Button) findViewById(R.id.formulario_activity_eliminar_button);

        contacto = (Contacto) ComunicadorContacto.getContacto();
        citaPeluqueria = (CitaPeluqueria) ComunicadorCita.getObjeto();
        citas = ComunicadorCita.getSusCitas();

        if (contacto!=null) bindeaContacto(contacto);
        if (citaPeluqueria!=null) bindeaCita(citaPeluqueria);


        try {
            viene = getIntent().getExtras().getString("VIENE");
            switch (viene) {

                case "peluquerias_activity":
                    if(citaPeluqueria==null){
                        añadir.setEnabled(true);
                        eliminar.setEnabled(false);
                        actualizar.setEnabled(false);
                        fechaS = getIntent().getExtras().getString("FECHA");
                        try {
                            fecha = dfFecha.parse(fechaS);
                        } catch (ParseException e) {
                            Log.e("fallo fechaS formulario cita", fechaS);
                            e.printStackTrace();
                        }
                    } else {
                        añadir.setEnabled(false);
                        eliminar.setEnabled(true);
                        actualizar.setEnabled(true);
                    }
                    break;

                case "peluquerias_cambiar_fecha":
                    añadir.setEnabled(false);
                    eliminar.setEnabled(false);
                    añadir.setText("CAMBIAR FECHA");
                    añadir.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            ComunicadorCita.setObjeto(citaPeluqueria);
                            Intent intent = new Intent(getApplicationContext(), PeluqueriasActivity.class);
                            intent.putExtra("VIENE", "cita_cambiar_fecha");
                            startActivity(intent);
                        }
                    });
                    break;

                case "peluquerias_contacto_activity":
                    añadir.setEnabled(false);
                    eliminar.setEnabled(true);
                    actualizar.setEnabled(true);
                    break;
                case "contactos_activity":
                    añadir.setEnabled(true);
                    eliminar.setEnabled(false);
                    actualizar.setEnabled(false);
                    fechaS = getIntent().getExtras().getString("FECHA");
                    try {
                        fecha = dfFecha.parse(fechaS);
                    } catch (ParseException e) {
                        Log.e("fallo fechaS formulario cita", fechaS);
                        e.printStackTrace();
                    }
                    break;
            }


        } catch (NullPointerException e) {
            Log.e("Viene_formulario_cita_ctivity", e.getMessage());
        }


        añadir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    añadirCita();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    public void cargarFoto(final DocumentSnapshot document) {


        try {
            storageRef = storage.getReferenceFromUrl("gs://animalarium-android-6eb93.appspot.com/external/images/media").child(document.getString("foto"));
            //  Log.e("foto",document.getString("foto"));
            final File localFile = File.createTempFile("images", "jpg");
            final DocumentSnapshot doc = document;
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    uri = getImageUri(bitmap);
                    bindeaContactoDocument(doc, uri);

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("FAllo", exception.getMessage());
                }
            });

            String dir = getFilesDir().getAbsolutePath();
            File f0 = new File(dir, localFile.getName());
            boolean d0 = f0.delete();
            Log.w("Delete Check", "File deleted: " + dir + "/myFile " + d0);

        } catch (IOException e) {
            Log.e("FAllo", "IOException");
        } catch (IllegalArgumentException e) {
            bindeaContactoDocument(document, uri);
        }

    }

    private void bindeaContactoDocument(DocumentSnapshot doc, Uri uri) {
//        if(uri!=null)
//            Log.e("uri",uri.toString());
        Contacto con = new Contacto(
                doc.getId(),
                uri,
                doc.getString("mascota"),
                doc.getString("raza"),
                doc.getString("tamaño"),
                doc.getString("telefono1"),
                doc.getString("telefono2"),
                doc.getString("propietario"));
        bindeaContacto(con);
        // Log.e("contactoS", doc.getString("mascota"));
    }

    private Uri getImageUri(Bitmap bitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(FormularioCitaActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null, null);
            uri = Uri.parse(path);

            return uri;
        }
        return null;
    }


    private void bindeaCita(CitaPeluqueria citaPeluqueria) {

        try {

            hora.setText(String.valueOf(citaPeluqueria.getFecha().getHours()));
            minutos.setText(String.valueOf(citaPeluqueria.getFecha().getMinutes()));
            switch (citaPeluqueria.getTrabajo()) {
                case "Completo":
                    trabajo.setSelection(0);
                    break;
                case "Retoque":
                    trabajo.setSelection(1);
                    break;
                case "Baño":
                    trabajo.setSelection(2);
                    break;
            }
        } catch (NullPointerException e) {
            trabajo.setSelection(0);
        }
        tarifa.setText(String.valueOf(citaPeluqueria.getTarifa()));
    }


    public void bindeaContacto(Contacto contacto) {
        try {

            this.contacto = contacto;
            foto.setImageURI(contacto.getFoto());
            mascota.setText(contacto.getMascota());
            raza.setText(contacto.getRaza());
            telefono1.setText(contacto.getTelefono1());
            telefono2.setText(contacto.getTelefono2());
            propietario.setText(contacto.getPropietario());

            try {
                switch (contacto.getTamaño()) {
                    case "Pequeño":
                        tamaño.setSelection(0);
                        break;
                    case "Mediano":
                        tamaño.setSelection(1);
                        break;
                    case "Grande":
                        tamaño.setSelection(2);
                        break;
                }
            } catch (NullPointerException e) {
                tamaño.setSelection(0);
            }

        } catch (OutOfMemoryError error) {
        } catch (NullPointerException error) {

        }
    }


    public void añadirCita() throws ParseException {
        try {

            fecha.setHours(Integer.valueOf(hora.getText().toString()));
            fecha.setMinutes(Integer.valueOf(minutos.getText().toString()));

            Log.e("fecha_aññadircita", fecha.toString());
            citaPeluqueria = new CitaPeluqueria(null, contacto.get_id(), fecha, trabajo.getSelectedItem().toString(), Double.valueOf(tarifa.getText().toString()));

            Log.e("citanueva_formulario_cita_activity", citaPeluqueria.toString());
            Map<String, Object> map = new HashMap<>();

            map.put(KEY_ID_CONTACTO, citaPeluqueria.get_idContacto());
            map.put(KEY_FECHA, citaPeluqueria.getFecha());
            map.put(KEY_TRABAJO, citaPeluqueria.getTrabajo());
            map.put(KEY_TARIFA, citaPeluqueria.getTarifa());

            //Firebase
            contactoRef.set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(FormularioCitaActivity.this, "Cita añadida", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), PeluqueriasActivity.class);
                            startActivity(intent);
                            ComunicadorContacto.setContacto(null);
                            Log.e("cita__id_fca", contactoRef.getId());
                            docSnippets.getCitaConId(contactoRef.getId());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FormularioCitaActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                }
            });
        } catch (StringIndexOutOfBoundsException e) {
            Toast.makeText(FormularioCitaActivity.this, "Debes introducir la hora de la cita", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());
        }


    }


    public void actualizarCita(View view) {

        try {

            progressDialog.setTitle("...actualizando cita...");
            progressDialog.show();
            fecha.setHours(Integer.parseInt(hora.getText().toString()));
            fecha.setMinutes(Integer.parseInt(minutos.getText().toString()));

            citaPeluqueria = new CitaPeluqueria(citaPeluqueria.get_id(), citaPeluqueria.get_idContacto(), fecha, trabajo.getSelectedItem().toString(), Double.valueOf(tarifa.getText().toString()));

            DocumentReference contact = db.collection("citas").document(citaPeluqueria.get_id());

            contact.update(KEY_FECHA, citaPeluqueria.getFecha());
            contact.update(KEY_TARIFA, citaPeluqueria.getTarifa());
            contact.update(KEY_TRABAJO, citaPeluqueria.getTrabajo())

                    .addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override

                        public void onSuccess(Void aVoid) {

                            Toast.makeText(context, "Cita actualizada ;)", Toast.LENGTH_SHORT).show();
                            ComunicadorCita.setObjeto(citaPeluqueria);
                            actualizaComunicadorCita();
                        }

                    });


        } catch (NullPointerException e) {
            Log.e("No se modificar: ", "Fallo al actualizar"

            );

        }
    }


    public void eliminarCita(View view) {
        try {
            Log.e("cita_formcitaactivi", citaPeluqueria.get_id());
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_seguro_eliminar_cita);
            builder.setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.collection("citas").document(citaPeluqueria.get_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "La cita de peluqueria ha sido eliminada", Toast.LENGTH_SHORT);
                            toast1.show();
                            citas.remove(citaPeluqueria);
                            ComunicadorCita.setSusCitas(citas);
                            intent();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
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
            Log.e("Error", "Error al localizar la cita de peluqueria");

        }
    }

    private void actualizaComunicadorCita() {

        for (int i=0;i<citas.size();i++) {
            if (citas.get(i).get_id().equalsIgnoreCase(citaPeluqueria.get_id())) {
                citas.set(i,citaPeluqueria);
                progressDialog.dismiss();
                intent();
                break;
            }
        }




    }

    private ArrayList<CitaPeluqueria> añadimosCitaAlComunicador() {
        citas.add(citaPeluqueria);
        //ordenamos los contactos
        Collections.sort(citas, new Comparator<CitaPeluqueria>() {
            @Override
            public int compare(CitaPeluqueria o1, CitaPeluqueria o2) {
                return o1.getFecha().compareTo(o2.getFecha());
            }
        });
        //
        return citas;
    }


    // Llamada telefonica

    public void llamada1(View view) {
        try {
            llama("tel:" + telefono1.getText());
        } catch (NullPointerException e) {
        }
    }

    public void llamada2(View view) {
        try {
            llama("tel:" + telefono2.getText());
        } catch (NullPointerException e) {
        }
    }

    private void llama(String s) {
        Intent llamada1 = new Intent(Intent.ACTION_CALL);
        llamada1.setData(Uri.parse(s));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            requestForCallPermission();

        } else {
            startActivity(llamada1);
        }
    }

    private void requestForCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void peluquerias(View view) {


        Intent intent = new Intent(this, PeluqueriasContactoActivity.class);
        startActivity(intent);


    }

    // Termina llamada telefonica


    public void horaOnClick(View view) {
        hora.setText("");
    }

    public void minutosOnClick(View view) {
        minutos.setText("");
    }


    public void tarifaOnClick(View view) {
        tarifa.setText("");
    }

    private void intent() {
        Intent intent = null;
        switch (viene) {
            case "peluquerias_contacto_activity":
                intent = new Intent(getApplicationContext(), PeluqueriasContactoActivity.class);
                ComunicadorCita.setSusCitas(añadimosCitaAlComunicador());
                break;
            case "peluquerias_activity":
                intent = new Intent(getApplicationContext(), PeluqueriasActivity.class);
                break;
        }
        intent.putExtra("VIENE", "formulario_cita_activity");
        startActivity(intent);
    }


}
