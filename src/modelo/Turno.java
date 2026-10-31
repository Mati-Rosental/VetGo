package modelo;

import java.rmi.MarshalException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Turno implements Comparable<Turno> {
    private final String id;
    private LocalDateTime fechaHora;
    private TipoAtencion tipo;
    private EstadoTurno estado;
    private boolean urgencia;
    private Mascota mascota;
    private Veterinario veterinario;
    private Consulta consulta;

    

    public Turno(LocalDateTime fechaHora, EstadoTurno estado, boolean urgencia, Mascota mascota,
            Veterinario veterinario, Consulta consulta) {
        this.id = UUID.randomUUID().toString();
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.urgencia = urgencia;
        this.mascota = mascota;
        this.veterinario = veterinario;
        this.consulta = consulta;
    }

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
        // Cambiar el estado del turno
        this.estado = EstadoTurno.AUSENTE;

        // Registrar la ausencia del dueño
        this.mascota.getDuenio().registrarAusencia();
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
        System.out.println("Turno.getMascota");
        return null;
    }

    public Consulta getConsulta() {
        System.out.println("Turno.getConsulta");
        return null;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }
}
