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
    final int framesPerSecond = 30;
    private Thread gameThread;
    private KeyInputHandler keyInput;
    
    int x = 100;
    int y = 100;
    int speed = 2;
    
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
        g2.fillRect(x, y, 50, 50);
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
        final double delay = 1000 / framesPerSecond;
        long beforeTime = System.currentTimeMillis();
        long frameCounter = 0;
        long countStartTime = System.currentTimeMillis();

        while (true) {            
            //LOGIC HERE-------
            if (keyInput.up) {
                y -= speed;
            }
            if (keyInput.down) {
                y += speed;
            }
            if (keyInput.left) {
                x -= speed;
            }
            if (keyInput.right) {
                x += speed;
            }
            
            repaint();
            //---------------
            
            long remainingTime = (long) (delay - 
                    (System.currentTimeMillis()- beforeTime));
            if (remainingTime < 0) {
                remainingTime = 0;
            }
            try {
                Thread.sleep(remainingTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            beforeTime = System.currentTimeMillis();
            frameCounter++;
            if (frameCounter > 200) {
                float fps = frameCounter /
                        ((System.currentTimeMillis() - countStartTime) / 1000);
                System.out.println("FPS: " + fps);
                frameCounter = 0;
                countStartTime = System.currentTimeMillis();
            }
        }
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




