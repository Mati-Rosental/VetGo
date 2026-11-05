package modelo.inventario;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Producto implements Serializable {
    private final String codigo;
    private String nombre;
    private int stockMinimo;
    private double precioUnitario;
    private Proveedor proveedor;
    private List<Lote> lotes = new ArrayList<>();

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
        return getStockActual() < stockMinimo;
    }

    public void agregarLote(Lote lote) {
        lotes.add(lote);
        Collections.sort(lotes);
    }

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

    public void modificarDatos(String nombre2, int stockMinimo2, double precioUnitario2, Proveedor proveedor2) {
        if (nombre != null && !nombre.isBlank()) this.nombre = nombre;
        this.stockMinimo = stockMinimo;
        if (precioUnitario > 0) this.precioUnitario = precioUnitario;
        if (proveedor != null) this.proveedor = proveedor;
    }

    public void reemplazarLotes(List<Lote> lotes2) {
        this.lotes = lotes2;
    }
}
