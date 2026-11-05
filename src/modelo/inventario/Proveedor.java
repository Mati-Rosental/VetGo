package modelo.inventario;

import java.io.Serializable;

public class Proveedor implements Serializable {
    private String CUIL;
    private String nombre;
    private String telefono;
    private String correo;

    public Proveedor(String CUIL, String nombre, String telefono, String correo) {
        this.CUIL = CUIL;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    public void modificarDatos(String nombre2, String telefono2, String correo2) {
        if (nombre != null && !nombre.isBlank()) this.nombre = nombre;
        if (telefono != null && !telefono.isBlank()) this.telefono = telefono;
        if (correo != null && !correo.isBlank()) this.correo = correo;
    }
    public String getCUIL() {
        return CUIL;
    }
}

