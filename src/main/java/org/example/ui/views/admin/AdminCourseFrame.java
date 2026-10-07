package org.example.ui.views.admin;

import org.example.data.CourseRepository;
import org.example.model.Course;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminCourseFrame extends JFrame {

    private final Color BRAND_GREEN = new Color(11, 59, 36);
    private final Color BRAND_YELLOW = new Color(223, 179, 61);
    private final Color SIDEBAR_BG = new Color(7, 43, 33);
    private final Color SIDEBAR_ACTIVE = new Color(20, 60, 48);
    private final Color BG_LIGHT = new Color(248, 250, 252);
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_MUTED = new Color(100, 116, 139);
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color GOLD = new Color(207, 160, 48);
    private final List<CourseData> courses = new ArrayList<>();
    private final CourseRepository courseRepository = new CourseRepository();
    private JPanel catalogListPanel;
    private JPanel sectionsPanel;
    private JLabel selectedCourseTitle;
    private JLabel selectedCourseSubtitle;
    private JLabel activeSectionsTitle;
    private JTextField courseSearchField;
    private CourseData selectedCourse;

    public AdminCourseFrame() {
        initializeCourses();
        setTitle("REY SIS - Courses & Sections");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private void initializeCourses() {
        try {
            for (Course course : courseRepository.findAll()) {
                courses.add(new CourseData(course.getCode(), course.getTitle(), course.getUnits(), "", ""));
            }
        } catch (java.sql.SQLException exception) {
            JOptionPane.showMessageDialog(this, "Unable to load courses from the database.", "Course Error", JOptionPane.ERROR_MESSAGE);
        }
        if (!courses.isEmpty()) {
            selectedCourse = courses.get(0);
        }
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
        navPanel.add(createNavButton("Dashboard", IconType.HOME, false));
        navPanel.add(createNavButton("Student Masterlist", IconType.LIST, false));
        navPanel.add(createNavButton("Enrollment Validation", IconType.WINDOW, false));
        navPanel.add(createNavButton("Courses & Sections", IconType.LAYOUT, true));
        navPanel.add(createNavButton("System Audit", IconType.USER_OUTLINE, false));
        sidebar.add(navPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 0, 20, 0));
        JButton signOut = createNavButton("Sign Out", IconType.LOGOUT, false);
        signOut.addActionListener(e -> {
            dispose();
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

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Courses & Sections");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton addSection = createPrimaryButton("+ Add Section", BRAND_GREEN, Color.WHITE, BRAND_GREEN);
        addSection.addActionListener(event -> showSectionDialog(null));
        JButton addCourse = createPrimaryButton("+ Add Course", Color.WHITE, BRAND_GREEN, BRAND_GREEN);
        addCourse.addActionListener(event -> showCourseDialog(null));
        btnPanel.add(addSection);
        btnPanel.add(addCourse);
        header.add(btnPanel, BorderLayout.EAST);

        // Split Content Area
        JPanel contentSplit = new JPanel(new BorderLayout(30, 0));
        contentSplit.setBackground(Color.WHITE);

        contentSplit.add(createDynamicCourseCatalog(), BorderLayout.WEST);
        contentSplit.add(createDynamicSectionDetails(), BorderLayout.CENTER);

        main.add(header, BorderLayout.NORTH);
        main.add(contentSplit, BorderLayout.CENTER);

        return main;
    }

    private JPanel createDynamicCourseCatalog() {
        JPanel catalog = new JPanel(new BorderLayout(0, 15));
        catalog.setBackground(Color.WHITE);
        catalog.setPreferredSize(new Dimension(320, 0));
        JLabel title = new JLabel("Course Catalog");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        catalog.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        RoundedPanel searchBox = new RoundedPanel(10, BG_LIGHT, BORDER_COLOR);
        searchBox.setLayout(new BorderLayout());
        searchBox.setBorder(new EmptyBorder(8, 12, 8, 12));
        searchBox.setMaximumSize(new Dimension(320, 40));
        courseSearchField = new JTextField("Search course code or title...");
        courseSearchField.setBorder(null);
        courseSearchField.setOpaque(false);
        courseSearchField.setForeground(TEXT_MUTED);
        courseSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() { refreshCatalog(); }
            public void insertUpdate(javax.swing.event.DocumentEvent event) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent event) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent event) { filter(); }
        });
        courseSearchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent event) {
                if (courseSearchField.getText().equals("Search course code or title...")) {
                    courseSearchField.setText(""); courseSearchField.setForeground(TEXT_DARK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent event) {
                if (courseSearchField.getText().trim().isEmpty()) {
                    courseSearchField.setText("Search course code or title..."); courseSearchField.setForeground(TEXT_MUTED);
                }
            }
        });
        searchBox.add(new JLabel(new VectorIcon(IconType.SEARCH, TEXT_MUTED)), BorderLayout.WEST);
        searchBox.add(courseSearchField, BorderLayout.CENTER);
        content.add(searchBox);
        content.add(Box.createVerticalStrut(15));
        catalogListPanel = new JPanel();
        catalogListPanel.setLayout(new BoxLayout(catalogListPanel, BoxLayout.Y_AXIS));
        catalogListPanel.setBackground(Color.WHITE);
        content.add(catalogListPanel);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        catalog.add(scroll, BorderLayout.CENTER);
        refreshCatalog();
        return catalog;
    }

    private void refreshCatalog() {
        if (catalogListPanel == null) return;
        catalogListPanel.removeAll();
        String query = courseSearchField == null ? "" : courseSearchField.getText().trim().toLowerCase();
        if (query.equals("search course code or title...")) query = "";
        for (CourseData course : courses) {
            if (!query.isEmpty() && !course.code.toLowerCase().contains(query)
                    && !course.title.toLowerCase().contains(query)) continue;
            catalogListPanel.add(createDynamicCourseCard(course, course == selectedCourse));
            catalogListPanel.add(Box.createVerticalStrut(10));
        }
        catalogListPanel.revalidate();
        catalogListPanel.repaint();
    }

    private JPanel createDynamicCourseCard(CourseData course, boolean active) {
        JPanel card = createCourseCard(course.code, course.title, course.sections.size() + " Secs", active);
        addClickListener(card, new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                selectCourse(course);
            }
        });
        return card;
    }

    private void addClickListener(Component component, java.awt.event.MouseListener listener) {
        component.addMouseListener(listener);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) addClickListener(child, listener);
        }
    }

    private void selectCourse(CourseData course) {
        selectedCourse = course;
        refreshCatalog();
        refreshSectionDetails();
    }

    private JPanel createDynamicSectionDetails() {
        JPanel details = new JPanel(new BorderLayout(0, 20));
        details.setBackground(Color.WHITE);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(Color.WHITE);
        selectedCourseTitle = new JLabel();
        selectedCourseTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        selectedCourseTitle.setForeground(TEXT_DARK);
        selectedCourseSubtitle = new JLabel();
        selectedCourseSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        selectedCourseSubtitle.setForeground(TEXT_MUTED);
        titlePanel.add(selectedCourseTitle);
        titlePanel.add(selectedCourseSubtitle);
        JButton editCourse = new JButton("Edit Course");
        editCourse.setForeground(BRAND_GREEN);
        editCourse.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BRAND_GREEN));
        editCourse.setContentAreaFilled(false);
        editCourse.setFocusPainted(false);
        editCourse.addActionListener(event -> showCourseDialog(selectedCourse));
        header.add(titlePanel, BorderLayout.CENTER);
        header.add(editCourse, BorderLayout.EAST);
        details.add(header, BorderLayout.NORTH);
        sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(Color.WHITE);
        JPanel sectionContainer = new JPanel(new BorderLayout(0, 15));
        sectionContainer.setBackground(Color.WHITE);
        activeSectionsTitle = new JLabel();
        activeSectionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activeSectionsTitle.setForeground(TEXT_DARK);
        sectionContainer.add(activeSectionsTitle, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(sectionsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        sectionContainer.add(scroll, BorderLayout.CENTER);
        details.add(sectionContainer, BorderLayout.CENTER);
        refreshSectionDetails();
        return details;
    }

    private void refreshSectionDetails() {
        if (sectionsPanel == null || selectedCourse == null) return;
        selectedCourseTitle.setText(selectedCourse.code + " — " + selectedCourse.title);
        selectedCourseSubtitle.setText(selectedCourse.units + " Units | " + selectedCourse.department + " Dept | Term: " + selectedCourse.term);
        activeSectionsTitle.setText("Active Sections (" + selectedCourse.sections.size() + ")");
        sectionsPanel.removeAll();
        for (SectionData section : selectedCourse.sections) {
            sectionsPanel.add(createDynamicSectionCard(selectedCourse, section));
            sectionsPanel.add(Box.createVerticalStrut(15));
        }
        sectionsPanel.revalidate();
        sectionsPanel.repaint();
    }

    private JPanel createDynamicSectionCard(CourseData course, SectionData section) {
        JPanel card = createSectionCard(section.name, section.isFull() ? "FULL" : "OPEN", section.schedule,
                section.room, section.instructor, section.enrolled, section.capacity);
        JButton edit = findButton(card, "Edit");
        JButton students = findButton(card, "Students");
        if (edit != null) edit.addActionListener(event -> showSectionDialog(section));
        if (students != null) students.addActionListener(event -> showStudentsDialog(section));
        return card;
    }

    private JButton findButton(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button && button.getText().equals(text)) return button;
            if (component instanceof Container child) {
                JButton result = findButton(child, text);
                if (result != null) return result;
            }
        }
        return null;
    }

    private JPanel createCourseCatalog() {
        JPanel catalog = new JPanel(new BorderLayout(0, 15));
        catalog.setBackground(Color.WHITE);
        catalog.setPreferredSize(new Dimension(320, 0));

        JLabel title = new JLabel("Course Catalog");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        catalog.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        // Search Box (Mockup)
        RoundedPanel searchBox = new RoundedPanel(10, BG_LIGHT, BORDER_COLOR);
        searchBox.setLayout(new BorderLayout());
        searchBox.setBorder(new EmptyBorder(10, 15, 10, 15));
        searchBox.setMaximumSize(new Dimension(320, 40));
        JLabel searchLbl = new JLabel("Search course code or title...");
        searchLbl.setForeground(TEXT_MUTED);
        searchBox.add(searchLbl, BorderLayout.WEST);

        listPanel.add(searchBox);
        listPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Course Cards
        listPanel.add(createCourseCard("DBMS 101", "Database Management Systems", "4 Secs", true));
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        listPanel.add(createCourseCard("OOP 202", "Object-Oriented Programming", "3 Secs", false));
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        listPanel.add(createCourseCard("OS 301", "Operating Systems", "2 Secs", false));
        listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        listPanel.add(createCourseCard("ACC 101", "Fundamentals of Accounting", "5 Secs", false));

        catalog.add(listPanel, BorderLayout.CENTER);
        return catalog;
    }

    private JPanel createCourseCard(String code, String name, String secs, boolean active) {
        RoundedPanel card = new RoundedPanel(15, active ? BRAND_GREEN : BG_LIGHT, active ? BRAND_GREEN : BORDER_COLOR);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(320, 90));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel lblCode = new JLabel(code);
        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCode.setForeground(active ? Color.WHITE : TEXT_DARK);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblName.setForeground(active ? new Color(200, 220, 210) : TEXT_MUTED);

        textPanel.add(lblCode);
        textPanel.add(lblName);

        RoundedPanel badge = new RoundedPanel(15, active ? new Color(25, 85, 55) : new Color(226, 232, 240), new Color(0,0,0,0));
        badge.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel lblSecs = new JLabel(secs);
        lblSecs.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSecs.setForeground(active ? Color.WHITE : TEXT_DARK);
        badge.add(lblSecs);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(badge);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createSectionDetails() {
        JPanel details = new JPanel(new BorderLayout(0, 20));
        details.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("DBMS 101 — Database Management Systems");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_DARK);

        JLabel subTitle = new JLabel("3 Units | Computer Science Dept | Term: 1st Sem 2026-2027");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setForeground(TEXT_MUTED);

        titlePanel.add(title);
        titlePanel.add(subTitle);

        JLabel editBtn = new JLabel("Edit Course");
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editBtn.setForeground(BRAND_GREEN);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(editBtn, BorderLayout.EAST);

        // Active Sections List
        JPanel listContainer = new JPanel(new BorderLayout(0, 15));
        listContainer.setBackground(Color.WHITE);

        JLabel listTitle = new JLabel("Active Sections (4)");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        listTitle.setForeground(TEXT_DARK);
        listContainer.add(listTitle, BorderLayout.NORTH);

        JPanel sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(Color.WHITE);

        sectionsPanel.add(createSectionCard("Section A (DBMS101-A)", "OPEN", "MWF 08:00 AM - 09:30 AM", "Lab 3", "Prof. M. Santos", 42, 45));
        sectionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sectionsPanel.add(createSectionCard("Section B (DBMS101-B)", "FULL", "TTH 10:00 AM - 11:30 AM", "Lab 2", "Prof. R. Reyes", 45, 45));
        sectionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sectionsPanel.add(createSectionCard("Section C (DBMS101-C)", "OPEN", "MWF 01:00 PM - 02:30 PM", "Rm 204", "Prof. M. Santos", 28, 45));
        sectionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sectionsPanel.add(createSectionCard("Section D (DBMS101-D)", "OPEN", "SAT 09:00 AM - 12:00 PM", "Lab 1", "Prof. J. Cruz", 15, 45));

        JScrollPane scrollPane = new JScrollPane(sectionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        listContainer.add(scrollPane, BorderLayout.CENTER);

        details.add(headerPanel, BorderLayout.NORTH);
        details.add(listContainer, BorderLayout.CENTER);

        return details;
    }

    private JPanel createSectionCard(String secName, String status, String sched, String room, String instructor, int current, int max) {
        RoundedPanel card = new RoundedPanel(15, BG_LIGHT, BORDER_COLOR);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 1: Title and Status
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topRow.setOpaque(false);
        JLabel lblTitle = new JLabel(secName);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(TEXT_DARK);

        boolean isOpen = status.equals("OPEN");
        Color badgeBg = isOpen ? new Color(214, 245, 226) : new Color(253, 216, 216);
        Color badgeFg = isOpen ? new Color(15, 110, 50) : new Color(180, 20, 20);

        RoundedPanel badge = new RoundedPanel(15, badgeBg, new Color(0,0,0,0));
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblStatus.setForeground(badgeFg);
        badge.add(lblStatus);

        topRow.add(lblTitle);
        topRow.add(badge);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(topRow, gbc);

        // Row 2 & 3: Details (Sched, Room, Instructor)
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblSched = new JLabel("Sched: " + sched);
        lblSched.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSched.setForeground(TEXT_MUTED);

        JLabel lblRoomInst = new JLabel("Room: " + room + " | Instructor: " + instructor);
        lblRoomInst.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRoomInst.setForeground(TEXT_MUTED);

        detailsPanel.add(lblSched);
        detailsPanel.add(lblRoomInst);

        gbc.gridy = 1;
        card.add(detailsPanel, gbc);

        // Right side: Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        actionPanel.setOpaque(false);

        JButton btnEdit = createLinkButton("Edit");
        JButton btnStudents = createLinkButton("Students");

        actionPanel.add(btnEdit);
        actionPanel.add(btnStudents);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        card.add(actionPanel, gbc);

        // Row 4: Capacity & Progress Bar
        JPanel capacityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        capacityPanel.setOpaque(false);

        JLabel lblCap = new JLabel("Capacity: " + current + " / " + max);
        lblCap.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCap.setForeground(TEXT_MUTED);

        ProgressBar bar = new ProgressBar(current, max, isOpen ? BRAND_GREEN : new Color(230, 60, 60));

        capacityPanel.add(lblCap);
        capacityPanel.add(bar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        card.add(capacityPanel, gbc);

        return card;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(BRAND_GREEN);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BRAND_GREEN));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createPrimaryButton(String text, Color bg, Color fg, Color border) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border), new EmptyBorder(8, 20, 8, 20)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showCourseDialog(CourseData course) {
        JTextField code = new JTextField(course == null ? "" : course.code);
        JTextField title = new JTextField(course == null ? "" : course.title);
        JTextField units = new JTextField(course == null ? "3" : String.valueOf(course.units));
        JTextField department = new JTextField(course == null ? "" : course.department);
        JTextField term = new JTextField(course == null ? "1st Sem 2026-2027" : course.term);
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Code:")); form.add(code);
        form.add(new JLabel("Title:")); form.add(title);
        form.add(new JLabel("Units:")); form.add(units);
        form.add(new JLabel("Department:")); form.add(department);
        form.add(new JLabel("Term:")); form.add(term);
        int result = JOptionPane.showConfirmDialog(this, form, course == null ? "Add Course" : "Edit Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            int unitCount = Integer.parseInt(units.getText().trim());
            if (code.getText().trim().isEmpty() || title.getText().trim().isEmpty() || unitCount <= 0) throw new IllegalArgumentException();
            if (course == null) {
                CourseData newCourse = new CourseData(code.getText().trim(), title.getText().trim(), unitCount,
                        department.getText().trim(), term.getText().trim());
                courseRepository.create(new Course(newCourse.code, newCourse.title, newCourse.units), newCourse.department);
                courses.add(newCourse);
                selectedCourse = newCourse;
            } else {
                String oldCode = course.code;
                course.code = code.getText().trim();
                course.title = title.getText().trim();
                course.units = unitCount;
                course.department = department.getText().trim();
                course.term = term.getText().trim();
                courseRepository.update(oldCode, new Course(course.code, course.title, course.units), course.department);
            }
            refreshCatalog();
            refreshSectionDetails();
        } catch (IllegalArgumentException | java.sql.SQLException exception) {
            JOptionPane.showMessageDialog(this, "Please enter valid course details and units.", "Invalid Course", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showSectionDialog(SectionData section) {
        if (selectedCourse == null) return;
        JTextField name = new JTextField(section == null ? "" : section.name);
        JTextField schedule = new JTextField(section == null ? "" : section.schedule);
        JTextField room = new JTextField(section == null ? "" : section.room);
        JTextField instructor = new JTextField(section == null ? "" : section.instructor);
        JTextField capacity = new JTextField(section == null ? "45" : String.valueOf(section.capacity));
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Section Name:")); form.add(name);
        form.add(new JLabel("Schedule:")); form.add(schedule);
        form.add(new JLabel("Room:")); form.add(room);
        form.add(new JLabel("Instructor:")); form.add(instructor);
        form.add(new JLabel("Max Capacity:")); form.add(capacity);
        int result = JOptionPane.showConfirmDialog(this, form, section == null ? "Add Section" : "Edit Section",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            int max = Integer.parseInt(capacity.getText().trim());
            if (name.getText().trim().isEmpty() || max <= 0) throw new IllegalArgumentException();
            if (section == null) {
                selectedCourse.sections.add(new SectionData(name.getText().trim(), schedule.getText().trim(),
                        room.getText().trim(), instructor.getText().trim(), 0, max));
            } else {
                section.name = name.getText().trim();
                section.schedule = schedule.getText().trim();
                section.room = room.getText().trim();
                section.instructor = instructor.getText().trim();
                section.capacity = max;
            }
            refreshCatalog();
            refreshSectionDetails();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, "Please enter valid section details and capacity.", "Invalid Section", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showStudentsDialog(SectionData section) {
        String[] columns = {"STUDENT ID", "FULL NAME", "PROGRAM"};
        Object[][] students = {
                {"2025-0011", "Justine Rivera", "BS Information Technology"},
                {"2025-0012", "Mark Mendoza", "BS Civil Engineering"},
                {"2025-0013", "Prince Cariaga", "BS Information Technology"}
        };
        int count = Math.min(section.enrolled, students.length);
        Object[][] visibleStudents = new Object[count][3];
        System.arraycopy(students, 0, visibleStudents, 0, count);
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(visibleStudents, columns) {
            public boolean isCellEditable(int row, int column) { return false; }
        });
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(480, 180));
        JOptionPane.showMessageDialog(this, scroll, "Students - " + section.name, JOptionPane.INFORMATION_MESSAGE);
    }

    private static class CourseData {
        private String code;
        private String title;
        private int units;
        private String department;
        private String term;
        private final List<SectionData> sections = new ArrayList<>();

        CourseData(String code, String title, int units, String department, String term) {
            this.code = code;
            this.title = title;
            this.units = units;
            this.department = department;
            this.term = term;
        }
    }

    private static class SectionData {
        private String name;
        private String schedule;
        private String room;
        private String instructor;
        private int enrolled;
        private int capacity;

        SectionData(String name, String schedule, String room, String instructor, int enrolled, int capacity) {
            this.name = name;
            this.schedule = schedule;
            this.room = room;
            this.instructor = instructor;
            this.enrolled = enrolled;
            this.capacity = capacity;
        }

        boolean isFull() { return enrolled >= capacity; }
    }

    // --- Custom UI Components ---

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bg;
        private Color border;

        public RoundedPanel(int radius, Color bg, Color border) {
            this.radius = radius;
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));

            if (border != null && border.getAlpha() > 0) {
                g2.setColor(border);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            }
            g2.dispose();
        }
    }

    class ProgressBar extends JPanel {
        private int current, max;
        private Color fillCol;

        public ProgressBar(int current, int max, Color fillCol) {
            this.current = current;
            this.max = max;
            this.fillCol = fillCol;
            setPreferredSize(new Dimension(100, 8));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw background track
            g2.setColor(BORDER_COLOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

            // Draw filled track
            int fillWidth = (int) (((double) current / max) * getWidth());
            g2.setColor(fillCol);
            g2.fillRoundRect(0, 0, fillWidth, getHeight(), getHeight(), getHeight());

            g2.dispose();
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
            try (InputStream is = LogoView.class.getResourceAsStream("/images/images/Frame 6 (1).png")) {
                return is == null ? null : ImageIO.read(is);
            } catch (Exception e) { return null; }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminCourseFrame().setVisible(true);
        });
    }

    public enum IconType {
        HOME, LIST, WINDOW, LAYOUT, USER_OUTLINE, LOGOUT, SEARCH
    }

    private static class VectorIcon implements javax.swing.Icon {
        private final IconType type;
        private final Color color;

        VectorIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }

        public int getIconWidth() { return 20; }
        public int getIconHeight() { return 20; }

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
                case SEARCH -> {
                    g.drawOval(x + 3, y + 3, 10, 10);
                    g.drawLine(x + 12, y + 12, x + 18, y + 18);
                }
            }
            g.dispose();
        }
    }
}
