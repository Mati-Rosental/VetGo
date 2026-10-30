package modelo;

import java.time.LocalDate;

public class Ave extends Mascota {
    private boolean vuela;

    public Ave(Duenio duenio, String nombre, String raza, Sexo sexo, LocalDate fechaNacimiento, double peso, boolean vuela) {
        super(duenio, nombre, raza, sexo, fechaNacimiento, peso);
        this.vuela = vuela;
    }

    @Override
    public Especie getEspecie() {
        System.out.println("Ave.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Ave.getDatosPropios");
        return null;
    }
}
