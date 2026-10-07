package org.example.ui.views.student;
import org.example.ui.LoginFrame;
import org.example.model.Student;
import org.example.model.Task;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.geom.Path2D;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class StudentDashboardFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | Student Dashboard");
    private final Student student;
    private final DefaultListModel<Task> taskModel = new DefaultListModel<>();

    public StudentDashboardFrame(Student student) {
        this.student = student;
        for (Task task : student.getTasks()) {
            taskModel.addElement(task);
        }
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1060, 680));
        window.setSize(1360, 820);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(createSidebar(), BorderLayout.WEST);
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(PAGE);
        main.add(new HeroPanel(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(createDashboardBody());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(PAGE);
        main.add(scrollPane, BorderLayout.CENTER);
        content.add(main, BorderLayout.CENTER);
        return content;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(205, 0));
        sidebar.setBackground(DARK_GREEN);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(14, 20, 18, 20));
        LogoView logo = new LogoView();
        logo.setAlignmentX(0.5f);
        logo.setPreferredSize(new Dimension(145, 105));
        logo.setMaximumSize(new Dimension(145, 105));
        top.add(logo);
        top.add(Box.createVerticalStrut(4));
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(0.5f);
        JLabel rey = new JLabel("REY");
        rey.setFont(new Font("SansSerif", Font.BOLD, 21));
        rey.setForeground(Color.WHITE);
        JLabel sis = new JLabel(" SIS");
        sis.setFont(new Font("SansSerif", Font.BOLD, 21));
        sis.setForeground(GOLD);
        brand.add(rey);
        brand.add(sis);
        top.add(brand);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        navigation.add(createNavigationButton("Dashboard", IconType.DASHBOARD, true));
        navigation.add(createNavigationButton("My Profile", IconType.PROFILE, false));
        navigation.add(createNavigationButton("Enrollment", IconType.ENROLLMENT, false));
        navigation.add(createNavigationButton("My Schedule", IconType.CALENDAR, false));
        navigation.add(createNavigationButton("Grades", IconType.GRADES, false));
        sidebar.add(navigation, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        JButton signOut = createNavigationButton("Sign Out", IconType.SIGN_OUT, false);
        signOut.addActionListener(event -> signOut());
        bottom.add(signOut, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private void openEnrollment() {
        window.dispose();
        new StudentEnrollmentFrame(student).showWindow();
    }

    private void openSchedule() {
        window.dispose();
        new StudentScheduleFrame(student).showWindow();
    }

    private JButton createNavigationButton(String text, IconType iconType, boolean active) {
        JButton button = new JButton(text, new DashboardIcon(iconType, active ? GOLD : new Color(202, 219, 211)));
        button.setAlignmentX(0.5f);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setPreferredSize(new Dimension(205, 46));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setMargin(new Insets(0, 25, 0, 12));
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setForeground(active ? GOLD : Color.WHITE);
        button.setBackground(active ? new Color(26, 86, 66) : DARK_GREEN);
        button.setBorder(active ? BorderFactory.createMatteBorder(0, 4, 0, 0, GOLD) : BorderFactory.createEmptyBorder(0, 4, 0, 0));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (text.equals("Sign Out")) {
            return button;
        }
        button.addActionListener(event -> {
            if (text.equals("My Profile")) {
                openProfile();
            } else if (text.equals("Enrollment")) {
                openEnrollment(); // Replace showComingSoon("Enrollment")
            } else if (text.equals("Grades")) {
                window.dispose();
                new StudentGradesFrame(student).showWindow();
            } else if (text.equals("My Schedule")) {
                openSchedule();
            } else if (active) {
                showDashboardMessage();
            } else {
                showComingSoon(text);
            }
        });
        return button;
    }




    private JPanel createDashboardBody() {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 20, 20, 20));
        body.setPreferredSize(new Dimension(1120, 610));
        body.add(createMainGrid(), BorderLayout.CENTER);
        return body;
    }

    private JPanel createMainGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.67;
        constraints.insets = new Insets(0, 0, 12, 14);
        grid.add(createStatsAndSchedule(), constraints);
        constraints.gridx = 1;
        constraints.weightx = 0.33;
        constraints.insets = new Insets(0, 0, 12, 0);
        grid.add(createRightColumn(), constraints);
        return grid;
    }

    private JPanel createStatsAndSchedule() {
        JPanel left = new JPanel(new BorderLayout(0, 14));
        left.setOpaque(false);
        left.add(createStatsPanel(), BorderLayout.NORTH);
        left.add(createTasksCard(), BorderLayout.CENTER);
        return left;
    }

    private JPanel createStatsPanel() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setOpaque(false);
        section.add(sectionTitle("Quick Stats", IconType.STATS, null), BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);
        cards.add(createStatCard("Enrolled Subjects", String.valueOf(student.getEnrolledSubjects()), "", IconType.ENROLLMENT, DEEP_GREEN));
        cards.add(createStatCard("Current GPA", String.format("%.2f", student.getCurrentGpa()), "", IconType.GPA, GOLD));
        cards.add(createStatCard("Units Enrolled", student.getEnrolledUnits() + " / " + student.getMaximumUnits(), "", IconType.GRADES, DEEP_GREEN));
        cards.add(createStatCard("Dean's List Standing", student.getDeansListStanding(), "", IconType.STAR, GOLD));
        section.add(cards, BorderLayout.CENTER);
        return section;
    }

    private JPanel createStatCard(String title, String value, String suffix, IconType iconType, Color iconColor) {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        DashboardIconLabel icon = new DashboardIconLabel(iconType, iconColor);
        icon.setAlignmentX(0.5f);
        card.add(icon);
        card.add(Box.createVerticalStrut(7));
        JLabel valueLabel = new JLabel(value + suffix);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, title.equals("Dean's List Standing") ? 19 : 25));
        valueLabel.setForeground(TEXT);
        valueLabel.setAlignmentX(0.5f);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        JLabel titleLabel = smallLabel(title, 9, MUTED);
        titleLabel.setAlignmentX(0.5f);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel);
        return card;
    }

    private JPanel createRightColumn() {
        JPanel right = new JPanel(new BorderLayout(0, 14));
        right.setOpaque(false);
        right.add(createInformationCard(), BorderLayout.NORTH);
        right.add(createEnrollmentCard(), BorderLayout.CENTER);
        return right;
    }

    private JPanel createInformationCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(sectionTitle("My Information", IconType.PROFILE, null), BorderLayout.WEST);
        header.add(actionButton("Edit Profile", this::openProfile), BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);
        JPanel details = new JPanel(new GridLayout(6, 1, 0, 2));
        details.setOpaque(false);
        addInfoRow(details, "Student ID", student.getStudentId());
        addInfoRow(details, "Name", student.getName());
        addInfoRow(details, "Program", student.getProgram());
        addInfoRow(details, "Year Level", student.getYearLevel());
        addInfoRow(details, "Email", student.getEmail());
        addInfoRow(details, "Contact No.", student.getContactNumber());
        card.add(details, BorderLayout.CENTER);
        return card;
    }

    private void addInfoRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(242, 242, 239)),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        row.add(smallLabel(label, 11, MUTED));
        row.add(smallLabel(value, 11, TEXT));
        parent.add(row);
    }

    private JPanel createEnrollmentCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(sectionTitle("Enrollment Status", IconType.ENROLLMENT, null), BorderLayout.WEST);
        header.add(smallLabel("1st Semester, AY 2025–2026", 8, MUTED), BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);
        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        content.setOpaque(false);
        content.add(new DonutPanel(student.getEnrolledSubjects(), 6));
        JPanel legend = new JPanel(new GridLayout(3, 1, 0, 3));
        legend.setOpaque(false);
        legend.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(2, 14, 2, 0)));
        addLegendRow(legend, "Enrolled", String.valueOf(student.getEnrolledSubjects()), DEEP_GREEN);
        addLegendRow(legend, "Remaining", String.valueOf(6 - student.getEnrolledSubjects()), GOLD);
        addLegendRow(legend, "Total Units", String.valueOf(student.getMaximumUnits()), new Color(126, 128, 126));
        content.add(legend);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void addLegendRow(JPanel parent, String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        JLabel marker = new JLabel(" ");
        marker.setOpaque(true);
        marker.setBackground(color);
        marker.setPreferredSize(new Dimension(10, 10));
        row.add(marker, BorderLayout.WEST);
        row.add(smallLabel(label, 9, TEXT), BorderLayout.CENTER);
        row.add(smallLabel(value, 9, TEXT), BorderLayout.EAST);
        parent.add(row);
    }

    private JPanel createTasksCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, GOLD),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(sectionTitle("My Tasks", IconType.TASK, null), BorderLayout.WEST);
        JButton addTaskButton = actionButton("+ Add Task", null);
        addTaskButton.addActionListener(event -> addTask());
        header.add(addTaskButton, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);
        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        refreshTaskRows(rows);
        taskModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent event) {
                refreshTaskRows(rows);
            }

            @Override
            public void intervalRemoved(ListDataEvent event) {
                refreshTaskRows(rows);
            }

            @Override
            public void contentsChanged(ListDataEvent event) {
                refreshTaskRows(rows);
            }
        });
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private void addTask() {
        String title = JOptionPane.showInputDialog(window, "Enter task title:", "Add Task", JOptionPane.PLAIN_MESSAGE);
        if (title != null && !title.trim().isEmpty()) {
            taskModel.addElement(new Task(title.trim(), ""));
        }
    }

    private void refreshTaskRows(JPanel rows) {
        rows.removeAll();
        for (int index = 0; index < taskModel.size(); index++) {
            Task task = taskModel.getElementAt(index);
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

            JCheckBox checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            checkBox.setFocusPainted(false);
            JLabel titleLabel = smallLabel(task.getTitle(), 9, TEXT);
            checkBox.addActionListener(event -> taskModel.removeElement(task));
            row.add(checkBox, BorderLayout.WEST);
            row.add(titleLabel, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            actions.setOpaque(false);
            if (!task.getDueDate().isBlank()) {
                JLabel due = new JLabel(task.getDueDate(), SwingConstants.CENTER);
                due.setFont(new Font("SansSerif", Font.PLAIN, 8));
                due.setForeground(new Color(224, 88, 73));
                due.setOpaque(true);
                due.setBackground(new Color(255, 226, 220));
                due.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
                actions.add(due);
            }
            JButton removeButton = actionButton("Remove", null);
            removeButton.addActionListener(event -> {
                taskModel.removeElement(task);
            });
            actions.add(removeButton);
            row.add(actions, BorderLayout.EAST);
            rows.add(row);
        }
        rows.revalidate();
        rows.repaint();
    }

    private JPanel sectionTitle(String title, IconType iconType, Color iconColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.add(new DashboardIconLabel(iconType, iconColor == null ? DEEP_GREEN : iconColor));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Serif", Font.BOLD, 16));
        label.setForeground(TEXT);
        panel.add(label);
        return panel;
    }

    private JLabel linkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 9));
        label.setForeground(DEEP_GREEN);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    private JButton actionButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button.setForeground(DEEP_GREEN);
        button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DEEP_GREEN));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(1, 2, 1, 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (action != null) {
            button.addActionListener(event -> action.run());
        }
        return button;
    }

    private JLabel smallLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }

    private JLabel multiLineLabel(String text, int size, Color color, boolean bold) {
        JLabel label = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>");
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private void openProfile() {
        window.dispose();
        new StudentProfileFrame(student).showWindow();
    }

    private void showComingSoon(String module) {
        javax.swing.JOptionPane.showMessageDialog(window, module + " is coming next.", "REY SIS", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDashboardMessage() {
        javax.swing.JOptionPane.showMessageDialog(window, "Dashboard is already open.", "REY SIS", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = StudentDashboardFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static BufferedImage loadFirstImage(String... resourcePaths) {
        for (String resourcePath : resourcePaths) {
            BufferedImage image = loadImage(resourcePath);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    private static class HeroPanel extends JPanel {
        private final BufferedImage image = loadImage("/images/university-building.jpg");

        HeroPanel() {
            setPreferredSize(new Dimension(0, 170));
            setOpaque(false);
            setLayout(new BorderLayout());
            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.setBorder(BorderFactory.createEmptyBorder(30, 36, 18, 20));
            JLabel greeting = new JLabel("Good day,");
            greeting.setFont(new Font("SansSerif", Font.BOLD, 20));
            greeting.setForeground(DEEP_GREEN);
            JLabel name = new JLabel("Justine Rivera!");
            name.setFont(new Font("SansSerif", Font.BOLD, 30));
            name.setForeground(DEEP_GREEN);
            JLabel line = new JLabel("Stay consistent.");
            line.setFont(new Font("SansSerif", Font.PLAIN, 11));
            line.setForeground(new Color(72, 79, 75));
            JLabel lineTwo = new JLabel("Your future is built one step at a time.");
            lineTwo.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lineTwo.setForeground(new Color(72, 79, 75));
            text.add(greeting);
            text.add(name);
            text.add(Box.createVerticalStrut(16));
            text.add(line);
            text.add(lineTwo);
            add(text, BorderLayout.WEST);
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (image != null) {
                double scale = Math.max((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
                int width = (int) Math.ceil(image.getWidth() * scale);
                int height = (int) Math.ceil(image.getHeight() * scale);
                g.drawImage(image, (getWidth() - width) / 2, (getHeight() - height) / 2, width, height, null);
            } else {
                g.setColor(new Color(222, 235, 228));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 235), getWidth(), 0, new Color(255, 255, 255, 115)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 50), 0, getHeight(), new Color(255, 255, 255, 205)));
            g.fillRect(0, 0, getWidth(), getHeight());
            Path2D wave = new Path2D.Double();
            wave.moveTo(getWidth() - 30, 0);
            wave.lineTo(getWidth(), 0);
            wave.lineTo(getWidth(), getHeight());
            wave.lineTo(getWidth() - 112, getHeight());
            wave.curveTo(getWidth() - 112, getHeight() - 28, getWidth() - 103, getHeight() - 48, getWidth() - 84, getHeight() - 68);
            wave.curveTo(getWidth() - 59, getHeight() - 94, getWidth() - 39, getHeight() - 120, getWidth() - 30, 0);
            wave.closePath();
            g.setColor(new Color(0, 59, 44, 245));
            g.fill(wave);
            g.setColor(GOLD);
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D accent = new Path2D.Double();
            accent.moveTo(getWidth() - 2, 47);
            accent.curveTo(getWidth() - 18, 85, getWidth() - 39, 96, getWidth() - 56, 119);
            accent.curveTo(getWidth() - 73, 140, getWidth() - 82, 155, getWidth() - 84, getHeight());
            g.draw(accent);
            g.dispose();
        }
    }

    private static class LogoView extends JPanel {
        private final BufferedImage image = loadFirstImage("/images/images/Frame 6 (1).png", "/images/rey-sis-logo.png");

        LogoView() { setOpaque(false); }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (image == null) return;
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double scale = Math.min((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
            int width = (int) (image.getWidth() * scale);
            int height = (int) (image.getHeight() * scale);
            g.drawImage(image, (getWidth() - width) / 2, (getHeight() - height) / 2, width, height, null);
            g.dispose();
        }
    }

    private static class CardPanel extends JPanel {
        private final Color background;

        CardPanel(Color background) {
            this.background = background;
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(background);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.setColor(BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class BadgeLabel extends JLabel {
        BadgeLabel(String text) {
            super(text, SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 9));
            setForeground(Color.WHITE);
            setOpaque(true);
            setBackground(new Color(218, 145, 33));
            setPreferredSize(new Dimension(18, 18));
        }
    }

    private static class DashboardIconLabel extends JLabel {
        DashboardIconLabel(IconType type, Color color) {
            super(new DashboardIcon(type, color));
        }
    }

    private static class DonutPanel extends JPanel {
        private final int completed;
        private final int total;

        DonutPanel(int completed, int total) {
            this.completed = completed;
            this.total = total;
            setOpaque(false);
            setPreferredSize(new Dimension(116, 112));
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 16;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(231, 235, 229));
            g.drawOval(x, y, size, size);
            g.setColor(DEEP_GREEN);
            g.drawArc(x, y, size, size, 90, -360 * completed / total);
            String value = completed + "/" + total;
            g.setFont(new Font("SansSerif", Font.BOLD, 19));
            g.setColor(TEXT);
            int textWidth = g.getFontMetrics().stringWidth(value);
            g.drawString(value, (getWidth() - textWidth) / 2, getHeight() / 2 + 4);
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String label = "Subjects";
            int labelWidth = g.getFontMetrics().stringWidth(label);
            g.setColor(MUTED);
            g.drawString(label, (getWidth() - labelWidth) / 2, getHeight() / 2 + 19);
            g.dispose();
        }
    }

    enum IconType {
        DASHBOARD, PROFILE, ENROLLMENT, CALENDAR, GRADES, RECORDS, REQUESTS, BELL,
        SIGN_OUT, STATS, GPA, STAR, ANNOUNCEMENT, TASK
    }

    static class DashboardIcon implements javax.swing.Icon {
        private final IconType type;
        private final Color color;

        DashboardIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }

        public int getIconWidth() { return 19; }
        public int getIconHeight() { return 19; }

        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (type) {
                case DASHBOARD -> {
                    g.fillRect(x + 2, y + 8, 5, 8); g.fillRect(x + 10, y + 4, 5, 12);
                    g.fillRect(x + 2, y + 4, 5, 2); g.fillRect(x + 10, y + 1, 5, 2);
                }
                case PROFILE -> { g.drawOval(x + 6, y + 2, 7, 7); g.drawArc(x + 3, y + 10, 13, 9, 0, 180); }
                case ENROLLMENT -> { g.drawRect(x + 3, y + 4, 13, 13); g.drawLine(x + 3, y + 8, x + 16, y + 8); g.drawLine(x + 6, y + 2, x + 6, y + 6); g.drawLine(x + 13, y + 2, x + 13, y + 6); }
                case CALENDAR -> { g.drawRect(x + 2, y + 4, 15, 13); g.drawLine(x + 2, y + 8, x + 17, y + 8); g.drawLine(x + 6, y + 2, x + 6, y + 6); g.drawLine(x + 13, y + 2, x + 13, y + 6); }
                case GRADES, STATS -> { g.fillRect(x + 2, y + 11, 4, 6); g.fillRect(x + 8, y + 7, 4, 10); g.fillRect(x + 14, y + 3, 4, 14); }
                case RECORDS -> { g.drawRoundRect(x + 3, y + 2, 13, 16, 2, 2); g.drawLine(x + 6, y + 7, x + 13, y + 7); g.drawLine(x + 6, y + 11, x + 13, y + 11); g.drawLine(x + 6, y + 15, x + 10, y + 15); }
                case REQUESTS -> { g.drawRect(x + 3, y + 3, 12, 14); g.drawLine(x + 6, y + 7, x + 12, y + 7); g.drawLine(x + 6, y + 11, x + 12, y + 11); }
                case BELL -> { g.drawArc(x + 4, y + 2, 11, 13, 0, 180); g.drawLine(x + 4, y + 9, x + 4, y + 15); g.drawLine(x + 15, y + 9, x + 15, y + 15); g.drawLine(x + 2, y + 15, x + 17, y + 15); g.drawOval(x + 8, y + 16, 3, 3); }
                case SIGN_OUT -> { g.drawLine(x + 3, y + 3, x + 3, y + 16); g.drawLine(x + 3, y + 3, x + 10, y + 3); g.drawLine(x + 3, y + 16, x + 10, y + 16); g.drawLine(x + 8, y + 9, x + 17, y + 9); g.drawLine(x + 13, y + 5, x + 17, y + 9); g.drawLine(x + 13, y + 13, x + 17, y + 9); }
                case GPA -> { g.drawRect(x + 3, y + 3, 13, 15); g.drawLine(x + 6, y + 7, x + 13, y + 7); g.drawLine(x + 6, y + 11, x + 10, y + 11); g.drawOval(x + 11, y + 12, 4, 4); }
                case STAR -> { int[] xs = {x + 10, x + 12, x + 17, x + 13, x + 14, x + 10, x + 6, x + 7, x + 3, x + 8}; int[] ys = {y + 2, y + 7, y + 7, y + 10, y + 16, y + 13, y + 16, y + 10, y + 7, y + 7}; g.fillPolygon(xs, ys, 10); }
                case ANNOUNCEMENT -> { g.drawRect(x + 4, y + 5, 12, 10); g.drawLine(x + 4, y + 7, x + 1, y + 5); g.drawLine(x + 1, y + 5, x + 1, y + 14); g.drawLine(x + 1, y + 14, x + 4, y + 13); }
                case TASK -> { g.drawRoundRect(x + 4, y + 3, 12, 14, 2, 2); g.drawLine(x + 7, y + 2, x + 13, y + 2); g.drawLine(x + 7, y + 8, x + 13, y + 8); g.drawLine(x + 7, y + 12, x + 13, y + 12); }
            }
            g.dispose();
        }
    }
}
