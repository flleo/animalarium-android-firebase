package com.example.fede.animalarium;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class PropietariosAdapter extends BaseAdapter {
    //Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Referencia del almacenamiento de archivos en Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    DocSnippets docSnippets ;
    //

    private ArrayList<Propietario> listItems;
    ArrayList<String> mascotas = new ArrayList<>();
    private Context context;
    public TextView mascota, raza, tamaño;
    private TextView mascotasTV;


    public PropietariosAdapter(Context context, ArrayList<Propietario> propietarios) {

        this.context = context;
        this.listItems = propietarios;
        docSnippets = new DocSnippets(db, this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_propietario, null);

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView_list_propietario);
        TextView propietario = (TextView) convertView.findViewById(R.id.propietario_list_propietario);
        TextView email = (TextView) convertView.findViewById(R.id.email_list_propietario);
        TextView telefono1 = (TextView) convertView.findViewById(R.id.telefono1_list_propietario);
        TextView telefono2 = (TextView) convertView.findViewById(R.id.telefono2_list_propietario);
        mascotasTV = (TextView) convertView.findViewById(R.id.mascotas_list_propietario);

        Propietario pro = (Propietario) getItem(position);
        docSnippets.getMascotasPorPropietario(pro);

        image.setImageURI(pro.getFoto());
        propietario.setText(pro.getPropietario());
        telefono1.setText(pro.getTelefono1());
        telefono2.setText(pro.getTelefono2());
        email.setText(pro.getEmail());

        Log.e("propietarios",pro.toString());

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
        }catch (IndexOutOfBoundsException e){return null;}
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }



    public void setMascotas(QuerySnapshot result) {

        for (DocumentSnapshot doc:result) {
            mascotas.add(doc.getString("propietario"));
        }
        if (mascotas.size()==result.size()) mascotasTV.setText(mascotas.toString());
        Log.e("mascotas",mascotas.toString());
    }


}
