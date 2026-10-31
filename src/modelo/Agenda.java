package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Agenda {
    private final List<Turno> turnos = new ArrayList<>();

    public boolean asignarTurno(
            Mascota mascota,
            Veterinario veterinario,
            TipoAtencion tipo,
            LocalDateTime fechaHora) {
        System.out.println("Agenda.asignarTurno");
        return false;
    }

    public boolean reprogramar(Turno turno, LocalDateTime nuevaFechaHora) {
        System.out.println("Agenda.reprogramar");
        return false;
    }

    public boolean cancelar(Turno turno) {
        // True si hay cargo por cancelacion tardia
        System.out.println("Agenda.cancelar");
        return false;
    }

    public void registrarAusencia(Turno turno) {
        System.out.println("Agenda.registrarAusencia");
    }

    public boolean hayDisponibilidad(Veterinario veterinario, LocalDateTime inicio, int minutos) {
        System.out.println("Agenda.hayDisponibilidad");
        return false;
    }

    public List<Turno> getTurnosDe(Veterinario veterinario, LocalDate desde, LocalDate hasta) {
        System.out.println("Agenda.getTurnosDe");
        return null;
    }

    public Turno getTurnoSiguiente(Veterinario veterinario, LocalDateTime despuesDe) {
        System.out.println("Agenda.getTurnoSiguiente");
        return null;
    }

    public LocalDateTime primerHorarioLibre(Veterinario veterinario, LocalDateTime desde, int minutos) {
        System.out.println("Agenda.primerHorarioLibre");
        return null;
    }

    public Turno buscarTurno(String idTurno) {
        System.out.println("Agenda.buscarTurno");
        return null;
    }
}
