package controlador;

import java.time.LocalDate;

import modelo.*;
import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.enumeradores.TamanioPerro;
import modelo.mascotas.Ave;
import modelo.mascotas.Conejo;
import modelo.mascotas.Gato;
import modelo.mascotas.Mascota;
import modelo.mascotas.Perro;
import modelo.personas.Clinica;
import modelo.personas.Duenio;
import persistencia.EstadoSistema;
import persistencia.Repositorio;

public class ControladorRegistro {
    private Repositorio estadoRepositorio;
    private EstadoSistema estado;
    private Clinica clinica;

    public ControladorRegistro(Repositorio estadoRepositorio, EstadoSistema estado) {
        this.estadoRepositorio = estadoRepositorio;
        this.estado = estado;
        this.clinica = estado.getClinica();
    }

    public boolean altaDuenio(String nombre, String apellido, String documento, String telefono, String correo, boolean planSalud) {
        Duenio nuevoDuenio = new Duenio(nombre,apellido,documento,telefono,correo,planSalud);
        // Si ya existe devuelve falso
        if (clinica.buscarDuenio(documento) != null) {
            return false;
        }

        clinica.agregarDuenio(nuevoDuenio);
        estadoRepositorio.guardar(estado);
        return true;
    }

    public boolean altaMascota(
            String documentoDuenio,
            Especie especie,
            String nombre,
            String raza,
            Sexo sexo,
            LocalDate fechaNacimiento,
            double peso,
            String numeroChip,
            TamanioPerro tamanio,
            boolean viveEnInterior,
            boolean vuela) {
        // Si el duenio no existe devuelve false
        Duenio duenioMascota = clinica.buscarDuenio(documentoDuenio);
        if (duenioMascota == null) {
            return false;
        }

        Mascota nuevaMascota;
        switch (especie) {
            case PERRO:
                nuevaMascota = new Perro(
                    duenioMascota, nombre, raza, sexo, fechaNacimiento,
                    peso, numeroChip, tamanio
                );
                break;

            case GATO:
                nuevaMascota = new Gato(
                    duenioMascota, nombre, raza, sexo, fechaNacimiento,
                    peso, viveEnInterior
                );
                break;

            case AVE:
                nuevaMascota = new Ave(
                    duenioMascota, nombre, raza, sexo, fechaNacimiento,
                    peso, vuela
                );
                break;

            case CONEJO:
                nuevaMascota = new Conejo(
                    duenioMascota, nombre, raza, sexo, fechaNacimiento, peso
                );
                break;

            default:
                return false;
        }

        duenioMascota.agregarMascota(nuevaMascota);

        estadoRepositorio.guardar(estado);

        return true;
    }

    public boolean transferirMascota(String idMascota, String documentoNuevoDuenio) {
        Mascota mascota = clinica.buscarMascota(idMascota);
        Duenio nuevoDuenio = clinica.buscarDuenio(documentoNuevoDuenio);

        if (mascota == null || nuevoDuenio == null) {
            return false;
        }
        if (mascota.getDuenio().equals(nuevoDuenio)) {
            return false;
        }

        mascota.transferirA(nuevoDuenio, LocalDate.now());

        estadoRepositorio.guardar(estado);

        return true;
    }
}