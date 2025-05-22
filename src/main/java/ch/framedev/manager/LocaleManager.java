package ch.framedev.manager;



/*
 * ch.framedev.manager
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 26.02.2025 22:16
 */

import ch.framedev.main.Main;
import ch.framedev.yamlutils.FileConfiguration;

import java.io.File;

public record LocaleManager(Locale locale) {

    private static FileConfiguration fileConfiguration;

    public LocaleManager(Locale locale) {
        this.locale = locale;
        // Initialize localization based on the provided locale
        File localeFile = new File(Main.utils.getFilePath(Main.class) + "locales/" + "locale_" + locale.getCode() + ".yml");
        fileConfiguration = new FileConfiguration(localeFile);
        fileConfiguration.load();
    }

    public String getString(String key) {
        return fileConfiguration.getString(key);
    }

    public static enum LocaleSetting {

        DISPLAY_MENU("displayMenu"),
        DISPLAY_SETTINGS("displaySettings"),
        DISPLAY_EXIT("exit"),
        DISPLAY_USE_DATABASE("useDatabase"),
        DISPLAY_DATABASE_TYPE("databaseType");

        final String key;

        LocaleSetting(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return fileConfiguration.getString(key);
        }

        public String getValue(String defaultValue) {
            return fileConfiguration.getString(key, defaultValue);
        }

        public static LocaleSetting getByKey(String key) {
            for (LocaleSetting setting : LocaleSetting.values()) {
                if (setting.getKey().equalsIgnoreCase(key)) {
                    return setting;
                }
            }
            return null;
        }
    }
}
