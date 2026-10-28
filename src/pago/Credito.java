package pago;

import modelo.*;
import modelo.MedioPago;
public class Credito implements MedioPago {
    private int cuotas;

    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        System.out.println("Credito.calcularTotal");
        return 0.0;
    }

    @Override
    public String getNombre() {
        System.out.println("Credito.getNombre");
        return null;
    }
}
