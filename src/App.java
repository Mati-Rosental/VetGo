
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import controlador.ControladorAdministracion;
import controlador.ControladorAtencion;
import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.agenda.Agenda;
import modelo.enumeradores.TipoAtencion;
import modelo.facturacion.Factura;
import modelo.facturacion.Tarifario;
import modelo.inventario.Inventario;
import modelo.personas.Clinica;
import persistencia.EstadoSistema;
import persistencia.Repositorio;
import persistencia.RepositorioArchivo;
import servicio.GeneradorReportes;
import servicio.GestorUrgencias;
import servicio.Notificador;
import servicio.NotificadorConsola;
import servicio.ServicioRecordatorios;
import vistas.VistaAdministrador;
import vistas.VistaRecepcionista;
import vistas.VistaVeterinario;

public class App {

    private static final String ARCHIVO_DATOS = "vetgo.dat";

    public static void main(String[] args) {
        // Inicializar el repositorio y recuperar el estado guardado
        Repositorio repositorio = new RepositorioArchivo(ARCHIVO_DATOS);
        EstadoSistema estado;

        if (new File(ARCHIVO_DATOS).exists()) {
            estado = repositorio.cargar();
        } else {
            estado = null;
        }

        // Crear un estado inicial si no existen datos guardados
        if (estado == null) {
            estado = new EstadoSistema(
                    new Clinica("VetGo"),
                    new Agenda(),
                    new Inventario(),
                    new ArrayList<Factura>()
            );

            repositorio.guardar(estado);
        }

        // Inicializar servicios compartidos
        Notificador notificador = new NotificadorConsola();
        ServicioRecordatorios servicioRecordatorios = new ServicioRecordatorios(notificador);

        GeneradorReportes generadorReportes = new GeneradorReportes(
                estado.getAgenda(),
                estado.getInventario()
        );

        GestorUrgencias gestorUrgencias = new GestorUrgencias(
                estado.getAgenda(),
                notificador
        );

        // Configurar los precios del tarifario
        Map<TipoAtencion, Double> precios = new HashMap<>();
        precios.put(TipoAtencion.CONSULTA_GENERAL, 10000.0);
        precios.put(TipoAtencion.VACUNACION, 7000.0);
        precios.put(TipoAtencion.CIRUGIA, 50000.0);
        precios.put(TipoAtencion.URGENCIA, 15000.0);

        Tarifario tarifario = new Tarifario(precios, 2000.0, 0.2);

        // Inicializar los controladores
        ControladorFacturacion controladorFacturacion = new ControladorFacturacion(
                estado,
                repositorio,
                tarifario
        );

        ControladorRegistro controladorRegistro = new ControladorRegistro(
                repositorio,
                estado
        );

        ControladorTurnos controladorTurnos = new ControladorTurnos(
                repositorio,
                estado,
                gestorUrgencias,
                controladorFacturacion
        );

        ControladorAtencion controladorAtencion = new ControladorAtencion(
                repositorio,
                estado,
                notificador
        );

        ControladorAdministracion controladorAdministracion = new ControladorAdministracion(
                repositorio,
                estado,
                generadorReportes,
                servicioRecordatorios
        );

        // Inicializar las vistas
        VistaRecepcionista vistaRecepcionista = new VistaRecepcionista(
                controladorRegistro,
                controladorTurnos,
                controladorFacturacion
        );

        VistaVeterinario vistaVeterinario = new VistaVeterinario(
                controladorAtencion
        );

        VistaAdministrador vistaAdministrador = new VistaAdministrador(
                controladorAdministracion
        );

        // Ejecutar el menú principal
        Scanner scanner = new Scanner(System.in);
        String opcion;

        do {
            System.out.println("\n========== VETGO ==========");
            System.out.println("1. Recepción");
            System.out.println("2. Veterinario");
            System.out.println("3. Administración");
            System.out.println("0. Salir");

            System.out.print("Opción: ");
            opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    vistaRecepcionista.iniciarMenu(scanner);
                    break;

                case "2":
                    vistaVeterinario.iniciarMenu(scanner);
                    break;

                case "3":
                    vistaAdministrador.iniciarMenu(scanner);
                    break;

                case "0":
                    System.out.println("Cerrando VetGo...");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }

        } while (!opcion.equals("0"));

        // Guardar el estado antes de finalizar
        repositorio.guardar(estado);
        scanner.close();
    }
}
