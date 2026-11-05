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

    public Factura(String numero, LocalDate fecha, EstadoFactura estado, double seniaDescontada, Consulta consulta, Duenio duenio, Tarifario tarifario, MedioPago medioPago) {
        this.numero = UUID.randomUUID().toString();
        this.fecha = LocalDate.now();
        this.estado = EstadoFactura.EMITIDA; // <--- Agregar esto
        this.consulta = consulta;
        this.duenio = duenio;
        this.tarifario = tarifario;
        this.medioPago = medioPago;
        this.seniaDescontada = 0.0; // O la lógica de la seña pendiente del dueño
    }

    public Factura(Consulta consulta, Duenio duenio, Tarifario tarifario, MedioPago medioPago) {
        this.numero = UUID.randomUUID().toString();
        fecha = LocalDate.now();
        this.consulta = consulta;
        this.duenio = duenio;
        this.tarifario = tarifario;
        this.medioPago = medioPago;
    }

    public void agregarLinea(String descripcion, int cantidad, double precioUnitario) {
        lineas.add(new LineaFactura(descripcion, cantidad, precioUnitario));
    }

    public double calcularSubtotal() {
        double sub = 0;
        for (LineaFactura lf : lineas) {
            sub += lf.getSubtotal();
        }
        return sub;
    }

    public double calcularTotal() {
        System.out.println("Factura.calcularTotal");
        return 0.0;
    }

    public void anular(String motivo, Recepcionista emisor) {
        this.estado = EstadoFactura.ANULADA;
        this.notaCredito = new NotaCredito(0, this, motivo, emisor);
    }

    public boolean estaAnulada() {
        return this.estado.equals(EstadoFactura.ANULADA);
    }

    public String getNumero() {
        return numero;
    }
}
