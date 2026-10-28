package modelo;

public class Gato extends Mascota {
    private boolean viveEnInterior;

    @Override
    public Especie getEspecie() {
        System.out.println("Gato.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Gato.getDatosPropios");
        return null;
    }
}
