package modelo.facturacion;

import java.io.Serializable;
import java.time.LocalDate;

import modelo.personas.Recepcionista;

public class NotaCredito implements Serializable {
    private int numero;
    private LocalDate fecha;
    private String motivo;
    private double importe;
    private Factura factura;
    private Recepcionista emisor;

    public double getImporte() {
        return importe;
    }

    public NotaCredito(int numero, Factura factura, String motivo, Recepcionista emisor) {
        this.numero = numero;
        this.factura = factura;
        this.motivo = motivo;
        this.emisor = emisor;
        this.fecha = LocalDate.now();
        this.importe = factura.calcularTotal();
    }
}
