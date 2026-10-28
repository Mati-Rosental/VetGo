package modelo;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private final List<Producto> productos = new ArrayList<>();
    private final List<AlertaStock> alertas = new ArrayList<>();

    public boolean hayStock(Producto producto, int cantidad) {
        System.out.println("Inventario.hayStock");
        return false;
    }

    public List<Lote> descontarStock(Consulta consulta) {
        System.out.println("Inventario.descontarStock");
        return null;
    }

    public List<Producto> getProductosBajoMinimo() {
        System.out.println("Inventario.getProductosBajoMinimo");
        return null;
    }
}
