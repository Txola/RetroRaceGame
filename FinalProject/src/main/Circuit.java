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
    private int rumbleWidth;
    private int segmentLenght;
    private int numberOfSegments;
    private Segment[] roadSegments;
    final Color[] colors = {
        new Color(80, 81, 92), //Road 1
        new Color(108,109,117), //Road 2
        new Color(0, 204, 0), //Grass 1
        new Color(0, 183, 0)  // Grass 2
    };


    public Circuit(int roadWidth, int rumbleWidth, int segmentLenght, int numberOfSegments) {
        this.roadWidth = roadWidth;
        this.rumbleWidth = rumbleWidth;
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
    
    public void renderCircuit(Graphics2D g2, Camera camera, float cx, float cy) {
        g2.setColor(new Color(21,205,212));
        g2.fillRect(0, 0, (int)cx * 2, (int)cy * 2);
        int base = getCurrentSegmentIndex(camera);
        for (int i = base; i < base + 200; i++) {
            Segment s = roadSegments[i];
            int []screenX = new int[4];
            int []screenY = new int[4];
            float scale1 = camera.getDistanceToPlane() / 
                    (s.getPoint1().z - camera.getPosition().z);
            float screenW1 = scale1 * this.roadWidth * cx;
            float scrX = cx * 
                   (1 + scale1 * (s.getPoint1().x - camera.getPosition().x));
            screenY[0] = screenY[1] = Math.round(cy * 
                   (1 - scale1 * (s.getPoint1().y - camera.getPosition().y)));
            screenX[0] = Math.round(scrX - screenW1);
            screenX[1] = Math.round(scrX + screenW1);


            s = roadSegments[i + 1];
            float scale2 = camera.getDistanceToPlane() / 
                    (s.getPoint1().z - camera.getPosition().z);
            float screenW2 = scale2 * this.roadWidth * cx;
            scrX = cx * 
                       (1 + scale2 * (s.getPoint1().x - camera.getPosition().x));
            screenY[2] = screenY[3] = Math.round(cy * 
                   (1 - scale2 * (s.getPoint1().y - camera.getPosition().y)));
            screenX[2] = Math.round(scrX + screenW2);
            screenX[3] = Math.round(scrX - screenW2);
            if (i % 2 == 0) 
                g2.setColor(colors[0]);
            else
                g2.setColor(colors[1]);
            if (screenX[0] == screenX[1])
                System.out.println("akdjf");
            g2.fillPolygon(screenX, screenY, 4);
            
            int []grass = new int[4];
            grass[0] = 0;
            grass[1] = screenX[0];
            grass[2] = screenX[3];
            grass[3] = 0;
            
            if (i % 2 != 0) 
                g2.setColor(colors[2]);
            else
                g2.setColor(colors[3]);
            g2.fillPolygon(grass, screenY, 4);
            
            
            g2.fillPolygon(grass, screenY, 4);
            
            grass[0] = screenX[1];
            grass[1] = (int) cx * 2;
            grass[2] = (int) cx * 2;
            grass[3] = screenX[2];
            g2.fillPolygon(grass, screenY, 4);
            
            
            if (i % 2 == 0) 
                g2.setColor(Color.red);
            else
                g2.setColor(Color.white);
            int []rumble = new int[4];
            rumble[0] = screenX[0] - Math.round(scale1 * rumbleWidth * cx);
            rumble[1] = screenX[0];
            rumble[2] = screenX[3];
            rumble[3] = screenX[3] - Math.round(scale2 * rumbleWidth * cx);
            g2.fillPolygon(rumble, screenY, 4);
            
            rumble[0] = screenX[1];
            rumble[1] = screenX[1] + Math.round(scale1 * rumbleWidth * cx);
            rumble[2] = screenX[2] + Math.round(scale2 * rumbleWidth * cx);
            rumble[3] = screenX[2];
            g2.fillPolygon(rumble, screenY, 4);
        }
    }   
    
    private int getCurrentSegmentIndex(Camera camera) {
        int index = ((camera.getPosition().z + camera.getDistanceToPlayer()) /
                segmentLenght) % numberOfSegments;
        return index;
    }

}