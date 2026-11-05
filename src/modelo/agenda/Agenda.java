package modelo.agenda;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.enumeradores.EstadoTurno;
import modelo.enumeradores.TipoAtencion;
import modelo.mascotas.Mascota;
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
        LocalDateTime viejoInicio = turno.getFechaHora();
        turno.reprogramar(nuevaFechaHora);
        if (hayDisponibilidad(turno.getVeterinario(), nuevaFechaHora, turno.getDuracionMin())) {
            return true;
        }
        turno.reprogramar(viejoInicio); // rollback si no hay lugar
        return false;
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
        for (Turno t : turnos) {
            if (!t.getEstado().equals(EstadoTurno.CANCELADO) && t.getVeterinario().getDocumento().equals(veterinario.getDocumento())) {
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
        Turno siguiente = null;
        for (Turno t : turnos) {
            if (t.getVeterinario().getDocumento().equals(veterinario.getDocumento()) && t.getFechaHora().isAfter(despuesDe)) {
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
            if (!t.getEstado().equals(EstadoTurno.CANCELADO) && !t.getEstado().equals(EstadoTurno.AUSENTE)) {
                if (t.getVeterinario().getDocumento().equals(veterinario.getDocumento())) {
                    if (!ahora.isBefore(t.getFechaHora()) && !ahora.isAfter(t.getFin())) {
                        return t;
                    }
                }
            }
        }
        return null;
    }

    public LocalDateTime primerHorarioLibre(Veterinario veterinario, LocalDateTime desde, int minutos) {
        LocalDateTime cursor = desde;
        for (int i = 0; i < 30; i++) { // límite de días
            if (hayDisponibilidad(veterinario, cursor, minutos)) {
                return cursor;
            }
            cursor = cursor.plusMinutes(15);
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
