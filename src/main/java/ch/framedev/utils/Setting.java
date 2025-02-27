package ch.framedev.utils;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 23:11
 */

import ch.framedev.main.Main;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a setting in the application.
 * Each setting has a unique key and can be retrieved, updated, or deleted.
 */
public enum Setting {

    /**
     * The language setting.
     */
    LANGUAGE("language"),

    USE_DATABASE("useDatabase"),
    DATABASE_TYPE("database|databaseType"),
    PREFERRED_DATABASE("database|preferred"),
    MYSQL_CONNECTIONS("database|mysql"),
    SQLITE_CONNECTIONS("database|sqlite"),
    MONGODB_CONNECTIONS("database|mongodb");

    final static Map<String, Object> data = Main.getSettingsManager().getData();

    final String key;

    Setting(String key) {
        this.key = key;
    }

    /**
     * Returns the key of the setting.
     *
     * @return the key of the setting
     */
    public String getKey() {
        return key;
    }

    /**
     * Retrieves the value of the setting from the settings manager.
     *
     * @return the value of the setting, or null if the setting does not exist
     */
    @SuppressWarnings({"ReassignedVariable", "unchecked"})
    public Optional<Object> getValue() {
        String[] keyData = key.split("\\|");
        Map<String, Object> currentMap = data;
        Object value = null;

        for (String keyDatum : keyData) {
            if (currentMap == null) return Optional.empty();
            value = currentMap.get(keyDatum);
            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return Optional.ofNullable(value);
            }
        }
        return Optional.ofNullable(value);
    }

    /**
     * Retrieves the value of the setting from the settings manager.
     * If the setting does not exist, the provided default value is returned.
     *
     * @param defaultValue the default value to return if the setting does not exist
     * @return the value of the setting, or the default value if the setting does not exist
     */
    public Object getValue(Object defaultValue) {
        return getValue().orElse(defaultValue);
    }

    /**
     * Updates the value of the setting in the settings manager.
     *
     * @param value the new value of the setting
     */
    public void setValue(Object value) {
        Main.getSettingsManager().getData().put(key, value);
        Main.getSettingsManager().saveSettings();
    }
}
