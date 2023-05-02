/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import main.Camera;
import main.Circuit;
import main.Coordinate3D;
import main.Point;
import main.Segment;

/**
 *
 * @author txola
 */
public class Entity {
    Circuit circuit;
    public Coordinate3D position;
    BufferedImage image;
    final int scale;
    public boolean looped = false;
    public int imageWidth;
    public int imageHeight;
    public int pointX;
    
    public Entity(Coordinate3D position, String image, int scale, Circuit circuit) {
        this.position = position;
        this.circuit = circuit;
        this.scale = scale;
        loadImage(image);
    }

    private final void loadImage(String image) {
        try {
            this.image = ImageIO.read(new File(image));
        } catch (IOException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        if (camera.getPosition().z >= position.z) {
            looped = true;
        }
        else {
            looped = false;
        }
        int baseIndex = circuit.getCurrentSegmentIndex(camera.getPosition().z + camera.getDistanceToPlayer()); 
        int currentIndex = circuit.getCurrentSegmentIndex(position.z);
        if (position.z > camera.getPosition().z && (currentIndex - baseIndex) < circuit.getNumberOfVisibleSegments() ||
                (looped && ((circuit.getNumberOfSegments() - baseIndex + currentIndex) < circuit.getNumberOfVisibleSegments()) ))  {
            Segment currentSegment = circuit.getCurrentSegment(position.z);
            Segment baseSegment = circuit.getCurrentSegment(camera.getPosition().z + camera.getDistanceToPlayer());
            float offsetY = currentSegment.getYOffset(position.z) - baseSegment.getYOffset(camera.getPosition().z + camera.getDistanceToPlayer());
            Point point = new Point(position);
            point.projectPoint(camera, looped ? circuit.getRoadLength() : 0,
                    currentSegment.getXOffset(position.z), -offsetY, screenWidth / 2, screenHeight / 2);
            float xScale = point.getXScale();
            float yScale = point.getYScale();
            imageWidth = (int) (image.getWidth() * scale * xScale);
            if (imageWidth > 1000)   {
                int x = 1;
            }
            imageHeight = (int) (image.getHeight() * scale * yScale);
            
            pointX = point.getXWorld();

            if (point.getYWorld() < currentSegment.maxy + imageHeight) {
                int s2y, d2y;
                if (currentSegment.maxy < point.getYWorld()) {
                    s2y = (int) (image.getHeight() * 
                        (imageHeight - point.getYWorld() + currentSegment.maxy)
                        / imageHeight);
                    d2y = (int) currentSegment.maxy;
                }
                else {
                    s2y = image.getHeight();
                    d2y = point.getYWorld();
                }

                g2.drawImage(image, point.getXWorld() - imageWidth / 2,
                        point.getYWorld() - imageHeight, 
                        point.getXWorld() + imageWidth / 2, 
                        d2y, 0, 0, image.getWidth(), s2y, null);
            }
        }
    }
}
