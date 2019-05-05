package com.example.fede.animalarium;

import java.util.List;

public class ComunicadorReserva {



    private  static ReservaHotel reserva = null;
    static List<ReservaHotel> reservas;


    public static ReservaHotel getReserva() {
        return reserva;
    }

    public static void setReserva(ReservaHotel reserva) {
        ComunicadorReserva.reserva = reserva;
    }

    public static List<ReservaHotel> getReservas() {
        return reservas;
    }

    public static void setReservas(List<ReservaHotel> reservas) {
        ComunicadorReserva.reservas = reservas;
    }
}
