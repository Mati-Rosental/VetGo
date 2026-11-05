package modelo.atencion;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import modelo.agenda.Turno;
import modelo.enumeradores.EstadoConsulta;
import modelo.inventario.Producto;
import modelo.inventario.Vacuna;
import modelo.mascotas.Mascota;
import modelo.personas.Veterinario;

public class Consulta implements Serializable {
    private LocalDateTime fecha;
    private String motivo;
    private Double pesoDia;
    private String diagnostico;
    private String observaciones;
    private EstadoConsulta estado;
    private Turno turno;
    private Veterinario veterinario;
    private List<Tratamiento> tratamientos = new ArrayList<>();
    private List<VacunaAplicada> vacunasAplicadas = new ArrayList<>();
    private List<NotaRectificatoria> notasRectificatorias = new ArrayList<>();
    private final String id;

    public void registrarPeso(double peso) {
        this.pesoDia = peso;
        if (turno != null && turno.getMascota() != null) {
            turno.getMascota().actualizarPeso(peso); // S-23
        }
    }

    public void indicarTratamiento(
            Producto medicamento,
            String dosis,
            String frecuencia,
            int duracionDias,
            int cantidad) {
        if (pesoDia != null) {
            tratamientos.add(new Tratamiento(dosis, frecuencia, duracionDias, cantidad, medicamento)); // RN-M5
        }
    }

    public void aplicarVacuna(Vacuna vacuna) {
        Mascota m = turno.getMascota();
        int semanas = m.edadEnSemanas(LocalDate.now());
        if (vacuna.aplicaA(m.getEspecie()) && vacuna.permiteEdad(semanas)) {
            vacunasAplicadas.add(new VacunaAplicada(vacuna, vacuna.getNumeroPrimerLote(), LocalDate.now()));
        }
    }

    public void agregarNota(String texto, Veterinario firmante) {
        notasRectificatorias.add(new NotaRectificatoria(texto, firmante));
    }

    public void confirmar() {
        this.estado = EstadoConsulta.CONFIRMADA;
    }

    public boolean estaConfirmada() {
        return this.estado.equals(EstadoConsulta.CONFIRMADA);
    }

    public Map<Producto, Integer> getInsumosUtilizados() {
        Map<Producto, Integer> insumos = new HashMap<>();
        for (Tratamiento t : tratamientos) {
            insumos.merge(t.getMedicamento(), t.getCantidad(), Integer::sum);
        }
        for (VacunaAplicada v : vacunasAplicadas) {
            insumos.merge(v.getVacuna(), 1, Integer::sum);
        }
        return insumos;
    }

    public Turno getTurno() {
        return turno;
    }

    public Consulta(Turno turno, Veterinario veterinario, String motivo) {
        this.turno = turno;
        this.veterinario = veterinario;
        this.motivo = motivo;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoConsulta.EN_CURSO;
        this.id = UUID.randomUUID().toString();
        this.pesoDia = null;
    }
    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public List<VacunaAplicada> getVacunasAplicadas() {
        return vacunasAplicadas;
    }

    @Override
    public String toString() {
        return "Consulta [ID=" + id + ", Fecha=" + fecha + ", Motivo=" + motivo + ", Estado=" + estado + "]";
    }
    public String getId() {
        return id;
    }
}
