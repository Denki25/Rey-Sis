package org.example.ui.views.student;

import org.example.model.Student;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StudentGradesFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);
    private static final Color BLACK = new Color(0, 0, 0);

    // New colors for the summary blocks
    private static final Color SUMMARY_GREEN_BG = new Color(235, 242, 233);
    private static final Color SUMMARY_GOLD_BG = new Color(248, 237, 219);

    private final JFrame window = new JFrame("REY SIS | My Grade");
    private final Student student;
    private final List<GradeRowData> gradesData = new ArrayList<>();

    public StudentGradesFrame(Student student) {
        this.student = student;
        initMockGrades();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1160, 780));
        window.setSize(1360, 920);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private void initMockGrades() {
        gradesData.add(new GradeRowData("1", "COMSCI3100", 3, "1.25", "92.15", "Passed", "Waiting For Approval"));
        gradesData.add(new GradeRowData("2", "COMSCI 3101", 3, "1.00", "95.60", "Passed", "Final Grade"));
        gradesData.add(new GradeRowData("3", "INTECH 3100", 3, "1.25", "93.45", "Passed", "Waiting For Approval"));
        gradesData.add(new GradeRowData("4", "MATH 1030", 3, "1.50", "92.10", "Passed", "Final Grade"));
        gradesData.add(new GradeRowData("5", "COMSCI 3110", 3, "1.75", "91.05", "Passed", "Final Grade"));
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.add(createTopNavigation(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createBody());
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
        navigation.add(createNavigationButton("Dashboard", StudentDashboardFrame.IconType.DASHBOARD, false));
        navigation.add(createNavigationButton("My Profile", StudentDashboardFrame.IconType.PROFILE, false));
        navigation.add(createNavigationButton("Enrollment", StudentDashboardFrame.IconType.ENROLLMENT, false));
        navigation.add(createNavigationButton("My Schedule", StudentDashboardFrame.IconType.CALENDAR, false));
        navigation.add(createNavigationButton("Grades", StudentDashboardFrame.IconType.GRADES, true));
        navigation.add(createNavigationButton("Academic Records", StudentDashboardFrame.IconType.RECORDS, false));
        navigation.add(createNavigationButton("Requests", StudentDashboardFrame.IconType.REQUESTS, false));
        navigation.add(createNavigationButton("Notifications", StudentDashboardFrame.IconType.BELL, false));
        sidebar.add(navigation, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        JButton signOut = createNavigationButton("Sign Out", StudentDashboardFrame.IconType.SIGN_OUT, false);
        signOut.addActionListener(event -> signOut());
        bottom.add(signOut, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavigationButton(String text, StudentDashboardFrame.IconType iconType, boolean active) {
        JButton button = new JButton(text, new StudentDashboardFrame.DashboardIcon(iconType, active ? GOLD : new Color(202, 219, 211)));
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
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (!text.equals("Sign Out")) {
            button.addActionListener(event -> {
                if (text.equals("Dashboard")) {
                    openDashboard();
                } else if (text.equals("My Profile")) {
                    openProfile();
                } else if (text.equals("Enrollment")) {
                    openEnrollment();
                } else if (text.equals("Grades")) {
                    showGradesMessage();
                } else if (text.equals("Requests")) {
                    window.dispose();
                    new StudentRequestFrame(student).showWindow();
                } else {
                    showComingSoon(text);
                }
            });
        }
        return button;
    }

    private JPanel createTopNavigation() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));

        // Search Bar (Left)
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel searchIcon = new JLabel(new VectorIcon(VectorIcon.Type.SEARCH, MUTED));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        searchPanel.add(searchIcon, BorderLayout.WEST);

        JTextField searchField = new JTextField("Search courses, schedules, or announcements...");
        searchField.setForeground(MUTED);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(350, 24));

        // Clear placeholder on focus
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search courses, schedules, or announcements...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search courses, schedules, or announcements...");
                    searchField.setForeground(MUTED);
                }
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(searchPanel);
        header.add(leftContainer, BorderLayout.WEST);

        // User Controls (Right)
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightControls.setOpaque(false);

        // Notification Bell
        JPanel bellPanel = new JPanel(new BorderLayout());
        bellPanel.setOpaque(false);
        JLabel bellIcon = new JLabel(new VectorIcon(VectorIcon.Type.BELL, MUTED));
        JLabel badge = new JLabel("3", SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 9));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(GOLD);
        badge.setPreferredSize(new Dimension(14, 14));
        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(badge);
        bellPanel.add(badgeWrapper, BorderLayout.NORTH);
        bellPanel.add(bellIcon, BorderLayout.CENTER);
        rightControls.add(bellPanel);

        // User Profile
        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userBadge.setOpaque(false);
        JLabel avatar = new JLabel(new VectorIcon(VectorIcon.Type.USER_AVATAR, MUTED));
        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(student != null ? student.getName() : "Justine Rivera");
        userName.setFont(new Font("SansSerif", Font.BOLD, 12));
        userName.setForeground(TEXT);
        JLabel userSub = new JLabel(student != null ? student.getProgram() + " · " + student.getStudentId() : "BSIT · 2025-0011");
        userSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        userSub.setForeground(MUTED);
        userText.add(userName);
        userText.add(userSub);
        userBadge.add(avatar);
        userBadge.add(userText);

        JLabel chevron = new JLabel(" \u2304 ");
        chevron.setForeground(MUTED);
        userBadge.add(chevron);
        rightControls.add(userBadge);

        header.add(rightControls, BorderLayout.EAST);
        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(PAGE);

        // Page Title Section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 28, 20, 28));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("My Grade");
        heading.setFont(new Font("Serif", Font.BOLD, 28));
        heading.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Student Progress Report and Viewing of Grades.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        JPanel goldLine = new JPanel();
        goldLine.setBackground(GOLD);
        goldLine.setPreferredSize(new Dimension(32, 3));
        goldLine.setMaximumSize(new Dimension(32, 3));
        goldLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(goldLine);

        // Wrapper for page content
        JPanel contentWrapper = new JPanel(new BorderLayout(0, 16));
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 28, 24, 28));

        contentWrapper.add(createProfileBanner(), BorderLayout.NORTH);

        JPanel tablesAndSummary = new JPanel();
        tablesAndSummary.setLayout(new BoxLayout(tablesAndSummary, BoxLayout.Y_AXIS));
        tablesAndSummary.setOpaque(false);
        tablesAndSummary.add(createGradesTableCard());
        tablesAndSummary.add(Box.createVerticalStrut(20));
        tablesAndSummary.add(createAcademicSummaryCard());

        contentWrapper.add(tablesAndSummary, BorderLayout.CENTER);

        body.add(titlePanel);
        body.add(contentWrapper);

        return body;
    }

    private JPanel createProfileBanner() {
        BannerPanel banner = new BannerPanel();
        banner.setLayout(new BorderLayout());
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        banner.setPreferredSize(new Dimension(0, 140));

        // Left Side: Avatar and Details
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftGroup.setOpaque(false);

        // Large Circle Avatar
        JPanel avatarBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(214, 222, 214)); // Light greenish gray
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarBox.setOpaque(false);
        avatarBox.setPreferredSize(new Dimension(90, 90));
        avatarBox.setLayout(new GridBagLayout());
        JLabel studentLbl = new JLabel("STUDENT");
        studentLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        studentLbl.setForeground(DEEP_GREEN);
        avatarBox.add(studentLbl);
        leftGroup.add(avatarBox);

        // Text details
        JPanel textGroup = new JPanel();
        textGroup.setOpaque(false);
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(student != null ? student.getName() : "Justine Rivera");
        name.setFont(new Font("SansSerif", Font.BOLD, 22));
        name.setForeground(DEEP_GREEN);

        JLabel id = new JLabel(student != null ? student.getStudentId() : "2025-0011");
        id.setFont(new Font("SansSerif", Font.PLAIN, 12));
        id.setForeground(TEXT);

        JLabel prog = new JLabel(student != null ? student.getProgram() : "BS Information Technology");
        prog.setFont(new Font("SansSerif", Font.PLAIN, 12));
        prog.setForeground(TEXT);

        JLabel year = new JLabel("3rd Year");
        year.setFont(new Font("SansSerif", Font.BOLD, 12));
        year.setForeground(DEEP_GREEN);

        textGroup.add(name);
        textGroup.add(Box.createVerticalStrut(4));
        textGroup.add(id);
        textGroup.add(Box.createVerticalStrut(4));
        textGroup.add(prog);
        textGroup.add(Box.createVerticalStrut(4));
        textGroup.add(year);

        leftGroup.add(textGroup);
        banner.add(leftGroup, BorderLayout.WEST);

        // Right Side: Contact info & Edit
        JPanel rightGroup = new JPanel(new BorderLayout());
        rightGroup.setOpaque(false);

        JPanel contactGroup = new JPanel();
        contactGroup.setOpaque(false);
        contactGroup.setLayout(new BoxLayout(contactGroup, BoxLayout.Y_AXIS));
        contactGroup.add(createIconTextRow(VectorIcon.Type.EMAIL, "justine.rivera@reyu.edu"));
        contactGroup.add(Box.createVerticalStrut(8));
        contactGroup.add(createIconTextRow(VectorIcon.Type.PHONE, "0917 123 4567"));
        contactGroup.add(Box.createVerticalStrut(8));
        contactGroup.add(createIconTextRow(VectorIcon.Type.LOCATION, "San Isidro, City of REY"));
        contactGroup.add(Box.createVerticalStrut(8));
        contactGroup.add(createIconTextRow(VectorIcon.Type.CALENDAR, "")); // Intentionally left blank as in image

        rightGroup.add(contactGroup, BorderLayout.CENTER);

        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        editPanel.setOpaque(false);
        JLabel editLabel = new JLabel("Edit Profile");
        editLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        editLabel.setForeground(GOLD);
        editPanel.add(editLabel);
        editPanel.add(new JLabel(new VectorIcon(VectorIcon.Type.PENCIL, Color.WHITE)));
        editPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        rightGroup.add(editPanel, BorderLayout.EAST);

        banner.add(rightGroup, BorderLayout.EAST);
        return banner;
    }

    private JPanel createIconTextRow(VectorIcon.Type iconType, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.add(new JLabel(new VectorIcon(iconType, DEEP_GREEN)));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXT);
        row.add(lbl);
        return row;
    }

    private JPanel createGradesTableCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleGrp.setOpaque(false);
        titleGrp.add(new JLabel(new VectorIcon(VectorIcon.Type.BAR_CHART, MUTED)));
        JLabel title = new JLabel(student != null ? student.getName() : "Justine Rivera");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(DEEP_GREEN);
        titleGrp.add(title);
        header.add(titleGrp, BorderLayout.WEST);

        String[] semesters = {"1st Semester : 2026-2027", "2nd Semester : 2025-2026"};
        JComboBox<String> semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        semesterCombo.setForeground(TEXT);
        semesterCombo.setBackground(Color.WHITE);
        semesterCombo.setFocusable(false);
        semesterCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        header.add(semesterCombo, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        // Table Container
        JPanel tableContainer = new JPanel();
        tableContainer.setOpaque(false);
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));

        // Table Header
        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(8, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addTableHeaderCell(tableHeader, "No.", 0, 0.05, gbc);
        addTableHeaderCell(tableHeader, "Course Subject", 1, 0.25, gbc, SwingConstants.CENTER);
        addTableHeaderCell(tableHeader, "Units", 2, 0.10, gbc, SwingConstants.CENTER);
        addTableHeaderCell(tableHeader, "Grade", 3, 0.15, gbc, SwingConstants.CENTER);
        addTableHeaderCell(tableHeader, "Percentage", 4, 0.15, gbc, SwingConstants.CENTER);
        addTableHeaderCell(tableHeader, "Remarks", 5, 0.15, gbc, SwingConstants.CENTER);
        addTableHeaderCell(tableHeader, "Status", 6, 0.15, gbc, SwingConstants.CENTER);

        tableContainer.add(tableHeader);

        // Table Rows
        for (GradeRowData data : gradesData) {
            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(242, 243, 240)),
                    BorderFactory.createEmptyBorder(14, 12, 14, 12)
            ));

            JLabel cNo = new JLabel(data.no);
            cNo.setFont(new Font("SansSerif", Font.BOLD, 12));
            cNo.setForeground(MUTED);
            gbc.gridx = 0; gbc.weightx = 0.05; row.add(cNo, gbc);

            JLabel cSubject = new JLabel(data.subject, SwingConstants.CENTER);
            cSubject.setFont(new Font("SansSerif", Font.BOLD, 11));
            cSubject.setForeground(TEXT);
            gbc.gridx = 1; gbc.weightx = 0.25; row.add(cSubject, gbc);

            JLabel cUnits = new JLabel(String.valueOf(data.units), SwingConstants.CENTER);
            cUnits.setFont(new Font("SansSerif", Font.BOLD, 11));
            cUnits.setForeground(TEXT);
            gbc.gridx = 2; gbc.weightx = 0.10; row.add(cUnits, gbc);

            JLabel cGrade = new JLabel(data.grade, SwingConstants.CENTER);
            cGrade.setFont(new Font("SansSerif", Font.BOLD, 12));
            cGrade.setForeground(TEXT);
            gbc.gridx = 3; gbc.weightx = 0.15; row.add(cGrade, gbc);

            JLabel cPercentage = new JLabel(data.percentage, SwingConstants.CENTER);
            cPercentage.setFont(new Font("SansSerif", Font.BOLD, 11));
            cPercentage.setForeground(TEXT);
            gbc.gridx = 4; gbc.weightx = 0.15; row.add(cPercentage, gbc);

            JLabel cRemarks = new JLabel(data.remarks, SwingConstants.CENTER);
            cRemarks.setFont(new Font("SansSerif", Font.BOLD, 11));
            cRemarks.setForeground(TEXT);
            gbc.gridx = 5; gbc.weightx = 0.15; row.add(cRemarks, gbc);

            JLabel cStatus = new JLabel(data.status, SwingConstants.CENTER);
            cStatus.setFont(new Font("SansSerif", Font.BOLD, 11));
            cStatus.setForeground(MUTED);
            gbc.gridx = 6; gbc.weightx = 0.15; row.add(cStatus, gbc);

            tableContainer.add(row);
        }

        card.add(tableContainer, BorderLayout.CENTER);
        return card;
    }

    private void addTableHeaderCell(JPanel header, String text, int gridx, double weightx, GridBagConstraints gbc) {
        addTableHeaderCell(header, text, gridx, weightx, gbc, SwingConstants.LEFT);
    }

    private void addTableHeaderCell(JPanel header, String text, int gridx, double weightx, GridBagConstraints gbc, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MUTED);
        gbc.gridx = gridx;
        gbc.weightx = weightx;
        header.add(label, gbc);
    }

    private JPanel createAcademicSummaryCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel titleGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleGrp.setOpaque(false);
        titleGrp.add(new JLabel(new VectorIcon(VectorIcon.Type.BAR_CHART, DEEP_GREEN)));
        JLabel title = new JLabel("Academic Summary");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(DEEP_GREEN);
        titleGrp.add(title);
        card.add(titleGrp, BorderLayout.NORTH);

        // Data Blocks
        JPanel blocksPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        blocksPanel.setOpaque(false);

        blocksPanel.add(createSummaryBlock("5", "Enrolled Subjects", SUMMARY_GREEN_BG));
        blocksPanel.add(createSummaryBlock("1.75", "Current GPA", SUMMARY_GOLD_BG));
        blocksPanel.add(createSummaryBlock("15 / 18", "Units Enrolled", SUMMARY_GREEN_BG));
        blocksPanel.add(createSummaryBlock("Eligible", "Dean's List Standing", SUMMARY_GOLD_BG));

        card.add(blocksPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSummaryBlock(String value, String label, Color bgColor) {
        JPanel block = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBorder(BorderFactory.createEmptyBorder(24, 10, 24, 10));

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valLabel.setForeground(BLACK);
        valLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(label);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLabel.setForeground(TEXT);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        block.add(valLabel);
        block.add(Box.createVerticalStrut(6));
        block.add(subLabel);

        return block;
    }

    private void openDashboard() {
        window.dispose();
        new StudentDashboardFrame(student).showWindow();
    }

    private void openProfile() {
        window.dispose();
        new StudentProfileFrame(student).showWindow();
    }

    private void openEnrollment() {
        window.dispose();
        new StudentEnrollmentFrame(student).showWindow();
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private void showGradesMessage() {
        JOptionPane.showMessageDialog(window, "You are already viewing Grades.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showComingSoon(String module) {
        JOptionPane.showMessageDialog(window, module + " is coming next.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = StudentGradesFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static class GradeRowData {
        String no;
        String subject;
        int units;
        String grade;
        String percentage;
        String remarks;
        String status;

        GradeRowData(String no, String subject, int units, String grade, String percentage, String remarks, String status) {
            this.no = no;
            this.subject = subject;
            this.units = units;
            this.grade = grade;
            this.percentage = percentage;
            this.remarks = remarks;
            this.status = status;
        }
    }

    private static class LogoView extends JPanel {
        private final BufferedImage image = loadImage("/images/images/Frame 6 (1).png");

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

    private static class BannerPanel extends JPanel {
        BannerPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw a subtle background to simulate the sky/statue effect from the image
            LinearGradientPaint gradient = new LinearGradientPaint(
                    0, 0, getWidth(), 0,
                    new float[]{0.0f, 0.4f, 1.0f},
                    new Color[]{Color.WHITE, new Color(240, 245, 250), new Color(190, 200, 210)}
            );

            g2.setPaint(gradient);
            // Draw rounded rectangle with left side flat (to match the card inset style if desired)
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 60, 60);

            // Draw a white circle segment to simulate the clipping behind the text
            g2.setColor(Color.WHITE);
            g2.fillArc(-getWidth()/4, -getHeight(), getWidth() + 100, getHeight() * 3, -40, 80);

            g2.setColor(BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 60, 60);

            g2.dispose();
            super.paintComponent(g);
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
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(background);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.setColor(BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class VectorIcon implements javax.swing.Icon {
        enum Type { SEARCH, BELL, USER_AVATAR, PENCIL, EMAIL, PHONE, LOCATION, CALENDAR, BAR_CHART }

        private final Type type;
        private final Color color;

        VectorIcon(Type type, Color color) {
            this.type = type;
            this.color = color;
        }

        public int getIconWidth() { return 18; }
        public int getIconHeight() { return 18; }

        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case SEARCH -> {
                    g.drawOval(x + 2, y + 2, 10, 10);
                    g.drawLine(x + 10, y + 10, x + 16, y + 16);
                }
                case BELL -> {
                    g.drawArc(x + 4, y + 3, 10, 10, 0, 180);
                    g.drawLine(x + 4, y + 8, x + 4, y + 13);
                    g.drawLine(x + 14, y + 8, x + 14, y + 13);
                    g.drawLine(x + 2, y + 13, x + 16, y + 13);
                    g.drawArc(x + 7, y + 13, 4, 4, 180, 180);
                }
                case USER_AVATAR -> {
                    g.drawOval(x + 5, y + 2, 8, 8);
                    g.drawArc(x + 2, y + 9, 14, 8, 0, 180);
                }
                case PENCIL -> {
                    g.drawLine(x + 3, y + 15, x + 6, y + 15);
                    g.drawLine(x + 3, y + 15, x + 3, y + 12);
                    g.drawLine(x + 3, y + 12, x + 12, y + 3);
                    g.drawLine(x + 12, y + 3, x + 15, y + 6);
                    g.drawLine(x + 15, y + 6, x + 6, y + 15);
                }
                case EMAIL -> {
                    g.drawRect(x + 2, y + 4, 14, 10);
                    g.drawLine(x + 2, y + 4, x + 9, y + 9);
                    g.drawLine(x + 16, y + 4, x + 9, y + 9);
                }
                case PHONE -> {
                    g.drawRoundRect(x + 4, y + 2, 10, 14, 4, 4);
                    g.drawLine(x + 7, y + 13, x + 11, y + 13);
                }
                case LOCATION -> {
                    g.drawOval(x + 5, y + 2, 8, 8);
                    g.drawLine(x + 5, y + 6, x + 9, y + 16);
                    g.drawLine(x + 13, y + 6, x + 9, y + 16);
                    g.drawOval(x + 8, y + 5, 2, 2);
                }
                case CALENDAR -> {
                    g.drawRect(x + 3, y + 4, 12, 11);
                    g.drawLine(x + 3, y + 8, x + 15, y + 8);
                    g.drawLine(x + 6, y + 2, x + 6, y + 4);
                    g.drawLine(x + 12, y + 2, x + 12, y + 4);
                }
                case BAR_CHART -> {
                    g.drawRect(x + 2, y + 8, 3, 6);
                    g.drawRect(x + 7, y + 4, 3, 10);
                    g.drawRect(x + 12, y + 2, 3, 12);
                }
            }
            g.dispose();
        }
    }
}