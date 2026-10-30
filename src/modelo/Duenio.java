package modelo;

import java.util.ArrayList;
import java.util.List;

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

    public void registrarAusencia() {
        System.out.println("Duenio.registrarAusencia");
    }

    public void registrarAsistencia() {
        System.out.println("Duenio.registrarAsistencia");
    }

    public void pagarSenia(double monto) {
        System.out.println("Duenio.pagarSenia");
    }

    public boolean puedePedirTurno() {
        System.out.println("Duenio.puedePedirTurno");
        return false;
    }

    @Override
    public String getRol() {
        System.out.println("Duenio.getRol");
        return null;
    }

    public void agregarMascota(Mascota mascota) {
        System.out.println("Duenio.agregarMascota");
    }
}
