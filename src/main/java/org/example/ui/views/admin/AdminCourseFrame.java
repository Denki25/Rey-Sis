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
        initializeCourses(null);
    }

    private void initializeCourses(String selectedCode) {
        courses.clear();
        try {
            for (Course course : courseRepository.findAll()) {
                courses.add(new CourseData(course.getCode(), course.getTitle(), course.getUnits(),
                        course.getDepartment(), courseRepository.findSchedulesByCourseCode(course.getCode())));
            }
        } catch (java.sql.SQLException exception) {
            JOptionPane.showMessageDialog(this, "Unable to load courses from the database.", "Course Error", JOptionPane.ERROR_MESSAGE);
        }
        selectedCourse = courses.stream()
                .filter(course -> course.code.equals(selectedCode))
                .findFirst()
                .orElse(courses.isEmpty() ? null : courses.get(0));
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

        JButton addCourse = createPrimaryButton("+ Add Course", Color.WHITE, BRAND_GREEN, BRAND_GREEN);
        addCourse.addActionListener(event -> showCourseDialog(null));
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
        if (courses.isEmpty()) {
            catalogListPanel.add(new JLabel("No course records found."));
            catalogListPanel.revalidate();
            catalogListPanel.repaint();
            return;
        }
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
        JPanel card = createCourseCard(course.code, course.title, course.schedules.size() + " Schedules", active);
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
        if (sectionsPanel == null) return;
        sectionsPanel.removeAll();
        if (selectedCourse == null) {
            selectedCourseTitle.setText("No course selected");
            selectedCourseSubtitle.setText("");
            activeSectionsTitle.setText("Schedules");
            sectionsPanel.add(new JLabel("No course records found."));
            sectionsPanel.revalidate();
            sectionsPanel.repaint();
            return;
        }
        selectedCourseTitle.setText(selectedCourse.code + " — " + selectedCourse.title);
        selectedCourseSubtitle.setText(selectedCourse.units + " Units | "
                + (selectedCourse.department == null ? "" : selectedCourse.department) + " Dept");
        activeSectionsTitle.setText("Schedules (" + selectedCourse.schedules.size() + ")");
        if (selectedCourse.schedules.isEmpty()) {
            sectionsPanel.add(new JLabel("No schedule records are available for this course."));
        }
        for (CourseRepository.CourseSchedule schedule : selectedCourse.schedules) {
            sectionsPanel.add(createDynamicScheduleCard(schedule));
            sectionsPanel.add(Box.createVerticalStrut(15));
        }
        sectionsPanel.revalidate();
        sectionsPanel.repaint();
    }

    private JPanel createDynamicScheduleCard(CourseRepository.CourseSchedule schedule) {
        RoundedPanel card = new RoundedPanel(15, BG_LIGHT, BORDER_COLOR);
        card.setLayout(new GridLayout(3, 1, 0, 5));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        JLabel day = new JLabel(schedule.dayOfWeek());
        day.setFont(new Font("Segoe UI", Font.BOLD, 15));
        day.setForeground(TEXT_DARK);
        JLabel time = new JLabel("Schedule: " + schedule.startTime() + " - " + schedule.endTime());
        time.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        time.setForeground(TEXT_MUTED);
        JLabel details = new JLabel("Room: " + schedule.room() + " | Instructor: " + schedule.instructor());
        details.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        details.setForeground(TEXT_MUTED);
        card.add(day);
        card.add(time);
        card.add(details);
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
        return createDynamicCourseCatalog();
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
        return createDynamicSectionDetails();
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
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Code:")); form.add(code);
        form.add(new JLabel("Title:")); form.add(title);
        form.add(new JLabel("Units:")); form.add(units);
        form.add(new JLabel("Department:")); form.add(department);
        int result = JOptionPane.showConfirmDialog(this, form, course == null ? "Add Course" : "Edit Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            int unitCount = Integer.parseInt(units.getText().trim());
            if (code.getText().trim().isEmpty() || title.getText().trim().isEmpty() || unitCount <= 0) throw new IllegalArgumentException();
            if (course == null) {
                courseRepository.create(new Course(code.getText().trim(), title.getText().trim(), unitCount,
                        department.getText().trim()), department.getText().trim());
            } else {
                courseRepository.update(course.code,
                        new Course(code.getText().trim(), title.getText().trim(), unitCount, department.getText().trim()),
                        department.getText().trim());
            }
            initializeCourses(code.getText().trim());
            refreshCatalog();
            refreshSectionDetails();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, "Please enter valid course details and units.",
                    "Invalid Course", JOptionPane.WARNING_MESSAGE);
        } catch (java.sql.SQLException exception) {
            JOptionPane.showMessageDialog(this, "Unable to save the course to the database.",
                    "Course Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class CourseData {
        private String code;
        private String title;
        private int units;
        private String department;
        private final List<CourseRepository.CourseSchedule> schedules;

        CourseData(String code, String title, int units, String department,
                   List<CourseRepository.CourseSchedule> schedules) {
            this.code = code;
            this.title = title;
            this.units = units;
            this.department = department;
            this.schedules = new ArrayList<>(schedules);
        }
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
