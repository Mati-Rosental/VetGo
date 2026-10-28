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
}
