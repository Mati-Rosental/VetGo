
package controlador;

import java.util.List;

import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.atencion.Consulta;
import modelo.inventario.AlertaStock;
import modelo.inventario.Inventario;
import modelo.inventario.Producto;
import modelo.inventario.Vacuna;
import modelo.mascotas.HistoriaClinica;
import modelo.mascotas.Mascota;
import modelo.personas.Administrador;
import modelo.personas.Clinica;
import modelo.personas.Veterinario;
import persistencia.EstadoSistema;
import persistencia.Repositorio;
import servicio.Notificador;

public class ControladorAtencion {

    private Repositorio estadoRepositorio;
    private EstadoSistema estado;

    private Clinica clinica;
    private Agenda agenda;
    private Inventario inventario;

    private Notificador notificador;

    public ControladorAtencion(Repositorio estadoRepositorio, EstadoSistema estado, Notificador notificador) {
        this.estadoRepositorio = estadoRepositorio;
        this.estado = estado;

        this.clinica = estado.getClinica();
        this.agenda = estado.getAgenda();
        this.inventario = estado.getInventario();

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
        // Marcar el turno como en curso
        turno.marcarEnCurso();
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

        return true;
    }

    public boolean registrarPeso(String idConsulta, double peso) {
        // Validar los parámetros
        if (idConsulta == null || peso <= 0) {
            return false;
        }
        // Buscar la consulta
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        if (consulta == null || !consulta.estaEnCurso()) {
            return false;
        }
        // Registrar el peso en la consulta
        consulta.registrarPeso(peso);
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

        return true;
    }
    public boolean registrarDiagnostico(String idConsulta, String diagnostico) {
        // Validar los parámetros
        if (idConsulta == null || vacio(diagnostico)) {
            return false;
        }
        // Buscar la consulta
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        if (consulta == null || !consulta.estaEnCurso()) {
            return false;
        }
        // Registrar el diagnostico en la consulta
        consulta.registrarDiagnostico(diagnostico);
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

        return true;
    }
    public boolean registrarObservaciones(String idConsulta, String observaciones) {
        // Validar los parámetros
        if (idConsulta == null || vacio(observaciones)) {
            return false;
        }
        // Buscar la consulta
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        if (consulta == null || !consulta.estaEnCurso()) {
            return false;
        }
        // Registrar las observaciones en la consulta
        consulta.registrarObservaciones(observaciones);
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

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
        if (consulta == null || medicamento == null || !consulta.estaEnCurso()) {
            return false;
        }
        // Verificar que haya stock suficiente
        if (!medicamento.hayStock(cantidad)) {
            return false;
        }
        // Delegar el registro del tratamiento a Consulta
        if (!consulta.indicarTratamiento(medicamento, dosis, frecuencia, duracionDias, cantidad)) {
            return false;
        }
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

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
        if (consulta == null || vacuna == null || !consulta.estaEnCurso()) {
            return false;
        }
        // Verificar que haya stock suficiente
        if (!vacuna.hayStock(1)) {
            return false;
        }
        // Registrar la aplicación de la vacuna en la consulta
        consulta.aplicarVacuna(vacuna);
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

        return true;
    }

    public boolean confirmarConsulta(String idConsulta) {
        // Buscar la consulta
        if (idConsulta == null) {
            return false;
        }
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        if (consulta == null || !consulta.estaEnCurso()) {
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
        // Marcar turno como atendido
        consulta.getTurno().marcarAtendido();
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

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
        Veterinario veterinario = clinica.buscarVeterinarioPorDocumento(documentoFirmante);
        if (consulta == null || veterinario == null || consulta.estaEnCurso()) {
            return false;
        }
        // Delegar la creación de la nota rectificatoria a Consulta
        consulta.agregarNota(texto, veterinario);
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

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


    // Devuelve los turnos que todavía pueden iniciar una consulta
    public List<Turno> obtenerTurnosProgramados() {
        List<Turno> disponibles = new java.util.ArrayList<>();
        for (Turno turno : agenda.getTurnos()) {
            if (turno.getEstado() == modelo.enumeradores.EstadoTurno.PROGRAMADO) {
                disponibles.add(turno);
            }
        }
        return disponibles;
    }

    // Devuelve las consultas que todavía están siendo atendidas
    public List<Consulta> obtenerConsultasEnCurso() {
        List<Consulta> consultas = new java.util.ArrayList<>();
        for (Consulta consulta : clinica.getConsultas()) {
            if (consulta.estaEnCurso()) consultas.add(consulta);
        }
        return consultas;
    }

    // Devuelve las consultas que ya finalizaron su atención
    public List<Consulta> obtenerConsultasFinalizadas() {
        List<Consulta> consultas = new java.util.ArrayList<>();
        for (Consulta consulta : clinica.getConsultas()) {
            if (!consulta.estaEnCurso()) consultas.add(consulta);
        }
        return consultas;
    }

    public List<Producto> obtenerProductos() {
        return new java.util.ArrayList<>(inventario.getProductos());
    }

    public List<Vacuna> obtenerVacunas() {
        List<Vacuna> vacunas = new java.util.ArrayList<>();
        for (Producto producto : inventario.getProductos()) {
            if (producto instanceof Vacuna) vacunas.add((Vacuna) producto);
        }
        return vacunas;
    }

    public List<modelo.mascotas.Mascota> obtenerMascotas() {
        return new java.util.ArrayList<>(clinica.getMascotas());
    }

    public List<Veterinario> obtenerVeterinarios() {
        return new java.util.ArrayList<>(clinica.getVeterinarios());
    }

    // Auxiliares

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
