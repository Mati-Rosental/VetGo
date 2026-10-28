package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
import java.time.LocalDate;

public class ControladorAdministracion {
    private Repositorio<Persona> repositorioPersonas;
    private Repositorio<Producto> repositorioProductos;
    private Clinica clinica;
    private Inventario inventario;
    private GeneradorReportes generadorReportes;
    private ServicioRecordatorios servicioRecordatorios;

    public boolean altaVeterinario(Veterinario veterinario) {
        return true;
    }

    public boolean altaProducto(Producto producto, Proveedor proveedor) {
        return true;
    }

    public boolean altaVacuna(Vacuna vacuna) {
        return true;
    }

    public boolean emitirReporteTurnos(Veterinario veterinario, LocalDate desde, LocalDate hasta) {
        return true;
    }

    public boolean emitirReporteStockBajo() {
        return true;
    }

    public boolean emitirReporteLotesPorVencer(LocalDate fechaActual, int diasAnticipacion) {
        return true;
    }

    public boolean emitirRecordatorios(LocalDate fechaActual) {
        return true;
    }
}
