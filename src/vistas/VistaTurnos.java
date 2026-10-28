package vistas;

import controlador.ControladorTurnos;
import modelo.Mascota;
import modelo.TipoAtencion;
import modelo.Turno;
import modelo.Veterinario;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VistaTurnos {
    private final ControladorTurnos controlador;

    public VistaTurnos(ControladorTurnos controlador) {
        this.controlador = controlador;
    }

    public void asignarTurno(
            Mascota mascota,
            Veterinario veterinario,
            TipoAtencion tipo,
            LocalDateTime fechaHora) {
        mostrarResultado(
                "asignación de turno",
                controlador.asignarTurno(mascota, veterinario, tipo, fechaHora));
    }

    public void reprogramarTurno(Turno turno, LocalDateTime nuevaFechaHora) {
        mostrarResultado(
                "reprogramación de turno",
                controlador.reprogramarTurno(turno, nuevaFechaHora));
    }

    public void cancelarTurno(Turno turno, LocalDateTime ahora) {
        mostrarResultado(
                "cancelación de turno",
                controlador.cancelarTurno(turno, ahora));
    }

    public void registrarAusencia(Turno turno) {
        mostrarResultado(
                "registro de ausencia",
                controlador.registrarAusencia(turno));
    }

    public void atenderUrgencia(Mascota mascota, LocalDate dia) {
        mostrarResultado(
                "atención de urgencia",
                controlador.atenderUrgencia(mascota, dia));
    }

    private void mostrarResultado(String operacion, boolean correcto) {
        if (correcto) {
            System.out.println("La vista ejecutó correctamente la operación: " + operacion + ".");
        } else {
            System.out.println("La vista no pudo ejecutar la operación: " + operacion + ".");
        }
    }
}
