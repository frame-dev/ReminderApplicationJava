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
    public static final String CALENDAR_TABLE_NAME = "calendar_data";
    public static Logger logger = LogManager.getLogger(DatabaseManager.class);

    private DatabaseType databaseType;
    private final IDatabase iDatabase;
    private final IDatabaseCalendar iDatabaseCalendar;

    public DatabaseManager() {
        // Safe parse of configured database type, defaulting to NONE
        Object configured = Setting.DATABASE_TYPE.getValue("NONE");
        String configuredStr = configured.toString().trim().toUpperCase();
        DatabaseType resolvedType;
        try {
            resolvedType = DatabaseType.valueOf(configuredStr);
        } catch (IllegalArgumentException ex) {
            logger.warn("Unknown database type '{}', falling back to NONE", configuredStr);
            resolvedType = DatabaseType.NONE;
        }

        // Temporary holders for implementations
        IDatabase tempDb;
        IDatabaseCalendar tempDbCal;

        switch (resolvedType) {
            case SQLITE -> {
                SQLiteManager sqliteManager = new SQLiteManager();
                tempDb = sqliteManager;
                tempDbCal = sqliteManager;
            }
            case MYSQL -> {
                MySQLManager mySQLManager = new MySQLManager();
                tempDb = mySQLManager;
                tempDbCal = mySQLManager;
            }
            case MONGODB -> {
                MongoManager mongoManager = new MongoManager();
                tempDb = mongoManager;
                tempDbCal = mongoManager;
            }
            case NONE -> {
                if(!((Boolean) Setting.USE_DATABASE.getValue(false))) {
                    tempDb = null;
                    tempDbCal = null;
                    break;
                }
                String preferred = String.valueOf(Setting.PREFERRED_DATABASE.getValue("mysql"));
                if (preferred == null) preferred = "mysql";
                preferred = preferred.trim().toLowerCase();
                switch (preferred) {
                    case "mysql" -> {
                        MySQLManager mySQLManager = new MySQLManager();
                        tempDb = mySQLManager;
                        tempDbCal = mySQLManager;
                        resolvedType = DatabaseType.MYSQL;
                    }
                    case "sqlite" -> {
                        SQLiteManager sqliteManager = new SQLiteManager();
                        tempDb = sqliteManager;
                        tempDbCal = sqliteManager;
                        resolvedType = DatabaseType.SQLITE;
                    }
                    case "mongodb" -> {
                        MongoManager mongoManager = new MongoManager();
                        tempDb = mongoManager;
                        tempDbCal = mongoManager;
                        resolvedType = DatabaseType.MONGODB;
                    }
                    default -> {
                        tempDb = null;
                        tempDbCal = null;
                        logger.warn("No valid preferred database type configured ('{}'). Database functionalities will be disabled.", preferred);
                    }
                }
            }
            default -> {
                tempDb = null;
                tempDbCal = null;
                logger.warn("No valid database type configured. Database functionalities will be disabled.");
            }
        }

        // Assign final fields
        this.databaseType = resolvedType;
        this.iDatabase = tempDb;
        this.iDatabaseCalendar = tempDbCal;

        if (this.iDatabase != null && isDatabaseSupported()) {
            logger.info("Database type set to: {}", this.databaseType);
        }
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public IDatabase getIDatabase() {
        return iDatabase;
    }

    public IDatabaseCalendar getIDatabaseCalendar() {
        return iDatabaseCalendar;
    }

    public boolean isDatabaseSupported() {
        return iDatabase != null && iDatabaseCalendar != null && databaseType != DatabaseType.NONE;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    /**
     * Enum representing the supported database types.
     */
    public enum DatabaseType {
        SQLITE,
        MYSQL,
        MONGODB,
        NONE
    }
}