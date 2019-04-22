package com.example.fede.animalarium;


import android.net.Uri;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fede on 3/6/18.
 */

class ComunicadorContacto {

     static Object objeto = null;
     static List<Contacto> objects;
     static List<Uri> uris;



     static void setObjeto(Object newObjeto) {
        objeto = newObjeto;
    }

     static Object getObjeto() {
        return objeto;
    }

     static List<Contacto> getObjects() {
        return objects;
    }

     static void setObjects(List<Contacto> objects) {
        ComunicadorContacto.objects = objects;
     }

    public static List<Uri> getUris() {
        return uris;
    }

    public static void setUris(List<Uri> uris) {
        ComunicadorContacto.uris = uris;
    }

    public static void addContacto(Contacto contacto) {
         objects.add(contacto);
    }
}