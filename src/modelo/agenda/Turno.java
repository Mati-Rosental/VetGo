package modelo.agenda;

import java.io.Serializable;
import java.rmi.MarshalException;
import java.time.LocalDateTime;
import java.util.UUID;

import modelo.atencion.Consulta;
import modelo.enumeradores.EstadoTurno;
import modelo.enumeradores.TipoAtencion;
import modelo.mascotas.Mascota;
import modelo.personas.Veterinario;

public class Turno implements Comparable<Turno>, Serializable {
    private final String id;
    private LocalDateTime fechaHora;
    private TipoAtencion tipo;
    private EstadoTurno estado;
    private Mascota mascota;
    private Veterinario veterinario;
    private Consulta consulta;



    public Turno(LocalDateTime fechaHora, TipoAtencion tipo,
                 Mascota mascota, Veterinario veterinario) {
        this.id = UUID.randomUUID().toString();
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.estado = EstadoTurno.PROGRAMADO;
        this.mascota = mascota;
        this.veterinario = veterinario;
        this.consulta = null;
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
        return tipo;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public String getDescripcion() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescripcion'");
    }
}
