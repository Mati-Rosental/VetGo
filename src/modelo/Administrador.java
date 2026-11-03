package modelo;

public class Administrador extends Persona {
    @Override
    public String getRol() {
        System.out.println("Administrador.getRol");
        return null;
    }

    public Administrador(String nombre, String apellido, String documento, String telefono) {
        super(nombre, apellido, documento, telefono);
    }
}
