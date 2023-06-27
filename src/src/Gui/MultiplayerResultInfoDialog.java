/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import main.GameFrame;
import main.GamePanel;


/**
 *
 * @author txola
 */
public class MultiplayerResultInfoDialog extends javax.swing.JDialog {
    private GamePanel gamePanel;
    private GameFrame gameFrame;
    /**
     * Creates new form MultiplayerResultInfoDialog
     */
    public MultiplayerResultInfoDialog(GameFrame gameFrame, GamePanel gamePanel, int messageIndex) {
        super(gameFrame);
        this.gamePanel = gamePanel;
        this.gameFrame = gameFrame;
        setUndecorated(true);
        initComponents();
        if (messageIndex == 0) {
            text.setText("¡You Win!");
        }
        else if (messageIndex == 1)
            text.setText("¡You Lose!");
        else {
            text.setFont(new java.awt.Font("URW Gothic L", 1, 18));
            text.setText("Your oponent left the game.");
        }
        repaint();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        goToMenuButton = new javax.swing.JButton();
        text = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(211, 211, 211));

        goToMenuButton.setBackground(new java.awt.Color(141, 141, 141));
        goToMenuButton.setFont(new java.awt.Font("URW Gothic L", 1, 18)); // NOI18N
        goToMenuButton.setText("Go to menu");
        goToMenuButton.setFocusPainted(false);
        goToMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToMenuButtonActionPerformed(evt);
            }
        });

        text.setFont(new java.awt.Font("URW Gothic L", 1, 48)); // NOI18N
        text.setForeground(new java.awt.Color(237, 67, 67));
        text.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(goToMenuButton, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(text, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(78, Short.MAX_VALUE)
                .addComponent(text, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(goToMenuButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void goToMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToMenuButtonActionPerformed
        // TODO add your handling code here:
        gameFrame.goToMenu(gamePanel);
        gamePanel.finish();
        this.dispose();
    }//GEN-LAST:event_goToMenuButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton goToMenuButton;
    private javax.swing.JLabel text;
    // End of variables declaration//GEN-END:variables
}