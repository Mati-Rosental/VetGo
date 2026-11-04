package modelo.atencion;

import java.io.Serializable;
import java.time.LocalDate;

import modelo.inventario.Vacuna;

public class VacunaAplicada implements Serializable {
    private LocalDate fechaAplicacion;
    private String numeroLote;
    private LocalDate proximoRefuerzo;
    private Vacuna vacuna;

    public void calcularProximoRefuerzo() {
        System.out.println("VacunaAplicada.calcularProximoRefuerzo");
    }

    public boolean requiereRecordatorio(LocalDate fechaActual) {
        System.out.println("VacunaAplicada.requiereRecordatorio");
        return false;
    }

    public boolean estaVencida(LocalDate fechaActual) {
        System.out.println("VacunaAplicada.estaVencida");
        return false;
    }

    public VacunaAplicada(Vacuna vacuna, String numeroLote, LocalDate fechaAplicacion) {
        this.vacuna = vacuna;
        this.numeroLote = numeroLote;
        this.fechaAplicacion = fechaAplicacion;
        calcularProximoRefuerzo();
    }
}
