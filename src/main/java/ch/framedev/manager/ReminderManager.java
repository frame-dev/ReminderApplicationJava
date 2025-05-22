package ch.framedev.manager;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 18:42
 */

import ch.framedev.classes.Reminder;
import ch.framedev.javajsonutils.JsonUtils;
import ch.framedev.main.Main;
import ch.framedev.simplejavautils.SimpleJavaUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReminderManager {

    private final Logger logger = LogManager.getLogger(ReminderManager.class);

    private final List<Reminder> reminderList;
    private final List<Reminder> deletedReminderList;
    private final SimpleJavaUtils utils = new SimpleJavaUtils();

    /**
     * Constructs a new ReminderManager instance.
     * This constructor initializes the reminderList and deletedReminderList by loading data from JSON files.
     * If the JSON files do not exist or contain invalid data, empty lists are created.
     */
    public ReminderManager() {
        if (!Main.isDatabaseSupported()) {
            Type type = new TypeToken<List<Reminder>>() {
            }.getType();
            Type deletedType = new TypeToken<List<Reminder>>() {
            }.getType();
            List<Reminder> loadedReminders = new JsonUtils().getTypeFromJsonFile(
                    new File(utils.getFilePath(Main.class), "reminders.json"), type
            );
            this.reminderList = (loadedReminders == null) ? new ArrayList<>() : loadedReminders;
            List<Reminder> loadedDeletedReminders = new JsonUtils().getTypeFromJsonFile(
                    new File(utils.getFilePath(Main.class), "deleted_reminders.json"), deletedType
            );
            this.deletedReminderList = (loadedDeletedReminders == null) ? new ArrayList<>() : loadedDeletedReminders;
        } else {
            Type deletedType = new TypeToken<List<Reminder>>() {
            }.getType();
            List<Reminder> loadedDeletedReminders = new JsonUtils().getTypeFromJsonFile(
                    new File(utils.getFilePath(Main.class), "deleted_reminders.json"), deletedType
            );
            this.deletedReminderList = (loadedDeletedReminders == null) ? new ArrayList<>() : loadedDeletedReminders;
            this.reminderList = Main.getDatabaseManager().getIDatabase().getAllReminders();
        }
    }

    /**
     * Saves the current list of reminders to a JSON file.
     * This method is used to persist the reminders between application sessions.
     *
     * @throws RuntimeException If an IOException occurs while saving the JSON file.
     */
    public void saveReminders() {
        if(Main.isDatabaseSupported()) {
            for(Reminder reminder : reminderList)
                Main.getDatabaseManager().getIDatabase().updateReminder(reminder);
            return;
        }
        try {
            new JsonUtils().saveJsonToFile(new File(utils.getFilePath(Main.class), "reminders.json"), reminderList);
        } catch (IOException e) {
            logger.error("Error saving reminders.json", e);
        }
    }

    public List<Reminder> getDeletedReminderList() {
        return deletedReminderList;
    }

    /**
     * Saves the current list of deleted reminders to a JSON file.
     * This method is used to persist the deleted reminders between application sessions.
     *
     * @throws RuntimeException If an IOException occurs while saving the JSON file.
     */
    public void saveDeletedReminderList() {
        try {
            new JsonUtils().saveJsonToFile(new File(utils.getFilePath(Main.class), "deleted_reminders.json"), deletedReminderList);
        } catch (IOException e) {
            logger.error("Error saving deleted_reminders.json", e);
        }
    }

    /**
     * Adds a reminder to the deleted reminder list and saves the updated list.
     * This method is typically used when a reminder is removed from the active list
     * but needs to be retained for historical or recovery purposes.
     *
     * @param reminder The Reminder object to be added to the deleted reminder list.
     *                 This should be a fully initialized Reminder instance that was
     *                 previously removed from the active reminder list.
     */
    public void addDeletedReminder(Reminder reminder) {
        deletedReminderList.add(reminder);
        saveDeletedReminderList();
    }

    /**
     * Retrieves a reminder from the reminder list based on its title.
     * The search is case-insensitive.
     *
     * @param title The title of the reminder to search for.
     * @return An Optional containing the Reminder if found, or an empty Optional if not found.
     */
    public Optional<Reminder> getReminderByTitle(String title) {
        if (Main.isDatabaseSupported()) {
            return Optional.ofNullable(Main.getDatabaseManager().getIDatabase().getReminderByTitle(title));
        } else {
            for (Reminder reminder : reminderList) {
                if (reminder.getTitle().equalsIgnoreCase(title)) {
                    return Optional.of(reminder);
                }
            }
            return Optional.empty();
        }
    }

    public List<Reminder> getReminderList() {
        if (Main.isDatabaseSupported()) {
            return Main.getDatabaseManager().getIDatabase().getAllReminders();
        } else
            return reminderList;
    }

    /**
     * Edits an existing reminder in the reminder list based on its title.
     * If a reminder with the given title is found, its message, date, time, and notes
     * are updated with the values from the provided Reminder object.
     * The updated reminder list is then saved to the JSON file.
     *
     * @param reminder The Reminder object containing the updated information for the reminder.
     *                 The title of the reminder is used to identify the existing reminder to be edited.
     *                 The Reminder object should have the updated message, date, time, and notes.
     *                 All fields are required and should be non-null.
     */
    public void editReminder(Reminder reminder) {
        Optional<Reminder> existingReminder = getReminderByTitle(reminder.getTitle());
        existingReminder.ifPresent(r -> {
            r.setMessage(reminder.getMessage());
            r.setDate(reminder.getDate());
            r.setTime(reminder.getTime());
            r.setNotes(reminder.getNotes());
        });
        if (Main.isDatabaseSupported()) {
            Main.getIDatabase().updateReminder(reminder);
        } else
            saveReminders();
    }

    /**
     * Deletes a reminder from the reminder list based on its title.
     * If a reminder with the given title is found, it is removed from the list and saved to the JSON file.
     *
     * @param title The title of the reminder to be deleted. This should be a non-null and non-empty string.
     *              The search for the reminder is case-insensitive.
     * @throws IllegalArgumentException If the provided title is null or empty.
     */
    public void deleteReminder(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (Main.isDatabaseSupported()) {
            Main.getDatabaseManager().getIDatabase().deleteReminder(title);
        } else {
            reminderList.removeIf(r -> r.getTitle().equalsIgnoreCase(title));
            Optional<Reminder> existingReminder = getReminderByTitle(title);
            existingReminder.ifPresent(reminderList::remove);
            saveReminders();
        }
    }

    /**
     * Adds a new reminder to the reminder list and saves the updated list.
     *
     * @param reminder The Reminder object to be added to the list.
     *                 This should be a fully initialized Reminder instance
     *                 containing all necessary information such as title,
     *                 message, date, and time.
     */
    public void addReminder(Reminder reminder) {
        if (Main.isDatabaseSupported()) {
            Main.getDatabaseManager().getIDatabase().insertReminder(reminder);
        } else {
            reminderList.add(reminder);
            saveReminders();
        }
    }
}
