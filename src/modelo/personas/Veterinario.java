package modelo.personas;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Veterinario extends Persona {
    private String matricula;
    private String especialidad;
    private List<HorarioAtencion> horarios = new ArrayList<>();

    public Veterinario(String nombre, String apellido, String documento, String telefono,
                       String matricula, String especialidad, List<HorarioAtencion> horarios) {
        super(nombre, apellido, documento, telefono);
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.horarios = horarios;
    }

    public String getMatricula() {
        return matricula;
    }

    public boolean atiendeEn(LocalDateTime inicio, int minutos) {
        DayOfWeek dia = inicio.getDayOfWeek();
        LocalTime horaInicioTurno = inicio.toLocalTime();
        LocalTime horaFinTurno = horaInicioTurno.plusMinutes(minutos);

        // Si cruza la medianoche, rechazar
        if (horaFinTurno.isBefore(horaInicioTurno)) {
            return false;
        }

        for (HorarioAtencion h : horarios) {
            if (h.getDia().equals(dia)) {
                if (h.cubreTramoHorario(horaInicioTurno, horaFinTurno)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Está de guardia si hay un bloque de guardia de ese día que cubre la hora indicada
    public boolean esDeGuardia(LocalDateTime momento) {
        DayOfWeek d = momento.getDayOfWeek();
        LocalTime hora = momento.toLocalTime();
        for (HorarioAtencion h : horarios) {
            if (h.getDia().equals(d) && h.esDeGuardia() && h.cubreTramoHorario(hora, hora)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRol() {
        return "Veterinario";
    }

    public void modificarDatos(String nombre, String apellido, String telefono,
         String especialidad, List<HorarioAtencion> horarios) {
        // Si alguna variable es null, no hay que modificarla
        if (nombre != null && !nombre.isBlank()) this.nombre = nombre;
        if (apellido != null && !apellido.isBlank()) this.apellido = apellido;
        if (telefono != null && !telefono.isBlank()) this.telefono = telefono;
        if (especialidad != null && !especialidad.isBlank()) this.especialidad = especialidad;
        if (horarios != null && !horarios.isEmpty()) this.horarios = horarios;
    }

    // Devuelve la primera apertura disponible a partir del día siguiente al indicado
    public LocalDateTime proximaApertura(LocalDateTime inicio) {
        // Comienza la búsqueda desde el día siguiente
        LocalDate diaBusqueda = inicio.toLocalDate().plusDays(1);

        // Busca una apertura dentro de los próximos 7 días
        for (int i = 0; i < 7; i++) {
            DayOfWeek diaSemana = diaBusqueda.getDayOfWeek();
            LocalTime primeraApertura = null;

            // Busca el horario de inicio más temprano para ese día
            for (HorarioAtencion h : horarios) {
                if (h.getDia().equals(diaSemana)) {
                    if (primeraApertura == null || h.getHoraInicio().isBefore(primeraApertura)) {
                        primeraApertura = h.getHoraInicio();
                    }
                }
            }

            // Si encontró un horario, devuelve la fecha y hora de apertura
            if (primeraApertura != null) {
                return LocalDateTime.of(diaBusqueda, primeraApertura);
            }

            // Avanza al siguiente día
            diaBusqueda = diaBusqueda.plusDays(1);
        }

        // No hay horarios de atención configurados
        return null;
    }

    public String getEspecialidad() {
        return especialidad;
    }
}