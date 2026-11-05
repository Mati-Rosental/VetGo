package modelo.inventario;

import java.io.Serializable;
import java.time.LocalDate;

public class Lote implements Comparable<Lote>, Serializable {
    private String numero;
    private int cantidad;
    private LocalDate fechaVencimiento;

    public Lote(String numero, int cantidad, LocalDate fechaVencimiento) {
        this.numero = numero;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean estaVencido(LocalDate fechaActual) {
        return fechaVencimiento.isBefore(fechaActual);
    }

    public void descontar(int cantidad) {
        this.cantidad -= cantidad;
    }

    @Override
    public int compareTo(Lote otro) {
        return this.fechaVencimiento.compareTo(otro.fechaVencimiento);
    }

    public String getDescripcion() {
        return "Lote " + numero + " (Cant: " + cantidad + ", Vto: " + fechaVencimiento + ")";
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
}
