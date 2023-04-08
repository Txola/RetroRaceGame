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
import main.Coordinate3D;

/**
 *
 * @author txola
 */
public class Vehicle {
    Coordinate3D position;
    float maxSpeed;
    float speed;
    BufferedImage image;

    public Vehicle(Coordinate3D position, float maxSpeed, BufferedImage image) {
        this.position = position;
        this.maxSpeed = maxSpeed;
        loadImage();
    }

    private final void loadImage() {
        try {
            this.image = ImageIO.read(new File("src/resources/player_straight.png"));
        } catch (IOException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Coordinate3D getPosition() {
        return position;
    }

    public void setPosition(Coordinate3D position) {
        this.position = position;
    }

    public float getSpeed() {
        return maxSpeed;
    }

    public void setSpeed(float speed) {
        this.maxSpeed = speed;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public void draw(Graphics2D g2) {
        
        if (image == null) {
            g2.fillRect(350, 600, 300, 150);
            return;
        }
        g2.drawImage(image, 360, 615, 300, 150, null);
    }
}
