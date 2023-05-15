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
    private float distanceToPlayer;

    public Camera() {
        position = new Coordinate3D(0, 1200, -1000);
        distanceToPlayer = -position.z;
        distanceToPlane = distanceToPlayer / position.y;
    }
    public void increase(int dz) {
        position.z += dz;
    }
    public void increasX(int dx) {
        position.x += dx;
    }
    
    public void updateHeight(float y) {
        System.out.println(position.y);
        float dy = y - position.y;
        position.y = y;
        float dz = (position.y * distanceToPlayer) / (position.y - dy) - distanceToPlayer;
        updateDepth(dz);
    }
    
    public void updateFieldOfView(float z) {
        float dz = z - distanceToPlayer;
        position.z -= dz;
        distanceToPlayer += dz;
        distanceToPlane = distanceToPlayer / position.y;
    }
    
    public void updateDistanceToPlayer(float z) {
        float dz = z - distanceToPlayer;
        position.z -= dz;
        distanceToPlayer += dz;
    }
    
    private void updateDepth(float dz) {
        distanceToPlayer += dz;
        position.z -=dz;
    }
    
    public void update(Coordinate3D playerPosition) {
        position.x = playerPosition.x;
        position.z = playerPosition.z - distanceToPlayer;

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
    
     public float getDistanceToPlayer() {
        return distanceToPlayer;
    }

    public void setDistanceToPlayer(int distanceToPlayer) {
        this.distanceToPlayer = distanceToPlayer;
    }
//</editor-fold>
}
