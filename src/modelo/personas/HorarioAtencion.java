package modelo.personas;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;

public class HorarioAtencion implements Serializable {
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean deGuardia;

    public boolean cubreTramoHorario(LocalTime horaInicio, LocalTime horaFin) {
        return !horaInicio.isBefore(this.horaInicio) && !horaFin.isAfter(this.horaFin);
    }

    public boolean esDeGuardia() {
        return deGuardia;
    }

    public void setDeGuardia(boolean deGuardia) {
        this.deGuardia = deGuardia;
    }

    public HorarioAtencion(DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin, boolean deGuardia) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.deGuardia = deGuardia;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public DayOfWeek getDia() {
        return dia;
    }
}
