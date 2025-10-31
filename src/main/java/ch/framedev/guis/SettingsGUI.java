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
import ch.framedev.manager.Locale;
import ch.framedev.manager.LocaleManager;

import javax.swing.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents the graphical user interface for the application settings.
 * It provides a panel to display and modify various settings options.
 *
 * @author FrameDev
 * @since 25.02.2025 22:05
 */
@SuppressWarnings("unused")
public class SettingsGUI {
    private static JFrame frame;
    private JPanel panel;
    private JCheckBox useDatabaseCheckBox;
    private JComboBox<String> databaseComboBox;
    private JButton testSoundButton;
    private JPanel mysqlConnectionPanel;
    private JLabel hostLabel;
    private JTextField mysqlHostNameTextField;
    private JTextField mysqlPortTextField;
    private JTextField mysqlUserTextField;
    private JPasswordField mysqlPasswordField;
    private JTextField mysqlDatabaseTextField;
    private JButton testConnectionButton;
    private JPanel sqlitePanel;
    private JTextField sqlitePathTextField;
    private JTextField sqliteDatabaseTextField;
    private JPanel mongodbConnectionPanel;
    private JPanel connections;
    private JTextField mongoDbHostNameTextField;
    private JTextField mongoDbPortTextField;
    private JTextField mongoDbUsernameTextField;
    private JTextField mongoDbDatabaseTextField;
    private JPasswordField mongoDbPasswordField1;
    private JComboBox<String> languageComboBox;
    private JLabel databaseTypeLabel;

    public SettingsGUI() {
        databaseTypeLabel.setText(LocaleManager.LocaleSetting.DISPLAY_DATABASE_TYPE.getValue());

        mysqlHostNameTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mysql.host"), ""));
        mysqlPortTextField.setText(String.valueOf(Main.getSettingsManager().getConfiguration().getInt("database.mysql.port")));
        mysqlUserTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mysql.username"), ""));
        mysqlPasswordField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mysql.password"), ""));
        mysqlDatabaseTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mysql.database"), ""));

        sqlitePathTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.sqlite.path"), ""));
        sqliteDatabaseTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.sqlite.database"), ""));

        mongoDbHostNameTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mongodb.host"), ""));
        mongoDbPortTextField.setText(String.valueOf(Main.getSettingsManager().getConfiguration().getInt("database.mongodb.port")));
        mongoDbUsernameTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mongodb.username"), ""));
        mongoDbPasswordField1.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mongodb.password"), ""));
        mongoDbDatabaseTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mongodb.database"), ""));

        // Set the default database type based on the configuration
        useDatabaseCheckBox.setText(LocaleManager.LocaleSetting.DISPLAY_USE_DATABASE.getValue());
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
            Main.getDatabaseManager().setDatabaseType(DatabaseManager.DatabaseType.valueOf(Objects.requireNonNull(databaseComboBox.getSelectedItem()).toString()));
            Main.getSettingsManager().getConfiguration().set("database.databaseType", DatabaseManager.DatabaseType.valueOf(databaseComboBox.getSelectedItem().toString()).name());
            Main.getSettingsManager().saveSettings();
            JOptionPane.showMessageDialog(null, "Please Restart Application");
        });
        testSoundButton.addActionListener(e -> Main.getReminderScheduler().playSoundAsync());
        testConnectionButton.addActionListener(e -> {
            if(((String) Objects.requireNonNull(databaseComboBox.getSelectedItem())).equalsIgnoreCase("MySQL")) {
                String host = mysqlHostNameTextField.getText();
                String port = mysqlPortTextField.getText();
                String user = mysqlUserTextField.getText();
                String password = String.valueOf(mysqlPasswordField.getPassword());
                String database = mysqlDatabaseTextField.getText();

                if(host.isEmpty() || port.isEmpty() || user.isEmpty() || password.isEmpty() || database.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");
                    return;
                }
                Map<String, Object> parameters = Map.of("host", host, "port", Integer.parseInt(port), "username", user, "password", password, "database", database);
                Main.getSettingsManager().getConfiguration().set("database.mysql", parameters);
                Main.getSettingsManager().saveSettings();
                Main.setDatabaseManager(new DatabaseManager());
                Main.getDatabaseManager().getIDatabase().testConnection(parameters);
            } else if(((String) databaseComboBox.getSelectedItem()).equalsIgnoreCase("SQLite")) {
                String path = sqlitePathTextField.getText();
                String database = sqliteDatabaseTextField.getText();

                if(path.isEmpty() || database.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");
                    return;
                }
                Map<String, Object> parameters = Map.of("path", path, "database", database);
                Main.getSettingsManager().getConfiguration().set("database.sqlite", parameters);
                Main.getSettingsManager().saveSettings();
                Main.setDatabaseManager(new DatabaseManager());
                Main.getDatabaseManager().getIDatabase().testConnection(parameters);
            } else if(((String) databaseComboBox.getSelectedItem()).equalsIgnoreCase("MongoDB")) {
                String host = mongoDbHostNameTextField.getText();
                String port = mongoDbPortTextField.getText();
                String user = mongoDbUsernameTextField.getText();
                String password = String.valueOf(mongoDbPasswordField1.getPassword());
                String database = mongoDbDatabaseTextField.getText();

                if(host.isEmpty() || port.isEmpty() || user.isEmpty() || password.isEmpty() || database.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields");
                    return;
                }
                Map<String, Object> parameters = Map.of("host", host, "port", Integer.parseInt(port), "username", user, "password", password, "database", database);
                Main.getSettingsManager().getConfiguration().set("database.mongodb", parameters);
                Main.getSettingsManager().saveSettings();
                Main.setDatabaseManager(new DatabaseManager());
                Main.getDatabaseManager().getIDatabase().testConnection(parameters);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a database");
            }
        });
        frame.setJMenuBar(getJMenuBar());

        // Set the default language based on the configuration
        for (Locale locale : Arrays.asList(Locale.ENGLISH, Locale.GERMAN))
            languageComboBox.addItem(locale.getCode());

        languageComboBox.setSelectedItem(Main.getSettingsManager().getConfiguration().getString("language"));
        languageComboBox.addActionListener(e -> {
            String selectedLanguage = (String) languageComboBox.getSelectedItem();
            if (selectedLanguage == null) {
                return;
            }
            Main.getSettingsManager().getConfiguration().set("language", selectedLanguage);
            Main.getSettingsManager().saveSettings();
            JOptionPane.showMessageDialog(null, "Please Restart Application");
        });
    }

    private JMenuBar getJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create a top-level "Help" menu
        JMenu helpMenu = new JMenu("Help");

        // Create a menu item
        JMenuItem helpItem = new JMenuItem("View Help");
        helpItem.setToolTipText("Get help for the application (Ctrl+H)");
        helpItem.setAccelerator(KeyStroke.getKeyStroke("control H"));
        helpItem.addActionListener(e -> JOptionPane.showMessageDialog(null,
                """
                        This is a reminder app.
                        Use date format (yyyy-MM-dd)
                        Use time format (HH:mm)
                        Use comma-separated notes (e.g., Task, Reminder, Due Date)
                        Copyright 2025©. All rights reserved framedev
                        """
        ));

        // Add menu item to the menu and menu to the menu bar
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * The main method to run the SettingsGUI application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        frame = new JFrame("SettingsGUI");
        frame.setContentPane(new SettingsGUI().panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.pack();
        frame.setVisible(true);
    }
}
