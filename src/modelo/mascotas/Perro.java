package modelo.mascotas;

import java.time.LocalDate;

import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.enumeradores.TamanioPerro;
import modelo.personas.Duenio;

public class Perro extends Mascota {
    private String numeroChip;
    private TamanioPerro tamanio;

    

    public Perro(Duenio duenio, String nombre, String raza, Sexo sexo, LocalDate fechaNacimiento, double peso,
            String numeroChip, TamanioPerro tamanio) {
        super(duenio, nombre, raza, sexo, fechaNacimiento, peso);
        this.numeroChip = numeroChip;
        this.tamanio = tamanio;
    }

    @Override
    public Especie getEspecie() {
        return Especie.PERRO;
    }

    @Override
    public String getDatosPropios() {
        return "Chip: " + numeroChip + ", tamaño: " + tamanio;
    }
}
