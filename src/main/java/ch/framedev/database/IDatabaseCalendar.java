package ch.framedev.database;

import ch.framedev.classes.CalendarEntry;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IDatabaseCalendar {

    boolean testConnection(Map<String, Object> parameters);

    boolean insertCalendarEntry(CalendarEntry calendarEntry);
    boolean updateCalendarEntry(CalendarEntry calendarEntry);
    boolean deleteCalendarEntry(String calendarId);
    CalendarEntry getCalendarEntryById(String calendarId);
    List<CalendarEntry> getAllCalendarEntries();
    List<CalendarEntry> getCalendarEntriesByDate(String date);
    boolean existsCalendarEntry(String calendarId);
}
