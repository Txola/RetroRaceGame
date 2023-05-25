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
public class KeyInputStatus {
    public Boolean up, right, left;

    public KeyInputStatus() {
        this.up = false;
        this.right = false;
        this.left = false;
    }
    
        public void updateState(String state) {
        String[] parts = state.split(" ");
        
        if (parts[0].equals("true"))
            up = true;
        else 
            up = false;
        
        if (parts[1].equals("true"))
            left = true;
        else 
            left = false;
        
        if (parts[2].equals("true"))
            right = true;
        else 
            right = false;
    }
    
    @Override
    public String toString() {
        return (up + " " + left + " " + right);
    }
    
    public void restart() {
        up = right = left = false;
    }
}
