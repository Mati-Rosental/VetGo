package modelo.atencion;

import java.io.Serializable;
import java.time.LocalDate;

import modelo.inventario.Vacuna;

public class VacunaAplicada implements Serializable {
    private LocalDate fechaAplicacion;
    private String numeroLote;
    private LocalDate proximoRefuerzo;
    private Vacuna vacuna;

    public VacunaAplicada(Vacuna vacuna, String numeroLote, LocalDate fechaAplicacion) {
        this.vacuna = vacuna;
        this.numeroLote = numeroLote;
        this.fechaAplicacion = fechaAplicacion;

        // Calcula y guarda la fecha del próximo refuerzo según la vacuna aplicada
        this.proximoRefuerzo = vacuna.calcularProximoRefuerzo(fechaAplicacion);
    }

    public LocalDate getProximoRefuerzo() {
        return proximoRefuerzo;
    }

    
    public boolean requiereRecordatorio(LocalDate fechaActual) {
        if (proximoRefuerzo == null) return false;
        // Indica si corresponde enviar un recordatorio durante los 7 días previos al refuerzo
        LocalDate inicioAviso = proximoRefuerzo.minusDays(7);
        return !fechaActual.isBefore(inicioAviso) && !fechaActual.isAfter(proximoRefuerzo);
    }

    
    public boolean estaVencida(LocalDate fechaActual) {
        if (proximoRefuerzo == null) return false;
        // Indica si el refuerzo lleva más de 30 días vencido.
        return fechaActual.isAfter(proximoRefuerzo.plusDays(30));
    }

    public Vacuna getVacuna() {
        return vacuna;
    }
}