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
public class KeyInputHandler implements KeyListener{
    public Boolean up, down, right, left, plus, minus;

    public KeyInputHandler() {
        this.up = false;
        this.down = false;
        this.right = false;
        this.left = false;
        this.plus = false;
        this.minus = false;
    }
    
    
    @Override
    public void keyTyped(KeyEvent evt) {
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        int key = evt.getKeyCode();
        
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
            case KeyEvent.VK_PLUS:
                plus = true;
                break;
            case KeyEvent.VK_MINUS:
                minus = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        int key = evt.getKeyCode();
        
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
            case KeyEvent.VK_PLUS:
                plus = false;
                break;
            case KeyEvent.VK_MINUS:
                minus = false;
                break;
        }
    }
    
}
