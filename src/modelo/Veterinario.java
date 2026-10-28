package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Veterinario extends Persona {
    private String matricula;
    private String especialidad;
    private final List<HorarioAtencion> horarios = new ArrayList<>();

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
        System.out.println("Veterinario.getRol");
        return null;
    }
}
