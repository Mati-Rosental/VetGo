package pago;

import modelo.*;
import modelo.MedioPago;
public class Efectivo implements MedioPago {
    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        System.out.println("Efectivo.calcularTotal");
        return 0.0;
    }

    @Override
    public String getNombre() {
        System.out.println("Efectivo.getNombre");
        return null;
    }
}
