package vistas;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Ventana principal de VetGo que permite seleccionar con qué vista trabajar
 *
 * Las vistas siguen funcionando mediante consola, pero se ejecutan en un hilo
 * independiente para evitar que la interfaz gráfica se congele
 *
 * Mientras un menú está abierto, los botones permanecen deshabilitados hasta
 * que se regresa a la ventana principal mediante la opción 0
 */
public class VistaPrincipal extends JFrame {
    private static final String MENSAJE_INICIAL = "Seleccione la vista con la que desea trabajar.";

    private final Scanner scanner;
    private final Runnable alSalir;

    // Referencias a los botones de las vistas para poder habilitarlos o deshabilitarlos conjuntamente
    private final List<JButton> botonesVistas = new ArrayList<>();

    private final JPanel panelBotones = new JPanel(new GridLayout(4, 1, 0, 10));
    private final JButton botonSalir = crearBoton("Salir");
    private final JLabel estado = new JLabel("", SwingConstants.CENTER);

    // Indica si hay un menú ejecutándose en la consola
    // Solo se modifica y consulta desde el hilo de Swing
    private boolean ocupado = false;

    public VistaPrincipal(VistaRecepcionista vistaRecepcionista,
                          VistaVeterinario vistaVeterinario,
                          VistaAdministrador vistaAdministrador,
                          Scanner scanner,
                          Runnable alSalir) {
        super("VetGo");
        this.scanner = scanner;
        this.alSalir = alSalir;

        armarVentana();

        // Asocia cada botón con el método que inicia el menú correspondiente
        agregarVista("Recepción", vistaRecepcionista::iniciarMenu);
        agregarVista("Veterinario", vistaVeterinario::iniciarMenu);
        agregarVista("Administración", vistaAdministrador::iniciarMenu);

        botonSalir.addActionListener(e -> alSalir.run());

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Configura la estructura y las propiedades de la ventana principal
     */
    private void armarVentana() {
        // Evita que la ventana se cierre automáticamente sin ejecutar las validaciones correspondientes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Controla el cierre de la ventana mediante el botón de cerrar del sistema
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ocupado) {
                    JOptionPane.showMessageDialog(VistaPrincipal.this,
                            "Hay un menú abierto en la consola.\nVuelva con la opción 0 antes de cerrar VetGo.",
                            "VetGo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    alSalir.run();
                }
            }
        });

        // Configuración del título y subtítulo
        JLabel titulo = new JLabel("VetGo", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 28f));

        JLabel subtitulo = new JLabel("Gestión de clínica veterinaria", SwingConstants.CENTER);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        // Panel principal que organiza el encabezado, los botones y el mensaje de estado
        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(panelBotones, BorderLayout.CENTER);
        contenido.add(estado, BorderLayout.SOUTH);

        setContentPane(contenido);
        mostrarEstado(MENSAJE_INICIAL);
    }

    /**
     * Crea un botón para acceder a una vista y lo agrega al panel principal
     * <p> El consumer representa una acción que puede ser ejecutada en un momento. Esta accion recibe un Scanner
     * @param nombre nombre que se mostrará en el botón
     * @param vista acción que inicia el menú de consola correspondiente
     */
    private void agregarVista(String nombre, Consumer<Scanner> vista) {
        JButton boton = crearBoton(nombre);
        // Cuando se presiona el boton se ejecuta abrir
        boton.addActionListener(e -> abrir(nombre, vista));
        botonesVistas.add(boton);

        panelBotones.add(boton);

        // Agrega el botón de salida después de incorporar las tres vistas
        if (botonesVistas.size() == 3) {
            panelBotones.add(botonSalir);
        }
    }

    /**
     * Ejecuta el menú de una vista en un hilo independiente
     * <p> El consumer representa una acción que puede ser ejecutada en un momento. Esta accion recibe un Scanner
     * @param nombre nombre de la vista seleccionada
     * @param vista acción que inicia el menú de consola
     */
    private void abrir(String nombre, Consumer<Scanner> vista) {
        ocupado = true;
        habilitarBotones(false);

        mostrarEstado("Menú de " + nombre.toLowerCase() + " abierto en la consola. "
                + "Use la opción 0 para volver aquí.");

        // Ejecuta el menú fuera del hilo de Swing para mantener la interfaz gráfica receptiva
        Thread hilo = new Thread(() -> {
            try {
                // Esto ejecuta la funcion que se pasó por parámetro
                vista.accept(scanner);
            } catch (RuntimeException e) {
                Entrada.mostrarError(e);
            } finally {
                System.out.println("\nVolviendo a la ventana principal.");

                // Programa la actualización de la interfaz en el hilo de eventos de Swing
                SwingUtilities.invokeLater(() -> {
                    ocupado = false;
                    habilitarBotones(true);
                    mostrarEstado(MENSAJE_INICIAL);
                });
            }
        }, "vista-consola");

        hilo.setDaemon(true);
        hilo.start();
    }

    /**
     * Habilita o deshabilita los botones de acceso a las vistas y el botón de salida
     *
     * @param habilitar indica si los botones deben quedar habilitados
     */
    private void habilitarBotones(boolean habilitar) {
        for (JButton boton : botonesVistas) {
            boton.setEnabled(habilitar);
        }
        botonSalir.setEnabled(habilitar);
    }

    /**
     * Actualiza el mensaje de estado mostrado en la parte inferior de la ventana
     *
     * @param mensaje texto que se desea mostrar
     */
    private void mostrarEstado(String mensaje) {
        estado.setText("<html><div style='text-align:center; width:260px'>" + mensaje + "</div></html>");
    }

    /**
     * Crea un botón con el formato visual utilizado en la ventana principal
     *
     * @param texto texto que se mostrará en el botón
     * @return botón configurado
     */
    private static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 16f));
        boton.setFocusPainted(false);
        return boton;
    }
}