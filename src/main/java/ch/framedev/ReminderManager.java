package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 18:42
 */

import ch.framedev.javajsonutils.JsonUtils;
import ch.framedev.simplejavautils.SimpleJavaUtils;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReminderManager {

    private final List<Reminder> reminderList;
    private final List<Reminder> deletedReminderList;
    private final SimpleJavaUtils utils = new SimpleJavaUtils();

    public ReminderManager() {
        Type type = new TypeToken<List<Reminder>>(){}.getType();
        Type deletedType = new TypeToken<List<Reminder>>(){}.getType();
        List<Reminder> loadedReminders = new JsonUtils().getTypeFromJsonFile(
                new File(utils.getFilePath(Main.class), "reminders.json"), type
        );
        this.reminderList = (loadedReminders == null) ? new ArrayList<>() : loadedReminders;
        List<Reminder> loadedDeletedReminders = new JsonUtils().getTypeFromJsonFile(
                new File(utils.getFilePath(Main.class), "deleted_reminders.json"), deletedType
        );
        this.deletedReminderList = (loadedDeletedReminders == null) ? new ArrayList<>() : loadedDeletedReminders;
    }

    public void saveReminders() {
        try {
            new JsonUtils().saveJsonToFile(new File(utils.getFilePath(Main.class), "reminders.json"), reminderList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Reminder> getDeletedReminderList() {
        return deletedReminderList;
    }

    public void saveDeletedReminderList() {
        try {
            new JsonUtils().saveJsonToFile(new File(utils.getFilePath(Main.class), "deleted_reminders.json"), deletedReminderList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDeletedReminder(Reminder reminder) {
        deletedReminderList.add(reminder);
        saveDeletedReminderList();
    }

    public Optional<Reminder> getReminderByTitle(String title) {
        for (Reminder reminder : reminderList) {
            if (reminder.getTitle().equalsIgnoreCase(title)) {
                return Optional.of(reminder);
            }
        }
        return Optional.empty();
    }

    public List<Reminder> getReminderList() {
        return reminderList;
    }

    public void editReminder(Reminder reminder) {
        Optional<Reminder> existingReminder = getReminderByTitle(reminder.getTitle());
        existingReminder.ifPresent(r -> {
            r.setMessage(reminder.getMessage());
            r.setDate(reminder.getDate());
            r.setTime(reminder.getTime());
            r.setNotes(reminder.getNotes());
            saveReminders();
        });
        saveReminders();
    }

    public void deleteReminder(String title) {
        Optional<Reminder> existingReminder = getReminderByTitle(title);
        existingReminder.ifPresent(reminderList::remove);
        saveReminders();
    }

    public void addReminder(Reminder reminder) {
        reminderList.add(reminder);
        saveReminders();
    }
}
