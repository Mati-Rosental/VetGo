package vistas.consola;

import controlador.ControladorAtencion;
import modelo.mascotas.HistoriaClinica;
import vistas.interfaces.VistaVeterinario;

import java.util.Scanner;

public class VistaVeterinarioConsola implements VistaVeterinario {
    private final ControladorAtencion controlador;

    public VistaVeterinarioConsola(ControladorAtencion controlador) {
        this.controlador = controlador;
    }

    public void iniciarMenu(Scanner scanner) {
        String opcion;
        do {
            System.out.println("\n--- Menú de veterinario ---");
            System.out.println("1. Registrar consulta");
            System.out.println("2. Registrar peso de consulta");
            System.out.println("3. Registrar diagnóstico de consulta");
            System.out.println("4. Registrar observaciones de consulta");
            System.out.println("5. Indicar tratamiento");
            System.out.println("6. Aplicar vacuna");
            System.out.println("7. Confirmar consulta");
            System.out.println("8. Registrar nota rectificatoria");
            System.out.println("9. Consultar historia clínica");
            System.out.println("0. Volver");

            opcion = Entrada.textoOpcional(scanner, "Opción: ");

            try {
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
                        double peso = Entrada.decimalPositivo(scanner, "Peso registrado: ");
                        if (controlador.registrarPeso(leer(scanner, "Identificador de la consulta: "), peso)) {
                            System.out.println("Peso registrado correctamente.");
                        } else {
                            System.out.println("No se pudo registrar el peso.");
                        }
                        break;

                    case "3":
                        if (controlador.registrarDiagnostico(
                                leer(scanner, "Identificador de la consulta: "),
                                leer(scanner, "Diagnóstico: "))) {
                            System.out.println("Diagnóstico registrado correctamente.");
                        } else {
                            System.out.println("No se pudo registrar el diagnóstico.");
                        }
                        break;

                    case "4":
                        if (controlador.registrarObservaciones(
                                leer(scanner, "Identificador de la consulta: "),
                                leer(scanner, "Observaciones: "))) {
                            System.out.println("Observaciones registradas correctamente.");
                        } else {
                            System.out.println("No se pudo registrar las observaciones.");
                        }
                        break;

                    case "5":
                        String idConsulta = leer(scanner, "Identificador de la consulta: ");
                        String idProducto = leer(scanner, "Identificador del medicamento: ");
                        String dosis = leer(scanner, "Dosis: ");
                        String frecuencia = leer(scanner, "Frecuencia: ");
                        int duracionDias = Entrada.entero(scanner, "Duración en días: ", 1);
                        int cantidad = Entrada.entero(scanner, "Cantidad: ", 1);

                        if (controlador.indicarTratamiento(idConsulta, idProducto, dosis, frecuencia, duracionDias, cantidad)) {
                            System.out.println("Tratamiento registrado correctamente.");
                        } else {
                            System.out.println("No se pudo registrar el tratamiento.");
                        }
                        break;

                    case "6":
                        if (controlador.aplicarVacuna(
                                leer(scanner, "Identificador de la consulta: "),
                                leer(scanner, "Identificador de la vacuna: "))) {
                            System.out.println("Vacuna aplicada correctamente.");
                        } else {
                            System.out.println("No se pudo aplicar la vacuna. Verifique especie, edad y stock.");
                        }
                        break;

                    case "7":
                        if (controlador.confirmarConsulta(leer(scanner, "Identificador de la consulta: "))) {
                            System.out.println("Consulta confirmada correctamente.");
                        } else {
                            System.out.println("No se pudo confirmar la consulta.");
                        }
                        break;

                    case "8":
                        if (controlador.registrarNotaRectificatoria(
                                leer(scanner, "Identificador de la consulta: "),
                                leer(scanner, "Texto de la nota: "),
                                leer(scanner, "Documento del veterinario firmante: "))) {
                            System.out.println("Nota rectificatoria registrada correctamente.");
                        } else {
                            System.out.println("No se pudo registrar la nota rectificatoria.");
                        }
                        break;

                    case "9":
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
                        Entrada.mostrarOpcionInvalida(opcion);
                }
            } catch (RuntimeException e) {
                Entrada.mostrarError(e);
            }
        } while (!opcion.equals("0"));
    }

    private String leer(Scanner scanner, String mensaje) {
        return Entrada.texto(scanner, mensaje);
    }
}