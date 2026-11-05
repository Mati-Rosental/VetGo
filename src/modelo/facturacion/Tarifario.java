package modelo.facturacion;

import java.io.Serializable;
import java.util.Map;

import modelo.enumeradores.TipoAtencion;

public class Tarifario implements Serializable {
    private Map<TipoAtencion, Double> precios;
    private double montoSenia;
    private double porcCancelacionTardia;

    public Tarifario(Map<TipoAtencion, Double> precios, double montoSenia, double porcCancelacionTardia) {
        this.precios = precios;
        this.montoSenia = montoSenia;
        this.porcCancelacionTardia = porcCancelacionTardia;
    }

    public double getPrecio(TipoAtencion tipo) {
        // Si el tipo de atencion no esta registrado, devuelve 0
        return precios != null ? precios.getOrDefault(tipo, 0.0) : 0.0;
    }

    public double getMontoSenia() {
        return montoSenia;
    }

    public double getPorcCancelacionTardia() {
        return porcCancelacionTardia;
    }

    public double calcularCargoCancelacionTardia() {
        return precios.get(TipoAtencion.CONSULTA_GENERAL) * porcCancelacionTardia;
    }
}
