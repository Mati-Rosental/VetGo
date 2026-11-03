import controlador.ControladorAdministracion;
import controlador.ControladorAtencion;
import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.Agenda;
import modelo.Clinica;
import modelo.Factura;
import modelo.Inventario;
import modelo.Tarifario;
import persistencia.Repositorio;
import persistencia.RepositorioMemoria;
import servicio.GestorUrgencias;
import servicio.Notificador;
import servicio.NotificadorLog;
import vistas.VistaAdministrador;
import vistas.VistaRecepcionista;
import vistas.VistaVeterinario;

import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Punto de entrada del sistema de la clínica veterinaria «Patitas».
 * Arma las dependencias (repositorios, modelo, servicios y controladores)
 * y muestra el menú principal por consola.
 */
public class Main {

    public static void main(String[] args) {
        // Persistencia
        // TODO: cuando RepositorioArchivo esté implementado, reemplazar RepositorioMemoria por RepositorioArchivo
        Repositorio<Clinica> clinicaRepositorio = new RepositorioMemoria<>();
        Repositorio<Agenda> agendaRepositorio = new RepositorioMemoria<>();
        Repositorio<Factura> facturaRepositorio = new RepositorioMemoria<>();

        // Modelo: se carga lo guardado en archivo; si no hay nada, se crea nuevo
        Clinica clinica = cargarOCrear(clinicaRepositorio, () -> new Clinica("Patitas"));
        Agenda agenda = cargarOCrear(agendaRepositorio, Agenda::new);
        Inventario inventario = new Inventario();
        Tarifario tarifario = new Tarifario();

        // Servicios
        Notificador notificador = new NotificadorLog();
        GestorUrgencias gestorUrgencias = new GestorUrgencias();

        // Controladores
        ControladorFacturacion controladorFacturacion =
                new ControladorFacturacion(clinica, facturaRepositorio, tarifario);
        ControladorRegistro controladorRegistro =
                new ControladorRegistro(clinicaRepositorio, clinica);
        ControladorTurnos controladorTurnos =
                new ControladorTurnos(clinicaRepositorio, clinica, agendaRepositorio, agenda,
                        gestorUrgencias, controladorFacturacion);
        ControladorAtencion controladorAtencion =
                new ControladorAtencion(clinicaRepositorio, clinica, agendaRepositorio, agenda,
                        inventario, notificador);
        ControladorAdministracion controladorAdministracion = new ControladorAdministracion();

        // Vistas
        VistaRecepcionista vistaRecepcionista =
                new VistaRecepcionista(controladorRegistro, controladorTurnos, controladorFacturacion);
        VistaVeterinario vistaVeterinario = new VistaVeterinario(controladorAtencion);
        VistaAdministrador vistaAdministrador = new VistaAdministrador(controladorAdministracion);

        // Menú principal
        Scanner scanner = new Scanner(System.in);
        String opcion;
        do {
            System.out.println("\n=== Clínica Veterinaria Patitas ===");
            System.out.println("1. Recepcionista");
            System.out.println("2. Veterinario");
            System.out.println("3. Administrador");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = scanner.nextLine().trim();
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
                    System.out.println("Hasta luego.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (!opcion.equals("0"));
        scanner.close();
    }

    /**
     * Devuelve el primer elemento guardado en el repositorio o, si el archivo
     * no existe o está vacío, crea uno nuevo.
     */
    private static <T> T cargarOCrear(Repositorio<T> repositorio, Supplier<T> nuevo) {
        List<T> guardados = repositorio.cargar();
        if (guardados == null || guardados.isEmpty()) {
            return nuevo.get();
        }
        return guardados.get(0);
    }
}
