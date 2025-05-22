package ch.framedev.guis;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 19:08
 */

import ch.framedev.classes.Reminder;
import ch.framedev.main.Main;

import javax.swing.*;
import java.util.List;

public class ReminderCreateGUI {

    private static JFrame frame;
    private JPanel panel;
    private JTextField reminderTitleTextField;
    private JTextField reminderMessageTextField;
    private JTextField reminderDateTextField;
    private JTextField reminderTimeTextField;
    private JTextField reminderNotesTextField;
    private JButton createReminderButton;

    public ReminderCreateGUI() {
        frame.setJMenuBar(getJMenuBar());
        createReminderButton.addActionListener(e -> {
            Reminder reminder = new Reminder(
                    reminderTitleTextField.getText(),
                    reminderMessageTextField.getText(),
                    reminderDateTextField.getText(),
                    reminderTimeTextField.getText(),
                    List.of(reminderNotesTextField.getText().split(", ")));
            Main.reminderManager.addReminder(reminder);
            ReminderGUI.reminderGUI.loadList();
            JOptionPane.showMessageDialog(frame, "Reminder created successfully!");
        });
    }

    /**
     * This method creates and configures a JMenuBar for the ReminderCreateGUI.
     * The menu bar contains a single menu item labeled "Help". When clicked,
     * it displays a dialog box with helpful information about the application.
     *
     * @return The configured JMenuBar.
     */
    private JMenuBar getJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create a top-level "Help" menu
        JMenu helpMenu = new JMenu("Help");

        // Create a menu item
        JMenuItem helpItem = new JMenuItem("View Help");
        helpItem.setToolTipText("Get help for the application (Ctrl+H)");
        helpItem.setAccelerator(KeyStroke.getKeyStroke("control H"));
        helpItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    """
                            This is a reminder app.
                            Use date format (yyyy-MM-dd)
                            Use time format (HH:mm)
                            Use comma-separated notes (e.g., Task, Reminder, Due Date)
                            Copyright 2025©. All rights reserved framedev
                            """
            );
        });

        // Add menu item to menu, and menu to menu bar
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    public static void main(String[] args) {
        frame = new JFrame("ReminderCreateGUI");
        frame.setContentPane(new ReminderCreateGUI().panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}
