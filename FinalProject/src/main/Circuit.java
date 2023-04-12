/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author txola
 */
public class Circuit {
    private int roadWidth;
    private int roadLength;
    private int rumblestripWidth;
    private int segmentLenght;
    private int numberOfSegments;
    private List<Segment> roadSegments;
    final Color[] colors = {
        new Color(100, 100, 110), //Road 1
        new Color(80,80,90), //Road 2
        new Color(0, 204, 0), //Grass 1
        new Color(0, 183, 0)  // Grass 2
    };


    public Circuit(int roadWidth, int rumblestripWidth, int segmentLenght, int numberOfSegments) {
        this.roadWidth = roadWidth;
        this.rumblestripWidth = rumblestripWidth;
        this.segmentLenght = segmentLenght;
        this.numberOfSegments = numberOfSegments;
        createRoadSegments();
        this.roadLength = numberOfSegments * segmentLenght;
    }

//<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public int getRoadWidth() {
        return roadWidth;
    }
    
    public void setRoadWidth(int roadWidth) {
        this.roadWidth = roadWidth;
    }
    
    public int getRoadLength() {
        return roadLength;
    }
    
    public void setRoadLength(int roadLength) {
        this.roadLength = roadLength;
    }
    
    public int getRumblestripWidth() {
        return rumblestripWidth;
    }
    
    public void setRumblestripWidth(int rumblestripWidth) {
        this.rumblestripWidth = rumblestripWidth;
    }
    
    public int getSegmentLenght() {
        return segmentLenght;
    }
    
    public void setSegmentLenght(int segmentLenght) {
        this.segmentLenght = segmentLenght;
    }
    
    public int getNumberOfSegments() {
        return numberOfSegments;
    }
    
    public void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
    }
    
    public List<Segment> getRoadSegments() {
        return roadSegments;
    }
    
    public void setRoadSegments(List<Segment> roadSegments) {
        this.roadSegments = roadSegments;
    }
//</editor-fold>
    
    private final void createRoadSegments() {
        roadSegments = new ArrayList<>();
        addCurve(0, 4000, numberOfSegments / 4);
        addCurve(4, 0, numberOfSegments / 8);
        addCurve(0, 0, numberOfSegments / 8);
        addCurve(3, 0, numberOfSegments / 8);
        addCurve(-2, 0, numberOfSegments / 8);
        addCurve(0, 0,numberOfSegments / 4 + numberOfSegments - roadSegments.size());
    }
    
    public void renderCircuit(Graphics2D g2, Camera camera, int screenWidth, int screenHeight) {
        g2.setColor(new Color(21,205,212));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        int base = getCurrentSegmentIndex(camera);
        Point previousPoint = null;
        float acumulator = 0;
        float offsetX = 0;
        int maxy = screenHeight;
        float offsetY = roadSegments.get(base).getYOffset(camera.getPosition().z + camera.getDistanceToPlayer());
        System.out.println(offsetY + " , " + base + " , " + 0);
        //float offsetY = roadSegments.get(base).getPoint1().y;
        for (int i = base; i <= base + 200; i++) {
            
            int index = i % numberOfSegments;
                       
            Point currentPoint = new Point(roadSegments.get(index).getPoint1());
            
            acumulator += roadSegments.get(index).getCurve();
            offsetX += acumulator;
            
            
            
            currentPoint.projectPoint(camera, index < base ? roadLength : 0, offsetX,
                    offsetY, screenWidth / 2, screenHeight / 2);
            if (currentPoint.getYWorld() < 200) {
//                System.out.println(currentPoint.getYWorld());
            }

            if (i > base && currentPoint.getYWorld() < maxy) {
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
                maxy = currentPoint.getYWorld();
            }   
            
            previousPoint = currentPoint;
        }
    }   
    
    private int getCurrentSegmentIndex(Camera camera) {
        int index = ((camera.getPosition().z + camera.getDistanceToPlayer()) /
                segmentLenght);
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
    
    private void addCurve(int curve, int height, int numberSegmentsCurve) {
        int base = roadSegments.size();
        float auxCurve = 0;
        int auxHeight = 0;
        int auxHeight1 = 0;
        final int proportion = 5;
        float dc = (float) curve * proportion / numberSegmentsCurve;
        int dh = height * 7 / numberSegmentsCurve;
        for (int i = 0; i < numberSegmentsCurve; i++) {
            if (i < numberSegmentsCurve / proportion) {
                auxCurve += dc;
                auxHeight = (int) Utils.easeInOut(0, height, (float) (proportion* i) / numberSegmentsCurve);
                auxHeight1 = (int) Utils.easeInOut(0, height, (float) (proportion * (i + 1)) / numberSegmentsCurve);

            }
            else if (i < (proportion - 1) * numberSegmentsCurve / proportion) {
                auxCurve = curve;
                auxHeight = height;
            }
            else {
                auxCurve -= dc;
                auxHeight = (int) Utils.easeInOut(height, 0, (float) (proportion* i) / numberSegmentsCurve);
                auxHeight1 = (int) Utils.easeInOut(height, 0, (float) (proportion* (i + 1)) / numberSegmentsCurve);
            }
           
            roadSegments.add(new Segment(
                            new Coordinate3D(0, auxHeight, (base + i) * segmentLenght),
                            new Coordinate3D(0, auxHeight1, (base + i + 1) * segmentLenght),
                            auxCurve)
            );
        }
    }
    

    
    public Segment getCurrentSegment(Camera camera) {
        return roadSegments.get(getCurrentSegmentIndex(camera));
    }
}