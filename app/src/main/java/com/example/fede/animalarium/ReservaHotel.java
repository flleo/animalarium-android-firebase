package com.example.fede.animalarium;

import java.util.Date;

public class ReservaHotel {

    private String id, id_contacto;
    private Date fechaInicio, fechaFin;
    private Number precio, noches, coste;
    private Boolean pagado;

    public ReservaHotel(String id, String id_contacto, Date fechaInicio, Date fechaFin, Number precio, Number noches, Number coste, Boolean pagado) {

        this.id = id;
        this.id_contacto = id_contacto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precio = precio;
        this.noches = noches;
        this.coste = coste;
        this.pagado = pagado;
    }

    public ReservaHotel() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_contacto() {
        return id_contacto;
    }

    public void setId_contacto(String id_contacto) {
        this.id_contacto = id_contacto;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Number getPrecio() {
        return precio;
    }

    public void setPrecio(Number precio) {
        this.precio = precio;
    }

    public Number getNoches() {
        return noches;
    }

    public void setNoches(Number noches) {
        this.noches = noches;
    }

    public Number getCoste() {
        return coste;
    }

    public void setCoste(Number coste) {
        this.coste = coste;
    }

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
    }

    @Override
    public String toString() {
        return "ReservaHotel{" +
                "id='" + id + '\'' +
                ", id_contacto='" + id_contacto + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", precio=" + precio +
                ", noches=" + noches +
                ", coste=" + coste +
                ", pagado=" + pagado +
                '}';
    }
}


