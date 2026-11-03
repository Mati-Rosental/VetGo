package modelo;

import java.time.LocalDate;

public class NotaCredito {
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
