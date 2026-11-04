package servicio;

import modelo.personas.Persona;
public interface Notificador {
    void notificar(Persona destinatario, String mensaje);
}
