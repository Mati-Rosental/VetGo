package modelo.personas;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        System.out.println("Veterinario.atiendeEn");
        return false;
    }

    public boolean esDeGuardia(LocalDate dia) {
        System.out.println("Veterinario.esDeGuardia");
        return false;
    }

    @Override
    public String getRol() {
        return "Veterinario";
    }

    public void modificarDatos(String nombre, String apellido, String telefono,
         String especialidad, List<HorarioAtencion> horarios) {
        // Si alguna variable es null, no hay que modificarla
        System.out.println("Veterinario.modificarDatos");
    }

    public LocalDateTime proximaApertura(LocalDateTime inicio) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'proximaApertura'");
    }
}
