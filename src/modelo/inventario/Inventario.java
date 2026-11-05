package modelo.inventario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import modelo.atencion.Consulta;

public class Inventario implements Serializable {
    private List<Producto> productos = new ArrayList<>();
    private List<AlertaStock> alertas = new ArrayList<>();
    private List<Proveedor> proveedores = new ArrayList<>();

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

    public Proveedor buscarProveedor(String CUILProveedor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarProveedor'");
    }

    public void agregarProducto(Producto producto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'agregarProducto'");
    }

    public void darDeBajaVacuna(Vacuna vacuna) {
        // TODO Auto-generated method stub
        // Dar de baja sin borrar el historial de vacunas aplicadas
        throw new UnsupportedOperationException("Unimplemented method 'darDeBaja'");
    }

    public void darDeBajaProducto(Producto producto) {
        // TODO Auto-generated method stub
        // Dar de baja sin borrar el historial de consultas asociadas
        throw new UnsupportedOperationException("Unimplemented method 'darDeBaja'");
    }

    public void agregarProveedor(Proveedor proveedor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'agregarProveedor'");
    }

    public boolean proveedorTieneProductos(Proveedor proveedor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'proveedorTieneProductos'");
    }

    public void eliminarProveedor(Proveedor proveedor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminarProveedor'");
    }

    public List<Producto> getProductos() {
        // TODO Auto-generated method stub
        return productos;    }
}
