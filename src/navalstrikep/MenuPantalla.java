package navalstrikep;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class MenuPantalla extends PantallaBase {

    private static final String RUTA_FONDO_MENU = "/navalstrikep/imagenes/fondo_menu.jpg";

    public MenuPantalla(ControlUsuarios control) {
        super(control);
    }

    @Override
    protected String getTituloVentana() {
        return "Naval Strike - Menú Principal";
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

        PanelFondo fondo = new PanelFondo(RUTA_FONDO_MENU);
        fondo.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(70, 0, 0, 0);
        gc.anchor = GridBagConstraints.NORTH;

        fondo.add(crearPanelMenu(), gc);
        return fondo;
    }

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setPreferredSize(new Dimension(540, 460));
        panel.setBackground(new Color(15, 28, 44, 190));
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 70), 1, true),
                new EmptyBorder(22, 22, 22, 22)
        ));

        String nombre = control.getUsuarioActual().getNombre();
        JLabel titulo = new JLabel("MENÚ PRINCIPAL", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        JLabel sesion = new JLabel("Sesión: " + nombre, SwingConstants.CENTER);
        sesion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sesion.setForeground(new Color(220, 230, 240));

        JPanel head = new JPanel(new GridLayout(2, 1, 0, 8));
        head.setOpaque(false);
        head.add(titulo);
        head.add(sesion);
        panel.add(head, BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(6, 1, 12, 12));
        opciones.setOpaque(false);

        JButton btnJugar = botonGrande("Jugar Battleship");
        btnJugar.addActionListener(e -> cambiarAPantalla(new InvitarJugadorPantalla(control)));

        JButton btnConfig = botonGrande("Configuración");
        btnConfig.addActionListener(e -> cambiarAPantalla(new ConfigPantalla(control)));

        JButton btnReportes = botonGrande("Reportes");
        btnReportes.addActionListener(e -> cambiarAPantalla(new ReportesPantalla(control)));

        JButton btnPerfil = botonGrande("Mi Perfil");
        btnPerfil.addActionListener(e -> cambiarAPantalla(new PerfilPantalla(control)));

        JButton btnCerrar = botonGrande("Cerrar Sesión");
        btnCerrar.setBackground(new Color(170, 40, 40));
        btnCerrar.addActionListener(e -> {
            control.cerrarSesion();
            cambiarAPantalla(new LoginPantalla(control));
        });

        JButton btnSalirApp = botonGrande("Salir");
        btnSalirApp.setBackground(new Color(90, 90, 90));
        btnSalirApp.addActionListener(e -> {
            int ans = JOptionPane.showConfirmDialog(
                    ventana,
                    "¿Deseas salir de la aplicación?",
                    "Salir",
                    JOptionPane.YES_NO_OPTION
            );
            if (ans == JOptionPane.YES_OPTION) System.exit(0);
        });

        opciones.add(btnJugar);
        opciones.add(btnConfig);
        opciones.add(btnReportes);
        opciones.add(btnPerfil);
        opciones.add(btnCerrar);
        opciones.add(btnSalirApp);

        panel.add(opciones, BorderLayout.CENTER);

        return panel;
    }

    private JButton botonGrande(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        b.setBackground(new Color(30, 91, 168));
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 60), 1, true),
                new EmptyBorder(14, 12, 14, 12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
