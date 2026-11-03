package modelo;

public class Recepcionista extends Persona {
    @Override
    public String getRol() {
        return "Recepcionista";
    }

    public Recepcionista(String nombre, String apellido, String documento, String telefono) {
        super(nombre, apellido, documento, telefono);
    }
}
