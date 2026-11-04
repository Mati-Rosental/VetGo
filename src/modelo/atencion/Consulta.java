package modelo.atencion;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import modelo.agenda.Turno;
import modelo.enumeradores.EstadoConsulta;
import modelo.inventario.Producto;
import modelo.inventario.Vacuna;
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
    private final List<Tratamiento> tratamientos = new ArrayList<>();
    private final List<VacunaAplicada> vacunasAplicadas = new ArrayList<>();
    private final List<NotaRectificatoria> notasRectificatorias = new ArrayList<>();

    public void registrarPeso(double peso) {
        System.out.println("Consulta.registrarPeso");
    }

    public void indicarTratamiento(
            Producto medicamento,
            String dosis,
            String frecuencia,
            int duracionDias,
            int cantidad) {
        System.out.println("Consulta.indicarTratamiento");
    }

    public void aplicarVacuna(Vacuna vacuna) {
        System.out.println("Consulta.aplicarVacuna");
    }

    public void agregarNota(String texto, Veterinario firmante) {
        System.out.println("Consulta.agregarNota");
    }

    public void confirmar() {
        System.out.println("Consulta.confirmar");
    }

    public boolean estaConfirmada() {
        System.out.println("Consulta.estaConfirmada");
        return false;
    }

    public Map<Producto, Integer> getInsumosUtilizados() {
        System.out.println("Consulta.getInsumosUtilizados");
        return null;
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
        this.pesoDia = null;
    }
}
