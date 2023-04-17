/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import entity.Background;
import entity.Player;
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
    final int ROAD_WIDTH = 2500;
    final int RUMBLESTRIP_WIDTH = 400;
    final int NUMBER_OF_SEGMENTS = 600;
    final int SEGMENT_LENGTH = 200;
    
    
    private Thread gameThread;
    private KeyInputHandler keyInput;
    private Circuit circuit;
    private Camera camera;
    private Player player;
    private Background background;
    
    float x = 350;
    float y = 600;
    float speed = 160;
    
    public GamePanel() {
        setBackground(Color.WHITE); //QUITAR LUEGO
        circuit = new Circuit(ROAD_WIDTH, RUMBLESTRIP_WIDTH, SEGMENT_LENGTH, NUMBER_OF_SEGMENTS);
        camera = new Camera();
        keyInput = new KeyInputHandler();
        addKeyListener(keyInput);
        setFocusable(true);
        float maxSpeed = (float) (SEGMENT_LENGTH * FRAMES_PER_SECOND) - 1;
        player = new Player(new Coordinate3D(0, 0, 0), maxSpeed, "src/resources/boceto.png", 25, keyInput);
        background = new Background();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(21,205,212));
        g2.fillRect(0, 0, getWidth(), getHeight());
        //background.draw(g2);
        circuit.renderCircuit(g2, camera, getWidth(), getHeight());
        player.draw(g2, getWidth(), getHeight(), camera);
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
                update(deltaT);
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
    
    private void update(double dt) {
        
        if (keyInput.plus) {
            camera.updateHeight(25);
        }
        if (keyInput.minus) {
            camera.updateHeight(-25);
        }
        Segment s = circuit.getCurrentSegment(camera);
        player.updateX(s.getCurve());
        float dx = ROAD_WIDTH / (1 * FRAMES_PER_SECOND);
        player.update(dt, dx);
        camera.update(player.getPosition());
        if (camera.getPosition().z >= circuit.getRoadLength() - camera.getDistanceToPlayer()) {
            camera.restart();
            player.restart();
        }
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




