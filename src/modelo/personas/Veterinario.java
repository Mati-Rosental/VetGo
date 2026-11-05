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

    public boolean esDeGuardia(LocalDate dia) {
        DayOfWeek d = dia.getDayOfWeek();
        for (HorarioAtencion h : horarios) {
            if (h.getDia().equals(d) && h.esDeGuardia()) {
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

    public LocalDateTime proximaApertura(LocalDateTime inicio) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'proximaApertura'");
    }
}
