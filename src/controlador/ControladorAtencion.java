
package controlador;

import java.util.List;

import modelo.*;
import persistencia.Repositorio;
import servicio.Notificador;

public class ControladorAtencion {

    private Repositorio<Clinica> clinicaRepositorio;
    private Repositorio<Agenda> agendaRepositorio;
    private Clinica clinica;
    private Agenda agenda;
    private Inventario inventario;
    private Notificador notificador;

    public ControladorAtencion(Repositorio<Clinica> clinicaRepositorio, Clinica clinica, Repositorio<Agenda> agendaRepositorio, Agenda agenda, Inventario inventario, Notificador notificador) {
        this.clinicaRepositorio = clinicaRepositorio;
        this.clinica = clinica;
        this.agendaRepositorio = agendaRepositorio;
        this.agenda = agenda;
        this.inventario = inventario;
        this.notificador = notificador;
    }

    public boolean iniciarConsulta(String idTurno, String motivo) {
        // Validar los parámetros
        if (idTurno == null || motivo == null || motivo.isBlank()) {
            return false;
        }
        // Buscar el turno en Agenda
        Turno turno = agenda.buscarTurno(idTurno);
        if (turno == null || turno.getConsulta() != null) {
            return false;
        }
        // Abrir la consulta en la historia clínica de la mascota
        Mascota mascota = turno.getMascota();
        mascota.getHistoriaClinica().abrirConsulta(turno, motivo);
        // Marcar el turno como atendido
        turno.marcarAtendido();
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));
        agendaRepositorio.guardar(List.of(agenda));

        return true;
    }

    public boolean registrarPeso(String idConsulta, double peso) {
        // Validar los parámetros
        if (idConsulta == null || peso <= 0) {
            return false;
        }
        // Buscar la consulta
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        if (consulta == null || consulta.estaConfirmada()) {
            return false;
        }
        // Registrar el peso en la consulta
        consulta.registrarPeso(peso);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean indicarTratamiento(String idConsulta, String idProducto, String dosis, String frecuencia, int duracionDias, int cantidad) {
        // Validar los parámetros
        if (idConsulta == null || idProducto == null
                || dosis == null || dosis.isBlank()
                || frecuencia == null || frecuencia.isBlank()
                || duracionDias <= 0 || cantidad <= 0) {
            return false;
        }
        // Buscar la consulta y el medicamento
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        Producto medicamento = inventario.buscarProducto(idProducto);
        if (consulta == null || medicamento == null || consulta.estaConfirmada()) {
            return false;
        }
        // Verificar que haya stock suficiente
        if (!medicamento.hayStock(cantidad)) {
            return false;
        }
        // Delegar el registro del tratamiento a Consulta
        consulta.indicarTratamiento(medicamento, dosis, frecuencia, duracionDias, cantidad);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean aplicarVacuna(String idConsulta, String idVacuna) {
        // Validar los parámetros
        if (idConsulta == null || idVacuna == null) {
            return false;
        }
        // Buscar la consulta y la vacuna
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        Vacuna vacuna = inventario.buscarVacuna(idVacuna);
        if (consulta == null || vacuna == null || consulta.estaConfirmada()) {
            return false;
        }
        // Verificar que haya stock suficiente
        if (!vacuna.hayStock(1)) {
            return false;
        }
        // Registrar la aplicación de la vacuna en la consulta
        consulta.aplicarVacuna(vacuna);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean confirmarConsulta(String idConsulta) {
        // Buscar la consulta
        if (idConsulta == null) {
            return false;
        }
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        if (consulta == null || consulta.estaConfirmada()) {
            return false;
        }
        // Descontar los insumos utilizados y obtener las alertas de stock bajo
        List<AlertaStock> alertas = inventario.descontarStock(consulta);
        // Confirmar la consulta
        consulta.confirmar();
        // Notificar a todos los administradores si se generaron alertas
        if (alertas != null) {
            for (Administrador administrador : clinica.getAdministradores()) {
                for (AlertaStock alerta : alertas) {
                    notificador.notificar(administrador, alerta.getDescripcion());
                }
            }
        }
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean registrarNotaRectificatoria(String idConsulta, String texto, String documentoFirmante) {
        // Validar los parámetros
        if (idConsulta == null || texto == null || texto.isBlank()
                || documentoFirmante == null) {
            return false;
        }
        // Buscar la consulta y el veterinario firmante
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        Veterinario veterinario = clinica.buscarVeterinario(documentoFirmante);
        if (consulta == null || veterinario == null || !consulta.estaConfirmada()) {
            return false;
        }
        // Delegar la creación de la nota rectificatoria a Consulta
        consulta.agregarNota(texto, veterinario);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public HistoriaClinica consultarHistoriaClinica(String idMascota) {
        // Buscar la mascota
        if (idMascota == null) {
            return null;
        }
        Mascota mascota = clinica.buscarMascota(idMascota);
        if (mascota == null) {
            return null;
        }
        // Obtener la historia clínica de la mascota
        return mascota.getHistoriaClinica();
    }
}
