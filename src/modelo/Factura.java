package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Factura {
    private int numero;
    private LocalDate fecha;
    private EstadoFactura estado;
    private double seniaDescontada;
    private Consulta consulta;
    private Duenio duenio;
    private Tarifario tarifario;
    private MedioPago medioPago;
    private NotaCredito notaCredito;
    private final List<LineaFactura> lineas = new ArrayList<>();

    public void agregarLinea(String descripcion, int cantidad, double precioUnitario) {
        System.out.println("Factura.agregarLinea");
    }

    public double calcularSubtotal() {
        System.out.println("Factura.calcularSubtotal");
        return 0.0;
    }

    public double calcularTotal() {
        System.out.println("Factura.calcularTotal");
        return 0.0;
    }

    public NotaCredito anular(String motivo, Recepcionista emisor) {
        System.out.println("Factura.anular");
        return null;
    }

    public boolean estaAnulada() {
        System.out.println("Factura.estaAnulada");
        return false;
    }
}
