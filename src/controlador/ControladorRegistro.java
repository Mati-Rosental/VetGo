package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
import java.time.LocalDate;

public class ControladorRegistro {
    private Repositorio<Duenio> repositorioDuenios;
    private Clinica clinica;

    public void registrarDuenio(Duenio duenio) {
        System.out.println("ControladorRegistro.registrarDuenio");
    }

    public void registrarMascota(Duenio duenio, Mascota mascota) {
        System.out.println("ControladorRegistro.registrarMascota");
    }

    public void transferirMascota(Mascota mascota, Duenio nuevoDuenio, LocalDate fecha) {
        System.out.println("ControladorRegistro.transferirMascota");
    }
}
