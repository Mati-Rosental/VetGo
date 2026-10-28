package vistas;

import controlador.ControladorAtencion;
import modelo.Consulta;
import modelo.HistoriaClinica;
import modelo.Mascota;
import modelo.Producto;
import modelo.Turno;
import modelo.Vacuna;
import modelo.Veterinario;

public class VistaAtencion {
    private final ControladorAtencion controlador;

    public VistaAtencion(ControladorAtencion controlador) {
        this.controlador = controlador;
    }

    public void iniciarConsulta(Turno turno) {
        mostrarResultado(
                "inicio de consulta",
                controlador.iniciarConsulta(turno));
    }

    public void registrarPeso(Consulta consulta, double peso) {
        mostrarResultado(
                "registro de peso",
                controlador.registrarPeso(consulta, peso));
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
                controlador.indicarTratamiento(consulta, medicamento, dosis, frecuencia, duracionDias, cantidad));
    }

    public void aplicarVacuna(Consulta consulta, Vacuna vacuna) {
        mostrarResultado(
                "aplicación de vacuna",
                controlador.aplicarVacuna(consulta, vacuna));
    }

    public void confirmarConsulta(Consulta consulta) {
        mostrarResultado(
                "confirmación de consulta",
                controlador.confirmarConsulta(consulta));
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
