package com.example.fede.animalarium;


import java.util.List;

/**;
 * Created by fede on 3/6/18.
 */

class ComunicadorCita {

    private  static Object objeto = null;
    private static List<CitaPeluqueria> susCitas = null;


    public  static void setObjeto(Object newObjeto) {
        objeto = newObjeto;
    }

    public  static Object getObjeto() {
        return objeto;
    }

    public static List<CitaPeluqueria> getSusCitas() {
        return susCitas;
    }

    public static void setSusCitas(List<CitaPeluqueria> susCitas) {
        ComunicadorCita.susCitas = susCitas;
    }
}