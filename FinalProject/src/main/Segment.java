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
    private Point point1;
    private Point point2;
    private float curve;
    public float maxy;

    public Segment(Point point1, Point point2, float curve) {
        this.point1 = point1;
        this.point2 = point2;
        this.curve = curve;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">

    public Point getPoint1() {
        return point1;
    }

    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    public float getMaxy() {
        return maxy;
    }

    public void setMaxy(float maxy) {
        this.maxy = maxy;
    }
    
    public float getCurve() {
        return curve;
    }
    
    public void setCurve(float curve) {
        this.curve = curve;
    }
//</editor-fold>

    

    
    
    public float getSegmentPercent(float z) {
        return (z - point1.getWorldPosition().z) /
                (point2.getWorldPosition().z -point1.getWorldPosition().z);
    }
    
    public float getYOffset(float z) {
        return point1.getWorldPosition().y + getSegmentPercent(z) *
                (point2.getWorldPosition().y - point1.getWorldPosition().y);
    }


    
}
