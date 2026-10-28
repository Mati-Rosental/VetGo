package vistas;

import controlador.ControladorAdministracion;
import modelo.HorarioAtencion;
import modelo.Producto;
import modelo.Proveedor;
import modelo.Recepcionista;
import modelo.Vacuna;
import modelo.Veterinario;
import java.time.LocalDate;

public class VistaAdministrador {
    private final ControladorAdministracion controlador;

    public VistaAdministrador(ControladorAdministracion controlador) {
        this.controlador = controlador;
    }

    public void gestionarVeterinario(Veterinario veterinario) {
        mostrarResultado("gestión de veterinario", controlador.altaVeterinario(veterinario));
    }

    public void gestionarHorario(Veterinario veterinario, HorarioAtencion horario) {
        mostrarResultado(
                "gestión de horario",
                controlador.gestionarHorario(veterinario, horario));
    }

    public void gestionarRecepcionista(Recepcionista recepcionista) {
        mostrarResultado(
                "gestión de personal de recepción",
                controlador.altaRecepcionista(recepcionista));
    }

    public void gestionarCatalogoVacunas(Vacuna vacuna) {
        mostrarResultado("gestión del catálogo de vacunas", controlador.altaVacuna(vacuna));
    }

    public void gestionarCatalogoProductos(Producto producto, Proveedor proveedor) {
        mostrarResultado(
                "gestión del catálogo de productos",
                controlador.altaProducto(producto, proveedor));
    }

    public void gestionarCatalogoProveedores(Proveedor proveedor) {
        mostrarResultado(
                "gestión del catálogo de proveedores",
                controlador.altaProveedor(proveedor));
    }

    public void emitirReporteTurnos(Veterinario veterinario, LocalDate desde, LocalDate hasta) {
        mostrarResultado(
                "reporte de turnos",
                controlador.emitirReporteTurnos(veterinario, desde, hasta));
    }

    public void emitirReporteStockBajo() {
        mostrarResultado("reporte de stock bajo", controlador.emitirReporteStockBajo());
    }

    public void emitirReporteLotesPorVencer(LocalDate fechaActual, int diasAnticipacion) {
        mostrarResultado(
                "reporte de lotes próximos a vencer",
                controlador.emitirReporteLotesPorVencer(fechaActual, diasAnticipacion));
    }

    private void mostrarResultado(String operacion, boolean correcto) {
        if (correcto) {
            System.out.println("La vista ejecutó correctamente la operación: " + operacion + ".");
        } else {
            System.out.println("La vista no pudo ejecutar la operación: " + operacion + ".");
        }
    }
}
