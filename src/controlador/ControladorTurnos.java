package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ControladorTurnos {
    private Repositorio<Turno> repositorioTurnos;
    private Clinica clinica;
    private GestorUrgencias gestorUrgencias;
    private Agenda agenda;
    private Notificador notificador;

    public Turno asignarTurno(
            Mascota mascota,
            Veterinario veterinario,
            TipoAtencion tipo,
            LocalDateTime fechaHora) {
        System.out.println("ControladorTurnos.asignarTurno");
        return null;
    }

    public void reprogramarTurno(Turno turno, LocalDateTime nuevaFechaHora) {
        System.out.println("ControladorTurnos.reprogramarTurno");
    }

    public void cancelarTurno(Turno turno, LocalDateTime ahora) {
        System.out.println("ControladorTurnos.cancelarTurno");
    }

    public void registrarAusencia(Turno turno) {
        System.out.println("ControladorTurnos.registrarAusencia");
    }

    public Turno atenderUrgencia(Mascota mascota, LocalDate dia) {
        System.out.println("ControladorTurnos.atenderUrgencia");
        return null;
    }
}
