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
public class Vehicle extends Entity {
    
    float maxSpeed;
    float speed;

    

    public Vehicle(Coordinate3D position, float maxSpeed, String image, int scale, Circuit circuit) {
        super(position, image, scale, circuit);
        this.maxSpeed = maxSpeed;
        
    }

    
    public Coordinate3D getPosition() {
        return position;
    }

    public void restart() {
        position.z = 0;
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
    
    public void update(double dt, int s) {
        position.z  += dt * maxSpeed / s;
    }
    
    @Override
    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        if (position.z > circuit.getRoadLength()) {
            restart();
        }
        super.draw(g2, screenWidth, screenHeight, camera);
    }
}

  