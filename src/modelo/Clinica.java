package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Clinica {
    private String nombre;
    private final List<Veterinario> veterinarios = new ArrayList<>();
    private final List<Recepcionista> recepcionistas = new ArrayList<>();
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Duenio> duenios = new ArrayList<>();

    public Clinica(String nombre) {
        this.nombre = nombre;
    }

    public void agregarVeterinario(Veterinario veterinario) {
        System.out.println("Clinica.agregarVeterinario");
    }

    public void agregarRecepcionista(Recepcionista recepcionista) {
        System.out.println("Clinica.agregarRecepcionista");
    }

    public void agregarDuenio(Duenio duenio) {
        System.out.println("Clinica.registrarCliente");
    }

    public List<Veterinario> getVeterinarios() {
        return veterinarios;
    }

    public Veterinario getVeterinarioDeGuardia(LocalDate dia) {
        System.out.println("Clinica.getVeterinarioDeGuardia");
        return null;
    }

    public List<Mascota> getMascotas() {
        List<Mascota> todas = new ArrayList<>();
        for (Duenio duenio : duenios) {
            todas.addAll(duenio.getMascotas());
        }
        return todas;
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public Duenio buscarDuenio(String duenioDocumento) {
        System.out.println("Clinica.buscarDuenio");
        return null;
    }

    public Recepcionista buscarRecepcionista(String recepcionistaDocumento) {
        System.out.println("Clinica.buscarRecepcionista");
        return null;
    }    

    public Veterinario buscarVeterinarioPorDocumento(String veterinarioDocumento) {
        System.out.println("Clinica.buscarVeterinarioPorDocumento");
        return null;
    }    

    public Mascota buscarMascota(String idMascota) {
        System.out.println("Clinica.buscarMascota");
        return null;
    }

    public Consulta buscarConsulta(String idConsulta) {
        // Recorrer dueños, mascotas e historias clínicas.
        return null;
    }

    public Object buscarVeterinarioPorMatricula(String matricula) {
        System.out.println("Clinica.buscarVeterinarioPorMatricula");
        return null;
    }

    public void eliminarVeterinario(Veterinario veterinario) {
        System.out.println("Clinica.eliminarVeterinario");
    }

    public void eliminarRecepcionista(Recepcionista recepcionista) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminarRecepcionista'");
    }
}
