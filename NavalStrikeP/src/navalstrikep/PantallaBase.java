/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package navalstrikep;

/**
 *
 * @author riche
 */
import javax.swing.*;
import java.awt.*;
public abstract class PantallaBase {
    protected JFrame ventana;
    protected ControlUsuarios control;
    
    public PantallaBase(ControlUsuarios control) {
         if (control == null) throw new IllegalArgumentException("ControlUsuarios no puede ser null");
        this.control = control;
    }
    
    public final void mostrar() {
        SwingUtilities.invokeLater(() -> {
        try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        ventana = new JFrame(getTituloVentana());
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.setSize(getTamanoVentana());
        ventana.setLocationRelativeTo(null);
        ventana.setContentPane(crearContenido());
        ventana.setVisible(true);

         } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            null,
            "Error al iniciar la app: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
            );
        }
     });

    }
    
    protected abstract String getTituloVentana();
    protected abstract Dimension getTamanoVentana();
    protected abstract JPanel crearContenido();

    protected final void cambiarAPantalla(PantallaBase otra) {
        if (ventana != null) ventana.dispose();
        otra.mostrar();
    }
}
