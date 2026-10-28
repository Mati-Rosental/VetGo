package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
import java.time.LocalDate;

public class ControladorRegistro {
    private Repositorio<Duenio> repositorioDuenios;
    private Clinica clinica;

    public boolean registrarDuenio(Duenio duenio) {
        return true;
    }

    public boolean registrarMascota(Duenio duenio, Mascota mascota) {
        return true;
    }

    public boolean transferirMascota(Mascota mascota, Duenio nuevoDuenio, LocalDate fecha) {
        return true;
    }
}
