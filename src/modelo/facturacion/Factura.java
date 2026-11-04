package modelo.facturacion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import modelo.atencion.Consulta;
import modelo.enumeradores.EstadoFactura;
import modelo.facturacion.pago.MedioPago;
import modelo.personas.Duenio;
import modelo.personas.Recepcionista;

public class Factura implements Serializable {
    private String numero;
    private LocalDate fecha;
    private EstadoFactura estado;
    private double seniaDescontada;
    private Consulta consulta;
    private Duenio duenio;
    private Tarifario tarifario;
    private MedioPago medioPago;
    private NotaCredito notaCredito;
    private final List<LineaFactura> lineas = new ArrayList<>();

    

    public Factura(Consulta consulta, Duenio duenio, Tarifario tarifario, MedioPago medioPago) {
        this.numero = UUID.randomUUID().toString();
        fecha = LocalDate.now();
        this.consulta = consulta;
        this.duenio = duenio;
        this.tarifario = tarifario;
        this.medioPago = medioPago;
    }

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

    public void anular(String motivo, Recepcionista emisor) {
        System.out.println("Factura.anular");
    }

    public boolean estaAnulada() {
        System.out.println("Factura.estaAnulada");
        return false;
    }

    public String getNumero() {
        return numero;
    }
}
