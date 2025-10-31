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
import ch.framedev.classes.Reminder;
import ch.framedev.utils.Setting;
import ch.framedev.javamongodbutils.BackendMongoDBManager;
import ch.framedev.javamongodbutils.MongoDBManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoManager implements IDatabase, IDatabaseCalendar {

    private BackendMongoDBManager bMongoDBManager;

    @SuppressWarnings("unchecked")
    public MongoManager() {
        Map<String, Object> mongoDbConnections = (Map<String, Object>) Setting.MONGODB_CONNECTIONS.getValue().orElse(new HashMap<>());
        String host = safeToString(mongoDbConnections.get("host"), "localhost");
        String username = safeToString(mongoDbConnections.get("username"), null);
        String password = safeToString(mongoDbConnections.get("password"), null);
        int port = safeToInt(mongoDbConnections.get("port"));
        String database = safeToString(mongoDbConnections.get("database"), "test");

        try {
            MongoDBManager mongoDBManager = new MongoDBManager(host, username, password, port, database);
            mongoDBManager.connect();
            bMongoDBManager = new BackendMongoDBManager(mongoDBManager);
        } catch (Exception ex) {
            System.err.println("Failed to initialize MongoManager: " + ex.getMessage());
            bMongoDBManager = null;
        }
    }

    @Override
    public boolean testConnection(Map<String, Object> parameters) {
        String host = safeToString(parameters.get("host"), "localhost");
        String username = safeToString(parameters.get("username"), null);
        String password = safeToString(parameters.get("password"), null);
        int port = safeToInt(parameters.get("port"));
        String database = safeToString(parameters.get("database"), "test");

        MongoDBManager tmpManager;
        try {
            tmpManager = new MongoDBManager(host, username, password, port, database);
            tmpManager.connect();
            Document result = tmpManager.getDatabase().runCommand(new Document("ping", 1));
            Object ok = result.get("ok");
            if (ok instanceof Number) {
                return ((Number) ok).doubleValue() == 1.0;
            }
            return "1".equals(String.valueOf(ok)) || "1.0".equals(String.valueOf(ok));
        } catch (Exception ex) {
            System.err.println("MongoDB testConnection failed: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean insertCalendarEntry(CalendarEntry calendarEntry) {
        if (bMongoDBManager == null) return false;
        bMongoDBManager.createData("calendarId", calendarEntry.getCalendarId(), calendarEntry, DatabaseManager.CALENDAR_TABLE_NAME);
        return bMongoDBManager.exists("calendarId", calendarEntry.getCalendarId(), DatabaseManager.CALENDAR_TABLE_NAME);
    }

    @Override
    public boolean updateCalendarEntry(CalendarEntry calendarEntry) {
        if (bMongoDBManager == null) return false;
        bMongoDBManager.updateAll("calendarId", calendarEntry.getCalendarId(), calendarEntry, DatabaseManager.CALENDAR_TABLE_NAME);
        return bMongoDBManager.exists("calendarId", calendarEntry.getCalendarId(), DatabaseManager.CALENDAR_TABLE_NAME);
    }

    @Override
    public boolean deleteCalendarEntry(String calendarId) {
        if (bMongoDBManager == null) return false;
        bMongoDBManager.removeDocument("calendarId", calendarId, DatabaseManager.CALENDAR_TABLE_NAME);
        return !bMongoDBManager.exists("calendarId", calendarId, DatabaseManager.CALENDAR_TABLE_NAME);
    }

    @Override
    public CalendarEntry getCalendarEntryById(String id) {
        if (bMongoDBManager == null) return null;
        if (!existsCalendarEntry(id)) return null;
        return bMongoDBManager.getObjectFromJson("calendarId", id, DatabaseManager.CALENDAR_TABLE_NAME, CalendarEntry.class);
    }

    @Override
    public List<CalendarEntry> getAllCalendarEntries() {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        if (bMongoDBManager == null) return calendarEntries;
        List<Document> documents = bMongoDBManager.getAllDocuments(DatabaseManager.CALENDAR_TABLE_NAME);
        if (documents == null) return calendarEntries;
        for (Document document : documents) {
            Object idObj = document.getObjectId("_id");
            if (idObj != null) {
                calendarEntries.add(bMongoDBManager.getObjectFromJson("_id", idObj, DatabaseManager.CALENDAR_TABLE_NAME, CalendarEntry.class));
            }
        }
        return calendarEntries;
    }

    @Override
    public List<CalendarEntry> getCalendarEntriesByDate(String date) {
        List<CalendarEntry> calendarEntries = new ArrayList<>();
        if (bMongoDBManager == null) return calendarEntries;
        List<Document> documents = bMongoDBManager.getAllDocuments("date", date, DatabaseManager.CALENDAR_TABLE_NAME);
        if (documents == null) return calendarEntries;
        for (Document document : documents) {
            Object idObj = document.getObjectId("_id");
            if (idObj != null) {
                calendarEntries.add(bMongoDBManager.getObjectFromJson("_id", idObj, DatabaseManager.CALENDAR_TABLE_NAME, CalendarEntry.class));
            }
        }
        return calendarEntries;
    }

    @Override
    public boolean existsCalendarEntry(String id) {
        if (bMongoDBManager == null) return false;
        return bMongoDBManager.exists("calendarId", id, DatabaseManager.CALENDAR_TABLE_NAME);
    }

    @Override
    public void insertReminder(Reminder reminder) {
        if (bMongoDBManager == null) return;
        bMongoDBManager.createData("title", reminder.getTitle(), reminder, DatabaseManager.TABLE_NAME);
    }

    @Override
    public void updateReminder(Reminder reminder) {
        if (bMongoDBManager == null) return;
        bMongoDBManager.updateAll("title", reminder.getTitle(), reminder, DatabaseManager.TABLE_NAME);
    }

    @Override
    public void deleteReminder(String title) {
        if (bMongoDBManager == null) return;
        bMongoDBManager.removeDocument("title", title, DatabaseManager.TABLE_NAME);
    }

    @Override
    public Reminder getReminderByTitle(String title) {
        if (bMongoDBManager == null) return null;
        if (notExistsReminder(title)) return null;
        return bMongoDBManager.getObjectFromJson("title", title, DatabaseManager.TABLE_NAME, Reminder.class);
    }

    @Override
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        if (bMongoDBManager == null) return reminders;
        List<Document> docs = bMongoDBManager.getAllDocuments(DatabaseManager.TABLE_NAME);
        if (docs == null) return reminders;
        for (Document document : docs) {
            Object idObj = document.getObjectId("_id");
            if (idObj != null) {
                reminders.add(bMongoDBManager.getObjectFromJson("_id", idObj, DatabaseManager.TABLE_NAME, Reminder.class));
            }
        }
        return reminders;
    }

    @Override
    public boolean notExistsReminder(String title) {
        if (bMongoDBManager == null) return true;
        return !bMongoDBManager.exists("title", title, DatabaseManager.TABLE_NAME);
    }

    @Override
    public boolean existsReminder(String title) {
        if (bMongoDBManager == null) return false;
        return bMongoDBManager.exists("title", title, DatabaseManager.TABLE_NAME);
    }

    private static int safeToInt(Object val) {
        if (val == null) return 27017;
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return 27017;
        }
    }

    private static String safeToString(Object val, String defaultVal) {
        if (val == null) return defaultVal;
        return String.valueOf(val);
    }
}