package modelo.facturacion.pago;

import java.io.Serializable;

public class Efectivo implements MedioPago {
    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        return subtotal;
    }

    @Override
    public String getNombre() {
        return "Efectivo";
    }
}
