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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
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
    DocumentReference contactoRef = db.collection("contactos").document();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;

    //

    public Contacto contacto;
    ContactoS contactoS;

    private static EditText mascota, raza, telefono1, telefono2, propietario;
    private Spinner tamaño;
    private static ImageButton foto;
    Button añadir,actualizar,eliminar;
    private static final int PICK_IMAGE = 1;
    private Uri selectedImageUri, oldSelectedImageUri, imageUri;
    Context context;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String num_llamar, viene = "";
    int _id;
    private String fotoS = null;
    DocSnippets docSnippets;
    private List<Contacto> contactos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);


        //
        context = this;
        docSnippets = new DocSnippets(db, (FormularioActivity) context);
        //Inicializamos
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
        imageUri = getImageUri(icon);
        selectedImageUri = imageUri;
        foto = (ImageButton) findViewById(R.id.imageButtonFormulario);
        mascota = (EditText) findViewById(R.id.mascota);
        raza = (EditText) findViewById(R.id.raza);
        telefono1 = (EditText) findViewById(R.id.telefon1);
        telefono2 = (EditText) findViewById(R.id.telefon2);
        propietario = (EditText) findViewById(R.id.propietario);

        //Inicializamos el spinner de tamaños
        tamaño = (Spinner) findViewById(R.id.spinner);
        /*ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource(this, R.array.tamaños, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tamaño.setAdapter(spinner_adapter);*/
        añadir = (Button) findViewById(R.id.formulario_activity_añadir_button);
        actualizar = findViewById(R.id.formulario_activity_actualizar_button);
        eliminar = findViewById(R.id.formulario_activity_eliminar_button);


        contacto = (Contacto) ComunicadorContacto.getObjeto();
        try {
            viene = getIntent().getExtras().getString("VIENE");
            switch (viene) {
                case "main_activity":
                    añadir.setEnabled(false);
                    oldSelectedImageUri = contacto.getFoto();
                    selectedImageUri = oldSelectedImageUri;
                    break;
                case "añadirContacto":
                    añadir.setEnabled(true);
                    actualizar.setEnabled(false);
                    eliminar.setEnabled(false);
                    break;
                default:
                    añadir.setEnabled(false);
                    break;
            }



        } catch (NullPointerException e) {
           // imageUri = oldSelectedImageUri;
            añadir.setEnabled(true);
        }


        bindeaContactoView(contacto);


        //Pulsación larga -> Eliminamos la foto
        foto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.dialog_deseas_eliminarFoto);
                builder.setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        foto.setImageURI(imageUri);
                        selectedImageUri = imageUri;


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

    public void seleccionaImagen(View view) {

        openGallery();
    }


    private void openGallery() {
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

        //ComunicadorContacto.setObjeto(contacto);
        Intent intent = new Intent(this, PeluqueriasContactoActivity.class);
        intent.putExtra("VIENE", viene);
        startActivity(intent);


    }

    public void hotel(View view) {
        ComunicadorContacto.setObjeto(contacto);
        Intent intent = new Intent(this, HotelContactoActivity.class);
        startActivity(intent);
    }

    // Termina llamada telefonica


    //Añadir contacto

    public void añadir(View view) {

        if(!comprobarAñadir()) {
            //Con foto
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
        for (Uri u : ComunicadorContacto.getUris()) {
            if (selectedImageUri.compareTo(u) == 1) {
                ContactoS contactoS = new ContactoS(null, "46841", mascota.getText().toString(), raza.getText().toString(), tamaño.getSelectedItem().toString(), telefono1.getText().toString(), telefono2.getText().toString(), propietario.getText().toString());
                añadirContacto1(contactoS);
                añadido = true;
            }
        }
        return  añadido;
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
        contactoRef.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FormularioActivity.this, "Contacto añadido", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), ContactosActivity.class);
//                        startActivity(intent);

                        //Recpgemos el contacto con su uri y lo añadimos al comunicador
                        docSnippets.getContactoConId(contactoRef.getId());


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
        ComunicadorContacto.setObjeto(contacto);
        ComunicadorContacto.addContacto(contacto);

    }

    public void actualizarContacto1(final ContactoS contactoS) {

        DocumentReference contact = db.collection("contactos").document(contactoS.get_id());

        if (contactoS.getFoto() != null) {
            contact.update(KEY_FOTO, contactoS.getFoto());
            Log.e("contacto.getFoto()_actualizaContacto1!=null", contactoS.getFoto());
        } else {
            Log.e("contacto.getFoto()_actualizaContacto1==null", contactoS.getFoto());
        }
        contact.update(KEY_MASCOTA, contactoS.getMascota());
        contact.update(KEY_RAZA, contactoS.getRaza());
        contact.update(KEY_TAMAÑO, contactoS.getTamaño());
        contact.update(KEY_TELEFONO1, contactoS.getTelefono1());
        contact.update(KEY_TELEFONO2, contactoS.getTelefono2());
        contact.update(KEY_PROPIETARIO, contactoS.getPropietario())

                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {

                        Log.e("contacto_actualizado_ mascota:", contactoS.getMascota());
                        actualizaComunicadorContacto();

                    }

                });

    }

    private void actualizaComunicadorContacto() {
        contactos = ComunicadorContacto.getObjects();
        for (Contacto con:contactos) {
            if(con.get_id().equalsIgnoreCase(contacto.get_id())){
                con.setFoto(contacto.getFoto());
                con.setMascota(contacto.getMascota());
                con.setPropietario(contacto.getPropietario());
                con.setRaza(contacto.getRaza());
                con.setTamaño(contacto.getTamaño());
                con.setTelefono1(contacto.getTelefono1());
                con.setTelefono2(contacto.getTelefono2());
            }
        }
        ComunicadorContacto.setObjects(contactos);
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
                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "Contacto eliminado, con éxito", Toast.LENGTH_SHORT);
                            toast1.show();
                            eliminaComunicadorContacto(contacto);
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
                    Intent intent = new Intent(getApplicationContext(), ContactosActivity.class);
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
            Log.e("No se pudo eliminar ", "index=" + Integer.valueOf(contacto.get_id()));

        }
    }

    private void eliminaComunicadorContacto(Contacto contacto) {
        contactos = ComunicadorContacto.getObjects();
        for (Contacto con: contactos) {
            if(con.get_id().equalsIgnoreCase(contacto.get_id())) contactos.remove(con);
        }
        ComunicadorContacto.setObjects(contactos);
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


    @Override
    public void onBackPressed() {

        Intent menu = new Intent(getApplicationContext(), ContactosActivity.class);
        startActivity(menu);

    }


}