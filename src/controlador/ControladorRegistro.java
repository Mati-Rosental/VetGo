package controlador;

public class ControladorRegistro {
    public boolean altaDuenio(
            String nombre, String apellido, String documento, String telefono, String correo, String planSalud) {
        System.out.println("ControladorRegistro.altaDuenio");
        return false;
    }

    public boolean altaMascota(
            String documentoDuenio,
            String nombre,
            String especie,
            String raza,
            String sexo,
            String fechaNacimiento,
            String peso,
            String chip,
            String tamanio,
            String interiorExterior,
            String vuela) {
        System.out.println("ControladorRegistro.altaMascota");
        return false;
    }

    public boolean transferirMascota(String idMascota, String documentoNuevoDuenio, String fecha) {
        System.out.println("ControladorRegistro.transferirMascota");
        return false;
    }
}
