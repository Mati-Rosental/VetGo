package persistencia;

import modelo.*;
import java.util.List;

public class RepositorioArchivo<T> implements Repositorio<T> {
    private String rutaArchivo;

    @Override
    public void guardar(List<T> lista) {
        System.out.println("RepositorioArchivo.guardar");
    }

    @Override
    public List<T> cargar() {
        System.out.println("RepositorioArchivo.cargar");
        return null;
    }
}
