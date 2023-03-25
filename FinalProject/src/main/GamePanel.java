/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
/**
 *
 * @author txola
 */
public class GamePanel extends JPanel implements Runnable{
    final int framesPerSecond = 120;
    private Thread gameThread;
    private KeyInputHandler keyInput;
    
    float x = 100;
    float y = 100;
    float speed = 200;
    
    public GamePanel() {
        setBackground(Color.WHITE);
        keyInput = new KeyInputHandler();
        addKeyListener(keyInput);
        setFocusable(true);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect((int) x, (int) y, 50, 50);
        g2.dispose();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override
    public void run() {
        final int timeUnitsPerSecond = 1000000000;
        final double targetFrameTime = timeUnitsPerSecond / framesPerSecond;
        final double deltaT = targetFrameTime / timeUnitsPerSecond;
        double deltaTime = 0;
        double lastUpdateTime = System.nanoTime();
                
        long frameCounter = 0;
        long countStartTime = System.nanoTime();

        while (true) {    
            long currentTime = System.nanoTime();
            deltaTime += (currentTime - lastUpdateTime) / targetFrameTime;
            
            lastUpdateTime = currentTime;
            
            if (deltaTime >= 1) {
                frameCounter++;
                //LOGIC HERE-------
                if (keyInput.up) {
                    y -= speed * deltaT;
                }
                if (keyInput.down) {
                    y += speed * deltaT;
                }
                if (keyInput.left) {
                    x -= speed * deltaT;
                }
                if (keyInput.right) {
                    x += speed * deltaT;
                }
                repaint();
                //--------------
                deltaTime--;
            }
            if (System.nanoTime() - countStartTime >= timeUnitsPerSecond) {
                System.out.println("FPS: " + frameCounter);
                countStartTime = System.nanoTime();
                frameCounter = 0;
                        
            }
        }
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




