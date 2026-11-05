package controlador;

import modelo.*;
import modelo.agenda.Turno;
import modelo.atencion.Consulta;
import modelo.enumeradores.TipoAtencion;
import modelo.facturacion.Factura;
import modelo.facturacion.LineaFactura;
import modelo.facturacion.Tarifario;
import modelo.facturacion.pago.MedioPago;
import modelo.personas.Clinica;
import modelo.personas.Duenio;
import modelo.personas.Recepcionista;
import persistencia.EstadoSistema;
import persistencia.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class ControladorFacturacion {
    private Clinica clinica;
    private Repositorio estadoRepositorio;
    private EstadoSistema estado;
    private Tarifario tarifario;
    private List<Factura> facturas;

    public ControladorFacturacion(EstadoSistema estado, Repositorio estadoRepositorio, Tarifario tarifario) {
        this.estado = estado;
        this.estadoRepositorio = estadoRepositorio;
        this.clinica = estado.getClinica();
        this.tarifario = tarifario;
        this.facturas = estado.getFacturas();
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
        // Crear las líneas de la factura
        List<LineaFactura> lineas = new ArrayList<>();
        lineas.add(new LineaFactura("Consulta veterinaria", 1, precioConsulta));
        // Agregar una línea por cada insumo utilizado
        consulta.getInsumosUtilizados().forEach((producto, cantidad) -> {
            lineas.add(new LineaFactura(producto.getNombre(), cantidad, producto.getPrecioUnitario()));
        });
        // Crear la factura con todas sus líneas
        Factura factura = new Factura(consulta, medioPago, lineas);
        // Guardar la factura
        facturas.add(factura);
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

        return true;
    }

    public Factura cobrarCargoCancelacionTardia(Turno turno) {
        // Validar el turno
        if (turno == null) return null;
        // Obtener el dueño
        Duenio duenio = turno.getMascota().getDuenio();
        // Calcular el importe del cargo
        double importe = tarifario.calcularCargoCancelacionTardia();
        // Crear las líneas de la factura
        List<LineaFactura> lineas = new ArrayList<>();
        lineas.add(new LineaFactura("Cargo por cancelación tardía", 1, importe));
        // Crear la factura
        Factura factura = new Factura(duenio, lineas);
        // Guardar la factura
        facturas.add(factura);
        estadoRepositorio.guardar(estado);

        return factura;
    }
}