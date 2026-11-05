import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.SwingUtilities;

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
import vistas.VistaPrincipal;
import vistas.VistaRecepcionista;
import vistas.VistaVeterinario;

public class App {

    private static final String ARCHIVO_DATOS = "vetgo.dat";

    public static void main(String[] args) {
        // Inicializar el repositorio
        Repositorio repositorio = new RepositorioArchivo(ARCHIVO_DATOS);
        EstadoSistema estado;

        // Recuperar el estado guardado si existe
        if (new File(ARCHIVO_DATOS).exists()) {
            estado = repositorio.cargar();
        } else {
            estado = null;
        }

        // Crear el estado inicial si no hay datos guardados
        if (estado == null) {
            Map<TipoAtencion, Double> precios = new HashMap<>();
            precios.put(TipoAtencion.CONSULTA_GENERAL, 10000.0);
            precios.put(TipoAtencion.VACUNACION, 7000.0);
            precios.put(TipoAtencion.CIRUGIA, 50000.0);
            precios.put(TipoAtencion.URGENCIA, 15000.0);

            Tarifario tarifario = new Tarifario(precios, 2000.0, 0.2);

            estado = new EstadoSistema(
                    new Clinica("VetGo"),
                    new Agenda(),
                    new Inventario(),
                    new ArrayList<Factura>(),
                    tarifario
            );

            repositorio.guardar(estado);
        }

        // Inicializar los servicios compartidos
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

        // Inicializar los controladores
        ControladorFacturacion controladorFacturacion = new ControladorFacturacion(
                estado,
                repositorio
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

        VistaVeterinario vistaVeterinario = new VistaVeterinario(controladorAtencion);

        VistaAdministrador vistaAdministrador = new VistaAdministrador(controladorAdministracion);

        
        Scanner scanner = new Scanner(System.in);
        // Hacemos que estado sea final para utilizarse en el lambda
        final EstadoSistema estadoFinal = estado;

        // Guardar el estado antes de finalizar
        // Esto lo corre la ventana de swing antes de cerrarse
        Runnable salir = () -> {
            repositorio.guardar(estadoFinal);
            scanner.close();
            System.exit(0);
        };
        // Mostrar la ventana inicial para elegir la vista
        // Usa invokeLater para correr la ventana en su propio hilo
        // Recibe un runnable que se crea mediante lambda
        SwingUtilities.invokeLater(() -> new VistaPrincipal(
                vistaRecepcionista,
                vistaVeterinario,
                vistaAdministrador,
                scanner,
                salir
        ).setVisible(true));
    }
}