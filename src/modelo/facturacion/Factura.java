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
    private MedioPago medioPago;
    private NotaCredito notaCredito;
    private List<LineaFactura> lineas = new ArrayList<>();
    private double subtotal;
    private double total;


    public Factura(Consulta consulta, MedioPago medioPago, List<LineaFactura> lineas) {
        this.consulta = consulta;
        this.medioPago = medioPago;
        this.lineas = new ArrayList<>(lineas);

        this.numero = UUID.randomUUID().toString();
        this.fecha = LocalDate.now();
        this.estado = EstadoFactura.EMITIDA;
        this.duenio = consulta.getTurno().getMascota().getDuenio();
        this.seniaDescontada = 0;

        calcularTotal();
    }

    public Factura(Duenio duenio, List<LineaFactura> lineas) {
        this.duenio = duenio;
        this.lineas = new ArrayList<>(lineas);

        this.numero = UUID.randomUUID().toString();
        this.fecha = LocalDate.now();
        this.estado = EstadoFactura.EMITIDA;
        this.seniaDescontada = 0;

        calcularTotal();
    }

    private double calcularSubtotal() {
        double sub = 0;
        for (LineaFactura lf : lineas) {
            sub += lf.getSubtotal();
        }
        subtotal = sub;
        return sub;
    }

    private void calcularTotal() {
        // Si la factura es para un cobro de cancelacion
        if (consulta == null) {
            total = calcularSubtotal();
        }
        else {
            // calcula el subtototal y aplica medio de pago, plan salud y descuenta la seña
            double totalSinSenia = medioPago.calcularTotal(calcularSubtotal(), duenio.tienePlanSalud());
            // Consume la seña a favor del dueño (sin pasarse del total) para no descontarla dos veces
            seniaDescontada = duenio.consumirSenia(totalSinSenia);
            total = totalSinSenia - seniaDescontada;
        }
        
    }

    public double getTotal() {
        return total;
    }

    public void anular(String motivo, Recepcionista emisor) {
        this.estado = EstadoFactura.ANULADA;
        this.notaCredito = new NotaCredito(this, motivo, emisor);
    }

    public boolean estaAnulada() {
        return this.estado.equals(EstadoFactura.ANULADA);
    }

    public String getNumero() {
        return numero;
    }
}
