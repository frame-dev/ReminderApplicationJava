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
import ch.framedev.javamysqlutils.MySQLV2;
import ch.framedev.utils.Setting;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class MySQLManager implements IDatabase {

    private final String tableName = DatabaseManager.TABLE_NAME;
    private final String[] columns = {"title", "description", "date", "time", "notes", "displayed"};
    private MySQLV2 mySQLV2;

    @SuppressWarnings("unchecked")
    public MySQLManager() {
        Map<String, Object> mysqlConnections = (Map<String, Object>) Setting.MYSQL_CONNECTIONS.getValue().orElse(new HashMap<>());
        mySQLV2 = new MySQLV2((String) mysqlConnections.get("host"),
                (String) mysqlConnections.get("username"),
                (String) mysqlConnections.get("password"),
                (Integer) mysqlConnections.get("port"),
                mysqlConnections.get("database").toString());
        createTable();
    }

    private void createTable() {
        String[] columns = {
                "title VARCHAR(255)",
                "description VARCHAR(255)",
                "date VARCHAR(255)",
                "time VARCHAR(255)",
                "notes VARCHAR(255)",
                "displayed BOOLEAN"
        };
        mySQLV2.createTable(tableName, columns);
    }

    @Override
    public boolean testConnection(Map<String, Object> parameters) {
        mySQLV2 = new MySQLV2.Builder()
                .host((String) parameters.get("host"))
                .port((int) parameters.get("port"))
                .user((String) parameters.get("username"))
                .password((String) parameters.get("password"))
                .database((String) parameters.get("database")).build();
        try {
            return mySQLV2.getConnection().isValid(300);
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
                new Gson().toJson(reminder.getNotes()),
                reminder.isShow()
        };
        mySQLV2.insertData(tableName, values, columns);
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
                reminder.isShow()
        };
        mySQLV2.updateData(tableName, columns, values, "title='" + reminder.getTitle() + "'");
    }

    @Override
    public void deleteReminder(String title) {
        mySQLV2.deleteDataInTable(tableName, "title='" + title + "'");
    }

    @Override
    public Reminder getReminderByTitle(String title) {
        if (notExistsReminder(title)) return null;
        Object[] values = mySQLV2.get(tableName, columns, "title", title).toArray();
        Type type = new TypeToken<List<String>>() {}.getType();
        Reminder reminder = new Reminder((String) values[0], (String) values[1], (String) values[2], (String) values[3], new Gson().fromJson((String) values[4], type));
        reminder.setShow(values[5].toString().equalsIgnoreCase("0"));
        return reminder;
    }

    @Override
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        try (Statement statement = mySQLV2.getConnection().createStatement();
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
        return !mySQLV2.exists(tableName, "title", title);
    }

    @Override
    public boolean existsReminder(String title) {
        return !mySQLV2.exists(tableName, "title", title);
    }
}
