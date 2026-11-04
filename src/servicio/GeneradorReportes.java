package servicio;

import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.inventario.Inventario;
import modelo.inventario.Lote;
import modelo.inventario.Producto;
import modelo.personas.Veterinario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GeneradorReportes {
    private Agenda agenda;
    private Inventario inventario;

    public GeneradorReportes(Agenda agenda, Inventario inventario) {
        this.agenda = agenda;
        this.inventario = inventario;
    }

    public List<Turno> turnosPorVeterinario(Veterinario veterinario, LocalDate desde, LocalDate hasta) {
        if (veterinario == null || desde == null || (hasta != null && hasta.isBefore(desde))) {
            return null;
        }

        return agenda.getTurnosDe(veterinario, desde, hasta);
    }

    public List<Producto> productosConStockBajo() {
        return inventario.getProductosBajoMinimo();
    }

    public List<Lote> lotesProximosAVencer(LocalDate fechaActual, int diasAnticipacion) {
        List<Lote> lotesProximos = new ArrayList<>();

        if (fechaActual == null || diasAnticipacion < 0) {
            return null;
        }

        LocalDate fechaLimite = fechaActual.plusDays(diasAnticipacion);

        for (Producto producto : inventario.getProductos()) {
            for (Lote lote : producto.getLotes()) {
                LocalDate vencimiento = lote.getFechaVencimiento();

                if (vencimiento != null
                        && !vencimiento.isBefore(fechaActual)
                        && !vencimiento.isAfter(fechaLimite)) {
                    lotesProximos.add(lote);
                }
            }
        }

        return lotesProximos;
    }
}