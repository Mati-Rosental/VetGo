package modelo.mascotas;

import java.time.LocalDate;

import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.personas.Duenio;

public class Ave extends Mascota {
    private boolean vuela;

    public Ave(Duenio duenio, String nombre, String raza, Sexo sexo, LocalDate fechaNacimiento, double peso, boolean vuela) {
        super(duenio, nombre, raza, sexo, fechaNacimiento, peso);
        this.vuela = vuela;
    }

    @Override
    public Especie getEspecie() {
        return Especie.AVE;
    }

    @Override
    public String getDatosPropios() {
        return "Puede volar: " + (vuela ? "sí" : "no");
    }
}
