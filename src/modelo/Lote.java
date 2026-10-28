package modelo;

import java.time.LocalDate;

public class Lote implements Comparable<Lote> {
    private String numero;
    private int cantidad;
    private LocalDate fechaVencimiento;

    public boolean estaVencido(LocalDate fechaActual) {
        System.out.println("Lote.estaVencido");
        return false;
    }

    public void descontar(int cantidad) {
        System.out.println("Lote.descontar");
    }

    @Override
    public int compareTo(Lote otro) {
        System.out.println("Lote.compareTo");
        return 0;
    }
}
