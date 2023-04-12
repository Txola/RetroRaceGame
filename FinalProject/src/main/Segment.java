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
    
    public float getYOffset(float z) {
        if (z > 1100) {
            System.out.println("a");
        }
        float percent = (z - point1.z) / (point2.z -point1.z);
        System.out.println(percent);
       // float result = point1.
        float result =  (float) point1.y + (float) percent * (float) ((float) ((float)point2.y - (float)point1.y));
        System.out.println(result);
        return result;
    }


    
}
