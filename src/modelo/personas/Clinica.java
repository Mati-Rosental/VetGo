package modelo.personas;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import modelo.atencion.Consulta;
import modelo.mascotas.Mascota;

public class Clinica implements Serializable {
    private String nombre;
    private final List<Veterinario> veterinarios = new ArrayList<>();
    private final List<Recepcionista> recepcionistas = new ArrayList<>();
    private final List<Administrador> administradores = new ArrayList<>();
    private final List<Duenio> duenios = new ArrayList<>();

    public Clinica(String nombre) {
        this.nombre = nombre;
    }

    public void agregarVeterinario(Veterinario veterinario) {
        veterinarios.add(veterinario);
    }

    public void agregarRecepcionista(Recepcionista recepcionista) {
        recepcionistas.add(recepcionista);
    }

    public void agregarDuenio(Duenio duenio) {
        duenios.add(duenio);
    }

    public List<Veterinario> getVeterinarios() {
        return veterinarios;
    }

    public List<Recepcionista> getRecepcionistas() {
        return recepcionistas;
    }

    public Veterinario getVeterinarioDeGuardia(LocalDateTime momento) {
        for (Veterinario v : veterinarios) {
            if (v.esDeGuardia(momento)) {
                return v;
            }
        }
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
        for (Duenio d : duenios) {
            if (d.getDocumento().equals(duenioDocumento)) {
                return d;
            }
        }
        return null;
    }

    public Recepcionista buscarRecepcionista(String recepcionistaDocumento) {
        for (Recepcionista r : recepcionistas) {
            if (r.getDocumento().equals(recepcionistaDocumento)) {
                return r;
            }
        }
        return null;
    }    

    public Veterinario buscarVeterinarioPorDocumento(String veterinarioDocumento) {
        for (Veterinario v : veterinarios) {
            if (v.getDocumento().equals(veterinarioDocumento)) {
                return v;
            }
        }
        return null;
    }    

    public Mascota buscarMascota(String idMascota) {
        for (Mascota m : getMascotas()) {
            if (m.getId().equals(idMascota)) {
                return m;
            }
        }
        return null;
    }

    public Consulta buscarConsulta(String idConsulta) {
        for (Mascota m : getMascotas()) {
            for (Consulta c : m.getHistoriaClinica().getConsultas()) {
                if (c.getId().equals(idConsulta)) {
                    return c;
                }
            }
        }
        return null;
    }

    public Veterinario buscarVeterinarioPorMatricula(String matricula) {
        for (Veterinario v : veterinarios) {
            if (v.getMatricula().equals(matricula)) {
                return v;
            }
        }
        return null;
    }

    public void eliminarVeterinario(Veterinario veterinario) {
        veterinarios.remove(veterinario);
    }

    public void eliminarRecepcionista(Recepcionista recepcionista) {
        recepcionistas.remove(recepcionista);
    }

    public void agregarAdministrador(Administrador administrador) {
        administradores.add(administrador);
    }

    public List<Duenio> getDuenios() {
        return duenios;
    }

    public List<Consulta> getConsultas() {
        List<Consulta> consultas = new ArrayList<>();

        for (Mascota mascota : getMascotas()) {
            consultas.addAll(mascota.getHistoriaClinica().getConsultas());
        }

        return consultas;
    }
}