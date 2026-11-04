package modelo;

public abstract class Persona {
    protected String nombre;
    protected String apellido;
    protected String documento;
    protected String telefono;

    public Persona(String nombre, String apellido, String documento, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract String getRol();
}
