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
import main.*;

/**
 *
 * @author txola
 */
public class Vehicle {
    Coordinate3D position;
    float maxSpeed;
    float speed;
    BufferedImage image;
    final int scale;

    public Vehicle(Coordinate3D position, float maxSpeed, String image, int scale) {
        this.position = position;
        this.maxSpeed = maxSpeed;
        this.scale = scale;
        loadImage(image);
    }

    private final void loadImage(String image) {
        try {
            this.image = ImageIO.read(new File(image));
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

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setMaxSpeed(float speed) {
        this.maxSpeed = speed;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        Point point = new Point(position);
        point.projectPoint(camera, 0, 0, 0, screenWidth / 2, screenHeight / 2);
        float xScale = point.getXScale();
        float yScale = point.getYScale();
        int imageWidth = (int) (image.getWidth() * scale * xScale);
        int imageHeight = (int) (image.getHeight() * scale * yScale);
        g2.drawImage(image, screenWidth / 2 - imageWidth / 2, 
                screenHeight - imageHeight, imageWidth, imageHeight, null);
    }
}
