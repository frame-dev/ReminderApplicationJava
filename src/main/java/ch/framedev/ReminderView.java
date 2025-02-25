package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 18:58
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReminderView {

    private static JFrame frame;
    private JPanel panel;
    private JTextField reminderTitleTextField;
    private JTextArea reminderTextArea;
    private JButton editReminderButton;
    private JButton removeReminderButton;
    private JScrollPane areaScrollPane;
    public static ReminderView reminderView;

    public static void init(Reminder reminder) {
        reminderView.reminderTitleTextField.setText(reminder.getTitle());
        reminderView.reminderTextArea.setText(reminder.reminderToString());
        frame.pack();
    }

    public ReminderView() {
        editReminderButton.addActionListener(e -> {
            Main.reminderManager.editReminder(Reminder.reminderFromString(reminderTextArea.getText()));
        });
        removeReminderButton.addActionListener(e -> {
            Reminder reminder = Reminder.reminderFromString(reminderTextArea.getText());
            Main.reminderManager.deleteReminder(reminder.getTitle());
            if(ReminderGUI.reminderGUI == null) {
                ReminderGUI.reminderGUI = new ReminderGUI();
            }
            ReminderGUI.reminderGUI.loadList();
            Main.reminderManager.addDeletedReminder(reminder);
            frame.dispose();
        });
    }

    public static void main(String[] args) {
        frame = new JFrame("ReminderView");
        reminderView = new ReminderView();
        frame.setContentPane(reminderView.panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}
