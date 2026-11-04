package servicio;

import modelo.atencion.VacunaAplicada;
import modelo.mascotas.Mascota;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServicioRecordatorios {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Notificador notificador;

    public ServicioRecordatorios(Notificador notificador) {
        this.notificador = notificador;
    }

    public void emitirRecordatorios(List<Mascota> mascotas, LocalDate fechaActual) {
        if (mascotas == null || fechaActual == null) {
            return;
        }

        // Recorrer las vacunas aplicadas de cada mascota
        for (Mascota mascota : mascotas) {
            List<VacunaAplicada> vacunas = mascota.getHistoriaClinica().getVacunasAplicadas();
            if (vacunas == null) {
                continue;
            }

            // Por cada vacuna que requiere recordatorio, notificar al dueño
            for (VacunaAplicada vacuna : vacunas) {
                if (vacuna.requiereRecordatorio(fechaActual)) {
                    String mensaje = "Recordatorio: la vacuna " + vacuna.getVacuna().getNombre()
                            + " de " + mascota.getNombre()
                            + " debe refrescarse el " + vacuna.getProximoRefuerzo().format(FORMATO) + ".";
                    notificador.notificar(mascota.getDuenio(), mensaje);
                }
            }
        }
    }
}