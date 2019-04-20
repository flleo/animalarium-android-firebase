package com.example.fede.animalarium;


import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fede on 3/6/18.
 */

class ComunicadorContacto {

    public static Object objeto = null;
    public static List<Contacto> objects = null;



    public static void setObjeto(Object newObjeto) {
        objeto = newObjeto;
    }

    public static Object getObjeto() {
        return objeto;
    }

    public static List<Contacto> getObjects() {
        return objects;
    }

    public static void setObjects(ArrayList<Contacto> objects) {
        ComunicadorContacto.objects = objects;
    }
}