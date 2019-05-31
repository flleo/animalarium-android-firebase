package com.example.fede.animalarium;

import android.net.Uri;

import java.util.ArrayList;

class ComunicadorMascota {
    static Mascota mascota = null;
    static ArrayList<Mascota> mascotas = new ArrayList<>();
    static ArrayList<Uri> uris = new ArrayList<>();
    static ArrayList<String> fotos = new ArrayList<>();


    public static Mascota getMascota() {
        return mascota;
    }

    public static void setMascota(Mascota mascota) {
        ComunicadorMascota.mascota = mascota;
    }

    public static ArrayList<Mascota> getMascotas() {
        return mascotas;
    }

    public static void setMascotas(ArrayList<Mascota> mascotas) {
        ComunicadorMascota.mascotas = mascotas;
    }

    public static ArrayList<Uri> getUris() {
        return uris;
    }

    public static void setUris(ArrayList<Uri> uris) {
        ComunicadorMascota.uris = uris;
    }

    public static ArrayList<String> getFotos() {
        return fotos;
    }

    public static void setFotos(ArrayList<String> fotos) {
        ComunicadorMascota.fotos = fotos;
    }
}
