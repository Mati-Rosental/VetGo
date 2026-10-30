package modelo;

import java.rmi.MarshalException;
import java.time.LocalDateTime;

public class Turno implements Comparable<Turno> {
    private LocalDateTime fechaHora;
    private TipoAtencion tipo;
    private EstadoTurno estado;
    private boolean urgencia;
    private Mascota mascota;
    private Veterinario veterinario;
    private Consulta consulta;

    public int getDuracionMin() {
        System.out.println("Turno.getDuracionMin");
        return 0;
    }

    public LocalDateTime getFin() {
        System.out.println("Turno.getFin");
        return null;
    }

    public boolean seSuperpone(Turno otro) {
        System.out.println("Turno.seSuperpone");
        return false;
    }

    public boolean cancelar(LocalDateTime ahora) {
        System.out.println("Turno.cancelar");
        return false;
    }

    public void marcarAusente() {
        System.out.println("Turno.marcarAusente");
    }

    public void marcarAtendido() {
        System.out.println("Turno.marcarAtendido");
    }

    public void reprogramar(LocalDateTime nuevaFechaHora) {
        System.out.println("Turno.reprogramar");
    }

    @Override
    public int compareTo(Turno otro) {
        System.out.println("Turno.compareTo");
        return 0;
    }

    public TipoAtencion getTipoAtencion() {
        System.out.println("Turno.getTipoAtencion");
        return null;
    }

    public Mascota getMascota() {
        return null;
    }
}
