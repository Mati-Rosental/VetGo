package modelo.inventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import modelo.enumeradores.Especie;

public class Vacuna extends Producto {
    private Set<Especie> especies;
    private int edadMinimaSemanas;
    private int intervaloRefuerzoDias;

    public Vacuna(String codigo, String nombre, int stockMinimo, double precioUnitario, Proveedor proveedor, Set<Especie> especies, int edadMinimaSemanas, int intervaloRefuerzoDias) {
        super(codigo, nombre, stockMinimo, precioUnitario, proveedor);
        this.especies = especies;
        this.edadMinimaSemanas = edadMinimaSemanas;
        this.intervaloRefuerzoDias = intervaloRefuerzoDias;
    }

    public boolean aplicaA(Especie especie) {
        return especies.contains(especie);
    }

    public boolean permiteEdad(int semanas) {
        return semanas >= edadMinimaSemanas;
    }

    public LocalDate calcularProximoRefuerzo(LocalDate fechaAplicacion) {
        return fechaAplicacion.plusDays(intervaloRefuerzoDias);
    }

    public String getNumeroPrimerLote() {
        // Obtiene el numero del primer lote disponible, null si no hay
        LocalDate hoy = LocalDate.now();
        for (Lote l : getLotes()) {
            if (!l.estaVencido(hoy) && l.getCantidad() > 0) {
                return l.getNumero();
            }
        }
        return null;
    }

    @Override
    public String getDescripcion() {
        return "Vacuna: " + getNombre() + " [Código: " + getCodigo() + ", Refuerzo cada " + intervaloRefuerzoDias + " días]";
    }

    // Modifica los datos de la vacuna, manteniendo los valores actuales cuando los nuevos son null o no son válidos
    public void modificarDatos(String nuevoNombre, Set<Especie> nuevasEspecies, Integer nuevaEdadMinimaSemanas,
            Integer nuevoIntervaloRefuerzoDias, Double nuevoPrecioUnitario) {
        if (nuevoNombre != null && !nuevoNombre.isBlank()) this.nombre = nuevoNombre;
        if (nuevasEspecies != null) this.especies = nuevasEspecies;
        if (nuevaEdadMinimaSemanas != null && nuevaEdadMinimaSemanas >= 0) this.edadMinimaSemanas = nuevaEdadMinimaSemanas;
        if (nuevoIntervaloRefuerzoDias != null && nuevoIntervaloRefuerzoDias >= 0) this.intervaloRefuerzoDias = nuevoIntervaloRefuerzoDias;
        if (nuevoPrecioUnitario != null && nuevoPrecioUnitario > 0) this.precioUnitario = nuevoPrecioUnitario;
    }
}
