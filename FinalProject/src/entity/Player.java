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
import main.Camera;
import main.Coordinate3D;
import main.Circuit;
import main.KeyInputStatus;
import main.Point;
import main.Segment;

/**
 *
 * @author txola
 */
public class Player extends Vehicle{
    KeyInputStatus input;
    public boolean colidedWithSprite;
    String name;
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
        
        if (getPosition().z > circuit.getRoadLength())
            getPosition().z -= circuit.getRoadLength();
        
    }
    
    public void updateX(float curve, float dx) {
        float inc = (float) (curve * dx * (speed / maxSpeed)*(speed / maxSpeed) * 0.1);
        if (speed > 0 && !colidedWithSprite)
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
