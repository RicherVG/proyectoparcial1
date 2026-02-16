package navalstrikep;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ConfigPantalla extends PantallaBase {

    private JComboBox<String> cbDificultad;
    private JComboBox<String> cbModo;
    private JLabel lblActual;

    public ConfigPantalla(ControlUsuarios control) {
        super(control);
    }

    @Override
    protected String getTituloVentana() {
        return "Naval Strike - Configuración";
    }

    @Override
    protected Dimension getTamanoVentana() {
        return new Dimension(1000, 650);
    }

    @Override
    protected JPanel crearContenido() {

        if (control.getUsuarioActual() == null) {
            cambiarAPantalla(new LoginPantalla(control));
            return new JPanel();
        }

        PanelFondo fondo = new PanelFondo("/navalstrikep/imagenes/fondo_menu.jpg");
        fondo.setLayout(new BorderLayout(12, 12));
        fondo.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(15, 28, 44, 230));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,70), 1, true),
                new EmptyBorder(18,18,18,18)
        ));

        JLabel titulo = new JLabel("CONFIGURACIÓN", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        lblActual = new JLabel("", SwingConstants.CENTER);
        lblActual.setForeground(new Color(220,230,240));

        JPanel top = new JPanel(new GridLayout(2,1,0,6));
        top.setOpaque(false);
        top.add(titulo);
        top.add(lblActual);

        card.add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;

        centro.add(label("Dificultad"), gc);

        gc.gridy++;
        cbDificultad = new JComboBox<>(new String[]{"EASY", "NORMAL", "EXPERT", "GENIUS"});
        estiloCombo(cbDificultad);
        centro.add(cbDificultad, gc);

        gc.gridy++;
        centro.add(label("Modo"), gc);

        gc.gridy++;
        cbModo = new JComboBox<>(new String[]{"TUTORIAL", "ARCADE"});
        estiloCombo(cbModo);
        centro.add(cbModo, gc);

        card.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 10));
        botones.setOpaque(false);

        JButton btnGuardar = boton("Guardar", new Color(176,120,40));
        JButton btnVolver  = boton("Volver",  new Color(30,91,168));

        btnGuardar.addActionListener(e -> guardar());
        btnVolver.addActionListener(e -> cambiarAPantalla(new MenuPantalla(control)));

        botones.add(btnGuardar);
        botones.add(btnVolver);

        card.add(botones, BorderLayout.SOUTH);

        fondo.add(card, BorderLayout.CENTER);

        // cargar actuales
        cbDificultad.setSelectedItem(control.getDificultad());
        cbModo.setSelectedItem(control.getModo());
        refrescarLabel();

        return fondo;
    }

    private void guardar() {
        String dif = (String) cbDificultad.getSelectedItem();
        String modo = (String) cbModo.getSelectedItem();
        if (dif == null) dif = "NORMAL";
        if (modo == null) modo = "TUTORIAL";

        control.setDificultad(dif);
        control.setModo(modo);

        refrescarLabel();

        JOptionPane.showMessageDialog(
                ventana,
                "Configuración guardada:\nDificultad: " + control.getDificultad() + "\nModo: " + control.getModo(),
                "OK",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void refrescarLabel() {
        lblActual.setText("Actual: Dificultad = " + control.getDificultad() + " | Modo = " + control.getModo());
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(220,230,240));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private void estiloCombo(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(Color.WHITE);
        cb.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 210, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setBackground(color);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,60), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
