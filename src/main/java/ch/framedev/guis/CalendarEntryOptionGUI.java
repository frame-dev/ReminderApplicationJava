package ch.framedev.guis;

import ch.framedev.classes.CalendarEntry;
import ch.framedev.main.Main;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CalendarEntryOptionGUI extends JFrame {

    public CalendarEntryOptionGUI(LocalDate date) {
        setTitle("Calendar Entry Options");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI(date);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI(LocalDate date) {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(main);

        // Sample model - replace with real data source later
        DefaultListModel<String> model = new DefaultListModel<>();
        fillList(model, date);

        JList<String> eventList = new JList<>(model);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(eventList);
        scroll.setPreferredSize(new Dimension(260, 120));
        main.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton closeBtn = new JButton("Close");

        editBtn.setEnabled(false);

        addBtn.addActionListener(e -> {
            new CalendarEntryGUI(date).setVisible(true);
            dispose();
        });

        editBtn.addActionListener(e -> {
            // For now open CalendarEntryGUI for the same date.
            // Replace with loading selected entry into the editor when available.
            if(eventList.isSelectionEmpty()) return;
            if(model.isEmpty()) return;
            if(eventList.getSelectedValue() == null) return;
            CalendarEntry entry = getSelectedEntry(date, eventList.getSelectedValue());
            if(entry == null) return;
            new CalendarEntryGUI(entry).setVisible(true);
            dispose();
        });

        closeBtn.addActionListener(e -> dispose());

        eventList.addListSelectionListener((ListSelectionEvent e) -> {
            editBtn.setEnabled(!eventList.isSelectionEmpty());
        });

        deleteBtn.addActionListener(e -> {
            if(eventList.isSelectionEmpty()) return;
            if(model.isEmpty()) return;
            if(eventList.getSelectedValue() == null) return;
            CalendarEntry entry = getSelectedEntry(date, eventList.getSelectedValue());
            if(entry == null) return;
            Main.getCalendarManager().deleteCalendarEntry(entry);
            model.removeElement(eventList.getSelectedValue());
            JOptionPane.showMessageDialog(null, "Calendar entry deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(closeBtn);
        main.add(btnPanel, BorderLayout.SOUTH);
    }

    private CalendarEntry getSelectedEntry(LocalDate date, String selectedValue) {
        List<CalendarEntry> entries = Main.getCalendarManager().getCalendarEntriesByDate(date.toString());
        for (CalendarEntry entry : entries) {
            String displayValue = selectedValue.split(" - ")[0];
            if (displayValue.equals(entry.getTitle())) {
                return entry;
            }
        }
        return null;
    }

    private void fillList(DefaultListModel<String> model, LocalDate date) {
        model.clear();
        List<CalendarEntry> entries = Main.getCalendarManager().getCalendarEntriesByDate(date.toString());
        for (CalendarEntry entry : entries) {
            model.addElement(entry.getTitle() + " - " + entry.getTime());
        }
    }
}