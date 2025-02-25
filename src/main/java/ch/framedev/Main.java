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

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static SimpleJavaUtils utils = new SimpleJavaUtils();
    public static TrayIcon trayIcon;
    public static ReminderManager reminderManager;
    private static String lastNotificationTitle;

    //
    public static void main(String[] args) throws MalformedURLException {
        System.setProperty("apple.awt.UIElement", "true");
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        hideConsoleWindow();
        Image image = Toolkit.getDefaultToolkit().getImage(utils.getFromResourceFile("images/tray-icon.png", Main.class).toURI().toURL());

        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(image, "Reminder APP", popup);
        final SystemTray tray = SystemTray.getSystemTray();
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

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
        reminderManager = new ReminderManager();
        ReminderScheduler reminderScheduler = new ReminderScheduler(reminderManager.getReminderList());
        reminderScheduler.start();
    }

    /**
     * Displays a system tray notification and stores the title.
     */
    public static void showNotification(String title, String message) {
        lastNotificationTitle = title;
        if(new SystemUtils().getOSType() == SystemUtils.OSType.MACOS) {
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
            }
        } else {
            if(trayIcon != null)
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
        if(reminder == null) {
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
            e.printStackTrace();
        }
    }
}