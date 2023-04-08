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
public class Point {
    private Coordinate3D worldPosition;
    private int xWorld, yWorld;
    private float xScale, yScale;
    

    public Point(Coordinate3D worldPosition) {
        this.worldPosition = worldPosition;
    }

    public int getXWorld() {
        return xWorld;
    }

    public int getYWorld() {
        return yWorld;
    }

    public float getXScale() {
        return xScale;
    }

    public float getYScale() {
        return yScale;
    }

    
    public void projectPoint(Camera camera, float offsetZ, float offsetX, int screenCenterX, int screenCenterY) {
        float scale = camera.getDistanceToPlane() / 
                (worldPosition.z - camera.getPosition().z + offsetZ);
        xScale = scale * screenCenterX;
        yScale = scale * screenCenterY;
        
        xWorld = Math.round(screenCenterX + 
                xScale * (worldPosition.x - offsetX - camera.getPosition().x));
        yWorld = Math.round(screenCenterY -
                yScale * (worldPosition.y - camera.getPosition().y));
    }
    
}
