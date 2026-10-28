package vistas;

import controlador.ControladorRegistro;
import modelo.Duenio;
import modelo.Mascota;
import java.time.LocalDate;

public class VistaRegistro {
    private final ControladorRegistro controlador;

    public VistaRegistro(ControladorRegistro controlador) {
        this.controlador = controlador;
    }

    public void registrarDuenio(Duenio duenio) {
        mostrarResultado(
                "registro de dueño",
                controlador.registrarDuenio(duenio));
    }

    public void registrarMascota(Duenio duenio, Mascota mascota) {
        mostrarResultado(
                "registro de mascota",
                controlador.registrarMascota(duenio, mascota));
    }

    public void transferirMascota(Mascota mascota, Duenio nuevoDuenio, LocalDate fecha) {
        mostrarResultado(
                "transferencia de mascota",
                controlador.transferirMascota(mascota, nuevoDuenio, fecha));
    }

    private void mostrarResultado(String operacion, boolean correcto) {
        if (correcto) {
            System.out.println("La vista ejecutó correctamente la operación: " + operacion + ".");
        } else {
            System.out.println("La vista no pudo ejecutar la operación: " + operacion + ".");
        }
    }
}
