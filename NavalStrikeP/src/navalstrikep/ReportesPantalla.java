package navalstrikep;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ReportesPantalla extends PantallaBase {

    private JTextArea txt;

    public ReportesPantalla(ControlUsuarios control) {
        super(control);
    }

    @Override
    protected String getTituloVentana() {
        return "Naval Strike - Reportes";
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
        fondo.setBorder(new EmptyBorder(12,12,12,12));

        JPanel card = new JPanel(new BorderLayout(10,10));
        card.setBackground(new Color(15,28,44,230));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,70), 1, true),
                new EmptyBorder(18,18,18,18)
        ));

        JLabel titulo = new JLabel("REPORTES", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        String nombre = control.getUsuarioActual().getNombre();
        JLabel sub = new JLabel("Sesión: " + nombre, SwingConstants.CENTER);
        sub.setForeground(new Color(220,230,240));

        JPanel top = new JPanel(new GridLayout(2,1,0,6));
        top.setOpaque(false);
        top.add(titulo);
        top.add(sub);

        card.add(top, BorderLayout.NORTH);

        txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setForeground(new Color(235,245,255));
        txt.setOpaque(true);
        txt.setBackground(new Color(15,28,44));
        txt.setBorder(new EmptyBorder(10,10,10,10));

        JScrollPane sp = new JScrollPane(txt);
        sp.setBorder(new LineBorder(new Color(255,255,255,60), 1, true));
        sp.setOpaque(true);
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(txt.getBackground());

        card.add(sp, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1,3,10,10));
        botones.setOpaque(false);

        JButton btnMis10 = boton("Mis últimos 10", new Color(176,120,40));
        JButton btnRanking = boton("Ranking", new Color(75,145,210));
        JButton btnVolver = boton("Volver", new Color(30,91,168));

        btnMis10.addActionListener(e -> cargarMis10());
        btnRanking.addActionListener(e -> cargarRanking());
        btnVolver.addActionListener(e -> cambiarAPantalla(new MenuPantalla(control)));

        botones.add(btnMis10);
        botones.add(btnRanking);
        botones.add(btnVolver);

        card.add(botones, BorderLayout.SOUTH);

        fondo.add(card, BorderLayout.CENTER);

        cargarMis10();

        return fondo;
    }

    private void cargarMis10() {
        StringBuilder sb = new StringBuilder();
        sb.append("MIS ÚLTIMOS 10 JUEGOS\n\n");

        String[] logs = control.misUltimos10Juegos();

        // ✅ mostrar siempre 10 líneas (aunque estén vacías)
        for (int i = 0; i < 10; i++) {
            String log = (logs != null && i < logs.length) ? logs[i] : null;
            sb.append((i + 1)).append(") ");
            if (log != null && !log.trim().isEmpty()) sb.append(log.trim());
            sb.append("\n");
        }

        txt.setText(sb.toString());
        txt.setCaretPosition(0);
    }

    private void cargarRanking() {
        StringBuilder sb = new StringBuilder();
        sb.append("RANKING DE JUGADORES (por puntos)\n\n");

        Usuario[] arr = control.rankingJugadores();

        if (arr == null || arr.length == 0) {
            sb.append("No hay jugadores.\n");
        } else {
            for (int i = 0; i < arr.length; i++) {
                sb.append((i+1)).append(") ")
                  .append(arr[i].getNombre())
                  .append("  | Puntos: ")
                  .append(arr[i].getPuntos())
                  .append("\n");
            }
        }

        txt.setText(sb.toString());
        txt.setCaretPosition(0);
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
