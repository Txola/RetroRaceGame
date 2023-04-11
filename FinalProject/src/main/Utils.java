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
    static public double easeInOut(int a, int b, float percent) {
        if (percent > 0) {
            System.out.println(percent);
        }
        return a + (b - a) * ((-Math.cos(percent * Math.PI) / 2) + 0.5);
    }
}
