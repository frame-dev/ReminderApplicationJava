package ch.framedev.database;



/*
 * ch.framedev.database
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 26.02.2025 19:46
 */

import ch.framedev.classes.Reminder;
import ch.framedev.utils.Setting;
import ch.framedev.javamongodbutils.BackendMongoDBManager;
import ch.framedev.javamongodbutils.MongoDBManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoManager implements IDatabase {

    private BackendMongoDBManager bMongoDBManager;

    @SuppressWarnings("unchecked")
    public MongoManager() {
        Map<String, Object> mongoDbConnections = (Map<String, Object>) Setting.MONGODB_CONNECTIONS.getValue().orElse(new HashMap<>());
        MongoDBManager mongoDBManager = new MongoDBManager((String) mongoDbConnections.get("host"),
                (String) mongoDbConnections.get("username"),
                (String) mongoDbConnections.get("password"),
                (int) mongoDbConnections.get("port"),
                (String) mongoDbConnections.get("database"));
        mongoDBManager.connect();
        bMongoDBManager = new BackendMongoDBManager(mongoDBManager);
    }

    @Override
    public boolean testConnection(Map<String, Object> parameters) {
        MongoDBManager mongoDBManager = new MongoDBManager((String) parameters.get("host"),
                (String) parameters.get("username"),
                (String) parameters.get("password"),
                (int) parameters.get("port"),
                (String) parameters.get("database"));
        mongoDBManager.connect();
        bMongoDBManager = new BackendMongoDBManager(mongoDBManager);
        Document result = mongoDBManager.getDatabase().runCommand(new Document("ping", 1));
        return result.getDouble("ok") == 1.0;
    }

    @Override
    public void insertReminder(Reminder reminder) {
        bMongoDBManager.createData("title", reminder.getTitle(), reminder, DatabaseManager.TABLE_NAME);
    }

    @Override
    public void updateReminder(Reminder reminder) {
        bMongoDBManager.updateAll("title", reminder.getTitle(), reminder, DatabaseManager.TABLE_NAME);
    }

    @Override
    public void deleteReminder(String title) {
        bMongoDBManager.removeDocument("title", title, DatabaseManager.TABLE_NAME);
    }

    @Override
    public Reminder getReminderByTitle(String title) {
        if(notExistsReminder(title)) return null;
        return bMongoDBManager.getObjectFromJson("title", title, DatabaseManager.TABLE_NAME, Reminder.class);
    }

    @Override
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        for(Document document : bMongoDBManager.getAllDocuments(DatabaseManager.TABLE_NAME)) {
            reminders.add(bMongoDBManager.getObjectFromJson("_id", document.getObjectId("_id"), DatabaseManager.TABLE_NAME, Reminder.class));
        }
        return reminders;
    }

    @Override
    public boolean notExistsReminder(String title) {
        return !bMongoDBManager.exists("title", title, DatabaseManager.TABLE_NAME);
    }

    @Override
    public boolean existsReminder(String title) {
        return bMongoDBManager.exists("title", title, DatabaseManager.TABLE_NAME);
    }
}
