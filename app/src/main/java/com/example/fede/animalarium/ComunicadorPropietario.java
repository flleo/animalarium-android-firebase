package com.example.fede.animalarium;

import android.net.Uri;

import java.util.ArrayList;

class ComunicadorPropietario {

    static Propietario propietario;
    static ArrayList<Propietario> propietarios = new ArrayList<>();
    static ArrayList<Uri> uris = new ArrayList<>();
    static ArrayList<String> fotos = new ArrayList<>();

    public static Propietario getPropietario() {
        return propietario;
    }

    public static void setPropietario(Propietario propietario) {
        ComunicadorPropietario.propietario = propietario;
    }

    public static ArrayList<Propietario> getPropietarios() {
        return propietarios;
    }

    public static void setPropietarios(ArrayList<Propietario> propietarios) {
        ComunicadorPropietario.propietarios = propietarios;
    }

    public static ArrayList<Uri> getUris() {
        return uris;
    }

    public static void setUris(ArrayList<Uri> uris) {
        ComunicadorPropietario.uris = uris;
    }

    public static ArrayList<String> getFotos() {
        return fotos;
    }

    public static void setFotos(ArrayList<String> fotos) {
        ComunicadorPropietario.fotos = fotos;
    }
}
