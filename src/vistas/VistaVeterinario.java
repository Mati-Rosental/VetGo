package vistas;

import controlador.ControladorAtencion;
import modelo.HistoriaClinica;
import java.util.Scanner;

public class VistaVeterinario {
    private final ControladorAtencion controlador;

    public VistaVeterinario(ControladorAtencion controlador) {
        this.controlador = controlador;
    }

    public void iniciarMenu(Scanner scanner) {
        String opcion;
        do {
            System.out.println("\n--- Menú de veterinario ---");
            System.out.println("1. Registrar consulta");
            System.out.println("2. Registrar peso de consulta");
            System.out.println("3. Indicar tratamiento");
            System.out.println("4. Aplicar vacuna");
            System.out.println("5. Confirmar consulta");
            System.out.println("6. Registrar nota rectificatoria");
            System.out.println("7. Consultar historia clínica");
            System.out.println("0. Volver");

            opcion = leer(scanner, "Opción: ");

            switch (opcion) {
                case "1":
                    if (controlador.iniciarConsulta(
                            leer(scanner, "Identificador del turno: "),
                            leer(scanner, "Motivo de consulta: "))) {
                        System.out.println("Consulta iniciada correctamente.");
                    } else {
                        System.out.println("No se pudo iniciar la consulta.");
                    }
                    break;

                case "2":
                    try {
                        double peso = Double.parseDouble(leer(scanner, "Peso registrado: "));
                        if (controlador.registrarPeso(leer(scanner, "Identificador de la consulta: "), peso)) {
                            System.out.println("Peso registrado correctamente.");
                        } else {
                            System.out.println("No se pudo registrar el peso.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("El peso debe ser un número válido.");
                    }
                    break;

                case "3":
                    try {
                        String idConsulta = leer(scanner, "Identificador de la consulta: ");
                        String idProducto = leer(scanner, "Identificador del medicamento: ");
                        String dosis = leer(scanner, "Dosis: ");
                        String frecuencia = leer(scanner, "Frecuencia: ");
                        int duracionDias = Integer.parseInt(leer(scanner, "Duración en días: "));
                        int cantidad = Integer.parseInt(leer(scanner, "Cantidad: "));

                        if (controlador.indicarTratamiento(idConsulta, idProducto, dosis, frecuencia, duracionDias, cantidad)) {
                            System.out.println("Tratamiento registrado correctamente.");
                        } else {
                            System.out.println("No se pudo registrar el tratamiento.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("La duración y la cantidad deben ser números enteros.");
                    }
                    break;

                case "4":
                    if (controlador.aplicarVacuna(
                            leer(scanner, "Identificador de la consulta: "),
                            leer(scanner, "Identificador de la vacuna: "))) {
                        System.out.println("Vacuna aplicada correctamente.");
                    } else {
                        System.out.println("No se pudo registrar la aplicación de la vacuna.");
                    }
                    break;

                case "5":
                    if (controlador.confirmarConsulta(leer(scanner, "Identificador de la consulta: "))) {
                        System.out.println("Consulta confirmada correctamente.");
                    } else {
                        System.out.println("No se pudo confirmar la consulta.");
                    }
                    break;

                case "6":
                    if (controlador.registrarNotaRectificatoria(
                            leer(scanner, "Identificador de la consulta: "),
                            leer(scanner, "Texto de la nota: "),
                            leer(scanner, "Documento del veterinario firmante: "))) {
                        System.out.println("Nota rectificatoria registrada correctamente.");
                    } else {
                        System.out.println("No se pudo registrar la nota rectificatoria.");
                    }
                    break;

                case "7":
                    HistoriaClinica historia = controlador.consultarHistoriaClinica(
                            leer(scanner, "Identificador de la mascota: "));

                    if (historia == null) {
                        System.out.println("No se encontró la historia clínica.");
                    } else {
                        System.out.println(historia);
                    }
                    break;

                case "0":
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));
    }

    private String leer(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }
}