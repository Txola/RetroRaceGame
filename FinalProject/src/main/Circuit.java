/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author txola
 */
public class Circuit {
    private int roadWidth;
    private int rumblestripWidth;
    private int segmentLenght;
    private int numberOfSegments;
    private Segment[] roadSegments;
    final Color[] colors = {
        new Color(80, 81, 92), //Road 1
        new Color(108,109,117), //Road 2
        new Color(0, 204, 0), //Grass 1
        new Color(0, 183, 0)  // Grass 2
    };


    public Circuit(int roadWidth, int rumblestripWidth, int segmentLenght, int numberOfSegments) {
        this.roadWidth = roadWidth;
        this.rumblestripWidth = rumblestripWidth;
        this.segmentLenght = segmentLenght;
        this.numberOfSegments = numberOfSegments;
        this.roadSegments = createRoadSegments();
    }
    
    private final Segment[] createRoadSegments() {
        Segment []segments = new Segment[numberOfSegments];
        for (int i = 0; i < numberOfSegments; i++) {
            segments[i] = new Segment(
                            new Coordinate3D(0, 0, i * segmentLenght),
                            new Coordinate3D(0, 0, (i + 1) * segmentLenght)
            );
        }
        return segments;
    }
    
    public void renderCircuit(Graphics2D g2, Camera camera, int screenWidth, int screenHeight) {
        g2.setColor(new Color(21,205,212));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        int base = getCurrentSegmentIndex(camera);
        Point previousPoint = null;
        
        for (int i = base; i < base + 200; i++) {
            Point currentPoint = new Point(roadSegments[i].getPoint1());
            
            currentPoint.projectPoint(camera, screenWidth / 2, screenHeight / 2);

            if (i > base) {
                float prevWidth = previousPoint.getXScale() * roadWidth;
                float currWidth = currentPoint.getXScale() * roadWidth;

                int x1 = Math.round(previousPoint.getXWorld() - prevWidth);
                int x2 = Math.round(previousPoint.getXWorld() + prevWidth);
                int x3 = Math.round(currentPoint.getXWorld() + currWidth);
                int x4 = Math.round(currentPoint.getXWorld() - currWidth);
                setTextureColor(g2, colors[0], colors[1], i, 1);
                drawPolygon(g2, x1, x2, x3, x4, previousPoint.getYWorld(),
                        currentPoint.getYWorld());
                
                
               setTextureColor(g2, colors[2], colors[3], i, 1);
                drawPolygon(g2, 0, x1, x4, 0, previousPoint.getYWorld(),
                        currentPoint.getYWorld());
                drawPolygon(g2, x2, screenWidth, screenWidth, x3,
                        previousPoint.getYWorld(), currentPoint.getYWorld());
                

                prevWidth = previousPoint.getXScale() * rumblestripWidth;
                currWidth = currentPoint.getXScale() * rumblestripWidth;
                setTextureColor(g2, Color.red, Color.white, i, 2);
                drawPolygon(g2, Math.round(x1 - prevWidth), x1, x4, 
                        Math.round(x4 - currWidth), previousPoint.getYWorld(),
                        currentPoint.getYWorld());
                drawPolygon(g2, x2, Math.round(x2 + prevWidth),
                        Math.round(x3 + currWidth), x3,
                        previousPoint.getYWorld(), currentPoint.getYWorld());
   
            }
            
            previousPoint = currentPoint;
        }
    }   
    
    private int getCurrentSegmentIndex(Camera camera) {
        int index = ((camera.getPosition().z + camera.getDistanceToPlayer()) /
                segmentLenght) % numberOfSegments;
        return index;
    }
    
    private void drawPolygon(Graphics2D g2, int x1, int x2, int x3, int x4, int y1, int y2) {
        int []x = {x1, x2, x3, x4};
        int []y = {y1, y1, y2, y2};
        g2.fillPolygon(x, y, 4);  
    }
    
    private void setTextureColor(Graphics2D g2, Color color1, Color color2, int index, int changeRate) {
        if (index % (changeRate * 2) >= changeRate) {
            g2.setColor(color1);
        }
        else
            g2.setColor(color2);
    }
}