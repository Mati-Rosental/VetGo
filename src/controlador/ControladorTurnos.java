package controlador;

public class ControladorTurnos {
    public boolean asignarTurno(
            String idMascota, String documentoVeterinario, String tipo, String fechaHora) {
        System.out.println("ControladorTurnos.asignarTurno");
        return false;
    }

    public boolean reprogramarTurno(String idTurno, String nuevaFechaHora, String documentoVeterinario) {
        System.out.println("ControladorTurnos.reprogramarTurno");
        return false;
    }

    public boolean cancelarTurno(String idTurno, String ahora) {
        System.out.println("ControladorTurnos.cancelarTurno");
        return false;
    }

    public boolean registrarAusencia(String idTurno) {
        System.out.println("ControladorTurnos.registrarAusencia");
        return false;
    }

    public boolean atenderUrgencia(String idMascota, String dia) {
        System.out.println("ControladorTurnos.atenderUrgencia");
        return false;
    }
}
