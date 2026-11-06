package vistas.swing;

import controlador.ControladorFacturacion;
import controlador.ControladorRegistro;
import controlador.ControladorTurnos;
import modelo.agenda.Turno;
import modelo.atencion.Consulta;
import modelo.enumeradores.Especie;
import modelo.enumeradores.Sexo;
import modelo.enumeradores.TamanioPerro;
import modelo.enumeradores.TipoAtencion;
import modelo.facturacion.pago.Credito;
import modelo.facturacion.pago.Debito;
import modelo.facturacion.pago.Efectivo;
import modelo.facturacion.pago.MedioPago;
import modelo.mascotas.Mascota;
import modelo.personas.Duenio;
import modelo.personas.Veterinario;
import vistas.interfaces.VistaRecepcionista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class VistaRecepcionistaSwing implements VistaRecepcionista{
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    protected final ControladorRegistro controladorRegistro;
    protected final ControladorTurnos controladorTurnos;
    protected final ControladorFacturacion controladorFacturacion;
    private JFrame ventana;

    public VistaRecepcionistaSwing(ControladorRegistro controladorRegistro, ControladorTurnos controladorTurnos, ControladorFacturacion controladorFacturacion) {
        this.controladorRegistro = controladorRegistro;
        this.controladorTurnos = controladorTurnos;
        this.controladorFacturacion = controladorFacturacion;
    }

    // Mantiene la integración con VistaPrincipal, que ejecuta las vistas en un hilo separado
    public void iniciarMenu(java.util.Scanner scanner) {
        CountDownLatch cierre = new CountDownLatch(1);

        try {
            SwingUtilities.invokeAndWait(() -> abrirVentana(cierre));
            cierre.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo abrir la vista de recepción", e);
        }
    }

    private void abrirVentana(CountDownLatch cierre) {
        ventana = new JFrame("VetGo | Recepción");
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

        JLabel titulo = new JLabel("Panel de recepción");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 25f));
        JLabel subtitulo = new JLabel("Gestión de dueños, mascotas, turnos y facturación");
        subtitulo.setForeground(Color.GRAY);

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        contenido.add(encabezado, BorderLayout.NORTH);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Inicio", crearInicio());
        pestanias.addTab("Dueños y mascotas", crearPanelAcciones(
                new String[]{"Registrar dueño", "Registrar mascota", "Transferir mascota", "Ver dueños", "Ver mascotas"},
                new Runnable[]{this::registrarDuenio, this::registrarMascota, this::transferirMascota,
                        this::verDuenios, this::verMascotas}));
        pestanias.addTab("Turnos", crearPanelAcciones(
                new String[]{"Asignar turno", "Reprogramar turno", "Cancelar turno", "Registrar ausencia", "Ver todos los turnos", "Turnos de un dueño"},
                new Runnable[]{this::asignarTurno, this::reprogramarTurno, this::cancelarTurno,
                        this::registrarAusencia, this::verTurnos, this::verTurnosDuenio}));
        pestanias.addTab("Urgencias", crearPanelAcciones(
                new String[]{"Atender urgencia", "Ver mascotas"},
                new Runnable[]{this::atenderUrgencia, this::verMascotas}));
        pestanias.addTab("Facturación", crearPanelAcciones(
                new String[]{"Facturar consulta", "Anular factura", "Consultas pendientes", "Cobrar seña"},
                new Runnable[]{this::facturarConsulta, this::anularFactura, this::verConsultasPendientes, this::cobrarSenia}));

        contenido.add(pestanias, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearInicio() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(new EmptyBorder(20, 10, 10, 10));

        JTextArea ayuda = new JTextArea("Seleccione una pestaña para comenzar.\n\n"
                + "• Dueños y mascotas: altas y transferencias\n"
                + "• Turnos: asignación, reprogramación y ausencias\n"
                + "• Urgencias: atención inmediata con veterinario de guardia\n"
                + "• Facturación: cobros y anulación de facturas");
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
        c.insets = new Insets(7, 100, 7, 100);

        for (int i = 0; i < nombres.length; i++) {
            JButton boton = new JButton(nombres[i]);
            boton.setFont(boton.getFont().deriveFont(15f));
            boton.setPreferredSize(new Dimension(320, 42));
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
            mostrarError("Ocurrió un error al realizar la operación:\n" + mensajeError(e));
        }
    }

    private void registrarDuenio() {
        JTextField nombre = campo("Nombre");
        JTextField apellido = campo("Apellido");
        JTextField documento = campo("Documento");
        JTextField telefono = campo("Teléfono");
        JTextField correo = campo("Correo");
        JCheckBox plan = new JCheckBox("Tiene plan de salud");
        JPanel formulario = formulario(
                new JLabel("Nombre"), nombre,
                new JLabel("Apellido"), apellido,
                new JLabel("Documento"), documento,
                new JLabel("Teléfono"), telefono,
                new JLabel("Correo"), correo,
                new JLabel("Plan de salud"), plan);

        if (!confirmarFormulario("Registrar dueño", formulario)) return;
        if (controladorRegistro.altaDuenio(valor(nombre), valor(apellido), valor(documento),
                valor(telefono), valor(correo), plan.isSelected())) {
            mostrarExito("El dueño fue registrado correctamente.");
        } else {
            mostrarError("No se pudo registrar el dueño. Es posible que ya exista un documento igual.");
        }
    }

    private void registrarMascota() {
        List<Duenio> duenios = controladorRegistro.obtenerDuenios();
        if (duenios.isEmpty()) {
            mostrarError("Primero debe registrar al menos un dueño.");
            return;
        }

        JComboBox<Opcion<Duenio>> duenio = combo(duenios, d -> d.getNombreCompleto() + " — DNI " + d.getDocumento());
        JComboBox<Especie> especie = new JComboBox<>(Especie.values());
        JTextField nombre = campo("Nombre");
        JTextField raza = campo("Raza");
        JComboBox<Sexo> sexo = new JComboBox<>(Sexo.values());
        JTextField nacimiento = campo("AAAA-MM-DD");
        JTextField peso = campo("Peso en kg");
        JTextField chip = campo("Número de chip (solo perros)");
        JComboBox<TamanioPerro> tamanio = new JComboBox<>(TamanioPerro.values());
        JCheckBox interior = new JCheckBox("Vive en interior (gatos)");
        JCheckBox vuela = new JCheckBox("Puede volar (aves)");

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        agregarCampo(formulario, "Dueño", duenio);
        agregarCampo(formulario, "Especie", especie);
        agregarCampo(formulario, "Nombre", nombre);
        agregarCampo(formulario, "Raza", raza);
        agregarCampo(formulario, "Sexo", sexo);
        agregarCampo(formulario, "Nacimiento", nacimiento);
        agregarCampo(formulario, "Peso", peso);
        agregarCampo(formulario, "Chip", chip);
        agregarCampo(formulario, "Tamaño de perro", tamanio);
        formulario.add(new JLabel("Otras características"));
        JPanel extras = new JPanel(new GridLayout(2, 1));
        extras.add(interior);
        extras.add(vuela);
        formulario.add(extras);

        if (!confirmarFormulario("Registrar mascota", formulario)) return;
        try {
            LocalDate fecha = LocalDate.parse(valor(nacimiento));
            double pesoActual = Double.parseDouble(valor(peso).replace(',', '.'));
            if (pesoActual <= 0) throw new IllegalArgumentException("El peso debe ser mayor a cero.");
            Especie seleccionada = (Especie) especie.getSelectedItem();
            boolean registrada = controladorRegistro.altaMascota(
                    duenio.getItemAt(duenio.getSelectedIndex()).valor.getDocumento(),
                    seleccionada, valor(nombre), valor(raza), (Sexo) sexo.getSelectedItem(), fecha, pesoActual,
                    valor(chip), (TamanioPerro) tamanio.getSelectedItem(), interior.isSelected(), vuela.isSelected());
            if (registrada) mostrarExito("La mascota fue registrada correctamente.");
            else mostrarError("No se pudo registrar la mascota. Verifique los datos del dueño.");
        } catch (DateTimeParseException e) {
            mostrarError("La fecha debe tener el formato AAAA-MM-DD.");
        } catch (NumberFormatException e) {
            mostrarError("Ingrese un peso válido, por ejemplo 4.5.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void transferirMascota() {
        List<Mascota> mascotas = controladorRegistro.obtenerMascotas();
        List<Duenio> duenios = controladorRegistro.obtenerDuenios();
        if (mascotas.isEmpty() || duenios.size() < 2) {
            mostrarError("Se necesita al menos una mascota y dos dueños registrados.");
            return;
        }
        JComboBox<Opcion<Mascota>> mascota = combo(mascotas, m -> m.getNombre() + " (" + m.getEspecie() + ") — " + m.getDuenio().getNombreCompleto());
        JComboBox<Opcion<Duenio>> duenio = combo(duenios, d -> d.getNombreCompleto() + " — DNI " + d.getDocumento());
        JPanel formulario = formulario(new JLabel("Mascota"), mascota, new JLabel("Nuevo dueño"), duenio);
        if (!confirmarFormulario("Transferir mascota", formulario)) return;
        Mascota seleccionada = mascota.getItemAt(mascota.getSelectedIndex()).valor;
        Duenio nuevoDuenio = duenio.getItemAt(duenio.getSelectedIndex()).valor;
        if (seleccionada.getDuenio().equals(nuevoDuenio)) {
            mostrarError("La mascota ya pertenece a ese dueño.");
            return;
        }
        if (controladorRegistro.transferirMascota(seleccionada.getId(), nuevoDuenio.getDocumento())) {
            mostrarExito("La mascota fue transferida correctamente.");
        } else mostrarError("No se pudo transferir la mascota.");
    }

    private void asignarTurno() {
        List<Mascota> mascotas = controladorRegistro.obtenerMascotas();
        List<Veterinario> veterinarios = controladorTurnos.obtenerVeterinarios();
        if (mascotas.isEmpty() || veterinarios.isEmpty()) {
            mostrarError("Se necesitan mascotas y veterinarios registrados para asignar un turno.");
            return;
        }
        JComboBox<Opcion<Mascota>> mascota = combo(mascotas, m -> m.getNombre() + " — " + m.getDuenio().getNombreCompleto());
        JComboBox<Opcion<Veterinario>> veterinario = combo(veterinarios,
                v -> v.getNombreCompleto() + " — Matrícula " + v.getMatricula() + " — " + v.getEspecialidad());
        JComboBox<TipoAtencion> tipo = new JComboBox<>(new TipoAtencion[]{TipoAtencion.CONSULTA_GENERAL, TipoAtencion.VACUNACION, TipoAtencion.CIRUGIA});
        JTextField fecha = campo("AAAA-MM-DD HH:MM");
        JPanel formulario = formulario(new JLabel("Mascota"), mascota, new JLabel("Veterinario"), veterinario,
                new JLabel("Tipo de atención"), tipo, new JLabel("Fecha y hora"), fecha);
        if (!confirmarFormulario("Asignar turno", formulario)) return;
        try {
            LocalDateTime fechaHora = LocalDateTime.parse(valor(fecha), FORMATO_FECHA_HORA);
            boolean asignado = controladorTurnos.asignarTurno(
                    mascota.getItemAt(mascota.getSelectedIndex()).valor.getId(),
                    veterinario.getItemAt(veterinario.getSelectedIndex()).valor.getDocumento(),
                    (TipoAtencion) tipo.getSelectedItem(), fechaHora);
            if (asignado) mostrarExito("El turno fue asignado correctamente.");
            else mostrarError("No se pudo asignar el turno. Verifique la disponibilidad del veterinario, los datos y que el dueño no tenga una seña pendiente.");
        } catch (DateTimeParseException e) {
            mostrarError("La fecha debe tener el formato AAAA-MM-DD HH:MM.");
        }
    }

    private void reprogramarTurno() {
        List<Turno> turnos = turnosActivos();
        if (turnos.isEmpty()) { mostrarError("No hay turnos programados para reprogramar."); return; }
        JComboBox<Opcion<Turno>> turno = combo(turnos, this::descripcionTurno);
        JTextField fecha = campo("AAAA-MM-DD HH:MM");
        if (!confirmarFormulario("Reprogramar turno", formulario(new JLabel("Turno"), turno, new JLabel("Nueva fecha y hora"), fecha))) return;
        try {
            LocalDateTime nuevaFecha = LocalDateTime.parse(valor(fecha), FORMATO_FECHA_HORA);
            boolean ok = controladorTurnos.reprogramarTurno(turno.getItemAt(turno.getSelectedIndex()).valor.getId(), nuevaFecha);
            if (ok) mostrarExito("El turno fue reprogramado correctamente.");
            else mostrarError("No se pudo reprogramar. Compruebe que el horario esté disponible.");
        } catch (DateTimeParseException e) { mostrarError("La fecha debe tener el formato AAAA-MM-DD HH:MM."); }
    }

    private void cancelarTurno() {
        seleccionarTurno("Cancelar turno", "No hay turnos activos para cancelar.", t -> {
            if (confirmar("¿Está seguro de que desea cancelar este turno?")) {
                if (controladorTurnos.cancelarTurno(t.getId())) mostrarExito("Turno cancelado correctamente.");
                else mostrarError("No se pudo cancelar el turno.");
            }
        });
    }

    private void registrarAusencia() {
        seleccionarTurno("Registrar ausencia", "No hay turnos activos para registrar como ausentes.", t -> {
            if (confirmar("¿Registrar la ausencia para " + t.getMascota().getNombre() + "?")) {
                if (controladorTurnos.registrarAusencia(t.getId())) mostrarExito("Ausencia registrada correctamente.");
                else mostrarError("No se pudo registrar la ausencia.");
            }
        });
    }

    private void atenderUrgencia() {
        List<Mascota> mascotas = controladorRegistro.obtenerMascotas();
        if (mascotas.isEmpty()) { mostrarError("No hay mascotas registradas."); return; }
        JComboBox<Opcion<Mascota>> mascota = combo(mascotas, m -> m.getNombre() + " — " + m.getDuenio().getNombreCompleto());
        if (!confirmarFormulario("Atender urgencia", formulario(new JLabel("Mascota"), mascota))) return;
        Turno turno = controladorTurnos.atenderUrgencia(mascota.getItemAt(mascota.getSelectedIndex()).valor.getId());
        if (turno == null) mostrarError("No se pudo asignar la urgencia. Verifique que haya un veterinario de guardia.");
        else mostrarExito("Turno de urgencia asignado:\n" + descripcionTurno(turno));
    }

    private void facturarConsulta() {
        List<Consulta> consultas = controladorFacturacion.obtenerConsultasConfirmadas();
        if (consultas.isEmpty()) { mostrarError("No hay consultas pendientes de facturación."); return; }
        JComboBox<Opcion<Consulta>> consulta = combo(consultas, c -> descripcionConsulta(c));
        JComboBox<String> medio = new JComboBox<>(new String[]{"Efectivo", "Débito", "Crédito"});
        JSpinner cuotas = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JPanel formulario = formulario(new JLabel("Consulta"), consulta, new JLabel("Medio de pago"), medio,
                new JLabel("Cuotas (crédito)"), cuotas);
        if (!confirmarFormulario("Facturar consulta", formulario)) return;
        MedioPago pago;
        switch ((String) medio.getSelectedItem()) {
            case "Débito": pago = new Debito(); break;
            case "Crédito": pago = new Credito((Integer) cuotas.getValue()); break;
            default: pago = new Efectivo();
        }
        boolean ok = controladorFacturacion.facturarConsulta(
                consulta.getItemAt(consulta.getSelectedIndex()).valor.getId(), pago);
        if (ok) mostrarExito("La consulta fue facturada correctamente.");
        else mostrarError("No se pudo facturar la consulta.");
    }

    private void cobrarSenia() {
        List<Duenio> duenios = controladorFacturacion.obtenerDueniosConSeniaRequerida();
        if (duenios.isEmpty()) { mostrarError("No hay dueños con una seña pendiente de pago."); return; }
        JComboBox<Opcion<Duenio>> duenio = combo(duenios, d -> d.getNombreCompleto() + " — " + d.getDocumento());
        if (!confirmarFormulario("Cobrar seña ($" + controladorFacturacion.getMontoSenia() + ")",
                formulario(new JLabel("Dueño"), duenio))) return;
        boolean ok = controladorFacturacion.cobrarSenia(duenio.getItemAt(duenio.getSelectedIndex()).valor.getDocumento());
        if (ok) mostrarExito("La seña fue cobrada. El dueño ya puede pedir turnos.");
        else mostrarError("No se pudo cobrar la seña.");
    }

    private void anularFactura() {
        JTextField numero = campo("Número de factura");
        JTextField motivo = campo("Motivo de anulación");
        JTextField documento = campo("Documento de la recepcionista");
        if (!confirmarFormulario("Anular factura", formulario(
                new JLabel("Número de factura"), numero,
                new JLabel("Motivo"), motivo,
                new JLabel("Documento de recepcionista"), documento))) return;
        boolean ok = controladorFacturacion.anularFactura(valor(numero), valor(motivo), valor(documento));
        if (ok) mostrarExito("La factura fue anulada correctamente.");
        else mostrarError("No se pudo anular. Verifique el número, el motivo y el documento de la recepcionista.");
    }

    private void verDuenios() {
        List<Duenio> duenios = controladorRegistro.obtenerDuenios();
        StringBuilder texto = new StringBuilder();
        for (Duenio d : duenios) texto.append(d).append("\nMascotas: ")
                .append(d.getMascotas().isEmpty() ? "Ninguna" : nombresMascotas(d.getMascotas())).append("\n\n");
        mostrarLista("Dueños registrados", texto.toString(), "No hay dueños registrados.");
    }

    private void verMascotas() {
        StringBuilder texto = new StringBuilder();
        for (Mascota m : controladorRegistro.obtenerMascotas()) {
            texto.append(m).append("\nDueño: ").append(m.getDuenio().getNombreCompleto()).append("\n\n");
        }
        mostrarLista("Mascotas registradas", texto.toString(), "No hay mascotas registradas.");
    }

    private void verTurnos() {
        StringBuilder texto = new StringBuilder();
        for (Turno t : controladorTurnos.obtenerTurnos()) texto.append(descripcionTurno(t)).append("\n\n");
        mostrarLista("Todos los turnos", texto.toString(), "No hay turnos registrados.");
    }

    private void verTurnosDuenio() {
        List<Duenio> duenios = controladorRegistro.obtenerDuenios();
        if (duenios.isEmpty()) { mostrarError("No hay dueños registrados."); return; }
        JComboBox<Opcion<Duenio>> duenio = combo(duenios, d -> d.getNombreCompleto() + " — DNI " + d.getDocumento());
        if (!confirmarFormulario("Consultar turnos de un dueño", formulario(new JLabel("Dueño"), duenio))) return;
        Duenio seleccionado = duenio.getItemAt(duenio.getSelectedIndex()).valor;
        StringBuilder texto = new StringBuilder();
        for (Turno t : controladorTurnos.obtenerTurnosDeDuenio(seleccionado.getDocumento())) {
            texto.append(descripcionTurno(t)).append("\n\n");
        }
        mostrarLista("Turnos de " + seleccionado.getNombreCompleto(), texto.toString(), "Este dueño no tiene turnos registrados.");
    }

    private void verConsultasPendientes() {
        StringBuilder texto = new StringBuilder();
        for (Consulta c : controladorFacturacion.obtenerConsultasConfirmadas()) texto.append(descripcionConsulta(c)).append("\n\n");
        mostrarLista("Consultas pendientes de facturación", texto.toString(), "No hay consultas pendientes de facturación.");
    }

    private List<Turno> turnosActivos() {
        return controladorTurnos.obtenerTurnos().stream()
                .filter(t -> t.getEstado().name().equals("PROGRAMADO") || t.getEstado().name().equals("EN_CURSO"))
                .toList();
    }

    private void seleccionarTurno(String titulo, String vacio, java.util.function.Consumer<Turno> accion) {
        List<Turno> turnos = turnosActivos();
        if (turnos.isEmpty()) { mostrarError(vacio); return; }
        JComboBox<Opcion<Turno>> combo = combo(turnos, this::descripcionTurno);
        if (confirmarFormulario(titulo, formulario(new JLabel("Turno"), combo))) {
            accion.accept(combo.getItemAt(combo.getSelectedIndex()).valor);
        }
    }

    private String descripcionTurno(Turno t) {
        return t.getFechaHora().format(FORMATO_FECHA_HORA) + " — " + t.getMascota().getNombre()
                + " — " + t.getTipoAtencion() + " — Dr. " + t.getVeterinario().getNombreCompleto()
                + " — " + t.getEstado() + " — ID " + t.getId();
    }

    private String descripcionConsulta(Consulta c) {
        Turno t = c.getTurno();
        return "Consulta " + c.getId() + " — " + t.getMascota().getNombre() + " — Dueño: "
                + t.getMascota().getDuenio().getNombreCompleto() + " — "
                + t.getFechaHora().format(FORMATO_FECHA_HORA) + " — " + t.getTipoAtencion();
    }

    private String nombresMascotas(List<Mascota> mascotas) {
        StringBuilder nombres = new StringBuilder();
        for (Mascota m : mascotas) {
            if (nombres.length() > 0) nombres.append(", ");
            nombres.append(m.getNombre());
        }
        return nombres.toString();
    }

    private JTextField campo(String placeholder) {
        JTextField campo = new JTextField(20);
        campo.setToolTipText(placeholder);
        return campo;
    }

    private JPanel formulario(Component... componentes) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        for (int i = 0; i < componentes.length; i++) {
            panel.add(componentes[i]);
        }
        return panel;
    }

    private void agregarCampo(JPanel panel, String etiqueta, Component componente) {
        panel.add(new JLabel(etiqueta));
        panel.add(componente);
    }

    private boolean confirmarFormulario(String titulo, JPanel formulario) {
        return JOptionPane.showConfirmDialog(ventana, formulario, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(ventana, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void mostrarLista(String titulo, String contenido, String vacio) {
        if (contenido.isBlank()) contenido = vacio;
        JTextArea area = new JTextArea(contenido, 22, 65);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        JOptionPane.showMessageDialog(ventana, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "No se pudo completar la operación", JOptionPane.ERROR_MESSAGE);
    }

    private String mensajeError(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    private String valor(JTextField campo) {
        return campo.getText().trim();
    }

    private <T> JComboBox<Opcion<T>> combo(List<T> elementos, java.util.function.Function<T, String> etiqueta) {
        DefaultComboBoxModel<Opcion<T>> modelo = new DefaultComboBoxModel<>();
        for (T elemento : elementos) modelo.addElement(new Opcion<>(elemento, etiqueta.apply(elemento)));
        return new JComboBox<>(modelo);
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
