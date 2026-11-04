package modelo.atencion;

import java.io.Serializable;
import java.time.LocalDate;

import modelo.personas.Veterinario;

public class NotaRectificatoria implements Serializable {
    private LocalDate fecha;
    private String texto;
    private Veterinario firmante;

    public String getTexto() {
        return texto;
    }

    public NotaRectificatoria(String texto, Veterinario firmante) {
        this.texto = texto;
        this.firmante = firmante;
        this.fecha = LocalDate.now();
    }
}
