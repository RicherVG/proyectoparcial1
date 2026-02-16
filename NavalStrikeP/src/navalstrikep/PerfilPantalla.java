package navalstrikep;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PerfilPantalla extends PantallaBase {

    private JTextField txtNuevoUsuario;
    private JPasswordField txtNuevaClave;
    private JLabel lblMsg;

    private JLabel lblSesion;
    private JTextArea txtMisDatos;

    public PerfilPantalla(ControlUsuarios control) {
        super(control);
    }

    @Override
    protected String getTituloVentana() {
        return "Naval Strike - Perfil";
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

        JLabel titulo = new JLabel("MI PERFIL", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        lblSesion = new JLabel("", SwingConstants.CENTER);
        lblSesion.setForeground(new Color(220,230,240));

        JPanel top = new JPanel(new GridLayout(2,1,0,6));
        top.setOpaque(false);
        top.add(titulo);
        top.add(lblSesion);

        card.add(top, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        centro.add(label("Mis datos"), gc);

        gc.gridy++;
        txtMisDatos = new JTextArea(3, 20);
        txtMisDatos.setEditable(false);
        txtMisDatos.setLineWrap(true);
        txtMisDatos.setWrapStyleWord(true);
        txtMisDatos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMisDatos.setForeground(new Color(235,245,255));
        txtMisDatos.setBackground(new Color(0,0,0,120));
        txtMisDatos.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,50), 1, true),
                new EmptyBorder(10,10,10,10)
        ));
        centro.add(txtMisDatos, gc);
        gc.gridy++;
        centro.add(label("Nuevo usuario"), gc);

        gc.gridy++;
        txtNuevoUsuario = new JTextField();
        estiloCampo(txtNuevoUsuario);
        centro.add(txtNuevoUsuario, gc);

        gc.gridy++;
        centro.add(label("Nueva contraseña"), gc);

        gc.gridy++;
        txtNuevaClave = new JPasswordField();
        estiloCampo(txtNuevaClave);
        centro.add(txtNuevaClave, gc);

        gc.gridy++;
        lblMsg = new JLabel(" ", SwingConstants.CENTER);
        lblMsg.setForeground(new Color(255, 190, 190));
        centro.add(lblMsg, gc);

        card.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1, 3, 10, 10));
        botones.setOpaque(false);

        JButton btnGuardar = boton("Guardar", new Color(176,120,40));
        JButton btnEliminar = boton("Eliminar cuenta", new Color(190,90,90));
        JButton btnVolver = boton("Volver", new Color(30,91,168));

        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnVolver.addActionListener(e -> cambiarAPantalla(new MenuPantalla(control)));

        botones.add(btnGuardar);
        botones.add(btnEliminar);
        botones.add(btnVolver);

        card.add(botones, BorderLayout.SOUTH);

        fondo.add(card, BorderLayout.CENTER);

        refrescarDatos();

        return fondo;
    }

    private void refrescarDatos() {
        Usuario u = control.getUsuarioActual();
        if (u == null) return;

        lblSesion.setText("Sesión: " + u.getNombre());

        txtMisDatos.setText(
                "Usuario: " + u.getNombre() + "\n" +
                "Puntos: " + u.getPuntos() + "\n" +
                "Logs guardados: " + u.getCantidadLogs() + "/10"
        );
        txtNuevoUsuario.setText(u.getNombre());
        txtNuevaClave.setText("");
    }

    private void guardar() {
        String nuevoU = txtNuevoUsuario.getText();
        String nuevaC = new String(txtNuevaClave.getPassword());

        String err = control.modificarMisDatos(nuevoU, nuevaC);
        if (err == null) {
            lblMsg.setForeground(new Color(190,255,190));
            lblMsg.setText("Datos actualizados correctamente.");
            refrescarDatos();
        } else {
            lblMsg.setForeground(new Color(255,190,190));
            lblMsg.setText(err);
        }
    }

    private void eliminar() {
        int ans = JOptionPane.showConfirmDialog(
                ventana,
                "¿Seguro que quieres eliminar tu cuenta?\n(Esta acción no se puede deshacer)",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        if (ans != JOptionPane.YES_OPTION) return;

        boolean ok = control.eliminarMiCuenta();
        if (ok) {
            JOptionPane.showMessageDialog(ventana, "Cuenta eliminada.", "OK", JOptionPane.INFORMATION_MESSAGE);
            cambiarAPantalla(new LoginPantalla(control));
        } else {
            JOptionPane.showMessageDialog(ventana, "No se pudo eliminar (sin sesión).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(220,230,240));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private void estiloCampo(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(Color.WHITE);
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 210, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
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
