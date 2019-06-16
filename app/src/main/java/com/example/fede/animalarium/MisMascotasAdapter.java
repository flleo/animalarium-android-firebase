package com.example.fede.animalarium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

class MisMascotasAdapter extends BaseAdapter {

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets = new DocSnippets(db, this);
    //

    private List<Mascota> listItems;
    private Context context;

    public MisMascotasAdapter(MascotasPropietarioActivity mascotasPropietarioActivity, ArrayList<Mascota> mascotas) {
        listItems = mascotas;
        context = mascotasPropietarioActivity;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_mi_mascota, null);

        ImageView imageView = convertView.findViewById(R.id.activity_list_mi_mascota_imageView);
        TextView mascota = convertView.findViewById(R.id.activity_list_mi_mascota_mascota);
        TextView raza = convertView.findViewById(R.id.activity_list_mi_mascota_raza);
        TextView tamaño = convertView.findViewById(R.id.activity_list_mi_mascota_tamaño);

        return convertView;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
