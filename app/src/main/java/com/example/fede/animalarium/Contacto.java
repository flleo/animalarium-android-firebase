package com.example.fede.animalarium;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by fede on 2/12/18.
 */

public class Contacto  implements Serializable {

    private String  _id,id_propietario,mascota,raza, tamaño,telefono1,telefono2,propietario;
    private Uri foto;
    private ContactoS contactoS;

    public  Contacto(){

    }

    public Contacto(String _id,Uri foto,String mascota, String raza, String tamaño, String telefono1,String telefono2, String propietario) {
        this._id = _id;
        this.foto = foto;
        this.mascota = mascota;
        this.raza = raza;
        this.tamaño = tamaño;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.propietario = propietario;
    }

    public Contacto(String _id, Uri foto,String mascota, String raza, String tamaño, String telefono1, String telefono2, String propietario ,String id_propietario) {
        this._id = _id;
        this.id_propietario = id_propietario;
        this.mascota = mascota;
        this.raza = raza;
        this.tamaño = tamaño;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.propietario = propietario;
        this.foto = foto;
    }

    public ContactoS getContacto1(Contacto contacto, String foto){
        contactoS = new ContactoS(
                contacto.get_id(),
                foto,
                contacto.getMascota(),
                contacto.getRaza(),
                contacto.getTamaño(),
                contacto.getTelefono1(),
                contacto.getTelefono2(),
                contacto.getPropietario()

        );

        return contactoS;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
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

    public String getId_propietario() {
        return id_propietario;
    }

    public void setId_propietario(String id_propietario) {
        this.id_propietario = id_propietario;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "_id='" + _id + '\'' +
                ", id_propietario='" + id_propietario + '\'' +
                ", mascota='" + mascota + '\'' +
                ", raza='" + raza + '\'' +
                ", tamaño='" + tamaño + '\'' +
                ", telefono1='" + telefono1 + '\'' +
                ", telefono2='" + telefono2 + '\'' +
                ", propietario='" + propietario + '\'' +
                ", foto=" + foto +
                '}';
    }
}
