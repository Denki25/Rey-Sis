package org.example.ui.views.admin;

import org.example.data.AdminEnrollmentRepository;
import org.example.service.AdminEnrollmentService;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminValidationFrame {
    private static final Color SIDEBAR_BG = new Color(7, 43, 33);
    private static final Color SIDEBAR_ACTIVE = new Color(20, 60, 48);
    private static final Color GOLD = new Color(207, 160, 48);
    private static final Color PAGE_BG = new Color(250, 252, 253);
    private static final Color TEXT_DARK = new Color(22, 30, 45);
    private static final Color TEXT_MUTED = new Color(119, 131, 148);

    // Stat Colors
    private static final Color ORANGE_TEXT = new Color(217, 119, 6);
    private static final Color RED_TEXT = new Color(220, 38, 38);
    private static final Color GREEN_TEXT = new Color(24, 134, 75);

    // Badge & Card Colors
    private static final Color BADGE_RED_BG = new Color(254, 226, 226);
    private static final Color BADGE_YELLOW_BG = new Color(254, 243, 199);
    private static final Color BADGE_YELLOW_TEXT = new Color(180, 120, 0);
    private static final Color BADGE_GREEN_BG = new Color(223, 246, 235);

    private static final Color ACTIVE_CARD_BG = new Color(253, 249, 240);
    private static final Color ACTIVE_CARD_BORDER = new Color(245, 158, 11);
    private static final Color INACTIVE_CARD_BORDER = new Color(230, 235, 240);

    private final JFrame window = new JFrame("REY SIS | Enrollment Validation");
    private final AdminEnrollmentService enrollmentService = new AdminEnrollmentService();
    private final List<PendingStudent> pendingStudents = new ArrayList<>();
    private JPanel pendingListPanel;
    private JPanel inspectionContent;
    private JLabel pendingReviewsValue;
    private JLabel conflictValue;
    private JLabel clearedTodayValue;
    private PendingStudent selectedStudent;

    public AdminValidationFrame() {
        loadEnrollments();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1280, 800));
        window.setSize(1360, 900);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    private void loadEnrollments() {
        pendingStudents.clear();
        try {
            for (AdminEnrollmentRepository.EnrollmentSummary enrollment : enrollmentService.findEnrollments()) {
                String name = enrollment.courseCode() + " - " + enrollment.courseName();
                String term = enrollment.semester() + " | " + enrollment.academicYear();
                CourseRequest course = new CourseRequest(name, term, enrollment.units(), true);
                pendingStudents.add(new PendingStudent(enrollment.enrollmentId(), enrollment.studentName(),
                        enrollment.studentId(), enrollment.program(), enrollment.yearLevel(), enrollment.status(),
                        List.of(course)));
            }
            selectedStudent = pendingStudents.isEmpty() ? null : pendingStudents.get(0);
        } catch (SQLException | SecurityException exception) {
            JOptionPane.showMessageDialog(null, "Unable to load enrollment records from the database.",
                    "Enrollment Records", JOptionPane.ERROR_MESSAGE);
            selectedStudent = null;
        }
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(PAGE_BG);

        JScrollPane scrollPane = new JScrollPane(createBody());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(PAGE_BG);
        main.add(scrollPane, BorderLayout.CENTER);

        content.add(main, BorderLayout.CENTER);
        return content;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(SIDEBAR_BG);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        LogoView logo = new LogoView();
        logo.setAlignmentX(0.5f);
        logo.setPreferredSize(new Dimension(120, 130));
        logo.setMaximumSize(new Dimension(120, 130));
        top.add(logo);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.add(createNavButton("Dashboard", IconType.HOME, false)); // Set 'true' for the active page
        navPanel.add(createNavButton("Student Masterlist", IconType.LIST, false));
        navPanel.add(createNavButton("Enrollment Validation", IconType.WINDOW, true));
        navPanel.add(createNavButton("Courses & Sections", IconType.LAYOUT, false));
        navPanel.add(createNavButton("System Audit", IconType.USER_OUTLINE, false));
        sidebar.add(navPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 0, 20, 0));
        JButton signOut = createNavButton("Sign Out", IconType.LOGOUT, false);
        signOut.addActionListener(e -> {
            window.dispose();
            new LoginFrame().showWindow();
        });
        bottom.add(signOut, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text, IconType iconType, boolean active) {
        JButton button = new JButton(text, new VectorIcon(iconType, active ? GOLD : Color.WHITE));

        button.setMaximumSize(new Dimension(250, 45));
        button.setPreferredSize(new Dimension(240, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setMargin(new Insets(0, 20, 0, 10));

        button.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 14));
        button.setForeground(active ? Color.WHITE : new Color(200, 200, 200));
        button.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BG);

        button.setBorder(active ?
                BorderFactory.createMatteBorder(0, 4, 0, 0, GOLD) :
                BorderFactory.createEmptyBorder(0, 4, 0, 0));

        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (text.equals("Sign Out")) {
            return button;
        }

        button.addActionListener(e -> {
            if (active) return; // Don't reload the page if we are already on it

            // Universally safely close the current window
            Window currentWindow = SwingUtilities.getWindowAncestor(button);
            if (currentWindow != null) {
                currentWindow.dispose();
            }

            // Standard if/else navigation exactly like the Student Panels
            if (text.equals("Dashboard")) {
                new AdminDashboardFrame().showWindow();
            } else if (text.equals("Student Masterlist")) {
                new AdminMasterlistFrame().showWindow();
            } else if (text.equals("Enrollment Validation")) {
                new AdminValidationFrame().showWindow();
            } else if (text.equals("Courses & Sections")) {
                new AdminCourseFrame().setVisible(true);
            } else if (text.equals("System Audit")) {
                new AdminLogsFrame().setVisible(true);
            }
        });

        return button;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(PAGE_BG);
        body.setBorder(new EmptyBorder(30, 35, 30, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Title
        gbc.insets = new Insets(0, 0, 25, 0);
        JLabel pageTitle = new JLabel("Enrollment Records");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        pageTitle.setForeground(SIDEBAR_BG);
        body.add(pageTitle, gbc);

        // Stats Row
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        body.add(createStatsRow(), gbc);

        // Main Columns (Left List & Right Inspection)
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel columnsPanel = new JPanel(new GridBagLayout());
        columnsPanel.setOpaque(false);

        GridBagConstraints colGbc = new GridBagConstraints();
        colGbc.fill = GridBagConstraints.BOTH;
        colGbc.weighty = 1.0;

        // Left Column (List)
        colGbc.gridx = 0;
        colGbc.weightx = 0.6;
        colGbc.insets = new Insets(0, 0, 0, 15);
        columnsPanel.add(createDynamicPendingApprovalList(), colGbc);

        // Right Column (Inspection Panel)
        colGbc.gridx = 1;
        colGbc.weightx = 0.4;
        colGbc.insets = new Insets(0, 15, 0, 0);
        columnsPanel.add(createDynamicInspectionPanel(), colGbc);

        body.add(columnsPanel, gbc);

        return body;
    }

    private JPanel createStatsRow() {
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 100));

        stats.add(createStatCard("Enrollment Records", String.valueOf(pendingStudents.size()), ORANGE_TEXT));
        stats.add(createStatCard("Enrolled Records", String.valueOf(countEnrolled()), GREEN_TEXT));
        stats.add(createStatCard("Validation Decisions", "Not tracked", RED_TEXT));

        return stats;
    }

    private JPanel createStatCard(String title, String value, Color valueColor) {
        RoundedPanel card = new RoundedPanel(12, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INACTIVE_CARD_BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_MUTED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valLabel.setForeground(valueColor);
        valLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (title.equals("Enrollment Records")) pendingReviewsValue = valLabel;
        if (title.equals("Enrolled Records")) conflictValue = valLabel;
        if (title.equals("Validation Decisions")) clearedTodayValue = valLabel;

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valLabel);

        return card;
    }

    private int countEnrolled() {
        return (int) pendingStudents.stream()
                .filter(enrollment -> "ENROLLED".equalsIgnoreCase(enrollment.status))
                .count();
    }

    private void updateStats() {
        if (pendingReviewsValue != null) pendingReviewsValue.setText(String.valueOf(pendingStudents.size()));
        if (conflictValue != null) conflictValue.setText(String.valueOf(countEnrolled()));
        if (clearedTodayValue != null) clearedTodayValue.setText("Not tracked");
    }

    private JPanel createDynamicPendingApprovalList() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        JLabel title = new JLabel("Enrollment Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.WEST);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(event -> {
            loadEnrollments();
            refreshPendingList();
            refreshInspectionPanel();
        });
        heading.add(refresh, BorderLayout.EAST);
        container.add(heading, BorderLayout.NORTH);

        pendingListPanel = new JPanel();
        pendingListPanel.setLayout(new BoxLayout(pendingListPanel, BoxLayout.Y_AXIS));
        pendingListPanel.setOpaque(false);
        refreshPendingList();
        container.add(pendingListPanel, BorderLayout.CENTER);
        return container;
    }

    private void refreshPendingList() {
        if (pendingListPanel == null) return;
        pendingListPanel.removeAll();
        if (pendingStudents.isEmpty()) {
            pendingListPanel.add(new JLabel("No enrollment records found."));
        }
        for (int index = 0; index < pendingStudents.size(); index++) {
            PendingStudent student = pendingStudents.get(index);
            pendingListPanel.add(createDynamicStudentCard(student, student == selectedStudent));
            if (index < pendingStudents.size() - 1) pendingListPanel.add(Box.createVerticalStrut(15));
        }
        pendingListPanel.add(Box.createVerticalGlue());
        pendingListPanel.revalidate();
        pendingListPanel.repaint();
        updateStats();
    }

    private JPanel createDynamicStudentCard(PendingStudent student, boolean active) {
        JPanel card = createStudentCard(student.name,
                "ID: " + student.id + "  |  " + student.program + " (" + student.year + ")"
                        + "  |  Enrollment #" + student.enrollmentId,
                "Status: " + (student.status == null ? "Not specified" : student.status),
                "ENROLLED".equalsIgnoreCase(student.status) ? 2 : 1, active);
        JButton inspectButton = findButton(card, "Inspect Enrollment");
        if (inspectButton != null) {
            inspectButton.addActionListener(event -> {
                selectedStudent = student;
                refreshPendingList();
                refreshInspectionPanel();
            });
        }
        return card;
    }

    private JButton findButton(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button && button.getText().equals(text)) return button;
            if (component instanceof Container child) {
                JButton found = findButton(child, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private JPanel createPendingApprovalList() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        JLabel title = new JLabel("Enrollment Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        container.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        for (int index = 0; index < pendingStudents.size(); index++) {
            PendingStudent enrollment = pendingStudents.get(index);
            list.add(createStudentCard(enrollment.name,
                    "ID: " + enrollment.id + " | Enrollment #" + enrollment.enrollmentId,
                    "Status: " + (enrollment.status == null ? "Not specified" : enrollment.status),
                    "ENROLLED".equalsIgnoreCase(enrollment.status) ? 2 : 1, index == 0));
            list.add(Box.createVerticalStrut(15));
        }
        list.add(Box.createVerticalGlue());

        container.add(list, BorderLayout.CENTER);
        return container;
    }

    private JPanel createStudentCard(String name, String details, String badgeText, int badgeType, boolean isActive) {
        Color bgColor = isActive ? ACTIVE_CARD_BG : Color.WHITE;
        Color borderColor = isActive ? ACTIVE_CARD_BORDER : INACTIVE_CARD_BORDER;

        RoundedPanel card = new RoundedPanel(12, bgColor);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, isActive ? 2 : 1),
                new EmptyBorder(18, 20, 18, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));
        card.setPreferredSize(new Dimension(0, 155));

        // Left Side Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_DARK);

        JLabel detailLabel = new JLabel("<html>" + details.replace("  |  ", "<br>") + "</html>");
        detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detailLabel.setForeground(TEXT_MUTED);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel badge = createBadge(badgeText, badgeType);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(detailLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(badge);

        // Right Side Button
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        actionPanel.setPreferredSize(new Dimension(140, 0));

        JButton inspectBtn = new JButton("Inspect Enrollment");
        inspectBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        inspectBtn.setForeground(isActive ? Color.WHITE : TEXT_DARK);
        inspectBtn.setBackground(isActive ? SIDEBAR_BG : new Color(240, 244, 248));
        inspectBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        inspectBtn.setFocusPainted(false);
        inspectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        actionPanel.add(inspectBtn);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createBadge(String text, int type) {
        Color bg, fg;
        switch (type) {
            case 0 -> { bg = BADGE_RED_BG; fg = RED_TEXT; }
            case 1 -> { bg = BADGE_YELLOW_BG; fg = BADGE_YELLOW_TEXT; }
            default -> { bg = BADGE_GREEN_BG; fg = GREEN_TEXT; }
        }

        RoundedPanel badge = new RoundedPanel(8, bg);
        badge.setLayout(new BorderLayout());
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(fg);
        badge.add(label, BorderLayout.CENTER);

        // Wrap in left-aligned panel to prevent stretching
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(badge);
        return wrapper;
    }

    private JPanel createDynamicInspectionPanel() {
        RoundedPanel panel = new RoundedPanel(16, Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(INACTIVE_CARD_BORDER, 1));
        RoundedTopPanel header = new RoundedTopPanel(16, SIDEBAR_BG);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Enrollment Record");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);
        panel.add(header, BorderLayout.NORTH);
        inspectionContent = new JPanel();
        inspectionContent.setLayout(new BoxLayout(inspectionContent, BoxLayout.Y_AXIS));
        inspectionContent.setOpaque(false);
        inspectionContent.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.add(inspectionContent, BorderLayout.CENTER);
        refreshInspectionPanel();
        return panel;
    }

    private void refreshInspectionPanel() {
        if (inspectionContent == null) return;
        inspectionContent.removeAll();
        if (selectedStudent == null) {
            inspectionContent.add(new JLabel("No enrollment record is available to inspect."));
            inspectionContent.revalidate();
            inspectionContent.repaint();
            return;
        }
        JLabel name = new JLabel(selectedStudent.name);
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setForeground(TEXT_DARK);
        JLabel details = new JLabel(selectedStudent.id + " | " + selectedStudent.program + " - "
                + selectedStudent.year + " | Enrollment #" + selectedStudent.enrollmentId);
        details.setFont(new Font("SansSerif", Font.PLAIN, 12));
        details.setForeground(TEXT_MUTED);
        inspectionContent.add(name);
        inspectionContent.add(Box.createVerticalStrut(4));
        inspectionContent.add(details);
        inspectionContent.add(Box.createVerticalStrut(15));
        inspectionContent.add(new JSeparator());
        inspectionContent.add(Box.createVerticalStrut(15));
        JLabel courses = new JLabel("Enrolled Course (" + selectedStudent.totalUnits + " Units)");
        courses.setFont(new Font("SansSerif", Font.BOLD, 13));
        courses.setForeground(SIDEBAR_BG);
        inspectionContent.add(courses);
        inspectionContent.add(Box.createVerticalStrut(15));
        for (CourseRequest course : selectedStudent.courses) {
            inspectionContent.add(createCourseCard(course.name, course.schedule + " | " + course.units + " Units",
                    course.valid, null));
            inspectionContent.add(Box.createVerticalStrut(10));
        }
        RoundedPanel summary = new RoundedPanel(8, new Color(249, 246, 238));
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel units = new JLabel("Total Units: " + selectedStudent.totalUnits + ".0");
        units.setFont(new Font("SansSerif", Font.BOLD, 13));
        units.setForeground(TEXT_DARK);
        summary.add(units);
        inspectionContent.add(summary);
        inspectionContent.add(Box.createVerticalStrut(20));
        JLabel status = new JLabel("Enrollment status: "
                + (selectedStudent.status == null ? "Not specified" : selectedStudent.status));
        status.setFont(new Font("SansSerif", Font.BOLD, 12));
        status.setForeground(TEXT_MUTED);
        inspectionContent.add(status);
        inspectionContent.add(Box.createVerticalGlue());
        inspectionContent.revalidate();
        inspectionContent.repaint();
    }

    private JPanel createInspectionPanel() {
        return createDynamicInspectionPanel();
    }

    private JPanel createCourseCard(String title, String subtitle, boolean isValid, String error) {
        RoundedPanel card = new RoundedPanel(8, isValid ? Color.WHITE : BADGE_RED_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isValid ? INACTIVE_CARD_BORDER : new Color(245, 160, 160), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, isValid ? 65 : 85));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(isValid ? TEXT_DARK : RED_TEXT);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLabel.setForeground(isValid ? TEXT_MUTED : RED_TEXT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subLabel);

        if (!isValid && error != null) {
            JLabel errLabel = new JLabel("✖ " + error);
            errLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
            errLabel.setForeground(RED_TEXT);
            textPanel.add(Box.createVerticalStrut(6));
            textPanel.add(errLabel);
        }

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setOpaque(false);
        JLabel icon = new JLabel(new VectorIcon(isValid ? IconType.CHECK : IconType.CROSS, isValid ? GREEN_TEXT : RED_TEXT));
        iconPanel.add(icon);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconPanel, BorderLayout.EAST);

        return card;
    }

    private static class CourseRequest {
        private final String name;
        private final String schedule;
        private final int units;
        private final boolean valid;

        CourseRequest(String name, String schedule, int units, boolean valid) {
            this.name = name;
            this.schedule = schedule;
            this.units = units;
            this.valid = valid;
        }
    }

    private static class PendingStudent {
        private final int enrollmentId;
        private final String name;
        private final String id;
        private final String program;
        private final String year;
        private final String status;
        private final int totalUnits;
        private final List<CourseRequest> courses;

        PendingStudent(int enrollmentId, String name, String id, String program, String year, String status,
                       List<CourseRequest> courses) {
            this.enrollmentId = enrollmentId;
            this.name = name;
            this.id = id;
            this.program = program;
            this.year = year;
            this.status = status;
            this.courses = courses;
            this.totalUnits = courses.stream().mapToInt(course -> course.units).sum();
        }
    }

    // --- Auxiliary UI Classes --- //

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedTopPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedTopPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            // Draw a rounded rectangle, then overwrite bottom part with a standard rectangle to make only top rounded
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.fillRect(0, radius, getWidth(), getHeight() - radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class LogoView extends JPanel {
        private final BufferedImage image = loadLogo();
        LogoView() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double scale = Math.min((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
            int w = (int) (image.getWidth() * scale);
            int h = (int) (image.getHeight() * scale);
            g2.drawImage(image, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
            g2.dispose();
        }

        private BufferedImage loadLogo() {
            try (InputStream is = AdminValidationFrame.class.getResourceAsStream("/images/images/Frame 6 (1).png")) {
                return is == null ? null : ImageIO.read(is);
            } catch (Exception e) { return null; }
        }
    }

    public enum IconType {
        HOME, LIST, WINDOW, LAYOUT, USER_OUTLINE, LOGOUT, CHECK, CROSS
    }

    private static class VectorIcon implements javax.swing.Icon {
        private final IconType type;
        private final Color color;

        VectorIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }

        public int getIconWidth() { return type == IconType.CHECK || type == IconType.CROSS ? 14 : 20; }
        public int getIconHeight() { return type == IconType.CHECK || type == IconType.CROSS ? 14 : 20; }

        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case HOME -> {
                    g.drawPolygon(new int[]{x+2, x+10, x+18}, new int[]{x+10, y+2, y+10}, 3);
                    g.drawRect(x+4, y+10, 12, 8);
                }
                case LIST -> {
                    g.drawRect(x+2, y+4, 16, 12);
                    g.drawLine(x+5, y+8, x+9, y+8);
                    g.drawLine(x+5, y+12, x+15, y+12);
                }
                case WINDOW -> {
                    g.drawRect(x+2, y+3, 16, 14);
                    g.drawLine(x+2, y+7, x+18, y+7);
                }
                case LAYOUT -> {
                    g.drawRect(x+2, y+2, 16, 16);
                    g.drawLine(x+8, y+2, x+8, y+18);
                }
                case USER_OUTLINE -> {
                    g.drawOval(x+6, y+2, 8, 8);
                    g.drawArc(x+2, y+10, 16, 10, 0, 180);
                }
                case LOGOUT -> {
                    g.drawRect(x+5, y+3, 12, 14);
                    g.drawLine(x+1, y+10, x+8, y+10);
                }
                case CHECK -> {
                    g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine(x+2, y+7, x+5, y+10);
                    g.drawLine(x+5, y+10, x+11, y+3);
                }
                case CROSS -> {
                    // We aren't using the cross in the badge anymore but keeping it for completeness if needed
                    g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine(x+2, y+2, x+12, y+12);
                    g.drawLine(x+12, y+2, x+2, y+12);
                }
            }
            g.dispose();
        }
    }
}
