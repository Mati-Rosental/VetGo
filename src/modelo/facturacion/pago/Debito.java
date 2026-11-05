package modelo.facturacion.pago;

import java.io.Serializable;

public class Debito implements MedioPago {
    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        return subtotal;
    }

    @Override
    public String getNombre() {
        return "Débito";
    }
}
