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

/**
 * This class represents the graphical user interface for the application settings.
 * It provides a panel to display and modify various settings options.
 *
 * @author FrameDev
 * @since 25.02.2025 22:05
 */
public class SettingsGUI {
    private JPanel panel;

    /**
     * The main method to run the SettingsGUI application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("SettingsGUI");
        frame.setContentPane(new SettingsGUI().panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
