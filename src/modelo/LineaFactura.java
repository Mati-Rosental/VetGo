package modelo;

public class LineaFactura {
    private String descripcion;
    private int cantidad;
    private double precioUnitario;

    public LineaFactura(String descripcion, int cantidad, double precioUnitario) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        System.out.println("LineaFactura.getSubtotal");
        return 0.0;
    }
}
