package com.example.fede.animalarium;

import android.net.Uri;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by fede on 2/12/18.
 */

public class ContactoS implements Serializable {

    private String  _id,foto, mascota,raza, tamaño,telefono1,telefono2,propietario;
    private Contacto contacto;



    public ContactoS(String _id, String foto, String mascota, String raza, String tamaño, String telefono1, String telefono2, String propietario) {
        this._id = _id;
        this.foto = foto;
        this.mascota = mascota;
        this.raza = raza;
        this.tamaño = tamaño;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.propietario = propietario;
    }

    public ContactoS(){

    }

    public Contacto getContacto(ContactoS contactoS, Uri foto){
        Log.e("id", contactoS.get_id().toString());
        Log.e("uri",foto.toString());
        contacto = new Contacto(
                contactoS.get_id(),
                foto,
                contactoS.getMascota(),
                contactoS.getRaza(),
                contactoS.getTamaño(),
                contactoS.getTelefono1(),
                contactoS.getTelefono2(),
                contactoS.getPropietario()
        );

        return contacto;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getMascota() {
        return mascota;
    }

    public void setMascota(String mascota) {
        this.mascota = mascota;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getTamaño() {
        return tamaño;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "_id='" + _id + '\'' +
                ", foto=" + foto +'\'' +
                ", mascota='" + mascota + '\'' +
                ", raza='" + raza + '\'' +
                ", tamaño='" + tamaño + '\'' +
                ", telefono1='" + telefono1 + '\'' +
                ", telefono2='" + telefono2 + '\'' +
                ", propietario='" + propietario + '\'' +

                '}';
    }
}
