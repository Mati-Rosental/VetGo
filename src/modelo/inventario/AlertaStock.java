package modelo.inventario;

import java.io.Serializable;
import java.time.LocalDate;

public class AlertaStock implements Serializable {
    private LocalDate fecha;
    private int stockActual;
    private int stockMinimo;
    private boolean vigente;
    private Producto producto;

    public void cerrar() {
        System.out.println("AlertaStock.cerrar");
    }

    public String getDescripcion() {
        // genera una descripcion y la devuelve
        System.out.println("AlertaStock.cerrar");
        return null;
    }

    public AlertaStock(Producto producto) {
        this.producto = producto;
        this.fecha = LocalDate.now();
        this.stockActual = producto.getStockActual();
        this.stockMinimo = producto.getStockMinimo();
        this.vigente = true;
    }
}
