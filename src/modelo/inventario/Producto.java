package modelo.inventario;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Producto implements Serializable {
    protected final String codigo;
    protected String nombre;
    protected int stockMinimo;
    protected double precioUnitario;
    protected Proveedor proveedor;
    protected List<Lote> lotes = new ArrayList<>();

    public Producto(String codigo, String nombre, int stockMinimo, double precioUnitario, Proveedor proveedor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stockMinimo = stockMinimo;
        this.precioUnitario = precioUnitario;
        this.proveedor = proveedor;
    }

    public boolean hayStock(int cantidad) {
        return getStockActual() >= cantidad;
    }

    // Calcula el stock total disponible, sin contar los lotes vencidos
    public int getStockActual() {
        int total = 0;
        LocalDate hoy = LocalDate.now();
        for (Lote l : lotes) {
            if (!l.estaVencido(hoy)) {
                total += l.getCantidad();
            }
        }
        return total;
    }

    public boolean estaBajoMinimo() {
        return getStockActual() <= stockMinimo;
    }

    public void agregarLote(Lote lote) {
        lotes.add(lote);
        Collections.sort(lotes);
    }

    // Descuenta una cantidad del stock, priorizando los lotes según su orden y omitiendo los vencidos
    public void descontar(int cantidad) {
        int restante = cantidad;
        LocalDate hoy = LocalDate.now();
        for (Lote l : lotes) {
            if (!l.estaVencido(hoy) && l.getCantidad() > 0) {
                if (l.getCantidad() >= restante) {
                    l.descontar(restante);
                    break;
                } else {
                    restante -= l.getCantidad();
                    l.descontar(l.getCantidad());
                }
            }
        }
    }

    public String getDescripcion() {
        return "Producto: " + nombre + " [Código: " + codigo + ", Stock Actual: " + getStockActual() + "]";
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public List<Lote> getLotes() {
        return lotes;
    }

    // Modifica los datos del producto, manteniendo los valores actuales cuando los nuevos son null o no son válidos
    public void modificarDatos(String nuevoNombre, Integer nuevoStockMinimo, Double nuevoPrecioUnitario, Proveedor nuevoProveedor) {
        if (nuevoNombre != null && !nuevoNombre.isBlank()) this.nombre = nuevoNombre;
        if (nuevoStockMinimo != null && nuevoStockMinimo >= 0) this.stockMinimo = nuevoStockMinimo;
        if (nuevoPrecioUnitario != null && nuevoPrecioUnitario > 0) this.precioUnitario = nuevoPrecioUnitario;
        if (nuevoProveedor != null) this.proveedor = nuevoProveedor;
    }

    public void reemplazarLotes(List<Lote> lotes2) {
        this.lotes = lotes2;
    }
}
