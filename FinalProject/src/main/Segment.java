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
public class Segment {
    private Coordinate3D point1;
    private Coordinate3D point2;
    private float curve;
    public float offsetX1, offsetX2;
    public float maxy;

    public Segment(Coordinate3D point1, Coordinate3D point2, float curve) {
        this.point1 = point1;
        this.point2 = point2;
        this.curve = curve;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Coordinate3D getPoint1() {
        return point1;
    }
    
    public void setPoint1(Coordinate3D point1) {
        this.point1 = point1;
    }
    
    public Coordinate3D getPoint2() {
        return point2;
    }
    
    public void setPoint2(Coordinate3D point2) {
        this.point2 = point2;
    }
    
//</editor-fold>

    public float getCurve() {
        return curve;
    }
    
    public float getSegmentPercent(float z) {
        return (z - point1.z) / (point2.z -point1.z);
    }
    
    public float getYOffset(float z) {
        return point1.y + getSegmentPercent(z) * (point2.y - point1.y);
    }

    public float getXOffset(float z) {
        System.out.println("-------\n" + offsetX1 + ", " + offsetX2 + "\n ------");
        return offsetX1 + getSegmentPercent(z) * (offsetX2 - offsetX1);
    }

    
}
