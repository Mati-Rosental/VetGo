package vistas;

import controlador.ControladorAtencion;
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
                    controlador.iniciarConsulta(leer(scanner, "Identificador del turno: "));
                    break;
                case "2":
                    controlador.registrarPeso(
                            leer(scanner, "Identificador de la consulta: "),
                            leer(scanner, "Peso registrado: "));
                    break;
                case "3":
                    controlador.indicarTratamiento(
                            leer(scanner, "Identificador de la consulta: "),
                            leer(scanner, "Identificador del medicamento: "),
                            leer(scanner, "Dosis: "),
                            leer(scanner, "Frecuencia: "),
                            leer(scanner, "Duración en días: "),
                            leer(scanner, "Cantidad: "));
                    break;
                case "4":
                    controlador.aplicarVacuna(
                            leer(scanner, "Identificador de la consulta: "),
                            leer(scanner, "Identificador de la vacuna: "));
                    break;
                case "5":
                    controlador.confirmarConsulta(leer(scanner, "Identificador de la consulta: "));
                    break;
                case "6":
                    controlador.registrarNotaRectificatoria(
                            leer(scanner, "Identificador de la consulta: "),
                            leer(scanner, "Texto de la nota: "),
                            leer(scanner, "Documento del veterinario firmante: "));
                    break;
                case "7":
                    controlador.consultarHistoriaClinica(
                            leer(scanner, "Identificador de la mascota: "));
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
