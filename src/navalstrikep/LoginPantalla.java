package navalstrikep;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LoginPantalla extends PantallaBase {

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JLabel lblMensaje;

    private static final String RUTA_FONDO_LOGIN = "/navalstrikep/imagenes/fondo_login.jpg";

    public LoginPantalla(ControlUsuarios control) {
        super(control);
    }

    @Override
    protected String getTituloVentana() {
        return "Naval Strike - Login";
    }

    @Override
    protected Dimension getTamanoVentana() {
        return new Dimension(1000, 750);
    }

    @Override
    protected JPanel crearContenido() {
        PanelFondo fondo = new PanelFondo(RUTA_FONDO_LOGIN);
        fondo.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(290, 0, 0, 0);
        gc.anchor = GridBagConstraints.NORTH;

        fondo.add(crearFormulario(), gc);
        return fondo;
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new BorderLayout(12, 12));
        formulario.setPreferredSize(new Dimension(520, 340));
        formulario.setBackground(new Color(15, 28, 44, 210));
        formulario.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 80), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel titulo = new JLabel("Menú de Inicio", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 6));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        

        formulario.add(encabezado, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(-6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        gc.gridy = 0;
        campos.add(label("Usuario"), gc);

        gc.gridy++;
        txtUsuario = campoTexto();
        campos.add(txtUsuario, gc);

        gc.gridy++;
        campos.add(label("Contraseña"), gc);

        gc.gridy++;
        txtClave = new JPasswordField();
        estiloCampo(txtClave);
        campos.add(txtClave, gc);

        gc.gridy++;
        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setForeground(new Color(255, 190, 190));
        campos.add(lblMensaje, gc);

        formulario.add(campos, BorderLayout.CENTER);

        JButton btnLogin = boton("Iniciar Sesión", new Color(30, 91, 168));
        JButton btnCrear = boton("Crear Player", new Color(176, 120, 40));
        JButton btnSalir = boton("Salir", new Color(90, 90, 90));

        btnLogin.addActionListener(e -> intentarLogin());
        btnCrear.addActionListener(e -> crearUsuario());
        btnSalir.addActionListener(e -> salirApp());

        JPanel botones = new JPanel(new GridLayout(1, 3, 10, 10));
        botones.setOpaque(false);
        botones.add(btnLogin);
        botones.add(btnCrear);
        botones.add(btnSalir);

        formulario.add(botones, BorderLayout.SOUTH);

        txtClave.addActionListener(e -> intentarLogin());

        return formulario;
    }



    private void intentarLogin() {
        String usuario = txtUsuario.getText();
        String clave = new String(txtClave.getPassword());

        String error = control.validarCredenciales(usuario, clave);

        if (error == null) {
            mostrarOk("Login correcto.");
            cambiarAPantalla(new MenuPantalla(control));
        } else {
            mostrarError(error);
        }
    }

    private void crearUsuario() {
        String u = txtUsuario.getText();
        String c = new String(txtClave.getPassword());

        String err = control.crearUsuario(u, c);
        txtClave.setText("");
        if (err == null) {
            mostrarOk("Usuario creado. Entrando al menú...");
            cambiarAPantalla(new MenuPantalla(control)); 
        } else {
            mostrarError(err);
        }
    }

    private void salirApp() {
        int ans = JOptionPane.showConfirmDialog(
                ventana,
                "¿Deseas salir de la aplicación?",
                "Salir",
                JOptionPane.YES_NO_OPTION
        );
        if (ans == JOptionPane.YES_OPTION) System.exit(0);
    }

    private void mostrarError(String msg) {
        lblMensaje.setForeground(new Color(255, 190, 190));
        lblMensaje.setText(msg);
    }

    private void mostrarOk(String msg) {
        lblMensaje.setForeground(new Color(190, 255, 190));
        lblMensaje.setText(msg);
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(new Color(220, 230, 240));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private JTextField campoTexto() {
        JTextField f = new JTextField();
        estiloCampo(f);
        return f;
    }

    private void estiloCampo(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(Color.WHITE);
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 210, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(color);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 12, 10, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

}
