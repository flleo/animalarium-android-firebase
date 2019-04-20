package com.example.fede.animalarium;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HotelContactoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_contacto);
    }


    public void añadir_hotel(View view) {
        Intent formulario = new Intent(getApplicationContext(), FormularioHotelActivity.class);
        startActivity(formulario);
    }
}
