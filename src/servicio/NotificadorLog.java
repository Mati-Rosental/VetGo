package servicio;

import modelo.*;
import persistencia.*;
public class NotificadorLog implements Notificador {
    private String rutaLog;

    @Override
    public void notificar(Persona destinatario, String mensaje) {
        System.out.println("NotificadorLog.notificar");
    }
}
