package vistas;

import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.Especie;
import modelo.Sexo;
import modelo.TamanioPerro;

import java.time.LocalDate;
import java.util.Scanner;

public class VistaRecepcionista {
    private final ControladorRegistro controladorRegistro;
    private final ControladorTurnos controladorTurnos;
    private final ControladorFacturacion controladorFacturacion;

    public VistaRecepcionista(
            ControladorRegistro controladorRegistro,
            ControladorTurnos controladorTurnos,
            ControladorFacturacion controladorFacturacion) {
        this.controladorRegistro = controladorRegistro;
        this.controladorTurnos = controladorTurnos;
        this.controladorFacturacion = controladorFacturacion;
    }

    public void iniciarMenu(Scanner scanner) {
        String opcion;
        do {
            System.out.println("\n--- Menú de recepción ---");
            System.out.println("1. Registrar dueño");
            System.out.println("2. Registrar mascota");
            System.out.println("3. Transferir mascota a otro dueño");
            System.out.println("4. Asignar turno");
            System.out.println("5. Reprogramar turno");
            System.out.println("6. Cancelar turno");
            System.out.println("7. Registrar ausencia");
            System.out.println("8. Atender urgencia");
            System.out.println("9. Facturar consulta");
            System.out.println("10. Anular factura");
            System.out.println("0. Volver");
            opcion = leer(scanner, "Opción: ");
            switch (opcion) {
            case "1":
            controladorRegistro.altaDuenio(
                    leer(scanner, "Nombre: "),
                    leer(scanner, "Apellido: "),
                    leer(scanner, "Documento: "),
                    leer(scanner, "Teléfono: "),
                    leer(scanner, "Correo: "),
                    leerBooleano(scanner, "¿Tiene plan de salud? (si/no): ")
            );
            break;

            case "2":
                controladorRegistro.altaMascota(
                        leer(scanner, "Documento del dueño: "),
                        Especie.valueOf(leer(scanner, "Especie (PERRO/GATO/AVE/CONEJO): ").toUpperCase()),
                        leer(scanner, "Nombre de la mascota: "),
                        leer(scanner, "Raza: "),
                        Sexo.valueOf(leer(scanner, "Sexo: ").toUpperCase()),
                        LocalDate.parse(leer(scanner, "Fecha de nacimiento (AAAA-MM-DD): ")),
                        Double.parseDouble(leer(scanner, "Peso actual: ")),
                        leer(scanner, "Chip (si corresponde): "),
                        leerTamanio(scanner),
                        leerBooleano(scanner, "¿Vive en interior? (si/no): "),
                        leerBooleano(scanner, "¿Vuela? (si/no): ")
                );
                break;

            case "3":
                controladorRegistro.transferirMascota(
                        leer(scanner, "Identificador de la mascota: "),
                        leer(scanner, "Documento del nuevo dueño: ")
                );
                break;
                case "4":
                    controladorTurnos.asignarTurno(
                            leer(scanner, "Identificador de la mascota: "),
                            leer(scanner, "Documento del veterinario: "),
                            leer(scanner, "Tipo de atención: "),
                            leer(scanner, "Fecha y hora: "));
                    break;
                case "5":
                    controladorTurnos.reprogramarTurno(
                            leer(scanner, "Identificador del turno: "),
                            leer(scanner, "Nueva fecha y hora: "),
                            leer(scanner, "Documento del veterinario (opcional): "));
                    break;
                case "6":
                    controladorTurnos.cancelarTurno(
                            leer(scanner, "Identificador del turno: "),
                            leer(scanner, "Fecha y hora actual: "));
                    break;
                case "7":
                    controladorTurnos.registrarAusencia(leer(scanner, "Identificador del turno: "));
                    break;
                case "8":
                    controladorTurnos.atenderUrgencia(
                            leer(scanner, "Identificador de la mascota: "),
                            leer(scanner, "Día de la urgencia: "));
                    break;
                case "9":
                    controladorFacturacion.facturarConsulta(
                            leer(scanner, "Identificador de la consulta: "),
                            leer(scanner, "Medio de pago: "));
                    break;
                case "10":
                    controladorFacturacion.anularFactura(
                            leer(scanner, "Identificador de la factura: "),
                            leer(scanner, "Motivo: "),
                            leer(scanner, "Documento de la recepcionista: "));
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

    private boolean leerBooleano(Scanner scanner, String mensaje) {
    String respuesta = leer(scanner, mensaje).trim().toLowerCase();

    return respuesta.equals("si") || respuesta.equals("sí");
    }

    private TamanioPerro leerTamanio(Scanner scanner) {
        String tamanio = leer(scanner, "Tamaño (CHICO/MEDIANO/GRANDE): ");

        if (tamanio.isBlank()) {
            return null;
        }

        return TamanioPerro.valueOf(tamanio.toUpperCase());
    }
}
