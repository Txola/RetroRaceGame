/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author txola
 */
public class KeyInputHandler {
    public Boolean up, down, right, left;

    public KeyInputHandler() {
        this.up = false;
        this.down = false;
        this.right = false;
        this.left = false;
    }
    
    public void updateKeyPressed(int key) {
        switch(key) {
            case KeyEvent.VK_UP:
                up = true;
                break;
            case KeyEvent.VK_DOWN:
                down = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
        }
    }
    
    public void updateKeyReleased(int key) {
        switch(key) {
            case KeyEvent.VK_UP:
                up = false;
                break;
            case KeyEvent.VK_DOWN:
                down = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
        }
    }
    
    public void updateState(String state) {
        String[] parts = state.split(" ");
        
        if (parts[0].equals("true"))
            up = true;
        else 
            up = false;
        
        if (parts[1].equals("true"))
            down = true;
        else 
            down = false;
        
        if (parts[2].equals("true"))
            left = true;
        else 
            left = false;
        
        if (parts[3].equals("true"))
            right = true;
        else 
            right = false;
    }
    
    @Override
    public String toString() {
        return (up + " " + down + " " + left + " " + right);
    }
      
}
