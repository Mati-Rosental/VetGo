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

    public boolean asignarTurno(
            Mascota mascota,
            Veterinario veterinario,
            TipoAtencion tipo,
            LocalDateTime fechaHora) {
        return true;
    }

    public boolean reprogramarTurno(Turno turno, LocalDateTime nuevaFechaHora) {
        return true;
    }

    public boolean cancelarTurno(Turno turno, LocalDateTime ahora) {
        return true;
    }

    public boolean registrarAusencia(Turno turno) {
        return true;
    }

    public boolean atenderUrgencia(Mascota mascota, LocalDate dia) {
        return true;
    }
}
