package modelo;

import java.time.LocalDate;

public class Conejo extends Mascota {

    public Conejo(Duenio duenio, String nombre, String raza, Sexo sexo, LocalDate fechaNacimiento, double peso) {
        super(duenio, nombre, raza, sexo, fechaNacimiento, peso);
    }

    @Override
    public Especie getEspecie() {
        System.out.println("Conejo.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Conejo.getDatosPropios");
        return null;
    }
}
