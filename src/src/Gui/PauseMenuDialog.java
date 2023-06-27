/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Gui.PauseMenu;
import Gui.OptionsMenu;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JPanel;
import main.GameFrame;
import main.GamePanel;

/**
 *
 * @author txola
 */
public class PauseMenuDialog extends JDialog {
    private final JPanel cards;
    private final GameFrame gameFrame;
    private final GamePanel gamePanel;
    public PauseMenuDialog(GameFrame gameFrame, GamePanel gamePanel) {
        this.gameFrame = gameFrame;
        this.gamePanel = gamePanel;
        setPreferredSize(new Dimension(400, 300));
        setUndecorated(true);
        OptionsMenu options = new OptionsMenu(this);
        PauseMenu pause = new PauseMenu(this);
        cards = new JPanel(new CardLayout());
        cards.add(pause, "pause");
        cards.add(options, "options");
        this.add(cards);
        pack();
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
   
    
    
    public void changePanel() {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.next(cards);
    }
}
