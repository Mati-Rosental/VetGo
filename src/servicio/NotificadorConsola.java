package servicio;

import modelo.personas.Persona;

public class NotificadorConsola implements Notificador {

    @Override
    public void notificar(Persona destinatario, String mensaje) {
        String nombre = destinatario != null ? destinatario.toString() : "(sin destinatario)";
        System.out.println("[NOTIFICACIÓN] Para: " + nombre + " - " + mensaje);
    }
}