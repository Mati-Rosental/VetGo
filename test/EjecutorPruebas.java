import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Marco de pruebas minimo: cada caso se identifica con un id y se imprime PASA o FALLA. */
public class EjecutorPruebas {

    public interface Caso {
        void ejecutar() throws Exception;
    }

    public static class Omision extends RuntimeException {
        public Omision(String motivo) {
            super(motivo);
        }
    }

    private static List<String> estados = new ArrayList<>();

    public static void caso(String id, String descripcion, Caso caso) {
        String estado = "PASA";
        String detalle = "";
        try {
            caso.ejecutar();
        } catch (Omision o) {
            estado = "OMITIDA";
            detalle = o.getMessage();
        } catch (AssertionError e) {
            estado = "FALLA";
            detalle = e.getMessage();
        } catch (Exception e) {
            estado = "ERROR";
            detalle = e.toString();
        }
        estados.add(estado);
        System.out.println("[" + estado + "] " + id + " - " + descripcion);
        if (!detalle.isEmpty()) {
            System.out.println("        -> " + detalle);
        }
    }

    public static void omitir(String motivo) {
        throw new Omision(motivo);
    }

    public static void esperar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    public static void esperarIgual(Object esperado, Object obtenido, String mensaje) {
        if (!Objects.equals(esperado, obtenido)) {
            throw new AssertionError(mensaje + " (esperado: " + esperado + ", obtenido: " + obtenido + ")");
        }
    }

    public static void esperarIgual(double esperado, double obtenido, String mensaje) {
        if (Math.abs(esperado - obtenido) > 0.005) {
            throw new AssertionError(mensaje + " (esperado: " + esperado + ", obtenido: " + obtenido + ")");
        }
    }

    /** Devuelve la cantidad de casos que fallaron. */
    public static int resumen() {
        int pasan = 0, fallan = 0, omitidos = 0;
        for (String e : estados) {
            if (e.equals("PASA")) pasan++;
            else if (e.equals("OMITIDA")) omitidos++;
            else fallan++;
        }
        System.out.println();
        System.out.println("=== RESUMEN: " + estados.size() + " casos | PASAN " + pasan
                + " | FALLAN " + fallan + " | ERRORES 0 | OMITIDOS " + omitidos + " ===");
        return fallan;
    }
}
