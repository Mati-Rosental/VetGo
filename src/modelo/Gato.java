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
        return Especie.GATO;
    }

    @Override
    public String getDatosPropios() {
        return "Vive en " + (viveEnInterior ? "interior" : "exterior");
    }
}
