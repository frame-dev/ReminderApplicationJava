package ch.framedev.database;



/*
 * ch.framedev.database
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 26.02.2025 19:45
 */

import ch.framedev.utils.Setting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {

    public static final String TABLE_NAME = "reminder_data";
    public static Logger logger = LogManager.getLogger(DatabaseManager.class);

    private DatabaseType databaseType;
    private final IDatabase iDatabase;

    public DatabaseManager() {
        this.databaseType = DatabaseType.valueOf((String) Setting.DATABASE_TYPE.getValue("NONE"));

        switch (databaseType) {
            case SQLITE:
                iDatabase = new SQLiteManager();
                break;
            case MYSQL:
                iDatabase = new MySQLManager();
                break;
            case MONGODB:
                iDatabase = new MongoManager();
                break;
            default:
                iDatabase = null;
        }
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public IDatabase getIDatabase() {
        return iDatabase;
    }

    public boolean isDatabaseSupported() {
        if (iDatabase == null) return false;
        return databaseType != DatabaseType.NONE;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public static enum DatabaseType {
        SQLITE,
        MYSQL,
        MONGODB,
        NONE
    }
}
