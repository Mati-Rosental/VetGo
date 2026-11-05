package vistas;

import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.agenda.Turno;
import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.enumeradores.TamanioPerro;
import modelo.enumeradores.TipoAtencion;
import modelo.facturacion.pago.Credito;
import modelo.facturacion.pago.Debito;
import modelo.facturacion.pago.Efectivo;
import modelo.facturacion.pago.MedioPago;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

public class VistaRecepcionista {
    private final ControladorRegistro controladorRegistro;
    private final ControladorTurnos controladorTurnos;
    private final ControladorFacturacion controladorFacturacion;

    public VistaRecepcionista(ControladorRegistro controladorRegistro, ControladorTurnos controladorTurnos, ControladorFacturacion controladorFacturacion) {
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
            opcion = Entrada.textoOpcional(scanner, "Opción: ");

            try {
                switch (opcion) {
                    case "1":
                        resultado(controladorRegistro.altaDuenio(
                                leer(scanner, "Nombre: "),
                                leer(scanner, "Apellido: "),
                                leer(scanner, "Documento: "),
                                leer(scanner, "Teléfono: "),
                                leer(scanner, "Correo: "),
                                Entrada.booleano(scanner, "¿Tiene plan de salud? (si/no): ")),
                                "Dueño registrado correctamente.",
                                "No se pudo registrar el dueño. Es posible que ya exista uno con ese documento.");
                        break;

                    case "2":
                        registrarMascota(scanner);
                        break;

                    case "3":
                        resultado(controladorRegistro.transferirMascota(
                                leer(scanner, "Identificador de la mascota: "),
                                leer(scanner, "Documento del nuevo dueño: ")),
                                "Mascota transferida correctamente.",
                                "No se pudo transferir la mascota. Verifique que la mascota y el nuevo dueño existan y que el dueño sea distinto al actual.");
                        break;

                    case "4":
                        resultado(controladorTurnos.asignarTurno(
                                leer(scanner, "Identificador de la mascota: "),
                                leer(scanner, "Documento del veterinario: "),
                                Entrada.elegir(scanner, "Tipo de atención", new TipoAtencion[] {
                                        TipoAtencion.CONSULTA_GENERAL,
                                        TipoAtencion.VACUNACION,
                                        TipoAtencion.CIRUGIA }),
                                Entrada.fechaHora(scanner, "Fecha y hora (AAAA-MM-DDTHH:MM): ")),
                                "Turno asignado correctamente.",
                                "No se pudo asignar el turno. Verifique los datos y que el veterinario esté disponible en ese horario.");
                        break;

                    case "5":
                        resultado(controladorTurnos.reprogramarTurno(
                                leer(scanner, "Identificador del turno: "),
                                Entrada.fechaHora(scanner, "Nueva fecha y hora (AAAA-MM-DDTHH:MM): ")),
                                "Turno reprogramado correctamente.",
                                "No se pudo reprogramar el turno. Verifique el identificador y la disponibilidad del horario.");
                        break;

                    case "6":
                        resultado(controladorTurnos.cancelarTurno(
                                leer(scanner, "Identificador del turno: ")),
                                "Turno cancelado correctamente.",
                                "No se pudo cancelar el turno.");
                        break;

                    case "7":
                        resultado(controladorTurnos.registrarAusencia(
                                leer(scanner, "Identificador del turno: ")),
                                "Ausencia registrada correctamente.",
                                "No se pudo registrar la ausencia.");
                        break;

                    case "8":
                        Turno turno = controladorTurnos.atenderUrgencia(
                                leer(scanner, "Identificador de la mascota: ")
                        );

                        if (turno == null) {
                            System.out.println("No se pudo asignar un turno de urgencia.");
                        } else {
                            System.out.println("\n--- Turno de urgencia asignado ---");
                            System.out.println("Mascota: " + turno.getMascota().getNombre());
                            System.out.println("Veterinario: " + turno.getVeterinario().getNombre());
                            System.out.println("Fecha y hora: " + turno.getFechaHora());
                            System.out.println("Tipo de atención: " + turno.getTipoAtencion());
                        }
                        break;

                    case "9":
                        resultado(controladorFacturacion.facturarConsulta(
                                leer(scanner, "Identificador de la consulta: "),
                                leerMedioPago(scanner)),
                                "Consulta facturada correctamente.",
                                "No se pudo facturar la consulta. Verifique que la consulta exista y esté confirmada.");
                        break;

                    case "10":
                        resultado(controladorFacturacion.anularFactura(
                                leer(scanner, "Identificador de la factura: "),
                                leer(scanner, "Motivo: "),
                                leer(scanner, "Documento de la recepcionista: ")),
                                "Factura anulada correctamente.",
                                "No se pudo anular la factura. Verifique los datos ingresados.");
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

    private void registrarMascota(Scanner scanner) {
        String documentoDuenio = leer(scanner, "Documento del dueño: ");
        Especie especie = Entrada.elegir(scanner, "Especie", Especie.values());
        String nombre = leer(scanner, "Nombre de la mascota: ");
        String raza = leer(scanner, "Raza: ");
        Sexo sexo = Entrada.elegir(scanner, "Sexo", Sexo.values());
        LocalDate fechaNacimiento = Entrada.fecha(scanner, "Fecha de nacimiento (AAAA-MM-DD): ");
        double peso = Entrada.decimalPositivo(scanner, "Peso actual: ");

        // Solo se piden los datos que corresponden a la especie elegida
        String chip = "";
        TamanioPerro tamanio = null;
        boolean viveEnInterior = false;
        boolean vuela = false;

        switch (especie) {
            case PERRO:
                chip = Entrada.textoOpcional(scanner, "Chip (Enter si no tiene): ");
                tamanio = Entrada.elegir(scanner, "Tamaño", TamanioPerro.values());
                break;
            case GATO:
                viveEnInterior = Entrada.booleano(scanner, "¿Vive en interior? (si/no): ");
                break;
            case AVE:
                vuela = Entrada.booleano(scanner, "¿Vuela? (si/no): ");
                break;
            default:
                break;
        }

        resultado(controladorRegistro.altaMascota(documentoDuenio, especie, nombre, raza, sexo,
                fechaNacimiento, peso, chip, tamanio, viveEnInterior, vuela),
                "Mascota registrada correctamente.",
                "No se pudo registrar la mascota. Verifique que el documento del dueño exista.");
    }

    private void resultado(boolean ok, String exito, String error) {
        System.out.println(ok ? exito : error);
    }

    private String leer(Scanner scanner, String mensaje) {
        return Entrada.texto(scanner, mensaje);
    }

    private MedioPago leerMedioPago(Scanner scanner) {
        int medio = Entrada.elegirIndice(scanner, "Medios de pago", Arrays.asList("Efectivo", "Débito", "Crédito"));

        switch (medio) {
            case 0:
                return new Efectivo();
            case 1:
                return new Debito();
            default:
                return new Credito(Entrada.entero(scanner, "Cantidad de cuotas: ", 1));
        }
    }
}