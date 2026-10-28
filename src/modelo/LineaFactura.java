package modelo;

public class LineaFactura {
    private String descripcion;
    private int cantidad;
    private double precioUnitario;

    public double getSubtotal() {
        System.out.println("LineaFactura.getSubtotal");
        return 0.0;
    }
}
