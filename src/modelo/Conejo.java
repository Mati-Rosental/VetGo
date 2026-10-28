package modelo;

public class Conejo extends Mascota {
    @Override
    public Especie getEspecie() {
        System.out.println("Conejo.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Conejo.getDatosPropios");
        return null;
    }
}
