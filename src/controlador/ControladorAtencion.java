package controlador;

import modelo.*;
import persistencia.*;
import servicio.*;
public class ControladorAtencion {
    private Repositorio<Consulta> repositorioConsultas;
    private Inventario inventario;
    private Notificador notificador;

    public boolean iniciarConsulta(Turno turno) {
        return true;
    }

    public boolean registrarPeso(Consulta consulta, double peso) {
        return true;
    }

    public boolean indicarTratamiento(
            Consulta consulta,
            Producto medicamento,
            String dosis,
            String frecuencia,
            int duracionDias,
            int cantidad) {
        return true;
    }

    public boolean aplicarVacuna(Consulta consulta, Vacuna vacuna) {
        return true;
    }

    public boolean confirmarConsulta(Consulta consulta) {
        return true;
    }

    public boolean registrarNotaRectificatoria(Consulta consulta, String texto, Veterinario firmante) {
        return true;
    }

    public boolean consultarHistoriaClinica(Mascota mascota) {
        return true;
    }
}
