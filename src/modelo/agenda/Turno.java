package modelo.agenda;

import java.io.Serializable;
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
        return tipo.getDuracionMin();
    }

    public LocalDateTime getFin() {
        return fechaHora.plusMinutes(getDuracionMin());
    }

    public boolean seSuperpone(Turno otro) {
        if ((estado != EstadoTurno.PROGRAMADO && estado != EstadoTurno.EN_CURSO)
            || (otro.getEstado() != EstadoTurno.PROGRAMADO && otro.getEstado() != EstadoTurno.EN_CURSO)) {
            return false;
        }
        if (!this.veterinario.getDocumento().equals(otro.veterinario.getDocumento())) {
            return false;
        }
        return this.fechaHora.isBefore(otro.getFin()) && otro.fechaHora.isBefore(this.getFin());
    }

    public boolean cancelar(LocalDateTime ahora) {
        this.estado = EstadoTurno.CANCELADO;
        // Si faltan menos de 2 horas hay cargo
        return java.time.Duration.between(ahora, this.fechaHora).toHours() < 2;
    }

    public void marcarAusente() {
        // Cambiar el estado del turno
        this.estado = EstadoTurno.AUSENTE;

        // Registrar la ausencia del dueño
        this.mascota.getDuenio().registrarAusencia();
    }
    public void marcarEnCurso() {
        this.estado = EstadoTurno.EN_CURSO;
    }
    public void marcarAtendido() {
        this.estado = EstadoTurno.ATENDIDO;
    }

    public void reprogramar(LocalDateTime nuevaFechaHora) {
        this.fechaHora = nuevaFechaHora;
    }

    @Override
    public int compareTo(Turno otro) {
        return this.fechaHora.compareTo(otro.fechaHora);
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
        return "Turno [ID=" + id + ", Fecha=" + fechaHora + ", Tipo=" + tipo + ", Estado=" + estado +
                ", Mascota=" + mascota.getNombre() + ", Veterinario=" + veterinario.getNombreCompleto() + "]";
    }
    public String getId() {
        return id;
    }
    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public EstadoTurno getEstado() {
        return estado;
    }
}
