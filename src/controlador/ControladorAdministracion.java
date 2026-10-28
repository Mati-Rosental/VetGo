package controlador;

public class ControladorAdministracion {
    public boolean altaVeterinario(
            String nombre,
            String apellido,
            String documento,
            String telefono,
            String matricula,
            String especialidad,
            String horarios,
            String guardias) {
        System.out.println("ControladorAdministracion.altaVeterinario");
        return false;
    }

    public boolean bajaVeterinario(String documento) {
        System.out.println("ControladorAdministracion.bajaVeterinario");
        return false;
    }

    public boolean modificarVeterinario(
            String documento,
            String nombre,
            String apellido,
            String telefono,
            String matricula,
            String especialidad,
            String horarios,
            String guardias) {
        System.out.println("ControladorAdministracion.modificarVeterinario");
        return false;
    }

    public boolean altaRecepcionista(String nombre, String apellido, String documento, String telefono) {
        System.out.println("ControladorAdministracion.altaRecepcionista");
        return false;
    }

    public boolean bajaRecepcionista(String documento) {
        System.out.println("ControladorAdministracion.bajaRecepcionista");
        return false;
    }

    public boolean modificarRecepcionista(String documento, String nombre, String apellido, String telefono) {
        System.out.println("ControladorAdministracion.modificarRecepcionista");
        return false;
    }

    public boolean altaVacuna(
            String nombre, String especies, String edadMinimaSemanas, String intervaloRefuerzoDias,
            String precioUnitario, String lotes) {
        System.out.println("ControladorAdministracion.altaVacuna");
        return false;
    }

    public boolean bajaVacuna(String idVacuna) {
        System.out.println("ControladorAdministracion.bajaVacuna");
        return false;
    }

    public boolean modificarVacuna(
            String idVacuna,
            String nombre,
            String especies,
            String edadMinimaSemanas,
            String intervaloRefuerzoDias,
            String precioUnitario,
            String lotes) {
        System.out.println("ControladorAdministracion.modificarVacuna");
        return false;
    }

    public boolean altaProducto(
            String codigo, String nombre, String stockMinimo, String precioUnitario, String lotes,
            String idProveedor) {
        System.out.println("ControladorAdministracion.altaProducto");
        return false;
    }

    public boolean bajaProducto(String codigo) {
        System.out.println("ControladorAdministracion.bajaProducto");
        return false;
    }

    public boolean modificarProducto(
            String codigo,
            String nombre,
            String stockMinimo,
            String precioUnitario,
            String lotes,
            String idProveedor) {
        System.out.println("ControladorAdministracion.modificarProducto");
        return false;
    }

    public boolean altaProveedor(String nombre, String telefono, String correo) {
        System.out.println("ControladorAdministracion.altaProveedor");
        return false;
    }

    public boolean bajaProveedor(String idProveedor) {
        System.out.println("ControladorAdministracion.bajaProveedor");
        return false;
    }

    public boolean modificarProveedor(
            String idProveedor, String nombre, String telefono, String correo) {
        System.out.println("ControladorAdministracion.modificarProveedor");
        return false;
    }

    public boolean emitirReporteTurnos(String documentoVeterinario, String desde, String hasta) {
        System.out.println("ControladorAdministracion.emitirReporteTurnos");
        return false;
    }

    public boolean emitirReporteStockBajo() {
        System.out.println("ControladorAdministracion.emitirReporteStockBajo");
        return false;
    }

    public boolean emitirReporteLotesPorVencer(String fechaActual, String diasAnticipacion) {
        System.out.println("ControladorAdministracion.emitirReporteLotesPorVencer");
        return false;
    }

    public boolean emitirRecordatorios(String fechaActual) {
        System.out.println("ControladorAdministracion.emitirRecordatorios");
        return false;
    }
}
