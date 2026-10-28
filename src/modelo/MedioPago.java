package modelo;

public interface MedioPago {
    double calcularTotal(double subtotal, boolean tienePlanSalud);

    String getNombre();
}
