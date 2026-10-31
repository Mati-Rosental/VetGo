package controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import modelo.*;
import persistencia.Repositorio;
import servicio.GestorUrgencias;

public class ControladorTurnos {
    private Repositorio<Clinica> clinicaRepositorio;
    private Repositorio<Agenda> agendaRepositorio;
    private Clinica clinica;
    private Agenda agenda;
    private GestorUrgencias gestorUrgencias;
    private ControladorFacturacion controladorFacturacion;

    public ControladorTurnos(Repositorio<Clinica> clinicaRepositorio, Clinica clinica, Repositorio<Agenda> agendaRepositorio, Agenda agenda, 
        GestorUrgencias gestorUrgencias, ControladorFacturacion controladorFacturacion) {
        this.clinicaRepositorio = clinicaRepositorio;
        this.clinica = clinica;
        this.agendaRepositorio = agendaRepositorio;
        this.agenda = agenda;
        this.gestorUrgencias = gestorUrgencias;
        this.controladorFacturacion = controladorFacturacion;
    }

    public boolean asignarTurno(String idMascota, String documentoVeterinario, TipoAtencion tipo, LocalDateTime fechaHora) {
        // Validar los parámetros
        if (idMascota == null || documentoVeterinario == null || tipo == null || fechaHora == null) return false;
        // Buscar la mascota y el veterinario
        Mascota mascota = clinica.buscarMascota(idMascota);
        Veterinario veterinario = clinica.buscarVeterinario(documentoVeterinario);
        if (mascota == null || veterinario == null || !mascota.getDuenio().puedePedirTurno()) return false;
        // Delegar la asignación a Agenda
        if (!agenda.asignarTurno(mascota, veterinario, tipo, fechaHora)) return false;
        // Guardar los cambios
        agendaRepositorio.guardar(List.of(agenda));
        return true;
    }

    public boolean reprogramarTurno(String idTurno, LocalDateTime nuevaFechaHora) {
        // Validar los parámetros
        if (idTurno == null || nuevaFechaHora == null) return false;
        // Buscar el turno en Agenda
        Turno turno = agenda.buscarTurno(idTurno);
        if (turno == null) return false;
        // Delegar la reprogramación a Agenda
        if (!agenda.reprogramar(turno, nuevaFechaHora)) return false;
        // Guardar los cambios
        agendaRepositorio.guardar(List.of(agenda));
        return true;
    }

    public boolean cancelarTurno(String idTurno) {
        // Validar los parámetros
        if (idTurno == null) return false;
        // Buscar el turno en Agenda
        Turno turno = agenda.buscarTurno(idTurno);
        if (turno == null) return false;
        // Delegar la cancelación a Agenda y cobrar si es tardia
        if (agenda.cancelar(turno)) controladorFacturacion.cobrarCargoCancelacionTardia(turno);
        // Guardar los cambios
        agendaRepositorio.guardar(List.of(agenda));
        return true;
    }

    public boolean registrarAusencia(String idTurno) {
        // Validar el parámetro
        if (idTurno == null) return false;
        // Buscar el turno en Agenda
        Turno turno = agenda.buscarTurno(idTurno);
        if (turno == null) return false;
        // Marcar el turno como ausente
        agenda.registrarAusencia(turno);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));
        agendaRepositorio.guardar(List.of(agenda));
        return true;
    }

    public Turno atenderUrgencia(String idMascota) {
        // Validar los parámetros
        if (idMascota == null) return null;
        // Buscar la mascota
        Mascota mascota = clinica.buscarMascota(idMascota);
        if (mascota == null) return null;
        // Obtener el veterinario de guardia
        Veterinario veterinario = clinica.getVeterinarioDeGuardia(LocalDate.now());
        if (veterinario == null) return null;
        // Delegar la atención de la urgencia a GestorUrgencias
        Turno turno = gestorUrgencias.atenderUrgencia(mascota, veterinario);
        // Guardar los cambios
        agendaRepositorio.guardar(List.of(agenda));
        return turno;
    }
}