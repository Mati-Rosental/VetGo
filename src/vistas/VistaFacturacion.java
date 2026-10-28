package vistas;

import controlador.ControladorFacturacion;
import modelo.Consulta;
import modelo.Factura;
import modelo.MedioPago;
import modelo.NotaCredito;
import modelo.Recepcionista;

public class VistaFacturacion {
    private final ControladorFacturacion controlador;

    public VistaFacturacion(ControladorFacturacion controlador) {
        this.controlador = controlador;
    }

    public void facturarConsulta(Consulta consulta, MedioPago medioPago) {
        mostrarResultado(
                "facturación de consulta",
                controlador.facturarConsulta(consulta, medioPago));
    }

    public void anularFactura(Factura factura, String motivo, Recepcionista recepcionista) {
        mostrarResultado(
                "anulación de factura",
                controlador.anularFactura(factura, motivo, recepcionista));
    }

    private void mostrarResultado(String operacion, boolean correcto) {
        if (correcto) {
            System.out.println("La vista ejecutó correctamente la operación: " + operacion + ".");
        } else {
            System.out.println("La vista no pudo ejecutar la operación: " + operacion + ".");
        }
    }
}
