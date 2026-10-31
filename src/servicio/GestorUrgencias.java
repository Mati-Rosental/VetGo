package servicio;

import modelo.*;
import persistencia.*;
import java.time.LocalDate;

public class GestorUrgencias {
    private Agenda agenda;
    private Notificador notificador;

    public Turno atenderUrgencia(Mascota mascota, Veterinario veterinario) {
        System.out.println("GestorUrgencias.atenderUrgencia");
        return null;
    }

    private void desplazarEnCascada(Turno turno) {
        System.out.println("GestorUrgencias.desplazarEnCascada");
    }
}
