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
    private int imageOffset;
    private int offset = 0;
    private int yOffset;

    public Background(String path, int yOffset) {
        loadImage(path);
        imageOffset = image.getWidth() / 2;
    }
    
    private final void loadImage(String path) {
        try {
            this.image = ImageIO.read(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateImageOffset(int curve) {
        imageOffset += curve;
    }
    public void updateOffset(int curve) {
        offset = (int) curve;
    }
    
    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.drawImage(image, 0, yOffset, screenWidth, (int) (screenHeight / 1.8), imageOffset + offset, 0, image.getWidth() / 8 +imageOffset + offset, image.getHeight(), null);
    }

    
}
