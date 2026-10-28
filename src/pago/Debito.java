package pago;

import modelo.*;
import modelo.MedioPago;
public class Debito implements MedioPago {
    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        System.out.println("Debito.calcularTotal");
        return 0.0;
    }

    @Override
    public String getNombre() {
        System.out.println("Debito.getNombre");
        return null;
    }
}
