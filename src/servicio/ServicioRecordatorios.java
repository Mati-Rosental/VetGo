package servicio;

import modelo.*;
import persistencia.*;
import java.time.LocalDate;
import java.util.List;

public class ServicioRecordatorios {
    private Notificador notificador;

    public void emitirRecordatorios(List<Mascota> mascotas, LocalDate fechaActual) {
        System.out.println("ServicioRecordatorios.emitirRecordatorios");
    }
}
