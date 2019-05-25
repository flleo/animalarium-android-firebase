package com.example.fede.animalarium;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.Date;

public class CitaPeluqueria implements Serializable {

    String _id;
    String _idContacto;
    DocumentReference idContacto;
    Date fecha;
    String trabajo;
    Number tarifa;

    public CitaPeluqueria() {
    }

    public CitaPeluqueria (String _id, String _idContacto, Date fecha, String trabajo, Number tarifa) {

        this._id  = _id;
        this._idContacto = _idContacto;
        this.fecha = fecha;
        this.trabajo = trabajo;
        this.tarifa = tarifa;

    }

    public CitaPeluqueria (String _id, DocumentReference idContacto, Date fecha, String trabajo, Number tarifa) {

        this._id  = _id;
        this.idContacto = idContacto;
        this.fecha = fecha;
        this.trabajo = trabajo;
        this.tarifa = tarifa;

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_idContacto() {
        return _idContacto;
    }

    public void set_idContacto(String _idContacto) {
        this._idContacto = _idContacto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(String trabajo) {
        this.trabajo = trabajo;
    }

    public Number getTarifa() {
        return tarifa;
    }

    public void setTarifa(Number tarifa) {
        this.tarifa = tarifa;
    }

    public DocumentReference getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(DocumentReference idContacto) {
        this.idContacto = idContacto;
    }

    @Override
    public String toString() {
        return "CitaPeluqueria{" +
                "_id='" + _id + '\'' +
                ", _idContacto='" + _idContacto + '\'' +
                ", idContacto=" + idContacto +
                ", fecha=" + fecha +
                ", trabajo='" + trabajo + '\'' +
                ", tarifa=" + tarifa +
                '}';
    }
}
