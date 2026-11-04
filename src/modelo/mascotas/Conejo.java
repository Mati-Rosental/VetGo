package modelo.mascotas;

import java.time.LocalDate;

import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.personas.Duenio;

public class Conejo extends Mascota {

    public Conejo(Duenio duenio, String nombre, String raza, Sexo sexo, LocalDate fechaNacimiento, double peso) {
        super(duenio, nombre, raza, sexo, fechaNacimiento, peso);
    }

    @Override
    public Especie getEspecie() {
        return Especie.CONEJO;
    }

    @Override
    public String getDatosPropios() {
        return "Sin datos propios";
    }
}
