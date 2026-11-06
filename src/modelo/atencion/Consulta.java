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
    private List<Tratamiento> tratamientos = new ArrayList<>();
    private List<VacunaAplicada> vacunasAplicadas = new ArrayList<>();
    private List<NotaRectificatoria> notasRectificatorias = new ArrayList<>();
    private final String id;

    public Consulta(Turno turno, String motivo) {
        this.turno = turno;
        turno.setConsulta(this);
        this.motivo = motivo;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoConsulta.EN_CURSO;
        this.id = UUID.randomUUID().toString();
        this.pesoDia = null;
    }

    public String getId() {
        return id;
    }

    public void registrarPeso(double peso) {
        this.pesoDia = peso;
        turno.getMascota().actualizarPeso(peso);
    }

    public void registrarDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public void registrarObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean indicarTratamiento(Producto medicamento, String dosis, String frecuencia, int duracionDias, int cantidad) {
        if (estaConfirmada() || pesoDia == null || medicamento == null) {
            return false;
        }

        tratamientos.add(new Tratamiento(dosis, frecuencia, duracionDias, cantidad, medicamento));
        return true;
    }

    // Devuelve true si la vacuna se registró, false si no corresponde aplicarla
    public boolean aplicarVacuna(Vacuna vacuna) {
        Mascota m = turno.getMascota();
        int semanas = m.edadEnSemanas();
        // Registra la vacuna solo si es para la especie de la mascota y si tiene la edad mínima
        if (vacuna.aplicaA(m.getEspecie()) && vacuna.permiteEdad(semanas)) {
            vacunasAplicadas.add(new VacunaAplicada(vacuna, vacuna.getNumeroPrimerLote(), LocalDate.now()));
            return true;
        }
        return false;
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

    public void marcarFacturada() {
        this.estado = EstadoConsulta.FACTURADA;
    }

    public boolean estaFacturada() {
        return this.estado.equals(EstadoConsulta.FACTURADA);
    }

    public boolean estaEnCurso() {
        return this.estado.equals(EstadoConsulta.EN_CURSO);
    }

    // Junta los productos usados en la consulta con la cantidad total de cada uno
    public Map<Producto, Integer> getInsumosUtilizados() {
        Map<Producto, Integer> insumos = new HashMap<>();
        // Medicamentos de los tratamientos, sumando si se repite el producto
        for (Tratamiento t : tratamientos) {
            insumos.merge(t.getMedicamento(), t.getCantidad(), Integer::sum);
        }
        // Cada vacuna aplicada consume una unidad
        for (VacunaAplicada v : vacunasAplicadas) {
            insumos.merge(v.getVacuna(), 1, Integer::sum);
        }
        return insumos;
    }

    public Turno getTurno() {
        return turno;
    }

    public List<Tratamiento> getTratamientos() {
        return tratamientos;
    }

    public List<VacunaAplicada> getVacunasAplicadas() {
        return vacunasAplicadas;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + "\nTurno: " + turno.getId()
                + "\nMascota: " + turno.getMascota().getNombre()
                + "\nDueño: " + turno.getMascota().getDuenio().getNombreCompleto()
                + "\nFecha y hora: " + turno.getFechaHora()
                + "\nTipo de atención: " + turno.getTipoAtencion();
    }
}