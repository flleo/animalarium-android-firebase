package com.example.fede.animalarium;

public class PropietarioS {
    String id,foto,propietario,telefono1,telefono2,email;

    public PropietarioS() {
    }

    public PropietarioS(String id, String foto, String propietario, String telefono1, String telefono2, String email) {
        this.id = id;
        this.foto = foto;
        this.propietario = propietario;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
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

    @Override
    public String toString() {
        return "PropietarioS{" +
                "id='" + id + '\'' +
                ", foto='" + foto + '\'' +
                ", propietario='" + propietario + '\'' +
                ", telefono1='" + telefono1 + '\'' +
                ", telefono2='" + telefono2 + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
