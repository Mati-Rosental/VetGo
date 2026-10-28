import controlador.ControladorAdministracion;
import controlador.ControladorAtencion;
import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import java.util.Scanner;
import vistas.VistaAdministrador;
import vistas.VistaRecepcionista;
import vistas.VistaVeterinario;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        VistaRecepcionista vistaRecepcionista = new VistaRecepcionista(
                new ControladorRegistro(),
                new ControladorTurnos(),
                new ControladorFacturacion());
        VistaVeterinario vistaVeterinario = new VistaVeterinario(new ControladorAtencion());
        VistaAdministrador vistaAdministrador = new VistaAdministrador(new ControladorAdministracion());
        String opcion;

        do {
            System.out.println("\n=== VetGo ===");
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
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (!opcion.equals("0"));

        scanner.close();
    }
}
