/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//<editor-fold defaultstate="collapsed" desc="Imports">
import Gui.PauseMenuDialog;
import entity.Background;
import entity.Entity;
import entity.Image;
import entity.ResourceManager;
import entity.Player;
import entity.Vehicle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
//</editor-fold>

/**
 *
 * @author txola
 */

public class GamePanel extends JPanel implements Runnable, KeyListener {
    final int FRAMES_PER_SECOND = 60;
    final int RUMBLESTRIP_WIDTH = 400;
    final int NUMBER_OF_SEGMENTS = 800;
    final int SEGMENT_LENGTH = 250;
    private int roadWidth = 2500;
    private GameFrame gameFrame;
    private boolean pause = true;
    private Thread gameThread;
    private KeyInputHandler keyInput, oponentKeyInput;
    private Circuit circuit;
    private Camera camera;
    private Player player, oponent;
    private Background backgroundCity, backgroundSky;
    private Segment lastSegment;
    private Vehicle vehicle;
    private List<Entity> sprites;
    private List<Vehicle> vehicles;

    
    private boolean network, host;
    private ServerSocket hostSocket;
    private Socket joinSocket;


    public Camera getCamera() {
        return camera;
    }

    
    
    public GamePanel(GameFrame gameFrame, boolean network, boolean host) {
        this.gameFrame = gameFrame;
        this.network = network;
        this.host = host;
        if (!network) {
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
        }
        
        keyInput = new KeyInputHandler();
        if (network) oponentKeyInput = new KeyInputHandler();
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
        circuit = new Circuit(roadWidth, RUMBLESTRIP_WIDTH, SEGMENT_LENGTH, NUMBER_OF_SEGMENTS, 450);
        camera = new Camera();
        backgroundCity = new Background("src/resources/city_2.png", 100);
        backgroundSky = new Background("src/resources/clouds.png", 0);
        lastSegment = circuit.getCurrentSegment(0);
        sprites = new ArrayList<>();
        vehicles = new ArrayList<>();

        if (network) {
            if (host) 
                player = new Player(new Coordinate3D(-roadWidth / 3, 0, 0), (float) (SEGMENT_LENGTH * 0.55 * FRAMES_PER_SECOND), keyInput, circuit, ResourceManager.instance().get(0));
            else 
                player = new Player(new Coordinate3D(roadWidth / 3, 0, 0), (float) (SEGMENT_LENGTH * 0.55 * FRAMES_PER_SECOND), keyInput, circuit, ResourceManager.instance().get(0));
        }
        else {
            player = new Player(new Coordinate3D(0, 0, 0), (float) (SEGMENT_LENGTH * 0.55 * FRAMES_PER_SECOND), keyInput, circuit, ResourceManager.instance().get(0));
            sprites.add(player);
        }
        
        if (!network || host) {
            loadVehicles();
            circuit.addSprites(sprites);
            System.out.println(sprites.size());
        }
        if (network) {
            initMultiplayer();
            
        }

        gameThread = new Thread(this);
        gameThread.start();

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
    public void run() {
        final int timeUnitsPerSecond = 1000000000;
        final double targetFrameTime = timeUnitsPerSecond / FRAMES_PER_SECOND;
        final double deltaT = 1.0 / FRAMES_PER_SECOND;
        double deltaTime = 0;
        double lastUpdateTime = System.nanoTime();
                
        long frameCounter = 0;
        long countStartTime = System.nanoTime();
        
        if (network) {
            try (
                DataInputStream inFromSocket = new DataInputStream(joinSocket.getInputStream());
                DataOutputStream outToSocket = new DataOutputStream(joinSocket.getOutputStream());) {
                String oponent;
                if (host) {
                    StringBuilder spritesString = new StringBuilder();
                    for (Entity sprite : sprites) {
                        spritesString.append(sprite + "\n");
                        System.out.println(sprite + "\n");
                    }
                    outToSocket.writeUTF(spritesString.toString());
                    oponent = inFromSocket.readUTF();
                    outToSocket.writeUTF(player.toString());
                }
                else {
                    String entities = inFromSocket.readUTF();
                    parseEntities(entities);
                    outToSocket.writeUTF(player.toString());
                    oponent = inFromSocket.readUTF();
                }
                final String[] parts = oponent.split(" ");
                this.oponent = new Player(new Coordinate3D(
                        Float.parseFloat(parts[1]),
                        Float.parseFloat(parts[2]),
                        Float.parseFloat(parts[3])),
                        Float.parseFloat(parts[4]),
                        oponentKeyInput,
                        circuit,
                        ResourceManager.instance().get(Integer.parseInt(parts[0])));
                synchronized(sprites) {
                    sprites.add(player);
                    sprites.add(this.oponent);
                }
                
                while (true) {
                long currentTime = System.nanoTime();
                deltaTime += (currentTime - lastUpdateTime) / targetFrameTime;

                lastUpdateTime = currentTime;

                if (deltaTime >= 1) {
                    frameCounter++;
                    //LOGIC HERE-------
                    if (!pause) {
                        outToSocket.writeUTF(keyInput.toString());
                        String oponentInputState = inFromSocket.readUTF();
                        if(host) System.out.println("host: " +player.getPosition().x + " "+ player.getPosition().z);
                        else System.out.println("join: " + this.oponent.getPosition().x+ " "+ this.oponent.getPosition().z);
                        oponentKeyInput.updateState(oponentInputState);
                        update(deltaT);
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
                
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        else {
            while (true) {
                    long currentTime = System.nanoTime();
                    deltaTime += (currentTime - lastUpdateTime) / targetFrameTime;

                    lastUpdateTime = currentTime;

                    if (deltaTime >= 1) {
                        frameCounter++;
                        //LOGIC HERE-------
                        if (!pause) {
                            update(deltaT);
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
    
    private void update(double dt) {

        Segment playerSegment = circuit.getCurrentSegment(camera.getPosition().z + camera.getDistanceToPlayer());
        final float prop = (float) 1;
        final float prop2 = (float) 2.5;
        if (!lastSegment.equals(playerSegment)) {
           backgroundCity.updateImageOffset((int) (float) (-playerSegment.getCurve()*prop));
           backgroundSky.updateImageOffset((int) (float) (-playerSegment.getCurve()/prop2));
        }
        
        backgroundCity.updateOffset((int) (-playerSegment.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())*prop));
        backgroundSky.updateOffset((int) (-playerSegment.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())/prop2));
        
        float dx = roadWidth / (1 * FRAMES_PER_SECOND);
        
        player.update(dt, dx);
        player.updateX(playerSegment.getCurve(), dx);
        if (network) {
            Segment oponentSegment = circuit.getCurrentSegment(oponent.getPosition().z % circuit.getRoadLength());
            oponent.update(dt, dx);
            oponent.updateX(oponentSegment.getCurve(), dx);
        }
        
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
                if (vehicleSegment == playerSegment) {

                    if ((!(sprite instanceof Vehicle) || player.getSpeed() > ((Vehicle) sprite).getSpeed()) && Utils.overlap(player.getPointX(), player.getImageWidth() * player.getImage().getHitBox(), sprite.getPointX(), sprite.getImageWidth() * sprite.getImage().getHitBox())) {
                        if (!(sprite instanceof Vehicle)) {
                            player.colidedWithSprite = true;
                            player.setSpeed(0);
                        }
                        else
                            player.setSpeed(((Vehicle) sprite).getSpeed()/6);
                    }
                }
                if (network) {
                    Segment oponentSegment = circuit.getCurrentSegment(oponent.getPosition().z % circuit.getRoadLength());
                    if (vehicleSegment == oponentSegment) {

                    if ((!(sprite instanceof Vehicle) || oponent.getSpeed() > ((Vehicle) sprite).getSpeed()) && Utils.overlap(oponent.getPointX(), oponent.getImageWidth() * oponent.getImage().getHitBox(), sprite.getPointX(), sprite.getImageWidth() * sprite.getImage().getHitBox())) {
                        if (!(sprite instanceof Vehicle)) {
                            oponent.colidedWithSprite = true;
                            oponent.setSpeed(0);
                        }
                        else
                            oponent.setSpeed(((Vehicle) sprite).getSpeed()/6);
                    }
                }
            }
        }
        lastSegment = playerSegment;
    }
    
    private void initMultiplayer() {
        if (host) {
            try {
                hostSocket = new ServerSocket(10000);
                joinSocket = hostSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            try {
                joinSocket = new Socket("localhost", 10000);
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
    }
    private void loadVehicles() {
        final float maxSpeed = (float) (SEGMENT_LENGTH * 0.4 * FRAMES_PER_SECOND);
        final int frequency = 15;
        final int minimumSeparation = 3;
        /*for (int i = 0; i < NUMBER_OF_SEGMENTS - frequency; i += frequency) {
        float z = Utils.uniform(i * SEGMENT_LENGTH, (i + frequency - minimumSeparation) *SEGMENT_LENGTH);
        float x = Utils.uniform(-roadWidth + roadWidth / 5, roadWidth - roadWidth / 5);
        Vehicle vehicle = new Vehicle(new Coordinate3D(x, 0, z), maxSpeed, circuit, ResourceManager.instance().getRandomVehicleImage());
        vehicles.add(vehicle);
        sprites.add(vehicle);
        }
        for (Vehicle vehicle : vehicles) {
        vehicle.setSpeed(maxSpeed * Utils.uniform((float) 0.5, 1));
        }*/
    }
    
    private void parseEntities(String entities) {
        String[] lines = entities.split("\\n");
        for (String line : lines) {
            final String[] parts = line.split(" ");
            if (parts.length == 4) {
                synchronized(sprites) {
                    sprites.add(new Entity(new Coordinate3D(
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])),
                            circuit, ResourceManager.instance().get(Integer.parseInt(parts[0]))));
                }
            }
            else {
                Vehicle vehicle = new Vehicle(new Coordinate3D(
                        Float.parseFloat(parts[1]),
                        Float.parseFloat(parts[2]),
                        Float.parseFloat(parts[3])),
                        Float.parseFloat(parts[4]),
                        circuit,
                        ResourceManager.instance().get(Integer.parseInt(parts[0])));
                vehicle.setSpeed(Float.parseFloat(parts[5]));
                synchronized(sprites) {
                    sprites.add(vehicle);
                }
                synchronized(vehicles) {
                    vehicles.add(vehicle);
                }
            }
        }
    }
    
    private void initPauseDialog() {
        PauseMenuDialog pauseDialog = new PauseMenuDialog(gameFrame, this);
        pauseDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setVisible(true);
    }

//<editor-fold defaultstate="collapsed" desc="inputs">
    @Override
    public void keyTyped(KeyEvent arg0) {
    }
    
    @Override
    public void keyPressed(KeyEvent evt) {
        keyInput.updateKeyPressed(evt.getKeyCode());
    }
    
    @Override
    public void keyReleased(KeyEvent evt) {
        keyInput.updateKeyReleased(evt.getKeyCode());
    }
//</editor-fold>
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




