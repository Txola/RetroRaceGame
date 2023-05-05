/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import entity.Background;
import entity.Entity;
import entity.Image;
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
    final int ROAD_WIDTH = 2500;
    final int RUMBLESTRIP_WIDTH = 400;
    final int NUMBER_OF_SEGMENTS = 800;
    final int SEGMENT_LENGTH = 250;
    
    
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
        new Image("src/resources/kia.png", (float) 0.7, 1)
    };
    
    float x = 350;
    float y = 600;
    float speed = 160;
    
    private void loadVehicles() {
        final float maxSpeed = (float) (SEGMENT_LENGTH * 0.4 * FRAMES_PER_SECOND);
        final int frequency = 15;
        final int minimumSeparation = 3;
        for (int i = 0; i < NUMBER_OF_SEGMENTS - frequency; i += frequency) {
            float z = Utils.uniform(i * SEGMENT_LENGTH, (i + frequency - minimumSeparation) *SEGMENT_LENGTH);
            float x = Utils.uniform(-ROAD_WIDTH + ROAD_WIDTH / 5, ROAD_WIDTH - ROAD_WIDTH / 5);
            Vehicle vehicle = new Vehicle(new Coordinate3D(x, 0, z), maxSpeed, circuit, images[(int) Utils.uniform(1, 6)]);
            vehicles.add(vehicle);
            sprites.add((Entity) vehicle);
        }
        for (Vehicle vehicle : vehicles) {
            vehicle.setSpeed(maxSpeed * Utils.uniform((float) 0.5, 1));
        }
    }
    
    public GamePanel() {
        setBackground(Color.WHITE); //QUITAR LUEGO
        circuit = new Circuit(ROAD_WIDTH, RUMBLESTRIP_WIDTH, SEGMENT_LENGTH, NUMBER_OF_SEGMENTS, 300);
        camera = new Camera();
        keyInput = new KeyInputHandler();
        addKeyListener(keyInput);
        setFocusable(true);
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
        backgroundSky.draw(g2, getWidth(), getHeight());
        backgroundCity.draw(g2, getWidth(), getHeight());
        
        circuit.renderCircuit(g2, camera, getWidth(), getHeight());
        synchronized(sprites) {
            sprites.forEach(vehicle -> vehicle.draw(g2, getWidth(), getHeight(), camera));
        }
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
        final float prop = (float) 1;
        final float prop2 = (float) 2.5;
        if (!lastSegment.equals(s)) {
           backgroundCity.updateImageOffset((int) (float) (-s.getCurve()*prop));
           backgroundSky.updateImageOffset((int) (float) (-s.getCurve()/prop2));
        }
        
        backgroundCity.updateOffset((int) (-s.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())*prop));
        backgroundSky.updateOffset((int) (-s.getCurveAmount(camera.getPosition().z + camera.getDistanceToPlayer())/prop2));
        float dx = ROAD_WIDTH / (1 * FRAMES_PER_SECOND);
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
                        player.setSpeed(0);
                        if (!(sprite instanceof Vehicle))
                            player.colidedWithSprite = true;
                        
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




