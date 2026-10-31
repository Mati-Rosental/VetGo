package modelo;

import java.time.LocalDate;

public class AlertaStock {
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
}
