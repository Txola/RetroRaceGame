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
    private int width;
    private int segmentLenght;
    private int numberOfSegments;
    private Segment[] roadSegments;
    final Color[] colors = {
        
    };


    public Circuit(int width, int segmentLenght, int numberOfSegments) {
        this.width = width;
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
        for (int i = 0; i < 20; i++) {
            Segment s = roadSegments[i];
            int []screenX = new int[4];
            int []screenY = new int[4];
            float scale = camera.getDistanceToPlane() / 
                    (s.getPoint1().z - camera.getCoordinate3D().z);
            int screenW1 = (int) (scale * this.width * cx);
            float scrX = cx * 
                   (1 + scale * (s.getPoint1().x - camera.getCoordinate3D().x));
            screenY[0] = screenY[1] = Math.round(cy * 
                   (1 - scale * (s.getPoint1().y - camera.getCoordinate3D().y)));
            screenX[0] = Math.round(scrX - screenW1);
            screenX[1] = Math.round(scrX + screenW1);


            s = roadSegments[i+1];
            scale = camera.getDistanceToPlane() / 
                    (s.getPoint1().z - camera.getCoordinate3D().z);
            int screenW2 = (int) (scale * this.width * cx);
            scrX = cx * 
                       (1 + scale * (s.getPoint1().x - camera.getCoordinate3D().x));
            screenY[2] = screenY[3] = Math.round(cy * 
                   (1 - scale * (s.getPoint1().y - camera.getCoordinate3D().y)));
            screenX[2] = Math.round(scrX + screenW2);
            screenX[3] = Math.round(scrX - screenW2);
            if (i % 2 == 0) 
                g2.setColor(Color.gray);
            else
                g2.setColor(Color.LIGHT_GRAY);
            g2.fillPolygon(screenX, screenY, 4);
            
            int []grass = new int[4];
            grass[0] = 0;
            grass[1] = screenX[0];
            grass[2] = screenX[3];
            grass[3] = 0;
            
            if (i % 2 != 0) 
                g2.setColor(new Color(0, 204, 0));
            else
                g2.setColor(new Color(0, 153, 0));
            g2.fillPolygon(grass, screenY, 4);
            
            grass[0] = screenX[1];
            grass[1] = (int) cx * 2;
            grass[2] = (int) cx * 2;
            grass[3] = screenX[2];
            g2.fillPolygon(grass, screenY, 4);
            
            g2.setColor(new Color(21,205,212));
            g2.fillRect(0, 0, (int)cx * 2, (int)cy);
        }
    }   
    
    

}