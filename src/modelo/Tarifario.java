package modelo;

import java.util.Map;

public class Tarifario {
    private Map<TipoAtencion, Double> precios;
    private double montoSenia;
    private double porcCancelacionTardia;

    public double getPrecio(TipoAtencion tipo) {
        System.out.println("Tarifario.getPrecio");
        return 0.0;
    }

    public double getMontoSenia() {
        System.out.println("Tarifario.getMontoSenia");
        return 0.0;
    }

    public double getPorcCancelacionTardia() {
        System.out.println("Tarifario.getPorcCancelacionTardia");
        return 0.0;
    }

    public double calcularCargoCancelacionTardia() {
        System.out.println("Tarifario.calcularCargoCancelacionTardia");
        return 0.0;
    }
}
