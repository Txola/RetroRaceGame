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
    Coordinate3D coordinate3D;
    private float distanceToPlane;

    public Camera() {
        coordinate3D = new Coordinate3D(0, 1000, -100);
        distanceToPlane = -coordinate3D.z / (float) coordinate3D.y;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Coordinate3D getCoordinate3D() {
        return coordinate3D;
    }

    public void setCoordinate3D(Coordinate3D coordinate3D) {
        this.coordinate3D = coordinate3D;
    }

    public float getDistanceToPlane() {
        return distanceToPlane;
    }

    public void setDistanceToPlane(float distanceToPlane) {
        this.distanceToPlane = distanceToPlane;
    }
//</editor-fold>

}
