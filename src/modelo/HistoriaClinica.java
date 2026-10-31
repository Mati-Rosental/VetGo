package modelo;

import java.util.ArrayList;
import java.util.List;

public class HistoriaClinica {
    private Mascota mascota;
    private final List<Consulta> consultas = new ArrayList<>();

    public Consulta abrirConsulta(Turno turno, String motivo) {
        System.out.println("HistoriaClinica.abrirConsulta");
        return null;
    }

    public List<Consulta> getConsultas() {
        System.out.println("HistoriaClinica.getConsultas");
        return null;
    }

    public List<VacunaAplicada> getVacunasAplicadas() {
        System.out.println("HistoriaClinica.getVacunasAplicadas");
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Historia clínica de: ").append(mascota.getNombre()).append("\n");
        sb.append("--------------------------------\n");

        if (consultas.isEmpty()) {
            sb.append("No hay consultas registradas.\n");
        } else {
            for (Consulta consulta : consultas) {
                sb.append(consulta).append("\n");
                sb.append("--------------------------------\n");
            }
        }

        return sb.toString();
    }
}
