/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import entity.Background;
import entity.Player;
import entity.Vehicle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
/**
 *
 * @author txola
 */
public class GamePanel extends JPanel implements Runnable{
    final int FRAMES_PER_SECOND = 60;
    final int ROAD_WIDTH = 2200;
    final int RUMBLESTRIP_WIDTH = 400;
    final int NUMBER_OF_SEGMENTS = 500;
    final int SEGMENT_LENGTH = 250;
    
    
    private Thread gameThread;
    private KeyInputHandler keyInput;
    private Circuit circuit;
    private Camera camera;
    private Player player;
    private Background background;
    private Vehicle vehicle, vehicle2;
    private List<Vehicle> vehicles;
    
    float x = 350;
    float y = 600;
    float speed = 160;
    
    private void loadVehicles() {
        float maxSpeed = (float) (SEGMENT_LENGTH * 0.7 * FRAMES_PER_SECOND);
        int numberOfCars = 45;
        for (int i = 0; i < numberOfCars; i++) {
            float z = Utils.uniform(0, circuit.getRoadLength());
            float x = Utils.uniform(-ROAD_WIDTH + ROAD_WIDTH / 5, ROAD_WIDTH - ROAD_WIDTH / 5);
            vehicles.add(new Vehicle(new Coordinate3D(x, 0, z), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        }
        /*vehicles.add(new Vehicle(new Coordinate3D(0, 0, 60000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(ROAD_WIDTH / 2, 0, 45000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(0, 0, 30000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(ROAD_WIDTH / 2, 0, 24000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(0, 0, 20000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(-ROAD_WIDTH / 2, 0, 12000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(0, 0, 8000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(ROAD_WIDTH / 2, 0, 4000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(-ROAD_WIDTH / 2, 0, 1000), maxSpeed, "src/resources/player_straight.png", 12, circuit));
        vehicles.add(new Vehicle(new Coordinate3D(0, 0, 2000), maxSpeed, "src/resources/player_straight.png", 12, circuit));*/
        
    }
    
    public GamePanel() {
        setBackground(Color.WHITE); //QUITAR LUEGO
        circuit = new Circuit(ROAD_WIDTH, RUMBLESTRIP_WIDTH, SEGMENT_LENGTH, NUMBER_OF_SEGMENTS, 300);
        camera = new Camera();
        keyInput = new KeyInputHandler();
        addKeyListener(keyInput);
        setFocusable(true);
        player = new Player(new Coordinate3D(0, 0, 0), (float) (SEGMENT_LENGTH * 0.7 * FRAMES_PER_SECOND), "src/resources/boceto.png", 25, keyInput, circuit);
        background = new Background();
        vehicles = new ArrayList<>();
        loadVehicles();
        
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(21,205,212));
        g2.fillRect(0, 0, getWidth(), getHeight());
        //background.draw(g2);
        circuit.renderCircuit(g2, camera, getWidth(), getHeight());
        synchronized(vehicles) {
            vehicles.forEach(vehicle -> vehicle.draw(g2, getWidth(), getHeight(), camera));
        }
        player.draw(g2, getWidth(), getHeight(), camera);
        //vehicle2.draw(g2, getWidth(), getHeight(), camera);
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
            camera.updateHeight(30);
        }
        if (keyInput.minus) {
            camera.updateHeight(-30);
        }
        Segment s = circuit.getCurrentSegment(camera.getPosition().z + camera.getDistanceToPlayer());
        player.updateX(s.getCurve());
        float dx = ROAD_WIDTH / (1 * FRAMES_PER_SECOND);
        player.update(dt, dx);
        synchronized(vehicles) {
            vehicles.forEach(vehicle-> vehicle.update(dt,6));
        }
        synchronized(vehicles) {
            vehicles.sort((v2, v1)-> Float.compare(v1.looped ? v1.getPosition().z + circuit.getRoadWidth() : v1.getPosition().z, v2.looped ? v2.getPosition().z + circuit.getRoadWidth() : v2.getPosition().z));   
        }
        
        camera.update(player.getPosition());
        if (camera.getPosition().z >= circuit.getRoadLength() - camera.getDistanceToPlayer()) {
            camera.restart();
            player.restart();
        }
        for (Vehicle vehicle : vehicles) {
            Segment vehicleSegment = circuit.getCurrentSegment(vehicle.position.z % circuit.getRoadLength());
                if (vehicleSegment == s) {

                    if (Utils.overlap(player.pointX, player.imageWidth, vehicle.pointX, vehicle.imageWidth)) {
                        if (vehicle.getSpeed() < player.getSpeed())
                            player.setSpeed(0);
                        else
                            vehicle.setSpeed(0);
                        System.out.println("COLISION");
                }
            }
        }
        //System.out.println(player.imageWidth + ", "+ player.imageHeight);
        
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




