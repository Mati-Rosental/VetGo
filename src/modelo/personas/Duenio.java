package modelo.personas;

import java.util.ArrayList;
import java.util.List;

import modelo.mascotas.Mascota;

public class Duenio extends Persona {
    private String correo;
    private boolean tienePlanSalud;
    private int ausenciasSeguidas;
    private boolean seniaRequerida;
    private double seniaPendiente;
    private final List<Mascota> mascotas = new ArrayList<>();

    public Duenio(String nombre, String apellido, String documento, String telefono, String correo, boolean tienePlanSalud) {
        super(nombre,apellido,documento,telefono);
        this.correo = correo;
        this.tienePlanSalud = tienePlanSalud;
        this.ausenciasSeguidas = 0;
        this.seniaRequerida = false;
        this.seniaPendiente = 0;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void registrarAusencia() {
        // con tres ausencias seguidas el dueño debe pagar una seña
        ausenciasSeguidas++;
        if (ausenciasSeguidas >= 3) {
            seniaRequerida = true;
        }

    }

    public void registrarAsistencia() {
        ausenciasSeguidas = 0;
    }

    public void pagarSenia(double monto) {
        seniaPendiente += monto;
        seniaRequerida = false;
    }

    public boolean puedePedirTurno() {
        return !seniaRequerida;
    }

    @Override
    public String getRol() {
        return "Dueño";
    }

    public void agregarMascota(Mascota mascota) {
        System.out.println("Duenio.agregarMascota");
    }
}
