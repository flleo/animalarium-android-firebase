package com.example.fede.animalarium;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_propietario);

        context = this;
        docSnippets = new DocSnippets(db, this);
        progressDialog = new ProgressDialog(this);
        propietarios = ComunicadorPropietario.getPropietarios();

        //view
        imageButton = findViewById(R.id.formulario_propietario_imageButtonFormulario);
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

        bindeaPropietarioAPropietario();
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
        fotoS = null;
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

        bindeaPropietarioAPropietario();

      /*  try {

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
*/

    }

    public void eliminarContacto(View view) {
    }

    private void bindeaPropietarioAPropietario() {
        propietario = new Propietario(
                null,
                imageUri,
                nombre.getText().toString(),
                telefono1.getText().toString(),
                telefono2.getText().toString(),
                email.getText().toString()

        );
    }

    private void bindeaPropietarioAVista() {

        imageButton.setImageURI(propietario.getFoto());
        nombre.setText(propietario.getPropietario());
        telefono1.setText(propietario.getTelefono1());
        telefono2.setText(propietario.getTelefono2());
        email.setText(propietario.getEmail());
    }

    private void openGalleryPropietario(View view) {
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
                    imageButton.setImageURI(selectedImageUri);

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
    }

    public void llamada2(View view) {
    }


}
