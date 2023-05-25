/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//<editor-fold defaultstate="collapsed" desc="Imports">
import Gui.InfoPanel;
import Gui.MultiplayerInfoPanel;
import Gui.MultiplayerResultInfoDialog;
import Gui.MultiplayerWaitingInfoPanel;
import Gui.SoloInfoPanel;
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
    private final int RUMBLESTRIP_WIDTH = 400;
    private final int NUMBER_OF_SEGMENTS = 800;
    private final int SEGMENT_LENGTH = 250;
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
    private InfoPanel infoPanel;
    private int fps = 60;

    
    private boolean network, host;
    private ServerSocket hostSocket;
    private Socket joinSocket;
    private int lap;
    private float lapSeconds;
    private float fastestLap;
    private int numberOfLaps;
    private int oponentLap;

    public Camera getCamera() {
        return camera;
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public int getFPS() {
        return fps;
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

    public boolean isHost() {
        return host;
    }
    
    public GameFrame getGameFrame() {
        return gameFrame;
    }
    
    
    
    
    

    
    
    public GamePanel(GameFrame gameFrame, boolean network, boolean host, String name, int numberOfLaps) {
        if (network && host)
            this.numberOfLaps = numberOfLaps;
        this.gameFrame = gameFrame;
        this.network = network;
        this.host = host;
        this.lap = 0;
        this.lapSeconds = 0;
        this.fastestLap = -1;
        if (!network)
            infoPanel = new SoloInfoPanel(this);
        else
            infoPanel = new MultiplayerInfoPanel(gameFrame, this);
        setLayout(new BorderLayout());
        ((JPanel) infoPanel).setOpaque(false);
        add((JPanel) infoPanel, BorderLayout.NORTH);
        
        
        inputHandler = new KeyInputHandler();
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
                player = new Player(new Coordinate3D(-roadWidth / 3, 0, 0), (float) (SEGMENT_LENGTH * 33), keyInputStatus, circuit, ResourceManager.instance().get(0), false);
            else 
                player = new Player(new Coordinate3D( roadWidth / 3, 0, 0), (float) (SEGMENT_LENGTH * 33), keyInputStatus, circuit, ResourceManager.instance().get(0), false);
            if (!name.isEmpty() && name != null) player.setName(name);
        }
        else {
            player = new Player(new Coordinate3D(0, 0, 0), (float) ((SEGMENT_LENGTH * 33)), keyInputStatus, circuit, ResourceManager.instance().get(0), false);
        }
        
        camera = new Camera();
        camera.update(player.getPosition());
        
        if (!network || host) {
            loadVehicles();
            circuit.addSprites(sprites);
        }
        
        if (network) {
            oponentLap = 0;
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
        double targetFrameTime = timeUnitsPerSecond / fps;
        double deltaT = 1.0 / fps;
        double deltaTime = 0;
        double lastUpdateTime = System.nanoTime();
                
        long frameCounter = 0;
        long countStartTime = System.nanoTime();
        
        //<editor-fold defaultstate="collapsed" desc="singlePlayer loop">
        if (!network)
            sprites.add(player);
        while (!network && !Thread.interrupted()) {
            targetFrameTime = timeUnitsPerSecond / fps;
            deltaT = 1.0 / fps;
            long currentTime = System.nanoTime();
            deltaTime += (currentTime - lastUpdateTime) / targetFrameTime;

            lastUpdateTime = currentTime;

            if (deltaTime >= 1) {
                frameCounter++;
                if (!pause) {
                    keyInputStatus.updateState(inputHandler.toString());
                    update(deltaT);
                }
                repaint();
                deltaTime--;
            }
            if (System.nanoTime() - countStartTime >= timeUnitsPerSecond) {
                System.out.println("FPS: " + frameCounter);
                countStartTime = System.nanoTime();
                frameCounter = 0;

            }
        }
        
//</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="multiplayer">
        if (!network) {
            return;
        }
        
        
        
        try (
            DataInputStream inFromSocket = new DataInputStream(joinSocket.getInputStream());
            DataOutputStream outToSocket = new DataOutputStream(joinSocket.getOutputStream());) {
            String oponent, oponentName;
            String name = player.getName();
            if (name == null)
                name = " ";
            if (host) {
                sprites.remove(player);
                StringBuilder spritesString = new StringBuilder();
                synchronized (sprites) {
                    for (Entity sprite : sprites) {
                        spritesString.append(sprite + "\n");
                    }
                }
                outToSocket.writeUTF(spritesString.toString());
                outToSocket.writeUTF(player + "\n" + name + "\n" + numberOfLaps);
                String[] lines = inFromSocket.readUTF().split("\n");
                oponent = lines[0];
                oponentName = lines[1];

            }
            else {
                String entities = inFromSocket.readUTF();
                parseEntities(entities);
                outToSocket.writeUTF(player + "\n" + name);
                String[] lines = inFromSocket.readUTF().split("\n");
                oponent = lines[0];
                oponentName = lines[1];
                numberOfLaps = Integer.parseInt(lines[2]);
            }
            final String[] parts = oponent.split(" ");
            this.oponent = new Player(new Coordinate3D(
                    Float.parseFloat(parts[1]),
                    Float.parseFloat(parts[2]),
                    Float.parseFloat(parts[3])),
                    Float.parseFloat(parts[4]),
                    oponentKeyInputStatus,
                    circuit,
                    ResourceManager.instance().get(Integer.parseInt(parts[0])),
                    true);
            if (!oponentName.equals(" "))
                this.oponent.setName(oponentName);
            synchronized(sprites) {
                sprites.add(this.player);
                sprites.add(this.oponent);
            }
            ((MultiplayerInfoPanel) infoPanel).updateLapInfo(lap, numberOfLaps);
            
            int i = 3;
            while (i > 0) {
                ((MultiplayerInfoPanel) infoPanel).showNumber(i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                i--;
            }
            ((MultiplayerInfoPanel) infoPanel).changePanel();
            deltaTime = 0;
            lastUpdateTime = System.nanoTime();
            

            while (!Thread.interrupted()) {
                long currentTime = System.nanoTime();
                deltaTime += (currentTime - lastUpdateTime) / targetFrameTime;

                lastUpdateTime = currentTime;

                if (deltaTime >= 1) {
                    frameCounter++;
                    update(deltaT);

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
                    }
                    repaint();
                    deltaTime--;
                }
                if (System.nanoTime() - countStartTime >= timeUnitsPerSecond) {
                    System.out.println("FPS: " + frameCounter);
                    countStartTime = System.nanoTime();
                    frameCounter = 0;

                }
            }

        } catch (IOException ex) {
            try {
                joinSocket.close();
                if (host) hostSocket.close();
            } catch (IOException ex1) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
//</editor-fold>

    }
    
    private void update(double dt) {
        boolean network = this.network; //Just in case client joins while updating
        Segment playerSegment = circuit.getCurrentSegment(camera.getPosition().z + camera.getDistanceToPlayer());
        final float prop = (float) 1;
        final float prop2 = (float) 2.5;
        if (!lastSegment.equals(playerSegment)) {
           backgroundCity.updateImageOffset((int) (float) (-playerSegment.getCurve()*prop));
           backgroundSky.updateImageOffset((int) (float) (-playerSegment.getCurve()/prop2));
        }
        
        backgroundCity.updateOffset((int) (-playerSegment.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())*prop));
        backgroundSky.updateOffset((int) (-playerSegment.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())/prop2));
        
        
        
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
        
        
        float dx = roadWidth / (1 * fps);
        float playerLastPosition = player.getPosition().z;
        float oponentLastPosition = circuit.getRoadLength();
        if (network)
            oponentLastPosition = oponent.getPosition().z;
        player.update(dt, dx, sprites);
        player.updateX(playerSegment.getCurve(), dx);
        if (network) {
            Segment oponentSegment = circuit.getCurrentSegment(oponent.getPosition().z);
            oponent.update(dt, dx, sprites);
            oponent.updateX(oponentSegment.getCurve(), dx);
        }
        
        
        
        
        camera.update(player.getPosition());
        if (camera.getPosition().z >= circuit.getRoadLength() - camera.getDistanceToPlayer()) {
            camera.restart();
            player.getPosition().z = 0;
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
        
        //<editor-fold defaultstate="collapsed" desc="GUI update">
        if (network)System.out.println(oponentLastPosition + " "+oponent.getMaxSpeed() + " " + oponent.getPosition().z);
        if (network && (oponentLastPosition - oponent.getMaxSpeed() > oponent.getPosition().z)) {
            oponentLap++;
        }
        if (playerLastPosition - player.getMaxSpeed()> player.getPosition().z) {
            lap++;
            if (fastestLap < 0 || lapSeconds < fastestLap) {
                fastestLap = lapSeconds;
                infoPanel.updateFastestLapCounter();
            }
            if (network)
                ((MultiplayerInfoPanel) infoPanel).updateLapInfo(lap, numberOfLaps);
            lapSeconds = 0;
        }
        else {
            lapSeconds += 1.0 / fps;
        }
        if (network) {
            if (this.lap == numberOfLaps) {
                MultiplayerResultInfoDialog infoDialog = new MultiplayerResultInfoDialog(gameFrame, this, true);
                infoDialog.setModalityType(ModalityType.APPLICATION_MODAL);
                infoDialog.setLocationRelativeTo(this);
                infoDialog.setVisible(true);
            }
            if (this.oponentLap == numberOfLaps) {
                MultiplayerResultInfoDialog infoDialog = new MultiplayerResultInfoDialog(gameFrame, this, false);
                infoDialog.setModalityType(ModalityType.APPLICATION_MODAL);
                infoDialog.setLocationRelativeTo(this);
                infoDialog.setVisible(true);
            }
            boolean ahead = (lap > oponentLap ||
                    (lap == oponentLap && player.getPosition().z >= oponent.getPosition().z));
            
            ((MultiplayerInfoPanel) infoPanel).updatePosition(ahead);
        }
        infoPanel.update();
//</editor-fold>
    }
    
    private void initMultiplayer() {
        Thread accept = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    hostSocket = new ServerSocket(10000);
                    joinSocket = hostSocket.accept();
                    lapSeconds = fastestLap = lap = 0;
                    player.getPosition().z = 0;
                    player.getPosition().x = -roadWidth / 3;
                    keyInputStatus.restart();
                    player.setSpeed(0);
                    network = true;
                } catch (IOException ex) {
                    Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        if (host) {
            network = false;
            accept.start();
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
        pause = !pause;
    }
    private void loadVehicles() {
        final float maxSpeed = (float) (SEGMENT_LENGTH * 24);
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
            if (oponent.getPosition().z == player.getPosition().z && !host)
                this.vehicles.get(i).getPosition().x = Float.parseFloat(lines[i]);
            
            else if (oponent.getPosition().z > player.getPosition().z &&
                    (vehicle.getPosition().z > oponent.getPosition().z ||
                     vehicle.getPosition().z < player.getPosition().z)) {
                this.vehicles.get(i).getPosition().x = Float.parseFloat(lines[i]);
            }
            else if (oponent.getPosition().z < player.getPosition().z && 
                    (vehicle.getPosition().z >= oponent.getPosition().z &&
                    vehicle.getPosition().z < player.getPosition().z)) {
                this.vehicles.get(i).getPosition().x = Float.parseFloat(lines[i]);
            }
        }
    }
    
    public void finish() {
        if (network) {
            try {
                joinSocket.close();
                if (host) hostSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);                
            }
        }
        gameThread.interrupt();
    }
    
    public void initPauseDialog() {
        PauseMenuDialog pauseDialog = new PauseMenuDialog(gameFrame, this);
        pauseDialog.setModalityType(ModalityType.APPLICATION_MODAL);
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setVisible(true);
    }
}