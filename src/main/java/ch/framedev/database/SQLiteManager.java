package ch.framedev.database;



/*
 * ch.framedev.database
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 26.02.2025 19:46
 */

import ch.framedev.main.Main;
import ch.framedev.classes.Reminder;
import ch.framedev.utils.Setting;
import ch.framedev.javasqliteutils.SQLite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static ch.framedev.main.Main.utils;

public class SQLiteManager implements IDatabase {

    private final String tableName = DatabaseManager.TABLE_NAME;
    private final String[] columns = {"title", "description", "date", "time", "notes", "show"};

    @SuppressWarnings({"unchecked", "InstantiationOfUtilityClass"})
    public SQLiteManager() {
        Map<String, Object> sqliteConnections = (Map<String, Object>) Setting.SQLITE_CONNECTIONS.getValue(new HashMap<>());
        new SQLite(utils.getFilePath(Main.class) + sqliteConnections.get("path"), sqliteConnections.get("database").toString());
        createTable();
    }

    private void createTable() {
        String[] columns = {
                "title VARCHAR(255)",
                "description VARCHAR(255)",
                "date DATE",
                "time TIME",
                "notes VARCHAR(255)",
                "`show` BOOLEAN"
        };
        SQLite.createTable(tableName, true, columns);
    }

    @Override
    public boolean testConnection(Map<String, Object> parameters) {
        new SQLite(utils.getFilePath(Main.class) + parameters.get("path"), parameters.get("database").toString());
        try(Connection connection = SQLite.connect()) {
            return connection.isValid(3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertReminder(Reminder reminder) {
        Object[] values = {
                reminder.getTitle(),
                reminder.getMessage(),
                reminder.getDate(),
                reminder.getTime(),
                reminder.getNotes().toString(),
                reminder.isShow()
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
                reminder.getNotes().toString(),
                reminder.isShow()
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
        Reminder reminder = new Reminder((String) values[0], (String) values[1], (String) values[2], (String) values[3], List.of(((String) values[4]).split(", ")));
        reminder.setShow(values[5].toString().equalsIgnoreCase("0"));
        return reminder;
    }

    @SuppressWarnings("resource")
    @Override
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        try (Statement statement = SQLite.connect().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {
            while (resultSet.next()) {
                Reminder reminder = new Reminder(resultSet.getString("title"), resultSet.getString("description"),
                        resultSet.getString("date"), resultSet.getString("time"),
                        Arrays.asList(resultSet.getString("notes").split(", ")));
                reminder.setShow(resultSet.getBoolean("show"));
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
