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
        if (!this.isLooped() && vehicle.isLooped())
            return vehicle.getPosition().z + getCircuit().getRoadLength() - this.getPosition().z;
        return vehicle.getPosition().z - this.getPosition().z;
    }
    
    private void avoidVehicle(Vehicle vehicle, float reactionDistance, double dt) {
        int direction;
        if (vehicle.getPosition().x > circuit.getRoadWidth() / 2.5)
            direction = -1;
        else if (vehicle.getPosition().x < -circuit.getRoadWidth() / 2.5)
            direction = 1;
        else
            direction = vehicle.getPosition().x > this.getPosition().x ? -1 : 1;

        float percent = (reactionDistance - getDistanceToVehicleInFront(vehicle)) / reactionDistance;
        float vel = (float) Utils.easeInOut(0, (int) (700000 * dt),
                percent) * ((speed - vehicle.getSpeed()) / maxSpeed);
        this.getPosition().x += direction * vel * dt;
    }
    
      
    public void update(double dt, List<Vehicle> vehicles, Player player) {
        getPosition().z  += dt * speed;
        if (getPosition().z > circuit.getRoadLength())
            getPosition().z -= circuit.getRoadLength();
        
        
        final int reactionDistance = 15 * getCircuit().getSegmentLenght();
        int vehicleIndex = vehicles.indexOf(this);
        
        if (getDistanceToVehicleInFront(player) < reactionDistance && getDistanceToVehicleInFront(player) > 0 && player.getSpeed() < this.getSpeed() && Utils.overlap(this.getPointX(), this.getImageWidth() * 1.1, player.getPointX(), player.getImageWidth() * 1.1)) {
            avoidVehicle(player, reactionDistance, dt);
        }
        
        if (vehicleIndex > 0) {
            int otherVehicleIndex = vehicleIndex - 1;
            Vehicle otherVehicle = vehicles.get(otherVehicleIndex);
            while (otherVehicleIndex > 0 && getDistanceToVehicleInFront(otherVehicle) < reactionDistance && ! Utils.overlap(this.getPointX(), this.getImageWidth() * 1.1, otherVehicle.getPointX(), otherVehicle.getImageWidth() * 1.1)) {
                otherVehicle = vehicles.get(otherVehicleIndex - 1);
                otherVehicleIndex--;
            }

            if (otherVehicleIndex > 0 && getDistanceToVehicleInFront(otherVehicle) < reactionDistance && otherVehicle.getSpeed() < this.getSpeed()) {
                avoidVehicle(otherVehicle, reactionDistance, dt);
            }
        }
        if (this.getPosition().x > getCircuit().getRoadWidth() - getCircuit().getRoadWidth() / 6) {
            this.getPosition().x -= (getCircuit().getRoadWidth() * 3 * dt);
        }
        if (this.getPosition().x < -getCircuit().getRoadWidth() + getCircuit().getRoadWidth() / 6) {
            this.getPosition().x += (getCircuit().getRoadWidth() * 3 * dt);
        }
    }
    
    
    
    @Override
    public String toString() {
        return (super.toString() + " " + maxSpeed + " " + speed);
    }
}

  