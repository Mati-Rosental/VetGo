package vistas;

import controlador.ControladorAdministracion;
import modelo.Producto;
import modelo.Proveedor;
import modelo.Vacuna;
import modelo.Veterinario;
import java.time.LocalDate;

public class VistaAdministracion {
    private final ControladorAdministracion controlador;

    public VistaAdministracion(ControladorAdministracion controlador) {
        this.controlador = controlador;
    }

    public void altaVeterinario(Veterinario veterinario) {
        mostrarResultado(
                "alta de veterinario",
                controlador.altaVeterinario(veterinario));
    }

    public void altaProducto(Producto producto, Proveedor proveedor) {
        mostrarResultado(
                "alta de producto",
                controlador.altaProducto(producto, proveedor));
    }

    public void altaVacuna(Vacuna vacuna) {
        mostrarResultado(
                "alta de vacuna",
                controlador.altaVacuna(vacuna));
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

    public void emitirRecordatorios(LocalDate fechaActual) {
        mostrarResultado(
                "emisión de recordatorios",
                controlador.emitirRecordatorios(fechaActual));
    }

    private void mostrarResultado(String operacion, boolean correcto) {
        if (correcto) {
            System.out.println("La vista ejecutó correctamente la operación: " + operacion + ".");
        } else {
            System.out.println("La vista no pudo ejecutar la operación: " + operacion + ".");
        }
    }
}
