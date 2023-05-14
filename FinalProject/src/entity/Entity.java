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
import java.io.Serializable;
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
public class Entity implements Serializable{
    Circuit circuit;
    private Coordinate3D position;
    private Image image;
    private boolean looped = false;
    private int imageWidth;
    private int imageHeight;
    private int pointX;

    
    public Entity(Coordinate3D position, Circuit circuit, Image image) {
        this.position = position;
        this.circuit = circuit;
        this.image = image;
    }

    

    //<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Coordinate3D getPosition() {
        return position;
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public Image getImage() {
        return image;
    }
    
    public boolean isLooped() {
        return looped;
    }
    
    public int getImageWidth() {
        return imageWidth;
    }
    
    public int getImageHeight() {
        return imageHeight;
    }
    
    public int getPointX() {
        return pointX;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
    
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    
    public void setPointX(int pointX) {
        this.pointX = pointX;
    }
//</editor-fold>
    

    public void draw(Graphics2D g2, int screenWidth, int screenHeight, Camera camera) {
        if (camera.getPosition().z >= position.z) {
            looped = true;
        }
        else {
            looped = false;
        }
        int baseIndex = circuit.getCurrentSegmentIndex(camera.getPosition().z + camera.getDistanceToPlayer()); 
        int currentIndex = circuit.getCurrentSegmentIndex(position.z);
        if (position.z > camera.getPosition().z + circuit.getSegmentLenght() && (currentIndex - baseIndex) < circuit.getNumberOfVisibleSegments() ||
                (looped && ((circuit.getNumberOfSegments() - baseIndex + currentIndex) < circuit.getNumberOfVisibleSegments()) ))  {
            Segment currentSegment = circuit.getCurrentSegment(position.z);
            Segment baseSegment = circuit.getCurrentSegment(camera.getPosition().z + camera.getDistanceToPlayer());
            float offsetY = currentSegment.getYOffset(position.z) - baseSegment.getYOffset(camera.getPosition().z + camera.getDistanceToPlayer());
            Point point = new Point(position);
            point.projectPoint(camera, looped ? circuit.getRoadLength() : 0,
                    currentSegment.getXOffset(position.z), -offsetY, screenWidth / 2, screenHeight / 2);
            float xScale = point.getXScale();
            float yScale = point.getYScale();
            imageWidth = (int) (image.getBufferedImage().getWidth() * image.getScale() * xScale);
            if (imageWidth > 4000)   {
                int x = 1;
            }
            imageHeight = (int) (image.getBufferedImage().getHeight() * image.getScale() * yScale);
            
            pointX = point.getXWorld();
            if (point.getYWorld() < currentSegment.maxy + imageHeight) {
                int s2y, d2y;
                if (currentSegment.maxy < point.getYWorld()) {
                    s2y = (int) (image.getBufferedImage().getHeight() * 
                        (imageHeight - point.getYWorld() + currentSegment.maxy)
                        / imageHeight);
                    d2y = (int) currentSegment.maxy;
                }
                else {
                    s2y = image.getBufferedImage().getHeight();
                    d2y = point.getYWorld();
                }

                //System.out.println(point.getYWorld() + ", "  + imageHeight+ ", " + s2y +", " + d2y + "-, " + currentIndex + ", " + baseIndex );

                g2.drawImage(image.getBufferedImage(), point.getXWorld() - imageWidth / 2,
                        point.getYWorld() - imageHeight, 
                        point.getXWorld() + imageWidth / 2, 
                        d2y, 0, 0, image.getBufferedImage().getWidth(), s2y, null);
            }
        }
    }
    
    @Override
    public String toString() {
       return (ResourceManager.instance().getIndexOf(image) + " " + position.x + " " + position.y + " " + position.z); 
    }
}
