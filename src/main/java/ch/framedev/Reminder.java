package ch.framedev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Reminder {

    private String title;
    private String message;
    private String date;
    private String time;
    private List<String> notes;
    private boolean show;

    public Reminder(String title, String message) {
        this(title, message, "Not Set", "Not Set", new ArrayList<>()); // Default values
    }

    public Reminder(String title, String message, String date, String time, List<String> notes) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.time = time;
        this.notes = (notes != null) ? notes : new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = (notes != null) ? notes : new ArrayList<>();
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    /**
     * Sends a system notification (Placeholder method).
     */
    public void sendNotification() {
        System.out.println("🔔 Reminder: " + title + " - " + message);
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", notes=" + notes +
                '}';
    }

    public String reminderToString() {
        return "Title: " + title + "\n" +
                "Message: " + message + "\n" +
                "Date: " + date + " " + time + "\n" +
                "Notes: " + String.join(", ", notes) + "\n" +
                "shown: " + show;
    }

    public static Reminder reminderFromString(String reminderString) {
        String[] parts = reminderString.split("\n");

        String title = parts[0].substring("Title: ".length()).trim();
        String message = parts[1].substring("Message: ".length()).trim();
        String dateTime = parts[2].substring("Date: ".length()).trim();
        String[] dateTimeParts = dateTime.split(" ");
        String date = dateTimeParts.length > 0 ? dateTimeParts[0] : "";
        String time = dateTimeParts.length > 1 ? dateTimeParts[1] : "";

        List<String> notes = new ArrayList<>();
        if (parts.length > 3) {
            String notesText = parts[3].substring("Notes: ".length()).trim();
            if (!notesText.isEmpty()) {
                notes = Arrays.asList(notesText.split(", "));
            }
        }
        boolean show = Boolean.parseBoolean(parts[parts.length - 1].substring("shown: ".length()).trim());
        Reminder reminder = new Reminder(title, message, date, time, new ArrayList<>(notes));
        reminder.setShow(show);
        return reminder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(title, reminder.title) &&
                Objects.equals(message, reminder.message) &&
                Objects.equals(date, reminder.date) &&
                Objects.equals(time, reminder.time) &&
                Objects.equals(notes, reminder.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, message, date, time, notes);
    }
}