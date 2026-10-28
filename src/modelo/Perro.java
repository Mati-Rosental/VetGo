package modelo;

public class Perro extends Mascota {
    private String numeroChip;
    private TamanioPerro tamanio;

    @Override
    public Especie getEspecie() {
        System.out.println("Perro.getEspecie");
        return null;
    }

    @Override
    public String getDatosPropios() {
        System.out.println("Perro.getDatosPropios");
        return null;
    }
}
