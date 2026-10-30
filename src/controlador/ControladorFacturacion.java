package controlador;

import modelo.*;
import persistencia.Repositorio;
import servicio.Notificador;

import java.util.ArrayList;
import java.util.List;

public class ControladorFacturacion {

    private Clinica clinica;
    private Repositorio<Clinica> clinicaRepositorio;
    private Repositorio<Factura> facturaRepositorio;
    private Tarifario tarifario;
    private Notificador notificador;
    private List<Factura> facturas;
    private MedioPago medioPago;


    public ControladorFacturacion(
            Repositorio<Clinica> clinicaRepositorio,
            Repositorio<Factura> facturaRepositorio,
            Tarifario tarifario,
            Notificador notificador) {

        this.clinicaRepositorio = clinicaRepositorio;
        this.facturaRepositorio = facturaRepositorio;
        this.tarifario = tarifario;
        this.notificador = notificador;

        // Si no hay clinica guardada entonces crea una
        // Repositorio guarda siempre una lista pero solo debe haber 1 clinica asi que obtiene el primer elemento
        List<Clinica> clinicas = clinicaRepositorio.cargar();
        this.clinica = clinicas.isEmpty() ? new Clinica("clinica") : clinicas.get(0);

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
        Factura factura = buscarFactura(idFactura);
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

    private Factura buscarFactura(String idFactura) {
        for (Factura factura : facturas) {
            if (factura.getNumero().equals(idFactura)) {
                return factura;
            }
        }
        return null;
    }
}