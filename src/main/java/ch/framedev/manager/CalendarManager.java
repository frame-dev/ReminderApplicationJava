package ch.framedev.manager;

import ch.framedev.classes.CalendarEntry;
import ch.framedev.javajsonutils.JsonUtils;
import ch.framedev.main.Main;
import ch.framedev.simplejavautils.SimpleJavaUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CalendarManager {

    private final Logger logger = LogManager.getLogger(CalendarManager.class);

    private final List<CalendarEntry> calendarEntries;
    private final List<CalendarEntry> deletedCalendarEntries;
    private final SimpleJavaUtils utils = new SimpleJavaUtils();

    public CalendarManager() {
        // Ensure lists are always initialized (final fields must be assigned)
        if (!Main.isDatabaseSupported()) {
            Type type = new TypeToken<List<CalendarEntry>>() {
            }.getType();
            Type deletedType = new TypeToken<List<CalendarEntry>>() {
            }.getType();

            List<CalendarEntry> loadedEntries = new JsonUtils().getTypeFromJsonFile(
                    new File(utils.getFilePath(Main.class), "calendar_entries.json"), type
            );
            this.calendarEntries = (loadedEntries == null) ? new ArrayList<>() : loadedEntries;

            List<CalendarEntry> loadedDeletedEntries = new JsonUtils().getTypeFromJsonFile(
                    new File(utils.getFilePath(Main.class), "deleted_calendar_entries.json"), deletedType
            );
            this.deletedCalendarEntries = (loadedDeletedEntries == null) ? new ArrayList<>() : loadedDeletedEntries;
        } else {
            Type deletedType = new TypeToken<List<CalendarEntry>>() {
            }.getType();

            List<CalendarEntry> loadedDeletedEntries = new JsonUtils().getTypeFromJsonFile(
                    new File(utils.getFilePath(Main.class), "deleted_calendar_entries.json"), deletedType
            );
            this.deletedCalendarEntries = (loadedDeletedEntries == null) ? new ArrayList<>() : loadedDeletedEntries;

            List<CalendarEntry> dbEntries = null;
            try {
                dbEntries = Main.getDatabaseManager().getIDatabaseCalendar().getAllCalendarEntries();
            } catch (Exception ex) {
                logger.warn("Failed to load calendar entries from database, falling back to empty list: {}", ex.getMessage());
            }
            this.calendarEntries = (dbEntries == null) ? new ArrayList<>() : dbEntries;
        }
    }

    private void saveCalendarEntries() {
        if (!Main.isDatabaseSupported()) {
            try {
                new JsonUtils().saveJsonToFile(
                        new File(utils.getFilePath(Main.class), "calendar_entries.json"), calendarEntries);
            } catch (IOException e) {
                logger.error("Failed to save calendar entries to JSON file: {}", e.getMessage(), e);
            }
            try {
                new JsonUtils().saveJsonToFile(
                        new File(utils.getFilePath(Main.class), "deleted_calendar_entries.json"), deletedCalendarEntries);
            } catch (IOException e) {
                logger.error("Failed to save deleted calendar entries to JSON file: {}", e.getMessage(), e);
            }
        }
    }

    public void addCalendarEntry(CalendarEntry entry) {
        if (entry == null) return;
        if (!calendarEntries.contains(entry)) {
            calendarEntries.add(entry);
        }
        if (Main.isDatabaseSupported()) {
            try {
                Main.getIDatabaseCalendar().insertCalendarEntry(entry);
            } catch (Exception ex) {
                logger.warn("Failed to insert calendar entry into database: {}", ex.getMessage());
            }
        }
        saveCalendarEntries();
    }

    public CalendarEntry getCalendarEntryById(String calendarId) {
        if (calendarId == null) return null;
        for (CalendarEntry entry : calendarEntries) {
            if (entry.getCalendarId().equals(calendarId)) {
                return entry;
            }
        }
        return null;
    }

    public void deleteCalendarEntry(CalendarEntry entry) {
        if (entry == null) return;
        calendarEntries.remove(entry);
        if (!deletedCalendarEntries.contains(entry)) {
            deletedCalendarEntries.add(entry);
        }
        if (Main.isDatabaseSupported()) {
            try {
                Main.getIDatabaseCalendar().deleteCalendarEntry(entry.getCalendarId());
            } catch (Exception ex) {
                logger.warn("Failed to delete calendar entry from database: {}", ex.getMessage());
            }
        }
        saveCalendarEntries();
    }

    public void updateCalendarEntry(CalendarEntry entry) {
        if (entry == null) return;
        if (Main.isDatabaseSupported() && getCalendarEntryById(entry.getCalendarId()) != null) {
            try {
                Main.getIDatabaseCalendar().updateCalendarEntry(entry);
            } catch (Exception ex) {
                logger.warn("Failed to update calendar entry in database: {}", ex.getMessage());
            }
        }
        calendarEntries.removeIf(e -> e.getCalendarId().equals(entry.getCalendarId()));
        calendarEntries.add(entry);
        saveCalendarEntries();
    }

    public List<CalendarEntry> getCalendarEntriesByDate(String date) {
        List<CalendarEntry> entriesByDate = new ArrayList<>();
        if (date == null) return entriesByDate;
        for (CalendarEntry entry : calendarEntries) {
            if (entry.getDate().equals(date)) {
                entriesByDate.add(entry);
            }
        }
        return entriesByDate;
    }

    public boolean calendarEntryExists(String calendarId) {
        if (calendarId == null) return false;
        for (CalendarEntry entry : calendarEntries) {
            if (entry.getCalendarId().equals(calendarId)) {
                return true;
            }
        }
        return false;
    }

    public List<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    public List<CalendarEntry> getDeletedCalendarEntries() {
        return deletedCalendarEntries;
    }
}