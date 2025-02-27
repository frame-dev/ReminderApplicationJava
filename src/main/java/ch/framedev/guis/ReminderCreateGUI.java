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
        JMenuBar menu = new JMenuBar();
        // Add your menu items here
        JMenuItem menuItem = new JMenuItem("Help");
        menuItem.setToolTipText("Get help for the Application");
        menuItem.addActionListener(e -> {
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
        menu.add(menuItem);
        return menu;
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
