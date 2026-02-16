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
import java.net.URL;
public class PanelFondo extends JPanel{
    private Image fondo;
    
    public PanelFondo(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url != null) {
            fondo = new ImageIcon(url).getImage();
        }
        setOpaque(false);
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
