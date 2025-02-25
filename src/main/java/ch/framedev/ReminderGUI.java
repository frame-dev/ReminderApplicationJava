package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 18:50
 */

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ReminderGUI {

    private static JFrame frame;
    private JPanel panel;
    private JList<String> reminderList;
    private JScrollPane reminderScrollPane;
    private JButton createReminderButton;
    public static ReminderGUI reminderGUI;

    public ReminderGUI() {
        loadList();
        reminderList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ReminderView.main(new String[0]);
                    Reminder reminder = Main.reminderManager.getReminderByTitle(reminderList.getSelectedValue()).orElse(null);
                    if (reminder == null) {
                        System.out.println("Reminder not found!");
                        return;
                    }
                    ReminderView.init(reminder);
                }
            }
        });
        createReminderButton.addActionListener(e -> {
            ReminderCreateGUI.main(new String[0]);
        });
    }

    /**
     * This function loads the list of reminder titles from the ReminderManager and updates the JList in the ReminderGUI.
     *
     * @throws NoClassDefFoundError If the ReminderManager class or its dependencies are not found.
     * @throws NullPointerException If the ReminderManager or any of its methods return null unexpectedly.
     */
    public void loadList() {
        List<String> reminderTitles = Main.reminderManager.getReminderList()
                .stream()
                .map(Reminder::getTitle)
                .toList(); // Java 16+ (Use .collect(Collectors.toList()) for older versions)
        reminderList.setListData(reminderTitles.toArray(new String[0]));
    }

    public static void main(String[] args) {
        frame = new JFrame("ReminderGUI");
        reminderGUI = new ReminderGUI();
        frame.setContentPane(reminderGUI.panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}
