package modelo;

import java.time.LocalDate;

public abstract class Mascota {
    private String nombre;
    private String raza;
    private Sexo sexo;
    private LocalDate fechaNacimiento;
    private double pesoActual;
    private LocalDate fechaTransferencia;
    private Duenio duenio;
    private HistoriaClinica historiaClinica;

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

    public abstract Especie getEspecie();

    public abstract String getDatosPropios();
}
