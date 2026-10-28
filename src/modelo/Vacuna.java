package modelo;

import java.time.LocalDate;
import java.util.Set;

public class Vacuna extends Producto {
    private Set<Especie> especies;
    private int edadMinimaSemanas;
    private int intervaloRefuerzoDias;

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

    @Override
    public String getDescripcion() {
        System.out.println("Vacuna.getDescripcion");
        return null;
    }
}
