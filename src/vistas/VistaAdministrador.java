package vistas;

import controlador.ControladorAdministracion;
import modelo.enumeradores.Especie;
import modelo.inventario.Lote;
import modelo.personas.HorarioAtencion;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class VistaAdministrador {
    private final ControladorAdministracion controlador;

    public VistaAdministrador(ControladorAdministracion controlador) {
        this.controlador = controlador;
    }

    public void iniciarMenu(Scanner scanner) {
        String opcion;
        do {
            System.out.println("\n--- Menú de administración ---");
            System.out.println("1. Gestionar veterinarios y horarios");
            System.out.println("2. Gestionar personal de recepción");
            System.out.println("3. Gestionar catálogo de vacunas");
            System.out.println("4. Gestionar catálogo de productos");
            System.out.println("5. Gestionar catálogo de proveedores");
            System.out.println("6. Emitir reportes y recordatorios");
            System.out.println("0. Volver");
            opcion = leer(scanner, "Opción: ");
            switch (opcion) {
                case "1":
                    gestionarVeterinarios(scanner);
                    break;
                case "2":
                    gestionarRecepcionistas(scanner);
                    break;
                case "3":
                    gestionarVacunas(scanner);
                    break;
                case "4":
                    gestionarProductos(scanner);
                    break;
                case "5":
                    gestionarProveedores(scanner);
                    break;
                case "6":
                    menuReportes(scanner);
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));
    }

    private void gestionarVeterinarios(Scanner scanner) {
        String accion = menuGestion(scanner, "veterinarios", false);
        switch (accion) {
            case "1":
                resultado(controlador.altaVeterinario(
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Documento: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Matrícula: "),
                        leer(scanner, "Especialidad: "),
                        leerHorarios(scanner)),
                        "Veterinario registrado correctamente.",
                        "No se pudo registrar el veterinario.");
                break;
            case "2":
                resultado(controlador.bajaVeterinario(leer(scanner, "Documento del veterinario: ")),
                        "Veterinario dado de baja correctamente.",
                        "No se pudo dar de baja al veterinario.");
                break;
            case "3":
                resultado(controlador.modificarVeterinario(
                        leer(scanner, "Documento del veterinario: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Especialidad: "),
                        leerHorarios(scanner)),
                        "Veterinario modificado correctamente.",
                        "No se pudo modificar al veterinario.");
                break;
            default:
                volver();
        }
    }

    private void gestionarRecepcionistas(Scanner scanner) {
        String accion = menuGestion(scanner, "personal de recepción", false);
        switch (accion) {
            case "1":
                resultado(controlador.altaRecepcionista(
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Documento: "),
                        leer(scanner, "Teléfono: ")),
                        "Recepcionista registrada correctamente.",
                        "No se pudo registrar a la recepcionista.");
                break;
            case "2":
                resultado(controlador.bajaRecepcionista(leer(scanner, "Documento de la recepcionista: ")),
                        "Recepcionista dada de baja correctamente.",
                        "No se pudo dar de baja a la recepcionista.");
                break;
            case "3":
                resultado(controlador.modificarRecepcionista(
                        leer(scanner, "Documento de la recepcionista: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Teléfono: ")),
                        "Recepcionista modificada correctamente.",
                        "No se pudo modificar a la recepcionista.");
                break;
            default:
                volver();
        }
    }

    private void gestionarVacunas(Scanner scanner) {
        String accion = menuGestion(scanner, "vacunas", false);
        switch (accion) {
            case "1":
                resultado(controlador.altaVacuna(
                        leer(scanner, "Código: "),
                        leer(scanner, "Nombre: "),
                        leerEspecies(scanner),
                        leerEntero(scanner, "Edad mínima en semanas: "),
                        leerEntero(scanner, "Intervalo de refuerzo en días: "),
                        leerDecimal(scanner, "Precio unitario: "),
                        leerEntero(scanner, "Stock mínimo: "),
                        leer(scanner, "CUIL del proveedor: "),
                        leerLotes(scanner)),
                        "Vacuna registrada correctamente.",
                        "No se pudo registrar la vacuna.");
                break;
            case "2":
                resultado(controlador.bajaVacuna(leer(scanner, "Identificador de la vacuna: ")),
                        "Vacuna dada de baja correctamente.",
                        "No se pudo dar de baja la vacuna.");
                break;
            case "3":
                resultado(controlador.modificarVacuna(
                        leer(scanner, "Identificador de la vacuna: "),
                        leer(scanner, "Nombre: "),
                        leerEspecies(scanner),
                        leerEntero(scanner, "Edad mínima en semanas: "),
                        leerEntero(scanner, "Intervalo de refuerzo en días: "),
                        leerDecimal(scanner, "Precio unitario: "),
                        leerLotesOConservar(scanner)),
                        "Vacuna modificada correctamente.",
                        "No se pudo modificar la vacuna.");
                break;
            default:
                volver();
        }
    }

    private void gestionarProductos(Scanner scanner) {
        String accion = menuGestion(scanner, "productos", true);
        switch (accion) {
            case "1":
                resultado(controlador.altaProducto(
                        leer(scanner, "Código: "),
                        leer(scanner, "Nombre: "),
                        leerEntero(scanner, "Stock mínimo: "),
                        leerDecimal(scanner, "Precio unitario de venta: "),
                        leerLotes(scanner),
                        leer(scanner, "CUIL del proveedor: ")),
                        "Producto registrado correctamente.",
                        "No se pudo registrar el producto.");
                break;
            case "2":
                resultado(controlador.bajaProducto(leer(scanner, "Código del producto: ")),
                        "Producto dado de baja correctamente.",
                        "No se pudo dar de baja el producto.");
                break;
            case "3":
                resultado(controlador.modificarProducto(
                        leer(scanner, "Código del producto: "),
                        leer(scanner, "Nombre: "),
                        leerEntero(scanner, "Stock mínimo: "),
                        leerDecimal(scanner, "Precio unitario de venta: "),
                        leerLotesOConservar(scanner),
                        leer(scanner, "CUIL del proveedor: ")),
                        "Producto modificado correctamente.",
                        "No se pudo modificar el producto.");
                break;
            case "4":
                resultado(controlador.agregarLotesProducto(
                        leer(scanner, "Código del producto: "),
                        leerLotes(scanner)),
                        "Lotes agregados correctamente.",
                        "No se pudieron agregar los lotes.");
                break;
            default:
                volver();
        }
    }

    private void gestionarProveedores(Scanner scanner) {
        String accion = menuGestion(scanner, "proveedores", false);
        switch (accion) {
            case "1":
                resultado(controlador.altaProveedor(
                        leer(scanner, "CUIL: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Correo: ")),
                        "Proveedor registrado correctamente.",
                        "No se pudo registrar el proveedor.");
                break;
            case "2":
                resultado(controlador.bajaProveedor(leer(scanner, "CUIL del proveedor: ")),
                        "Proveedor dado de baja correctamente.",
                        "No se pudo dar de baja el proveedor.");
                break;
            case "3":
                resultado(controlador.modificarProveedor(
                        leer(scanner, "CUIL del proveedor: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Correo: ")),
                        "Proveedor modificado correctamente.",
                        "No se pudo modificar el proveedor.");
                break;
            default:
                volver();
        }
    }

    private void menuReportes(Scanner scanner) {
        String opcion;
        do {
            System.out.println("\n--- Reportes ---");
            System.out.println("1. Turnos por veterinario");
            System.out.println("2. Productos con stock bajo");
            System.out.println("3. Lotes próximos a vencer");
            System.out.println("4. Emitir recordatorios");
            System.out.println("0. Volver");
            opcion = leer(scanner, "Opción: ");
            switch (opcion) {
                case "1":
                    resultado(controlador.emitirReporteTurnos(
                            leer(scanner, "Documento del veterinario: "),
                            leerFecha(scanner, "Desde (AAAA-MM-DD): "),
                            leerFecha(scanner, "Hasta (AAAA-MM-DD): ")),
                            null,
                            "No se pudo emitir el reporte de turnos.");
                    break;
                case "2":
                    resultado(controlador.emitirReporteStockBajo(),
                            null,
                            "No se pudo emitir el reporte de stock bajo.");
                    break;
                case "3":
                    resultado(controlador.emitirReporteLotesPorVencer(
                            leerFecha(scanner, "Fecha actual (AAAA-MM-DD): "),
                            leerEntero(scanner, "Días de anticipación: ")),
                            null,
                            "No se pudo emitir el reporte de lotes por vencer.");
                    break;
                case "4":
                    resultado(controlador.emitirRecordatorios(
                            leerFecha(scanner, "Fecha actual (AAAA-MM-DD): ")),
                            "Recordatorios emitidos correctamente.",
                            "No se pudieron emitir los recordatorios.");
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));
    }

    private String menuGestion(Scanner scanner, String elemento, boolean conAgregarLotes) {
        System.out.println("\n--- Gestión de " + elemento + " ---");
        System.out.println("1. Alta");
        System.out.println("2. Baja");
        System.out.println("3. Modificación");
        if (conAgregarLotes) {
            System.out.println("4. Agregar lotes");
        }
        System.out.println("0. Volver");
        return leer(scanner, "Opción: ");
    }

    private String leer(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private int leerEntero(Scanner scanner, String mensaje) {
        return Integer.parseInt(leer(scanner, mensaje).trim());
    }

    private double leerDecimal(Scanner scanner, String mensaje) {
        return Double.parseDouble(leer(scanner, mensaje).trim().replace(',', '.'));
    }

    private LocalDate leerFecha(Scanner scanner, String mensaje) {
        return LocalDate.parse(leer(scanner, mensaje).trim());
    }

    private boolean leerBooleano(Scanner scanner, String mensaje) {
        return leer(scanner, mensaje + " (s/n): ").trim().equalsIgnoreCase("s");
    }

    private Set<Especie> leerEspecies(Scanner scanner) {
        Set<Especie> especies = EnumSet.noneOf(Especie.class);
        String texto = leer(scanner, "Especies, separadas por coma (PERRO/GATO/AVE/CONEJO): ");
        for (String parte : texto.split(",")) {
            especies.add(Especie.valueOf(parte.trim().toUpperCase()));
        }
        return especies;
    }

    private DayOfWeek leerDia(Scanner scanner) {
        String dia = leer(scanner, "Día (LUNES/MARTES/MIERCOLES/JUEVES/VIERNES/SABADO/DOMINGO): ")
                .trim().toUpperCase();
        switch (dia) {
            case "LUNES":
                return DayOfWeek.MONDAY;
            case "MARTES":
                return DayOfWeek.TUESDAY;
            case "MIERCOLES":
            case "MIÉRCOLES":
                return DayOfWeek.WEDNESDAY;
            case "JUEVES":
                return DayOfWeek.THURSDAY;
            case "VIERNES":
                return DayOfWeek.FRIDAY;
            case "SABADO":
            case "SÁBADO":
                return DayOfWeek.SATURDAY;
            case "DOMINGO":
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Día no válido: " + dia);
        }
    }

    private List<HorarioAtencion> leerHorarios(Scanner scanner) {
        List<HorarioAtencion> horarios = new ArrayList<>();
        int cantidad = leerEntero(scanner, "Cantidad de bloques de atención: ");
        for (int i = 1; i <= cantidad; i++) {
            System.out.println("Bloque " + i + ":");
            DayOfWeek dia = leerDia(scanner);
            LocalTime inicio = LocalTime.parse(leer(scanner, "Hora de inicio (HH:MM): ").trim());
            LocalTime fin = LocalTime.parse(leer(scanner, "Hora de fin (HH:MM): ").trim());
            boolean guardia = leerBooleano(scanner, "¿Es día de guardia?");
            horarios.add(new HorarioAtencion(dia, inicio, fin, guardia));
        }
        return horarios;
    }

    private List<Lote> leerLotes(Scanner scanner) {
        List<Lote> lotes = new ArrayList<>();
        int cantidad = leerEntero(scanner, "Cantidad de lotes a cargar: ");
        for (int i = 1; i <= cantidad; i++) {
            System.out.println("Lote " + i + ":");
            String numero = leer(scanner, "Número de lote: ");
            int unidades = leerEntero(scanner, "Cantidad de unidades: ");
            LocalDate vencimiento = leerFecha(scanner, "Vencimiento (AAAA-MM-DD): ");
            lotes.add(new Lote(numero, unidades, vencimiento));
        }
        return lotes;
    }

    // Devuelve null si el usuario prefiere conservar los lotes actuales
    private List<Lote> leerLotesOConservar(Scanner scanner) {
        if (!leerBooleano(scanner, "¿Reemplazar los lotes actuales?")) {
            return null;
        }
        return leerLotes(scanner);
    }

    private void resultado(boolean ok, String exito, String error) {
        if (ok) {
            if (exito != null) {
                System.out.println(exito);
            }
        } else {
            System.out.println(error);
        }
    }

    private void volver() {
        System.out.println("Volviendo al menú anterior.");
    }
}