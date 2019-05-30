package com.example.fede.animalarium;

import android.net.Uri;

class Propietario {

    String id, propietario,telefono1,telefono2,email;
    Uri foto;

    public Propietario() {
    }

    public Propietario(String id,Uri foto, String propietario, String telefono1, String telefono2, String email) {
        this.id = id;
        this.propietario = propietario;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.email = email;
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Propietario{" +
                "id='" + id + '\'' +
                ", propietario='" + propietario + '\'' +
                ", telefono1='" + telefono1 + '\'' +
                ", telefono2='" + telefono2 + '\'' +
                ", email='" + email + '\'' +
                ", foto=" + foto +
                '}';
    }
}
