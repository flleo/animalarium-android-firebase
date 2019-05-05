package com.example.fede.animalarium;


import android.net.Uri;

import java.util.List;

/**
 * Created by fede on 3/6/18.
 */

class ComunicadorContacto {

     static Contacto contacto = null;
     static List<Contacto> contactos;
     static List<Uri> uris;



     static void setContacto(Contacto newObjeto) {
         contacto =  newObjeto;
     }

     static Contacto getContacto() {
        return contacto;
    }

     static List<Contacto> getContactos() {
        return contactos;
    }

     static void setContactos(List<Contacto> contactos) {
        ComunicadorContacto.contactos = contactos;
     }

    public static List<Uri> getUris() {
        return uris;
    }

    public static void setUris(List<Uri> uris) {
        ComunicadorContacto.uris = uris;
    }

    public static void addContacto(Contacto contacto) {
         contactos.add(contacto);
    }
}