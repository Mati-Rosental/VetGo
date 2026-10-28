package vistas;

import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.Consulta;
import modelo.Duenio;
import modelo.Factura;
import modelo.Mascota;
import modelo.MedioPago;
import modelo.Recepcionista;
import modelo.TipoAtencion;
import modelo.Turno;
import modelo.Veterinario;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VistaRecepcionista {
    private final ControladorRegistro controladorRegistro;
    private final ControladorTurnos controladorTurnos;
    private final ControladorFacturacion controladorFacturacion;

    public VistaRecepcionista(
            ControladorRegistro controladorRegistro,
            ControladorTurnos controladorTurnos,
            ControladorFacturacion controladorFacturacion) {
        this.controladorRegistro = controladorRegistro;
        this.controladorTurnos = controladorTurnos;
        this.controladorFacturacion = controladorFacturacion;
    }

    public void registrarDuenio(Duenio duenio) {
        mostrarResultado("registro de dueño", controladorRegistro.registrarDuenio(duenio));
    }

    public void registrarMascota(Duenio duenio, Mascota mascota) {
        mostrarResultado("registro de mascota", controladorRegistro.registrarMascota(duenio, mascota));
    }

    public void transferirMascota(Mascota mascota, Duenio nuevoDuenio, LocalDate fecha) {
        mostrarResultado(
                "transferencia de mascota",
                controladorRegistro.transferirMascota(mascota, nuevoDuenio, fecha));
    }

    public void asignarTurno(
            Mascota mascota,
            Veterinario veterinario,
            TipoAtencion tipo,
            LocalDateTime fechaHora) {
        mostrarResultado(
                "asignación de turno",
                controladorTurnos.asignarTurno(mascota, veterinario, tipo, fechaHora));
    }

    public void reprogramarTurno(Turno turno, LocalDateTime nuevaFechaHora) {
        mostrarResultado(
                "reprogramación de turno",
                controladorTurnos.reprogramarTurno(turno, nuevaFechaHora));
    }

    public void cancelarTurno(Turno turno, LocalDateTime ahora) {
        mostrarResultado("cancelación de turno", controladorTurnos.cancelarTurno(turno, ahora));
    }

    public void registrarAusencia(Turno turno) {
        mostrarResultado("registro de ausencia", controladorTurnos.registrarAusencia(turno));
    }

    public void atenderUrgencia(Mascota mascota, LocalDate dia) {
        mostrarResultado("atención de urgencia", controladorTurnos.atenderUrgencia(mascota, dia));
    }

    public void facturarConsulta(Consulta consulta, MedioPago medioPago) {
        mostrarResultado(
                "facturación de consulta",
                controladorFacturacion.facturarConsulta(consulta, medioPago));
    }

    public void anularFactura(Factura factura, String motivo, Recepcionista recepcionista) {
        mostrarResultado(
                "anulación de factura",
                controladorFacturacion.anularFactura(factura, motivo, recepcionista));
    }

    private void mostrarResultado(String operacion, boolean correcto) {
        if (correcto) {
            System.out.println("La vista ejecutó correctamente la operación: " + operacion + ".");
        } else {
            System.out.println("La vista no pudo ejecutar la operación: " + operacion + ".");
        }
    }
}
