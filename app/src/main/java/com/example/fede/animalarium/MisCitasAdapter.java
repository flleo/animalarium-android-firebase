package com.example.fede.animalarium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MisCitasAdapter extends BaseAdapter {

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets = new DocSnippets(db, this);
    //

    private List<CitaPeluqueria> listItems;
    private Context context;



    public MisCitasAdapter(Context context, List<CitaPeluqueria> listItems) {
        this.context = context;
        this.listItems = listItems;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_mi_cita, null);

        TextView fecha = (TextView) convertView.findViewById(R.id.fecha);
        TextView hora = (TextView) convertView.findViewById(R.id.hora);
        TextView mitrabajo = (TextView) convertView.findViewById(R.id.mitrabajo);
        TextView mitarifa = (TextView) convertView.findViewById(R.id.mitarifa);


        CitaPeluqueria citaPeluqueria = (CitaPeluqueria) getItem(position);


       // docSnippets.getContactoConId(citaPeluqueria.get_idContacto());

        fecha.setText(new SimpleDateFormat("dd-MM-yyyy").format(citaPeluqueria.getFecha()));
        hora.setText(new SimpleDateFormat("HH:mm").format(citaPeluqueria.getFecha()));
        mitrabajo.setText(citaPeluqueria.getTrabajo());
        mitarifa.setText(String.valueOf(citaPeluqueria.getTarifa()));

        return convertView;
    }


    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }



}
