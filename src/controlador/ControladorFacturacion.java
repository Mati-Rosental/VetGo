package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
public class ControladorFacturacion {
    private Repositorio<Factura> repositorioFacturas;
    private Tarifario tarifario;

    public boolean facturarConsulta(Consulta consulta, MedioPago medioPago) {
        return true;
    }

    public boolean anularFactura(Factura factura, String motivo, Recepcionista recepcionista) {
        return true;
    }
}
