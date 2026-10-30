package modelo;

import java.time.LocalDate;

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
        System.out.println("Perro.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Perro.getDatosPropios");
        return null;
    }
}
