package modelo.atencion;

import java.io.Serializable;

import modelo.inventario.Producto;

public class Tratamiento implements Serializable {
    private String dosis;
    private String frecuencia;
    private int duracionDias;
    private int cantidad;
    private Producto medicamento;

    public int getCantidad() {
        return cantidad;
    }
    public Producto getMedicamento() {
        return medicamento;
    }

    public Tratamiento(String dosis, String frecuencia, int duracionDias, int cantidad, Producto medicamento) {
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracionDias = duracionDias;
        this.cantidad = cantidad;
        this.medicamento = medicamento;
    }
}
