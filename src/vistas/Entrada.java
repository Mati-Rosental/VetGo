package vistas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lectura de datos por consola para las vistas.
 *
 * Reglas generales (los errores se lanzan como IllegalArgumentException):
 *  - Un campo vacío (Enter sin escribir nada) lanza IllegalArgumentException:
 *    la vista informa el error y vuelve al menú anterior.
 *  - Un valor con formato incorrecto (número, fecha, sí/no, opción de menú)
 *    muestra el error y vuelve a pedir el dato; dejando el campo vacío se cancela.
 */
public final class Entrada {

    private Entrada() {
    }

    // ---------------------------------------------------------------- Texto

    /** Texto obligatorio. */
    public static String texto(Scanner scanner, String mensaje) {
        String valor = leerLinea(scanner, mensaje);
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(mensajeVacio(mensaje));
        }
        return valor;
    }

    /** Texto que puede quedar vacío (por ejemplo, la opción de un menú o un dato opcional). */
    public static String textoOpcional(Scanner scanner, String mensaje) {
        return leerLinea(scanner, mensaje);
    }

    // -------------------------------------------------------------- Números

    /** Entero mayor o igual a 0. */
    public static int entero(Scanner scanner, String mensaje) {
        return entero(scanner, mensaje, 0);
    }

    /** Entero mayor o igual a un mínimo. */
    public static int entero(Scanner scanner, String mensaje, int minimo) {
        return leerValidado(scanner, mensaje, mensajeVacio(mensaje), valor -> {
            int numero;
            try {
                numero = Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("\"" + valor + "\" no es un número entero válido.");
            }
            if (numero < minimo) {
                throw new IllegalArgumentException("El valor no puede ser menor a " + minimo + ".");
            }
            return numero;
        });
    }

    /** Decimal mayor o igual a 0 (acepta coma o punto). */
    public static double decimal(Scanner scanner, String mensaje) {
        return leerDecimal(scanner, mensaje, false);
    }

    /** Decimal estrictamente mayor a 0 (acepta coma o punto). */
    public static double decimalPositivo(Scanner scanner, String mensaje) {
        return leerDecimal(scanner, mensaje, true);
    }

    private static double leerDecimal(Scanner scanner, String mensaje, boolean positivo) {
        return leerValidado(scanner, mensaje, mensajeVacio(mensaje), valor -> {
            double numero;
            try {
                numero = Double.parseDouble(valor.replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("\"" + valor + "\" no es un número válido.");
            }
            if (Double.isNaN(numero) || Double.isInfinite(numero)) {
                throw new IllegalArgumentException("\"" + valor + "\" no es un número válido.");
            }
            if (positivo ? numero <= 0 : numero < 0) {
                throw new IllegalArgumentException(positivo
                        ? "El valor debe ser mayor a 0."
                        : "El valor no puede ser negativo.");
            }
            return numero;
        });
    }

    // --------------------------------------------------------------- Fechas

    /** Fecha con formato AAAA-MM-DD. */
    public static LocalDate fecha(Scanner scanner, String mensaje) {
        return leerValidado(scanner, mensaje, mensajeVacio(mensaje), valor -> {
            try {
                return LocalDate.parse(valor);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("La fecha \"" + valor
                        + "\" no es válida. Use el formato AAAA-MM-DD (por ejemplo 2026-11-05).");
            }
        });
    }

    /** Fecha y hora con formato AAAA-MM-DDTHH:MM (también acepta un espacio en lugar de la T). */
    public static LocalDateTime fechaHora(Scanner scanner, String mensaje) {
        return leerValidado(scanner, mensaje, mensajeVacio(mensaje), valor -> {
            try {
                return LocalDateTime.parse(valor.replace(' ', 'T'));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("La fecha y hora \"" + valor
                        + "\" no son válidas. Use el formato AAAA-MM-DDTHH:MM (por ejemplo 2026-11-05T10:30).");
            }
        });
    }

    /** Hora con formato HH:MM. */
    public static LocalTime hora(Scanner scanner, String mensaje) {
        return leerValidado(scanner, mensaje, mensajeVacio(mensaje), valor -> {
            try {
                return LocalTime.parse(valor);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("La hora \"" + valor
                        + "\" no es válida. Use el formato HH:MM (por ejemplo 09:30).");
            }
        });
    }

    // ------------------------------------------------------------- Sí / No

    /** Acepta s / si / sí / n / no. */
    public static boolean booleano(Scanner scanner, String mensaje) {
        return leerValidado(scanner, mensaje, "Debe responder si o no.", valor -> {
            String respuesta = valor.toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            throw new IllegalArgumentException("\"" + valor + "\" no es una respuesta válida. Responda si o no.");
        });
    }

    // ------------------------------------------------------- Multiple choice

    /** Menú numerado; devuelve el índice (desde 0) de la opción elegida. */
    public static int elegirIndice(Scanner scanner, String titulo, List<String> etiquetas) {
        System.out.println(titulo + ":");
        for (int i = 0; i < etiquetas.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + etiquetas.get(i));
        }
        int cantidad = etiquetas.size();
        return leerValidado(scanner, "Seleccione una opción (1-" + cantidad + "): ",
                "No se seleccionó ninguna opción de \"" + titulo + "\".", valor -> {
                    int numero;
                    try {
                        numero = Integer.parseInt(valor);
                    } catch (NumberFormatException e) {
                        throw opcionFueraDeRango(valor, cantidad);
                    }
                    if (numero < 1 || numero > cantidad) {
                        throw opcionFueraDeRango(valor, cantidad);
                    }
                    return numero - 1;
                });
    }

    /** Menú numerado con los valores indicados (por ejemplo, los de un enum). */
    public static <E> E elegir(Scanner scanner, String titulo, E[] valores) {
        List<String> etiquetas = Arrays.stream(valores)
                .map(Entrada::etiqueta)
                .collect(Collectors.toList());
        return valores[elegirIndice(scanner, titulo, etiquetas)];
    }

    /** Menú numerado de selección múltiple: se ingresan los números separados por coma. */
    public static <E extends Enum<E>> Set<E> elegirVarios(Scanner scanner, String titulo, E[] valores) {
        System.out.println(titulo + ":");
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + ". " + etiqueta(valores[i]));
        }
        int cantidad = valores.length;
        return leerValidado(scanner, "Ingrese los números separados por coma (ej: 1,3): ",
                "No se seleccionó ninguna opción de \"" + titulo + "\".", valor -> {
                    Set<Integer> numeros = new LinkedHashSet<>();
                    for (String parte : valor.split(",")) {
                        String limpio = parte.trim();
                        if (limpio.isEmpty()) {
                            continue;
                        }
                        int numero;
                        try {
                            numero = Integer.parseInt(limpio);
                        } catch (NumberFormatException e) {
                            throw opcionFueraDeRango(limpio, cantidad);
                        }
                        if (numero < 1 || numero > cantidad) {
                            throw opcionFueraDeRango(limpio, cantidad);
                        }
                        numeros.add(numero);
                    }
                    if (numeros.isEmpty()) {
                        throw new IllegalArgumentException("Debe elegir al menos una opción.");
                    }
                    Set<E> elegidos = EnumSet.noneOf(valores[0].getDeclaringClass());
                    for (int numero : numeros) {
                        elegidos.add(valores[numero - 1]);
                    }
                    return elegidos;
                });
    }

    // --------------------------------------------------------------- Errores

    /** Opción ingresada en un menú que no corresponde a ninguna acción. */
    public static void mostrarOpcionInvalida(String opcion) {
        if (opcion == null || opcion.isEmpty()) {
            System.out.println("Error: no se ingresó ninguna opción.");
        } else {
            System.out.println("Opción no válida.");
        }
    }

    /** Informa el error y deja que la vista vuelva a mostrar el menú anterior. */
    public static void mostrarError(RuntimeException e) {
        String detalle = e.getMessage();
        if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
            System.out.println("\nError: " + (detalle != null ? detalle : "los datos ingresados no son válidos."));
        } else {
            System.out.println("\nError inesperado (" + e.getClass().getSimpleName() + ")"
                    + (detalle != null ? ": " + detalle : "."));
        }
        System.out.println("Operación cancelada. Volviendo al menú anterior.");
    }

    // ------------------------------------------------------------- Internos

    private static String leerLinea(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    /**
     * Lee un valor obligatorio y lo convierte. Si está vacío cancela (excepción);
     * si el formato es incorrecto muestra el error y lo vuelve a pedir.
     */
    private static <T> T leerValidado(Scanner scanner, String mensaje, String mensajeVacio,
            Function<String, T> conversor) {
        while (true) {
            String valor = leerLinea(scanner, mensaje);
            if (valor.isEmpty()) {
                throw new IllegalArgumentException(mensajeVacio);
            }
            try {
                return conversor.apply(valor);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage()
                        + " Intente nuevamente o deje el campo vacío para volver al menú.");
            }
        }
    }

    private static IllegalArgumentException opcionFueraDeRango(String valor, int cantidad) {
        return new IllegalArgumentException("\"" + valor + "\" no es una opción válida. "
                + "Ingrese un número entre 1 y " + cantidad + ".");
    }

    private static String mensajeVacio(String mensaje) {
        return "El campo \"" + nombreCampo(mensaje) + "\" no puede estar vacío.";
    }

    // "Fecha de nacimiento (AAAA-MM-DD): " -> "Fecha de nacimiento"
    private static String nombreCampo(String mensaje) {
        return mensaje.replaceAll("\\s*\\(.*?\\)", "").replaceAll("[:\\s]+$", "");
    }

    // CONSULTA_GENERAL -> Consulta general
    private static String etiqueta(Object valor) {
        String texto = valor.toString().replace('_', ' ').toLowerCase();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}