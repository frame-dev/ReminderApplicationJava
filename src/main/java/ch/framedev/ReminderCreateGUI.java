package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 19:08
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

public class ReminderCreateGUI {
    private JPanel panel;
    private JTextField reminderTitleTextField;
    private JTextField reminderMessageTextField;
    private JTextField reminderDateTextField;
    private JTextField reminderTimeTextField;
    private JTextField reminderNotesTextField;
    private JButton createReminderButton;

    public ReminderCreateGUI() {
        createReminderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Reminder reminder = new Reminder(
                        reminderTitleTextField.getText(),
                        reminderMessageTextField.getText(),
                        reminderDateTextField.getText(),
                        reminderTimeTextField.getText(),
                        List.of(reminderNotesTextField.getText().split(", ")));
                Main.reminderManager.addReminder(reminder);
                ReminderGUI.reminderGUI.loadList();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ReminderCreateGUI");
        frame.setContentPane(new ReminderCreateGUI().panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}
