package com.example.fede.animalarium;

import android.content.Context;
import android.util.Log;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CitasAdapter extends BaseAdapter {


    private ArrayList<CitaPeluqueria> listItems;
    private ArrayList<Contacto> contactos;
    private Context context;
    public TextView mascota, raza, tamaño,fecha,hora;



    public CitasAdapter(Context context, ArrayList<CitaPeluqueria> listItems, ArrayList<Contacto> contactos) {
        this.context = context;
        this.listItems = listItems;
        this.contactos = contactos;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_cita, null);

        fecha = (TextView) convertView.findViewById(R.id.fecha);
        hora = (TextView) convertView.findViewById(R.id.hora);
        mascota = (TextView) convertView.findViewById(R.id.mascota);
        raza = (TextView) convertView.findViewById(R.id.raza);
        tamaño = (TextView) convertView.findViewById(R.id.tamaño);
        TextView trabajo = (TextView) convertView.findViewById(R.id.trabajo);

        CitaPeluqueria citaPeluqueria = (CitaPeluqueria) getItem(position);
        Contacto contacto =  contactos.get(position);


        mascota.setText(contacto.getMascota());
        raza.setText(contacto.getRaza());
        tamaño.setText(contacto.getTamaño());
        fecha.setText(new SimpleDateFormat("dd-MM-yyyy").format(citaPeluqueria.getFecha()));
        hora.setText(new SimpleDateFormat("HH:mm").format(citaPeluqueria.getFecha()));
        trabajo.setText(citaPeluqueria.getTrabajo());



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
