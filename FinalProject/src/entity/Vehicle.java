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
    
    private void avoidVehicle(Vehicle vehicle, float reactionDistance, double dt) {
        int direction;
        if (vehicle.getPosition().x > circuit.getRoadWidth() / 2.5)
            direction = -1;
        else if (vehicle.getPosition().x < -circuit.getRoadWidth() / 2.5)
            direction = 1;
        else
            direction = vehicle.getPosition().x > this.getPosition().x ? -1 : 1;

        float percent = (reactionDistance - getDistanceToVehicleInFront(vehicle)) / reactionDistance;
        //System.out.println(percent);
        float vel = (float) Utils.easeInOut(0, 12000, percent) * ((speed - vehicle.getSpeed()) / maxSpeed);
        this.getPosition().x += direction * vel * dt;
    }
    
    private void avoidPlayer(Player player, float reactionDistance, double dt) {
        if (getDistanceToVehicleInFront(player) < reactionDistance && getDistanceToVehicleInFront(player) > 0 && player.getSpeed() < this.getSpeed() && Utils.overlap(this.getPointX(), this.getImageWidth() * 1.1, player.getPointX(), player.getImageWidth() * 1.1)) {
            avoidVehicle(player, reactionDistance, dt);
        }
    }
    
    
    
    public void update(double dt, List<Vehicle> vehicles, Player player) {
        getPosition().z  += dt * speed;
        final int reactionDistance = 15 * getCircuit().getSegmentLenght();
        int vehicleIndex = vehicles.indexOf(this);
        
            //avoidPlayer(player, reactionDistance, dt);
        /*if (getDistanceToVehicleInFront(oponent) < reactionDistance && getDistanceToVehicleInFront(oponent) > 0 && oponent.getSpeed() < this.getSpeed() && Utils.overlap((int) this.getPointX(), (float) (this.getImageWidth() * 1.1), (int) oponent.getPointX(), (float) (oponent.getImageWidth() * 1.1))) {
        avoidVehicle(oponent, reactionDistance, dt);
        }*/
        
        if (vehicleIndex > 0) {
            int otherVehicleIndex = vehicleIndex - 1;
            //System.out.println(vehicles.get(vehicleIndex).getPosition().z + ", " + vehicles.get(otherVehicleIndex).getPosition().z);
            Vehicle otherVehicle = vehicles.get(otherVehicleIndex);
            //getDistanceToNextVehicle(otherVehicle) > reactionSegments * getCircuit().getSegmentLenght() || 
            while (otherVehicleIndex > 0 && getDistanceToVehicleInFront(otherVehicle) < reactionDistance && ! Utils.overlap(this.getPointX(), this.getImageWidth() * 1.1, otherVehicle.getPointX(), otherVehicle.getImageWidth() * 1.1)) {
                otherVehicle = vehicles.get(otherVehicleIndex - 1);
                otherVehicleIndex--;
            }



            //System.out.println(getDistanceToNextVehicle(otherVehicle) + ", " + reactionSegments * getCircuit().getSegmentLenght());
            if (otherVehicleIndex > 0 && getDistanceToVehicleInFront(otherVehicle) < reactionDistance && otherVehicle.getSpeed() < this.getSpeed()) {
                //System.out.println(getDistanceToNextVehicle(otherVehicle) + ", " + reactionSegments * getCircuit().getSegmentLenght());
                avoidVehicle(otherVehicle, reactionDistance, dt);
            }
        }
        if (this.getPosition().x > getCircuit().getRoadWidth() - getCircuit().getRoadWidth() / 6) {
            this.getPosition().x -= (getCircuit().getRoadWidth() / 20);
        }
        if (this.getPosition().x < -getCircuit().getRoadWidth() + getCircuit().getRoadWidth() / 6) {
            this.getPosition().x += (getCircuit().getRoadWidth() / 20);
        }
    }
    
    @Override
    public boolean draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        if (getPosition().z > circuit.getRoadLength()) {
            restart();
        }
        return super.draw(g2, screenWidth, screenHeight, camera);
    }
    
    @Override
    public String toString() {
        return (super.toString() + " " + maxSpeed + " " + speed);
    }
}

  