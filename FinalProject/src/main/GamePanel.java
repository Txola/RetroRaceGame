/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import Gui.PauseMenuDialog;
import entity.Background;
import entity.Entity;
import entity.Image;
import entity.Player;
import entity.Vehicle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
/**
 *
 * @author txola
 */
public class GamePanel extends JPanel implements Runnable{
    final int FRAMES_PER_SECOND = 60;
    int roadWidth = 2500;
    final int RUMBLESTRIP_WIDTH = 400;
    final int NUMBER_OF_SEGMENTS = 800;
    final int SEGMENT_LENGTH = 250;
    private GameFrame gameFrame;
    private boolean pause = true;
    private Thread gameThread;
    private KeyInputHandler keyInput;
    private Circuit circuit;
    private Camera camera;
    private Player player;
    private Background backgroundCity, backgroundSky;
    private Segment lastSegment;
    private Vehicle vehicle, vehicle2;
    private List<Entity> sprites;
    private List<Vehicle> vehicles;
    private final Image[] images = {
        new Image("src/resources/car2.png", (float) 0.4, 1), //player car
        new Image("src/resources/mercedes.png", 1, 1),
        new Image("src/resources/subaruGris.png", (float) 2.3, 1),
        new Image("src/resources/azul.png", (float) 0.65, 1),
        new Image("src/resources/audi.png", (float) 0.55, 1),
        new Image("src/resources/kia.png", (float) 0.7, 1),
        new Image("src/resources/escarabajo.png", (float) 1.25, 1)
    };

    public Camera getCamera() {
        return camera;
    }

    public void updateRoadWidth(int roadWidth) {
        int dRoadWidth = roadWidth - this.roadWidth;
        this.roadWidth = roadWidth;
        circuit.setRoadWidth(roadWidth);
        synchronized (sprites) {
            for (Entity sprite : sprites)  {
                if (!(sprite instanceof Vehicle)) {
                    sprite.getPosition().x += sprite.getPosition().x > 0 ? dRoadWidth : -dRoadWidth;
                }
            }
        }
    }
    
    
    public void pauseOrResume() {
        pause = false;
        System.out.println("pause" + pause);
    }
    private void loadVehicles() {
        final float maxSpeed = (float) (SEGMENT_LENGTH * 0.4 * FRAMES_PER_SECOND);
        final int frequency = 15;
        final int minimumSeparation = 3;
        for (int i = 0; i < NUMBER_OF_SEGMENTS - frequency; i += frequency) {
            float z = Utils.uniform(i * SEGMENT_LENGTH, (i + frequency - minimumSeparation) *SEGMENT_LENGTH);
            float x = Utils.uniform(-roadWidth + roadWidth / 5, roadWidth - roadWidth / 5);
            Vehicle vehicle = new Vehicle(new Coordinate3D(x, 0, z), maxSpeed, circuit, images[(int) Utils.uniform(1, 7)]);
            vehicles.add(vehicle);
            sprites.add((Entity) vehicle);
        }
        for (Vehicle vehicle : vehicles) {
            vehicle.setSpeed(maxSpeed * Utils.uniform((float) 0.5, 1));
        }
    }
    
    private void initPauseDialog() {
        PauseMenuDialog pauseDialog = new PauseMenuDialog(gameFrame, this);
        pauseDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setVisible(true);
    }
    
    public GamePanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton settingsButton = new JButton();
        
        settingsButton.setPreferredSize(new Dimension(120, 40));
        settingsButton.setBackground(new Color(141, 141, 141));
        settingsButton.setIcon(new ImageIcon(getClass().getResource("/resources/pause2.png")));
        settingsButton.setText("Pause");
        settingsButton.setFocusPainted(false);
        settingsButton.setFocusable(false);
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pause = true;
                initPauseDialog();
            }
        });
        add(settingsButton);
        
        
        
        /*panel.add(settingsButton, BorderLayout.CENTER);
        panel.setOpaque(false);
        add(panel, BorderLayout.NORTH);*/
        
        keyInput = new KeyInputHandler();
        addKeyListener(keyInput);
        setFocusable(true);
        requestFocus();
        circuit = new Circuit(roadWidth, RUMBLESTRIP_WIDTH, SEGMENT_LENGTH, NUMBER_OF_SEGMENTS, 450);
        camera = new Camera();
        player = new Player(new Coordinate3D(0, 0, 0), (float) (SEGMENT_LENGTH * 0.55 * FRAMES_PER_SECOND), keyInput, circuit, images[0]);
        backgroundCity = new Background("src/resources/city_2.png", 100);
        backgroundSky = new Background("src/resources/clouds.png", 0);
        sprites = new ArrayList<>();
        vehicles = new ArrayList<>();
        loadVehicles();
        circuit.addSprites(sprites);
        System.out.println(sprites.size());
        sprites.add(player);
                System.out.println(sprites.size());
        lastSegment = circuit.getCurrentSegment(0);


    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(21,205,212));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(0,0,0));
        g2.fillRect(0, getHeight()/2, getWidth(), getHeight());
        backgroundSky.draw(g2, getWidth(), getHeight());
        backgroundCity.draw(g2, getWidth(), getHeight());
        
        circuit.renderCircuit(g2, camera, getWidth(), getHeight());
        synchronized(sprites) {
            sprites.forEach(vehicle -> vehicle.draw(g2, getWidth(), getHeight(), camera));
        }
        super.paintChildren(g);
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
                    repaint();
                    if (!pause) {
                        update(deltaT);
                        
                    }
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

        Segment s = circuit.getCurrentSegment(camera.getPosition().z + camera.getDistanceToPlayer());
        final float prop = (float) 1;
        final float prop2 = (float) 2.5;
        if (!lastSegment.equals(s)) {
           backgroundCity.updateImageOffset((int) (float) (-s.getCurve()*prop));
           backgroundSky.updateImageOffset((int) (float) (-s.getCurve()/prop2));
        }
        
        backgroundCity.updateOffset((int) (-s.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())*prop));
        backgroundSky.updateOffset((int) (-s.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())/prop2));
        float dx = roadWidth / (1 * FRAMES_PER_SECOND);
        player.update(dt, dx);
        player.updateX(s.getCurve(), dx);
        synchronized(sprites) {
            for (Vehicle vehicle : vehicles) {
                vehicle.update(dt, vehicles, player);
            }
        }
        synchronized(sprites) {
            sprites.sort((v2, v1)-> Float.compare(v1.isLooped() ? v1.getPosition().z + circuit.getRoadLength(): v1.getPosition().z, v2.isLooped() ? v2.getPosition().z + circuit.getRoadLength(): v2.getPosition().z));   
        }
        synchronized(vehicles) {
            vehicles.sort((v2, v1)-> Float.compare(v1.isLooped() ? v1.getPosition().z + circuit.getRoadLength(): v1.getPosition().z, v2.isLooped() ? v2.getPosition().z + circuit.getRoadLength(): v2.getPosition().z));   
        }
        
        camera.update(player.getPosition());
        if (camera.getPosition().z >= circuit.getRoadLength() - camera.getDistanceToPlayer()) {
            camera.restart();
            player.restart();
        }
        
        for (Entity sprite : sprites) {
            Segment vehicleSegment = circuit.getCurrentSegment(sprite.getPosition().z % circuit.getRoadLength());
                if (vehicleSegment == s) {

                    if ((!(sprite instanceof Vehicle) || player.getSpeed() > ((Vehicle) sprite).getSpeed()) && Utils.overlap(player.getPointX(), player.getImageWidth() * player.getImage().getHitBox(), sprite.getPointX(), sprite.getImageWidth() * sprite.getImage().getHitBox())) {
                        if (!(sprite instanceof Vehicle)) {
                            player.colidedWithSprite = true;
                            player.setSpeed(0);
                        }
                        else
                            
                            player.setSpeed(((Vehicle) sprite).getSpeed()/6);
                        //System.out.println("COLISION");
                }
            }
        }
        //System.out.println(player.imageWidth + ", "+ player.imageHeight);
        lastSegment = s;
    }
    
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




