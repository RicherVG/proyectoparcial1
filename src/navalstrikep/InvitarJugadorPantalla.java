package navalstrikep;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;

public class InvitarJugadorPantalla extends PantallaBase {

    private static final String RUTA_FONDO_MENU = "/navalstrikep/imagenes/fondo_menu.jpg";

    private JTextField txtInvitado;
    private String nombreJ1;

    public InvitarJugadorPantalla(ControlUsuarios control) {
        super(control);
    }

    @Override
    protected String getTituloVentana() {
        return "Naval Strike - Invitar Jugador 2";
    }

    @Override
    protected Dimension getTamanoVentana() {
        return new Dimension(1000, 650);
    }

    @Override
    protected JPanel crearContenido() {
        PanelFondo fondo = new PanelFondo(RUTA_FONDO_MENU);
        fondo.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(70, 0, 0, 0);
        gc.anchor = GridBagConstraints.NORTH;

        fondo.add(crearTarjeta(), gc);
        return fondo;
    }

    private JPanel crearTarjeta() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setPreferredSize(new Dimension(560, 360));
        card.setBackground(new Color(15, 28, 44, 190));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 70), 1, true),
                new EmptyBorder(22, 22, 22, 22)
        ));

        nombreJ1 = (control.getUsuarioActual() == null) ? "Usuario" : control.getUsuarioActual().getNombre();

        JLabel titulo = new JLabel("INVITAR JUGADOR 2", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JLabel sesion = new JLabel("Jugador 1 (sesión): " + nombreJ1, SwingConstants.CENTER);
        sesion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sesion.setForeground(new Color(220, 230, 240));

        JPanel head = new JPanel(new GridLayout(3, 1, 0, 6));
        head.setOpaque(false);
        head.add(titulo);
        head.add(sesion);
        card.add(head, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        gc.gridy = 0;
        JLabel l = new JLabel("Nombre del Jugador 2");
        l.setForeground(new Color(220, 230, 240));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        center.add(l, gc);

        gc.gridy++;
        txtInvitado = new JTextField();
        estiloCampo(txtInvitado);
        center.add(txtInvitado, gc);

        card.add(center, BorderLayout.CENTER);

        JButton btnInvitar = botonGrande("Confirmar");
        JButton btnVolver = botonGrande("Volver");
        btnVolver.setBackground(new Color(70, 120, 170));

        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 12));
        botones.setOpaque(false);
        botones.add(btnInvitar);
        botones.add(btnVolver);

        card.add(botones, BorderLayout.SOUTH);

        btnVolver.addActionListener(e -> cambiarAPantalla(new MenuPantalla(control)));
        btnInvitar.addActionListener(e -> confirmarInvitacion());
        txtInvitado.addActionListener(e -> confirmarInvitacion());

        return card;
    }

    private void confirmarInvitacion() {

        Usuario j1 = control.getUsuarioActual();
        if (j1 == null) {
            alertaError("No hay sesión activa. Volvé a iniciar sesión.");
            cambiarAPantalla(new LoginPantalla(control));
            return;
        }

        String j2 = txtInvitado.getText();
        if (j2 == null) j2 = "";
        j2 = j2.trim();

        if (j2.isEmpty()) {
            alertaError("Ingresa el usuario del Jugador 2 (o EXIT para cancelar).");
            txtInvitado.requestFocus();
            return;
        }

        // No permitir el mismo usuario
        if (j1.getNombre().equalsIgnoreCase(j2)) {
            alertaError("Jugador 2 debe ser diferente al Jugador 1.");
            txtInvitado.requestFocus();
            txtInvitado.selectAll();
            return;
        }

        // Debe existir
        if (!control.existeUsuario(j2)) {
            alertaError("Ese usuario no existe. Intenta de nuevo o escribe EXIT.");
            txtInvitado.requestFocus();
            txtInvitado.selectAll();
            return;
        }

        Usuario j2User = control.buscarUsuario(j2);
        if (j2User == null) {
            alertaError("No se pudo cargar el Jugador 2.");
            return;
        }

        cambiarAPantalla(new JuegoPantalla(control, j1, j2User));
    }

    private void estiloCampo(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(Color.WHITE);
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 210, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private JButton botonGrande(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setBackground(new Color(30, 91, 168));
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,60), 1, true),
                new EmptyBorder(14, 12, 14, 12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void alertaError(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class PanelFondo extends JPanel {
        private Image fondo;

        public PanelFondo(String ruta) {
            URL url = getClass().getResource(ruta);
            if (url != null) {
                fondo = new ImageIcon(url).getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (fondo != null) {
                int w = getWidth();
                int h = getHeight();
                g.drawImage(fondo, 0, 0, w, h, this);
                g.setColor(new Color(0, 0, 0, 50));
                g.fillRect(0, 0, w, h);
            }
        }
    }
}
