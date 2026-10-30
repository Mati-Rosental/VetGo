package modelo;

import java.time.LocalDate;

public class Gato extends Mascota {
    private boolean viveEnInterior;

    public Gato(Duenio duenio, String nombre, String raza, Sexo sexo, LocalDate fechaNacimiento, double peso, boolean viveEnInterior) {
        super(duenio, nombre, raza, sexo, fechaNacimiento, peso);
        this.viveEnInterior = viveEnInterior;
    }

    @Override
    public Especie getEspecie() {
        System.out.println("Gato.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Gato.getDatosPropios");
        return null;
    }
}
