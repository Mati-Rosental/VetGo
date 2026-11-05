package modelo.mascotas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.personas.Duenio;

public abstract class Mascota implements Serializable {
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
    public String getId() {
        return id;
    }

    public void actualizarPeso(double peso) {
        this.peso = peso;
    }

    public void transferirA(Duenio nuevoDuenio, LocalDate fecha) {
        if (this.duenio != null) {
            this.duenio.getMascotas().remove(this);
        }
        this.duenio = nuevoDuenio;
        nuevoDuenio.agregarMascota(this);
        this.fechaTransferencia = fecha;
    }

    public Duenio getDuenio() {
        return duenio;
    }

    public String getNombre() {
        return nombre;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public abstract Especie getEspecie();

    public abstract String getDatosPropios();
}
