/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.awt.Color;
import java.awt.Font;
import static java.awt.Font.BOLD;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import main.Camera;
import main.Coordinate3D;
import main.Circuit;
import main.KeyInputStatus;
import main.Point;
import main.Segment;
import main.Utils;

/**
 *
 * @author txola
 */
public class Player extends Vehicle{
    private KeyInputStatus input;
    private boolean colided;
    private String name;
    boolean displayName;

    public Player(Coordinate3D position, float maxSpeed, KeyInputStatus input, Circuit circuit, Image image, boolean displayName) {
        super(position, maxSpeed, circuit, image);
        this.input = input;
        this.displayName = displayName;
        speed = 0;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void updateState(String state) {
        String[] parts = state.split(" ");
        getPosition().x = Float.parseFloat(parts[1]);
        getPosition().y = Float.parseFloat(parts[2]);
        getPosition().z = Float.parseFloat(parts[3]);
        speed = Float.parseFloat(parts[5]);
    }
    
    
    public void update(double dt, float dx, List<Entity> sprites) {
        if (input.up && !colided) {
            speed += 1800 * dt;
        }
        else {
            if (speed >= 900 * dt) {
                speed -= 900 * dt;
            }
            else
                speed = 0;
        }
        
        if (input.down) {
            if (speed >= 3000 * dt) {
                speed -= 3000 * dt;
            }
            else 
                speed = 0;
        }

        if (input.left) {
            getPosition().x -= colided ? dx * 0.1 : dx * (speed)/maxSpeed;
        }
        if (input.right) {
            getPosition().x += colided ? dx * 0.1 : dx * (speed)/maxSpeed;
        }
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }
        getPosition().z += speed * dt;
        
        if (Math.abs(getPosition().x) > getCircuit().getRoadWidth()) {
            speed -= (speed / getMaxSpeed()) * 6000 * dt;
            if (speed < 0) speed = 0;
        }
        
        if (colided)
            colided = false;
        
        Segment playerSegment = circuit.getCurrentSegment(this.getPosition().z % getCircuit().getRoadLength());
        synchronized(sprites) {
            for (Entity sprite : sprites) {
                if (sprite != this) {
                    Segment vehicleSegment = circuit.getCurrentSegment(sprite.getPosition().z % getCircuit().getRoadLength());
                    if (vehicleSegment == playerSegment) {

                        if ((!(sprite instanceof Vehicle) || getSpeed() >=
                                ((Vehicle) sprite).getSpeed()) && Utils.overlap(getPointX(),
                                getImageWidth()* getImage().getHitBox(),
                                sprite.getPointX(),
                                sprite.getImageWidth() * sprite.getImage().getHitBox())) {

                            if (!(sprite instanceof Vehicle)) {
                                this.colided = true;
                                this.setSpeed(0);
                            }
                            else {
                                this.setSpeed(((Vehicle) sprite).getSpeed() / 2);
                                if (speed == ((Vehicle) sprite).getSpeed())
                                    this.colided = true;
                            }

                            getPosition().z -= speed * dt;
                        }
                    }
                }
            }
        }
        
        
        if (getPosition().z > circuit.getRoadLength())
            getPosition().z -= circuit.getRoadLength();
        
    }
    
    public void updateX(float curve, float dx) {
        float inc = (float) (curve * dx * (speed / maxSpeed)*(speed / maxSpeed) * 0.11);
        if (speed > 0 && !colided)
            getPosition().x += inc;
    }
    
    @Override
    public boolean draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        boolean visible = super.draw(g2, screenWidth, screenHeight, camera);
        if (visible && name != null && displayName) {
            float scale = getImageWidth() / (getImage().getBufferedImage().getWidth() * getImage().getScale());
            g2.setFont(new Font("URW Gothic L", BOLD, (int) (120 * scale)));
            g2.setColor(new Color(255,255,255));
            int textLength = (int) g2.getFontMetrics().getStringBounds(name, g2).getWidth();
            int x = (int) (getPointX() - textLength/2);
            int y = (int) (getPointY() - getImageHeight() - 60 * scale);
            g2.drawString(name, x, y);
        }
        return visible;
    }
}
