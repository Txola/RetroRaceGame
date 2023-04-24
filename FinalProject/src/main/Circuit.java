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
    final int numberOfVisibleSegments;
    private List<Segment> roadSegments;
    final Color[] colors = {
        new Color(90, 90, 100), //Road 1
        new Color(80,80,90), //Road 2
        new Color(0, 204, 0), //Grass 1
        new Color(0, 183, 0)  // Grass 2
    };


    public Circuit(int roadWidth, int rumblestripWidth, int segmentLenght, int numberOfSegments, int numberOfVisibleSegments) {
        this.roadWidth = roadWidth;
        this.rumblestripWidth = rumblestripWidth;
        this.segmentLenght = segmentLenght;
        this.numberOfSegments = numberOfSegments;
        this.numberOfVisibleSegments = numberOfVisibleSegments;
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

    public int getNumberOfVisibleSegments() {
        return numberOfVisibleSegments;
    }
    
    
//</editor-fold>
    
    private final void createRoadSegments() {
        roadSegments = new ArrayList<>();
        final int unit = numberOfSegments / 32;
        addRoadSection(0, 15, 0, numberOfSegments, 0);
        /*addRoadSection(4000, -20, unit, 3 * unit, 2 *unit);
        addRoadSection(0, 3, unit, 2 * unit, unit);
        addRoadSection(0, 0, 2 * unit, 0, 0);
        addRoadSection(-2000, 4, 2 * unit, unit, unit);
        addRoadSection(3000, 0, 3 * unit, 0, 0);
        addRoadSection(3000, 0, 2 * unit, 0, 0);
        addRoadSection(0, 3, 2 * unit, 2 *unit, unit);
        addRoadSection(0, -2, unit, 3 * unit, unit);
        addRoadSection(0, 0, numberOfSegments - roadSegments.size(), 0, 0);*/
    }
    
        private void addRoadSegment(float height, float curve) {
        roadSegments.add(new Segment(
                            new Point(new Coordinate3D(0, getPreviousSegmentHeight(),
                                    roadSegments.size() * segmentLenght)),
                            new Point(new Coordinate3D(0, (int) height,
                                    (roadSegments.size() + 1) * segmentLenght)),
                            curve)
            );
    }
    
    private void addRoadSection(int height, float curve, int enter, int maintain, int exit) {
        float startY = getPreviousSegmentHeight();
        int totalOfSegments = enter + maintain + exit;
        
        for (int i = 0; i < enter; i++) {
            addRoadSegment((float) Utils.easeInOut(startY,
                    height, (float) i / totalOfSegments),
                    (int) Utils.easeInOut(0, curve, (float) i / enter));
        }
        
        for (int i = 0; i < maintain; i++) {
            addRoadSegment((float) Utils.easeInOut(startY,
                    height, (float) (i + enter) / totalOfSegments),
                    curve);
        }
        
        for (int i = 0; i < exit; i++) {
            addRoadSegment((float) Utils.easeInOut(startY,
                    height, (float) (i + enter + maintain)/ totalOfSegments),
                    (int) Utils.easeInOut(curve, 0, (float) i / exit));
        }
    }
    
    private float getPreviousSegmentHeight() {
        return roadSegments.isEmpty() ? 0 :
                roadSegments.get(
                        roadSegments.size() - 1).getPoint2().getWorldPosition().y;
    }
    
    public void renderCircuit(Graphics2D g2, Camera camera, int screenWidth, int screenHeight) {
        
        int base = getCurrentSegmentIndex(camera.getPosition().z + camera.getDistanceToPlayer());
        Segment baseSegment = roadSegments.get(base);
        Point previousPoint = null;
        float acumulator = -baseSegment.getCurve() * baseSegment.
                getSegmentPercent(camera.getPosition().z + camera.getDistanceToPlayer());
        float offsetX = 0;
        int maxy = screenHeight;
        float offsetY = baseSegment.getYOffset(camera.getPosition().z + camera.getDistanceToPlayer());
        
        for (int i = base; i <= base + numberOfVisibleSegments; i++) {
            
            int index = i % numberOfSegments;
                       
            Point currentPoint = roadSegments.get(index).getPoint1();
            acumulator += roadSegments.get(index).getCurve();
            offsetX += acumulator;

            
            currentPoint.projectPoint(camera, index < base ? roadLength : 0, offsetX,
                    offsetY, screenWidth / 2, screenHeight / 2);

            if (i > base && currentPoint.getYScreen() < maxy) {
                System.out.println(base);
                roadSegments.get(index == 0 ? numberOfSegments - 1 : index - 1)
                        .setPoint2(currentPoint);
                float prevWidth = previousPoint.getXScale() * roadWidth;
                float currWidth = currentPoint.getXScale() * roadWidth;
                int x1 = Math.round(previousPoint.getXScreen() - prevWidth);
                int x2 = Math.round(previousPoint.getXScreen() + prevWidth);
                int x3 = Math.round(currentPoint.getXScreen() + currWidth);
                int x4 = Math.round(currentPoint.getXScreen() - currWidth);
                setTextureColor(g2, colors[0], colors[1], i, 1);
                drawPolygon(g2, x1, x2, x3, x4, previousPoint.getYScreen(),
                        currentPoint.getYScreen());
                
               setTextureColor(g2, colors[2], colors[3], i, 1);
                drawPolygon(g2, 0, x1, x4, 0, previousPoint.getYScreen(),
                        currentPoint.getYScreen());
                drawPolygon(g2, x2, screenWidth, screenWidth, x3,
                        previousPoint.getYScreen(), currentPoint.getYScreen());
                

                prevWidth = previousPoint.getXScale() * rumblestripWidth;
                currWidth = currentPoint.getXScale() * rumblestripWidth;
                setTextureColor(g2, Color.red, Color.white, i, 2);
                drawPolygon(g2, Math.round(x1 - prevWidth), x1, x4, 
                        Math.round(x4 - currWidth), previousPoint.getYScreen(),
                        currentPoint.getYScreen());
                drawPolygon(g2, x2, Math.round(x2 + prevWidth),
                        Math.round(x3 + currWidth), x3,
                        previousPoint.getYScreen(), currentPoint.getYScreen());
                maxy = currentPoint.getYScreen();
            }   
            roadSegments.get(index).maxy = maxy;
            previousPoint = currentPoint;
        }
    }   
    
    public int getCurrentSegmentIndex(float z) {
        int index = (int) z / segmentLenght;
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
    

    
    public Segment getCurrentSegment(float z) {
        return roadSegments.get(getCurrentSegmentIndex(z));
    }
}