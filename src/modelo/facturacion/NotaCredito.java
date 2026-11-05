package modelo.facturacion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import modelo.personas.Recepcionista;

public class NotaCredito implements Serializable {
    private String numero;
    private LocalDate fecha;
    private String motivo;
    private double importe;
    private Factura factura;
    private Recepcionista emisor;

    public NotaCredito(Factura factura, String motivo, Recepcionista emisor) {
        this.numero = UUID.randomUUID().toString();
        this.factura = factura;
        this.motivo = motivo;
        this.emisor = emisor;
        this.fecha = LocalDate.now();
        this.importe = factura.getTotal();
    }

    public double getImporte() {
        return importe;
    }
}
