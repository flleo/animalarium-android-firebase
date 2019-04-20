package com.example.fede.animalarium;

public class ComunicadorReserva {



    private  static Object objeto = null;

    public  static void setObjeto(Object newObjeto) {
        objeto = newObjeto;
    }

    public  static Object getObjeto() {
        return objeto;
    }

}
