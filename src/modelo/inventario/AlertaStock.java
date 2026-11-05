package modelo.inventario;

import java.io.Serializable;
import java.time.LocalDate;

public class AlertaStock implements Serializable {
    private LocalDate fecha;
    private int stockActual;
    private int stockMinimo;
    private boolean vigente;
    private Producto producto;

    public AlertaStock(Producto producto) {
        this.producto = producto;
        this.fecha = LocalDate.now();
        this.stockActual = producto.getStockActual();
        this.stockMinimo = producto.getStockMinimo();
        this.vigente = true;
    }

    public void cerrar() {
        this.vigente = false;
    }

    public String getDescripcion() {
        // genera una descripcion y la devuelve
        return "Alerta: El producto " + producto.getNombre() + " tiene stock actual (" + producto.getStockActual() + ") menor al mínimo (" + stockMinimo + ").";
    }

    public boolean esVigente() {
        return vigente;
    }
}
