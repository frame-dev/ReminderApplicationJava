package ch.framedev.guis;

import ch.framedev.classes.CalendarEntry;
import ch.framedev.main.Main;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CalendarEntryGUI extends JFrame {

    private CalendarEntry calendarEntry;
    private final JTextField titleField = new JTextField(20);
    private final JTextArea descriptionArea = new JTextArea(5, 20);
    private final JTextField timeField = new JTextField(5);
    private final JTextField fromTimeField = new JTextField(5);
    private final JTextField toTimeField = new JTextField(5);
    private final JTextField fromDateField = new JTextField(10);
    private final JTextField toDateField = new JTextField(10);
    private final JLabel dateLabel = new JLabel();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // Backwards-compatible constructor
    public CalendarEntryGUI(LocalDate date) {
        this(date, false);
    }

    public CalendarEntryGUI(LocalDate date, boolean isEditMode) {
        setTitle(isEditMode ? "Edit Calendar Entry" : "Calendar Entry");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI(date, isEditMode);
        setVisible(true);
    }

    public CalendarEntryGUI(CalendarEntry calendarEntry) {
        this(LocalDate.parse(calendarEntry.getDate()), true);
        this.calendarEntry = calendarEntry;

        // Populate fields with existing data
        titleField.setText(calendarEntry.getTitle());
        descriptionArea.setText(calendarEntry.getDescription());
        timeField.setText(calendarEntry.getTime());

        // Assuming CalendarEntry has methods to get from/to dates and times
        fromDateField.setText(calendarEntry.getFromDate());
        toDateField.setText(calendarEntry.getToDate());
        fromTimeField.setText(calendarEntry.getFromTime());
        toTimeField.setText(calendarEntry.getToTime());
    }

    private void initUI(LocalDate date, boolean isEditMode) {
        dateLabel.setText(date.toString());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        form.add(dateLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        form.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        form.add(new JScrollPane(descriptionArea), gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        form.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;
        form.add(new JLabel("(Leave time empty for all-day event)"), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("From Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        form.add(fromTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("To Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        form.add(toTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("From Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        form.add(fromDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("To Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        form.add(toDateField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> onSave(date, isEditMode));
        cancelBtn.addActionListener(e -> dispose());

        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void onSave(LocalDate date, boolean isEditMode) {
        String title = titleField.getText().trim();
        String desc = descriptionArea.getText().trim();
        String timeText = timeField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title must not be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Determine main time (single-time event) or default all-day
        String mainTime = timeText.isEmpty() ? "00:00" : timeText;
        // Validate main time if provided
        try {
            LocalTime.parse(mainTime, TIME_FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:mm.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Determine from/to dates (default to the selected date)
        String fromDateText = fromDateField.getText().trim();
        String toDateText = toDateField.getText().trim();
        if (fromDateText.isEmpty()) fromDateText = date.format(DATE_FMT);
        if (toDateText.isEmpty()) toDateText = date.format(DATE_FMT);

        // Validate dates
        LocalDate fromDate;
        LocalDate toDate;
        try {
            fromDate = LocalDate.parse(fromDateText, DATE_FMT);
            toDate = LocalDate.parse(toDateText, DATE_FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Determine range times
        String fromTimeText = fromTimeField.getText().trim();
        String toTimeText = toTimeField.getText().trim();
        boolean hasRangeTimes = !fromTimeText.isEmpty() || !toTimeText.isEmpty();

        // If one of the range times is provided, require both and validate
        if (hasRangeTimes) {
            if (fromTimeText.isEmpty() || toTimeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide both From Time and To Time for a ranged time event.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                LocalTime.parse(fromTimeText, TIME_FMT);
                LocalTime.parse(toTimeText, TIME_FMT);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid range time format. Use HH:mm.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if(isEditMode) {
            // Update existing CalendarEntry
            calendarEntry.setTitle(title);
            calendarEntry.setDescription(desc);
            calendarEntry.setDate(date.format(DATE_FMT));
            calendarEntry.setTime(mainTime);
            calendarEntry.setFromDate(fromDate.format(DATE_FMT));
            calendarEntry.setToDate(toDate.format(DATE_FMT));
            calendarEntry.setFromTime(fromTimeText.isEmpty() ? "00:00" : fromTimeText);
            calendarEntry.setToTime(toTimeText.isEmpty() ? "00:00" : toTimeText);
        } else {
            // Construct CalendarEntry
            // Keep a simple default constructor call for single/mid events and use expanded constructor when range/date-range is provided.
            calendarEntry = new CalendarEntry(title, desc, date.format(DATE_FMT), mainTime);

            if (hasRangeTimes || !fromDate.equals(toDate)) {
                // Try to create an expanded entry if project provides such a constructor.
                // Parameter order used here: (title, startDate, endDate, createdDate, time, fromTime, toTime, description)
                // Adjust if `CalendarEntry` signature differs.
                calendarEntry = new CalendarEntry(
                        title,
                        fromDate.format(DATE_FMT),
                        toDate.format(DATE_FMT),
                        date.format(DATE_FMT),
                        mainTime,
                        fromTimeText.isEmpty() ? "00:00" : fromTimeText,
                        toTimeText.isEmpty() ? "00:00" : toTimeText,
                        desc
                );
            }
        }

        if (Main.getCalendarManager().calendarEntryExists(calendarEntry.getCalendarId()))
            Main.getCalendarManager().updateCalendarEntry(calendarEntry);
        else
            Main.getCalendarManager().addCalendarEntry(calendarEntry);

        dispose();
    }
}