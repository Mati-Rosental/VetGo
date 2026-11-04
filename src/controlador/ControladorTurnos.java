package controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;

import modelo.*;
import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.enumeradores.TipoAtencion;
import modelo.mascotas.Mascota;
import modelo.personas.Clinica;
import modelo.personas.Veterinario;
import persistencia.EstadoSistema;
import persistencia.Repositorio;
import servicio.GestorUrgencias;

public class ControladorTurnos {
    private Repositorio estadoRepositorio;
    private EstadoSistema estado;
    private Clinica clinica;
    private Agenda agenda;
    private GestorUrgencias gestorUrgencias;
    private ControladorFacturacion controladorFacturacion;

    public ControladorTurnos(Repositorio estadoRepositorio, EstadoSistema estado,
            GestorUrgencias gestorUrgencias, ControladorFacturacion controladorFacturacion) {
        this.estadoRepositorio = estadoRepositorio;
        this.estado = estado;
        this.clinica = estado.getClinica();
        this.agenda = estado.getAgenda();
        this.gestorUrgencias = gestorUrgencias;
        this.controladorFacturacion = controladorFacturacion;
    }

    public boolean asignarTurno(String idMascota, String documentoVeterinario, TipoAtencion tipo, LocalDateTime fechaHora) {
        // Validar los parámetros
        if (idMascota == null || documentoVeterinario == null || tipo == null || fechaHora == null) return false;
        // Buscar la mascota y el veterinario
        Mascota mascota = clinica.buscarMascota(idMascota);
        Veterinario veterinario = clinica.buscarVeterinarioPorDocumento(documentoVeterinario);
        if (mascota == null || veterinario == null || !mascota.getDuenio().puedePedirTurno()) return false;
        // Delegar la asignación a Agenda
        if (!agenda.asignarTurno(mascota, veterinario, tipo, fechaHora)) return false;
        // Guardar los cambios
        estadoRepositorio.guardar(estado);
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
        estadoRepositorio.guardar(estado);
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
        estadoRepositorio.guardar(estado);
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
        estadoRepositorio.guardar(estado);
        return true;
    }

    public Turno atenderUrgencia(String idMascota) {
        // Validar el parámetro
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
        estadoRepositorio.guardar(estado);
        return turno;
    }
}