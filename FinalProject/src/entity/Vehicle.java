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
    Circuit circuit;
    public Coordinate3D position;
    float maxSpeed;
    float speed;
    BufferedImage image;
    final int scale;
    public boolean looped = false;

    public Vehicle(Coordinate3D position, float maxSpeed, String image, int scale, Circuit circuit) {
        this.position = position;
        this.circuit = circuit;
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
    
    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        if (position.z > circuit.getRoadLength()) {
            restart();
            looped = true;
        }
        else if (camera.getPosition().z < position.z) {
            looped = false;
        }
        int baseIndex = circuit.getCurrentSegmentIndex(camera.getPosition().z + camera.getDistanceToPlayer()); 
        int currentIndex = circuit.getCurrentSegmentIndex(position.z);
        if (baseIndex <= currentIndex && (currentIndex - baseIndex) < circuit.getNumberOfVisibleSegments()|| (looped && ((circuit.getNumberOfSegments() - baseIndex + currentIndex) < circuit.getNumberOfVisibleSegments()) ))  {
            Segment currentSegment = circuit.getCurrentSegment(position.z);
            Segment baseSegment = circuit.getCurrentSegment(camera.getPosition().z + (int) camera.getDistanceToPlayer());
            float offsetY = currentSegment.getYOffset(position.z) - baseSegment.getYOffset(camera.getPosition().z + camera.getDistanceToPlayer());
            Point point = new Point(position);
            point.projectPoint(camera, looped ? circuit.getRoadLength() : 0,
                    currentSegment.offsetX, -offsetY, screenWidth / 2, screenHeight / 2);
            float xScale = point.getXScale();
            float yScale = point.getYScale();
            int imageWidth = (int) (image.getWidth() * scale * xScale);
            int imageHeight = (int) (image.getHeight() * scale * yScale);
            System.out.println(currentSegment.maxy);
            if (currentSegment.maxy == 0) {
                looped = looped;
            }
            if (point.getYWorld() < currentSegment.maxy + imageHeight) {
                g2.drawImage(image, point.getXWorld() - imageWidth / 2, 
                    point.getYWorld() - imageHeight, imageWidth, imageHeight, null);
            }
        }
    }
}

  