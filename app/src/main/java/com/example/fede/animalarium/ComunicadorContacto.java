package com.example.fede.animalarium;


import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fede on 3/6/18.
 */

class ComunicadorContacto {

     static Contacto contacto = null;
     static ArrayList<Contacto> contactos = new ArrayList<>();
     static ArrayList<Uri> uris = new ArrayList<>();



     static void setContacto(Contacto newObjeto) {
         contacto =  newObjeto;
     }

     static Contacto getContacto() {
        return contacto;
    }

    public static ArrayList<Contacto> getContactos() {
        return contactos;
    }

    public static void setContactos(ArrayList<Contacto> contactos) {
        ComunicadorContacto.contactos = contactos;
    }

    public static ArrayList<Uri> getUris() {
        return uris;
    }

    public static void setUris(ArrayList<Uri> uris) {
        ComunicadorContacto.uris = uris;
    }
}