package modelo;

public class Proveedor {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'modificarDatos'");
    }
}

