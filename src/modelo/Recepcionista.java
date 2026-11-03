package modelo;

public class Recepcionista extends Persona {
    @Override
    public String getRol() {
        System.out.println("Recepcionista.getRol");
        return null;
    }

    public Recepcionista(String nombre, String apellido, String documento, String telefono) {
        super(nombre, apellido, documento, telefono);
    }
}
