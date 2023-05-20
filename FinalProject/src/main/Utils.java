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
public class Utils {
    static public double easeInOut(float a, float b, float percent) {
        return a + (b - a) * ((-Math.cos(percent * Math.PI) / 2) + 0.5);
    }
    
    public static float uniform(float min, float max) {
        return (float) (Math.random() * (max-min) + min);
    }
    
    static public boolean overlap(double position1, double width1, double position2, double width2) {
        double min1 = position1 - width1/2;
        double max1 = position1 + width1/2;
        double min2 = position2 - width2/2;
        double max2 = position2 + width2/2;
        return !(max1 < min2 || max2 < min1);
    }
}
