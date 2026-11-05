package modelo.facturacion.pago;

public class Credito implements MedioPago {
    private int cuotas;

    public Credito(int cuotas) {
        this.cuotas = cuotas;
    }

    @Override
    public double calcularTotal(double subtotal, boolean tienePlanSalud) {
        // Aplica 15% de descuento con plan salud y otro 7% por recargo de credito
        if (tienePlanSalud) {
            double subtotalConDescuento = subtotal - (subtotal * 0.15);
            return (subtotalConDescuento - (subtotalConDescuento * 0.07));
        }
        else {
            return subtotal - (subtotal * 0.07);
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
