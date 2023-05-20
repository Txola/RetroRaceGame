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
public class KeyInputHandler extends KeyInputStatus implements KeyListener {
    boolean arrows;
    public KeyInputHandler(boolean arrows) {
        super();
        this.arrows = arrows;
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (arrows) {
            switch(e.getKeyCode()) {
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
        else {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_W:
                    up = true;
                    break;
                case KeyEvent.VK_S:
                    down = true;
                    break;
                case KeyEvent.VK_A:
                    left = true;
                    break;
                case KeyEvent.VK_D:
                    right = true;
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (arrows) {
            switch(e.getKeyCode()) {
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
        else {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_W:
                    up = false;
                    break;
                case KeyEvent.VK_S:
                    down = false;
                    break;
                case KeyEvent.VK_A:
                    left = false;
                    break;
                case KeyEvent.VK_D:
                    right = false;
                    break;
            }
        }
    }
      
}
