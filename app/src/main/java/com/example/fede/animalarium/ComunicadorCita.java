package com.example.fede.animalarium;


import java.util.ArrayList;
import java.util.List;

/**;
 * Created by fede on 3/6/18.
 */

class ComunicadorCita {

    private  static Object objeto = null;
    private static ArrayList<CitaPeluqueria> susCitas = new ArrayList<>();


    public  static void setObjeto(Object newObjeto) {
        objeto = newObjeto;
    }

    public  static Object getObjeto() {
        return objeto;
    }

    public static ArrayList<CitaPeluqueria> getSusCitas() {
        return susCitas;
    }

    public static void setSusCitas(ArrayList<CitaPeluqueria> susCitas) {
        ComunicadorCita.susCitas = susCitas;
    }
}