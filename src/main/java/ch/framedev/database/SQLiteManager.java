package ch.framedev.database;



/*
 * ch.framedev.database
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 26.02.2025 19:46
 */

import ch.framedev.classes.CalendarEntry;
import ch.framedev.main.Main;
import ch.framedev.classes.Reminder;
import ch.framedev.utils.Setting;
import ch.framedev.javasqliteutils.SQLite;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static ch.framedev.main.Main.utils;

@SuppressWarnings("resource")
public class SQLiteManager implements IDatabase, IDatabaseCalendar {

    private final String tableName = DatabaseManager.TABLE_NAME;
    private final String[] columns = {"title", "description", "date", "time", "notes", "displayed"};
    private final String[] calendarColumns = {"calendarId", "title", "date", "fromDate", "toDate", "time", "fromTime", "toTime", "description"};

    @SuppressWarnings({"unchecked", "InstantiationOfUtilityClass"})
    public SQLiteManager() {
        Map<String, Object> sqliteConnections = (Map<String, Object>) Setting.SQLITE_CONNECTIONS.getValue().orElse(new HashMap<>());
        new SQLite(utils.getFilePath(Main.class) + sqliteConnections.get("path"), sqliteConnections.get("database").toString());
        createTable();
    }

    private void createTable() {
        String[] columns = {
                "title VARCHAR(255)",
                "description VARCHAR(255)",
                "date VARCHAR(255)",
                "time VARCHAR(255)",
                "notes VARCHAR(255)",
                "displayed INTEGER"
        };
        SQLite.createTable(tableName, true, columns);
        String[] calendarColumns = {
                "calendarId VARCHAR(255) PRIMARY KEY",
                "title VARCHAR(255)",
                "date VARCHAR(255)",
                "fromDate VARCHAR(255)",
                "toDate VARCHAR(255)",
                "time VARCHAR(255)",
                "fromTime VARCHAR(255)",
                "toTime VARCHAR(255)",
                "description VARCHAR(255)"
        };
        SQLite.createTable(DatabaseManager.CALENDAR_TABLE_NAME, true, calendarColumns);
    }

    @Override
    public boolean testConnection(Map<String, Object> parameters) {
        //noinspection InstantiationOfUtilityClass
        new SQLite(utils.getFilePath(Main.class) + parameters.get("path"), parameters.get("database").toString());
        try(Connection connection = SQLite.connect()) {
            return connection.isValid(3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insertCalendarEntry(CalendarEntry calendarEntry) {
        Object[] values = {
                calendarEntry.getCalendarId(),
                calendarEntry.getTitle(),
                calendarEntry.getDate(),
                calendarEntry.getFromDate(),
                calendarEntry.getToDate(),
                calendarEntry.getTime(),
                calendarEntry.getFromTime(),
                calendarEntry.getToTime(),
                calendarEntry.getDescription()
        };
        return SQLite.insertData(DatabaseManager.CALENDAR_TABLE_NAME, values, calendarColumns);
    }

    @Override
    public boolean updateCalendarEntry(CalendarEntry calendarEntry) {
        Object[] values = {
                calendarEntry.getCalendarId(),
                calendarEntry.getTitle(),
                calendarEntry.getDate(),
                calendarEntry.getFromDate(),
                calendarEntry.getToDate(),
                calendarEntry.getTime(),
                calendarEntry.getFromTime(),
                calendarEntry.getToTime(),
                calendarEntry.getDescription()
        };
        return SQLite.updateData(DatabaseManager.CALENDAR_TABLE_NAME, calendarColumns, values, "calendarId='" + calendarEntry.getCalendarId() + "'");
    }

    @Override
    public boolean deleteCalendarEntry(String calendarId) {
        return SQLite.deleteDataInTable(DatabaseManager.CALENDAR_TABLE_NAME, "calendarId='" + calendarId + "'");
    }

    @Override
    public CalendarEntry getCalendarEntryById(String calendarId) {
        Object[] values = SQLite.get(DatabaseManager.CALENDAR_TABLE_NAME, calendarColumns, "calendarId", calendarId).toArray();
        return new CalendarEntry((String) values[1], (String) values[8], (String) values[2], (String) values[3], (String) values[4]);
    }

    @Override
    public List<CalendarEntry> getAllCalendarEntries() {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        try (Statement statement = SQLite.connect().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.CALENDAR_TABLE_NAME)) {
            while (resultSet.next()) {
                CalendarEntry calendarEntry = new CalendarEntry(
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("date"),
                        resultSet.getString("fromDate"),
                        resultSet.getString("toDate")
                );
                calendarEntry.setTime(resultSet.getString("time"));
                calendarEntry.setFromTime(resultSet.getString("fromTime"));
                calendarEntry.setToTime(resultSet.getString("toTime"));
                calendarEntries.add(calendarEntry);
            }
        } catch (Exception ex) {
            DatabaseManager.logger.error("Exception", ex);
        }
        return calendarEntries;
    }

    @Override
    public List<CalendarEntry> getCalendarEntriesByDate(String date) {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        try (Statement statement = SQLite.connect().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DatabaseManager.CALENDAR_TABLE_NAME + " WHERE date='" + date + "'")) {
            while (resultSet.next()) {
                CalendarEntry calendarEntry = new CalendarEntry(
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("date"),
                        resultSet.getString("fromDate"),
                        resultSet.getString("toDate")
                );
                calendarEntry.setTime(resultSet.getString("time"));
                calendarEntry.setFromTime(resultSet.getString("fromTime"));
                calendarEntry.setToTime(resultSet.getString("toTime"));
                calendarEntries.add(calendarEntry);
            }
        } catch (Exception ex) {
            DatabaseManager.logger.error("Exception", ex);
        }
        return calendarEntries;
    }

    @Override
    public boolean existsCalendarEntry(String calendarId) {
        return SQLite.exists(DatabaseManager.CALENDAR_TABLE_NAME, "calendarId", calendarId);
    }

    @Override
    public void insertReminder(Reminder reminder) {
        Object[] values = {
                reminder.getTitle(),
                reminder.getMessage(),
                reminder.getDate(),
                reminder.getTime(),
                new Gson().toJson(reminder.getNotes()),
                reminder.isShow() ? 1 : 0
        };
        SQLite.insertData(tableName, values, columns);
    }

    @Override
    public void updateReminder(Reminder reminder) {
        if (notExistsReminder(reminder.getTitle())) return;
        Object[] values = {
                reminder.getTitle(),
                reminder.getMessage(),
                reminder.getDate(),
                reminder.getTime(),
                new Gson().toJson(reminder.getNotes()),
                reminder.isShow() ? 1 : 0
        };
        SQLite.updateData(tableName, columns, values, "title='" + reminder.getTitle() + "'");
    }

    @Override
    public void deleteReminder(String title) {
        SQLite.deleteDataInTable(tableName, "title='" + title + "'");
    }

    @Override
    public Reminder getReminderByTitle(String title) {
        if (notExistsReminder(title)) return null;
        Object[] values = SQLite.get(tableName, columns, "title", title).toArray();
        Type type = new TypeToken<List<String>>() {}.getType();
        Reminder reminder = new Reminder((String) values[0], (String) values[1], (String) values[2], (String) values[3], new Gson().fromJson((String) values[4], type));reminder.setShow(((int) values[5]) == 1);
        return reminder;
    }

    @Override
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        try (Statement statement = SQLite.connect().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {
            while (resultSet.next()) {
                Type type = new TypeToken<List<String>>() {}.getType();
                Reminder reminder = new Reminder(resultSet.getString("title"), resultSet.getString("description"),
                        resultSet.getString("date"), resultSet.getString("time"),
                        new Gson().fromJson(resultSet.getString("notes"), type));
                reminder.setShow(resultSet.getInt("displayed") == 1);
                reminders.add(reminder);
            }
        } catch (Exception ex) {
            DatabaseManager.logger.error("Exception", ex);
        }
        return reminders;
    }

    @Override
    public boolean notExistsReminder(String title) {
        return !SQLite.exists(tableName, "title", title);
    }

    @Override
    public boolean existsReminder(String title) {
        return SQLite.exists(tableName, "title", title);
    }
}