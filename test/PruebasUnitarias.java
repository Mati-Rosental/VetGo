import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.atencion.Consulta;
import modelo.atencion.VacunaAplicada;
import modelo.enumeradores.*;
import modelo.facturacion.*;
import modelo.facturacion.pago.*;
import modelo.inventario.*;
import modelo.mascotas.*;
import modelo.personas.*;
import persistencia.EstadoSistema;
import persistencia.RepositorioArchivo;
import servicio.GeneradorReportes;
import servicio.ServicioRecordatorios;

/** Pruebas unitarias de las clases del modelo y los servicios. */
public class PruebasUnitarias {

    static LocalDate hoy = LocalDate.now();
    static LocalDateTime lunes10 = LocalDateTime.of(2031, 3, 3, 10, 0);

    // ----- objetos de ejemplo -----

    static Duenio duenio(boolean plan) {
        return new Duenio("Ana", "Perez", "100", "111", "ana@test.com", plan);
    }

    static Perro perro(Duenio d) {
        return new Perro(d, "Toby", "mestizo", Sexo.MACHO, hoy.minusYears(3), 12.0, "CHIP-1", TamanioPerro.MEDIANO);
    }

    static List<HorarioAtencion> horarios(boolean guardia) {
        List<HorarioAtencion> lista = new ArrayList<>();
        for (DayOfWeek dia : DayOfWeek.values()) {
            lista.add(new HorarioAtencion(dia, LocalTime.of(9, 0), LocalTime.of(18, 0), guardia));
        }
        return lista;
    }

    static Veterinario vet(String doc) {
        return new Veterinario("Juan", "Lopez", doc, "222", "MAT-" + doc, "General", horarios(false));
    }

    static Proveedor proveedor() {
        return new Proveedor("30-1", "Distribuidora", "333", "d@test.com");
    }

    static Consulta consultaConPeso(Mascota m) {
        Turno t = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, m, vet("1"));
        Consulta c = new Consulta(t, "control");
        c.registrarPeso(10.0);
        return c;
    }

    static Vacuna vacuna(int edadMinima, int intervalo) {
        return new Vacuna("V1", "Triple", 2, 500, proveedor(), EnumSet.of(Especie.PERRO), edadMinima, intervalo);
    }

    public static void ejecutar() {
        System.out.println("\n######## PRUEBAS UNITARIAS ########");

        // ----- Duenio, Veterinario -----
        EjecutorPruebas.caso("U-01", "Duenio: tres ausencias seguidas exigen sena y bloquean pedir turno (RN-T4)", () -> {
            Duenio d = duenio(false);
            d.registrarAusencia();
            d.registrarAusencia();
            EjecutorPruebas.esperar(d.puedePedirTurno(), "con 2 ausencias puede pedir turno");
            d.registrarAusencia();
            EjecutorPruebas.esperar(d.requiereSenia(), "con 3 ausencias requiere sena");
            EjecutorPruebas.esperar(!d.puedePedirTurno(), "con 3 ausencias no puede pedir turno");
        });

        EjecutorPruebas.caso("U-02", "Duenio: asistir reinicia el contador de ausencias (S-05)", () -> {
            Duenio d = duenio(false);
            d.registrarAusencia();
            d.registrarAusencia();
            d.registrarAsistencia();
            d.registrarAusencia();
            EjecutorPruebas.esperar(!d.requiereSenia(), "no deberia requerir sena");
        });

        EjecutorPruebas.caso("U-03", "Duenio: pagar la sena levanta el bloqueo y deja el monto a favor (S-04)", () -> {
            Duenio d = duenio(false);
            for (int i = 0; i < 3; i++) {
                d.registrarAusencia();
            }
            d.pagarSenia(2000);
            EjecutorPruebas.esperar(d.puedePedirTurno(), "puede pedir turno");
            EjecutorPruebas.esperarIgual(2000.0, d.getSeniaPendiente(), "sena a favor");
            EjecutorPruebas.esperarIgual(500.0, d.consumirSenia(500), "consume 500");
            EjecutorPruebas.esperarIgual(1500.0, d.consumirSenia(99999), "consume solo lo que queda");
            EjecutorPruebas.esperarIgual(0.0, d.getSeniaPendiente(), "sena agotada");
        });

        EjecutorPruebas.caso("U-04", "Persona: roles y nombre completo heredados", () -> {
            EjecutorPruebas.esperarIgual("Dueño", duenio(false).getRol(), "rol duenio");
            EjecutorPruebas.esperarIgual("Veterinario", vet("1").getRol(), "rol veterinario");
            EjecutorPruebas.esperarIgual("Recepcionista", new Recepcionista("B", "C", "2", "3").getRol(), "rol recepcionista");
            EjecutorPruebas.esperarIgual("Administrador", new Administrador("D", "E", "4", "5").getRol(), "rol administrador");
            EjecutorPruebas.esperarIgual("Ana Perez", duenio(false).getNombreCompleto(), "nombre completo");
        });

        EjecutorPruebas.caso("U-05", "Veterinario.atiendeEn: dentro del bloque, fuera de bloque y tramo que excede el fin (RN-T2, S-22)", () -> {
            Veterinario v = vet("1");
            EjecutorPruebas.esperar(v.atiendeEn(lunes10, 30), "10:00 por 30 min entra");
            EjecutorPruebas.esperar(v.atiendeEn(lunes10.withHour(17).withMinute(30), 30), "17:30 a 18:00 entra justo");
            EjecutorPruebas.esperar(!v.atiendeEn(lunes10.withHour(17).withMinute(45), 30), "17:45 se pasa del fin");
            EjecutorPruebas.esperar(!v.atiendeEn(lunes10.withHour(8), 30), "8:00 es antes de abrir");
            List<HorarioAtencion> soloLunes = new ArrayList<>();
            soloLunes.add(new HorarioAtencion(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0), false));
            Veterinario v2 = new Veterinario("A", "B", "9", "1", "M9", "x", soloLunes);
            EjecutorPruebas.esperar(!v2.atiendeEn(lunes10.plusDays(1), 30), "el martes no atiende");
        });

        EjecutorPruebas.caso("U-06", "Veterinario.esDeGuardia segun el bloque marcado como guardia (S-07)", () -> {
            Veterinario g = new Veterinario("A", "B", "9", "1", "M9", "x", horarios(true));
            EjecutorPruebas.esperar(g.esDeGuardia(lunes10), "de guardia dentro del horario");
            EjecutorPruebas.esperar(!g.esDeGuardia(lunes10.withHour(20)), "no esta de guardia fuera del horario");
            EjecutorPruebas.esperar(!vet("1").esDeGuardia(lunes10), "sin marca de guardia");
        });

        // ----- Mascotas -----
        EjecutorPruebas.caso("U-07", "Mascota: cada especie informa su especie y datos propios (RN-M2, S-12)", () -> {
            Duenio d = duenio(false);
            LocalDate nac = hoy.minusYears(1);
            EjecutorPruebas.esperarIgual(Especie.PERRO, perro(d).getEspecie(), "perro");
            EjecutorPruebas.esperarIgual("Chip: CHIP-1, tamaño: MEDIANO", perro(d).getDatosPropios(), "datos del perro");
            EjecutorPruebas.esperarIgual(Especie.GATO, new Gato(d, "Mora", "siames", Sexo.HEMBRA, nac, 4.0, true).getEspecie(), "gato");
            EjecutorPruebas.esperarIgual(Especie.AVE, new Ave(d, "Lolo", "canario", Sexo.MACHO, nac, 0.3, true).getEspecie(), "ave");
            Conejo c = new Conejo(d, "Nube", "holandes", Sexo.HEMBRA, nac, 1.8);
            EjecutorPruebas.esperarIgual(Especie.CONEJO, c.getEspecie(), "conejo");
            EjecutorPruebas.esperar(c.getHistoriaClinica() != null, "toda mascota tiene historia clinica");
        });

        EjecutorPruebas.caso("U-08", "Mascota.edadEnSemanas se calcula desde la fecha de nacimiento (S-14)", () -> {
            Perro p = new Perro(duenio(false), "Cachorro", "x", Sexo.MACHO, hoy.minusWeeks(10), 3, "C", TamanioPerro.PEQUENO);
            EjecutorPruebas.esperarIgual(10, p.edadEnSemanas(), "semanas");
        });

        EjecutorPruebas.caso("U-09", "Mascota.transferirA: cambia de duenio y conserva la historia clinica (RN-M1, S-24)", () -> {
            Duenio viejo = duenio(false);
            Duenio nuevo = new Duenio("Luis", "Gomez", "200", "9", "l@test.com", false);
            Perro p = perro(viejo);
            viejo.agregarMascota(p);
            Object historia = p.getHistoriaClinica();
            p.transferirA(nuevo, hoy);
            EjecutorPruebas.esperar(p.getDuenio() == nuevo, "nuevo duenio");
            EjecutorPruebas.esperar(!viejo.getMascotas().contains(p), "el duenio anterior ya no la tiene");
            EjecutorPruebas.esperar(nuevo.getMascotas().contains(p), "el nuevo duenio la tiene");
            EjecutorPruebas.esperar(p.getHistoriaClinica() == historia, "misma historia clinica");
        });

        // ----- Turnos y agenda -----
        EjecutorPruebas.caso("U-10", "TipoAtencion: duraciones 30 / 15 / 120 min y urgencia 30 min (RN-T1, S-08)", () -> {
            EjecutorPruebas.esperarIgual(30, TipoAtencion.CONSULTA_GENERAL.getDuracionMin(), "consulta general");
            EjecutorPruebas.esperarIgual(15, TipoAtencion.VACUNACION.getDuracionMin(), "vacunacion");
            EjecutorPruebas.esperarIgual(120, TipoAtencion.CIRUGIA.getDuracionMin(), "cirugia");
            EjecutorPruebas.esperarIgual(30, TipoAtencion.URGENCIA.getDuracionMin(), "urgencia");
        });

        EjecutorPruebas.caso("U-11", "Turno.seSuperpone: mismo veterinario solapado si, contiguo o distinto veterinario no (RN-T2)", () -> {
            Perro p = perro(duenio(false));
            Veterinario v1 = vet("1");
            Turno a = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, v1);
            Turno solapado = new Turno(lunes10.plusMinutes(15), TipoAtencion.CONSULTA_GENERAL, p, v1);
            Turno contiguo = new Turno(lunes10.plusMinutes(30), TipoAtencion.CONSULTA_GENERAL, p, v1);
            Turno otroVet = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, vet("2"));
            EjecutorPruebas.esperar(a.seSuperpone(solapado), "solapado");
            EjecutorPruebas.esperar(!a.seSuperpone(contiguo), "contiguo");
            EjecutorPruebas.esperar(!a.seSuperpone(otroVet), "otro veterinario");
        });

        EjecutorPruebas.caso("U-12", "Turno.cancelar: sin cargo con 2 h o mas de anticipacion, con cargo con menos (RN-T3)", () -> {
            Perro p = perro(duenio(false));
            Turno t1 = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, vet("1"));
            Turno t2 = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, vet("1"));
            Turno t3 = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, vet("1"));
            EjecutorPruebas.esperarIgual(false, t1.cancelar(lunes10.minusHours(3)), "3 horas antes");
            EjecutorPruebas.esperarIgual(false, t2.cancelar(lunes10.minusHours(2)), "exactamente 2 horas antes");
            EjecutorPruebas.esperarIgual(true, t3.cancelar(lunes10.minusMinutes(119)), "1 hora 59 minutos antes");
            EjecutorPruebas.esperarIgual(EstadoTurno.CANCELADO, t1.getEstado(), "queda cancelado");
        });

        EjecutorPruebas.caso("U-13", "Agenda.asignarTurno: rechaza superposicion y fuera de horario, acepta contiguo (RN-T2)", () -> {
            Agenda agenda = new Agenda();
            Perro p = perro(duenio(false));
            Veterinario v = vet("1");
            EjecutorPruebas.esperar(agenda.asignarTurno(new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, v)), "primer turno");
            EjecutorPruebas.esperar(!agenda.asignarTurno(new Turno(lunes10.plusMinutes(10), TipoAtencion.VACUNACION, p, v)), "superpuesto");
            EjecutorPruebas.esperar(agenda.asignarTurno(new Turno(lunes10.plusMinutes(30), TipoAtencion.CONSULTA_GENERAL, p, v)), "contiguo");
            EjecutorPruebas.esperar(!agenda.asignarTurno(new Turno(lunes10.withHour(17), TipoAtencion.CIRUGIA, p, v)), "cirugia que se pasa de la jornada");
            EjecutorPruebas.esperar(agenda.asignarTurno(new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, vet("2"))), "otro veterinario a la misma hora");
        });

        EjecutorPruebas.caso("U-14", "Agenda.reprogramar: valida disponibilidad y no choca consigo mismo", () -> {
            Agenda agenda = new Agenda();
            Perro p = perro(duenio(false));
            Veterinario v = vet("1");
            Turno a = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, v);
            Turno b = new Turno(lunes10.plusHours(1), TipoAtencion.CONSULTA_GENERAL, p, v);
            agenda.asignarTurno(a);
            agenda.asignarTurno(b);
            EjecutorPruebas.esperar(agenda.reprogramar(a, lunes10.plusMinutes(10)), "moverse dentro de su propio horario");
            EjecutorPruebas.esperar(!agenda.reprogramar(a, lunes10.plusHours(1)), "no puede pisar a otro turno");
            EjecutorPruebas.esperarIgual(lunes10.plusMinutes(10), a.getFechaHora(), "el rechazo no cambia la fecha");
            EjecutorPruebas.esperar(!agenda.reprogramar(a, lunes10.withHour(20)), "fuera de horario");
        });

        EjecutorPruebas.caso("U-15", "Un turno cancelado libera el horario del veterinario", () -> {
            Agenda agenda = new Agenda();
            Veterinario v = vet("1");
            Turno t = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, perro(duenio(false)), v);
            agenda.asignarTurno(t);
            EjecutorPruebas.esperar(!agenda.hayDisponibilidad(v, lunes10, 30), "ocupado");
            t.cancelar(lunes10.minusDays(1));
            EjecutorPruebas.esperar(agenda.hayDisponibilidad(v, lunes10, 30), "libre despues de cancelar");
        });

        EjecutorPruebas.caso("U-16", "Agenda.getTurnosDe: filtra por veterinario y rango, ordenado por fecha (reporte RF-19)", () -> {
            Agenda agenda = new Agenda();
            Perro p = perro(duenio(false));
            Veterinario v = vet("1");
            Turno segundo = new Turno(lunes10.plusDays(1), TipoAtencion.CONSULTA_GENERAL, p, v);
            Turno primero = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, v);
            Turno lejano = new Turno(lunes10.plusDays(10), TipoAtencion.CONSULTA_GENERAL, p, v);
            agenda.asignarTurno(segundo);
            agenda.asignarTurno(primero);
            agenda.asignarTurno(lejano);
            agenda.asignarTurno(new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, vet("2")));
            List<Turno> turnos = agenda.getTurnosDe(v, lunes10.toLocalDate(), lunes10.toLocalDate().plusDays(2));
            EjecutorPruebas.esperarIgual(2, turnos.size(), "cantidad de turnos");
            EjecutorPruebas.esperar(turnos.get(0) == primero && turnos.get(1) == segundo, "orden por fecha");
        });

        // ----- Consulta y vacunas -----
        EjecutorPruebas.caso("U-17", "Consulta: no se indica medicamento sin peso registrado (RN-M5)", () -> {
            Turno t = new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, perro(duenio(false)), vet("1"));
            Consulta c = new Consulta(t, "control");
            Producto med = new Producto("M1", "Amoxicilina", 2, 100, proveedor());
            EjecutorPruebas.esperar(!c.indicarTratamiento(med, "1 ml", "c/12h", 5, 1), "sin peso se rechaza");
            c.registrarPeso(12.5);
            EjecutorPruebas.esperar(c.indicarTratamiento(med, "1 ml", "c/12h", 5, 1), "con peso se acepta");
            EjecutorPruebas.esperarIgual(1, c.getTratamientos().size(), "cantidad de tratamientos");
        });

        EjecutorPruebas.caso("U-18", "Consulta.registrarPeso: con el peso del dia ya se pueden indicar medicamentos (RN-M5, S-23)", () -> {
            Consulta c = consultaConPeso(perro(duenio(false)));
            Producto med = new Producto("M1", "Amoxicilina", 2, 100, proveedor());
            EjecutorPruebas.esperar(c.indicarTratamiento(med, "1", "d", 3, 1), "se acepta el tratamiento");
        });

        EjecutorPruebas.caso("U-19", "Consulta.aplicarVacuna: valida especie y edad minima (RN-V3), calcula refuerzo (RN-V1)", () -> {
            Vacuna vac = vacuna(8, 365);
            vac.agregarLote(new Lote("L1", 10, hoy.plusYears(1)));
            Consulta adulto = consultaConPeso(perro(duenio(false)));
            EjecutorPruebas.esperar(adulto.aplicarVacuna(vac), "perro adulto");
            EjecutorPruebas.esperarIgual(hoy.plusDays(365), adulto.getVacunasAplicadas().get(0).getProximoRefuerzo(), "proximo refuerzo");
            Perro cachorro = new Perro(duenio(false), "Bebe", "x", Sexo.MACHO, hoy.minusWeeks(4), 2, "C", TamanioPerro.PEQUENO);
            EjecutorPruebas.esperar(!consultaConPeso(cachorro).aplicarVacuna(vac), "menor a la edad minima");
            Gato gato = new Gato(duenio(false), "Mora", "x", Sexo.HEMBRA, hoy.minusYears(2), 4, true);
            EjecutorPruebas.esperar(!consultaConPeso(gato).aplicarVacuna(vac), "especie no incluida");
        });

        EjecutorPruebas.caso("U-20", "Consulta.getInsumosUtilizados suma medicamentos y una unidad por vacuna aplicada", () -> {
            Producto med = new Producto("M1", "Amoxicilina", 2, 100, proveedor());
            Vacuna vac = vacuna(0, 365);
            vac.agregarLote(new Lote("L1", 10, hoy.plusYears(1)));
            Consulta c = consultaConPeso(perro(duenio(false)));
            c.indicarTratamiento(med, "1", "d", 3, 2);
            c.indicarTratamiento(med, "1", "d", 3, 1);
            c.aplicarVacuna(vac);
            EjecutorPruebas.esperarIgual(3, (int) c.getInsumosUtilizados().get(med), "medicamento repetido");
            EjecutorPruebas.esperarIgual(1, (int) c.getInsumosUtilizados().get(vac), "vacuna");
        });

        EjecutorPruebas.caso("U-21", "Consulta confirmada es inmodificable: no admite nuevos tratamientos (S-18)", () -> {
            Consulta c = consultaConPeso(perro(duenio(false)));
            c.confirmar();
            Producto med = new Producto("M1", "Amoxicilina", 2, 100, proveedor());
            EjecutorPruebas.esperar(!c.indicarTratamiento(med, "1", "d", 3, 1), "debe rechazarlo");
        });

        EjecutorPruebas.caso("U-22", "VacunaAplicada: recordatorio desde 7 dias antes; vencida pasados 30 dias (RN-V2, S-26)", () -> {
            VacunaAplicada va = new VacunaAplicada(vacuna(0, 100), "L1", hoy);
            LocalDate refuerzo = hoy.plusDays(100);
            EjecutorPruebas.esperar(va.requiereRecordatorio(refuerzo.minusDays(7)), "7 dias antes");
            EjecutorPruebas.esperar(!va.requiereRecordatorio(refuerzo.minusDays(8)), "8 dias antes");
            EjecutorPruebas.esperar(!va.estaVencida(refuerzo.plusDays(30)), "a los 30 dias");
            EjecutorPruebas.esperar(va.estaVencida(refuerzo.plusDays(31)), "a los 31 dias");
        });

        // ----- Inventario -----
        EjecutorPruebas.caso("U-23", "Producto.getStockActual no cuenta lotes vencidos (S-16, RN-S3)", () -> {
            Producto p = new Producto("P1", "Gasas", 5, 10, proveedor());
            p.agregarLote(new Lote("vencido", 100, hoy.minusDays(1)));
            p.agregarLote(new Lote("vigente", 7, hoy.plusDays(30)));
            EjecutorPruebas.esperarIgual(7, p.getStockActual(), "stock");
            EjecutorPruebas.esperar(p.hayStock(7) && !p.hayStock(8), "hayStock");
        });

        EjecutorPruebas.caso("U-24", "Producto.descontar consume primero el lote que vence antes - FEFO (RN-S3)", () -> {
            Producto p = new Producto("P1", "Gasas", 1, 10, proveedor());
            p.agregarLote(new Lote("tardio", 10, hoy.plusDays(90)));
            p.agregarLote(new Lote("temprano", 10, hoy.plusDays(10)));
            p.descontar(4);
            for (Lote l : p.getLotes()) {
                if (l.getNumero().equals("temprano")) {
                    EjecutorPruebas.esperarIgual(6, l.getCantidad(), "lote temprano");
                } else {
                    EjecutorPruebas.esperarIgual(10, l.getCantidad(), "lote tardio");
                }
            }
        });

        EjecutorPruebas.caso("U-25", "Producto.descontar continua con el siguiente lote cuando uno no alcanza", () -> {
            Producto p = new Producto("P1", "Gasas", 1, 10, proveedor());
            p.agregarLote(new Lote("A", 3, hoy.plusDays(10)));
            p.agregarLote(new Lote("B", 10, hoy.plusDays(20)));
            p.descontar(5);
            EjecutorPruebas.esperarIgual(8, p.getStockActual(), "13 - 5");
            EjecutorPruebas.esperarIgual(0, p.getLotes().get(0).getCantidad(), "el lote A queda en 0");
        });

        EjecutorPruebas.caso("U-26", "Inventario.descontarStock: genera alerta al quedar bajo el minimo y no la duplica (RN-S1)", () -> {
            Inventario inv = new Inventario();
            Producto med = new Producto("M1", "Amoxicilina", 5, 100, proveedor());
            med.agregarLote(new Lote("L", 8, hoy.plusDays(30)));
            inv.agregarProducto(med);
            Consulta c1 = consultaConPeso(perro(duenio(false)));
            c1.indicarTratamiento(med, "1", "d", 3, 4);
            EjecutorPruebas.esperarIgual(1, inv.descontarStock(c1).size(), "alertas de la primera consulta");
            Consulta c2 = consultaConPeso(perro(duenio(false)));
            c2.indicarTratamiento(med, "1", "d", 3, 1);
            EjecutorPruebas.esperarIgual(0, inv.descontarStock(c2).size(), "no se duplica la alerta");
            EjecutorPruebas.esperarIgual(1, inv.getAlertasVigentes().size(), "una sola alerta vigente");
        });

        EjecutorPruebas.caso("U-27", "Inventario: alerta cuando el stock LLEGA al minimo, es decir stock <= minimo (RN-S1, CU-23)", () -> {
            Producto med = new Producto("M1", "Amoxicilina", 5, 100, proveedor());
            med.agregarLote(new Lote("L", 5, hoy.plusDays(30)));
            EjecutorPruebas.esperar(med.estaBajoMinimo(), "con stock igual al minimo debe estar bajo el minimo");
        });

        EjecutorPruebas.caso("U-28", "Inventario.cerrarAlertasResueltas cierra la alerta cuando se repone el stock", () -> {
            Inventario inv = new Inventario();
            Producto med = new Producto("M1", "Amoxicilina", 5, 100, proveedor());
            med.agregarLote(new Lote("L", 8, hoy.plusDays(30)));
            inv.agregarProducto(med);
            Consulta c = consultaConPeso(perro(duenio(false)));
            c.indicarTratamiento(med, "1", "d", 3, 6);
            inv.descontarStock(c);
            EjecutorPruebas.esperarIgual(1, inv.getAlertasVigentes().size(), "alerta abierta");
            med.agregarLote(new Lote("L2", 50, hoy.plusDays(60)));
            inv.cerrarAlertasResueltas();
            EjecutorPruebas.esperarIgual(0, inv.getAlertasVigentes().size(), "alerta cerrada");
        });

        EjecutorPruebas.caso("U-29", "GeneradorReportes: lotes proximos a vencer y productos con stock bajo (RF-19, S-29)", () -> {
            Inventario inv = new Inventario();
            Producto a = new Producto("A", "A", 100, 1, proveedor());
            a.agregarLote(new Lote("cerca", 5, hoy.plusDays(10)));
            a.agregarLote(new Lote("lejos", 5, hoy.plusDays(400)));
            inv.agregarProducto(a);
            Vacuna v = new Vacuna("V", "V", 0, 1, proveedor(), EnumSet.allOf(Especie.class), 0, 30);
            v.agregarLote(new Lote("vac-cerca", 5, hoy.plusDays(5)));
            inv.agregarProducto(v);
            GeneradorReportes reportes = new GeneradorReportes(new Agenda(), inv);
            EjecutorPruebas.esperarIgual(2, reportes.lotesProximosAVencer(hoy, 30).size(), "lotes por vencer (producto y vacuna)");
            EjecutorPruebas.esperarIgual(1, reportes.productosConStockBajo().size(), "productos con stock bajo");
            EjecutorPruebas.esperar(reportes.lotesProximosAVencer(null, 30) == null, "fecha nula");
        });

        // ----- Facturacion -----
        EjecutorPruebas.caso("U-30", "Efectivo: 10 % de descuento; con plan de salud 15 %, no se acumulan (RN-F2, RN-F3)", () -> {
            EjecutorPruebas.esperarIgual(9000.0, new Efectivo().calcularTotal(10000, false), "sin plan");
            EjecutorPruebas.esperarIgual(8500.0, new Efectivo().calcularTotal(10000, true), "con plan");
        });

        EjecutorPruebas.caso("U-31", "Debito: sin recargo; con plan de salud 15 % de descuento (RN-F2, RN-F3)", () -> {
            EjecutorPruebas.esperarIgual(10000.0, new Debito().calcularTotal(10000, false), "sin plan");
            EjecutorPruebas.esperarIgual(8500.0, new Debito().calcularTotal(10000, true), "con plan");
        });

        EjecutorPruebas.caso("U-32", "Credito: 8 % de recargo sobre el subtotal (RN-F2)", () -> {
            EjecutorPruebas.esperarIgual(10800.0, new Credito(3).calcularTotal(10000, false), "sin plan");
        });

        EjecutorPruebas.caso("U-33", "Credito con plan de salud: primero 15 % de descuento y luego 8 % de recargo (S-19)", () -> {
            EjecutorPruebas.esperarIgual(9180.0, new Credito(3).calcularTotal(10000, true), "10000 * 0,85 * 1,08");
        });

        EjecutorPruebas.caso("U-34", "Factura: incluye lineas y total segun medio de pago y plan del duenio (RN-F1)", () -> {
            Consulta c = consultaConPeso(perro(duenio(true)));
            List<LineaFactura> lineas = new ArrayList<>();
            lineas.add(new LineaFactura("Consulta", 1, 10000));
            lineas.add(new LineaFactura("Insumo", 2, 3000));
            Factura f = new Factura(c, new Debito(), lineas);
            EjecutorPruebas.esperarIgual(13600.0, f.getTotal(), "16000 con 15 % de plan");
        });

        EjecutorPruebas.caso("U-35", "Factura descuenta la sena a favor del duenio y la consume una sola vez (S-04)", () -> {
            Duenio d = duenio(false);
            d.pagarSenia(2000);
            Consulta c = consultaConPeso(perro(d));
            List<LineaFactura> lineas = new ArrayList<>();
            lineas.add(new LineaFactura("Consulta", 1, 10000));
            Factura f1 = new Factura(c, new Debito(), lineas);
            EjecutorPruebas.esperarIgual(8000.0, f1.getTotal(), "10000 - 2000");
            EjecutorPruebas.esperarIgual(0.0, d.getSeniaPendiente(), "sena consumida");
            Factura f2 = new Factura(c, new Debito(), lineas);
            EjecutorPruebas.esperarIgual(10000.0, f2.getTotal(), "la sena no se descuenta dos veces");
        });

        EjecutorPruebas.caso("U-36", "Factura.anular: queda ANULADA y no altera el total (RN-F4, S-20)", () -> {
            Consulta c = consultaConPeso(perro(duenio(false)));
            List<LineaFactura> lineas = new ArrayList<>();
            lineas.add(new LineaFactura("Consulta", 1, 10000));
            Factura f = new Factura(c, new Debito(), lineas);
            double total = f.getTotal();
            f.anular("error de carga", new Recepcionista("R", "R", "9", "9"));
            EjecutorPruebas.esperar(f.estaAnulada(), "debe quedar anulada");
            EjecutorPruebas.esperarIgual(total, f.getTotal(), "el total no cambia");
        });

        EjecutorPruebas.caso("U-37", "Tarifario: cargo por cancelacion tardia = 20 % de la consulta general (RN-T3, S-06)", () -> {
            HashMap<TipoAtencion, Double> precios = new HashMap<>();
            precios.put(TipoAtencion.CONSULTA_GENERAL, 10000.0);
            Tarifario t = new Tarifario(precios, 2000, 0.2);
            EjecutorPruebas.esperarIgual(2000.0, t.calcularCargoCancelacionTardia(), "20 % de 10000");
            EjecutorPruebas.esperarIgual(0.0, t.getPrecio(TipoAtencion.CIRUGIA), "tipo sin precio devuelve 0");
        });

        // ----- Servicios -----
        EjecutorPruebas.caso("U-38", "ServicioRecordatorios notifica solo las vacunas dentro de los 7 dias previos al refuerzo (RN-V2)", () -> {
            Duenio d = duenio(false);
            Perro p = perro(d);
            d.agregarMascota(p);
            Vacuna vac = new Vacuna("V1", "Rabia", 0, 500, proveedor(), EnumSet.allOf(Especie.class), 0, 100);
            vac.agregarLote(new Lote("L1", 10, hoy.plusYears(1)));
            Turno t = new Turno(lunes10, TipoAtencion.VACUNACION, p, vet("1"));
            p.getHistoriaClinica().abrirConsulta(t, "vacuna");
            p.getHistoriaClinica().getConsultas().get(0).aplicarVacuna(vac);
            List<String> mensajes = new ArrayList<>();
            ServicioRecordatorios servicio = new ServicioRecordatorios((destinatario, mensaje) -> mensajes.add(mensaje));
            List<Mascota> mascotas = new ArrayList<>();
            mascotas.add(p);
            servicio.emitirRecordatorios(mascotas, hoy.plusDays(92));
            EjecutorPruebas.esperarIgual(0, mensajes.size(), "faltan mas de 7 dias");
            servicio.emitirRecordatorios(mascotas, hoy.plusDays(93));
            EjecutorPruebas.esperarIgual(1, mensajes.size(), "faltan 7 dias");
        });

        EjecutorPruebas.caso("U-39", "RepositorioArchivo: guardar y cargar conserva el grafo de objetos (persistencia)", () -> {
            Clinica clinica = new Clinica("Patitas");
            Duenio d = duenio(true);
            Perro p = perro(d);
            d.agregarMascota(p);
            clinica.agregarDuenio(d);
            Veterinario v = vet("1");
            clinica.agregarVeterinario(v);
            Agenda agenda = new Agenda();
            agenda.asignarTurno(new Turno(lunes10, TipoAtencion.CONSULTA_GENERAL, p, v));
            EstadoSistema estado = new EstadoSistema(clinica, agenda, new Inventario(), new ArrayList<Factura>(),
                    new Tarifario(new HashMap<TipoAtencion, Double>(), 2000, 0.2));
            File archivo = File.createTempFile("vetgo-test", ".dat");
            archivo.deleteOnExit();
            RepositorioArchivo repo = new RepositorioArchivo(archivo.getPath());
            repo.guardar(estado);
            EstadoSistema leido = repo.cargar();
            EjecutorPruebas.esperar(leido != null, "se pudo leer el archivo");
            EjecutorPruebas.esperarIgual(1, leido.getClinica().getDuenios().size(), "duenios");
            EjecutorPruebas.esperarIgual(1, leido.getClinica().getMascotas().size(), "mascotas");
            EjecutorPruebas.esperarIgual(1, leido.getAgenda().getTurnos().size(), "turnos");
            EjecutorPruebas.esperar(leido.getClinica().buscarDuenio("100").tienePlanSalud(), "plan de salud del duenio");
        });
    }
}
