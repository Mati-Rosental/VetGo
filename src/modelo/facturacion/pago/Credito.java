package modelo.facturacion.pago;

public class Credito implements MedioPago {
    private int cuotas;

    public Credito(int cuotas) {
        this.cuotas = cuotas;
    }

    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        // Aplica 15% de descuento con plan salud y luego suma 8% por recargo de credito
        if (tienePlanSalud) {
            double subtotalConDescuento = subtotal - (subtotal * 0.15);
            return (subtotalConDescuento + (subtotalConDescuento * 0.08));
        }
        else {
            return subtotal + (subtotal * 0.08);
        }
    }

    @Override
    public String getNombre() {
        return "Crédito";
    }

    public int getCuotas() {
        return cuotas;
    }
}
