/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author txola
 */
public class Background {
    private BufferedImage image;
    private int offset;

    public Background() {
        offset = 0;
        loadImage();
    }
    
    private final void loadImage() {
        try {
            this.image = ImageIO.read(new File("src/resources/city.png"));
        } catch (IOException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void draw(Graphics2D g2) {
        
        if (image == null) {
            g2.fillRect(0, 300, 700, 300);
            return;
        }
        g2.drawImage(image, 0, 100, 1024, 410, 100, 0, 600, 105, null);
        //g2.drawImage(image, 0, 170, 1400, 240, null);
    }

    
}
