package ch.framedev.classes;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a reminder with a title, message, date, time, notes, and visibility status.
 */
public class Reminder {

    private String title;
    private String message;
    private String date;
    private String time;
    private List<String> notes;
    private boolean show;

    protected Reminder() {}

    /**
     * Constructs a new Reminder object with the given title and message.
     * The date, time, and notes are set to default values.
     *
     * @param title   the title of the reminder
     * @param message the message of the reminder
     */
    public Reminder(String title, String message) {
        this(title, message, "Not Set", "Not Set", new ArrayList<>()); // Default values
    }

    /**
     * Constructs a new Reminder object with the given title, message, date, time, and notes.
     *
     * @param title   the title of the reminder
     * @param message the message of the reminder
     * @param date    the date of the reminder
     * @param time    the time of the reminder
     * @param notes   the notes of the reminder
     */
    public Reminder(String title, String message, String date, String time, List<String> notes) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.time = time;
        this.notes = (notes != null) ? notes : new ArrayList<>();
    }

    public Reminder(Document document) {
        this.title = document.getString("title");
        this.message = document.getString("message");
        this.date = document.getString("date");
        this.time = document.getString("time");
        this.notes = document.getList("notes", String.class, new ArrayList<>());
        this.show = document.getBoolean("show", false);
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

    /**
     * Returns a string representation of this Reminder object.
     * The string representation consists of the Reminder's title, message, date, time, and notes,
     * enclosed in curly braces and separated by commas.
     *
     * @return a string representation of this Reminder object
     */
    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", notes=" + notes +
                ", show=" + show +
                '}';
    }

    /**
     * Converts the Reminder object into a formatted string representation.
     *
     * @return a string representation of the Reminder object with the following format:
     * "Title: [title]\nMessage: [message]\nDate: [date] [time]\nNotes: [notes]\nshown: [show]"
     */
    public String reminderToString() {
        return "Title: " + title + "\n" +
                "Message: " + message + "\n" +
                "Date: " + date + " " + time + "\n" +
                "Notes: " + String.join(", ", notes) + "\n" +
                "show: " + show;
    }

    /**
     * Parses a reminder string and returns a Reminder object.
     *
     * @param reminderString the string representation of the reminder
     * @return a Reminder object parsed from the given string
     */
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
                notes = Arrays.stream(notesText.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList(); // Java 16+, otherwise use .collect(Collectors.toList())
            }
        }
        boolean show = Boolean.parseBoolean(parts[parts.length - 1].substring("show: ".length()).trim());
        Reminder reminder = new Reminder(title, message, date, time, new ArrayList<>(notes));
        reminder.setShow(show);
        return reminder;
    }

    /**
     * Compares the specified object with this Reminder for equality.
     * Returns {@code true} if the given object is also a Reminder and all its fields (title, message, date, time, notes, and show)
     * are equal to the corresponding fields of this Reminder; otherwise, returns {@code false}.
     *
     * @param o the object to be compared for equality with this Reminder
     * @return {@code true} if the specified object is equal to this Reminder; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(title, reminder.title) &&
                Objects.equals(message, reminder.message) &&
                Objects.equals(date, reminder.date) &&
                Objects.equals(time, reminder.time) &&
                Objects.equals(notes, reminder.notes) &&
                Objects.equals(hashCode(), reminder.hashCode()) &&
                show == reminder.show;
    }

    public Document toDocument() {
        return new Document("title", title)
                .append("message", message)
                .append("date", date)
                .append("time", time)
                .append("notes", notes)
                .append("show", show);
    }

    /**
     * Generates a hash code for the Reminder object.
     * The hash code is based on the values of the title, message, date, time, notes, and show fields.
     *
     * @return the hash code of the Reminder object
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, message, date, time, notes, show);
    }
}