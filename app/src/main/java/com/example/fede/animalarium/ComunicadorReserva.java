package com.example.fede.animalarium;

import java.util.ArrayList;
import java.util.List;

public class ComunicadorReserva {



    private  static ReservaHotel reserva = null;
    static ArrayList<ReservaHotel> reservas;


    public static ReservaHotel getReserva() {
        return reserva;
    }

    public static void setReserva(ReservaHotel reserva) {
        ComunicadorReserva.reserva = reserva;
    }

    public static ArrayList<ReservaHotel> getReservas() {
        return reservas;
    }

    public static void setReservas(ArrayList<ReservaHotel> reservas) {
        ComunicadorReserva.reservas = reservas;
    }
}
