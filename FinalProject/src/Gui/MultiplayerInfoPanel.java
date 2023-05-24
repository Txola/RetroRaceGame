/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import static java.awt.Font.BOLD;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import main.GameFrame;
import main.GamePanel;

/**
 *
 * @author txola
 */
public class MultiplayerInfoPanel extends JPanel implements InfoPanel{
    private final GameFrame gameFrame;
    private final GamePanel gamePanel;
    private final MultiplayerPlayingInfoPanel playingPanel;
    private final MultiplayerWaitingInfoPanel waitingPanel;
    public MultiplayerInfoPanel(GameFrame gameFrame, GamePanel gamePanel) {
        this.gameFrame = gameFrame;
        this.gamePanel = gamePanel;
        setPreferredSize(new Dimension(1024, 91));
        setLayout(new CardLayout());
        playingPanel = new MultiplayerPlayingInfoPanel(this);
        waitingPanel = new MultiplayerWaitingInfoPanel(gamePanel.isHost());
        add(waitingPanel, "waiting");
        add(playingPanel, "playing");
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
   
    public void update() {
        playingPanel.update();
    }
    public void updateFastestLapCounter() {
        playingPanel.updateFastestLapCounter();
    }
    
    public void updateLapInfo(int lap, int numberOfLaps) {
        playingPanel.updateLapInfo(lap, numberOfLaps);
    }
    
    public void updatePosition(boolean ahead) {
        playingPanel.updatePosition(ahead);
    }
    
    public void changePanel() {
        CardLayout cl = (CardLayout) getLayout();
        cl.next(this);
    }
    
    public void showNumber(int number) {
        waitingPanel.showNumber(number);
    }
    
}
