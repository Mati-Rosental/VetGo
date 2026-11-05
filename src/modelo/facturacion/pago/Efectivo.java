package modelo.facturacion.pago;

import java.io.Serializable;

public class Efectivo implements MedioPago {
    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        if (tienePlanSalud) {
            return (subtotal - (subtotal * 0.15));
        }
        else {
            return (subtotal - (subtotal * 0.10));
        }
    }

    @Override
    public String getNombre() {
        return "Efectivo";
    }
}
