package com.example.fede.animalarium;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class FormularioActivity extends AppCompatActivity {

    //Firesbase
    private static final String TAG = "FormularioActivity";
    private static final String KEY_FOTO = "foto";
    private static final String KEY_MASCOTA = "mascota";
    private static final String KEY_RAZA = "raza";
    private static final String KEY_TAMAÑO = "tamaño";
    private static final String KEY_TELEFONO1 = "telefono1";
    private static final String KEY_TELEFONO2 = "telefono2";
    private static final String KEY_PROPIETARIO = "propietario";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference contactosRef = db.collection("contactos").document();
    DocumentReference citasRef = db.collection("citas").document();
    DocumentReference reservasRef = db.collection("hoteles").document();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;

    //

    public Contacto contacto = new Contacto();
    ContactoS contactoS;

    private static EditText mascota, raza, telefono1, telefono2, propietario;
    private Spinner tamaño;
    private static ImageButton foto;
    Button añadir, actualizar, eliminar, citas, reservas;
    private static final int PICK_IMAGE = 1;
    private Uri selectedImageUri, oldSelectedImageUri, imageUri;
    Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String num_llamar, viene = "";
    int _id;
    private String fotoS = null;
    DocSnippets docSnippets;
    private ArrayList<Contacto> contactos = new ArrayList<>();
    private ProgressDialog progressDialog = null;
    private ArrayList<Uri> uris;
    ArrayList<CitaPeluqueria> citass = new ArrayList<>();
    private ArrayList<ReservaHotel> reservass = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);


        //
        context = this;
        docSnippets = new DocSnippets(db, (FormularioActivity) context);
        progressDialog = new ProgressDialog(this);
        contactos = ComunicadorContacto.getContactos();
        uris = ComunicadorContacto.getUris();
        if (uris.size()==0){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.image);
            Uri uri = getImageUri(bitmap);
            imageUri = uri;
            selectedImageUri = uri;
        } else
        if (uris.size()!=0){
            imageUri = uris.get(0);             //definimos la por defecto
            selectedImageUri = uris.get(0);
        }
        //Inicializamos
        foto = (ImageButton) findViewById(R.id.imageButtonFormulario);
        mascota = (EditText) findViewById(R.id.mascota);
        raza = (EditText) findViewById(R.id.raza);
        telefono1 = (EditText) findViewById(R.id.telefon1);
        telefono2 = (EditText) findViewById(R.id.telefon2);
        propietario = (EditText) findViewById(R.id.propietario);

        //Inicializamos el spinner de tamaños
        tamaño = (Spinner) findViewById(R.id.spinner);
        añadir = (Button) findViewById(R.id.formulario_activity_añadir_button);
        actualizar = findViewById(R.id.formulario_activity_actualizar_button);
        eliminar = findViewById(R.id.formulario_activity_eliminar_button);
        citas = findViewById(R.id.formulario_susCitas_button);
        reservas = findViewById(R.id.formulario_susReservas_button);



        viene = getIntent().getExtras().getString("VIENE");
        switch (viene) {

            case "contactos_activity_añadirContacto":
                añadir.setEnabled(true);
                actualizar.setEnabled(false);
                eliminar.setEnabled(false);
                citas.setEnabled(false);
                reservas.setEnabled(false);
                break;

            default:
                contacto = (Contacto) ComunicadorContacto.getContacto();
                docSnippets.getPeluqueriasPorContacto();
                docSnippets.getReservasPorContacto();
                bindeaContactoView(contacto);
                añadir.setEnabled(false);
                oldSelectedImageUri = contacto.getFoto();
                selectedImageUri = oldSelectedImageUri;
                break;
        }


        //Pulsación larga -> Eliminamos la foto
        foto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.dialog_deseas_eliminarFoto);
                builder.setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        foto.setImageURI(imageUri);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.create();
                builder.show();


                return true;

            }
        });


    }




    private void openGalleryContacto(View view) {
        //ACTION_OPEN_DOCUMENT paar poder recuperar la foto luego
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        String filePath = null;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == FormularioActivity.RESULT_OK) {
                    //selectedImageUri = imageReturnedIntent.getData();
                    try {
                        selectedImageUri = getImageUri(reducirImagen(context, imageReturnedIntent.getData(), 104));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    foto.setImageURI(selectedImageUri);


                }
                break;
        }


    }


    public void bindeaContactoView(Contacto contacto) {
        try {

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
        } catch (NullPointerException e) {
            foto.setImageURI(imageUri);
            tamaño.setSelection(0);
        }
    }

    // Llamada telefonica

    public void llamada1(View view) {
        num_llamar = "tel:" + telefono1.getText();
        llama();

    }

    public void llamada2(View view) {
        num_llamar = "tel:" + telefono2.getText();
        llama();

    }

    private void llama() {
        Intent llamada1 = new Intent(Intent.ACTION_CALL);
        llamada1.setData(Uri.parse(num_llamar));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    llama();
                }
                break;
        }
    }

    public void peluquerias(View view) {

        //ComunicadorContacto.setReserva(contacto);
        Intent intent = new Intent(this, PeluqueriasContactoActivity.class);
        intent.putExtra("VIENE", "formulario_activity");
        startActivity(intent);


    }

    public void hotel(View view) {
        ComunicadorContacto.setContacto(contacto);
        Intent intent = new Intent(this, HotelContactoActivity.class);
        intent.putExtra("VIENE", "formulario_activity");
        startActivity(intent);
    }

    // Termina llamada telefonica


    //Añadir contacto

    public void añadir(View view) {

        if (!comprobarAñadir()) {
            //Con foto  SUBIMOS FOTO A FIREBASE
            try {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(selectedImageUri.getPath());
                progressDialog.setTitle("Subiendo foto...");
                progressDialog.show();
                StorageTask<UploadTask.TaskSnapshot> foto_subida_con_éxito = storageReference.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(FormularioActivity.this, "Foto subida con éxito", Toast.LENGTH_SHORT).show();
                                fotoS = storageReference.getName();
                                contactoS = new ContactoS(null, storageReference.getName(), mascota.getText().toString(), raza.getText().toString(), tamaño.getSelectedItem().toString(), telefono1.getText().toString(), telefono2.getText().toString(), propietario.getText().toString());
                                añadirContacto1(contactoS);
                                ;
                                // Log.e("foto path",storageReference.getPath());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(FormularioActivity.this, "Fallo al subir la foto" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Subiendo foto... " + (int) progress + "%");
                            }
                        });

            } catch (NullPointerException e) {
                /*//Sin Foto, le asignamos la foto de firebase que tenemos por defecto
                ContactoS contactoS = new ContactoS(null, "46841", mascota.getText().toString(), raza.getText().toString(), tamaño.getSelectedItem().toString(), telefono1.getText().toString(), telefono2.getText().toString(), propietario.getText().toString());
                añadirContacto1(contactoS);
                */
                e.getMessage();
            }
        }

    }

    private boolean comprobarAñadir() {
        boolean añadido = false;
        if (selectedImageUri.compareTo(imageUri) == 0) {
            ContactoS contactoS = new ContactoS(null, "46841", mascota.getText().toString(), raza.getText().toString(), tamaño.getSelectedItem().toString(), telefono1.getText().toString(), telefono2.getText().toString(), propietario.getText().toString());
            añadirContacto1(contactoS);
            añadido = true;
        }

        return añadido;
    }


    public void actualizar(View view) {

        try {

            if (!selectedImageUri.equals(oldSelectedImageUri)) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Subiendo foto...");
                progressDialog.show();
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(selectedImageUri.getPath());
                StorageTask<UploadTask.TaskSnapshot> foto_subida_con_éxito = storageReference.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(FormularioActivity.this, "Foto subida con éxito", Toast.LENGTH_SHORT).show();
                                contactoS = new ContactoS(contacto.get_id(), storageReference.getName(), mascota.getText().toString(), raza.getText().toString(), tamaño.getSelectedItem().toString(), telefono1.getText().toString(), telefono2.getText().toString(), propietario.getText().toString());
                                actualizarContacto1(contactoS);
                                oldSelectedImageUri = selectedImageUri;
                                // Log.e("foto path",storageReference.getPath());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(FormularioActivity.this, "Fallo al subir la foto" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Subiendo foto... " + (int) progress + "%");
                            }
                        });
            } else {
                contactoS = new ContactoS(contacto.get_id(), "46841", mascota.getText().toString(), raza.getText().toString(), tamaño.getSelectedItem().toString(), telefono1.getText().toString(), telefono2.getText().toString(), propietario.getText().toString());
                actualizarContacto1(contactoS);
            }
        } catch (NullPointerException e) {

            ContactoS contactoS = new ContactoS(contacto.get_id(), null, mascota.getText().toString(), raza.getText().toString(), tamaño.getSelectedItem().toString(), telefono1.getText().toString(), telefono2.getText().toString(), propietario.getText().toString());

            actualizarContacto1(contactoS);
        }

    }


    public void añadirContacto1(final ContactoS contactoS) {

        Map<String, Object> map = new HashMap<>();

        map.put(KEY_FOTO, contactoS.getFoto());
        map.put(KEY_MASCOTA, contactoS.getMascota());
        map.put(KEY_RAZA, contactoS.getRaza());
        map.put(KEY_TAMAÑO, contactoS.getTamaño());
        map.put(KEY_TELEFONO1, contactoS.getTelefono1());
        map.put(KEY_TELEFONO2, contactoS.getTelefono2());
        map.put(KEY_PROPIETARIO, contactoS.getPropietario());

        //Firebase
        contactosRef.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FormularioActivity.this, "Contacto añadido", Toast.LENGTH_SHORT).show();
                        Log.e("ID", contactosRef.getId());
                        contactoS.set_id(contactosRef.getId());
                        bindeaContactoS(contactoS);
                        añadimosContactoAlComunicador(contacto);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormularioActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        });

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
        contacto = con;
        ComunicadorContacto.setContacto(contacto);
        añadimosContactoAlComunicador(contacto);
        Intent menu = new Intent(getApplicationContext(), ContactosActivity.class);
        menu.putExtra("VIENE", "formulario_activity");
        startActivity(menu);

    }


    public void actualizarContacto1(final ContactoS contactoS) {

        progressDialog.setTitle("...actualizando contacto...");
        progressDialog.show();
        DocumentReference contact = db.collection("contactos").document(contactoS.get_id());

        /*if (contactoS.getFoto() != null) {

            Log.e("contacto.getFoto()_actualizaContacto1!=null", contactoS.getFoto());
        } else {
            Log.e("contacto.getFoto()_actualizaContacto1==null", contactoS.getFoto());
        }*/
        contact.update(KEY_FOTO, contactoS.getFoto());
        contact.update(KEY_MASCOTA, contactoS.getMascota());
        contact.update(KEY_RAZA, contactoS.getRaza());
        contact.update(KEY_TAMAÑO, contactoS.getTamaño());
        contact.update(KEY_TELEFONO1, contactoS.getTelefono1());
        contact.update(KEY_TELEFONO2, contactoS.getTelefono2());
        contact.update(KEY_PROPIETARIO, contactoS.getPropietario())

                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Contacto actualizado, con éxito", Toast.LENGTH_SHORT).show();
                        Log.e("contacto_actualizado_ mascota:", contactoS.getMascota());

                        actualizaComunicadorContacto();

                    }

                });


    }


    public void eliminarContacto(View view) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_seguro_eliminar_contacto);
            builder.setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.collection("contactos").document(contacto.get_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            eliminaSusCitas();
                            eliminaSusReservas();
                            Toast.makeText(getApplicationContext(), "Mascota eliminada, con éxito", Toast.LENGTH_SHORT).show();
                            eliminaContactoDelComunicador(contacto);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toast1 =
                                            Toast.makeText(getApplicationContext(),
                                                    "El contacto no se pudo eliminar", Toast.LENGTH_SHORT);
                                    toast1.show();
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
            Log.e("No se pudo eliminar ", "index=" + Integer.valueOf(contacto.get_id()));

        }
    }

    private void eliminaSusCitas(){
       for (CitaPeluqueria cita:citass){
           db.collection("citas").document(cita.get_id()).delete();
       }
    }

    private void eliminaSusReservas() {
        for (ReservaHotel res: reservass){
            db.collection("hoteles").document(res.getId()).delete();
        }
    }

    private void añadimosContactoAlComunicador(Contacto con) {

        contactos.add(con);
        //ordenamos los contactos
        Collections.sort(contactos, new Comparator<Contacto>() {
            @Override
            public int compare(Contacto o1, Contacto o2) {
                return o1.getMascota().compareToIgnoreCase(o2.getMascota());
            }
        });
        //
        ComunicadorContacto.setContactos(contactos);
        ComunicadorContacto.setContacto(con);

        añadir.setEnabled(false);
        actualizar.setEnabled(true);
        eliminar.setEnabled(true);
        citas.setEnabled(true);
        reservas.setEnabled(true);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void actualizaComunicadorContacto() {

        bindeaContactoS(contactoS);
        contactos.remove(contacto);
        añadimosContactoAlComunicador(contacto);

    }

    private void bindeaContactoS(ContactoS con) {
        contacto.set_id(con.get_id());
        contacto.setTelefono2(con.getTelefono2());
        contacto.setTelefono1(con.getTelefono1());
        contacto.setTamaño(con.getTamaño());
        contacto.setRaza(con.getRaza());
        contacto.setPropietario(con.getPropietario());
        contacto.setMascota(con.getMascota());
        contacto.setFoto(selectedImageUri);
    }


    private void eliminaContactoDelComunicador(Contacto contacto) {

        contactos.remove(contacto);
        ComunicadorContacto.setContactos(contactos);
        Intent intent = new Intent(getApplicationContext(), ContactosActivity.class);
        intent.putExtra("VIENE", "formulario_activity");
        startActivity(intent);

    }

    public static Bitmap reducirImagen(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    private Uri getImageUri(Bitmap bitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(FormularioActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
            Uri uri = Uri.parse(path);

            return uri;
        }
        return null;
    }

    public void bindeaCitas(QuerySnapshot result) {
        CitaPeluqueria cita = new CitaPeluqueria();

        for (DocumentSnapshot doc: result){
            cita.set_id(doc.getId());
            cita.set_idContacto(doc.getString("idContacto"));
            cita.setFecha(doc.getDate("fecha"));
            cita.setTrabajo(doc.getString("trabajo"));
            cita.setTarifa(doc.getDouble("tarifa"));
            citass.add(cita);
        }

        ComunicadorCita.setSusCitas(citass);
    }

    public void bindeaReservas(QuerySnapshot result) {
        ReservaHotel res = new ReservaHotel();
        for (DocumentSnapshot doc: result){
            res.setId(doc.getId());
            res.setId_contacto(doc.getString("idContacto"));
            res.setFechaInicio(doc.getDate("fechaInicio"));
            res.setFechaFin(doc.getDate("fechaFin"));
            res.setPrecio(doc.getDouble("precio"));
            res.setNoches(doc.getDouble("noches"));
            res.setCoste(doc.getDouble("coste"));
            res.setPagado(doc.getBoolean("pagado"));
            reservass.add(res);
        }
    }

    @Override
    public void onBackPressed() {

        Intent menu = new Intent(getApplicationContext(), ContactosActivity.class);
        menu.putExtra("VIENE", "formulario_activity");
        startActivity(menu);

    }



}
