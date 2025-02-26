package ch.framedev;

/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 18:17
 */

import ch.framedev.simplejavautils.SimpleJavaUtils;
import ch.framedev.simplejavautils.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    // initialization of the main class logger.
    private final static Logger logger = LogManager.getLogger(Main.class);

    public static SimpleJavaUtils utils = new SimpleJavaUtils();
    public static TrayIcon trayIcon;
    public static ReminderManager reminderManager;

    private static String lastNotificationTitle;

    private static SettingsManager settingsManager;

    /**
     * The main entry point of the Reminder Application.
     * <p>
     * This function initializes the application, sets up the system tray icon, creates a pop-up menu,
     * and starts the reminder scheduler.
     *
     * @param args Command-line arguments. Not used in this function.
     * @throws MalformedURLException If the URL for the tray icon image is malformed.
     */
    public static void main(String[] args) throws MalformedURLException {
        settingsManager = new SettingsManager();

        System.setProperty("apple.awt.UIElement", "true");

        // Check if SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        // Hide ConsoleWindow for Windows
        hideConsoleWindow();
        Image image = Toolkit.getDefaultToolkit().getImage(utils.getFromResourceFile("images/tray-icon.png", Main.class).toURI().toURL());

        // Create a pop-up menu
        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(image, "Reminder APP", popup);
        final SystemTray systemTray = SystemTray.getSystemTray();
        MenuItem menuItem = new MenuItem("Show Menu");
        menuItem.addActionListener(e -> {
            ReminderGUI.main(args);
        });
        popup.add(menuItem);

        MenuItem settingsMenu = new MenuItem("Settings");
        settingsMenu.addActionListener(e -> {
            SettingsGUI.main(args);
        });
        popup.add(settingsMenu);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            reminderManager.saveReminders();
            System.exit(1);
        });
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        // Add the systemTray icon to the system systemTray
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            getLogger().error("TrayIcon could not be added.", e);
        }
        // Load reminders from JSON file
        reminderManager = new ReminderManager();

        // Start the reminder scheduler to check for upcoming reminders
        ReminderScheduler reminderScheduler = new ReminderScheduler(reminderManager.getReminderList());
        reminderScheduler.start();
    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * Displays a system tray notification and stores the title.
     */
    public static void showNotification(String title, String message) {
        lastNotificationTitle = title;
        if (new SystemUtils().getOSType() == SystemUtils.OSType.MACOS) {
            String notificationScript = "display notification \"" + "Message: " + message + "\" with title \"" + "Title: " + title + "\" sound name \"default\"";

            // AppleScript command to show a dialog with "Show" and "Dismiss" buttons
            String dialogScript = "set userChoice to button returned of (display dialog \"" + message + "\" buttons {\"Show\", \"Dismiss\"} default button \"Show\")\n"
                    + "return userChoice";

            try {
                // Show macOS notification
                new ProcessBuilder("osascript", "-e", notificationScript).start();

                // Show dialog and capture user response
                Process process = new ProcessBuilder("osascript", "-e", dialogScript).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String userResponse = reader.readLine();

                if ("Show".equals(userResponse)) {
                    System.out.println("User clicked 'Show'! Opening reminder...");
                    openReminderWindow();
                }

            } catch (IOException e) {
                System.out.println("Failed to send macOS notification: " + e.getMessage());
                getLogger().error("Failed to send macOS notification.", e);
            }
        } else {
            if (trayIcon != null)
                trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }

    /**
     * Opens a dummy reminder window (replace with your actual UI).
     */
    private static void openReminderWindow() {
        System.out.println("Opening Reminder Details...");
        ReminderView.main(new String[0]);
        Reminder reminder = reminderManager.getReminderByTitle(lastNotificationTitle).orElse(null);
        if (reminder == null) {
            System.out.println("Reminder not found!");
            return;
        }
        ReminderView.init(reminder);
    }

    /**
     * Hides the console window on Windows when running as a console application.
     */
    private static void hideConsoleWindow() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                java.awt.Window win = new java.awt.Frame();
                win.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Failed to hide console window: " + e.getMessage());
            getLogger().error("Failed to hide console window.", e);
        }
    }

    /**
     * Retrieves the singleton instance of the SettingsManager class.
     * <p>
     * The SettingsManager class is responsible for managing application settings, such as reminder intervals,
     * notification preferences, and other configurable options. This method provides access to the single instance
     * of the SettingsManager, allowing other classes to interact with the settings.
     *
     * @return The singleton instance of the SettingsManager class.
     */
    public static SettingsManager getSettingsManager() {
        return settingsManager;
    }
}