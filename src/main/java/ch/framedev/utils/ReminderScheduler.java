package ch.framedev.utils;

import ch.framedev.classes.Reminder;
import ch.framedev.main.Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ch.framedev.main.Main.reminderManager;

public class ReminderScheduler {
    private final List<Reminder> reminders;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ReminderScheduler(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkReminders, 0, 30, TimeUnit.SECONDS);
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Reminder reminder : reminders) {
            if (reminder.getDate() != null && reminder.getTime() != null) {
                String reminderDateTime = reminder.getDate() + " " + reminder.getTime();
                if (now.format(formatter).equals(reminderDateTime) && !reminder.isShow()) {
                    reminder.setShow(true);
                    sendNotification(reminder);
                    reminderManager.saveReminders();
                }
            }
        }
    }

    private void sendNotification(Reminder reminder) {
        Main.showNotification(reminder.getTitle(), reminder.getMessage());
    }
}