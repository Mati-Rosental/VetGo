package servicio;

import modelo.*;
import persistencia.*;
public interface Notificador {
    void notificar(Persona destinatario, String mensaje);
}
