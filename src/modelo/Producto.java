package modelo;

import java.util.ArrayList;
import java.util.List;

public class Producto {
    private String codigo;
    private String nombre;
    private int stockMinimo;
    private double precioUnitario;
    private Proveedor proveedor;
    private final List<Lote> lotes = new ArrayList<>();

    public int getStockActual() {
        System.out.println("Producto.getStockActual");
        return 0;
    }

    public boolean estaBajoMinimo() {
        System.out.println("Producto.estaBajoMinimo");
        return false;
    }

    public void agregarLote(Lote lote) {
        System.out.println("Producto.agregarLote");
    }

    public void descontar(int cantidad) {
        System.out.println("Producto.descontar");
    }

    public String getDescripcion() {
        System.out.println("Producto.getDescripcion");
        return null;
    }
}
