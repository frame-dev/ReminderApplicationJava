package ch.framedev.main;

/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 18:17
 */

import ch.framedev.classes.Reminder;
import ch.framedev.database.DatabaseManager;
import ch.framedev.database.IDatabase;
import ch.framedev.guis.ReminderGUI;
import ch.framedev.guis.ReminderView;
import ch.framedev.guis.SettingsGUI;
import ch.framedev.manager.Locale;
import ch.framedev.manager.LocaleManager;
import ch.framedev.manager.ReminderManager;
import ch.framedev.manager.SettingsManager;
import ch.framedev.simplejavautils.SimpleJavaUtils;
import ch.framedev.simplejavautils.SystemUtils;
import ch.framedev.utils.ReminderScheduler;
import ch.framedev.utils.Setting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Main {

    // initialization of the main class logger.
    private final static Logger logger = LogManager.getLogger(Main.class);

    public static SimpleJavaUtils utils = new SimpleJavaUtils();
    public static TrayIcon trayIcon;
    public static ReminderManager reminderManager;

    private static String lastNotificationTitle;

    private static SettingsManager settingsManager;
    private static DatabaseManager databaseManager;
    private static LocaleManager localeManager;
    private static ReminderScheduler reminderScheduler;

    /**
     * The main entry point of the Reminder Application.
     * <p>
     * This function initializes the application, sets up the system tray icon, creates a pop-up menu,
     * and starts the reminder scheduler.
     *
     * @param args Command-line arguments. Not used in this function.
     * @throws IOException If an I/O error occurs while reading the tray icon image.
     * @throws MalformedURLException If the URL for the tray icon image is malformed.
     */
    public static void main(String[] args) throws IOException {
        System.setProperty("apple.awt.UIElement", "true");
        System.setProperty("sun.java2d.uiScale", "2.0");
        settingsManager = new SettingsManager();
        databaseManager = new DatabaseManager();
        createSounds();

        setupLocaleFiles();
        localeManager = new LocaleManager(Locale.getByCode(settingsManager.getConfiguration().getString("language")));

        // Check if SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        // Hide ConsoleWindow for Windows
        hideConsoleWindow();

        // Create a pop-up menu
        final PopupMenu popup = new PopupMenu();
        try {
            trayIcon = createTrayIcon();
        } catch (Exception e) {
            getLogger().error("TrayIcon could not be created.", e);
            return;
        }
        final SystemTray systemTray = SystemTray.getSystemTray();

        MenuItem menuItem = new MenuItem(LocaleManager.LocaleSetting.DISPLAY_MENU.getValue());
        menuItem.addActionListener(e -> ReminderGUI.main(args));
        popup.add(menuItem);

        MenuItem settingsMenu = new MenuItem(LocaleManager.LocaleSetting.DISPLAY_SETTINGS.getValue());
        settingsMenu.addActionListener(e -> SettingsGUI.main(args));
        popup.add(settingsMenu);

        MenuItem exitItem = new MenuItem(LocaleManager.LocaleSetting.DISPLAY_EXIT.getValue());
        exitItem.addActionListener(e -> {
            reminderManager.saveReminders();
            System.exit(0);
        });
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        // Add the systemTray icon to the system systemTray
        try {
            trayIcon.setPopupMenu(popup);
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            getLogger().error("TrayIcon could not be added.", e);
        }

        // Load reminders from a JSON file
        reminderManager = new ReminderManager();

        // Start the reminder scheduler to check for upcoming reminders
        reminderScheduler = new ReminderScheduler(reminderManager.getReminderList());
        reminderScheduler.start();

        // Runtime.getRuntime().addShutdownHook(new Thread(() -> reminderScheduler.getScheduler().shutdown()));
    }

    public static Logger getLogger() {
        return logger;
    }

    private static void setupLocaleFiles() {
        File localeDir = new File(utils.getFilePath(Main.class), "locales");
        if (!localeDir.exists() && !localeDir.mkdirs()) {
            getLogger().error("Failed to create locale directory: {}", localeDir.getAbsolutePath());
            return;
        }

        String[] resourceLocales = {"locale_de-DE.yml", "locale_en-EN.yml"};

        for (String resourceLocale : resourceLocales) {
            File localeFile = new File(localeDir, resourceLocale);
            try {
                Path sourcePath = utils.getFromResourceFile("locales/" + resourceLocale, Main.class).toPath();
                Files.copy(sourcePath, localeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                getLogger().error("Error copying locale file: {}", resourceLocale, e);
            }
        }
    }

    public static LocaleManager getLocaleManager() {
        return localeManager;
    }

    public static boolean isDatabaseSupported() {
        if (!(boolean) Setting.USE_DATABASE.getValue(false)) return false;
        return databaseManager.isDatabaseSupported();
    }

    public static IDatabase getIDatabase() {
        return databaseManager.getIDatabase();
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static void setDatabaseManager(DatabaseManager databaseManager) {
        Main.databaseManager = databaseManager;
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

    private static Image getScaledImage(BufferedImage srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    public static TrayIcon createTrayIcon() throws Exception {
        final String TRAY_ICON_PATH = "/images/reminder_app_icon_256x256.png";
        try (InputStream is = Main.class.getResourceAsStream(TRAY_ICON_PATH)) {
            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                throw new IOException("Tray icon image not found!");
            }
            int trayIconWidth = (int) SystemTray.getSystemTray().getTrayIconSize().getWidth();
            int trayIconHeight = (int) SystemTray.getSystemTray().getTrayIconSize().getHeight();
            Image scaledImage = getScaledImage(image, trayIconWidth, trayIconHeight);
            TrayIcon trayIcon = new TrayIcon(scaledImage, "Reminder Application");
            trayIcon.setImageAutoSize(true); // Optional, but safe
            return trayIcon;
        }
    }
    
    /**
     * Opens the reminder window and initializes it with the last notification title.
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

    private static void createSounds() {
        File directory = new File(utils.getFilePath(Main.class), "sounds");
        if (!directory.exists()) {
            if(!directory.mkdirs()) {
                getLogger().error("Failed to create sounds directory: {}", directory.getAbsolutePath());
                return;
            }
        }
        File soundFile = new File(directory, "soft_bell_reminder.mp3");
        if(soundFile.exists()) if(!soundFile.delete()) {
            getLogger().error("Failed to delete existing sound file: {}", soundFile.getAbsolutePath());
            return;
        }
        try (
                InputStream inStream = Main.class.getResourceAsStream("/sounds/soft_bell_reminder.mp3");
                OutputStream outStream = new FileOutputStream(soundFile)
        ) {
            if (inStream == null) {
                throw new IOException("Resource not found: /sounds/soft_bell_reminder.mp3");
            }
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
            System.out.println("File is copied successfully!");
        } catch (Exception ex) {
            getLogger().error("Error copying sound file: {}", soundFile.getAbsolutePath(), ex);
        }
    }

    public static void exportResourceToFile(String resourcePath, File destinationFile) {
        // resourcePath should start with '/' for an absolute path in the JAR!
        try (InputStream stream = Main.class.getResourceAsStream(resourcePath);
             OutputStream resStreamOut = new FileOutputStream(destinationFile)) {

            if (stream == null) {
                throw new IOException("Cannot get resource \"" + resourcePath + "\" from Jar file.");
            }

            byte[] buffer = new byte[4096];
            int readBytes;
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            resStreamOut.flush();

        } catch (IOException ex) {
            System.err.println("Error exporting resource: " + ex.getMessage());
            getLogger().error("Error exporting resource: {}", resourcePath, ex);
        }
    }

    public static ReminderScheduler getReminderScheduler() {
        return reminderScheduler;
    }
}