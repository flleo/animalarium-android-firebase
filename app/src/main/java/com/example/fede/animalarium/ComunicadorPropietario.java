package com.example.fede.animalarium;

import java.util.ArrayList;

class ComunicadorPropietario {

    static Propietario propietario;
    static ArrayList<Propietario> propietarios = new ArrayList<>();

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
}
