/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

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
public class Image {
    private BufferedImage bufferedImage;
    private String path;
    private float scale;
    private float hitBox;
    
    public Image(String path, float scale, float hitBox) {
        this.path = path;
        this.scale = scale;
        this.hitBox = hitBox;
        loadImage();
    }
    
    private final void loadImage() {
        try {
            this.bufferedImage = ImageIO.read(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public float getScale() {
        return scale;
    }

    public float getHitBox() {
        return hitBox;
    }
}
