package modelo.mascotas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import modelo.agenda.Turno;
import modelo.atencion.Consulta;
import modelo.atencion.VacunaAplicada;

public class HistoriaClinica implements Serializable {
    private Mascota mascota;
    private final List<Consulta> consultas = new ArrayList<>();

    public Consulta abrirConsulta(Turno turno, String motivo) {
        System.out.println("HistoriaClinica.abrirConsulta");
        return null;
    }

    public List<Consulta> getConsultas() {
        return consultas;
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
