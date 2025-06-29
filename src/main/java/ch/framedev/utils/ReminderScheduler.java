package ch.framedev.utils;

import ch.framedev.classes.Reminder;
import ch.framedev.main.Main;
import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ch.framedev.main.Main.reminderManager;
import static ch.framedev.main.Main.utils;

public class ReminderScheduler {
    private final List<Reminder> reminders;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ReminderScheduler(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkReminders, 0, 1, TimeUnit.SECONDS);
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Reminder reminder : reminders) {
            if (reminder.getDate() != null && reminder.getTime() != null) {
                LocalDateTime reminderDateTime = LocalDateTime.parse(reminder.getDate() + " " + reminder.getTime(), formatter);

                // if (reminderDateTime.isBefore(now.minusMinutes(1))) continue;

                if (!reminder.isShow() && now.getYear() == reminderDateTime.getYear() &&
                        now.getMonth() == reminderDateTime.getMonth() &&
                        now.getDayOfMonth() == reminderDateTime.getDayOfMonth() &&
                        now.getHour() == reminderDateTime.getHour() &&
                        now.getMinute() == reminderDateTime.getMinute()) {
                    reminder.setShow(true);
                    sendNotification(reminder);
                    playSoundAsync();
                    reminderManager.saveReminder(reminder);
                }
            }
        }
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void playSoundAsync() {
        new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(
                    new File(utils.getFilePath(Main.class), "sounds/soft_bell_reminder.mp3"))) {
                System.out.println("Starting sound...");
                Player player = new Player(fis);
                player.play(); // Blocks until done
                System.out.println("Finished playing sound.");
            } catch (Exception e) {
                System.err.println("Error playing sound: " + e.getMessage());
                Main.getLogger().error("Error playing sound: {}", e.getMessage(), e);
            }
        }).start();
    }

    private void sendNotification(Reminder reminder) {
        Main.showNotification(reminder.getTitle(), reminder.getMessage());
    }
}