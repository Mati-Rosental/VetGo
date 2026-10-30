package controlador;

import java.time.LocalDate;
import java.util.List;

import modelo.Ave;
import modelo.Clinica;
import modelo.Conejo;
import modelo.Duenio;
import modelo.Especie;
import modelo.Gato;
import modelo.Mascota;
import modelo.Perro;
import modelo.Sexo;
import modelo.TamanioPerro;
import persistencia.Repositorio;

public class ControladorRegistro {
    private Repositorio<Clinica> clinicaRepositorio;
    private Clinica clinica;

    public ControladorRegistro(Repositorio<Clinica> clinicaRepositorio) {
        this.clinicaRepositorio = clinicaRepositorio;
        // Repositorio guarda siempre una lista pero solo debe haber 1 clinica asi que obtiene el primer elemento
        this.clinica = clinicaRepositorio.cargar().get(0);
    }

    public boolean altaDuenio(String nombre, String apellido, String documento, String telefono, String correo, boolean planSalud) {
        Duenio nuevoDuenio = new Duenio(nombre,apellido,documento,telefono,correo,planSalud);
        // Si ya existe devuelve falso
        if (clinica.buscarDuenio(documento) != null) {
            return false;
        }

        clinica.agregarDuenio(nuevoDuenio);
        clinicaRepositorio.guardar(List.of(clinica));
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

        clinicaRepositorio.guardar(java.util.List.of(clinica));

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

        clinicaRepositorio.guardar(java.util.List.of(clinica));

        return true;
    }
}
