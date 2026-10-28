package modelo;

public class Ave extends Mascota {
    private boolean puedeVolar;

    @Override
    public Especie getEspecie() {
        System.out.println("Ave.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Ave.getDatosPropios");
        return null;
    }
}
