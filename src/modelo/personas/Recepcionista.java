package modelo.personas;

public class Recepcionista extends Persona {
    @Override
    public String getRol() {
        return "Recepcionista";
    }

    public Recepcionista(String nombre, String apellido, String documento, String telefono) {
        super(nombre, apellido, documento, telefono);
    }

    public void modificarDatos(String nombre, String apellido, String telefono) {
        // TODO Auto-generated method stub
        // Si alguna variable es null, no hay que modificarla
        if (nombre != null && !nombre.isBlank()) this.nombre = nombre;
        if (apellido != null && !apellido.isBlank()) this.apellido = apellido;
        if (telefono != null && !telefono.isBlank()) this.telefono = telefono;    }
}
