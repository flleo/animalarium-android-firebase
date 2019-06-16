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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class FormularioPropietarioActivity extends AppCompatActivity {
    //Firesbase
    private static final String TAG = "FormularioPropietarioActivity";
    private static final String KEY_FOTO = "foto";
    private static final String KEY_PROPIETARIO = "propietario";
    private static final String KEY_TELEFONO1 = "telefono1";
    private static final String KEY_TELEFONO2 = "telefono2";
    private static final String KEY_EMAIL = "email";
    private static final int PICK_IMAGE = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference propietariosRef = db.collection("propietarios").document();
    DocumentReference citasRef = db.collection("citas").document();
    DocumentReference reservasRef = db.collection("hoteles").document();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;

    //

    ImageButton imageButton ;
    Button mascotas,phone1,phone2,añadir,actualizar,eliminar;
    EditText nombre,telefono1,telefono2,email;
    private Propietario propietario;
    ArrayList<Propietario> propietarios = new ArrayList<>();
    private Context context;
    private Uri imageUri=null,selectedImageUri,oldSelectedImageUri;
    ArrayList<Uri> uris = new ArrayList<>();
    private DocSnippets docSnippets;
    ProgressDialog progressDialog;
    private String fotoS;
    ArrayList<String>fotos = new ArrayList<>();
    private PropietarioS propietarioS;
    private String num_llamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_propietario);

        context = this;
        docSnippets = new DocSnippets(db, this);
        progressDialog = new ProgressDialog(this);
        propietarios = ComunicadorPropietario.getPropietarios();

        //view
        imageButton = findViewById(R.id.formulario_propietario_imageButton);
        mascotas = findViewById(R.id.formulario_propietario_susMascotas_button);
        nombre = findViewById(R.id.formulario_propietario_nombre);
        telefono1 = findViewById(R.id.formulario_propietario_telefono1);
        telefono2 = findViewById(R.id.formulario_propietario_telefono2);
        email = findViewById(R.id.formulario_propietario_email);
        phone1 = findViewById(R.id.formulario_propietario_phone1);
        phone2 = findViewById(R.id.formulario_propietario_phone2);
        añadir = findViewById(R.id.formulario_propietario_añadir_button);
        actualizar = findViewById(R.id.formulario_propietario_actualizar_button);
        eliminar = findViewById(R.id.formulario_propietario_eliminar_button);
        //
        propietario = ComunicadorPropietario.getPropietario();
        if(propietario!=null) {
            bindeaPropietarioAVista();
            docSnippets.getMascotasPorPropietario(propietario);
            añadir.setEnabled(false);
            oldSelectedImageUri = propietario.getFoto();
            selectedImageUri = oldSelectedImageUri;
        } else {
            añadir.setEnabled(true);
            actualizar.setEnabled(false);
            eliminar.setEnabled(false);
            mascotas.setEnabled(false);
            propietario = new Propietario();

        }
        uris = ComunicadorPropietario.getUris();
        fotos = ComunicadorPropietario.getFotos();



        if (uris.size()==0){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.propietario);
            Uri uri = getImageUri(bitmap);
            imageUri = uri;
            selectedImageUri = uri;
        } else
        if (uris.size()!=0){
            imageUri = uris.get(0);             //definimos la por defecto
            selectedImageUri = uris.get(0);
            fotoS = fotos.get(0);
        }

        //imageButton.setImageURI(imageUri);
        //Pulsación larga -> Eliminamos la foto
        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.dialog_deseas_eliminarFoto);
                builder.setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        imageButton.setImageURI(imageUri);
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



    public void añadir(View view) {

        bindeaVistaAPropietario();
        if (!cargarFoto()) {
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
                                Toast.makeText(FormularioPropietarioActivity.this, "Foto subida con éxito", Toast.LENGTH_SHORT).show();
                                fotoS  = storageReference.getName();
                                propietarioS = new PropietarioS(null, fotoS,nombre.getText().toString(), telefono1.getText().toString(),telefono2.getText().toString(), email.getText().toString());
                                añadirPropietario1();
                                añadir.setEnabled(false);
                                actualizar.setEnabled(true);
                                eliminar.setEnabled(true);
                                mascotas.setEnabled(true);
                                // Log.e("foto path",storageReference.getPath());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Fallo al subir la foto" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                e.getMessage();
            }
        }
    }

    private void añadirPropietario1() {

        //Firebase
        propietariosRef.set(mapeaPropietarioS())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Propietario añadido", Toast.LENGTH_SHORT).show();
                        Log.e("ID", propietariosRef.getId());
                        propietarioS.setId(propietariosRef.getId());
                        bindeaPropietarioS(propietarioS);
                        ComunicadorPropietario.setPropietario(propietario);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        });


    }

    private void  bindeaPropietarioS(PropietarioS propietarioS) {
        propietario = new Propietario(
          propietarioS.getId(),
          selectedImageUri,
          propietarioS.getPropietario(),
          propietarioS.getTelefono1(),
          propietarioS.getTelefono2(),
          propietarioS.getEmail()
        );

    }

    private Map<String, Object> mapeaPropietarioS() {
        Map<String, Object> map = new HashMap<>();

        map.put(KEY_FOTO, propietarioS.getFoto());
        map.put(KEY_PROPIETARIO,  propietarioS.getPropietario());
        map.put(KEY_TELEFONO1 , propietarioS.getTelefono1());
        map.put(KEY_TELEFONO2  ,propietarioS.getTelefono2());
        map.put(KEY_EMAIL, propietarioS.getEmail());

        return map;
    }


    public boolean cargarFoto() {

        boolean cargada = false;
        //fotoS = null;
        int i;
        for (i = 0; i < uris.size(); i++)
            if (uris.get(i) == selectedImageUri) {
                fotoS = fotos.get(i);
                propietarioS.setFoto(fotoS);
                añadirPropietario1();
                cargada = true;
            }
        return cargada;

    }


    public void actualizarPropietario(View view) {

        bindeaVistaAPropietario();

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
                                Toast.makeText(FormularioPropietarioActivity.this, "Foto subida con éxito", Toast.LENGTH_SHORT).show();
                                fotoS = storageReference.getName();
                                actualizarPropietarioFirebasse();
                                oldSelectedImageUri = selectedImageUri;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Fallo al subir la foto" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                actualizarPropietarioFirebasse();
            }
        } catch (NullPointerException e) {

            actualizarPropietarioFirebasse();
        }


    }

    private void actualizarPropietarioFirebasse() {
        progressDialog.setTitle("...actualizando contacto...");
        progressDialog.show();
        DocumentReference contact = db.collection("propietarios").document(propietario.getId());
        contact.update(KEY_FOTO, fotoS);
        contact.update(KEY_PROPIETARIO, propietario.getPropietario());
        contact.update(KEY_TELEFONO1, propietario.getTelefono1());
        contact.update(KEY_TELEFONO2, propietario.getTelefono2());
        contact.update(KEY_EMAIL,propietario.getEmail())

                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(), "Propietarios actualizado, con éxito", Toast.LENGTH_SHORT).show();

                        actualizaComunicadorPropietario();

                    }

                });
    }

    private void actualizaComunicadorPropietario() {
        propietarios.remove(propietario);
        añadimosPropietarioAlComunicador();
    }

    private void añadimosPropietarioAlComunicador() {

        propietarios.add(propietario);
        //ordenamos los contactos
        Collections.sort(propietarios, new Comparator<Propietario>() {
            @Override
            public int compare(Propietario o1, Propietario o2) {
                return o1.getPropietario().compareToIgnoreCase(o2.getPropietario());
            }

        });
        //
        ComunicadorPropietario.setPropietarios(propietarios);
        ComunicadorPropietario.setPropietario(propietario);

        añadir.setEnabled(false);
        actualizar.setEnabled(true);
        eliminar.setEnabled(true);
        mascotas.setEnabled(true);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void eliminarContacto(View view) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_seguro_eliminar_propietario);
            builder.setPositiveButton(R.string.seguro, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.collection("propietarios").document(propietario.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(), "Propietario eliminado, con éxito", Toast.LENGTH_SHORT).show();
                            eliminaPropietarioDelComunicador();
                            intent();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toast1 =
                                            Toast.makeText(getApplicationContext(),
                                                    "El propietario no se pudo eliminar", Toast.LENGTH_SHORT);
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
            Log.e("No se pudo eliminar ", "index=" + Integer.valueOf(propietario.getId()));

        }
    }


    private void eliminaPropietarioDelComunicador() {
        propietarios.remove(propietario);
    }

    private void bindeaVistaAPropietario() {

        propietario.setPropietario(nombre.getText().toString());
        propietario.setTelefono1(telefono1.getText().toString());
        propietario.setTelefono2(telefono2.getText().toString());
        propietario.setEmail(email.getText().toString());

    }

    private void bindeaPropietarioAVista() {

        imageButton.setImageURI(propietario.getFoto());
        nombre.setText(propietario.getPropietario());
        telefono1.setText(propietario.getTelefono1());
        telefono2.setText(propietario.getTelefono2());
        email.setText(propietario.getEmail());
    }

    public void openGalleryPropietario(View view) {
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
                    try {
                        selectedImageUri = getImageUri(reducirImagen(context, imageReturnedIntent.getData(), 104));
                        imageButton.setImageURI(selectedImageUri);
                        if (selectedImageUri!=null&&propietario!=null);
                        propietario.setFoto(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
                break;
        }


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
                ActivityCompat.requestPermissions(FormularioPropietarioActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
        } else {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null);
            Uri uri = Uri.parse(path);

            return uri;
        }
        return null;
    }

    public void susMascotas(View view) {

        Intent intent = new Intent(context,MascotasPropietarioActivity.class);
        intent.putExtra("VIENE","formulario_propietario_activity");
        startActivity(intent);
    }

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
    private void intent() {
        Intent intent = new Intent(this,PropietariosActivity.class);
        intent.putExtra("VIENE","formulario_propietario_activity");
        startActivity(intent);
    }

}
