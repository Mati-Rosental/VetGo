package persistencia;

public interface Repositorio {
    void guardar(EstadoSistema sistema);

    EstadoSistema cargar();
}
