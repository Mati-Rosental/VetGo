package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
public class ControladorAtencion {
    private Repositorio<Consulta> repositorioConsultas;
    private Inventario inventario;
    private Notificador notificador;

    public Consulta iniciarConsulta(Turno turno) {
        System.out.println("ControladorAtencion.iniciarConsulta");
        return null;
    }

    public void registrarPeso(Consulta consulta, double peso) {
        System.out.println("ControladorAtencion.registrarPeso");
    }

    public void indicarTratamiento(
            Consulta consulta,
            Producto medicamento,
            String dosis,
            String frecuencia,
            int duracionDias,
            int cantidad) {
        System.out.println("ControladorAtencion.indicarTratamiento");
    }

    public void aplicarVacuna(Consulta consulta, Vacuna vacuna) {
        System.out.println("ControladorAtencion.aplicarVacuna");
    }

    public void confirmarConsulta(Consulta consulta) {
        System.out.println("ControladorAtencion.confirmarConsulta");
    }

    public void registrarNotaRectificatoria(Consulta consulta, String texto, Veterinario firmante) {
        System.out.println("ControladorAtencion.registrarNotaRectificatoria");
    }

    public HistoriaClinica consultarHistoriaClinica(Mascota mascota) {
        System.out.println("ControladorAtencion.consultarHistoriaClinica");
        return null;
    }
}
