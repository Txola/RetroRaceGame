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
    public boolean colidedWithSprite;

    public Player(Coordinate3D position, float maxSpeed, KeyInputHandler input, Circuit circuit, Image image) {
        super(position, maxSpeed, circuit, image);
        this.input = input;
        speed = 0;
    }
    
    
    
    public void update(double dt, float dx) {
        if (input.up && !colidedWithSprite) {
            speed += 30;
        }
        else {
            if (speed >= 15) {
                speed -= 15;
            }
        }
        if (input.down) {
            speed -= 50;
        }
        if (input.left) {
            getPosition().x -= colidedWithSprite ? dx * 0.1 : dx * (speed)/maxSpeed;
        }
        if (input.right) {
            getPosition().x += colidedWithSprite ? dx * 0.1 : dx * (speed)/maxSpeed;
        }
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }
        getPosition().z += speed * dt;
        if (Math.abs(getPosition().x) > getCircuit().getRoadWidth()) {
            //System.out.println("kalsdjfasjdfkñlasjdfasklñ");
            speed -= (speed / getMaxSpeed()) * 100;
            if (speed < 0) speed = 0;
        }
        
        if (colidedWithSprite)
            colidedWithSprite = false;
        
        
    }
    
    
    
    public void updateX(float curve, float dx) {
        float inc = (float) (curve * dx * (speed / maxSpeed)*(speed / maxSpeed) * 0.1);
        if (speed > 0 && !colidedWithSprite)
            getPosition().x += inc;
    }
    
    @Override
    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        Point point = new Point(getPosition());
        point.projectPoint(camera, 0, 0, 0, screenWidth / 2, screenHeight / 2);
        float xScale = point.getXScale();
        float yScale = point.getYScale();
        setPointX(point.getXWorld());
        setImageWidth((int) (getImage().getBufferedImage().getWidth() * getImage().getScale() * xScale));
        setImageHeight((int) (getImage().getBufferedImage().getHeight() * getImage().getScale() * yScale));
        g2.drawImage(getImage().getBufferedImage(), point.getXWorld() - getImageWidth() / 2, 
                point.getYWorld() - getImageHeight(), getImageWidth(), getImageHeight(), null);
    }
}
