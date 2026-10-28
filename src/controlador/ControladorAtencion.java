package controlador;

public class ControladorAtencion {
    public boolean iniciarConsulta(String idTurno) {
        System.out.println("ControladorAtencion.iniciarConsulta");
        return false;
    }

    public boolean registrarPeso(String idConsulta, String peso) {
        System.out.println("ControladorAtencion.registrarPeso");
        return false;
    }

    public boolean indicarTratamiento(
            String idConsulta,
            String idMedicamento,
            String dosis,
            String frecuencia,
            String duracionDias,
            String cantidad) {
        System.out.println("ControladorAtencion.indicarTratamiento");
        return false;
    }

    public boolean aplicarVacuna(String idConsulta, String idVacuna) {
        System.out.println("ControladorAtencion.aplicarVacuna");
        return false;
    }

    public boolean confirmarConsulta(String idConsulta) {
        System.out.println("ControladorAtencion.confirmarConsulta");
        return false;
    }

    public boolean registrarNotaRectificatoria(
            String idConsulta, String texto, String documentoFirmante) {
        System.out.println("ControladorAtencion.registrarNotaRectificatoria");
        return false;
    }

    public boolean consultarHistoriaClinica(String idMascota) {
        System.out.println("ControladorAtencion.consultarHistoriaClinica");
        return false;
    }
}
