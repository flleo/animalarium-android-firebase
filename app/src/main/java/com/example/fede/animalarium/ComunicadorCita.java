package com.example.fede.animalarium;



/**;
 * Created by fede on 3/6/18.
 */

class ComunicadorCita {

    private  static Object objeto = null;

    public  static void setObjeto(Object newObjeto) {
        objeto = newObjeto;
    }

    public  static Object getObjeto() {
        return objeto;
    }
}