package servicio;

import modelo.*;
import modelo.personas.Persona;
import persistencia.*;
public interface Notificador {
    void notificar(Persona destinatario, String mensaje);
}
