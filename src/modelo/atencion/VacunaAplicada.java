package modelo.atencion;

import java.io.Serializable;
import java.time.LocalDate;

import modelo.inventario.Vacuna;
import modelo.mascotas.Mascota;

public class VacunaAplicada implements Serializable {
    private LocalDate fechaAplicacion;
    private String numeroLote;
    private LocalDate proximoRefuerzo;
    private Vacuna vacuna;

    public VacunaAplicada(Vacuna vacuna, String numeroLote, LocalDate fechaAplicacion) {
        this.vacuna = vacuna;
        this.numeroLote = numeroLote;
        this.fechaAplicacion = fechaAplicacion;
        this.proximoRefuerzo = vacuna.calcularProximoRefuerzo(fechaAplicacion);
    }

    public LocalDate getProximoRefuerzo() {
        return proximoRefuerzo;
    }

    public boolean requiereRecordatorio(LocalDate fechaActual) {
        if (proximoRefuerzo == null) return false;
        LocalDate inicioAviso = proximoRefuerzo.minusDays(7);
        return !fechaActual.isBefore(inicioAviso) && !fechaActual.isAfter(proximoRefuerzo); // RN-V2
    }

    public boolean estaVencida(LocalDate fechaActual) {
        if (proximoRefuerzo == null) return false;
        return fechaActual.isAfter(proximoRefuerzo.plusDays(30));
    }

    public Vacuna getVacuna() {
        return vacuna;
    }
}
