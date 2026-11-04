package modelo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Vacuna extends Producto {
    private Set<Especie> especies;
    private int edadMinimaSemanas;
    private int intervaloRefuerzoDias;

    public Vacuna(String codigo, String nombre, int stockMinimo, double precioUnitario, Proveedor proveedor, Set<Especie> especies, int edadMinimaSemanas, int intervaloRefuerzoDias) {
        super(codigo, nombre, stockMinimo, precioUnitario, proveedor);
        this.especies = especies;
        this.edadMinimaSemanas = edadMinimaSemanas;
        this.intervaloRefuerzoDias = intervaloRefuerzoDias;
    }

    public boolean aplicaA(Especie especie) {
        System.out.println("Vacuna.aplicaA");
        return false;
    }

    public boolean permiteEdad(int semanas) {
        System.out.println("Vacuna.permiteEdad");
        return false;
    }

    public LocalDate calcularProximoRefuerzo(LocalDate fechaAplicacion) {
        System.out.println("Vacuna.calcularProximoRefuerzo");
        return null;
    }

    public String getNumeroPrimerLote() {
        // Obtiene el numero del primer lote disponible, null si no hay
        System.out.println("Vacuna.getNumeroPrimerLote");
        return null;
    }

    @Override
    public String getDescripcion() {
        System.out.println("Vacuna.getDescripcion");
        return null;
    }

    public void modificarDatos(String nombre, Set<Especie> especies2, int edadMinimaSemanas2,
            int intervaloRefuerzoDias2, double precioUnitario) {
        // TODO Auto-generated method stub
        // Si alguna variable es null, no hay que modificarla
        throw new UnsupportedOperationException("Unimplemented method 'modificarDatos'");
    }
}
