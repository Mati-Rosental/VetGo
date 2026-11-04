package controlador;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modelo.*;
import persistencia.Repositorio;
import servicio.GeneradorReportes;
import servicio.ServicioRecordatorios;

public class ControladorAdministracion {

    private Repositorio<Clinica> clinicaRepositorio;
    private Repositorio<Inventario> inventarioRepositorio;
    private Clinica clinica;
    private Agenda agenda;
    private Inventario inventario;
    private GeneradorReportes generadorReportes;
    private ServicioRecordatorios servicioRecordatorios;

    public ControladorAdministracion(Repositorio<Clinica> clinicaRepositorio, Clinica clinica,
            Repositorio<Inventario> inventarioRepositorio, Inventario inventario, Agenda agenda,
            GeneradorReportes generadorReportes, ServicioRecordatorios servicioRecordatorios) {
        this.clinicaRepositorio = clinicaRepositorio;
        this.clinica = clinica;
        this.inventarioRepositorio = inventarioRepositorio;
        this.inventario = inventario;
        this.agenda = agenda;
        this.generadorReportes = generadorReportes;
        this.servicioRecordatorios = servicioRecordatorios;
    }

    // Gestionar veterinarios

    public boolean altaVeterinario(
            String nombre,
            String apellido,
            String documento,
            String telefono,
            String matricula,
            String especialidad,
            List<HorarioAtencion> horarios) {
        // Validar los parámetros
        if (vacio(nombre) || vacio(apellido) || vacio(documento) || vacio(telefono)
                || vacio(matricula) || vacio(especialidad) || !horariosValidos(horarios)) {
            return false;
        }
        // Rechazar documento o matrícula duplicados
        if (clinica.buscarVeterinarioPorDocumento(documento) != null
                || clinica.buscarVeterinarioPorMatricula(matricula) != null) {
            return false;
        }
        // Crear el veterinario
        Veterinario veterinario = new Veterinario(nombre, apellido, documento, telefono, matricula, especialidad, horarios);
        // Agregarlo a la clínica
        clinica.agregarVeterinario(veterinario);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean bajaVeterinario(String documento) {
        // Validar el parámetro
        if (vacio(documento)) {
            return false;
        }
        // Buscar al veterinario
        Veterinario veterinario = clinica.buscarVeterinarioPorDocumento(documento);
        if (veterinario == null) {
            return false;
        }
        // Con turnos futuros hay que reprogramarlos antes de la baja
        if (!agenda.getTurnosDe(veterinario, LocalDate.now(), null).isEmpty()) {
            return false;
        }
        // Quitarlo de la clínica
        clinica.eliminarVeterinario(veterinario);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean modificarVeterinario(
            String documento,
            String nombre,
            String apellido,
            String telefono,
            String especialidad,
            List<HorarioAtencion> horarios) {
        // Validar los parámetros
        if (vacio(documento) || vacio(nombre) || vacio(apellido) || vacio(telefono)
            || vacio(especialidad) || !horariosValidos(horarios)) {
            return false;
        }
        // Buscar al veterinario
        Veterinario veterinario = clinica.buscarVeterinarioPorDocumento(documento);
        if (veterinario == null) {
            return false;
        }
        // Delegar la modificación a Veterinario
        veterinario.modificarDatos(nombre, apellido, telefono, especialidad, horarios);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    // Gestionar recepcionistas

    public boolean altaRecepcionista(String nombre, String apellido, String documento, String telefono) {
        // Validar los parámetros
        if (vacio(nombre) || vacio(apellido) || vacio(documento) || vacio(telefono)) {
            return false;
        }
        // Rechazar documento duplicado
        if (clinica.buscarRecepcionista(documento) != null) {
            return false;
        }
        // Crear la recepcionista y agregarla a la clínica
        clinica.agregarRecepcionista(new Recepcionista(nombre, apellido, documento, telefono));
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean bajaRecepcionista(String documento) {
        // Validar el parámetro
        if (vacio(documento)) {
            return false;
        }
        // Buscar a la recepcionista
        Recepcionista recepcionista = clinica.buscarRecepcionista(documento);
        if (recepcionista == null) {
            return false;
        }
        // Quitarla de la clínica
        clinica.eliminarRecepcionista(recepcionista);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    public boolean modificarRecepcionista(String documento, String nombre, String apellido, String telefono) {
        // Validar los parámetros
        if (vacio(documento) || vacio(nombre) || vacio(apellido) || vacio(telefono)) {
            return false;
        }
        // Buscar a la recepcionista
        Recepcionista recepcionista = clinica.buscarRecepcionista(documento);
        if (recepcionista == null) {
            return false;
        }
        // Delegar la modificación a Recepcionista
        recepcionista.modificarDatos(nombre, apellido, telefono);
        // Guardar los cambios
        clinicaRepositorio.guardar(List.of(clinica));

        return true;
    }

    // Agregar lotes a productos/vacunas

    public boolean agregarLotesProducto(String codigo, List<Lote> lotes) {
        // Validar los parámetros
        if (vacio(codigo) || lotes == null || lotes.isEmpty()) {
            return false;
        }
        // Los lotes deben ser válidos y no estar vencidos
        if (!lotesValidos(lotes)) {
            return false;
        }
        // Buscar el producto
        Producto producto = inventario.buscarProducto(codigo);
        if (producto == null) {
            return false;
        }
        // Agregar los lotes al producto
        for (Lote lote : lotes) {
            producto.agregarLote(lote);
        }
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    // Gestionar catálogo de vacunas

    public boolean altaVacuna(
            String codigo, String nombre, Set<Especie> especies, int edadMinimaSemanas, int intervaloRefuerzoDias,
            double precioUnitario, int stockMinimo, String CUILProveedor, List<Lote> lotes) {
        // Validar los parámetros
        if (vacio(codigo) || vacio(nombre) || especies == null || especies.isEmpty() || lotes == null) {
            return false;
        }
        if (edadMinimaSemanas < 0 || intervaloRefuerzoDias <= 0
            || precioUnitario <= 0 || stockMinimo < 0) {
            return false;
        }
        // Los lotes deben ser válidos y no estar vencidos
        if (!lotesValidos(lotes)) return false;
        // El proveedor tiene que existir
        Proveedor proveedor = inventario.buscarProveedor(CUILProveedor);
        if (proveedor == null) return false;
        if (inventario.buscarVacuna(codigo) != null) return false;
        // Crear la vacuna y cargarle los lotes
        Vacuna vacuna = new Vacuna(codigo, nombre, stockMinimo, precioUnitario, proveedor,
                especies, edadMinimaSemanas, intervaloRefuerzoDias);
        for (Lote lote : lotes) {
            vacuna.agregarLote(lote);
        }
        // Agregarla al catálogo
        inventario.agregarProducto(vacuna);
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    public boolean bajaVacuna(String idVacuna) {
        // Validar el parámetro
        if (vacio(idVacuna)) {
            return false;
        }
        // Buscar la vacuna
        Vacuna vacuna = inventario.buscarVacuna(idVacuna);
        if (vacuna == null) {
            return false;
        }
        // Dar de baja sin borrar el historial de vacunas aplicadas
        inventario.darDeBajaVacuna(vacuna);
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    public boolean modificarVacuna(
            String idVacuna,
            String nombre,
            Set<Especie> especies,
            int edadMinimaSemanas,
            int intervaloRefuerzoDias,
            double precioUnitario,
            List<Lote> lotes) {
        // Validar los parámetros
        if (vacio(idVacuna) || vacio(nombre) || especies == null || especies.isEmpty()) {
            return false;
        }
        if (edadMinimaSemanas <= 0 || intervaloRefuerzoDias <= 0 || precioUnitario <= 0) {
            return false;
        }
        if (lotes != null && !lotesValidos(lotes)) {
            return false;
        }
        // Buscar la vacuna
        Vacuna vacuna = inventario.buscarVacuna(idVacuna);
        if (vacuna == null) {
            return false;
        }
        // Delegar la modificación a Vacuna
        vacuna.modificarDatos(nombre, especies, edadMinimaSemanas, intervaloRefuerzoDias, precioUnitario);
        if (lotes != null) {
            vacuna.reemplazarLotes(lotes);
        }
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    // Gestionar catálogo de productos

    public boolean altaProducto(
            String codigo, String nombre, int stockMinimo, double precioUnitario, List<Lote> lotes,
            String CUILProveedor) {
        // Validar los parámetros
        if (vacio(nombre) || vacio(CUILProveedor) || lotes == null) {
            return false;
        }
        if (stockMinimo < 0 || precioUnitario <= 0) {
            return false;
        }
        // Rechazar código duplicado
        if (inventario.buscarProducto(codigo) != null) {
            return false;
        }
        // Los lotes deben ser válidos y no estar vencidos
        if (!lotesValidos(lotes)) {
            return false;
        }
        // El proveedor debe existir
        Proveedor proveedor = inventario.buscarProveedor(CUILProveedor);
        if (proveedor == null) {
            return false;
        }
        // Crear el producto y cargarle los lotes
        Producto producto = new Producto(codigo, nombre, stockMinimo, precioUnitario, proveedor);
        for (Lote lote : lotes) {
            producto.agregarLote(lote);
        }
        // Agregarlo al inventario
        inventario.agregarProducto(producto);
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    public boolean bajaProducto(String codigo) {
        // Validar el parámetro
        if (vacio(codigo)) {
            return false;
        }
        // Buscar el producto
        Producto producto = inventario.buscarProducto(codigo);
        if (producto == null) {
            return false;
        }
        // Dar de baja sin borrar el historial de consultas asociadas
        inventario.darDeBajaProducto(producto);
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    public boolean modificarProducto(
            String codigo,
            String nombre,
            int stockMinimo,
            double precioUnitario,
            List<Lote> lotes,
            String CUILProveedor) {
        // Validar los parámetros
        if (vacio(codigo) || vacio(nombre) || vacio(CUILProveedor)) {
            return false;
        }
        if (stockMinimo < 0 || precioUnitario <= 0) {
            return false;
        }
        if (lotes != null && !lotesValidos(lotes)) {
            return false;
        }
        // Buscar el producto y el proveedor
        Producto producto = inventario.buscarProducto(codigo);
        Proveedor proveedor = inventario.buscarProveedor(CUILProveedor);
        if (producto == null || proveedor == null) {
            return false;
        }
        // Delegar la modificación a Producto
        producto.modificarDatos(nombre, stockMinimo, precioUnitario, proveedor);
        if (lotes != null) {
            producto.reemplazarLotes(lotes);
        }
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    // Gestionar catálogo de proveedores

    public boolean altaProveedor(String CUIL, String nombre, String telefono, String correo) {
        // Validar los parámetros
        if (vacio(nombre) || vacio(telefono) || vacio(correo)) {
            return false;
        }
        // Rechazar proveedor ya registrado
        if (inventario.buscarProveedor(CUIL) != null) {
            return false;
        }
        // Crear el proveedor y agregarlo al inventario
        inventario.agregarProveedor(new Proveedor(CUIL, nombre, telefono, correo));
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    public boolean bajaProveedor(String CUILProveedor) {
        // Validar el parámetro
        if (vacio(CUILProveedor)) {
            return false;
        }
        // Buscar el proveedor
        Proveedor proveedor = inventario.buscarProveedor(CUILProveedor);
        if (proveedor == null) {
            return false;
        }
        // No se puede borrar si tiene productos asociados
        if (inventario.proveedorTieneProductos(proveedor)) {
            return false;
        }
        // Quitarlo del inventario
        inventario.eliminarProveedor(proveedor);
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    public boolean modificarProveedor(
            String CUILProveedor, String nombre, String telefono, String correo) {
        // Validar los parámetros
        if (vacio(CUILProveedor) || vacio(nombre) || vacio(telefono) || vacio(correo)) {
            return false;
        }
        // Buscar el proveedor
        Proveedor proveedor = inventario.buscarProveedor(CUILProveedor);
        if (proveedor == null) {
            return false;
        }
        // Delegar la modificación a Proveedor
        proveedor.modificarDatos(nombre, telefono, correo);
        // Guardar los cambios
        inventarioRepositorio.guardar(List.of(inventario));

        return true;
    }

    // Emitir reportes / recordatorios

    public boolean emitirReporteTurnos(String documentoVeterinario, LocalDate desde, LocalDate hasta) {
        // Validar los parámetros
        if (vacio(documentoVeterinario) || desde == null || hasta == null || hasta.isBefore(desde)) {
            return false;
        }
        // Buscar al veterinario
        Veterinario veterinario = clinica.buscarVeterinarioPorDocumento(documentoVeterinario);
        if (veterinario == null) {
            return false;
        }
        // Delegar la generación del reporte a GeneradorReportes
        List<Turno> turnos = generadorReportes.turnosPorVeterinario(veterinario, desde, hasta);
        // Mostrar el reporte
        List<String> lineas = new ArrayList<>();
        if (turnos != null) {
            for (Turno turno : turnos) {
                lineas.add(turno.getDescripcion());
            }
        }
        mostrarReporte("Turnos de " + veterinario.getNombreCompleto()
                + " (" + desde + " a " + hasta + ")", lineas);

        return true;
    }

    public boolean emitirReporteStockBajo() {
        // Delegar la generación del reporte a GeneradorReportes
        List<Producto> productos = generadorReportes.productosConStockBajo();
        // Mostrar el reporte
        List<String> lineas = new ArrayList<>();
        if (productos != null) {
            for (Producto producto : productos) {
                lineas.add(producto.getDescripcion());
            }
        }
        mostrarReporte("Productos con stock bajo", lineas);

        return true;
    }

    public boolean emitirReporteLotesPorVencer(LocalDate fechaActual, int diasAnticipacion) {
        // Validar los parámetros
        if (fechaActual == null || diasAnticipacion < 0) {
            return false;
        }
        // Delegar la generación del reporte a GeneradorReportes
        List<Lote> lotes = generadorReportes.lotesProximosAVencer(fechaActual, diasAnticipacion);
        // Mostrar el reporte
        List<String> lineas = new ArrayList<>();
        if (lotes != null) {
            for (Lote lote : lotes) {
                lineas.add(lote.getDescripcion());
            }
        }
        mostrarReporte("Lotes próximos a vencer (" + diasAnticipacion + " días desde " + fechaActual + ")", lineas);

        return true;
    }

    public boolean emitirRecordatorios(LocalDate fechaActual) {
        // Validar el parámetro
        if (fechaActual == null) {
            return false;
        }
        // Delegar la emisión de recordatorios a ServicioRecordatorios
        servicioRecordatorios.emitirRecordatorios(clinica.getMascotas(), fechaActual);

        return true;
    }

    // Auxiliares

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    // Al menos un bloque, fin posterior al inicio y un único bloque por día
    private boolean horariosValidos(List<HorarioAtencion> horarios) {
        if (horarios == null || horarios.isEmpty()) {
            return false;
        }
        Set<DayOfWeek> dias = new HashSet<>();
        for (HorarioAtencion horario : horarios) {
            if (horario == null || !horario.getHoraFin().isAfter(horario.getHoraInicio())
                    || !dias.add(horario.getDia())) {
                return false;
            }
        }
        return true;
    }

    // Cantidades positivas y ningún lote vencido
    private boolean lotesValidos(List<Lote> lotes) {
        LocalDate hoy = LocalDate.now();
        for (Lote lote : lotes) {
            if (lote == null || lote.getCantidad() <= 0 || lote.estaVencido(hoy)) {
                return false;
            }
        }
        return true;
    }

    private void mostrarReporte(String titulo, List<String> lineas) {
        System.out.println("\n--- " + titulo + " ---");
        if (lineas.isEmpty()) {
            System.out.println("No hay datos para el reporte.");
            return;
        }
        for (String linea : lineas) {
            System.out.println(linea);
        }
    }
}