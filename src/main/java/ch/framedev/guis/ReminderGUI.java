package ch.framedev.guis;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 18:50
 */

import ch.framedev.classes.Reminder;
import ch.framedev.main.Main;

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
                    System.out.println(reminder);
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
        frame.setJMenuBar(getJMenuBar());
    }

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
        if (reminderTitles.isEmpty()) {
            reminderList.setListData(new String[]{});
            return;
        }
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
