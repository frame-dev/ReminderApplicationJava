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

@SuppressWarnings("unused")
public record LocaleManager(Locale locale) {

    private static volatile FileConfiguration fileConfiguration;

    public LocaleManager(Locale locale) {
        this.locale = locale;
        loadForLocale(locale);
    }

    public static synchronized void loadForLocale(Locale locale) {
        if (locale == null) return;
        try {
            File localesDir = new File(Main.utils.getFilePath(Main.class), "locales");
            File localeFile = new File(localesDir, "locale_" + locale.getCode() + ".yml");
            fileConfiguration = new FileConfiguration(localeFile);
            fileConfiguration.load();
        } catch (Exception ex) {
            System.err.println("Failed to load locale file: " + ex.getMessage());
            fileConfiguration = null;
        }
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        if (fileConfiguration == null) return defaultValue;
        return fileConfiguration.getString(key, defaultValue);
    }

    public enum LocaleSetting {

        DISPLAY_MENU("displayMenu"),
        DISPLAY_SETTINGS("displaySettings"),
        DISPLAY_CALENDAR("displayCalendar"),
        DISPLAY_EXIT("exit"),
        DISPLAY_USE_DATABASE("useDatabase"),
        DISPLAY_DATABASE_TYPE("databaseType"),
        CALENDAR_MAIN_TITLE("calendar|main"),
        CALENDAR_ENTRY_GUI_FRAME_TITLE_ADD("calendar|entryGui|frameTitle"),
        CALENDAR_ENTRY_GUI_FRAME_TITLE_EDIT("calendar|entryGui|frameTitleEdit"),
        CALENDAR_ENTRY_GUI_DATE("calendar|entryGui|date"),
        CALENDAR_ENTRY_GUI_TITLE("calendar|entryGui|title"),
        CALENDAR_ENTRY_GUI_DESCRIPTION("calendar|entryGui|description"),
        CALENDAR_ENTRY_GUI_TIME("calendar|entryGui|time"),
        CALENDAR_ENTRY_GUI_FROM_DATE("calendar|entryGui|fromDate"),
        CALENDAR_ENTRY_GUI_TO_DATE("calendar|entryGui|toDate"),
        CALENDAR_ENTRY_GUI_FROM_TIME("calendar|entryGui|fromTime"),
        CALENDAR_ENTRY_GUI_TO_TIME("calendar|entryGui|toTime"),
        CALENDAR_ENTRY_GUI_SAVE_BUTTON("calendar|entryGui|saveButton"),
        CALENDAR_ENTRY_GUI_CANCEL_BUTTON("calendar|entryGui|cancelButton"),
        CALENDAR_ENTRY_GUI_EMPTY("calendar|entryGui|empty"),
        CALENDAR_ENTRY_OPTION_GUI_TITLE("calendar|entryOptionGui|title"),
        CALENDAR_ENTRY_OPTION_GUI_ADD_BUTTON("calendar|entryOptionGui|addButton"),
        CALENDAR_ENTRY_OPTION_GUI_EDIT_BUTTON("calendar|entryOptionGui|editButton"),
        CALENDAR_ENTRY_OPTION_GUI_DELETE_BUTTON("calendar|entryOptionGui|deleteButton"),
        CALENDAR_ENTRY_OPTION_GUI_BACK_BUTTON("calendar|entryOptionGui|backButton");

        final String key;

        LocaleSetting(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return getValue(null);
        }

        public String getValue(String defaultValue) {
            String actualKey = key;
            if (actualKey != null && actualKey.contains("|")) {
                actualKey = actualKey.replace("|", ".");
            }
            return LocaleManager.getString(actualKey, defaultValue);
        }

        public static LocaleSetting getByKey(String key) {
            if (key == null) return null;
            for (LocaleSetting setting : LocaleSetting.values()) {
                if (setting.getKey().equalsIgnoreCase(key)) {
                    return setting;
                }
            }
            return null;
        }
    }
}