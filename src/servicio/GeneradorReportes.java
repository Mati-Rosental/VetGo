package servicio;

import modelo.*;
import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.inventario.Inventario;
import modelo.inventario.Lote;
import modelo.inventario.Producto;
import modelo.personas.Veterinario;
import persistencia.*;
import java.time.LocalDate;
import java.util.List;

public class GeneradorReportes {
    private Agenda agenda;
    private Inventario inventario;

    public List<Turno> turnosPorVeterinario(Veterinario veterinario, LocalDate desde, LocalDate hasta) {
        System.out.println("GeneradorReportes.turnosPorVeterinario");
        return null;
    }

    public List<Producto> productosConStockBajo() {
        System.out.println("GeneradorReportes.productosConStockBajo");
        return null;
    }

    public List<Lote> lotesProximosAVencer(LocalDate fechaActual, int diasAnticipacion) {
        System.out.println("GeneradorReportes.lotesProximosAVencer");
        return null;
    }
}
