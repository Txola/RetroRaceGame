/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author txola
 */
public class Camera {
    private Coordinate3D position;
    private float distanceToPlane;
    private int distanceToPlayer;

    public Camera() {
        position = new Coordinate3D(0, 1200, -300);
        distanceToPlane = -position.z / (float) position.y;
        distanceToPlayer = -position.z;
    }
    public void increase(int dz) {
        position.z += dz;
    }
    public void increasX(int dx) {
        position.x += dx;
    }
    
    public void restart() {
        position.z = -distanceToPlayer;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Coordinate3D getPosition() {
        return position;
    }

    public void setPosition(Coordinate3D coordinate3D) {
        this.position = coordinate3D;
    }

    public float getDistanceToPlane() {
        return distanceToPlane;
    }

    public void setDistanceToPlane(float distanceToPlane) {
        this.distanceToPlane = distanceToPlane;
    }
    
     public int getDistanceToPlayer() {
        return distanceToPlayer;
    }

    public void setDistanceToPlayer(int distanceToPlayer) {
        this.distanceToPlayer = distanceToPlayer;
    }
//</editor-fold>
}
