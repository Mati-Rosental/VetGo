package vistas.consola;

import controlador.ControladorAdministracion;
import modelo.agenda.Turno;
import modelo.enumeradores.Especie;
import modelo.inventario.Lote;
import modelo.inventario.Producto;
import modelo.personas.HorarioAtencion;
import vistas.interfaces.VistaAdministrador;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class VistaAdministradorConsola implements VistaAdministrador {
    private final ControladorAdministracion controlador;

    public VistaAdministradorConsola(ControladorAdministracion controlador) {
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
            opcion = Entrada.textoOpcional(scanner, "Opción: ");
            try {
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
                        Entrada.mostrarOpcionInvalida(opcion);
                }
            } catch (RuntimeException e) {
                Entrada.mostrarError(e);
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
                        leer(scanner, "CUIT del proveedor: "),
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
                        leer(scanner, "CUIT del proveedor: ")),
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
                        leer(scanner, "CUIT del proveedor: ")),
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
                        leer(scanner, "CUIT: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Correo: ")),
                        "Proveedor registrado correctamente.",
                        "No se pudo registrar el proveedor.");
                break;
            case "2":
                resultado(controlador.bajaProveedor(leer(scanner, "CUIT del proveedor: ")),
                        "Proveedor dado de baja correctamente.",
                        "No se pudo dar de baja el proveedor.");
                break;
            case "3":
                resultado(controlador.modificarProveedor(
                        leer(scanner, "CUIT del proveedor: "),
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
            opcion = Entrada.textoOpcional(scanner, "Opción: ");

            try {
                switch (opcion) {
                    case "1":
                        mostrarReporteTurnos(
                                controlador.emitirReporteTurnos(
                                        leer(scanner, "Documento del veterinario: "),
                                        leerFecha(scanner, "Desde (AAAA-MM-DD): "),
                                        leerFecha(scanner, "Hasta (AAAA-MM-DD): ")));
                        break;

                    case "2":
                        mostrarReporteProductos(
                                controlador.emitirReporteStockBajo());
                        break;

                    case "3":
                        mostrarReporteLotes(
                                controlador.emitirReporteLotesPorVencer(
                                        LocalDate.now(),
                                        leerEntero(scanner, "Días de anticipación: ")));
                        break;

                    case "4":
                        resultado(controlador.emitirRecordatorios(
                                LocalDate.now()),
                                "Recordatorios emitidos correctamente.",
                                "No se pudieron emitir los recordatorios.");
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

    private String menuGestion(Scanner scanner, String elemento, boolean conAgregarLotes) {
        System.out.println("\n--- Gestión de " + elemento + " ---");
        System.out.println("1. Alta");
        System.out.println("2. Baja");
        System.out.println("3. Modificación");
        if (conAgregarLotes) {
            System.out.println("4. Agregar lotes");
        }
        System.out.println("0. Volver");

        String opcion = Entrada.textoOpcional(scanner, "Opción: ");
        boolean valida = opcion.equals("0") || opcion.equals("1") || opcion.equals("2") || opcion.equals("3")
                || (conAgregarLotes && opcion.equals("4"));
        if (!valida) {
            Entrada.mostrarOpcionInvalida(opcion);
            return "0";
        }
        return opcion;
    }

    private String leer(Scanner scanner, String mensaje) {
        return Entrada.texto(scanner, mensaje);
    }

    private int leerEntero(Scanner scanner, String mensaje) {
        return Entrada.entero(scanner, mensaje);
    }

    private int leerEntero(Scanner scanner, String mensaje, int minimo) {
        return Entrada.entero(scanner, mensaje, minimo);
    }

    private double leerDecimal(Scanner scanner, String mensaje) {
        return Entrada.decimal(scanner, mensaje);
    }

    private LocalDate leerFecha(Scanner scanner, String mensaje) {
        return Entrada.fecha(scanner, mensaje);
    }

    private boolean leerBooleano(Scanner scanner, String mensaje) {
        return Entrada.booleano(scanner, mensaje + " (s/n): ");
    }

    private Set<Especie> leerEspecies(Scanner scanner) {
        return Entrada.elegirVarios(scanner, "Especies", Especie.values());
    }

    private DayOfWeek leerDia(Scanner scanner) {
        int indice = Entrada.elegirIndice(scanner, "Día",
                Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"));
        return DayOfWeek.of(indice + 1);
    }

    private List<HorarioAtencion> leerHorarios(Scanner scanner) {
        List<HorarioAtencion> horarios = new ArrayList<>();
        int cantidad = leerEntero(scanner, "Cantidad de bloques de atención: ", 1);
        for (int i = 1; i <= cantidad; i++) {
            System.out.println("Bloque " + i + ":");
            DayOfWeek dia = leerDia(scanner);
            LocalTime inicio = Entrada.hora(scanner, "Hora de inicio (HH:MM): ");
            LocalTime fin = Entrada.hora(scanner, "Hora de fin (HH:MM): ");
            if (!fin.isAfter(inicio)) {
                throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
            }
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
            int unidades = leerEntero(scanner, "Cantidad de unidades: ", 1);
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

    private void mostrarReporteTurnos(List<Turno> turnos) {
        System.out.println("\n--- Reporte de turnos ---");

        if (turnos == null) {
            System.out.println("No se pudo generar el reporte. Verifique los datos ingresados.");
            return;
        }

        if (turnos.isEmpty()) {
            System.out.println("No hay turnos para los criterios seleccionados.");
            return;
        }

        for (Turno turno : turnos) {
            System.out.println(turno.getDescripcion());
        }
    }

    private void mostrarReporteProductos(List<Producto> productos) {
        System.out.println("\n--- Productos con stock bajo ---");

        if (productos == null) {
            System.out.println("No se pudo generar el reporte.");
            return;
        }

        if (productos.isEmpty()) {
            System.out.println("No hay productos con stock bajo.");
            return;
        }

        for (Producto producto : productos) {
            System.out.println(producto.getDescripcion());
        }
    }

    private void mostrarReporteLotes(List<Lote> lotes) {
        System.out.println("\n--- Lotes próximos a vencer ---");

        if (lotes == null) {
            System.out.println("No se pudo generar el reporte. Verifique los datos ingresados.");
            return;
        }

        if (lotes.isEmpty()) {
            System.out.println("No hay lotes próximos a vencer.");
            return;
        }

        for (Lote lote : lotes) {
            System.out.println(lote.getDescripcion());
        }
    }
}