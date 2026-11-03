package modelo;

public class Tratamiento {
    private String dosis;
    private String frecuencia;
    private int duracionDias;
    private int cantidad;
    private Producto medicamento;

    public int getCantidad() {
        System.out.println("Tratamiento.getCantidad");
        return 0;
    }

    public Tratamiento(String dosis, String frecuencia, int duracionDias, int cantidad, Producto medicamento) {
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracionDias = duracionDias;
        this.cantidad = cantidad;
        this.medicamento = medicamento;
    }
}
