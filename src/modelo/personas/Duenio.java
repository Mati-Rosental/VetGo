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

    // Al pagar la seña se levanta el bloqueo y se reinicia el conteo de ausencias.
    // El monto queda a favor y se descuenta de la próxima factura.
    public void pagarSenia(double monto) {
        seniaPendiente += monto;
        seniaRequerida = false;
        ausenciasSeguidas = 0;
    }

    // Descuenta de la seña a favor hasta el máximo indicado y devuelve lo consumido
    public double consumirSenia(double maximo) {
        double consumido = Math.min(seniaPendiente, Math.max(0, maximo));
        seniaPendiente -= consumido;
        return consumido;
    }

    public boolean requiereSenia() {
        return seniaRequerida;
    }

    public double getSeniaPendiente() {
        return seniaPendiente;
    }

    public boolean puedePedirTurno() {
        return !seniaRequerida;
    }

    public void agregarMascota(Mascota mascota) {
        this.mascotas.add(mascota);
    }

    public boolean tienePlanSalud() {
        return tienePlanSalud;
    }

    @Override
    public String getRol() {
        return "Dueño";
    }

    @Override
    public String toString() {
        return "Nombre: " + getNombreCompleto()
                + "\nDocumento: " + documento
                + "\nTeléfono: " + telefono
                + "\nCorreo: " + correo
                + "\nPlan de salud: " + (tienePlanSalud ? "Sí" : "No");
    }
}
