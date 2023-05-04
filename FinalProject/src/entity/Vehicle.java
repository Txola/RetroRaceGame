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
import java.util.List;
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

    

    public Vehicle(Coordinate3D position, float maxSpeed, Circuit circuit, Image image) {
        super(position, circuit, image);
        this.maxSpeed = maxSpeed;
        
    }

    
    public void restart() {
        getPosition().z = 0;
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

    private float getDistanceToVehicleInFront(Vehicle vehicle) {
        //System.out.println(this.isLooped() + ", " + this.getPosition().z + " - " + vehicle.isLooped() + ", " + vehicle.getPosition().z + ",   " + (vehicle.getPosition().z - this.getPosition().z));
        if (!this.isLooped() && vehicle.isLooped())
            return vehicle.getPosition().z + getCircuit().getRoadLength() - this.getPosition().z;
        return vehicle.getPosition().z - this.getPosition().z;
    }
    
    public void update(double dt, List<Vehicle> vehicles, Player player) {
        getPosition().z  += dt * speed;
        final int reactionSegments = 15;
        int vehicleIndex = vehicles.indexOf(this);
        
        
        if (getDistanceToVehicleInFront(player) < reactionSegments * getCircuit().getSegmentLenght() && getDistanceToVehicleInFront(player) > 0 && player.getSpeed() < this.getSpeed() && Utils.overlap((int) this.getPointX(), (float) (this.getImageWidth() * 1.1), (int) player.getPointX(), (float) (player.getImageWidth() * 1.1))) {
            int direction;
                if (player.getPosition().x > circuit.getRoadWidth() / 2.5)
                    direction = -1;
                else if (player.getPosition().x < -circuit.getRoadWidth() / 2.5)
                    direction = 1;
                else
                    direction = player.getPosition().x > this.getPosition().x ? -1 : 1;
                float step = (getDistanceToVehicleInFront(player) / getCircuit().getSegmentLenght());
                /*if (step > 4) step = 1;
                else step *= 2;*/
                float percent = (1 - getDistanceToVehicleInFront(player)) / reactionSegments * getCircuit().getSegmentLenght();
                float vel = (float) Utils.easeInOut(0, 3000, percent);
                System.out.println(vel);
                this.getPosition().x += direction * vel * dt;
        }
        
        
        if (vehicleIndex > 0) { //TODO : only when segments are visible
            int otherVehicleIndex = vehicleIndex - 1;
            //System.out.println(vehicles.get(vehicleIndex).getPosition().z + ", " + vehicles.get(otherVehicleIndex).getPosition().z);
            Vehicle otherVehicle = vehicles.get(otherVehicleIndex);
            //getDistanceToNextVehicle(otherVehicle) > reactionSegments * getCircuit().getSegmentLenght() || 
            while (otherVehicleIndex > 0 && getDistanceToVehicleInFront(otherVehicle) < reactionSegments * getCircuit().getSegmentLenght() && !Utils.overlap((int) this.getPointX(), (float) (this.getImageWidth() * 1.1), (int) otherVehicle.getPointX(), (float) (otherVehicle.getImageWidth() * 1.1))) {
                otherVehicle = vehicles.get(otherVehicleIndex - 1);
                otherVehicleIndex--;
            }
            
            
            
            //System.out.println(getDistanceToNextVehicle(otherVehicle) + ", " + reactionSegments * getCircuit().getSegmentLenght());
            if (otherVehicleIndex > 0 && getDistanceToVehicleInFront(otherVehicle) < reactionSegments * getCircuit().getSegmentLenght() && otherVehicle.getSpeed() < this.getSpeed()) {
                //System.out.println(getDistanceToNextVehicle(otherVehicle) + ", " + reactionSegments * getCircuit().getSegmentLenght());
                
                int direction;
                if (otherVehicle.getPosition().x > circuit.getRoadWidth() / 2.5)
                    direction = -1;
                else if (otherVehicle.getPosition().x < -circuit.getRoadWidth() / 2.5)
                    direction = 1;
                else
                    direction = otherVehicle.getPosition().x > this.getPosition().x ? -1 : 1;
                float step = (getDistanceToVehicleInFront(otherVehicle) / getCircuit().getSegmentLenght());
                /*if (step > 4) step = 1;
                else step *= 2;*/
                float percent = (1 - getDistanceToVehicleInFront(otherVehicle)) / reactionSegments * getCircuit().getSegmentLenght();
                float vel = (float) Utils.easeInOut(0, 3000, percent);
                System.out.println(vel);
                this.getPosition().x += direction * vel * dt;
            }
        }
        if (this.getPosition().x > getCircuit().getRoadWidth() - getCircuit().getRoadWidth() / 6) {
        this.getPosition().x -= (getCircuit().getRoadWidth() / 40);
        }
        if (this.getPosition().x < -getCircuit().getRoadWidth() + getCircuit().getRoadWidth() / 6) {
        this.getPosition().x += (getCircuit().getRoadWidth() / 40);
        }
    }
    
    @Override
    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        if (getPosition().z > circuit.getRoadLength()) {
            restart();
        }
        super.draw(g2, screenWidth, screenHeight, camera);
    }
}

  