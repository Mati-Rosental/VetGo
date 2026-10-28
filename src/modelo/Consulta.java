package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Consulta {
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

    public void aplicarVacuna(Vacuna vacuna, String numeroLote, java.time.LocalDate fechaAplicacion) {
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
}
