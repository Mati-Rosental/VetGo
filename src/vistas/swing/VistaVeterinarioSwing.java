
package vistas.swing;

import controlador.ControladorAtencion;
import modelo.agenda.Turno;
import modelo.atencion.Consulta;
import modelo.inventario.Producto;
import modelo.inventario.Vacuna;
import modelo.mascotas.HistoriaClinica;
import modelo.mascotas.Mascota;
import modelo.personas.Veterinario;
import vistas.interfaces.VistaVeterinario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

public class VistaVeterinarioSwing implements VistaVeterinario {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ControladorAtencion controlador;
    private JFrame ventana;

    public VistaVeterinarioSwing(ControladorAtencion controlador) {
        this.controlador = controlador;
    }

    // Mantiene la integración con VistaPrincipal
    public void iniciarMenu(java.util.Scanner scanner) {
        CountDownLatch cierre = new CountDownLatch(1);
        try {
            SwingUtilities.invokeAndWait(() -> abrirVentana(cierre));
            cierre.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo abrir la vista de veterinario", e);
        }
    }

    private void abrirVentana(CountDownLatch cierre) {
        ventana = new JFrame("VetGo | Veterinario");
        ventana.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ventana.setMinimumSize(new Dimension(850, 600));
        ventana.setSize(1000, 680);
        ventana.setLocationRelativeTo(null);
        ventana.setContentPane(crearContenido());
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cierre.countDown();
            }
        });
        ventana.setVisible(true);
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel titulo = new JLabel("Panel veterinario");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 25f));
        JLabel subtitulo = new JLabel("Atención clínica, tratamientos e historias clínicas");
        subtitulo.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        contenido.add(encabezado, BorderLayout.NORTH);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Inicio", crearInicio());
        pestanias.addTab("Atención", crearPanelAcciones(
                new String[]{"Iniciar consulta", "Registrar peso", "Registrar diagnóstico", "Registrar observaciones",
                        "Indicar tratamiento", "Aplicar vacuna", "Confirmar consulta"},
                new Runnable[]{this::iniciarConsulta, this::registrarPeso, this::registrarDiagnostico,
                        this::registrarObservaciones, this::indicarTratamiento, this::aplicarVacuna,
                        this::confirmarConsulta}));
        pestanias.addTab("Historias clínicas", crearPanelAcciones(
                new String[]{"Consultar historia clínica", "Registrar nota rectificatoria"},
                new Runnable[]{this::consultarHistoria, this::registrarNota}));

        contenido.add(pestanias, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearInicio() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(new EmptyBorder(20, 10, 10, 10));

        JTextArea ayuda = new JTextArea("Seleccione una pestaña para comenzar.\n\n"
                + "• Atención: iniciar consultas, registrar diagnósticos y tratamientos\n"
                + "• Historias clínicas: consultar historias y registrar notas rectificatorias");
        ayuda.setEditable(false);
        ayuda.setOpaque(false);
        ayuda.setFont(ayuda.getFont().deriveFont(15f));
        panel.add(ayuda, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelAcciones(String[] nombres, Runnable[] acciones) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 20, 25, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(7, 90, 7, 90);

        for (int i = 0; i < nombres.length; i++) {
            JButton boton = new JButton(nombres[i]);
            boton.setFont(boton.getFont().deriveFont(15f));
            boton.setPreferredSize(new Dimension(340, 42));

            Runnable accion = acciones[i];
            boton.addActionListener(e -> ejecutar(accion));

            panel.add(boton, c);
            c.gridy++;
        }

        return panel;
    }

    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException e) {
            mostrarError(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private void iniciarConsulta() {
        List<Turno> turnos = controlador.obtenerTurnosProgramados();
        if (turnos.isEmpty()) {
            mostrarError("No hay turnos programados para iniciar.");
            return;
        }

        JComboBox<Opcion<Turno>> turno = combo(turnos, this::descripcionTurno);
        JTextField motivo = campo("Motivo de consulta");

        if (!confirmar("Iniciar consulta", formulario(
                new JLabel("Turno"), turno,
                new JLabel("Motivo"), motivo))) return;

        if (controlador.iniciarConsulta(seleccion(turno).getId(), valor(motivo))) {
            mostrarExito("La consulta fue iniciada correctamente.");
        } else {
            mostrarError("No se pudo iniciar la consulta. Verifique que el turno siga disponible.");
        }
    }

    private void registrarPeso() {
        seleccionarConsultaEnCurso("Registrar peso", consulta -> {
            JTextField peso = campo("Peso en kg");

            if (!confirmar("Registrar peso", formulario(
                    new JLabel("Peso (kg)"), peso))) return;

            try {
                double valorPeso = Double.parseDouble(valor(peso).replace(',', '.'));
                if (valorPeso <= 0) throw new NumberFormatException();

                resultado(controlador.registrarPeso(consulta.getId(), valorPeso),
                        "Peso registrado correctamente.",
                        "No se pudo registrar el peso.");
            } catch (NumberFormatException e) {
                mostrarError("Ingrese un peso válido mayor a cero.");
            }
        });
    }

    private void registrarDiagnostico() {
        seleccionarConsultaEnCurso("Registrar diagnóstico", consulta -> {
            JTextArea diagnostico = area("Diagnóstico");

            if (!confirmar("Registrar diagnóstico", formulario(
                    new JLabel("Diagnóstico"), new JScrollPane(diagnostico)))) return;

            resultado(controlador.registrarDiagnostico(consulta.getId(), diagnostico.getText().trim()),
                    "Diagnóstico registrado correctamente.",
                    "No se pudo registrar el diagnóstico.");
        });
    }

    private void registrarObservaciones() {
        seleccionarConsultaEnCurso("Registrar observaciones", consulta -> {
            JTextArea observaciones = area("Observaciones");

            if (!confirmar("Registrar observaciones", formulario(
                    new JLabel("Observaciones"), new JScrollPane(observaciones)))) return;

            resultado(controlador.registrarObservaciones(consulta.getId(), observaciones.getText().trim()),
                    "Observaciones registradas correctamente.",
                    "No se pudieron registrar las observaciones.");
        });
    }

    private void indicarTratamiento() {
        seleccionarConsultaEnCurso("Indicar tratamiento", consulta -> {
            List<Producto> productos = controlador.obtenerProductos().stream()
                    .filter(p -> !(p instanceof Vacuna))
                    .collect(Collectors.toList());

            if (productos.isEmpty()) {
                mostrarError("No hay productos registrados en el inventario.");
                return;
            }

            JComboBox<Opcion<Producto>> producto = combo(productos,
                    p -> p.getNombre() + " — Stock: " + p.getStockActual());

            JTextField dosis = campo("Ej. 5 ml");
            JTextField frecuencia = campo("Ej. cada 8 horas");
            JTextField duracion = campo("Días");
            JTextField cantidad = campo("Unidades a descontar");

            JPanel form = formulario(
                    new JLabel("Medicamento"), producto,
                    new JLabel("Dosis"), dosis,
                    new JLabel("Frecuencia"), frecuencia,
                    new JLabel("Duración (días)"), duracion,
                    new JLabel("Cantidad"), cantidad);

            if (!confirmar("Indicar tratamiento", form)) return;

            try {
                int dias = Integer.parseInt(valor(duracion));
                int unidades = Integer.parseInt(valor(cantidad));

                resultado(controlador.indicarTratamiento(consulta.getId(), seleccion(producto).getCodigo(),
                                valor(dosis), valor(frecuencia), dias, unidades),
                        "Tratamiento registrado correctamente.",
                        "No se pudo registrar el tratamiento. Revise los datos y el stock disponible.");
            } catch (NumberFormatException e) {
                mostrarError("Duración y cantidad deben ser números enteros.");
            }
        });
    }

    private void aplicarVacuna() {
        seleccionarConsultaEnCurso("Aplicar vacuna", consulta -> {
            List<Vacuna> vacunas = controlador.obtenerVacunas();

            if (vacunas.isEmpty()) {
                mostrarError("No hay vacunas registradas en el inventario.");
                return;
            }

            JComboBox<Opcion<Vacuna>> vacuna = combo(vacunas,
                    v -> v.getNombre() + " — Stock: " + v.getStockActual());

            if (!confirmar("Aplicar vacuna", formulario(
                    new JLabel("Vacuna"), vacuna))) return;

            resultado(controlador.aplicarVacuna(consulta.getId(), seleccion(vacuna).getCodigo()),
                    "Vacuna aplicada correctamente.",
                    "No se pudo aplicar la vacuna. Verifique especie, edad y stock.");
        });
    }

    private void confirmarConsulta() {
        seleccionarConsultaEnCurso("Confirmar consulta", consulta -> {
            if (!confirmar("¿Desea confirmar la consulta? Se descontarán los insumos utilizados.")) return;

            resultado(controlador.confirmarConsulta(consulta.getId()),
                    "Consulta confirmada correctamente.",
                    "No se pudo confirmar la consulta.");
        });
    }

    private void consultarHistoria() {
        List<Mascota> mascotas = controlador.obtenerMascotas();

        if (mascotas.isEmpty()) {
            mostrarError("No hay mascotas registradas.");
            return;
        }

        JComboBox<Opcion<Mascota>> mascota = combo(mascotas,
                m -> m.getNombre() + " — " + m.getEspecie() + " — Dueño: " + m.getDuenio().getNombreCompleto());

        if (!confirmar("Seleccionar mascota", formulario(
                new JLabel("Mascota"), mascota))) return;

        HistoriaClinica historia = controlador.consultarHistoriaClinica(seleccion(mascota).getId());

        if (historia == null) {
            mostrarError("No se encontró la historia clínica.");
        } else {
            mostrarLista("Historia clínica", historia.toString());
        }
    }

    private void registrarNota() {
        List<Consulta> consultas = controlador.obtenerConsultasFinalizadas();
        List<Veterinario> veterinarios = controlador.obtenerVeterinarios();

        if (consultas.isEmpty() || veterinarios.isEmpty()) {
            mostrarError("Se necesita una consulta finalizada y un veterinario registrado.");
            return;
        }

        JComboBox<Opcion<Consulta>> consulta = combo(consultas, this::descripcionConsulta);
        JComboBox<Opcion<Veterinario>> firmante = combo(veterinarios,
                v -> v.getNombreCompleto() + " — " + v.getDocumento());
        JTextArea texto = area("Texto de la nota rectificatoria");

        JPanel form = formulario(
                new JLabel("Consulta"), consulta,
                new JLabel("Veterinario firmante"), firmante,
                new JLabel("Nota"), new JScrollPane(texto));

        if (!confirmar("Registrar nota rectificatoria", form)) return;

        resultado(controlador.registrarNotaRectificatoria(seleccion(consulta).getId(),
                        texto.getText().trim(), seleccion(firmante).getDocumento()),
                "Nota rectificatoria registrada correctamente.",
                "No se pudo registrar la nota rectificatoria.");
    }

    private void seleccionarConsultaEnCurso(String titulo, java.util.function.Consumer<Consulta> accion) {
        List<Consulta> consultas = controlador.obtenerConsultasEnCurso();

        if (consultas.isEmpty()) {
            mostrarError("No hay consultas en curso.");
            return;
        }

        JComboBox<Opcion<Consulta>> combo = combo(consultas, this::descripcionConsulta);

        if (confirmar(titulo, formulario(new JLabel("Consulta"), combo))) {
            accion.accept(seleccion(combo));
        }
    }

    private String descripcionTurno(Turno t) {
        return t.getFechaHora().format(FORMATO) + " — " + t.getMascota().getNombre() + " — "
                + t.getTipoAtencion() + " — " + t.getVeterinario().getNombreCompleto();
    }

    private String descripcionConsulta(Consulta c) {
        Turno t = c.getTurno();
        return t.getMascota().getNombre() + " — Dueño: " + t.getMascota().getDuenio().getNombreCompleto()
                + " — " + t.getFechaHora().format(FORMATO) + " — ID " + c.getId();
    }

    private JTextField campo(String ayuda) {
        JTextField campo = new JTextField(22);
        campo.setToolTipText(ayuda);
        return campo;
    }

    private JTextArea area(String ayuda) {
        JTextArea area = new JTextArea(5, 25);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setToolTipText(ayuda);
        return area;
    }

    private JPanel formulario(Component... componentes) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        for (Component componente : componentes) {
            panel.add(componente);
        }
        return panel;
    }

    private boolean confirmar(String titulo, Component formulario) {
        return JOptionPane.showConfirmDialog(ventana, formulario, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(ventana, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private <T> JComboBox<Opcion<T>> combo(List<T> elementos, Function<T, String> etiqueta) {
        DefaultComboBoxModel<Opcion<T>> modelo = new DefaultComboBoxModel<>();

        for (T elemento : elementos) {
            modelo.addElement(new Opcion<>(elemento, etiqueta.apply(elemento)));
        }

        return new JComboBox<>(modelo);
    }

    private <T> T seleccion(JComboBox<Opcion<T>> combo) {
        return combo.getItemAt(combo.getSelectedIndex()).valor;
    }

    private String valor(JTextField campo) {
        return campo.getText().trim();
    }

    private void resultado(boolean ok, String exito, String error) {
        if (ok) {
            mostrarExito(exito);
        } else {
            mostrarError(error);
        }
    }

    private void mostrarLista(String titulo, String contenido) {
        JTextArea area = new JTextArea(
                contenido == null || contenido.isBlank() ? "No hay información disponible." : contenido,
                22, 65);
        area.setEditable(false);
        area.setCaretPosition(0);

        JOptionPane.showMessageDialog(ventana, new JScrollPane(area), titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "Operación completada",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "No se pudo completar la operación",
                JOptionPane.ERROR_MESSAGE);
    }

    private static class Opcion<T> {
        private final T valor;
        private final String etiqueta;

        private Opcion(T valor, String etiqueta) {
            this.valor = valor;
            this.etiqueta = etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }
}
