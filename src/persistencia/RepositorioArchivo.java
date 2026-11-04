package persistencia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RepositorioArchivo implements Repositorio {
    private String rutaArchivo;

    public RepositorioArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void guardar(EstadoSistema sistema) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(sistema);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EstadoSistema cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (EstadoSistema) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}