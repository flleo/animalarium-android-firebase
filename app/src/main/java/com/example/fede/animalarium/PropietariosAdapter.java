package com.example.fede.animalarium;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class PropietariosAdapter extends BaseAdapter {


    private ArrayList<Propietario> listItems;
    private Context context;
    public TextView mascota, raza, tamaño;

    public PropietariosAdapter(Context context, ArrayList<Propietario> propietarios) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        return null;
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
