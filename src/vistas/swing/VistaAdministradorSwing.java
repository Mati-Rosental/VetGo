package vistas.swing;

import controlador.ControladorAdministracion;
import modelo.agenda.Turno;
import modelo.enumeradores.Especie;
import modelo.inventario.Lote;
import modelo.inventario.Producto;
import modelo.inventario.Proveedor;
import modelo.inventario.Vacuna;
import modelo.personas.HorarioAtencion;
import modelo.personas.Recepcionista;
import modelo.personas.Veterinario;
import vistas.interfaces.VistaAdministrador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.concurrent.CountDownLatch;

public class VistaAdministradorSwing implements VistaAdministrador {
    private final ControladorAdministracion controlador;
    private JFrame ventana;

    public VistaAdministradorSwing(ControladorAdministracion controlador) {
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
            throw new RuntimeException("No se pudo abrir la vista de administración", e);
        }
    }

    private void abrirVentana(CountDownLatch cierre) {
        ventana = new JFrame("VetGo | Administración");
        ventana.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ventana.setMinimumSize(new Dimension(900, 620));
        ventana.setSize(1050, 700);
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

        JLabel titulo = new JLabel("Panel de administración");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 25f));

        JLabel subtitulo = new JLabel("Personal, inventario, proveedores y reportes de la clínica");
        subtitulo.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        contenido.add(encabezado, BorderLayout.NORTH);

        JTabbedPane pestanias = new JTabbedPane();

        pestanias.addTab("Inicio", crearInicio());

        pestanias.addTab("Personal", crearPanelAcciones(
                new String[]{
                        "Registrar veterinario",
                        "Modificar veterinario",
                        "Dar de baja veterinario",
                        "Ver veterinarios",
                        "Registrar recepcionista",
                        "Modificar recepcionista",
                        "Dar de baja recepcionista",
                        "Ver recepcionistas"
                },
                new Runnable[]{
                        this::altaVeterinario,
                        this::modificarVeterinario,
                        this::bajaVeterinario,
                        this::verVeterinarios,
                        this::altaRecepcionista,
                        this::modificarRecepcionista,
                        this::bajaRecepcionista,
                        this::verRecepcionistas
                }
        ));

        pestanias.addTab("Inventario", crearPanelAcciones(
                new String[]{
                        "Registrar producto",
                        "Modificar producto",
                        "Dar de baja producto",
                        "Ver productos",
                        "Registrar vacuna",
                        "Modificar vacuna",
                        "Dar de baja vacuna",
                        "Ver vacunas",
                        "Agregar lotes"
                },
                new Runnable[]{
                        this::altaProducto,
                        this::modificarProducto,
                        this::bajaProducto,
                        this::verProductos,
                        this::altaVacuna,
                        this::modificarVacuna,
                        this::bajaVacuna,
                        this::verVacunas,
                        this::agregarLotes
                }
        ));

        pestanias.addTab("Proveedores", crearPanelAcciones(
                new String[]{
                        "Registrar proveedor",
                        "Modificar proveedor",
                        "Dar de baja proveedor",
                        "Ver proveedores"
                },
                new Runnable[]{
                        this::altaProveedor,
                        this::modificarProveedor,
                        this::bajaProveedor,
                        this::verProveedores
                }
        ));

        pestanias.addTab("Reportes", crearPanelAcciones(
                new String[]{
                        "Turnos por veterinario",
                        "Productos con stock bajo",
                        "Lotes próximos a vencer",
                        "Emitir recordatorios"
                },
                new Runnable[]{
                        this::reporteTurnos,
                        this::reporteStock,
                        this::reporteVencimientos,
                        this::emitirRecordatorios
                }
        ));

        contenido.add(pestanias, BorderLayout.CENTER);

        return contenido;
    }

    private JPanel crearInicio() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(new EmptyBorder(20, 10, 10, 10));

        JTextArea ayuda = new JTextArea(
                "Seleccione una pestaña para comenzar.\n\n"
                        + "• Personal: altas, modificaciones y bajas de veterinarios y recepcionistas\n"
                        + "• Inventario: administrar productos, vacunas y sus lotes\n"
                        + "• Proveedores: registrar, modificar y consultar proveedores\n"
                        + "• Reportes: consultar turnos, stock bajo, vencimientos y recordatorios"
        );

        ayuda.setEditable(false);
        ayuda.setOpaque(false);
        ayuda.setFont(ayuda.getFont().deriveFont(15f));

        panel.add(ayuda, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelAcciones(String[] nombres, Runnable[] acciones) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 90, 5, 90);

        for (int i = 0; i < nombres.length; i++) {
            JButton boton = new JButton(nombres[i]);
            boton.setFont(boton.getFont().deriveFont(14f));
            boton.setPreferredSize(new Dimension(350, 38));

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
            error(mensaje(e));
        }
    }

    // Personal

    private void altaVeterinario() {
        String[] v = pedir("Registrar veterinario",
                "Nombre",
                "Apellido",
                "Documento",
                "Teléfono",
                "Matrícula",
                "Especialidad");

        if (v == null) return;

        List<HorarioAtencion> horarios = seleccionarHorarios();

        if (horarios == null) return;

        resultado(controlador.altaVeterinario(
                        v[0], v[1], v[2], v[3], v[4], v[5], horarios),
                "Veterinario registrado correctamente.",
                "No se pudo registrar. Verifique documento, matrícula y horarios.");
    }

    private void modificarVeterinario() {
        Veterinario seleccionado = elegir(
                controlador.obtenerVeterinarios(),
                v -> v.getNombreCompleto() + " — " + v.getDocumento(),
                "Veterinario"
        );

        if (seleccionado == null) return;

        String[] v = pedir("Modificar veterinario",
                "Nombre",
                "Apellido",
                "Teléfono",
                "Especialidad");

        if (v == null) return;

        List<HorarioAtencion> horarios = seleccionarHorarios();

        if (horarios == null) return;

        resultado(controlador.modificarVeterinario(
                        seleccionado.getDocumento(),
                        nullSiVacio(v[0]), nullSiVacio(v[1]), nullSiVacio(v[2]), nullSiVacio(v[3]), horarios),
                "Veterinario modificado correctamente.",
                "No se pudo modificar el veterinario. Revise los horarios.");
    }

    private void bajaVeterinario() {
        Veterinario v = elegir(
                controlador.obtenerVeterinarios(),
                x -> x.getNombreCompleto() + " — " + x.getDocumento(),
                "Veterinario"
        );

        if (v != null && confirmar("¿Dar de baja a " + v.getNombreCompleto() + "?")) {
            resultado(controlador.bajaVeterinario(v.getDocumento()),
                    "Veterinario dado de baja.",
                    "No se pudo dar de baja. Puede tener turnos futuros asignados.");
        }
    }

    private void verVeterinarios() {
        StringBuilder s = new StringBuilder();

        for (Veterinario v : controlador.obtenerVeterinarios()) {
            s.append(v.getNombreCompleto())
                    .append(" | DNI: ").append(v.getDocumento())
                    .append(" | Matrícula: ").append(v.getMatricula())
                    .append(" | Especialidad: ").append(v.getEspecialidad())
                    .append('\n');
        }

        lista("Veterinarios", s.toString());
    }

    private void altaRecepcionista() {
        String[] v = pedir("Registrar recepcionista",
                "Nombre",
                "Apellido",
                "Documento",
                "Teléfono");

        if (v != null) {
            resultado(controlador.altaRecepcionista(v[0], v[1], v[2], v[3]),
                    "Recepcionista registrada correctamente.",
                    "No se pudo registrar. Verifique que el documento no esté duplicado.");
        }
    }

    private void modificarRecepcionista() {
        Recepcionista r = elegir(
                controlador.obtenerRecepcionistas(),
                x -> x.getNombreCompleto() + " — " + x.getDocumento(),
                "Recepcionista"
        );

        if (r == null) return;

        String[] v = pedir("Modificar recepcionista",
                "Nombre",
                "Apellido",
                "Teléfono");

        if (v != null) {
            resultado(controlador.modificarRecepcionista(r.getDocumento(), v[0], v[1], v[2]),
                    "Recepcionista modificada correctamente.",
                    "No se pudo modificar la recepcionista.");
        }
    }

    private void bajaRecepcionista() {
        Recepcionista r = elegir(
                controlador.obtenerRecepcionistas(),
                x -> x.getNombreCompleto() + " — " + x.getDocumento(),
                "Recepcionista"
        );

        if (r != null && confirmar("¿Dar de baja a " + r.getNombreCompleto() + "?")) {
            resultado(controlador.bajaRecepcionista(r.getDocumento()),
                    "Recepcionista dada de baja.",
                    "No se pudo dar de baja.");
        }
    }

    private void verRecepcionistas() {
        StringBuilder s = new StringBuilder();

        for (Recepcionista r : controlador.obtenerRecepcionistas()) {
            s.append(r.getNombreCompleto())
                    .append(" | DNI: ")
                    .append(r.getDocumento())
                    .append('\n');
        }

        lista("Recepcionistas", s.toString());
    }

    // Inventario

    private void altaProducto() {
        String[] v = pedir("Registrar producto",
                "Código",
                "Nombre",
                "Stock mínimo",
                "Precio unitario",
                "CUIT proveedor",
                "Lotes: número,cantidad,AAAA-MM-DD; (dejar vacío si no hay)");

        if (v == null) return;

        resultado(controlador.altaProducto(
                        v[0],
                        v[1],
                        entero(v[2]),
                        decimal(v[3]),
                        parsearLotes(v[5]),
                        v[4]),
                "Producto registrado correctamente.",
                "No se pudo registrar. Verifique código, proveedor, precio y lotes.");
    }

    private void modificarProducto() {
        Producto p = elegir(
                controlador.obtenerProductos().stream()
                        .filter(x -> !(x instanceof Vacuna))
                        .collect(Collectors.toList()),
                x -> x.getNombre() + " — " + x.getCodigo(),
                "Producto"
        );

        if (p == null) return;

        String[] v = pedir("Modificar producto",
                "Nombre",
                "Stock mínimo",
                "Precio unitario",
                "CUIT proveedor",
                "Nuevos lotes (dejar vacío para conservar)");

        if (v == null) return;

        List<Lote> lotes = v[4].isBlank() ? null : parsearLotes(v[4]);

        resultado(controlador.modificarProducto(
                        p.getCodigo(),
                        v[0],
                        entero(v[1]),
                        decimal(v[2]),
                        lotes,
                        v[3]),
                "Producto modificado correctamente.",
                "No se pudo modificar el producto.");
    }

    private void bajaProducto() {
        Producto p = elegir(
                controlador.obtenerProductos().stream()
                        .filter(x -> !(x instanceof Vacuna))
                        .collect(Collectors.toList()),
                x -> x.getNombre() + " — " + x.getCodigo(),
                "Producto"
        );

        if (p != null && confirmar("¿Dar de baja el producto " + p.getNombre() + "?")) {
            resultado(controlador.bajaProducto(p.getCodigo()),
                    "Producto dado de baja.",
                    "No se pudo dar de baja.");
        }
    }

    private void verProductos() {
        StringBuilder s = new StringBuilder();

        for (Producto p : controlador.obtenerProductos()) {
            if (!(p instanceof Vacuna)) {
                s.append(p.getDescripcion())
                        .append(" | Mínimo: ").append(p.getStockMinimo())
                        .append(" | Precio: ").append(p.getPrecioUnitario())
                        .append('\n');
            }
        }

        lista("Productos", s.toString());
    }

    private void altaVacuna() {
        String[] v = pedir("Registrar vacuna",
                "Código",
                "Nombre",
                "Especies (PERRO,GATO,AVE,CONEJO)",
                "Edad mínima en semanas",
                "Intervalo refuerzo en días",
                "Precio unitario",
                "Stock mínimo",
                "CUIT proveedor",
                "Lotes: número,cantidad,AAAA-MM-DD; (dejar vacío si no hay)");

        if (v == null) return;

        resultado(controlador.altaVacuna(
                        v[0],
                        v[1],
                        parsearEspecies(v[2]),
                        entero(v[3]),
                        entero(v[4]),
                        decimal(v[5]),
                        entero(v[6]),
                        v[7],
                        parsearLotes(v[8])),
                "Vacuna registrada correctamente.",
                "No se pudo registrar. Verifique los datos, proveedor, especies y lotes.");
    }

    private void modificarVacuna() {
        Vacuna vacuna = elegir(
                controlador.obtenerVacunas(),
                v -> v.getNombre() + " — " + v.getCodigo(),
                "Vacuna"
        );

        if (vacuna == null) return;

        String[] v = pedir("Modificar vacuna",
                "Nombre",
                "Especies (PERRO,GATO,AVE,CONEJO)",
                "Edad mínima en semanas",
                "Intervalo refuerzo en días",
                "Precio unitario",
                "Nuevos lotes (dejar vacío para conservar)");

        if (v == null) return;

        List<Lote> lotes = v[5].isBlank() ? null : parsearLotes(v[5]);

        resultado(controlador.modificarVacuna(
                        vacuna.getCodigo(),
                        v[0],
                        parsearEspecies(v[1]),
                        entero(v[2]),
                        entero(v[3]),
                        decimal(v[4]),
                        lotes),
                "Vacuna modificada correctamente.",
                "No se pudo modificar la vacuna.");
    }

    private void bajaVacuna() {
        Vacuna v = elegir(
                controlador.obtenerVacunas(),
                x -> x.getNombre() + " — " + x.getCodigo(),
                "Vacuna"
        );

        if (v != null && confirmar("¿Dar de baja la vacuna " + v.getNombre() + "?")) {
            resultado(controlador.bajaVacuna(v.getCodigo()),
                    "Vacuna dada de baja.",
                    "No se pudo dar de baja.");
        }
    }

    private void verVacunas() {
        StringBuilder s = new StringBuilder();

        for (Vacuna v : controlador.obtenerVacunas()) {
            s.append(v.getDescripcion())
                    .append(" | Stock: ").append(v.getStockActual())
                    .append(" | Precio: ").append(v.getPrecioUnitario())
                    .append('\n');
        }

        lista("Vacunas", s.toString());
    }

    private void agregarLotes() {
        Producto producto = elegir(
                controlador.obtenerProductos(),
                p -> p.getNombre() + " — Código " + p.getCodigo(),
                "Producto o vacuna"
        );

        if (producto == null) return;

        String[] v = pedir("Agregar lotes",
                "Lotes: número,cantidad,AAAA-MM-DD; (separar con punto y coma)");

        if (v == null) return;

        resultado(controlador.agregarLotesProducto(producto.getCodigo(), parsearLotes(v[0])),
                "Los lotes fueron agregados correctamente.",
                "No se pudieron agregar los lotes. Revise cantidades y vencimientos.");
    }

    // Proveedores

    private void altaProveedor() {
        String[] v = pedir("Registrar proveedor",
                "CUIT",
                "Nombre",
                "Teléfono",
                "Correo");

        if (v != null) {
            resultado(controlador.altaProveedor(v[0], v[1], v[2], v[3]),
                    "Proveedor registrado correctamente.",
                    "No se pudo registrar el proveedor.");
        }
    }

    private void modificarProveedor() {
        Proveedor p = elegir(
                controlador.obtenerProveedores(),
                x -> x.getNombre() + " — CUIT " + x.getCUIT(),
                "Proveedor"
        );

        if (p == null) return;

        String[] v = pedir("Modificar proveedor",
                "Nombre",
                "Teléfono",
                "Correo");

        if (v != null) {
            resultado(controlador.modificarProveedor(p.getCUIT(), v[0], v[1], v[2]),
                    "Proveedor modificado correctamente.",
                    "No se pudo modificar el proveedor.");
        }
    }

    private void bajaProveedor() {
        Proveedor p = elegir(
                controlador.obtenerProveedores(),
                x -> x.getNombre() + " — CUIT " + x.getCUIT(),
                "Proveedor"
        );

        if (p != null && confirmar("¿Dar de baja al proveedor " + p.getNombre() + "?")) {
            resultado(controlador.bajaProveedor(p.getCUIT()),
                    "Proveedor dado de baja.",
                    "No se pudo dar de baja. Puede tener productos asociados.");
        }
    }

    private void verProveedores() {
        StringBuilder s = new StringBuilder();

        for (Proveedor p : controlador.obtenerProveedores()) {
            s.append(p.getNombre())
                    .append(" | CUIT: ").append(p.getCUIT())
                    .append(" | Teléfono: ").append(p.getTelefono())
                    .append(" | Correo: ").append(p.getCorreo())
                    .append('\n');
        }

        lista("Proveedores", s.toString());
    }

    // Reportes

    private void reporteTurnos() {
        Veterinario v = elegir(
                controlador.obtenerVeterinarios(),
                x -> x.getNombreCompleto() + " — " + x.getDocumento(),
                "Veterinario"
        );

        if (v == null) return;

        String[] fechas = pedir("Reporte de turnos",
                "Desde (AAAA-MM-DD)",
                "Hasta (AAAA-MM-DD)");

        if (fechas == null) return;

        List<Turno> turnos = controlador.emitirReporteTurnos(
                v.getDocumento(),
                LocalDate.parse(fechas[0]),
                LocalDate.parse(fechas[1])
        );

        StringBuilder s = new StringBuilder();

        if (turnos != null) {
            for (Turno t : turnos) {
                s.append(t.getDescripcion()).append('\n');
            }
        }

        lista("Turnos por veterinario", s.toString());
    }

    private void reporteStock() {
        StringBuilder s = new StringBuilder();

        for (Producto p : controlador.emitirReporteStockBajo()) {
            s.append(p.getDescripcion()).append('\n');
        }

        lista("Productos con stock bajo", s.toString());
    }

    private void reporteVencimientos() {
        String[] v = pedir("Lotes próximos a vencer",
                "Días de anticipación");

        if (v == null) return;

        StringBuilder s = new StringBuilder();

        List<Lote> lotes = controlador.emitirReporteLotesPorVencer(
                LocalDate.now(),
                entero(v[0])
        );

        if (lotes != null) {
            for (Lote lote : lotes) {
                s.append(lote.getDescripcion()).append('\n');
            }
        }

        lista("Lotes próximos a vencer", s.toString());
    }

    private void emitirRecordatorios() {
        if (confirmar("¿Emitir los recordatorios correspondientes a la fecha de hoy?")) {
            resultado(controlador.emitirRecordatorios(LocalDate.now()),
                    "Recordatorios emitidos correctamente.",
                    "No se pudieron emitir los recordatorios.");
        }
    }

    // Editor semanal de horarios

    private List<HorarioAtencion> seleccionarHorarios() {
        String[] dias = {
                "Lunes",
                "Martes",
                "Miércoles",
                "Jueves",
                "Viernes",
                "Sábado",
                "Domingo"
        };

        DayOfWeek[] diasSemana = DayOfWeek.values();

        List<String> horas = new ArrayList<>();

        // Genera horarios disponibles cada 30 minutos
        for (int minuto = 0; minuto < 24 * 60; minuto += 30) {
            horas.add(String.format("%02d:%02d", minuto / 60, minuto % 60));
        }

        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 8));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        panel.add(new JLabel("Día"));
        panel.add(new JLabel("Desde"));
        panel.add(new JLabel("Hasta"));
        panel.add(new JLabel("Guardia"));

        List<JCheckBox> diasSeleccionados = new ArrayList<>();
        List<JComboBox<String>> horasInicio = new ArrayList<>();
        List<JComboBox<String>> horasFin = new ArrayList<>();
        List<JCheckBox> guardias = new ArrayList<>();

        for (int i = 0; i < dias.length; i++) {
            JCheckBox dia = new JCheckBox(dias[i]);

            JComboBox<String> inicio = new JComboBox<>(horas.toArray(new String[0]));
            JComboBox<String> fin = new JComboBox<>(horas.toArray(new String[0]));
            JCheckBox guardia = new JCheckBox();

            inicio.setSelectedItem("09:00");
            fin.setSelectedItem("17:00");

            inicio.setEnabled(false);
            fin.setEnabled(false);
            guardia.setEnabled(false);

            dia.addActionListener(e -> {
                boolean seleccionado = dia.isSelected();

                inicio.setEnabled(seleccionado);
                fin.setEnabled(seleccionado);
                guardia.setEnabled(seleccionado);
            });

            panel.add(dia);
            panel.add(inicio);
            panel.add(fin);
            panel.add(guardia);

            diasSeleccionados.add(dia);
            horasInicio.add(inicio);
            horasFin.add(fin);
            guardias.add(guardia);
        }

        int opcion = JOptionPane.showConfirmDialog(
                ventana,
                panel,
                "Configurar horarios de atención",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) return null;

        List<HorarioAtencion> horarios = new ArrayList<>();

        for (int i = 0; i < dias.length; i++) {
            if (!diasSeleccionados.get(i).isSelected()) continue;

            LocalTime inicio = LocalTime.parse(
                    horasInicio.get(i).getSelectedItem().toString()
            );

            LocalTime fin = LocalTime.parse(
                    horasFin.get(i).getSelectedItem().toString()
            );

            if (!fin.isAfter(inicio)) {
                error("La hora de fin debe ser posterior a la hora de inicio para " + dias[i] + ".");
                return null;
            }

            horarios.add(new HorarioAtencion(
                    diasSemana[i],
                    inicio,
                    fin,
                    guardias.get(i).isSelected()
            ));
        }

        if (horarios.isEmpty()) {
            error("Debe seleccionar al menos un día de atención.");
            return null;
        }

        return horarios;
    }

    // Utilidades de formularios y conversión

    // Un campo vacío se envía como null (el modelo lo interpreta como "no modificar")
    private String nullSiVacio(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto;
    }

    private String[] pedir(String titulo, String... etiquetas) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField[] campos = new JTextField[etiquetas.length];

        for (int i = 0; i < etiquetas.length; i++) {
            panel.add(new JLabel(etiquetas[i]));

            campos[i] = new JTextField(24);
            panel.add(campos[i]);
        }

        int opcion = JOptionPane.showConfirmDialog(
                ventana,
                panel,
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) return null;

        String[] valores = new String[campos.length];

        for (int i = 0; i < campos.length; i++) {
            valores[i] = campos[i].getText().trim();
        }

        return valores;
    }

    private <T> T elegir(List<T> elementos, java.util.function.Function<T, String> etiqueta, String tipo) {
        if (elementos == null || elementos.isEmpty()) {
            error("No hay registros de " + tipo.toLowerCase() + ".");
            return null;
        }

        DefaultComboBoxModel<Opcion<T>> modelo = new DefaultComboBoxModel<>();

        for (T elemento : elementos) {
            modelo.addElement(new Opcion<>(elemento, etiqueta.apply(elemento)));
        }

        JComboBox<Opcion<T>> combo = new JComboBox<>(modelo);

        int opcion = JOptionPane.showConfirmDialog(
                ventana,
                combo,
                "Seleccionar " + tipo.toLowerCase(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        return opcion == JOptionPane.OK_OPTION
                ? combo.getItemAt(combo.getSelectedIndex()).valor
                : null;
    }

    private List<Lote> parsearLotes(String texto) {
        List<Lote> lotes = new ArrayList<>();

        if (texto == null || texto.isBlank()) return lotes;

        for (String bloque : texto.split(";")) {
            String[] partes = bloque.trim().split(",");

            if (partes.length != 3) {
                throw new IllegalArgumentException(
                        "Formato de lote inválido. Use número,cantidad,AAAA-MM-DD."
                );
            }

            lotes.add(new Lote(
                    partes[0].trim(),
                    Integer.parseInt(partes[1].trim()),
                    LocalDate.parse(partes[2].trim())
            ));
        }

        return lotes;
    }

    private Set<Especie> parsearEspecies(String texto) {
        Set<Especie> especies = new HashSet<>();

        for (String especie : texto.split(",")) {
            especies.add(Especie.valueOf(especie.trim().toUpperCase()));
        }

        return especies;
    }

    private int entero(String texto) {
        return Integer.parseInt(texto.trim());
    }

    private double decimal(String texto) {
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }

    private boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(
                ventana,
                mensaje,
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    private void resultado(boolean ok, String exito, String fallo) {
        if (ok) {
            exito(exito);
        } else {
            error(fallo);
        }
    }

    private void exito(String mensaje) {
        JOptionPane.showMessageDialog(
                ventana,
                mensaje,
                "Operación completada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(
                ventana,
                mensaje,
                "No se pudo completar la operación",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String mensaje(Exception e) {
        return e.getMessage() == null
                ? e.getClass().getSimpleName()
                : e.getMessage();
    }

    private void lista(String titulo, String contenido) {
        if (contenido == null || contenido.isBlank()) {
            contenido = "No hay registros para mostrar.";
        }

        JTextArea area = new JTextArea(contenido, 22, 70);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JOptionPane.showMessageDialog(
                ventana,
                new JScrollPane(area),
                titulo,
                JOptionPane.INFORMATION_MESSAGE
        );
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