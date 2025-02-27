package ch.framedev.database;



/*
 * ch.framedev.database
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 26.02.2025 19:45
 */

import ch.framedev.classes.Reminder;

import java.util.List;

public interface IDatabase {

    void insertReminder(Reminder reminder);
    void updateReminder(Reminder reminder);
    void deleteReminder(String title);
    Reminder getReminderByTitle(String title);
    List<Reminder> getAllReminders();
    boolean notExistsReminder(String title);
    boolean existsReminder(String title);
}
