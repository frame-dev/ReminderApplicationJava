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
        // Initialize the database type from settings, defaulting to NONE if not set
        this.databaseType = DatabaseType.valueOf((String) Setting.DATABASE_TYPE.getValue("NONE"));

        // Initialize the database manager based on the configured database type
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

        // Log the database type if a database instance is created
        if(iDatabase != null && isDatabaseSupported()) {
            logger.info("Database type set to: {}", databaseType);
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

    /**
     * Enum representing the supported database types.
     */
    public static enum DatabaseType {
        SQLITE,
        MYSQL,
        MONGODB,
        NONE
    }
}
