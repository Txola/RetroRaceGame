/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.Coordinate3D;
import main.KeyInputHandler;

/**
 *
 * @author txola
 */
public class Player extends Vehicle{
    KeyInputHandler input;

    public Player(Coordinate3D position, float maxSpeed, BufferedImage image, KeyInputHandler input) {
        super(position, maxSpeed, image);
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
    
    public void restart() {
        position.z = 0;
    }
    
    public void updateX(float dx) {
        if (speed > 0)
            position.x = (int) (position.x + dx * (speed / maxSpeed) * maxSpeed / 5 * 1.0 / 60 * 0.6);
    }
    
    public void drawPlayer(Graphics2D g2) {
        super.draw(g2);
    }
}
