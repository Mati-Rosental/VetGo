package modelo.facturacion.pago;

import java.io.Serializable;

public interface MedioPago extends Serializable {
    double calcularTotal(double subtotal, boolean tienePlanSalud);
    String getNombre();
}