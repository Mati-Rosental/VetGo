package modelo;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Mascota {
    private final String id;
    private Duenio duenio;
    private String nombre;
    private String raza;
    private Sexo sexo;
    private LocalDate fechaNacimiento;
    private double peso;
    private LocalDate fechaTransferencia;
    private HistoriaClinica historiaClinica;

    public Mascota(Duenio duenio, String nombre, String raza, Sexo sexo, LocalDate fechaNacimiento, double peso) {        
        this.id = UUID.randomUUID().toString();
        this.duenio = duenio;
        this.nombre = nombre;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.fechaTransferencia = null;
        this.historiaClinica = new HistoriaClinica();
    }

    public int edadEnSemanas(LocalDate fechaActual) {
        System.out.println("Mascota.edadEnSemanas");
        return 0;
    }

    public void actualizarPeso(double peso) {
        System.out.println("Mascota.actualizarPeso");
    }

    public void transferirA(Duenio nuevoDuenio, LocalDate fecha) {
        System.out.println("Mascota.transferirA");
    }

    public Duenio getDuenio() {
        System.out.println("Mascota.getDuenio");
        return null;
    }

    public abstract Especie getEspecie();

    public abstract String getDatosPropios();
}
