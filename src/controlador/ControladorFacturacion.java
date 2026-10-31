package controlador;

import modelo.*;
import persistencia.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class ControladorFacturacion {
    private Clinica clinica;
    private Repositorio<Factura> facturaRepositorio;
    private Tarifario tarifario;
    private List<Factura> facturas;

    public ControladorFacturacion(Clinica clinica, Repositorio<Factura> facturaRepositorio, 
        Tarifario tarifario) {
        this.clinica = clinica;
        this.facturaRepositorio = facturaRepositorio;
        this.tarifario = tarifario;

        this.facturas = new ArrayList<>(facturaRepositorio.cargar());
    }

    public boolean facturarConsulta(String idConsulta, MedioPago medioPago) {
        // Buscar la consulta
        Consulta consulta = clinica.buscarConsulta(idConsulta);
        if (consulta == null) {
            return false;
        }
        // Verificar que pueda facturarse
        if (!consulta.estaConfirmada()) {
            return false;
        }
        // Obtener el precio de la consulta
        TipoAtencion tipo = consulta.getTurno().getTipoAtencion();
        double precioConsulta = tarifario.getPrecio(tipo);
        // Crear la factura
        Duenio duenio = consulta.getTurno().getMascota().getDuenio();
        Factura factura = new Factura(consulta, duenio, tarifario, medioPago);
        // Agregar la consulta como linea
        factura.agregarLinea("Consulta veterinaria", 1, precioConsulta);
        // Agregar una línea por cada insumo utilizado
        // Es una funcion lambda
        consulta.getInsumosUtilizados().forEach((producto, cantidad) -> {factura.agregarLinea(producto.getNombre(), cantidad, producto.getPrecioUnitario());});
        // Guardar la factura
        facturas.add(factura);
        facturaRepositorio.guardar(facturas);

        return true;
    }

    public boolean anularFactura(String idFactura, String motivo, String documentoRecepcionista) {
        // Validar parámetros
        if (idFactura == null || motivo == null || motivo.isBlank() || documentoRecepcionista == null) {
            return false;
        }
        // Buscar la factura
        Factura factura = null;
        for (Factura facturaRecorrida : facturas) {
            if (facturaRecorrida.getNumero().equals(idFactura)) {
                factura = facturaRecorrida;
            }
        }
        if (factura == null || factura.estaAnulada()) {
            return false;
        }
        // Buscar al recepcionista
        Recepcionista recepcionista = clinica.buscarRecepcionista(documentoRecepcionista);
        if (recepcionista == null) {
            return false;
        }
        // Delegar la anulación a Factura
        factura.anular(motivo, recepcionista);
        // Persistir los cambios
        facturaRepositorio.guardar(facturas);
        
        return true;
    }

    public Factura cobrarCargoCancelacionTardia(Turno turno) {
        // Validar el turno
        if (turno == null) return null;
        // Obtener el dueño
        Duenio duenio = turno.getMascota().getDuenio();
        // Calcular el importe del cargo
        double importe = tarifario.calcularCargoCancelacionTardia();
        // Crear la factura
        Factura factura = new Factura(null, duenio, tarifario, null);
        // Agregar la única línea de la factura
        factura.agregarLinea("Cargo por cancelación tardía", 1, importe);
        // Guardar la factura
        facturas.add(factura);
        facturaRepositorio.guardar(facturas);

        return factura;
    }
}