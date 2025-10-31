package ch.framedev.guis;

import ch.framedev.manager.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarGUI extends JFrame {
    private YearMonth currentMonth;
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel calendarPanel = new JPanel();

    public CalendarGUI() {
        currentMonth = YearMonth.now();
        setTitle(LocaleManager.LocaleSetting.CALENDAR_MAIN_TITLE.getValue("Calendar"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        JButton prevBtn = new JButton("<");
        prevBtn.setFocusable(false);
        prevBtn.addActionListener((ActionEvent e) -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendar();
        });

        JButton nextBtn = new JButton(">");
        nextBtn.setFocusable(false);
        nextBtn.addActionListener((ActionEvent e) -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendar();
        });

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        nav.add(prevBtn);
        nav.add(monthLabel);
        nav.add(nextBtn);
        top.add(nav, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);

        calendarPanel.setLayout(new GridLayout(0, 7, 4, 4));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        getContentPane().add(calendarPanel, BorderLayout.CENTER);

        refreshCalendar();
    }

    private void refreshCalendar() {
        calendarPanel.removeAll();

        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        // Day of week headers
        for (DayOfWeek dow : DayOfWeek.values()) {
            JLabel lbl = new JLabel(dow.toString().substring(0, 3), SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            calendarPanel.add(lbl);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int firstColumn = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        int daysInMonth = currentMonth.lengthOfMonth();

        // Add empty labels for days before the first
        int leadingEmpty = (firstColumn - 1) % 7; // convert Monday=1..Sunday=7 to 0..6 with Monday first
        for (int i = 0; i < leadingEmpty; i++) {
            calendarPanel.add(new JLabel(""));
        }

        LocalDate today = LocalDate.now();

        // Add day buttons
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setMargin(new Insets(4, 4, 4, 4));
            dayBtn.setFocusable(false);

            if (date.equals(today)) {
                dayBtn.setBackground(new Color(0xD1F0D1));
                dayBtn.setOpaque(true);
            }

            // Example action: print selected date
            dayBtn.addActionListener(e -> {
                System.out.println("Selected date: " + date);
                CalendarEntryOptionGUI entryOptionGUI = new CalendarEntryOptionGUI(date);
                entryOptionGUI.setVisible(true);
            });
            calendarPanel.add(dayBtn);
        }

        // Fill remaining cells so the grid stays rectangular
        int totalCells = leadingEmpty + daysInMonth;
        int trailingEmpty = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < trailingEmpty; i++) {
            calendarPanel.add(new JLabel(""));
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarGUI::new);
    }
}