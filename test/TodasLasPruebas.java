/** Ejecuta la suite unitaria y la funcional y devuelve codigo de salida distinto de 0 si algo falla. */
public final class TodasLasPruebas {
    public static void main(String[] args) {
        PruebasUnitarias.ejecutar();
        PruebasFuncionales.ejecutar();
        System.exit(EjecutorPruebas.resumen() == 0 ? 0 : 1);
    }
}
