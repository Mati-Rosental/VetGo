package modelo;

public class Administrador extends Persona {
    @Override
    public String getRol() {
        return "Administrador";
    }

    public Administrador(String nombre, String apellido, String documento, String telefono) {
        super(nombre, apellido, documento, telefono);
    }
}
