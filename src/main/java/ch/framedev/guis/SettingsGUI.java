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
import java.util.Map;
import java.util.Objects;

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
    private JPasswordField mongoDbpasswordField1;

    public SettingsGUI() {
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
        mongoDbpasswordField1.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mongodb.password"), ""));
        mongoDbDatabaseTextField.setText(Objects.requireNonNullElse(Main.getSettingsManager().getConfiguration().getString("database.mongodb.database"), ""));

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
        testSoundButton.addActionListener(e -> {
            Main.getReminderScheduler().playSoundAsync();
        });
        testConnectionButton.addActionListener(e -> {
            if(((String) databaseComboBox.getSelectedItem()).equalsIgnoreCase("MySQL")) {
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
                String password = String.valueOf(mongoDbpasswordField1.getPassword());
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
