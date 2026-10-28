package modelo;

public enum TipoAtencion {
    CONSULTA_GENERAL(30),
    VACUNACION(15),
    CIRUGIA(120);

    private final int duracionMin;

    TipoAtencion(int duracionMin) {
        this.duracionMin = duracionMin;
    }

    public int getDuracionMin() {
        System.out.println("TipoAtencion.getDuracionMin");
        return 0;
    }
}
