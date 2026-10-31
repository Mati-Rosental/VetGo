package modelo;

import java.util.ArrayList;
import java.util.List;

public class Inventario {
    private final List<Producto> productos = new ArrayList<>();
    private final List<AlertaStock> alertas = new ArrayList<>();

    public List<AlertaStock> descontarStock(Consulta consulta) {
        System.out.println("Inventario.descontarStock");
        return null;
    }

    public List<Producto> getProductosBajoMinimo() {
        System.out.println("Inventario.getProductosBajoMinimo");
        return null;
    }

    public Producto buscarProducto(String codigoProducto) {
        System.out.println("Inventario.buscarProducto");
        return null;
    }

    public Vacuna buscarVacuna(String codigoVacuna) {
        System.out.println("Inventario.buscarVacuna");
        return null;
    }
}
