package controlador;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modelo.*;
import modelo.agenda.Agenda;
import modelo.agenda.Turno;
import modelo.enumeradores.Especie;
import modelo.inventario.Inventario;
import modelo.inventario.Lote;
import modelo.inventario.Producto;
import modelo.inventario.Proveedor;
import modelo.inventario.Vacuna;
import modelo.personas.Clinica;
import modelo.personas.HorarioAtencion;
import modelo.personas.Recepcionista;
import modelo.personas.Veterinario;
import persistencia.EstadoSistema;
import persistencia.Repositorio;
import servicio.GeneradorReportes;
import servicio.ServicioRecordatorios;

public class ControladorAdministracion {

    private Repositorio estadoRepositorio;
    private EstadoSistema estado;
    private Clinica clinica;
    private Agenda agenda;
    private Inventario inventario;
    private GeneradorReportes generadorReportes;
    private ServicioRecordatorios servicioRecordatorios;

    public ControladorAdministracion(Repositorio estadoRepositorio, EstadoSistema estado,
            GeneradorReportes generadorReportes, ServicioRecordatorios servicioRecordatorios) {
        this.estadoRepositorio = estadoRepositorio;
        this.estado = estado;
        this.clinica = estado.getClinica();
        this.agenda = estado.getAgenda();
        this.inventario = estado.getInventario();
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
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

        return true;
    }

    // Gestionar catálogo de vacunas

    public boolean altaVacuna(
            String codigo, String nombre, Set<Especie> especies, int edadMinimaSemanas, int intervaloRefuerzoDias,
            double precioUnitario, int stockMinimo, String CUITProveedor, List<Lote> lotes) {
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
        Proveedor proveedor = inventario.buscarProveedor(CUITProveedor);
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
        estadoRepositorio.guardar(estado);

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
        inventario.darDeBajaProducto(vacuna);
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

        return true;
    }

    // Gestionar catálogo de productos

    public boolean altaProducto(
            String codigo, String nombre, int stockMinimo, double precioUnitario, List<Lote> lotes,
            String CUITProveedor) {
        // Validar los parámetros
        if (vacio(nombre) || vacio(CUITProveedor) || lotes == null) {
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
        Proveedor proveedor = inventario.buscarProveedor(CUITProveedor);
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
        estadoRepositorio.guardar(estado);

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
        estadoRepositorio.guardar(estado);

        return true;
    }

    public boolean modificarProducto(
            String codigo,
            String nombre,
            int stockMinimo,
            double precioUnitario,
            List<Lote> lotes,
            String CUITProveedor) {
        // Validar los parámetros
        if (vacio(codigo) || vacio(nombre) || vacio(CUITProveedor)) {
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
        Proveedor proveedor = inventario.buscarProveedor(CUITProveedor);
        if (producto == null || proveedor == null) {
            return false;
        }
        // Delegar la modificación a Producto
        producto.modificarDatos(nombre, stockMinimo, precioUnitario, proveedor);
        if (lotes != null) {
            producto.reemplazarLotes(lotes);
        }
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

        return true;
    }

    // Gestionar catálogo de proveedores

    public boolean altaProveedor(String CUIT, String nombre, String telefono, String correo) {
        // Validar los parámetros
        if (vacio(nombre) || vacio(telefono) || vacio(correo)) {
            return false;
        }
        // Rechazar proveedor ya registrado
        if (inventario.buscarProveedor(CUIT) != null) {
            return false;
        }
        // Crear el proveedor y agregarlo al inventario
        inventario.agregarProveedor(new Proveedor(CUIT, nombre, telefono, correo));
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

        return true;
    }

    public boolean bajaProveedor(String CUITProveedor) {
        // Validar el parámetro
        if (vacio(CUITProveedor)) {
            return false;
        }
        // Buscar el proveedor
        Proveedor proveedor = inventario.buscarProveedor(CUITProveedor);
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
        estadoRepositorio.guardar(estado);

        return true;
    }

    public boolean modificarProveedor(
            String CUITProveedor, String nombre, String telefono, String correo) {
        // Validar los parámetros
        if (vacio(CUITProveedor) || vacio(nombre) || vacio(telefono) || vacio(correo)) {
            return false;
        }
        // Buscar el proveedor
        Proveedor proveedor = inventario.buscarProveedor(CUITProveedor);
        if (proveedor == null) {
            return false;
        }
        // Delegar la modificación a Proveedor
        proveedor.modificarDatos(nombre, telefono, correo);
        // Guardar los cambios
        estadoRepositorio.guardar(estado);

        return true;
    }

    // Emitir reportes / recordatorios

    public List<Turno> emitirReporteTurnos(String documentoVeterinario, LocalDate desde, LocalDate hasta) {
        // Validar los parámetros
        if (vacio(documentoVeterinario) || desde == null || hasta == null || hasta.isBefore(desde)) {
            return null;
        }
        // Buscar al veterinario
        Veterinario veterinario = clinica.buscarVeterinarioPorDocumento(documentoVeterinario);
        if (veterinario == null) {
            return null;
        }
        // Delegar la generación del reporte a GeneradorReportes
        return generadorReportes.turnosPorVeterinario(veterinario, desde, hasta);
    }

    public List<Producto> emitirReporteStockBajo() {
        // Delegar la generación del reporte a GeneradorReportes
        return generadorReportes.productosConStockBajo();
    }

    public List<Lote> emitirReporteLotesPorVencer(LocalDate fechaActual, int diasAnticipacion) {
        // Validar los parámetros
        if (fechaActual == null || diasAnticipacion < 0) {
            return null;
        }
        // Delegar la generación del reporte a GeneradorReportes
        return generadorReportes.lotesProximosAVencer(fechaActual, diasAnticipacion);
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
}