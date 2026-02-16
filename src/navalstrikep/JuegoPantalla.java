package navalstrikep;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JuegoPantalla extends PantallaBase {

    private static final String RUTA_FONDO_JUEGO = "/navalstrikep/imagenes/fondo_juego.jpg";

    private boolean esperandoAnimacion = false;
    private static final int PAUSA_MS = 800;

    private static final int TAM = 8;
    private static final int CELL = 60;
    private static final int GAP  = 2;

    private static final int THUMB_W = 210;
    private static final int THUMB_H = 80;

    private final Usuario jugador1;
    private final Usuario jugador2;
    private final JuegoLogica juego;

    private JLabel lblJugadores;
    private JLabel lblEstado;
    private JLabel lblRestantes;
    private JLabel lblTitulo;

    private JButton btnGirar;
    private JButton btnSalir;
    private JButton btnRendirse;

    private JTextArea txtBitacora;

    private final JButton[][] celdas = new JButton[TAM][TAM];
    private JLayeredPane layered;
    private JPanel gridPanel;
    private OverlayTransparente overlay;
    private final List<JLabel> overlayShips = new ArrayList<>();
    private final String[] shipCodes = {"PA","AZ","SM","DT"};
    private final JLabel[] shipThumbs = new JLabel[4];
    private final ImageIcon iconAgua;
    private final ImageIcon iconExplosion;
    private int ultFila = -1, ultCol = -1;
    private boolean ultFueImpacto = false;
    private boolean ultFueAgua = false;
    private static final int MAX_BARCOS_EASY = 5;
    private final ShipOverlayInfo[] colocadosJ1 = new ShipOverlayInfo[MAX_BARCOS_EASY];
    private final ShipOverlayInfo[] colocadosJ2 = new ShipOverlayInfo[MAX_BARCOS_EASY];
    private int nColJ1 = 0;
    private int nColJ2 = 0;

    private static class ShipOverlayInfo {
        int fila, col, largo;
        String codigo;
        boolean horizontal;

        ShipOverlayInfo(int fila, int col, int largo, String codigo, boolean horizontal) {
            this.fila = fila;
            this.col = col;
            this.largo = largo;
            this.codigo = codigo;
            this.horizontal = horizontal;
        }
    }

    public JuegoPantalla(ControlUsuarios control, Usuario j1, Usuario j2) {
        super(control);
        this.jugador1 = j1;
        this.jugador2 = j2;

        this.juego = new JuegoLogica(j1, j2);
        this.juego.setConfig(control.getDificultad(), control.getModo());

        iconAgua = cargarIcono("agua.png");
        iconExplosion = cargarIcono("explosion.png");
    }

    @Override protected String getTituloVentana() { return "Naval Strike - Juego"; }
    @Override protected Dimension getTamanoVentana() { return new Dimension(1280, 1000); }

    @Override
    protected JPanel crearContenido() {
        PanelFondo fondo = new PanelFondo(RUTA_FONDO_JUEGO);
        fondo.setLayout(new BorderLayout(12, 12));
        fondo.setBorder(new EmptyBorder(12, 12, 12, 12));

        fondo.add(crearBarraSuperior(), BorderLayout.NORTH);
        fondo.add(crearPanelBarcosIzq(), BorderLayout.WEST);
        fondo.add(crearCentro(), BorderLayout.CENTER);
        fondo.add(crearBarraInferior(), BorderLayout.SOUTH);

        actualizarUI();
        return fondo;
    }

    private JPanel crearBarraSuperior() {
        JPanel top = new JPanel();
        top.setOpaque(true);
        top.setBackground(new Color(10, 18, 28, 190));
        top.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,60), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

        lblJugadores = new JLabel();
        lblJugadores.setForeground(new Color(235,245,255));
        lblJugadores.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblEstado = new JLabel();
        lblEstado.setForeground(new Color(235,245,255));
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblRestantes = new JLabel();
        lblRestantes.setForeground(new Color(235,245,255));
        lblRestantes.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGirar = new JButton("Girar");
        styleTopButton(btnGirar, new Color(75, 145, 210));
        btnGirar.addActionListener(e -> {
            if (!juego.estaEnColocacion()) { msg("Solo puedes girar en colocación."); return; }
            juego.girarOrientacion();
            actualizarUI();
        });

        btnRendirse = new JButton("Rendirse");
        styleTopButton(btnRendirse, new Color(220, 150, 80));
        btnRendirse.addActionListener(e -> rendirse());


        btnSalir = new JButton("Salir");
        styleTopButton(btnSalir, new Color(190, 90, 90));
        btnSalir.addActionListener(e -> cambiarAPantalla(new MenuPantalla(control)));

        top.add(lblJugadores);
        top.add(Box.createHorizontalStrut(16));
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(Box.createHorizontalStrut(16));
        top.add(lblEstado);

        top.add(Box.createHorizontalStrut(16));
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(Box.createHorizontalStrut(16));
        top.add(lblRestantes);

        top.add(Box.createHorizontalGlue());
        top.add(btnGirar);
        top.add(Box.createHorizontalStrut(10));
        top.add(btnRendirse);
        top.add(Box.createHorizontalStrut(10));
        top.add(btnSalir);

        return top;
    }

    private void styleTopButton(JButton b, Color bg) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,70), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel crearPanelBarcosIzq() {
        JPanel left = new JPanel();

        left.setOpaque(true);
        left.setBackground(new Color(10, 18, 28, 230));

        left.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,60), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel t = new JLabel("BARCOS");
        t.setAlignmentX(Component.LEFT_ALIGNMENT); 
        t.setForeground(new Color(235,245,255));
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        left.add(t);
        left.add(Box.createVerticalStrut(20));

        for (int i = 0; i < shipThumbs.length; i++) {
            String code = shipCodes[i];
            JLabel thumb = new JLabel();
            thumb.setOpaque(true);
            thumb.setBackground(new Color(0, 0, 0, 120));
            thumb.setBorder(new CompoundBorder(
                    new LineBorder(new Color(255,255,255,55), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            thumb.setPreferredSize(new Dimension(THUMB_W, THUMB_H));
            thumb.setMaximumSize(new Dimension(THUMB_W, THUMB_H));
            thumb.setHorizontalAlignment(SwingConstants.CENTER);

            String file = rutaBarcoPorCodigo(code, true);
            ImageIcon icon = cargarIcono(file);
            if (icon != null && icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(THUMB_W - 20, THUMB_H - 20, Image.SCALE_SMOOTH);
                thumb.setIcon(new ImageIcon(img));
            } else {
                thumb.setText(code);
                thumb.setForeground(Color.WHITE);
            }

            shipThumbs[i] = thumb;
            left.add(thumb);
            left.add(Box.createVerticalStrut(10));
        }
        return left;
    }

    private JPanel crearCentro() {
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        lblTitulo = new JLabel("", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(235,245,255));

        JPanel tituloWrap = new JPanel(new BorderLayout());
        tituloWrap.setOpaque(true);
        tituloWrap.setBackground(new Color(10, 18, 28, 150));
        tituloWrap.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,60), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        tituloWrap.add(lblTitulo, BorderLayout.CENTER);

        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,55), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        card.add(crearTableroLayered());

        center.add(tituloWrap, BorderLayout.NORTH);
        center.add(card, BorderLayout.CENTER);
        return center;
    }
    private void rendirse() {
       int r = JOptionPane.showConfirmDialog(
               null,
               "¿Seguro que deseas rendirte?\nEl otro jugador ganará por retiro.",
               "Confirmar retiro",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.WARNING_MESSAGE
       );
       if (r != JOptionPane.YES_OPTION) return;

       Usuario seRinde = juego.getJugadorEnTurno();
       Usuario ganador = juego.getOtroJugador(seRinde);

       juego.finalizarPorRetiro(ganador, seRinde);

       JOptionPane.showMessageDialog(
               null,
               "¡" + ganador.getNombre() + " ganó por retiro!",
               "Fin del juego",
               JOptionPane.INFORMATION_MESSAGE
       );

       cambiarAPantalla(new MenuPantalla(control));
   }


    private JLayeredPane crearTableroLayered() {
        int w = TAM * CELL + (TAM - 1) * GAP;
        int h = TAM * CELL + (TAM - 1) * GAP;

        layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(w, h));
        layered.setOpaque(false);
        layered.setLayout(null);

        gridPanel = new JPanel(new GridLayout(TAM, TAM, GAP, GAP));
        gridPanel.setOpaque(false);
        gridPanel.setBounds(0, 0, w, h);

        for (int r = 0; r < TAM; r++) {
            for (int c = 0; c < TAM; c++) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(CELL, CELL));
                b.setMargin(new Insets(0, 0, 0, 0));
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                b.setOpaque(false);
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setBorder(new LineBorder(new Color(255, 255, 255, 110), 1, true));

                final int rr = r, cc = c;
                b.addActionListener(e -> clickCelda(rr, cc));

                celdas[r][c] = b;
                gridPanel.add(b);
            }
        }

        overlay = new OverlayTransparente();
        overlay.setBounds(0, 0, w, h);

        layered.add(overlay, JLayeredPane.DEFAULT_LAYER);
        layered.add(gridPanel, JLayeredPane.PALETTE_LAYER);

        return layered;
    }

    private static class OverlayTransparente extends JPanel {
        public OverlayTransparente() { setOpaque(false); setLayout(null); }
        @Override public boolean contains(int x, int y) { return false; }
    }

    private JPanel crearBarraInferior() {
        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setOpaque(true);
        bottom.setBackground(new Color(8, 12, 20)); // sólido oscuro
        bottom.setBorder(new EmptyBorder(10, 12, 10, 12)); // sin línea

        txtBitacora = new JTextArea(4, 80);
        txtBitacora.setEditable(false);
        txtBitacora.setLineWrap(true);
        txtBitacora.setWrapStyleWord(true);
        txtBitacora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBitacora.setForeground(new Color(235,245,255));
        txtBitacora.setBackground(new Color(10, 15, 25)); // oscuro
        txtBitacora.setOpaque(true);
        txtBitacora.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(txtBitacora);
        sp.setBorder(null);
        sp.setOpaque(true);

        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(new Color(10, 15, 25)); // 👈 esto quita el gris

        bottom.add(sp, BorderLayout.CENTER);
        return bottom;
    }


    private void clickCelda(int fila, int col) {

        if (juego.estaEnColocacion()) {

            JuegoLogica.ColocacionResultado res = juego.colocarBarcoEn(fila, col);
            if (!res.valido) { msg(res.mensaje); return; }
            String nombreColocado = extraerNombreColocado(res.mensaje);
            String codigo = codigoPorNombre(nombreColocado);
            int largo = juego.getLargoPorCodigo(codigo);
            boolean horizontal = juego.isOrientacionHorizontal();

            guardarBarcoColocado(fila, col, largo, codigo, horizontal);

            if (res.cambioATurnoJ2) {
                limpiarOverlayTotal();
                limpiarIconosTablero();
                msg(res.mensaje);
                JOptionPane.showMessageDialog(
                        ventana,
                        "CAMBIO DE TURNO\n\nAhora coloca sus barcos el Jugador 2.\n(No debe mirar el Jugador 1)",
                        "Turno de colocación",
                        JOptionPane.INFORMATION_MESSAGE
                );
                redibujarColocadosActual();
                actualizarUI();
                return;
            }
            redibujarColocadosActual();
            msg(res.mensaje);

            if (res.inicioJuego) {
                limpiarOverlayTotal();
                limpiarIconosTablero();
            }

            actualizarUI();
            return;
        }

        if (esperandoAnimacion) return;
        JuegoLogica.DisparoResultado res = juego.disparar(fila, col);
        if (!res.valido) { msg(res.mensaje); return; }

        msg(res.mensaje);

        ultFila = res.filaDisparo;
        ultCol = res.colDisparo;
        ultFueImpacto = res.fueImpacto;
        ultFueAgua = res.fueAgua;

        mostrarAnimacionTemporal();

        esperandoAnimacion = true;

        new Timer(PAUSA_MS, ev -> {
            ((Timer) ev.getSource()).stop();
            esperandoAnimacion = false;
            limpiarIconosTablero();

            if (res.finJuego) {
                int puntos = juego.calcularPuntos();
                Usuario ganador = res.ganador != null && res.ganador.equalsIgnoreCase(jugador1.getNombre())
                        ? jugador1 : jugador2;

                control.darPuntos(ganador, puntos);

                String modoTxt = juego.isModoTutorial() ? "TUTORIAL" : "ARCADE";

                String log = ganador.getNombre()
                        + " hundió todos los barcos de "
                        + (ganador.equals(jugador1) ? jugador2.getNombre() : jugador1.getNombre())
                        + " en modo " + modoTxt + ".";

                jugador1.agregarLog(log);
                jugador2.agregarLog(log);


                JOptionPane.showMessageDialog(
                        ventana,
                        "GANÓ: " + ganador.getNombre() + "\nPuntos obtenidos: " + puntos,
                        "Fin del juego",
                        JOptionPane.INFORMATION_MESSAGE
                );
                cambiarAPantalla(new MenuPantalla(control));
                return;
            }

            juego.pasarTurno();
            actualizarUI();
        }).start();
    }

    private void mostrarAnimacionTemporal() {
        if (ultFila < 0 || ultCol < 0) return;

        JButton b = celdas[ultFila][ultCol];
        int size = CELL;

        if (ultFueImpacto && iconExplosion != null) {
            ImageIcon sc = escalar(iconExplosion, size, size);
            b.setIcon(sc);
            b.setDisabledIcon(sc);
        } else if (ultFueAgua && iconAgua != null) {
            ImageIcon sc = escalar(iconAgua, size, size);
            b.setIcon(sc);
            b.setDisabledIcon(sc);
        }
    }

    private void limpiarIconosTablero() {
        for (int r=0;r<TAM;r++) for (int c=0;c<TAM;c++) {
            celdas[r][c].setIcon(null);
            celdas[r][c].setDisabledIcon(null);
            celdas[r][c].setBorder(new LineBorder(new Color(255, 255, 255, 110), 1, true));
        }
    }

    private void msg(String m) {
        if (txtBitacora != null) txtBitacora.setText(m);
    }

    private void guardarBarcoColocado(int fila, int col, int largo, String codigo, boolean horizontal) {
        if (codigo == null || largo <= 0) return;

        ShipOverlayInfo info = new ShipOverlayInfo(fila, col, largo, codigo, horizontal);

        String colocador = juego.getColocadorActual().trim();
        if (colocador.equalsIgnoreCase(jugador1.getNombre().trim())) {
            if (nColJ1 < colocadosJ1.length) colocadosJ1[nColJ1++] = info;
        } else {
            if (nColJ2 < colocadosJ2.length) colocadosJ2[nColJ2++] = info;
        }
    }

    private void redibujarColocadosActual() {
        limpiarOverlayTotal();

        String colocador = juego.getColocadorActual().trim();
        if (colocador.equalsIgnoreCase(jugador1.getNombre().trim())) {
            for (int i=0;i<nColJ1;i++) {
                ShipOverlayInfo s = colocadosJ1[i];
                if (s != null) colocarBarcoOverlay(s.fila, s.col, s.largo, s.codigo, s.horizontal);
            }
        } else {
            for (int i=0;i<nColJ2;i++) {
                ShipOverlayInfo s = colocadosJ2[i];
                if (s != null) colocarBarcoOverlay(s.fila, s.col, s.largo, s.codigo, s.horizontal);
            }
        }
    }

    private void limpiarOverlayTotal() {
        for (JLabel l : overlayShips) overlay.remove(l);
        overlayShips.clear();
        overlay.repaint();
    }

    private void colocarBarcoOverlay(int fila, int col, int largo, String codigo, boolean horizontal) {
       String archivo = rutaBarcoPorCodigo(codigo, horizontal);
       if (archivo == null) return;
       int baseX = col * (CELL + GAP);
       int baseY = fila * (CELL + GAP);
       int grosor = CELL - 2;         
       int margen = (CELL - grosor) / 2;    
       int largoPx = (largo * CELL) + ((largo - 1) * GAP) - 2;
       int w = horizontal ? largoPx : grosor;
       int h = horizontal ? grosor : largoPx;
       int x = baseX + (horizontal ? 0 : margen);
       int y = baseY + (horizontal ? margen : 0);
       ImageIcon raw = cargarIcono(archivo);
       if (raw == null || raw.getIconWidth() <= 0) return;
       JLabel barco = new JLabel(escalar(raw, w, h));
       barco.setBounds(x, y, w, h);
       overlay.add(barco);
       overlayShips.add(barco);
       overlay.repaint();
   }

    
    private void actualizarUI() {
        lblJugadores.setText("Jugador 1: " + jugador1.getNombre() + "   |   Jugador 2: " + jugador2.getNombre());

        if (juego.estaEnColocacion()) {
            lblEstado.setText("COLOCACIÓN: " + juego.getColocadorActual());
            lblRestantes.setText("");

            lblTitulo.setText("Coloca: " + juego.getNombreBarcoActual() + " (" + juego.getLargoBarcoActual() + ") — " +
                    (juego.isOrientacionHorizontal() ? "Horizontal" : "Vertical"));

            limpiarIconosTablero();
            redibujarColocadosActual();

        } else {
            lblEstado.setText("TURNO: " + juego.getAtacante());
            lblRestantes.setText("Restantes: " + juego.getBarcosRestantesDefensor());
            lblTitulo.setText("Ataque — objetivo: " + juego.getDefensor());

            limpiarOverlayTotal(); 
            limpiarIconosTablero(); 

            if (juego.isModoTutorial()) {
                dibujarBarcosTutorialDefensor();
            }
        }

        if (ventana != null) ventana.repaint();
    }

    private void dibujarBarcosTutorialDefensor() {
        try {
            for (JuegoLogica.BarcoVista bv : juego.getBarcosDefensorVista()) {
                if (bv == null) continue;
                colocarBarcoOverlay(bv.fila, bv.col, bv.largo, bv.codigo, bv.horizontal);
            }
        } catch (Exception ignored) {
        }
    }
    private String codigoPorNombre(String nombre) {
        if (nombre == null) return null;
        return switch (nombre) {
            case "Portaviones" -> "PA";
            case "Acorazado" -> "AZ";
            case "Submarino" -> "SM";
            case "Destructor" -> "DT";
            default -> null;
        };
    }

    private String rutaBarcoPorCodigo(String codigo, boolean horizontal) {
        return switch (codigo) {
            case "PA" -> horizontal ? "barco_pa_h.png" : "barco_pa_v.png";
            case "AZ" -> horizontal ? "barco_az_h.png" : "barco_az_v.png";
            case "SM" -> horizontal ? "barco_sm_h.png" : "barco_sm_v.png";
            case "DT" -> horizontal ? "barco_dt_h.png" : "barco_dt_v.png";
            default -> null;
        };
    }

    private ImageIcon escalar(ImageIcon icon, int w, int h) {
        if (icon == null) return null;
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private ImageIcon cargarIcono(String nombreArchivo) {
        if (nombreArchivo == null) return null;
        try {
            URL url = getClass().getResource("/navalstrikep/imagenes/" + nombreArchivo);
            if (url == null) return null;
            return new ImageIcon(url);
        } catch (Exception e) {
            return null;
        }
    }


    private String extraerNombreColocado(String mensaje) {
        if (mensaje == null) return "";
        if (!mensaje.startsWith("Colocado:")) return "";
        String m = mensaje.replace("Colocado:", "").trim();
        int p = m.indexOf("(");
        if (p > 0) return m.substring(0, p).trim();
        // si no encuentra, devolver lo que haya
        return m.trim();
    }
}
