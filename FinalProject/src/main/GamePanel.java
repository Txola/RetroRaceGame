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
    final int FRAMES_PER_SECOND = 60;
    final int ROAD_WIDTH = 2000;
    final int RUMBLE_WIDTH = 400;
    final int NUMBER_OF_SEGMENTS = 1000;
    final int SEGMENT_LENGTH = 200;
    
    
    private Thread gameThread;
    private KeyInputHandler keyInput;
    private Circuit circuit;
    private Camera camera;
    
    float x = 100;
    float y = 100;
    float speed = 100;
    
    public GamePanel() {
        setBackground(Color.WHITE); //QUITAR LUEGO
        circuit = new Circuit(ROAD_WIDTH, RUMBLE_WIDTH, SEGMENT_LENGTH, NUMBER_OF_SEGMENTS);
        camera = new Camera();
        keyInput = new KeyInputHandler();
        addKeyListener(keyInput);
        setFocusable(true);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        circuit.renderCircuit(g2, camera, getWidth() / 2, getHeight() / 2);
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
        final double targetFrameTime = timeUnitsPerSecond / FRAMES_PER_SECOND;
        final double deltaT = 1.0 / FRAMES_PER_SECOND;
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
                camera.increase(10);
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




