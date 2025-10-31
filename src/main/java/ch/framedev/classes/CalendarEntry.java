package ch.framedev.classes;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class CalendarEntry {

    private String calendarId = UUID.randomUUID().toString();
    private String title;
    private String date, fromDate, toDate;
    private String time, fromTime, toTime;
    private String description;
    private List<Day> days;

    protected CalendarEntry() {}

    public CalendarEntry(String title, String date, String fromDate, String toDate,
                         String time, String fromTime, String toTime,
                         String description) {
        this.title = title;
        this.date = date;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.time = time;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.description = description;
    }

    public CalendarEntry(String title, String description, String date, String time) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public CalendarEntry(String title, String description, String date, String fromDate, String toDate) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getCalendarId() {
        return calendarId;
    }
}
