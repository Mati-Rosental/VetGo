package persistencia;

import modelo.*;
import java.util.List;

public interface Repositorio<T> {
    void guardar(List<T> lista);

    List<T> cargar();
}
