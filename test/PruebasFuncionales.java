import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controlador.ControladorAdministracion;
import controlador.ControladorAtencion;
import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.atencion.Consulta;
import modelo.enumeradores.Especie;
import modelo.enumeradores.EstadoTurno;
import modelo.enumeradores.Sexo;
import modelo.enumeradores.TamanioPerro;
import modelo.enumeradores.TipoAtencion;
import modelo.facturacion.Factura;
import modelo.facturacion.Tarifario;
import modelo.facturacion.pago.Credito;
import modelo.facturacion.pago.Debito;
import modelo.facturacion.pago.Efectivo;
import modelo.inventario.Inventario;
import modelo.inventario.Lote;
import modelo.inventario.Producto;
import modelo.mascotas.Mascota;
import modelo.personas.Clinica;
import modelo.personas.Duenio;
import modelo.personas.HorarioAtencion;
import modelo.personas.Persona;
import persistencia.EstadoSistema;
import persistencia.Repositorio;
import persistencia.RepositorioArchivo;
import servicio.GeneradorReportes;
import servicio.GestorUrgencias;
import servicio.Notificador;
import servicio.ServicioRecordatorios;

/**
 * Pruebas funcionales: recorren los casos de uso principales usando los mismos
 * controladores que usan las vistas (consola y Swing), con un repositorio en memoria
 * y un notificador que registra los mensajes. No pasan por la interfaz grafica.
 */
public final class PruebasFuncionales {

    private static final LocalDate HOY = LocalDate.now();
    /** Fecha fija futura para turnos deterministas. Todos los veterinarios atienden todos los dias. */
    private static final LocalDate DIA = LocalDate.of(2031, 3, 3);

    private PruebasFuncionales() {
    }

    // ---------- escenario ----------
    private static final class Mensaje {
        final Persona destinatario;
        final String texto;
        Mensaje(Persona d, String t) { destinatario = d; texto = t; }
    }

    private static final class Escenario {
        final List<Mensaje> mensajes = new ArrayList<>();
        final Notificador notificador = (dest, msg) -> mensajes.add(new Mensaje(dest, msg));
        int guardados = 0;
        final Repositorio repo = new Repositorio() {
            @Override public void guardar(EstadoSistema s) { guardados++; }
            @Override public EstadoSistema cargar() { return null; }
        };
        final EstadoSistema estado;
        final ControladorRegistro registro;
        final ControladorTurnos turnos;
        final ControladorAtencion atencion;
        final ControladorFacturacion facturacion;
        final ControladorAdministracion admin;

        Escenario() {
            Map<TipoAtencion, Double> precios = new HashMap<>();
            precios.put(TipoAtencion.CONSULTA_GENERAL, 10000.0);
            precios.put(TipoAtencion.VACUNACION, 7000.0);
            precios.put(TipoAtencion.CIRUGIA, 50000.0);
            precios.put(TipoAtencion.URGENCIA, 15000.0);
            estado = new EstadoSistema(new Clinica("Patitas"), new Agenda(), new Inventario(),
                    new ArrayList<Factura>(), new Tarifario(precios, 2000.0, 0.2));
            GeneradorReportes reportes = new GeneradorReportes(estado.getAgenda(), estado.getInventario());
            GestorUrgencias urgencias = new GestorUrgencias(estado.getAgenda(), notificador);
            ServicioRecordatorios recordatorios = new ServicioRecordatorios(notificador);
            facturacion = new ControladorFacturacion(estado, repo);
            registro = new ControladorRegistro(repo, estado);
            turnos = new ControladorTurnos(repo, estado, urgencias, facturacion);
            atencion = new ControladorAtencion(repo, estado, notificador);
            admin = new ControladorAdministracion(repo, estado, reportes, recordatorios);

            // Datos base cargados con los propios controladores
            admin.altaAdministrador("Roberto", "Diaz", "A1", "700");
            admin.altaRecepcionista("Ana", "Martinez", "R1", "500");
            admin.altaVeterinario("Juan", "Perez", "V1", "300", "MAT-1", "General", semana(9, 0, 18, 0, false));
            // V2: jornada completa y de guardia, para las pruebas que dependen de la hora actual
            admin.altaVeterinario("Laura", "Fernandez", "V2", "301", "MAT-2", "Cirugia", semana(0, 0, 23, 59, true));
            registro.altaDuenio("Carlos", "Gomez", "D1", "111", "c@test.com", false);
            registro.altaDuenio("Maria", "Lopez", "D2", "222", "m@test.com", true);
            registro.altaMascota("D1", Especie.PERRO, "Max", "Labrador", Sexo.MACHO, HOY.minusYears(3), 25.0,
                    "CHIP-1", TamanioPerro.GRANDE, false, false);
            registro.altaMascota("D2", Especie.GATO, "Luna", "Siames", Sexo.HEMBRA, HOY.minusYears(2), 4.5,
                    null, null, true, false);
            admin.altaProveedor("30-1", "Distribuidora", "333", "d@test.com");
        }

        Mascota mascota(String nombre) {
            for (Mascota m : estado.getClinica().getMascotas()) {
                if (m.getNombre().equals(nombre)) return m;
            }
            throw new IllegalStateException("No existe " + nombre);
        }

        Turno turnoDe(String idMascota) {
            Turno ultimo = null;
            for (Turno t : estado.getAgenda().getTurnos()) {
                if (t.getMascota().getId().equals(idMascota)) ultimo = t;
            }
            return ultimo;
        }

        Consulta ultimaConsulta(Mascota m) {
            List<Consulta> cs = m.getHistoriaClinica().getConsultas();
            return cs.get(cs.size() - 1);
        }

        Factura ultimaFactura() {
            List<Factura> fs = estado.getFacturas();
            return fs.get(fs.size() - 1);
        }

        /** Asigna un turno, inicia la consulta y devuelve su id. */
        Consulta consultaEnCurso(String nombreMascota, String vet, LocalDateTime hora, String motivo) {
            Mascota m = mascota(nombreMascota);
            EjecutorPruebas.esperar(turnos.asignarTurno(m.getId(), vet, TipoAtencion.CONSULTA_GENERAL, hora), "asignar turno de preparacion");
            EjecutorPruebas.esperar(atencion.iniciarConsulta(turnoDe(m.getId()).getId(), motivo), "iniciar consulta de preparacion");
            return ultimaConsulta(m);
        }
    }

    private static List<HorarioAtencion> semana(int hi, int mi, int hf, int mf, boolean guardia) {
        List<HorarioAtencion> h = new ArrayList<>();
        for (DayOfWeek d : DayOfWeek.values()) {
            h.add(new HorarioAtencion(d, LocalTime.of(hi, mi), LocalTime.of(hf, mf), guardia));
        }
        return h;
    }

    private static List<Lote> lotes(int cantidad, int diasVencimiento) {
        List<Lote> l = new ArrayList<>();
        l.add(new Lote("L-" + diasVencimiento, cantidad, HOY.plusDays(diasVencimiento)));
        return l;
    }

    private static void caso(String id, String d, EjecutorPruebas.Caso c) { EjecutorPruebas.caso(id, d, c); }
    private static void esperar(boolean b, String m) { EjecutorPruebas.esperar(b, m); }
    private static void igual(Object e, Object o, String m) { EjecutorPruebas.esperarIgual(e, o, m); }
    private static void igual(double e, double o, String m) { EjecutorPruebas.esperarIgual(e, o, m); }

    /**
     * Los casos que usan la hora actual (LocalDateTime.now() en el sistema) necesitan que
     * los turnos de prueba caigan en el mismo dia: se omiten entre ~22:40 y ~00:15.
     */
    private static boolean cercaDeMedianoche() {
        LocalDateTime ahora = LocalDateTime.now();
        return !ahora.minusMinutes(15).toLocalDate().equals(ahora.toLocalDate())
                || !ahora.plusMinutes(100).toLocalDate().equals(ahora.toLocalDate());
    }

    public static void ejecutar() {
        System.out.println("\n######## PRUEBAS FUNCIONALES (por caso de uso) ########");
        registro();
        turnos();
        atencion();
        facturacion();
        administracion();
        persistencia();
    }

    // =====================================================================
    private static void registro() {
        caso("PF-01", "CU-01 Registrar dueño: alta correcta y rechazo de documento duplicado", () -> {
            Escenario e = new Escenario();
            esperar(e.registro.altaDuenio("Pedro", "Ruiz", "D9", "999", "p@test.com", true), "alta nueva");
            esperar(!e.registro.altaDuenio("Otro", "Nombre", "D9", "888", "o@test.com", false), "documento duplicado");
            igual(3, e.registro.obtenerDuenios().size(), "no se creo un duplicado");
            esperar(e.estado.getClinica().buscarDuenio("D9").tienePlanSalud(), "conserva el plan de salud");
        });

        caso("PF-02", "CU-02 Registrar mascota: cuatro especies con datos propios; dueño inexistente se rechaza", () -> {
            Escenario e = new Escenario();
            esperar(e.registro.altaMascota("D1", Especie.GATO, "Mora", "x", Sexo.HEMBRA, HOY.minusYears(1), 3, null, null, true, false), "gato");
            esperar(e.registro.altaMascota("D1", Especie.AVE, "Lolo", "x", Sexo.MACHO, HOY.minusYears(1), 0.3, null, null, false, true), "ave");
            esperar(e.registro.altaMascota("D1", Especie.CONEJO, "Nube", "x", Sexo.HEMBRA, HOY.minusYears(1), 2, null, null, false, false), "conejo");
            igual(4, e.estado.getClinica().buscarDuenio("D1").getMascotas().size(), "Max + 3 nuevas asociadas al dueño");
            igual(Especie.CONEJO, e.mascota("Nube").getEspecie(), "especie del conejo");
            esperar(!e.registro.altaMascota("NOEXISTE", Especie.PERRO, "X", "x", Sexo.MACHO, HOY, 1, "C", TamanioPerro.PEQUENO, false, false), "dueño inexistente");
        });

        caso("PF-03", "CU-03 Transferir mascota: conserva la historia clinica; mismo dueño se rechaza", () -> {
            Escenario e = new Escenario();
            Mascota max = e.mascota("Max");
            Consulta c = e.consultaEnCurso("Max", "V1", DIA.atTime(10, 0), "control");
            esperar(!e.registro.transferirMascota(max.getId(), "D1"), "transferir al mismo dueño");
            esperar(e.registro.transferirMascota(max.getId(), "D2"), "transferir a D2");
            igual("D2", max.getDuenio().getDocumento(), "nuevo dueño");
            igual(0, e.estado.getClinica().buscarDuenio("D1").getMascotas().size(), "D1 ya no la tiene");
            igual(1, max.getHistoriaClinica().getConsultas().size(), "la consulta previa sigue en la historia");
            esperar(e.estado.getClinica().buscarConsulta(c.getId()) != null, "la consulta se sigue encontrando");
            esperar(!e.registro.transferirMascota(max.getId(), "NOEXISTE"), "nuevo dueño inexistente");
        });
    }

    // =====================================================================
    private static void turnos() {
        caso("PF-04", "CU-04 Asignar turno: valido, superpuesto, fuera de horario y con seña pendiente (Secuencia 2)", () -> {
            Escenario e = new Escenario();
            String max = e.mascota("Max").getId();
            String luna = e.mascota("Luna").getId();
            esperar(e.turnos.asignarTurno(max, "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(10, 0)), "turno valido");
            igual(30, e.turnoDe(max).getDuracionMin(), "duracion de consulta general");
            igual(EstadoTurno.PROGRAMADO, e.turnoDe(max).getEstado(), "estado programado");
            esperar(!e.turnos.asignarTurno(luna, "V1", TipoAtencion.VACUNACION, DIA.atTime(10, 15)), "superpuesto");
            esperar(!e.turnos.asignarTurno(luna, "V1", TipoAtencion.CIRUGIA, DIA.atTime(17, 0)), "cirugia que excede el horario");
            esperar(!e.turnos.asignarTurno(luna, "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(8, 0)), "antes de abrir");
            esperar(e.turnos.asignarTurno(luna, "V1", TipoAtencion.VACUNACION, DIA.atTime(10, 30)), "contiguo valido");
            igual(2, e.estado.getAgenda().getTurnos().size(), "el turno rechazado no quedo en la agenda");
            esperar(!e.turnos.asignarTurno("NOEXISTE", "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(12, 0)), "mascota inexistente");
        });

        caso("PF-05", "CU-05 Reprogramar turno: a horario libre si, a horario ocupado no", () -> {
            Escenario e = new Escenario();
            String max = e.mascota("Max").getId();
            String luna = e.mascota("Luna").getId();
            e.turnos.asignarTurno(max, "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(10, 0));
            e.turnos.asignarTurno(luna, "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(11, 0));
            String idMax = e.turnoDe(max).getId();
            esperar(e.turnos.reprogramarTurno(idMax, DIA.atTime(14, 0)), "a horario libre");
            igual(DIA.atTime(14, 0), e.turnoDe(max).getFechaHora(), "nueva fecha");
            esperar(!e.turnos.reprogramarTurno(idMax, DIA.atTime(11, 15)), "a horario ocupado");
            igual(DIA.atTime(14, 0), e.turnoDe(max).getFechaHora(), "el rechazo no modifica el turno");
            esperar(!e.turnos.reprogramarTurno("NOEXISTE", DIA.atTime(15, 0)), "turno inexistente");
        });

        caso("PF-06", "CU-06 Cancelar turno: sin cargo con mas de 2 h; con menos de 2 h genera factura del 20 %", () -> {
            Escenario e = new Escenario();
            String max = e.mascota("Max").getId();
            String luna = e.mascota("Luna").getId();
            e.turnos.asignarTurno(max, "V2", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(10, 0));
            esperar(e.turnos.cancelarTurno(e.turnoDe(max).getId()), "cancelar con mucha anticipacion");
            igual(EstadoTurno.CANCELADO, e.turnoDe(max).getEstado(), "turno cancelado");
            igual(0, e.estado.getFacturas().size(), "sin cargo");
            if (cercaDeMedianoche()) {
                EjecutorPruebas.omitir("la parte 'con cargo' usa la hora actual y se omite cerca de la medianoche");
            }
            e.turnos.asignarTurno(luna, "V2", TipoAtencion.CONSULTA_GENERAL, LocalDateTime.now().plusMinutes(60));
            esperar(e.turnos.cancelarTurno(e.turnoDe(luna).getId()), "cancelar con 1 h de anticipacion");
            igual(1, e.estado.getFacturas().size(), "se genero la factura del cargo");
            igual(2000.0, e.ultimaFactura().getTotal(), "20 % de 10000");
        });

        caso("PF-07", "CU-07/14 Ausencias: tres seguidas exigen seña; al pagarla se puede pedir turno y se descuenta de la proxima factura", () -> {
            Escenario e = new Escenario();
            String max = e.mascota("Max").getId();
            Duenio d1 = e.estado.getClinica().buscarDuenio("D1");
            for (int i = 0; i < 3; i++) {
                esperar(e.turnos.asignarTurno(max, "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(9 + i, 0)), "turno " + i);
                esperar(e.turnos.registrarAusencia(e.turnoDe(max).getId()), "ausencia " + i);
            }
            esperar(d1.requiereSenia(), "seña requerida");
            esperar(!e.turnos.asignarTurno(max, "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(13, 0)), "bloqueado sin seña");
            igual(1, e.facturacion.obtenerDueniosConSeniaRequerida().size(), "figura en la lista de dueños con seña");
            esperar(e.facturacion.cobrarSenia("D1"), "cobrar seña");
            esperar(e.turnos.asignarTurno(max, "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(13, 0)), "habilitado tras pagar");
            Consulta c = e.consultaEnCurso("Luna", "V1", DIA.atTime(15, 0), "x"); // otro dueño, sin efecto
            // consulta de Max con seña a favor
            esperar(e.atencion.iniciarConsulta(e.turnoDe(max).getId(), "control"), "iniciar consulta de Max");
            Consulta cMax = e.ultimaConsulta(e.mascota("Max"));
            esperar(e.atencion.confirmarConsulta(cMax.getId()), "confirmar");
            esperar(e.facturacion.facturarConsulta(cMax.getId(), new Efectivo()), "facturar");
            igual(7000.0, e.ultimaFactura().getTotal(), "10000 - 10 % efectivo - 2000 de seña");
            igual(0.0, d1.getSeniaPendiente(), "seña consumida");
        });

        caso("PF-08a", "CU-08 Urgencia sin veterinario de guardia: se rechaza y no se registra", () -> {
            Escenario e = new Escenario();
            esperar(e.admin.bajaVeterinario("V2"), "se da de baja al unico veterinario de guardia");
            esperar(e.turnos.atenderUrgencia(e.mascota("Max").getId()) == null, "sin guardia no hay urgencia");
            igual(0, e.estado.getAgenda().getTurnos().size(), "no se creo ningun turno");
        });

        caso("PF-08b", "CU-08 Urgencia: empieza al terminar el turno en curso, desplaza el siguiente y notifica al dueño", () -> {
            if (cercaDeMedianoche()) {
                EjecutorPruebas.omitir("usa la hora actual; se omite cerca de la medianoche");
            }
            Escenario e = new Escenario();
            Mascota max = e.mascota("Max");
            Mascota luna = e.mascota("Luna");
            LocalDateTime ahora = LocalDateTime.now();
            // V2 esta atendiendo ahora (turno de 30 min que empezo hace 10) y tiene otro turno justo a continuacion
            esperar(e.turnos.asignarTurno(luna.getId(), "V2", TipoAtencion.CONSULTA_GENERAL, ahora.minusMinutes(10)), "turno en curso");
            LocalDateTime finActual = e.turnoDe(luna.getId()).getFin();
            e.registro.altaMascota("D2", Especie.CONEJO, "Nube", "x", Sexo.HEMBRA, HOY.minusYears(1), 2, null, null, false, false);
            Mascota nube = e.mascota("Nube");
            esperar(e.turnos.asignarTurno(nube.getId(), "V2", TipoAtencion.CONSULTA_GENERAL, finActual), "turno siguiente");

            Turno urgencia = e.turnos.atenderUrgencia(max.getId());
            esperar(urgencia != null, "urgencia atendida");
            igual(TipoAtencion.URGENCIA, urgencia.getTipoAtencion(), "tipo urgencia");
            igual(finActual, urgencia.getFechaHora(), "empieza cuando termina el turno en curso");
            igual(urgencia.getFin(), e.turnoDe(nube.getId()).getFechaHora(), "el turno siguiente se desplazo al fin de la urgencia");
            boolean notificado = false;
            for (Mensaje m : e.mensajes) {
                if (m.destinatario == nube.getDuenio() && m.texto.contains("reprogramado")) notificado = true;
            }
            esperar(notificado, "se notifico al dueño del turno desplazado");
        });
    }

    // =====================================================================
    private static void atencion() {
        caso("PF-09", "CU-16/17/21/22/23 Registrar consulta (Secuencia 1): peso, tratamiento, stock, confirmacion y alerta", () -> {
            Escenario e = new Escenario();
            e.admin.altaProducto("MED-1", "Amoxicilina", 5, 3200.0, lotes(6, 60), "30-1");
            Producto med = e.estado.getInventario().buscarProducto("MED-1");
            Consulta c = e.consultaEnCurso("Max", "V1", DIA.atTime(10, 0), "control");
            igual(EstadoTurno.EN_CURSO, c.getTurno().getEstado(), "el turno pasa a en curso");
            esperar(!e.atencion.indicarTratamiento(c.getId(), "MED-1", "1 ml", "c/12h", 5, 1), "sin peso no se indica (RN-M5)");
            esperar(e.atencion.registrarPeso(c.getId(), 26.0), "registrar peso");
            esperar(e.atencion.registrarDiagnostico(c.getId(), "otitis"), "diagnostico");
            esperar(!e.atencion.indicarTratamiento(c.getId(), "MED-1", "1 ml", "c/12h", 5, 99), "stock insuficiente");
            esperar(!e.atencion.indicarTratamiento(c.getId(), "NOEXISTE", "1 ml", "c/12h", 5, 1), "medicamento inexistente");
            esperar(e.atencion.indicarTratamiento(c.getId(), "MED-1", "1 ml", "c/12h", 5, 3), "tratamiento valido");
            igual(6, med.getStockActual(), "indicar no descuenta stock (RN-S2)");
            esperar(e.atencion.confirmarConsulta(c.getId()), "confirmar");
            igual(3, med.getStockActual(), "al confirmar se descuenta el stock");
            igual(EstadoTurno.ATENDIDO, c.getTurno().getEstado(), "turno atendido");
            esperar(c.estaConfirmada(), "consulta confirmada");
            esperar(!e.atencion.indicarTratamiento(c.getId(), "MED-1", "1 ml", "c/12h", 5, 1), "confirmada es inmodificable");
            esperar(!e.atencion.confirmarConsulta(c.getId()), "no se confirma dos veces");
            igual(1, e.estado.getInventario().getAlertasVigentes().size(), "alerta de stock bajo (3 < 5)");
            boolean avisoAdmin = false;
            for (Mensaje m : e.mensajes) {
                if (m.destinatario.getDocumento().equals("A1") && m.texto.contains("Amoxicilina")) avisoAdmin = true;
            }
            esperar(avisoAdmin, "el administrador recibio la alerta");
        });

        caso("PF-10", "CU-18/19/20 Aplicar vacuna: especie, edad minima, stock, proximo refuerzo y recordatorio", () -> {
            Escenario e = new Escenario();
            e.admin.altaVacuna("VAC-1", "Antirrabica", EnumSet.of(Especie.PERRO), 12, 365, 7000.0, 2, "30-1", lotes(10, 365));
            Consulta cLuna = e.consultaEnCurso("Luna", "V1", DIA.atTime(10, 0), "vacuna");
            esperar(!e.atencion.aplicarVacuna(cLuna.getId(), "VAC-1"), "gato: especie no incluida");
            e.registro.altaMascota("D1", Especie.PERRO, "Bebe", "x", Sexo.MACHO, HOY.minusWeeks(4), 2, "C-2", TamanioPerro.PEQUENO, false, false);
            Consulta cBebe = e.consultaEnCurso("Bebe", "V1", DIA.atTime(11, 0), "vacuna");
            esperar(!e.atencion.aplicarVacuna(cBebe.getId(), "VAC-1"), "cachorro menor a la edad minima (RN-V3)");
            Consulta cMax = e.consultaEnCurso("Max", "V1", DIA.atTime(12, 0), "vacuna");
            esperar(e.atencion.aplicarVacuna(cMax.getId(), "VAC-1"), "perro adulto");
            igual(HOY.plusDays(365), cMax.getVacunasAplicadas().get(0).getProximoRefuerzo(), "proximo refuerzo a 365 dias");
            esperar(e.atencion.confirmarConsulta(cMax.getId()), "confirmar");
            igual(9, e.estado.getInventario().buscarProducto("VAC-1").getStockActual(), "la vacuna descuenta una unidad (S-25)");
            e.mensajes.clear();
            e.admin.emitirRecordatorios(HOY.plusDays(357));
            igual(0, e.mensajes.size(), "faltan 8 dias: sin recordatorio");
            e.admin.emitirRecordatorios(HOY.plusDays(358));
            igual(1, e.mensajes.size(), "faltan 7 dias: recordatorio al dueño");
            igual("D1", e.mensajes.get(0).destinatario.getDocumento(), "destinatario correcto");
        });

        caso("PF-11", "CU-24/25 Nota rectificatoria e historia clinica: solo con consulta cerrada, sin tocar la original", () -> {
            Escenario e = new Escenario();
            Consulta c = e.consultaEnCurso("Max", "V1", DIA.atTime(10, 0), "control");
            esperar(!e.atencion.registrarNotaRectificatoria(c.getId(), "correccion", "V1"), "consulta aun en curso");
            e.atencion.registrarDiagnostico(c.getId(), "diagnostico original");
            e.atencion.confirmarConsulta(c.getId());
            esperar(e.atencion.registrarNotaRectificatoria(c.getId(), "el peso era 25 kg", "V1"), "nota valida");
            esperar(!e.atencion.registrarNotaRectificatoria(c.getId(), "  ", "V1"), "texto vacio");
            esperar(!e.atencion.registrarNotaRectificatoria(c.getId(), "x", "NOEXISTE"), "firmante inexistente");
            igual(1, e.atencion.consultarHistoriaClinica(e.mascota("Max").getId()).getConsultas().size(), "la historia sigue teniendo una consulta");
            esperar(e.atencion.consultarHistoriaClinica("NOEXISTE") == null, "mascota inexistente");
        });
    }

    // =====================================================================
    private static void facturacion() {
        caso("PF-12", "CU-09/15 Facturar: solo consultas confirmadas; lineas de consulta e insumos; descuento por plan", () -> {
            Escenario e = new Escenario();
            e.admin.altaProducto("MED-1", "Amoxicilina", 1, 3200.0, lotes(20, 60), "30-1");
            Consulta c = e.consultaEnCurso("Luna", "V1", DIA.atTime(10, 0), "control"); // D2 tiene plan de salud
            e.atencion.registrarPeso(c.getId(), 4.5);
            e.atencion.indicarTratamiento(c.getId(), "MED-1", "1 ml", "c/12h", 5, 2);
            esperar(!e.facturacion.facturarConsulta(c.getId(), new Efectivo()), "no se factura en curso");
            igual(1, e.facturacion.obtenerConsultasConfirmadas().size() + 1, "aun no figura como confirmada");
            e.atencion.confirmarConsulta(c.getId());
            igual(1, e.facturacion.obtenerConsultasConfirmadas().size(), "figura como pendiente de facturar");
            esperar(e.facturacion.facturarConsulta(c.getId(), new Efectivo()), "facturar confirmada");
            igual(13940.0, e.ultimaFactura().getTotal(), "(10000 + 2 x 3200) con 15 % de plan (mayor que el 10 % de efectivo)");
            esperar(c.estaFacturada(), "consulta marcada como facturada");
            esperar(!e.facturacion.facturarConsulta(c.getId(), new Efectivo()), "no se factura dos veces");
            igual(0, e.facturacion.obtenerConsultasConfirmadas().size(), "ya no esta pendiente");
        });

        caso("PF-13", "CU-15 Facturar con credito en cuotas: 8 % de recargo (RN-F2) y 15 % + 8 % con plan (S-19)", () -> {
            Escenario e = new Escenario();
            Consulta c1 = e.consultaEnCurso("Max", "V1", DIA.atTime(10, 0), "control"); // D1 sin plan
            e.atencion.confirmarConsulta(c1.getId());
            e.facturacion.facturarConsulta(c1.getId(), new Credito(3));
            igual(10800.0, e.ultimaFactura().getTotal(), "10000 + 8 %");
            Consulta c2 = e.consultaEnCurso("Luna", "V1", DIA.atTime(11, 0), "control"); // D2 con plan
            e.atencion.confirmarConsulta(c2.getId());
            e.facturacion.facturarConsulta(c2.getId(), new Credito(3));
            igual(9180.0, e.ultimaFactura().getTotal(), "10000 - 15 % y luego + 8 %");
        });

        caso("PF-14", "CU-10 Anular factura con nota de credito: una sola vez, con motivo y recepcionista validos", () -> {
            Escenario e = new Escenario();
            Consulta c = e.consultaEnCurso("Max", "V1", DIA.atTime(10, 0), "control");
            e.atencion.confirmarConsulta(c.getId());
            e.facturacion.facturarConsulta(c.getId(), new Debito());
            Factura f = e.ultimaFactura();
            double total = f.getTotal();
            esperar(!e.facturacion.anularFactura(f.getNumero(), " ", "R1"), "motivo vacio");
            esperar(!e.facturacion.anularFactura(f.getNumero(), "error", "NOEXISTE"), "recepcionista inexistente");
            esperar(!e.facturacion.anularFactura("NOEXISTE", "error", "R1"), "factura inexistente");
            esperar(e.facturacion.anularFactura(f.getNumero(), "error de carga", "R1"), "anulacion valida");
            esperar(f.estaAnulada(), "anulada");
            igual(total, f.getTotal(), "el total original no se modifica (RN-F4)");
            esperar(!e.facturacion.anularFactura(f.getNumero(), "otra vez", "R1"), "no se anula dos veces");
            igual(1, e.estado.getFacturas().size(), "la nota de credito no crea otra factura");
        });
    }

    // =====================================================================
    private static void administracion() {
        caso("PF-15", "CU-26/27 Gestionar veterinarios y recepcionistas: duplicados, horarios invalidos y baja con turnos", () -> {
            Escenario e = new Escenario();
            esperar(!e.admin.altaVeterinario("X", "Y", "V9", "1", "MAT-1", "x", semana(9, 0, 18, 0, false)), "matricula duplicada");
            esperar(!e.admin.altaVeterinario("X", "Y", "V1", "1", "MAT-9", "x", semana(9, 0, 18, 0, false)), "documento duplicado");
            esperar(!e.admin.altaVeterinario("X", "Y", "V9", "1", "MAT-9", "x", semana(18, 0, 9, 0, false)), "fin anterior al inicio");
            esperar(e.admin.altaVeterinario("X", "Y", "V9", "1", "MAT-9", "x", semana(9, 0, 18, 0, false)), "alta valida");
            e.turnos.asignarTurno(e.mascota("Max").getId(), "V9", TipoAtencion.CONSULTA_GENERAL, DIA.plusYears(1).atTime(10, 0));
            esperar(!e.admin.bajaVeterinario("V9"), "baja con turnos futuros exige reprogramar");
            esperar(e.admin.altaRecepcionista("Pedro", "S", "R9", "6"), "recepcionista nuevo");
            esperar(!e.admin.altaRecepcionista("Otro", "S", "R9", "7"), "recepcionista duplicado");
            esperar(e.admin.bajaRecepcionista("R9"), "baja de recepcionista");
        });

        caso("PF-16", "CU-28/29/30 Catalogos: productos, vacunas y proveedores con sus validaciones", () -> {
            Escenario e = new Escenario();
            esperar(!e.admin.altaProducto("P1", "Gasas", 5, 100, lotes(10, 30), "NOEXISTE"), "proveedor inexistente");
            esperar(!e.admin.altaProducto("P1", "Gasas", 5, 100, lotes(10, -1), "30-1"), "lote vencido");
            esperar(e.admin.altaProducto("P1", "Gasas", 5, 100, lotes(10, 30), "30-1"), "alta valida");
            esperar(!e.admin.altaProducto("P1", "Otra", 5, 100, lotes(10, 30), "30-1"), "codigo duplicado");
            esperar(!e.admin.altaVacuna("V1", "Rabia", EnumSet.noneOf(Especie.class), 8, 365, 100, 1, "30-1", lotes(1, 30)), "vacuna sin especies");
            esperar(!e.admin.altaVacuna("V1", "Rabia", EnumSet.of(Especie.PERRO), 8, 0, 100, 1, "30-1", lotes(1, 30)), "intervalo no positivo");
            esperar(e.admin.altaVacuna("V1", "Rabia", EnumSet.of(Especie.PERRO), 8, 365, 100, 1, "30-1", lotes(1, 30)), "vacuna valida");
            igual(1, e.admin.obtenerVacunas().size(), "una vacuna en el catalogo");
            esperar(!e.admin.altaProveedor("30-1", "Repetido", "1", "r@test.com"), "proveedor duplicado");
            esperar(!e.admin.bajaProveedor("30-1"), "proveedor con productos no se borra (S-28)");
            esperar(e.admin.altaProveedor("30-2", "Otro", "2", "o@test.com") && e.admin.bajaProveedor("30-2"), "proveedor sin productos si se borra");
            esperar(e.admin.agregarLotesProducto("P1", lotes(5, 90)), "agregar lote");
            igual(15, e.estado.getInventario().buscarProducto("P1").getStockActual(), "stock 10 + 5");
        });

        caso("PF-17", "CU-31 Reportes: turnos por veterinario, stock bajo y lotes proximos a vencer", () -> {
            Escenario e = new Escenario();
            e.turnos.asignarTurno(e.mascota("Luna").getId(), "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(11, 0));
            e.turnos.asignarTurno(e.mascota("Max").getId(), "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(10, 0));
            e.turnos.asignarTurno(e.mascota("Max").getId(), "V1", TipoAtencion.CONSULTA_GENERAL, DIA.plusDays(30).atTime(10, 0));
            List<Turno> rep = e.admin.emitirReporteTurnos("V1", DIA, DIA.plusDays(1));
            igual(2, rep.size(), "dos turnos dentro del periodo");
            esperar(rep.get(0).getFechaHora().isBefore(rep.get(1).getFechaHora()), "ordenados por fecha");
            esperar(e.admin.emitirReporteTurnos("V1", DIA.plusDays(1), DIA) == null, "periodo invalido");
            e.admin.altaProducto("P-BAJO", "Bajo", 10, 100, lotes(2, 400), "30-1");
            e.admin.altaProducto("P-OK", "Ok", 1, 100, lotes(50, 10), "30-1");
            igual(1, e.admin.emitirReporteStockBajo().size(), "un producto bajo el minimo");
            igual("P-BAJO", e.admin.emitirReporteStockBajo().get(0).getCodigo(), "es P-BAJO");
            igual(1, e.admin.emitirReporteLotesPorVencer(HOY, 30).size(), "solo el lote que vence en 10 dias");
            igual(2, e.admin.emitirReporteLotesPorVencer(HOY, 500).size(), "con 500 dias entran ambos");
        });
    }

    // =====================================================================
    private static void persistencia() {
        caso("PF-18", "Persistencia: los datos cargados por los controladores sobreviven a un reinicio (archivo)", () -> {
            File tmp = File.createTempFile("vetgo-pf", ".dat");
            tmp.deleteOnExit();
            Repositorio repo = new RepositorioArchivo(tmp.getPath());
            Escenario base = new Escenario();
            ControladorRegistro registro = new ControladorRegistro(repo, base.estado);
            registro.altaDuenio("Nuevo", "Dueño", "D7", "7", "n@test.com", true); // guarda en archivo
            base.turnos.asignarTurno(base.mascota("Max").getId(), "V1", TipoAtencion.CONSULTA_GENERAL, DIA.atTime(10, 0));
            repo.guardar(base.estado);
            EstadoSistema recuperado = repo.cargar();
            esperar(recuperado != null, "estado leido");
            esperar(recuperado.getClinica().buscarDuenio("D7") != null, "dueño nuevo");
            esperar(recuperado.getClinica().buscarDuenio("D7").tienePlanSalud(), "plan de salud");
            igual(2, recuperado.getClinica().getVeterinarios().size(), "veterinarios");
            igual(1, recuperado.getAgenda().getTurnos().size(), "turnos");
            igual(10000.0, recuperado.getTarifario().getPrecio(TipoAtencion.CONSULTA_GENERAL), "tarifario");
        });
    }
}
