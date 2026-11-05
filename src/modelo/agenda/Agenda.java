package modelo.agenda;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.enumeradores.EstadoTurno;
import modelo.personas.Veterinario;

public class Agenda implements Serializable {
    private final List<Turno> turnos = new ArrayList<>();

    public boolean asignarTurno(Turno turno) {
        if (hayDisponibilidad(turno.getVeterinario(), turno.getFechaHora(), turno.getDuracionMin())) {
            turnos.add(turno);
            return true;
        }
        return false;
    }

    public boolean reprogramar(Turno turno, LocalDateTime nuevaFechaHora) {
        turnos.remove(turno);
        // Quita al turno de la lista para validar que haya disponibilidad
        // Si no lo quita, puede colisionar con si mismo y decir que no hay disponibilidad
        boolean hayLugar = hayDisponibilidad(turno.getVeterinario(), nuevaFechaHora, turno.getDuracionMin());
        turnos.add(turno);
        if (!hayLugar) {
            return false;
        }
        turno.reprogramar(nuevaFechaHora);
        return true;
    }

    public boolean cancelar(Turno turno) {
        return turno.cancelar(LocalDateTime.now());
    }

    public void registrarAusencia(Turno turno) {
        turno.marcarAusente();
    }

    public boolean hayDisponibilidad(Veterinario veterinario, LocalDateTime inicio, int minutos) {
        if (!veterinario.atiendeEn(inicio, minutos)) {
            return false;
        }
        LocalDateTime fin = inicio.plusMinutes(minutos);
        // Para cada turno busca los que sean del veterinario y programados/en curso
        // Y si se solapa con el rango deseado, devuelve false
        for (Turno t : turnos) {
            if ((t.getEstado().equals(EstadoTurno.PROGRAMADO) || t.getEstado().equals(EstadoTurno.EN_CURSO))
                && t.getVeterinario().getDocumento().equals(veterinario.getDocumento())) {
                if (inicio.isBefore(t.getFin()) && t.getFechaHora().isBefore(fin)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Turno> getTurnosDe(Veterinario veterinario, LocalDate desde, LocalDate hasta) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : turnos) {
            if (t.getVeterinario().getDocumento().equals(veterinario.getDocumento())) {
                // Valida que el turno entre dentro de los parametros solicitados
                LocalDate fechaTurno = t.getFechaHora().toLocalDate();
                boolean despuesDeOIgual = !fechaTurno.isBefore(desde);
                boolean antesDeOIgual = (hasta == null) || !fechaTurno.isAfter(hasta);
                if (despuesDeOIgual && antesDeOIgual) {
                    resultado.add(t);
                }
            }
        }
        Collections.sort(resultado);
        return resultado;
    }

    public Turno getTurnoSiguiente(Veterinario veterinario, LocalDateTime despuesDe) {
        // Se queda con el de menor horario entre todos los que cumplen la condición
        Turno siguiente = null;
        for (Turno t : turnos) {
            if ((t.getEstado().equals(EstadoTurno.PROGRAMADO) || t.getEstado().equals(EstadoTurno.EN_CURSO))
                && t.getVeterinario().getDocumento().equals(veterinario.getDocumento())
                && t.getFechaHora().isAfter(despuesDe)) {
                // Quedarse con el más temprano
                if (siguiente == null || t.compareTo(siguiente) < 0) {
                    siguiente = t;
                }
            }
        }
        return siguiente;
    }

    public Turno getTurnoActual(Veterinario veterinario) {
        LocalDateTime ahora = LocalDateTime.now();
        for (Turno t : turnos) {
            // Solo buscar turnos programados/en curso
            if ((t.getEstado().equals(EstadoTurno.PROGRAMADO) || t.getEstado().equals(EstadoTurno.EN_CURSO))) {
                if (t.getVeterinario().getDocumento().equals(veterinario.getDocumento())) {
                    // El turno está en curso si ahora está entre su inicio y su fin
                    if (!ahora.isBefore(t.getFechaHora()) && !ahora.isAfter(t.getFin())) {
                        return t;
                    }
                }
            }
        }
        return null;
    }

    public Turno buscarTurno(String idTurno) {
        for (Turno t : turnos) {
            if (t.getId().equals(idTurno)) {
                return t;
            }
        }
        return null;
    }
}
