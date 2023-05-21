/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//<editor-fold defaultstate="collapsed" desc="Imports">
import Gui.GameInfoPanel;
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

public class GamePanel extends JPanel implements Runnable {
    final int FRAMES_PER_SECOND = 60;
    final int RUMBLESTRIP_WIDTH = 400;
    final int NUMBER_OF_SEGMENTS = 800;
    final int SEGMENT_LENGTH = 250;
    private int roadWidth = 2500;
    private GameFrame gameFrame;
    private boolean pause = true;
    private Thread gameThread;
    private KeyInputStatus keyInputStatus, oponentKeyInputStatus;
    private KeyInputHandler inputHandler;
    private Circuit circuit;
    private Camera camera;
    private Player player, oponent;
    private Background backgroundCity, backgroundSky;
    private Segment lastSegment;
    private List<Entity> sprites;
    private List<Vehicle> vehicles;
    private GameInfoPanel infoPanel;
    
    private boolean network, host;
    private ServerSocket hostSocket;
    private Socket joinSocket;
    private int lap;
    private float lapSeconds;
    private float fastestLap;

    public Camera getCamera() {
        return camera;
    }

    public int getFRAMES_PER_SECOND() {
        return FRAMES_PER_SECOND;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLap() {
        return lap;
    }

    public float getLapSeconds() {
        return lapSeconds;
    }

    public float getFastestLap() {
        return fastestLap;
    }
    
    

    
    
    public GamePanel(GameFrame gameFrame, boolean network, boolean host, boolean arrows) {
        this.gameFrame = gameFrame;
        this.network = network;
        this.host = host;
        this.lap = 0;
        this.lapSeconds = 0;
        this.fastestLap = -1;
        infoPanel = new GameInfoPanel(this);
        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.NORTH);
        /*if (!network) {
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
        }*/
        
        inputHandler = new KeyInputHandler(arrows);
        keyInputStatus = new KeyInputStatus();
        if (network) oponentKeyInputStatus = new KeyInputStatus();
        addKeyListener(inputHandler);
        setFocusable(true);
        requestFocus();
        circuit = new Circuit(roadWidth, RUMBLESTRIP_WIDTH, SEGMENT_LENGTH, NUMBER_OF_SEGMENTS, 450);
        backgroundCity = new Background("src/resources/city_2.png", 100);
        backgroundSky = new Background("src/resources/clouds.png", 0);
        lastSegment = circuit.getCurrentSegment(0);
        sprites = new ArrayList<>();
        vehicles = new ArrayList<>();

        if (network) {
            if (host) 
                player = new Player(new Coordinate3D(-roadWidth / 3, 0, 0), (float) (SEGMENT_LENGTH * 0.55 * FRAMES_PER_SECOND), keyInputStatus, circuit, ResourceManager.instance().get(0));
            else 
                player = new Player(new Coordinate3D( roadWidth / 3, 0, 0), (float) (SEGMENT_LENGTH * 0.55 * FRAMES_PER_SECOND), keyInputStatus, circuit, ResourceManager.instance().get(0));
        }
        else {
            player = new Player(new Coordinate3D(0, 0, 0), (float) (SEGMENT_LENGTH * 0.55 * FRAMES_PER_SECOND), keyInputStatus, circuit, ResourceManager.instance().get(0));
        }
        
        camera = new Camera();
        camera.update(player.getPosition());
        
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
        if (network && !host) {
            synchronized(sprites) {
                for (Entity sprite : sprites) {
                    sprite.updateLooped(camera.getPosition().z);
                }
            }
        }
        synchronized(sprites) {
            sprites.sort((v2, v1)-> Float.compare(v1.isLooped() ? v1.getPosition().z + circuit.getRoadLength(): v1.getPosition().z, v2.isLooped() ? v2.getPosition().z + circuit.getRoadLength(): v2.getPosition().z));   
        }
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
                        oponentKeyInputStatus,
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
                            keyInputStatus.updateState(inputHandler.toString());

                            if (host) {
                                StringBuilder spritesString = new StringBuilder();
                                for (Vehicle vehicle : vehicles) {
                                    spritesString.append(vehicle.getPosition().x + "\n");
                                }
                                spritesString.append(player + "\n");
                                spritesString.append(keyInputStatus);
                                outToSocket.writeUTF(spritesString.toString());
                                String lines[] = inFromSocket.readUTF().split("\n");
                                oponentKeyInputStatus.updateState(lines[vehicles.size() + 1]);
                                this.oponent.updateState(lines[vehicles.size()]);
                                updateVehicles(lines);

                            }
                            else {
                                StringBuilder spritesString = new StringBuilder();
                                for (Vehicle vehicle : vehicles) {
                                    spritesString.append(vehicle.getPosition().x + "\n");
                                }
                                spritesString.append(player + "\n" + keyInputStatus);
                                outToSocket.writeUTF(spritesString.toString());
                                String vehicles = inFromSocket.readUTF();
                                String[] lines = vehicles.split("\\n");
                                String oponentInputState = lines[this.vehicles.size() + 1];
                                oponentKeyInputStatus.updateState(oponentInputState);
                                this.oponent.updateState(lines[this.vehicles.size()]);
                                synchronized (vehicles) {
                                    for (Vehicle vehicle : this.vehicles) {
                                        vehicle.updateLooped(camera.getPosition().z);
                                    }
                                }
                                updateVehicles(lines);
                                
                            }

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
            sprites.add(player);
            while (true) {
                    long currentTime = System.nanoTime();
                    deltaTime += (currentTime - lastUpdateTime) / targetFrameTime;

                    lastUpdateTime = currentTime;

                    if (deltaTime >= 1) {
                        frameCounter++;
                        //LOGIC HERE-------
                        if (!pause) {
                            keyInputStatus.updateState(inputHandler.toString());
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
        float playerLastPosition = player.getPosition().z;
        player.update(dt, dx);
        player.updateX(playerSegment.getCurve(), dx);
        if (network) {
            Segment oponentSegment = circuit.getCurrentSegment(oponent.getPosition().z % circuit.getRoadLength());
            oponent.update(dt, dx);
            oponent.updateX(oponentSegment.getCurve(), dx);
        }
        
        
        if (network) {
            synchronized (vehicles) {
                for (Vehicle vehicle : vehicles) {
                    vehicle.updateLooped(camera.getPosition().z);
                }
            vehicles.sort((v2, v1)-> Float.compare(v1.isLooped() ? v1.getPosition().z + circuit.getRoadLength(): v1.getPosition().z, v2.isLooped() ? v2.getPosition().z + circuit.getRoadLength(): v2.getPosition().z));   
            }
        }
        
        synchronized(sprites) {
            for (Vehicle vehicle : vehicles) {
                if (!network) {
                    vehicle.update(dt, vehicles, player);
                }
                else {
                    vehicle.update(dt, vehicles, oponent);
                }
            }
        }
        
        camera.update(player.getPosition());
        if (camera.getPosition().z >= circuit.getRoadLength() - camera.getDistanceToPlayer()) {
            camera.restart();
            player.restart();
        }
  
        synchronized(sprites) {
            for (Entity sprite : sprites) {
                Segment vehicleSegment = circuit.getCurrentSegment(sprite.getPosition().z % circuit.getRoadLength());
                if (vehicleSegment == playerSegment) {
                    if ((!(sprite instanceof Vehicle) || player.getSpeed() > ((Vehicle) sprite).getSpeed()) && Utils.overlap(player.getPointX(), player.getImageWidth()* player.getImage().getHitBox(), sprite.getPointX(), sprite.getImageWidth() * sprite.getImage().getHitBox())) {
                        if (!(sprite instanceof Vehicle)) {
                            player.colidedWithSprite = true;
                            player.setSpeed(0);
                        }
                        else
                            player.setSpeed(((Vehicle) sprite).getSpeed()/6);
                    }
                }
            }
        }
        if (network && !host) {
            synchronized (vehicles) {
                for (Vehicle vehicle : vehicles) {
                    vehicle.updateLooped(oponent.getPosition().z - camera.getDistanceToPlayer());
                }
            }
        }
        if (network && host) {
            synchronized (vehicles) {
                for (Vehicle vehicle : vehicles) {
                    vehicle.updateLooped(player.getPosition().z - camera.getDistanceToPlayer());
                }
            }
        }
        
        synchronized(vehicles) {
            vehicles.sort((v2, v1)-> Float.compare(v1.isLooped() ? v1.getPosition().z + circuit.getRoadLength(): v1.getPosition().z, v2.isLooped() ? v2.getPosition().z + circuit.getRoadLength(): v2.getPosition().z));   
        }
        
        lastSegment = playerSegment;
        
        
        
        if (playerLastPosition - player.getMaxSpeed()> player.getPosition().z) {
            lap++;
            if (fastestLap < 0 || lapSeconds < fastestLap) {
                fastestLap = lapSeconds;
                infoPanel.updateFastestLapCounter();
            }
            lapSeconds = 0;
        }
        else {
            lapSeconds += 1.0 / FRAMES_PER_SECOND;
        }
        
        infoPanel.update();
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
        for (int i = 0; i < NUMBER_OF_SEGMENTS - frequency; i += frequency) {
        float z = Utils.uniform(i * SEGMENT_LENGTH, (i + frequency - minimumSeparation) *SEGMENT_LENGTH);
        float x = Utils.uniform(-roadWidth + roadWidth / 5, roadWidth - roadWidth / 5);
        Vehicle vehicle = new Vehicle(new Coordinate3D(x, 0, z), maxSpeed, circuit, ResourceManager.instance().getRandomVehicleImage());
        vehicles.add(vehicle);
        sprites.add(vehicle);
        }
        for (Vehicle vehicle : vehicles) {
        vehicle.setSpeed(maxSpeed * Utils.uniform((float) 0.5, 1));
        }
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
    
    private void updateVehicles(String[] lines) {
        for (int i = 0; i < this.vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            if (camera.getPosition().z > oponent.getPosition().z - camera.getDistanceToPlayer() && !(vehicle.isLooped() && vehicle.getPosition().z > oponent.getPosition().z))
                this.vehicles.get(i).getPosition().x = Float.parseFloat(lines[i]);
            if (camera.getPosition().z <= oponent.getPosition().z - camera.getDistanceToPlayer() && !vehicle.isLooped() && vehicle.getPosition().z > oponent.getPosition().z)
                this.vehicles.get(i).getPosition().x = Float.parseFloat(lines[i]);
        }
    }
    
    public void initPauseDialog() {
        PauseMenuDialog pauseDialog = new PauseMenuDialog(gameFrame, this);
        pauseDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setVisible(true);
    }

}