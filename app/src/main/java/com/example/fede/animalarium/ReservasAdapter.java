package com.example.fede.animalarium;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class ReservasAdapter extends BaseAdapter {


    private ArrayList<ReservaHotel> reservas;
    private ArrayList<Contacto> contactos;
    private Context context;
    String fecha = "";
    Date fechaID,fechaFD;



    public ReservasAdapter(HotelActivity hotelActivity, ArrayList<ReservaHotel> reservas, ArrayList<Contacto> contactos) {

        this.context = hotelActivity;
        this.reservas = reservas;
        this.contactos = contactos;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_hotel, null);

        TextView mascota =  convertView.findViewById(R.id.mascota_list_hotel);
        TextView raza = (TextView) convertView.findViewById(R.id.raza_list_hotel);
        TextView fechaInicio = convertView.findViewById(R.id.fecha_inicio_list_hotel);
        TextView fechafin = convertView.findViewById(R.id.fecha_fin_list_hotel);
        TextView noches = convertView.findViewById(R.id.noches_list_hotel);
        TextView coste = convertView.findViewById(R.id.coste_list_hotel);
        TextView pagado = convertView.findViewById(R.id.pagado_list_hotel);

        ReservaHotel reservaHotel = (ReservaHotel) getItem(position);
        Contacto contacto =  contactos.get(position);

        mascota.setText(contacto.getMascota());
        raza.setText(contacto.getRaza());

        fechaID = reservaHotel.getFechaInicio();
        fechaFD = reservaHotel.getFechaFin();

        fechaInicio.setText(fechaID.getDate()+"-"+(fechaID.getMonth()+1)+"-"+ fechaID.getYear());
        fechafin.setText(fechaFD.getDate()+"-"+(fechaFD.getMonth()+1)+"-"+fechaFD.getYear());
        noches.setText(String.valueOf(reservaHotel.getNoches().intValue()));
        coste.setText(String.valueOf(reservaHotel.getCoste()));
        pagado.setText(String.valueOf(reservaHotel.getPagado()));


        return convertView;

    }


    @Override
    public int getCount() {
        return reservas.size();
    }

    @Override
    public Object getItem(int position) {
        return reservas.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}
