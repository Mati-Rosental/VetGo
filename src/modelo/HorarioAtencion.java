package modelo;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class HorarioAtencion {
    private DayOfWeek dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean deGuardia;

    public boolean cubreTramoHorario(LocalTime horaInicio, LocalTime horaFin) {
        System.out.println("HorarioAtencion.cubreTramoHorario");
        return false;
    }

    public boolean esDeGuardia() {
        System.out.println("HorarioAtencion.esDeGuardia");
        return false;
    }

    public void setDeGuardia(boolean deGuardia) {
        System.out.println("HorarioAtencion.setDeGuardia");
    }

    public HorarioAtencion(DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin, boolean deGuardia) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.deGuardia = deGuardia;
    }
}
