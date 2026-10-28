package modelo;

public abstract class Persona {
    protected String nombre;
    protected String apellido;
    protected String documento;
    protected String telefono;

    public String getNombreCompleto() {
        System.out.println("Persona.getNombreCompleto");
        return null;
    }

    public abstract String getRol();
}
