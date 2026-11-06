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

    // Descuenta del inventario los insumos utilizados durante una consulta
    // y genera alertas si algún producto queda bajo el mínimo
    public List<AlertaStock> descontarStock(Consulta consulta) {
        List<AlertaStock> nuevasAlertas = new ArrayList<>();
        Map<Producto, Integer> insumos = consulta.getInsumosUtilizados();

        // Cierra las alertas de productos que ya repusieron stock, para que puedan volver a generarse
        cerrarAlertasResueltas();

        // Recorre los productos utilizados y descuenta las cantidades correspondientes
        for (Map.Entry<Producto, Integer> entry : insumos.entrySet()) {
            Producto p = entry.getKey();
            int cant = entry.getValue();

            p.descontar(cant);

            // Genera y registra una alerta si el stock queda por debajo del mínimo establecido
            // y el producto todavía no tiene una alerta vigente (evita alertas repetidas)
            if (p.estaBajoMinimo() && !tieneAlertaVigente(p)) {
                AlertaStock alerta = new AlertaStock(p);
                alertas.add(alerta);
                nuevasAlertas.add(alerta);
            }
        }

        return nuevasAlertas;
    }

    // Cierra las alertas vigentes cuyo producto ya no está bajo el mínimo o fue dado de baja
    public void cerrarAlertasResueltas() {
        for (AlertaStock alerta : alertas) {
            if (alerta.esVigente()
                    && (!productos.contains(alerta.getProducto()) || !alerta.getProducto().estaBajoMinimo())) {
                alerta.cerrar();
            }
        }
    }

    private boolean tieneAlertaVigente(Producto producto) {
        for (AlertaStock alerta : alertas) {
            if (alerta.esVigente() && alerta.getProducto() == producto) {
                return true;
            }
        }
        return false;
    }

    public List<AlertaStock> getAlertasVigentes() {
        List<AlertaStock> vigentes = new ArrayList<>();
        for (AlertaStock alerta : alertas) {
            if (alerta.esVigente()) {
                vigentes.add(alerta);
            }
        }
        return vigentes;
    }

    // Devuelve una lista con los productos cuyo stock está por debajo del mínimo establecido
    public List<Producto> getProductosBajoMinimo() {
        List<Producto> lista = new ArrayList<>();

        for (Producto p : productos) {
            if (p.estaBajoMinimo()) {
                lista.add(p);
            }
        }

        return lista;
    }

    // Busca un producto por su código y lo devuelve si existe
    public Producto buscarProducto(String codigoProducto) {
        for (Producto p : productos) {
            if (p.getCodigo().equals(codigoProducto)) {
                return p;
            }
        }

        return null;
    }

    // Busca un producto por código y verifica que sea una vacuna.
    public Vacuna buscarVacuna(String codigoVacuna) {
        Producto p = buscarProducto(codigoVacuna);

        if (p instanceof Vacuna) {
            return (Vacuna) p;
        }

        return null;
    }

    public Proveedor buscarProveedor(String CUITProveedor) {
        for (Proveedor p : proveedores) {
            if (p.getCUIT().equals(CUITProveedor)) {
                return p;
            }
        }

        return null;
    }

    // Verifica si el proveedor tiene al menos un producto asociado
    public boolean proveedorTieneProductos(Proveedor proveedor) {
        for (Producto p : productos) {
            if (p.getProveedor().equals(proveedor)) {
                return true;
            }
        }

        return false;
    }

    // Elimina un proveedor únicamente si no tiene productos asociados
    public void eliminarProveedor(Proveedor proveedor) {
        if (!proveedorTieneProductos(proveedor)) {
            proveedores.remove(proveedor);
        }
    }

    public void agregarProveedor(Proveedor proveedor) {
        proveedores.add(proveedor);
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public void darDeBajaProducto(Producto producto) {
        productos.remove(producto);
        cerrarAlertasResueltas();
    }
    
    public List<Producto> getProductos() {
        return productos;
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }
}