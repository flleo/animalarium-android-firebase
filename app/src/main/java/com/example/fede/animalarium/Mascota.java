package com.example.fede.animalarium;

import com.google.firebase.firestore.DocumentReference;

public class Mascota {
    private String  _id,foto, mascota,raza, tamaño;
    DocumentReference _idPropietario;

    public Mascota() {
    }

    public Mascota(String _id, DocumentReference _idPropietario,String foto, String mascota, String raza, String tamaño) {
        this._id = _id;
        this._idPropietario = _idPropietario;
        this.foto = foto;
        this.mascota = mascota;
        this.raza = raza;
        this.tamaño = tamaño;

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

    public DocumentReference get_idPropietario() {
        return _idPropietario;
    }

    public void set_idPropietario(DocumentReference _idPropietario) {
        this._idPropietario = _idPropietario;
    }

    @Override
    public String toString() {
        return "Mascota{" +
                "_id='" + _id + '\'' +
                ", foto='" + foto + '\'' +
                ", mascota='" + mascota + '\'' +
                ", raza='" + raza + '\'' +
                ", tamaño='" + tamaño + '\'' +
                ", _idPropietario=" + _idPropietario +
                '}';
    }
}
