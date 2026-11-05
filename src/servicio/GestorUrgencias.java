package servicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.enumeradores.TipoAtencion;
import modelo.mascotas.Mascota;
import modelo.personas.Veterinario;

public class GestorUrgencias {
    private final Agenda agenda;
    private final Notificador notificador;

    public GestorUrgencias(Agenda agenda, Notificador notificador) {
        this.agenda = agenda;
        this.notificador = notificador;
    }

    public Turno atenderUrgencia(Mascota mascota, Veterinario veterinario) {
        if (mascota == null || veterinario == null) {
            return null;
        }

        // Determinar el horario de comienzo
        Turno turnoActual = agenda.getTurnoActual(veterinario);
        // Si el doctor esta atendiendo actualmente, la urgencia comienza cuando ese turno termina
        // Sino, comienza ahora
        LocalDateTime horarioUrgencia =
        turnoActual != null ? turnoActual.getFin() : LocalDateTime.now();

        Turno turnoUrgencia = new Turno(horarioUrgencia, TipoAtencion.URGENCIA, mascota, veterinario);

        // Desplazar los turnos que se superpongan con la urgencia.
        if (!desplazarEnCascada(turnoUrgencia)) {
            return null;
        }

        if (!agenda.asignarTurno(turnoUrgencia)) {
            return null;
        }

        return turnoUrgencia;
    }

    private boolean desplazarEnCascada(Turno turnoUrgencia) {
        Veterinario vet = turnoUrgencia.getVeterinario();

        // Juntar los turnos que hay que mover en orden cronológico
        List<Turno> turnosAfectados = new ArrayList<>();
        List<LocalDateTime> horariosOriginales = new ArrayList<>();
        List<LocalDateTime> nuevosHorarios = new ArrayList<>();

        LocalDateTime finActual = turnoUrgencia.getFin();
        // Se busca desde un instante antes para incluir un turno que empiece justo al inicio de la urgencia
        Turno siguiente = agenda.getTurnoSiguiente(vet, turnoUrgencia.getFechaHora().minusNanos(1));
        
        // Mientras haya un siguiente turno y este empiece antes de que la cadena actual termine
        while (siguiente != null && siguiente.getFechaHora().isBefore(finActual)) {
            int duracion = siguiente.getDuracionMin();

            LocalDateTime inicio = finActual;
            LocalDateTime fin = inicio.plusMinutes(duracion);

            // Si no entra en la jornada del veterinario, pasa a la próxima apertura
            if (!vet.atiendeEn(inicio, duracion)) {
                inicio = vet.proximaApertura(inicio);
                fin = inicio.plusMinutes(duracion);
            }

            turnosAfectados.add(siguiente);
            horariosOriginales.add(siguiente.getFechaHora());
            nuevosHorarios.add(inicio);

            // La cadena ahora termina cuando termina este turno desplazado
            finActual = fin;

            // El siguiente se busca a partir del horario original del turno afectado
            siguiente = agenda.getTurnoSiguiente(vet, siguiente.getFechaHora());
        }

        // Reprogramar desde el último hasta el primero
        for (int i = turnosAfectados.size() - 1; i >= 0; i--) {
            if (!agenda.reprogramar(turnosAfectados.get(i), nuevosHorarios.get(i))) {
                return false;
            }
        }

        // Notificar a los dueños
        notificarDesplazados(turnosAfectados, horariosOriginales);

        return true;
    }

    private void notificarDesplazados(List<Turno> turnosAfectados, List<LocalDateTime> horariosOriginales) {
        DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = 0; i < turnosAfectados.size(); i++) {
            Turno turno = turnosAfectados.get(i);
            Mascota mascota = turno.getMascota();

            String mensaje = "El turno de " + mascota.getNombre()
                    + " del " + horariosOriginales.get(i).format(FORMATO)
                    + " fue reprogramado para el " + turno.getFechaHora().format(FORMATO)
                    + " por una urgencia.";

            notificador.notificar(mascota.getDuenio(), mensaje);
        }
    }
}