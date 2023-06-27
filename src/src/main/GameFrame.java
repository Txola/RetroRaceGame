/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Gui.StartMenuPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author txola
 */
public class GameFrame extends JFrame{
    private final int SCREEN_WIDTH = 1024;
    private final int SCREEN_HEIGHT = 768;
    private final JPanel cards;

    public GameFrame() {
        
        
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Race game");
        setResizable(false);
        StartMenuPanel startMenuPanel = new StartMenuPanel(this);
        cards = new JPanel(new CardLayout());
        cards.add(startMenuPanel, "menu");        
        this.add(cards);
        pack();
    }
    
    public void startGame(GamePanel gamePanel) {
        cards.add(gamePanel, "game");
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, "game");
        gamePanel.pauseOrResume();
        gamePanel.requestFocusInWindow();
    }
    public void goToMenu(GamePanel gamePanel) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, "menu");
        cards.remove(gamePanel);
    }
    
    
    public static void main(String[] args) {
        GameFrame mf = new GameFrame();
        mf.setLocationRelativeTo(null);
        mf.setVisible(true);
    }
}
