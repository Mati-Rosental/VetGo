package modelo.inventario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import modelo.atencion.Consulta;

public class Inventario implements Serializable {
    private List<Producto> productos = new ArrayList<>();
    private List<AlertaStock> alertas = new ArrayList<>();
    private List<Proveedor> proveedores = new ArrayList<>();

    public List<AlertaStock> descontarStock(Consulta consulta) {
        List<AlertaStock> nuevasAlertas = new ArrayList<>();
        Map<Producto, Integer> insumos = consulta.getInsumosUtilizados();
        for (Map.Entry<Producto, Integer> entry : insumos.entrySet()) {
            Producto p = entry.getKey();
            int cant = entry.getValue();
            p.descontar(cant);
            if (p.estaBajoMinimo()) {
                AlertaStock alerta = new AlertaStock(p);
                alertas.add(alerta);
                nuevasAlertas.add(alerta);
            }
        }
        return nuevasAlertas;
    }

    public List<Producto> getProductosBajoMinimo() {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : productos) {
            if (p.estaBajoMinimo()) {
                lista.add(p);
            }
        }
        return lista;
    }

    public Producto buscarProducto(String codigoProducto) {
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigoProducto)) {
                return p;
            }
        }
        return null;
    }

    public Vacuna buscarVacuna(String codigoVacuna) {
        Producto p = buscarProducto(codigoVacuna);
        if (p instanceof Vacuna) {
            return (Vacuna) p;
        }
        return null;
    }

    public Proveedor buscarProveedor(String CUILProveedor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarProveedor'");
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public void darDeBajaVacuna(Vacuna vacuna) {
        productos.remove(vacuna); // Sin borrar historial de aplicadas
    }

    public void darDeBajaProducto(Producto producto) {
        productos.remove(producto);
    }

    public void agregarProveedor(Proveedor proveedor) {
        proveedores.add(proveedor);
        }

    public boolean proveedorTieneProductos(Proveedor proveedor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'proveedorTieneProductos'");
    }

    public void eliminarProveedor(Proveedor proveedor) {
        proveedores.remove(proveedor);
    }

    public List<Producto> getProductos() {
        return productos;
    }
}
