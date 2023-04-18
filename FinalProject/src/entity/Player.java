/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.Camera;
import main.Coordinate3D;
import main.KeyInputHandler;
import main.Circuit;
import main.Point;
import main.Segment;

/**
 *
 * @author txola
 */
public class Player extends Vehicle{
    KeyInputHandler input;

    public Player(Coordinate3D position, float maxSpeed, String image,
            int scale, KeyInputHandler input, Circuit circuit) {
        super(position, maxSpeed, image, scale, circuit);
        this.input = input;
        speed = 0;
    }
    
    
    
    public void update(double dt, float dx) {
        if (input.up) {
            speed += 30;
        }
        else {
            if (speed > 10) {
                speed -= 10;
            }
        }
        if (input.down) {
            speed -= 30;
        }
        if (input.left) {
            position.x -= dx;
        }
        if (input.right) {
            position.x += dx;
        }
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }
        position.z += speed * dt;
        
    }
    
    
    
    public void updateX(float dx) {
        if (speed > 0)
            position.x = (int) (position.x + dx * (speed / maxSpeed) * maxSpeed / 5 * 1.0 / 60 * 0.8);
    }
    
    @Override
    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        Point point = new Point(position);
        point.projectPoint(camera, 0, 0, 0, screenWidth / 2, screenHeight / 2);
        float xScale = point.getXScale();
        float yScale = point.getYScale();
        int imageWidth = (int) (image.getWidth() * scale * xScale);
        int imageHeight = (int) (image.getHeight() * scale * yScale);
        g2.drawImage(image, point.getXWorld() - imageWidth / 2, 
                point.getYWorld() - imageHeight, imageWidth, imageHeight, null);
    }
}
