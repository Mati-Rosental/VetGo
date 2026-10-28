package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
public class ControladorFacturacion {
    private Repositorio<Factura> repositorioFacturas;
    private Tarifario tarifario;

    public Factura facturarConsulta(Consulta consulta, MedioPago medioPago) {
        System.out.println("ControladorFacturacion.facturarConsulta");
        return null;
    }

    public NotaCredito anularFactura(Factura factura, String motivo, Recepcionista recepcionista) {
        System.out.println("ControladorFacturacion.anularFactura");
        return null;
    }
}
