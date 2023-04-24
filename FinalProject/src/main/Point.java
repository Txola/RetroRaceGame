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
    private int xScreen, yScreen;
    private float xScale, yScale;
    

    public Point(Coordinate3D worldPosition) {
        this.worldPosition = worldPosition;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Coordinate3D getWorldPosition() {
        return worldPosition;
    }

    public void setWorldPosition(Coordinate3D worldPosition) {
        this.worldPosition = worldPosition;
    }

    public int getXScreen() {
        return xScreen;
    }

    public void setXScreen(int xScreen) {
        this.xScreen = xScreen;
    }

    public int getYScreen() {
        return yScreen;
    }

    public void setYScreen(int yScreen) {
        this.yScreen = yScreen;
    }

    public float getXScale() {
        return xScale;
    }

    public void setXScale(float xScale) {
        this.xScale = xScale;
    }

    public float getYScale() {
        return yScale;
    }

    public void setYScale(float yScale) {
        this.yScale = yScale;
    }
//</editor-fold>

    
    public void projectPoint(Camera camera, float offsetZ, float offsetX, float offsetY, int screenCenterX, int screenCenterY) {
        float scale = camera.getDistanceToPlane() / 
                (worldPosition.z - camera.getPosition().z + offsetZ);
        xScale = scale * screenCenterX;
        yScale = scale * screenCenterY;
        
        xScreen = Math.round(screenCenterX + 
                xScale * (worldPosition.x - offsetX - camera.getPosition().x));
        yScreen = Math.round(screenCenterY -
                yScale * (worldPosition.y - offsetY - camera.getPosition().y));
    }
    
}
