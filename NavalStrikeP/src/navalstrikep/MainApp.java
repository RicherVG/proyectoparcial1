package navalstrikep;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            ControlUsuarios control = new ControlUsuarios();
            new LoginPantalla(control).mostrar();
        });
    }
}
