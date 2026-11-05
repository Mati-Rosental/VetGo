package modelo.facturacion.pago;

import java.io.Serializable;

public class Debito implements MedioPago {
    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        if (tienePlanSalud) {
            return (subtotal - (subtotal * 0.15));
        }
        else {
            return subtotal;
        }
    }

    @Override
    public String getNombre() {
        return "Débito";
    }
}
