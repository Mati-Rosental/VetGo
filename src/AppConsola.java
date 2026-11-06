
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import controlador.ControladorAdministracion;
import controlador.ControladorAtencion;
import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.agenda.Agenda;
import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.enumeradores.TamanioPerro;
import modelo.enumeradores.TipoAtencion;
import modelo.facturacion.Factura;
import modelo.facturacion.Tarifario;
import modelo.inventario.Inventario;
import modelo.inventario.Lote;
import modelo.personas.Clinica;
import modelo.personas.HorarioAtencion;
import modelo.mascotas.Mascota;
import persistencia.EstadoSistema;
import persistencia.Repositorio;
import persistencia.RepositorioArchivo;
import servicio.GeneradorReportes;
import servicio.GestorUrgencias;
import servicio.Notificador;
import servicio.NotificadorConsola;
import servicio.ServicioRecordatorios;
import vistas.consola.VistaAdministradorConsola;
import vistas.consola.VistaRecepcionistaConsola;
import vistas.consola.VistaVeterinarioConsola;
import vistas.interfaces.VistaAdministrador;
import vistas.interfaces.VistaRecepcionista;
import vistas.interfaces.VistaVeterinario;
import vistas.swing.VistaPrincipal;

public class AppConsola {

    private static final String ARCHIVO_DATOS = "vetgo.dat";

    public static void main(String[] args) {
        // Inicializar el repositorio
        Repositorio repositorio = new RepositorioArchivo(ARCHIVO_DATOS);
        EstadoSistema estado = null;

        // Recuperar el estado guardado si existe
        if (new File(ARCHIVO_DATOS).exists()) {
            estado = repositorio.cargar();
        }

        // Determinar si es necesario inicializar los datos
        boolean cargarDatosIniciales = estado == null;

        // Crear el estado inicial si no hay datos guardados
        if (cargarDatosIniciales) {
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

        // Cargar datos de prueba solamente si no había un estado guardado
        if (cargarDatosIniciales) {
            cargarDatosPrueba(controladorRegistro, controladorTurnos, controladorAdministracion);
            repositorio.guardar(estado);
        }

        // Inicializar las vistas
        VistaRecepcionista vistaRecepcionista = new VistaRecepcionistaConsola(
                controladorRegistro,
                controladorTurnos,
                controladorFacturacion
        );

        VistaVeterinario vistaVeterinario = new VistaVeterinarioConsola(controladorAtencion);

        VistaAdministrador vistaAdministrador = new VistaAdministradorConsola(controladorAdministracion);

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

    // Cargar datos iniciales utilizando los controladores
    private static void cargarDatosPrueba(
            ControladorRegistro registro,
            ControladorTurnos turnos,
            ControladorAdministracion administracion) {

        // Registrar dueños
        verificar(registro.altaDuenio(
                "Carlos", "Gómez", "10000001", "1111111111", "carlos@gmail.com", true));

        verificar(registro.altaDuenio(
                "María", "López", "10000002", "2222222222", "maria@gmail.com", false));

        // Registrar veterinarios
        verificar(administracion.altaVeterinario(
                "Juan", "Pérez", "20000001", "3333333333",
                "MAT-001", "Clínica general", crearHorarios(true)));

        verificar(administracion.altaVeterinario(
                "Laura", "Fernández", "20000002", "4444444444",
                "MAT-002", "Cirugía", crearHorarios(false)));

        // Registrar recepcionistas
        verificar(administracion.altaRecepcionista(
                "Ana", "Martínez", "30000001", "5555555555"));

        verificar(administracion.altaRecepcionista(
                "Pedro", "Sánchez", "30000002", "6666666666"));

        // Registrar administradores
        verificar(administracion.altaAdministrador(
                "Roberto", "Díaz", "40000001", "7777777777"));

        verificar(administracion.altaAdministrador(
                "Lucía", "Torres", "40000002", "8888888888"));

        // Registrar mascotas
        verificar(registro.altaMascota(
                "10000001", Especie.PERRO, "Max", "Labrador",
                Sexo.MACHO, LocalDate.now().minusYears(3), 25.0,
                "CHIP-001", TamanioPerro.GRANDE, false, false));

        verificar(registro.altaMascota(
                "10000001", Especie.GATO, "Luna", "Siamés",
                Sexo.HEMBRA, LocalDate.now().minusYears(2), 4.5,
                null, null, true, false));

        verificar(registro.altaMascota(
                "10000002", Especie.AVE, "Piolín", "Canario",
                Sexo.MACHO, LocalDate.now().minusYears(1), 0.2,
                null, null, false, true));

        verificar(registro.altaMascota(
                "10000002", Especie.CONEJO, "Nube", "Holandés",
                Sexo.HEMBRA, LocalDate.now().minusYears(1), 2.0,
                null, null, false, false));

        // Recuperar las mascotas registradas
        List<Mascota> mascotas = registro.obtenerMascotas();

        Mascota perro = buscarMascota(mascotas, "Max");
        Mascota gato = buscarMascota(mascotas, "Luna");
        Mascota ave = buscarMascota(mascotas, "Piolín");

        // Registrar turnos
        LocalDate manana = LocalDate.now().plusDays(1);

        verificar(turnos.asignarTurno(
                perro.getId(), "20000001", TipoAtencion.CONSULTA_GENERAL,
                manana.atTime(9, 0)));

        verificar(turnos.asignarTurno(
                gato.getId(), "20000002", TipoAtencion.VACUNACION,
                manana.atTime(9, 0)));

        verificar(turnos.asignarTurno(
                ave.getId(), "20000001", TipoAtencion.CONSULTA_GENERAL,
                manana.atTime(10, 0)));

        // Registrar proveedores
        verificar(administracion.altaProveedor(
                "30-11111111-1", "Distribuidora Veterinaria", "1111222233",
                "ventas@distribuidora.com"));

        verificar(administracion.altaProveedor(
                "30-22222222-2", "Insumos Animales", "2222333344",
                "contacto@insumos.com"));

        // Registrar vacunas
        verificar(administracion.altaVacuna(
                "VAC-001", "Antirrábica",
                EnumSet.allOf(Especie.class),
                12, 365, 7000.0, 5,
                "30-11111111-1", crearLotes("VAC-001", 20)));

        verificar(administracion.altaVacuna(
                "VAC-002", "Triple Felina",
                EnumSet.of(Especie.GATO),
                12, 365, 8500.0, 5,
                "30-22222222-2", crearLotes("VAC-002", 20)));

        // Registrar productos
        verificar(administracion.altaProducto(
                "PROD-001", "Antiparasitario",
                5, 4500.0, crearLotes("PROD-001", 30),
                "30-11111111-1"));

        verificar(administracion.altaProducto(
                "PROD-002", "Antiinflamatorio",
                5, 3200.0, crearLotes("PROD-002", 30),
                "30-22222222-2"));
    }

    // Crear horarios de atención para todos los días de la semana
    private static List<HorarioAtencion> crearHorarios(boolean deGuardia) {
        List<HorarioAtencion> horarios = new ArrayList<>();

        for (DayOfWeek dia : DayOfWeek.values()) {
            horarios.add(new HorarioAtencion(
                    dia,
                    LocalTime.of(8, 0),
                    LocalTime.of(18, 0),
                    deGuardia
            ));
        }

        return horarios;
    }

    // Crear lotes con fecha de vencimiento a un año
    private static List<Lote> crearLotes(String prefijo, int cantidad) {
        List<Lote> lotes = new ArrayList<>();

        lotes.add(new Lote(
                prefijo + "-LOTE-001",
                cantidad,
                LocalDate.now().plusYears(1)
        ));

        return lotes;
    }

    // Buscar una mascota por su nombre
    private static Mascota buscarMascota(List<Mascota> mascotas, String nombre) {
        for (Mascota mascota : mascotas) {
            if (mascota.getNombre().equalsIgnoreCase(nombre)) {
                return mascota;
            }
        }

        throw new IllegalStateException("No se encontró la mascota: " + nombre);
    }

    // Interrumpir la carga si una operación falla
    private static void verificar(boolean resultado) {
        if (!resultado) {
            throw new IllegalStateException("No se pudo cargar un dato de prueba");
        }
    }
}
