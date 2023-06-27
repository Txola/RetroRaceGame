/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import java.awt.Color;
import java.awt.Font;
import static java.awt.Font.BOLD;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author txola
 */
public class MultiplayerWaitingInfoPanel extends javax.swing.JPanel {

    /**
     * Creates new form MultiplayerWaitingInfoPanel
     */
    
    public MultiplayerWaitingInfoPanel(boolean host) {
        initComponents();
        if (host) {
            text.setText("Waiting until someone connects...");
            repaint();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        text = new javax.swing.JTextField();

        setOpaque(false);

        text.setEditable(false);
        text.setBackground(new Color(0,0,0,0));
        text.setFont(new java.awt.Font("URW Gothic L", 1, 18)); // NOI18N
        text.setForeground(new java.awt.Color(0, 0, 0));
        text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        text.setToolTipText("");
        text.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        text.setFocusable(false);
        text.setRequestFocusEnabled(false);
        text.setSelectionColor(new Color(0,0, 0, 0));
        text.setVerifyInputWhenFocusTarget(false);
        text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(303, Short.MAX_VALUE)
                .addComponent(text, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(302, 302, 302))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(text, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    
    private void textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textActionPerformed

    public void showNumber(int number) {
        text.setFont(new Font("URW Gothic L", BOLD, (int) (70)));
        text.setForeground(new Color(210, 30, 30));
        text.setText(Integer.toString(number));
        repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField text;
    // End of variables declaration//GEN-END:variables
}
