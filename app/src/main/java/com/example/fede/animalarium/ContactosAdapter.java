package com.example.fede.animalarium;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fede on 2/14/18.
 */

public class ContactosAdapter extends BaseAdapter {


    List<Contacto> listItems;
    private Context context;



    public ContactosAdapter(Context context, List<Contacto> listItems) {
        this.context = context;
        this.listItems = listItems;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_item, null);

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView2);
        TextView mascota = (TextView) convertView.findViewById(R.id.mascota);
        TextView raza = (TextView) convertView.findViewById(R.id.raza);
        TextView tamaño = (TextView) convertView.findViewById(R.id.tamaño);
        TextView telefono1 = (TextView) convertView.findViewById(R.id.telefono1);
        TextView telefono2 = (TextView) convertView.findViewById(R.id.telefono2);
        TextView propietario = (TextView) convertView.findViewById(R.id.propietario);


            Contacto contacto = (Contacto) getItem(position);

            image.setImageURI(contacto.getFoto());
            mascota.setText(contacto.getMascota());
            raza.setText(contacto.getRaza());
            tamaño.setText(contacto.getTamaño());
            telefono1.setText(contacto.getTelefono1());
            telefono2.setText(contacto.getTelefono2());
            propietario.setText(contacto.getPropietario());


        //Log.e("contactosAdptaer_contacto",contacto.toString());

        return convertView;
    }



    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        try {
            return listItems.get(position);
        }catch (IndexOutOfBoundsException e){

            return null;}
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }



}

