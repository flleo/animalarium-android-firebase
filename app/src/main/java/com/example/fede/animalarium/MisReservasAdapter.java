package com.example.fede.animalarium;

import android.content.Context;
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
import java.util.List;

class MisReservasAdapter extends BaseAdapter {

    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets = new DocSnippets(db, this);
    //

    private List<ReservaHotel> listItems;
    private Context context;

    public MisReservasAdapter(HotelContactoActivity hotelContactoActivity, List<ReservaHotel> reservas) {
        context = hotelContactoActivity;
        listItems = reservas;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_mi_reserva, null);

        TextView fechaInicio = convertView.findViewById(R.id.fecha_inicio_list_hotel);
        TextView fechaFin = convertView.findViewById(R.id.fecha_fin_list_hotel);
        TextView noches = convertView.findViewById(R.id.noches_list_hotel);
        TextView coste = convertView.findViewById(R.id.coste_list_hotel);
        TextView pagado = convertView.findViewById(R.id.pagado_list_hotel);

        ReservaHotel reservaHotel = (ReservaHotel) getItem(position);

        fechaInicio.setText(new SimpleDateFormat("dd-MM-yyyy").format(reservaHotel.getFechaInicio()));
        fechaFin.setText(new SimpleDateFormat("dd-MM-yyyy").format(reservaHotel.getFechaFin()));
        noches.setText(String.valueOf(reservaHotel.getNoches().intValue()));
        coste.setText(String.valueOf(reservaHotel.getCoste()));
        pagado.setText(String.valueOf(reservaHotel.getPagado()));

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
