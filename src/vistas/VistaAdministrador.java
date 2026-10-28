package vistas;

import controlador.ControladorAdministracion;
import java.util.Scanner;

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
        String accion = menuGestion(scanner, "veterinarios");
        switch (accion) {
            case "1":
                controlador.altaVeterinario(
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Documento: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Matrícula: "),
                        leer(scanner, "Especialidad: "),
                        leer(scanner, "Horarios de atención: "),
                        leer(scanner, "Días de guardia: "));
                break;
            case "2":
                controlador.bajaVeterinario(leer(scanner, "Documento del veterinario: "));
                break;
            case "3":
                controlador.modificarVeterinario(
                        leer(scanner, "Documento del veterinario: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Matrícula: "),
                        leer(scanner, "Especialidad: "),
                        leer(scanner, "Horarios de atención: "),
                        leer(scanner, "Días de guardia: "));
                break;
            default:
                volver();
        }
    }

    private void gestionarRecepcionistas(Scanner scanner) {
        String accion = menuGestion(scanner, "personal de recepción");
        switch (accion) {
            case "1":
                controlador.altaRecepcionista(
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Documento: "),
                        leer(scanner, "Teléfono: "));
                break;
            case "2":
                controlador.bajaRecepcionista(leer(scanner, "Documento de la recepcionista: "));
                break;
            case "3":
                controlador.modificarRecepcionista(
                        leer(scanner, "Documento de la recepcionista: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Apellido: "),
                        leer(scanner, "Teléfono: "));
                break;
            default:
                volver();
        }
    }

    private void gestionarVacunas(Scanner scanner) {
        String accion = menuGestion(scanner, "vacunas");
        switch (accion) {
            case "1":
                controlador.altaVacuna(
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Especies: "),
                        leer(scanner, "Edad mínima en semanas: "),
                        leer(scanner, "Intervalo de refuerzo en días: "),
                        leer(scanner, "Precio unitario: "),
                        leer(scanner, "Lotes y vencimientos: "));
                break;
            case "2":
                controlador.bajaVacuna(leer(scanner, "Identificador de la vacuna: "));
                break;
            case "3":
                controlador.modificarVacuna(
                        leer(scanner, "Identificador de la vacuna: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Especies: "),
                        leer(scanner, "Edad mínima en semanas: "),
                        leer(scanner, "Intervalo de refuerzo en días: "),
                        leer(scanner, "Precio unitario: "),
                        leer(scanner, "Lotes y vencimientos: "));
                break;
            default:
                volver();
        }
    }

    private void gestionarProductos(Scanner scanner) {
        String accion = menuGestion(scanner, "productos");
        switch (accion) {
            case "1":
                controlador.altaProducto(
                        leer(scanner, "Código: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Stock mínimo: "),
                        leer(scanner, "Precio unitario de venta: "),
                        leer(scanner, "Lotes, cantidades y vencimientos: "),
                        leer(scanner, "Identificador del proveedor: "));
                break;
            case "2":
                controlador.bajaProducto(leer(scanner, "Código del producto: "));
                break;
            case "3":
                controlador.modificarProducto(
                        leer(scanner, "Código del producto: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Stock mínimo: "),
                        leer(scanner, "Precio unitario de venta: "),
                        leer(scanner, "Lotes, cantidades y vencimientos: "),
                        leer(scanner, "Identificador del proveedor: "));
                break;
            default:
                volver();
        }
    }

    private void gestionarProveedores(Scanner scanner) {
        String accion = menuGestion(scanner, "proveedores");
        switch (accion) {
            case "1":
                controlador.altaProveedor(
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Correo: "));
                break;
            case "2":
                controlador.bajaProveedor(leer(scanner, "Identificador del proveedor: "));
                break;
            case "3":
                controlador.modificarProveedor(
                        leer(scanner, "Identificador del proveedor: "),
                        leer(scanner, "Nombre: "),
                        leer(scanner, "Teléfono: "),
                        leer(scanner, "Correo: "));
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
                    controlador.emitirReporteTurnos(
                            leer(scanner, "Documento del veterinario: "),
                            leer(scanner, "Desde: "),
                            leer(scanner, "Hasta: "));
                    break;
                case "2":
                    controlador.emitirReporteStockBajo();
                    break;
                case "3":
                    controlador.emitirReporteLotesPorVencer(
                            leer(scanner, "Fecha actual: "),
                            leer(scanner, "Días de anticipación: "));
                    break;
                case "4":
                    controlador.emitirRecordatorios(leer(scanner, "Fecha actual: "));
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));
    }

    private String menuGestion(Scanner scanner, String elemento) {
        System.out.println("\n--- Gestión de " + elemento + " ---");
        System.out.println("1. Alta");
        System.out.println("2. Baja");
        System.out.println("3. Modificación");
        System.out.println("0. Volver");
        return leer(scanner, "Opción: ");
    }

    private String leer(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private void volver() {
        System.out.println("Volviendo al menú anterior.");
    }
}
