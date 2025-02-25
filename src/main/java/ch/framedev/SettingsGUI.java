package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 22:05
 */

import javax.swing.*;

public class SettingsGUI {
    private JPanel panel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("SettingsGUI");
        frame.setContentPane(new SettingsGUI().panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
