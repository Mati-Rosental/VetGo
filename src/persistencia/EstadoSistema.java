package persistencia;

import java.io.Serializable;
import java.util.List;

import modelo.agenda.Agenda;
import modelo.facturacion.Factura;
import modelo.inventario.Inventario;
import modelo.personas.Clinica;

public class EstadoSistema implements Serializable {

    private Clinica clinica;
    private Agenda agenda;
    private Inventario inventario;
    private List<Factura> facturas;

    public EstadoSistema(Clinica clinica, Agenda agenda, Inventario inventario, List<Factura> facturas) {
        this.clinica = clinica;
        this.agenda = agenda;
        this.inventario = inventario;
        this.facturas = facturas;
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
}
