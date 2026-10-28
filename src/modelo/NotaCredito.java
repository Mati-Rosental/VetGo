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
        System.out.println("NotaCredito.getImporte");
        return 0.0;
    }
}
