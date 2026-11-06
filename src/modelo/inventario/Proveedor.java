package modelo.inventario;

import java.io.Serializable;

public class Proveedor implements Serializable {
    private String CUIT;
    private String nombre;
    private String telefono;
    private String correo;

    public Proveedor(String CUIT, String nombre, String telefono, String correo) {
        this.CUIT = CUIT;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Modifica los datos del proveedor, manteniendo los valores actuales cuando los nuevos son null o están vacíos
    public void modificarDatos(String nuevoNombre, String nuevoTelefono, String nuevoCorreo) {
        if (nuevoNombre != null && !nuevoNombre.isBlank()) this.nombre = nuevoNombre;
        if (nuevoTelefono != null && !nuevoTelefono.isBlank()) this.telefono = nuevoTelefono;
        if (nuevoCorreo != null && !nuevoCorreo.isBlank()) this.correo = nuevoCorreo;
    }

    public String getCUIT() {
        return CUIT;
    }

    public String getNombre() { return nombre; }

    public String getTelefono() { return telefono; }

    public String getCorreo() { return correo; }
}

