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

    public Segment(Coordinate3D point1, Coordinate3D point2) {
        this.point1 = point1;
        this.point2 = point2;
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


    
}
