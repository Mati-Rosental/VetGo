package persistencia;

import java.io.Serializable;
import java.util.List;

import modelo.agenda.Agenda;
import modelo.facturacion.Factura;
import modelo.facturacion.Tarifario;
import modelo.inventario.Inventario;
import modelo.personas.Clinica;

public class EstadoSistema implements Serializable {

    private Clinica clinica;
    private Agenda agenda;
    private Inventario inventario;
    private List<Factura> facturas;
    private Tarifario tarifario;

    public EstadoSistema(Clinica clinica, Agenda agenda, Inventario inventario,
            List<Factura> facturas, Tarifario tarifario) {
        this.clinica = clinica;
        this.agenda = agenda;
        this.inventario = inventario;
        this.facturas = facturas;
        this.tarifario = tarifario;
    }

    public Clinica getClinica() {
        return clinica;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public List<Factura> getFacturas() {
        return facturas;
    }

    public Tarifario getTarifario() {
        return tarifario;
    }
}