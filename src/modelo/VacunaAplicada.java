package modelo;

import java.time.LocalDate;

public class VacunaAplicada {
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
}
