import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import controlador.ControladorAtencion;
import controlador.ControladorFacturacion;
import controlador.ControladorTurnos;
import modelo.Administrador;
import modelo.Ave;
import modelo.Clinica;
import modelo.Conejo;
import modelo.Duenio;
import modelo.Especie;
import modelo.Factura;
import modelo.Gato;
import modelo.Perro;
import modelo.Recepcionista;
import modelo.Sexo;
import modelo.TamanioPerro;
import modelo.TipoAtencion;
import modelo.Veterinario;
import persistencia.Repositorio;

public final class Pruebas {
    private static int pruebasEjecutadas;

    private Pruebas() {
    }

    public static void main(String[] args) {
        probarPersonas();
        probarMascotas();
        probarDuracionesDeAtencion();
        probarValidacionesDeTurnos();
        probarValidacionesDeAtencion();
        probarValidacionesDeFacturacion();
        System.out.println("Resultado: " + pruebasEjecutadas + " pruebas aprobadas.");
    }

    private static void probarPersonas() {
        Administrador administrador = new Administrador("Ana", "Lopez", "100", "111");
        Recepcionista recepcionista = new Recepcionista("Bea", "Perez", "200", "222");
        Veterinario veterinario =
                new Veterinario("Carlos", "Diaz", "300", "333", "MAT-1", "Clinica", List.of());
        Duenio duenio = new Duenio("Diana", "Ruiz", "400", "444", "diana@example.test", false);

        comprobarIgual("Administrador", administrador.getRol(), "rol de administrador");
        comprobarIgual("Recepcionista", recepcionista.getRol(), "rol de recepcionista");
        comprobarIgual("Veterinario", veterinario.getRol(), "rol de veterinario");
        comprobarIgual("Dueño", duenio.getRol(), "rol de dueño");
        comprobarIgual("Ana Lopez", administrador.getNombreCompleto(), "nombre completo heredado");
        comprobarIgual("100", administrador.getDocumento(), "documento heredado");
        comprobarIgual("Ana", administrador.getNombre(), "nombre heredado");
    }

    private static void probarMascotas() {
        Duenio duenio = new Duenio("Elena", "Sosa", "500", "555", "elena@example.test", true);
        LocalDate nacimiento = LocalDate.of(2024, 1, 15);

        Perro perro = new Perro(
                duenio, "Toby", "mestizo", Sexo.MACHO, nacimiento, 12.5, "CHIP-1", TamanioPerro.MEDIANO);
        Gato gato = new Gato(
                duenio, "Mora", "siames", Sexo.HEMBRA, nacimiento, 4.2, true);
        Ave ave = new Ave(
                duenio, "Lolo", "canario", Sexo.MACHO, nacimiento, 0.3, true);
        Conejo conejo = new Conejo(
                duenio, "Nube", "holandes", Sexo.HEMBRA, nacimiento, 1.8);

        comprobarMascota(perro, duenio, Especie.PERRO, "Toby", "Chip: CHIP-1, tamaño: MEDIANO");
        comprobarMascota(gato, duenio, Especie.GATO, "Mora", "Vive en interior");
        comprobarMascota(ave, duenio, Especie.AVE, "Lolo", "Puede volar: sí");
        comprobarMascota(conejo, duenio, Especie.CONEJO, "Nube", "Sin datos propios");
    }

    private static void comprobarMascota(
            modelo.Mascota mascota,
            Duenio duenio,
            Especie especie,
            String nombre,
            String datosPropios) {
        comprobar(mascota.getDuenio() == duenio, nombre + ": conserva el dueño");
        comprobarIgual(especie, mascota.getEspecie(), nombre + ": especie");
        comprobarIgual(nombre, mascota.getNombre(), nombre + ": nombre");
        comprobarIgual(datosPropios, mascota.getDatosPropios(), nombre + ": datos propios");
        comprobar(mascota.getHistoriaClinica() != null, nombre + ": historia clínica creada");
    }

    private static void probarDuracionesDeAtencion() {
        comprobarIgual(30, TipoAtencion.CONSULTA_GENERAL.getDuracionMin(), "duración de consulta general");
        comprobarIgual(15, TipoAtencion.VACUNACION.getDuracionMin(), "duración de vacunación");
        comprobarIgual(120, TipoAtencion.CIRUGIA.getDuracionMin(), "duración de cirugía");
    }

    private static void probarValidacionesDeTurnos() {
        ControladorTurnos controlador = new ControladorTurnos(null, null, null, null, null, null);
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 2, 10, 0);

        comprobar(!controlador.asignarTurno(null, "vet-1", TipoAtencion.CONSULTA_GENERAL, fecha),
                "rechaza turno sin mascota");
        comprobar(!controlador.asignarTurno("mascota-1", null, TipoAtencion.CONSULTA_GENERAL, fecha),
                "rechaza turno sin veterinario");
        comprobar(!controlador.asignarTurno("mascota-1", "vet-1", null, fecha),
                "rechaza turno sin tipo");
        comprobar(!controlador.asignarTurno("mascota-1", "vet-1", TipoAtencion.CONSULTA_GENERAL, null),
                "rechaza turno sin fecha");
        comprobar(!controlador.reprogramarTurno(null, fecha), "rechaza reprogramar sin identificador");
        comprobar(!controlador.reprogramarTurno("turno-1", null), "rechaza reprogramar sin fecha");
        comprobar(!controlador.cancelarTurno(null), "rechaza cancelar sin identificador");
        comprobar(!controlador.registrarAusencia(null), "rechaza ausencia sin identificador");
        comprobar(controlador.atenderUrgencia(null) == null, "rechaza urgencia sin mascota");
    }

    private static void probarValidacionesDeAtencion() {
        ControladorAtencion controlador =
                new ControladorAtencion(null, null, null, null, null, null);

        comprobar(!controlador.iniciarConsulta(null, "control"), "rechaza consulta sin turno");
        comprobar(!controlador.iniciarConsulta("turno-1", " "), "rechaza consulta con motivo vacío");
        comprobar(!controlador.registrarPeso(null, 10), "rechaza peso sin consulta");
        comprobar(!controlador.registrarPeso("consulta-1", 0), "rechaza peso cero");
        comprobar(!controlador.indicarTratamiento(null, "producto-1", "1 ml", "diaria", 5, 1),
                "rechaza tratamiento sin consulta");
        comprobar(!controlador.indicarTratamiento("consulta-1", "producto-1", " ", "diaria", 5, 1),
                "rechaza tratamiento sin dosis");
        comprobar(!controlador.indicarTratamiento("consulta-1", "producto-1", "1 ml", " ", 5, 1),
                "rechaza tratamiento sin frecuencia");
        comprobar(!controlador.indicarTratamiento("consulta-1", "producto-1", "1 ml", "diaria", 0, 1),
                "rechaza tratamiento con duración inválida");
        comprobar(!controlador.indicarTratamiento("consulta-1", "producto-1", "1 ml", "diaria", 5, 0),
                "rechaza tratamiento con cantidad inválida");
        comprobar(!controlador.aplicarVacuna(null, "vacuna-1"), "rechaza vacuna sin consulta");
        comprobar(!controlador.aplicarVacuna("consulta-1", null), "rechaza vacuna sin identificador");
        comprobar(!controlador.confirmarConsulta(null), "rechaza confirmar sin identificador");
        comprobar(!controlador.registrarNotaRectificatoria(null, "corrección", "vet-1"),
                "rechaza nota sin consulta");
        comprobar(!controlador.registrarNotaRectificatoria("consulta-1", " ", "vet-1"),
                "rechaza nota sin texto");
        comprobar(!controlador.registrarNotaRectificatoria("consulta-1", "corrección", null),
                "rechaza nota sin firmante");
        comprobar(controlador.consultarHistoriaClinica(null) == null, "rechaza historia sin mascota");
    }

    private static void probarValidacionesDeFacturacion() {
        Repositorio<Factura> repositorio = new Repositorio<>() {
            @Override
            public void guardar(List<Factura> facturas) {
                throw new AssertionError("No debe persistir una solicitud inválida");
            }

            @Override
            public List<Factura> cargar() {
                return List.of();
            }
        };
        ControladorFacturacion controlador =
                new ControladorFacturacion((Clinica) null, repositorio, null);

        comprobar(!controlador.anularFactura(null, "motivo", "recep-1"),
                "rechaza anular sin identificador");
        comprobar(!controlador.anularFactura("factura-1", null, "recep-1"),
                "rechaza anular sin motivo");
        comprobar(!controlador.anularFactura("factura-1", " ", "recep-1"),
                "rechaza anular con motivo vacío");
        comprobar(!controlador.anularFactura("factura-1", "motivo", null),
                "rechaza anular sin recepcionista");
        comprobar(controlador.cobrarCargoCancelacionTardia(null) == null,
                "rechaza cargo por cancelación sin turno");
    }

    private static void comprobar(boolean resultado, String descripcion) {
        if (!resultado) {
            throw new AssertionError("Fallo: " + descripcion);
        }
        pruebasEjecutadas++;
        System.out.println("OK: " + descripcion);
    }

    private static void comprobarIgual(Object esperado, Object obtenido, String descripcion) {
        comprobar(Objects.equals(esperado, obtenido),
                descripcion + " (esperado: " + esperado + ", obtenido: " + obtenido + ")");
    }
}
