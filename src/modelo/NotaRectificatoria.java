package modelo;

import java.time.LocalDate;

public class NotaRectificatoria {
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
