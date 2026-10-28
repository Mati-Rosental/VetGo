package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
import java.time.LocalDate;
import java.util.List;

public class ControladorAdministracion {
    private Repositorio<Persona> repositorioPersonas;
    private Repositorio<Producto> repositorioProductos;
    private Clinica clinica;
    private Inventario inventario;
    private GeneradorReportes generadorReportes;
    private ServicioRecordatorios servicioRecordatorios;

    public void altaVeterinario(Veterinario veterinario) {
        System.out.println("ControladorAdministracion.altaVeterinario");
    }

    public void altaProducto(Producto producto, Proveedor proveedor) {
        System.out.println("ControladorAdministracion.altaProducto");
    }

    public void altaVacuna(Vacuna vacuna) {
        System.out.println("ControladorAdministracion.altaVacuna");
    }

    public List<Turno> emitirReporteTurnos(Veterinario veterinario, LocalDate desde, LocalDate hasta) {
        System.out.println("ControladorAdministracion.emitirReporteTurnos");
        return null;
    }

    public List<Producto> emitirReporteStockBajo() {
        System.out.println("ControladorAdministracion.emitirReporteStockBajo");
        return null;
    }

    public List<Lote> emitirReporteLotesPorVencer(LocalDate fechaActual, int diasAnticipacion) {
        System.out.println("ControladorAdministracion.emitirReporteLotesPorVencer");
        return null;
    }

    public void emitirRecordatorios(LocalDate fechaActual) {
        System.out.println("ControladorAdministracion.emitirRecordatorios");
    }
}
