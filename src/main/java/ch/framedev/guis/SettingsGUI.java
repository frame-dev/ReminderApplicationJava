package ch.framedev.guis;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 22:05
 */

import ch.framedev.database.DatabaseManager;
import ch.framedev.main.Main;

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
    private JCheckBox useDatabaseCheckBox;
    private JComboBox<String> databaseComboBox;

    public SettingsGUI() {
        useDatabaseCheckBox.setSelected(Main.getSettingsManager().getConfiguration().getBoolean("useDatabase"));
        useDatabaseCheckBox.addActionListener(e -> {
            Main.getSettingsManager().getConfiguration().set("useDatabase", useDatabaseCheckBox.isSelected());
            Main.getSettingsManager().saveSettings();
            JOptionPane.showMessageDialog(null, "Please Restart Application");
        });

        for(DatabaseManager.DatabaseType databaseType : DatabaseManager.DatabaseType.values()) {
            databaseComboBox.addItem(databaseType.name());
        }
        databaseComboBox.setSelectedItem(Main.getDatabaseManager().getDatabaseType().name());
        databaseComboBox.addActionListener(e -> {
            Main.getDatabaseManager().setDatabaseType(DatabaseManager.DatabaseType.valueOf(databaseComboBox.getSelectedItem().toString()));
            Main.getSettingsManager().getConfiguration().set("database.databaseType", DatabaseManager.DatabaseType.valueOf(databaseComboBox.getSelectedItem().toString()).name());
            Main.getSettingsManager().saveSettings();
            JOptionPane.showMessageDialog(null, "Please Restart Application");
        });
    }

    /**
     * The main method to run the SettingsGUI application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("SettingsGUI");
        frame.setContentPane(new SettingsGUI().panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.pack();
        frame.setVisible(true);
    }
}
