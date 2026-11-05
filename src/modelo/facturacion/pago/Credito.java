package modelo.facturacion.pago;

import java.io.Serializable;

public class Credito implements MedioPago {
    private int cuotas;

    public Credito(int cuotas) {
        this.cuotas = cuotas;
    }

    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        return subtotal;
    }

    @Override
    public String getNombre() {
        return "Crédito";
    }
    public int getCuotas() {
        return cuotas;
    }
}
