package vistas;

import controlador.ControladorAtencion;
import modelo.Consulta;
import modelo.Mascota;
import modelo.Producto;
import modelo.Turno;
import modelo.Vacuna;
import modelo.Veterinario;

public class VistaVeterinario {
    private final ControladorAtencion controlador;

    public VistaVeterinario(ControladorAtencion controlador) {
        this.controlador = controlador;
    }

    public void registrarConsulta(Turno turno) {
        mostrarResultado("registro de consulta", controlador.iniciarConsulta(turno));
    }

    public void indicarTratamiento(
            Consulta consulta,
            Producto medicamento,
            String dosis,
            String frecuencia,
            int duracionDias,
            int cantidad) {
        mostrarResultado(
                "indicación de tratamiento",
                controlador.indicarTratamiento(
                        consulta, medicamento, dosis, frecuencia, duracionDias, cantidad));
    }

    public void aplicarVacuna(Consulta consulta, Vacuna vacuna) {
        mostrarResultado("aplicación de vacuna", controlador.aplicarVacuna(consulta, vacuna));
    }

    public void confirmarConsulta(Consulta consulta) {
        mostrarResultado("confirmación de consulta", controlador.confirmarConsulta(consulta));
    }

    public void registrarNotaRectificatoria(Consulta consulta, String texto, Veterinario firmante) {
        mostrarResultado(
                "registro de nota rectificatoria",
                controlador.registrarNotaRectificatoria(consulta, texto, firmante));
    }

    public void consultarHistoriaClinica(Mascota mascota) {
        mostrarResultado(
                "consulta de historia clínica",
                controlador.consultarHistoriaClinica(mascota));
    }

    private void mostrarResultado(String operacion, boolean correcto) {
        if (correcto) {
            System.out.println("La vista ejecutó correctamente la operación: " + operacion + ".");
        } else {
            System.out.println("La vista no pudo ejecutar la operación: " + operacion + ".");
        }
    }
}
