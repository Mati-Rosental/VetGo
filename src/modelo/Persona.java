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
        System.out.println("Persona.getNombreCompleto");
        return null;
    }

    public abstract String getRol();

    public String getDocumento() {
        System.out.println("Persona.getDocumento");
        return null;
    }
}
