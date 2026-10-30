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
        System.out.println("Clinica.getVeterinarios");
        return null;
    }

    public Veterinario getVeterinarioDeGuardia(LocalDate dia) {
        System.out.println("Clinica.getVeterinarioDeGuardia");
        return null;
    }

    public List<Mascota> getMascotas() {
        System.out.println("Clinica.getMascotas");
        return null;
    }

    public Duenio buscarDuenio(String duenioDocumento) {
        System.out.println("Clinica.buscarDuenio");
        return null;
    }

    public Mascota buscarMascota(String mascotaID) {
        System.out.println("Clinica.buscarMascota");
        return null;
    }
}
