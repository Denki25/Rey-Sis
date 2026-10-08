package org.example.ui.views.admin;

import org.example.data.StudentDirectoryRepository;
import org.example.service.AdminStudentService;
import org.example.ui.LoginFrame;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

public class AdminMasterlistFrame {
    private static final Color SIDEBAR_BG = new Color(7, 43, 33);
    private static final Color SIDEBAR_ACTIVE = new Color(20, 60, 48);
    private static final Color GOLD = new Color(207, 160, 48);
    private static final Color PAGE_BG = new Color(250, 252, 253);
    private static final Color TEXT_DARK = new Color(22, 30, 45);
    private static final Color TEXT_MUTED = new Color(119, 131, 148);


    // Stat Card Backgrounds
    private static final Color STAT_GREEN_BG = new Color(236, 245, 241);
    private static final Color STAT_YELLOW_BG = new Color(253, 248, 237);
    private static final Color STAT_BLUE_BG = new Color(240, 244, 250);
    // Badge Colors
    private static final Color BADGE_ENROLLED_BG = new Color(223, 246, 235);
    private static final Color BADGE_ENROLLED_FG = new Color(24, 134, 75);
    private static final Color BADGE_UNENROLLED_BG = new Color(240, 242, 245);
    private static final Color BADGE_UNENROLLED_FG = new Color(100, 110, 120);
    private static final Color BADGE_PENDING_BG = new Color(254, 243, 199);
    private static final Color BADGE_PENDING_FG = new Color(180, 120, 0);
    private static final Color BADGE_CONFLICT_BG = new Color(254, 226, 226);
    private static final Color BADGE_CONFLICT_FG = new Color(220, 38, 38);

    private final JFrame window = new JFrame("REY SIS | Student Masterlist");
    private final String[] tableColumns = {"STUDENT ID", "FULL NAME", "PROGRAM & YEAR", "STATUS", "ACTION"};
    private final AdminStudentService studentService = new AdminStudentService();
    private List<StudentDirectoryRepository.StudentSummary> students = List.of();
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JLabel totalEnrolledValue;
    private JLabel activeRegularValue;
    private JLabel pendingRegistrationsValue;
    private JLabel footerInfo;
    private JComboBox<String> programFilter;
    private JComboBox<String> yearFilter;

    public AdminMasterlistFrame() {
        loadStudents();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1280, 800));
        window.setSize(1360, 900);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private void loadStudents() {
        try {
            students = studentService.findStudents();
        } catch (SQLException | SecurityException exception) {
            JOptionPane.showMessageDialog(window, "Unable to load student records from the database.",
                    "Student Records", JOptionPane.ERROR_MESSAGE);
            students = List.of();
        }
    }

    private void refreshStudents() {
        loadStudents();
        if (studentTableModel == null) return;
        studentTableModel.setRowCount(0);
        addStudentRows();
        refreshFilterOptions();
        updateStats();
        applyFilters("");
    }

    private void addStudentRows() {
        for (StudentDirectoryRepository.StudentSummary student : students) {
            studentTableModel.addRow(new Object[]{student.id(), student.name(),
                    student.program() + " - " + student.yearLevel(),
                    student.enrollmentStatus(), "View Profile"});
        }
    }

    private void refreshFilterOptions() {
        if (programFilter == null || yearFilter == null) return;
        String selectedProgram = String.valueOf(programFilter.getSelectedItem());
        String selectedYear = String.valueOf(yearFilter.getSelectedItem());
        programFilter.removeAllItems();
        programFilter.addItem("Program: All");
        students.stream().map(StudentDirectoryRepository.StudentSummary::program)
                .filter(value -> value != null && !value.isBlank()).distinct().sorted()
                .forEach(value -> programFilter.addItem("Program: " + value));
        yearFilter.removeAllItems();
        yearFilter.addItem("Year Level: All");
        students.stream().map(StudentDirectoryRepository.StudentSummary::yearLevel)
                .filter(value -> value != null && !value.isBlank()).distinct().sorted()
                .forEach(value -> yearFilter.addItem("Year Level: " + value));
        programFilter.setSelectedItem(selectedProgram);
        yearFilter.setSelectedItem(selectedYear);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(PAGE_BG);

        JScrollPane scrollPane = new JScrollPane(createBody());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
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
        navPanel.add(createNavButton("Student Masterlist", IconType.LIST, true));
        navPanel.add(createNavButton("Enrollment Validation", IconType.WINDOW, false));
        navPanel.add(createNavButton("Courses & Sections", IconType.LAYOUT, false));
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

        // Title Header
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        JLabel pageTitle = new JLabel("Student Masterlist");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        pageTitle.setForeground(SIDEBAR_BG);
        body.add(pageTitle, gbc);

        // Top 3 Stat Cards
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        body.add(createQuickStats(), gbc);

        // Action Toolbar (Search & Filter)
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        body.add(createFilterToolbar(), gbc);

        // Masterlist Data Table Container
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        body.add(createFunctionalTableContainer(), gbc);

        return body;
    }

    private JPanel createQuickStats() {
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);

        stats.add(createStatCard("Total Students", String.valueOf(students.size()), IconType.GRADUATION_CAP, STAT_GREEN_BG));
        stats.add(createStatCard("Enrolled Students", "0", IconType.DOCUMENT_STACK, STAT_YELLOW_BG));
        stats.add(createStatCard("Unenrolled Students", "0", IconType.HOURGLASS, STAT_BLUE_BG));

        return stats;
    }

    private JPanel createStatCard(String title, String value, IconType iconType, Color iconBg) {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                new EmptyBorder(20, 25, 20, 25)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_MUTED);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valLabel.setForeground(TEXT_DARK);

        if (title.equals("Total Students")) {
            totalEnrolledValue = valLabel;
        } else if (title.equals("Enrolled Students")) {
            activeRegularValue = valLabel;
        } else if (title.equals("Unenrolled Students")) {
            pendingRegistrationsValue = valLabel;
        }

        textPanel.add(titleLabel);
        textPanel.add(valLabel);
        card.add(textPanel, BorderLayout.CENTER);

        // Right Icon Badge Container
        RoundedPanel iconPanel = new RoundedPanel(12, iconBg);
        iconPanel.setPreferredSize(new Dimension(48, 48));
        iconPanel.setLayout(new GridBagLayout());
        iconPanel.add(new JLabel(new VectorIcon(iconType, SIDEBAR_BG)));

        card.add(iconPanel, BorderLayout.EAST);
        return card;
    }

    private JPanel createFilterToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setOpaque(false);

        // Left Controls: Search Box + Filters
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        // Search Input Box
        JPanel searchBox = new RoundedPanel(10, Color.WHITE);
        searchBox.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchBox.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 232), 1));
        searchBox.setPreferredSize(new Dimension(320, 42));

        JLabel searchIcon = new JLabel(new VectorIcon(IconType.SEARCH, TEXT_MUTED));
        JTextField searchField = new JTextField("Search by ID, Name, or Program...");
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setForeground(TEXT_MUTED);
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(270, 24));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if (searchField.getText().equals("Search by ID, Name, or Program...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_DARK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search by ID, Name, or Program...");
                    searchField.setForeground(TEXT_MUTED);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                applyFilters(searchField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent event) { filter(); }

            @Override
            public void removeUpdate(DocumentEvent event) { filter(); }

            @Override
            public void changedUpdate(DocumentEvent event) { filter(); }
        });

        searchBox.add(searchIcon);
        searchBox.add(searchField);
        left.add(searchBox);

        // Dropdowns
        programFilter = createStyledComboBox(new String[]{"Program: All"});
        yearFilter = createStyledComboBox(new String[]{"Year Level: All"});
        refreshFilterOptions();
        programFilter.addActionListener(event -> applyFilters(searchField.getText()));
        yearFilter.addActionListener(event -> applyFilters(searchField.getText()));
        left.add(programFilter);
        left.add(yearFilter);

        toolbar.add(left, BorderLayout.WEST);

        // Right Controls: Export CSV, refresh, and create a student account.
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JButton addStudent = new JButton("+ Add Student");
        addStudent.setFont(new Font("SansSerif", Font.BOLD, 12));
        addStudent.setForeground(Color.WHITE);
        addStudent.setBackground(SIDEBAR_BG);
        addStudent.setBorder(new EmptyBorder(11, 18, 11, 18));
        addStudent.setFocusPainted(false);
        addStudent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addStudent.addActionListener(event -> showAddStudentDialog());
        right.add(addStudent);

        JButton exportBtn = new JButton("Export CSV", new VectorIcon(IconType.EXPORT, TEXT_DARK));
        exportBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        exportBtn.setForeground(TEXT_DARK);
        exportBtn.setBackground(new Color(240, 244, 248));
        exportBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 218, 226), 1),
                new EmptyBorder(10, 18, 10, 18)
        ));
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(event -> exportVisibleRows());

        right.add(exportBtn);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setBackground(SIDEBAR_BG);
        refreshBtn.setBorder(new EmptyBorder(11, 22, 11, 22));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(event -> refreshStudents());
        right.add(refreshBtn);

        toolbar.add(right, BorderLayout.EAST);
        return toolbar;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setForeground(TEXT_DARK);
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(150, 42));
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 232), 1));
        return combo;
    }

    private JPanel createFunctionalTableContainer() {
        RoundedPanel container = new RoundedPanel(16, Color.WHITE);
        container.setLayout(new BorderLayout());
        container.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));

        studentTableModel = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        addStudentRows();
        studentTable = new JTable(studentTableModel);
        tableSorter = new TableRowSorter<>(studentTableModel);
        studentTable.setRowSorter(tableSorter);
        studentTable.setRowHeight(58);
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));
        studentTable.setSelectionBackground(new Color(245, 248, 250));

        JTableHeader header = studentTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 48));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                            boolean focused, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                label.setFont(new Font("SansSerif", Font.BOLD, 11));
                label.setForeground(new Color(74, 88, 101));
                label.setBackground(new Color(252, 249, 240));
                label.setBorder(new EmptyBorder(0, 20, 0, 10));
                return label;
            }
        });

        studentTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(270);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(new PaddingRenderer(Font.BOLD, TEXT_DARK, 12));
        studentTable.getColumnModel().getColumn(1).setCellRenderer(new PaddingRenderer(Font.BOLD, TEXT_DARK, 13));
        studentTable.getColumnModel().getColumn(2).setCellRenderer(new PaddingRenderer(Font.PLAIN, TEXT_MUTED, 12));
        studentTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        studentTable.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                int row = studentTable.rowAtPoint(event.getPoint());
                int column = studentTable.columnAtPoint(event.getPoint());
                if (row >= 0 && column == 4) {
                    int modelRow = studentTable.convertRowIndexToModel(row);
                    showStudentDialog(modelRow);
                }
            }
        });
        updateStats();

        container.add(studentTable.getTableHeader(), BorderLayout.NORTH);
        container.add(studentTable, BorderLayout.CENTER);
        container.add(createTableFooter(), BorderLayout.SOUTH);
        return container;
    }

    private JPanel createTableContainer() {
        RoundedPanel container = new RoundedPanel(16, Color.WHITE);
        container.setLayout(new BorderLayout());
        container.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));

        String[] columns = {"STUDENT ID", "FULL NAME", "PROGRAM & YEAR", "STATUS", "ACTION"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(58);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(245, 248, 250));

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 48));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                l.setForeground(new Color(110, 115, 125));
                l.setBackground(new Color(252, 249, 240));
                l.setBorder(new EmptyBorder(0, 20, 0, 10));
                l.setHorizontalAlignment(SwingConstants.LEFT);
                return l;
            }
        });

        // Custom Cell Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setFont(new Font("SansSerif", Font.BOLD, 12));
                l.setForeground(TEXT_DARK);
                l.setBorder(new EmptyBorder(0, 20, 0, 10));
                return l;
            }
        });

        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setFont(new Font("SansSerif", Font.BOLD, 13));
                l.setForeground(TEXT_DARK);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                return l;
            }
        });

        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setFont(new Font("SansSerif", Font.PLAIN, 12));
                l.setForeground(TEXT_MUTED);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                return l;
            }
        });

        // Status Badge Column Renderer
        table.getColumnModel().getColumn(3).setCellRenderer((t, val, isSelected, hasFocus, row, col) -> {
            String status = (String) val;
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 14));
            panel.setOpaque(false);

            JLabel badge = new JLabel(status, SwingConstants.CENTER);
            badge.setFont(new Font("SansSerif", Font.BOLD, 11));

            Color bg = BADGE_ENROLLED_BG;
            Color fg = BADGE_ENROLLED_FG;

            if ("Unenrolled".equalsIgnoreCase(status)) {
                bg = BADGE_UNENROLLED_BG;
                fg = BADGE_UNENROLLED_FG;
            } else if ("Pending".equalsIgnoreCase(status)) {
                bg = BADGE_PENDING_BG;
                fg = BADGE_PENDING_FG;
            } else if ("Conflict".equalsIgnoreCase(status)) {
                bg = BADGE_CONFLICT_BG;
                fg = BADGE_CONFLICT_FG;
            }

            badge.setForeground(fg);
            RoundedPanel badgeWrapper = new RoundedPanel(14, bg);
            badgeWrapper.setPreferredSize(new Dimension(95, 28));
            badgeWrapper.setLayout(new BorderLayout());
            badgeWrapper.add(badge, BorderLayout.CENTER);

            panel.add(badgeWrapper);
            return panel;
        });

        // Action Buttons Column Renderer
        table.getColumnModel().getColumn(4).setCellRenderer((t, val, isSelected, hasFocus, row, col) -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 18));
            panel.setOpaque(false);

            JLabel viewProfile = new JLabel("View Profile");
            viewProfile.setFont(new Font("SansSerif", Font.BOLD, 12));
            viewProfile.setForeground(new Color(40, 100, 230));
            viewProfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel edit = new JLabel("Edit");
            edit.setFont(new Font("SansSerif", Font.PLAIN, 12));
            edit.setForeground(TEXT_MUTED);
            edit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            panel.add(viewProfile);
            panel.add(edit);
            return panel;
        });

        container.add(table.getTableHeader(), BorderLayout.NORTH);
        container.add(table, BorderLayout.CENTER);
        container.add(createTableFooter(), BorderLayout.SOUTH);

        return container;
    }

    private JPanel createTableFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(16, 20, 16, 20));

        footerInfo = new JLabel("Showing 1 to 0 of 0 entries");
        JLabel info = footerInfo;
        info.setFont(new Font("SansSerif", Font.PLAIN, 12));
        info.setForeground(TEXT_MUTED);
        footer.add(info, BorderLayout.WEST);

        // Pagination Controls
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pagination.setOpaque(false);

        pagination.add(createPageBtn("‹", false, false));
        pagination.add(createPageBtn("1", true, true));
        pagination.add(createPageBtn("2", false, true));
        pagination.add(createPageBtn("3", false, true));
        pagination.add(createPageBtn("›", false, false));

        footer.add(pagination, BorderLayout.EAST);
        return footer;
    }

    private JButton createPageBtn(String text, boolean active, boolean number) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 12));
        btn.setForeground(active ? Color.WHITE : TEXT_DARK);
        btn.setBackground(active ? SIDEBAR_BG : new Color(242, 245, 248));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void applyFilters(String searchText) {
        if (tableSorter == null) {
            return;
        }
        String search = searchText == null ? "" : searchText.trim();
        if (search.equals("Search by ID, Name, or Program...")) {
            search = "";
        }
        String program = programFilter == null ? "All" : String.valueOf(programFilter.getSelectedItem());
        String year = yearFilter == null ? "All" : String.valueOf(yearFilter.getSelectedItem());
        final String searchValue = search.toLowerCase();
        final String programValue = program.replace("Program: ", "").toLowerCase();
        final String yearValue = year.replace("Year Level: ", "").toLowerCase();
        tableSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String id = String.valueOf(entry.getValue(0)).toLowerCase();
                String name = String.valueOf(entry.getValue(1)).toLowerCase();
                String programAndYear = String.valueOf(entry.getValue(2)).toLowerCase();
                String status = String.valueOf(entry.getValue(3)).toLowerCase();
                boolean matchesSearch = searchValue.isEmpty()
                        || id.contains(searchValue) || name.contains(searchValue)
                        || programAndYear.contains(searchValue);
                boolean matchesProgram = programValue.equals("all") || programAndYear.startsWith(programValue);
                boolean matchesYear = yearValue.equals("all") || programAndYear.contains(yearValue);
                return matchesSearch && matchesProgram && matchesYear;
            }
        });
        updateFooter();
    }

    private void updateStats() {
        int enrolled = 0;
        for (StudentDirectoryRepository.StudentSummary student : students) {
            if ("Enrolled".equalsIgnoreCase(student.enrollmentStatus())) enrolled++;
        }
        if (totalEnrolledValue != null) totalEnrolledValue.setText(String.valueOf(students.size()));
        if (activeRegularValue != null) activeRegularValue.setText(String.valueOf(enrolled));
        if (pendingRegistrationsValue != null) pendingRegistrationsValue.setText(String.valueOf(students.size() - enrolled));
        updateFooter();
    }

    private void updateFooter() {
        if (footerInfo != null && studentTable != null) {
            footerInfo.setText("Showing 1 to " + studentTable.getRowCount() + " of " + studentTable.getRowCount() + " entries");
        }
    }

    private void showStudentDialog(int modelRow) {
        if (studentTableModel == null || modelRow < 0 || modelRow >= studentTableModel.getRowCount()) return;
        String details = "<html><b>Student ID:</b> " + studentTableModel.getValueAt(modelRow, 0)
                + "<br><b>Full Name:</b> " + studentTableModel.getValueAt(modelRow, 1)
                + "<br><b>Program &amp; Year:</b> " + studentTableModel.getValueAt(modelRow, 2)
                + "<br><b>Enrollment Status:</b> " + studentTableModel.getValueAt(modelRow, 3) + "</html>";
        JOptionPane.showMessageDialog(window, details, "Student Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddStudentDialog() {
        JTextField firstName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField program = new JTextField("Bachelor of Science in Information Technology");
        JTextField yearLevel = new JTextField("1st Year");
        JTextField section = new JTextField("A");
        JTextField email = new JTextField();
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.add(new JLabel("First name:")); form.add(firstName);
        form.add(new JLabel("Last name:")); form.add(lastName);
        form.add(new JLabel("Program:")); form.add(program);
        form.add(new JLabel("Year level:")); form.add(yearLevel);
        form.add(new JLabel("Section:")); form.add(section);
        form.add(new JLabel("Email (optional):")); form.add(email);

        int result = JOptionPane.showConfirmDialog(window, form, "Add Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        if (firstName.getText().isBlank() || lastName.getText().isBlank()
                || program.getText().isBlank() || yearLevel.getText().isBlank()) {
            JOptionPane.showMessageDialog(window, "First name, last name, program, and year level are required.",
                    "Student Details Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            StudentDirectoryRepository.CreatedStudent created = studentService.createStudent(
                    firstName.getText().trim(), lastName.getText().trim(), program.getText().trim(),
                    yearLevel.getText().trim(), section.getText().trim(), email.getText().trim());
            refreshStudents();
            JOptionPane.showMessageDialog(window,
                    "Student created successfully.\nStudent ID / login: " + created.studentId()
                            + "\nInitial password: 123",
                    "Student Created", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | SecurityException exception) {
            JOptionPane.showMessageDialog(window, "Unable to create the student: " + exception.getMessage(),
                    "Student Creation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportVisibleRows() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Student Masterlist CSV");
        chooser.setSelectedFile(new File("student-masterlist.csv"));
        if (chooser.showSaveDialog(window) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName() + ".csv");
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("STUDENT ID,FULL NAME,PROGRAM & YEAR,STATUS\n");
            for (int viewRow = 0; viewRow < studentTable.getRowCount(); viewRow++) {
                int modelRow = studentTable.convertRowIndexToModel(viewRow);
                writer.write(csvValue(studentTableModel.getValueAt(modelRow, 0)) + ","
                        + csvValue(studentTableModel.getValueAt(modelRow, 1)) + ","
                        + csvValue(studentTableModel.getValueAt(modelRow, 2)) + ","
                        + csvValue(studentTableModel.getValueAt(modelRow, 3)) + "\n");
            }
            JOptionPane.showMessageDialog(window, "Student masterlist exported successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(window, "Unable to export CSV: " + exception.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csvValue(Object value) {
        String text = String.valueOf(value).replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    private static class PaddingRenderer extends DefaultTableCellRenderer {
        private final int style;
        private final Color color;
        private final int size;

        PaddingRenderer(int style, Color color, int size) {
            this.style = style;
            this.color = color;
            this.size = size;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focused, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            label.setFont(new Font("SansSerif", style, size));
            label.setForeground(color);
            label.setBorder(new EmptyBorder(0, 12, 0, 10));
            return label;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focused, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 11));
            label.setBorder(new EmptyBorder(0, 8, 0, 8));
            return label;
        }
    }

    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focused, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            label.setText("View Profile");
            label.setForeground(new Color(40, 100, 230));
            label.setFont(new Font("SansSerif", Font.BOLD, 11));
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            return label;
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
            try (InputStream is = AdminMasterlistFrame.class.getResourceAsStream("/images/images/Frame 6 (1).png")) {
                return is == null ? null : ImageIO.read(is);
            } catch (Exception e) { return null; }
        }
    }

    public enum IconType {
        HOME, LIST, WINDOW, LAYOUT, USER_OUTLINE, LOGOUT, GRADUATION_CAP, DOCUMENT_STACK, HOURGLASS, SEARCH, EXPORT
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
                case GRADUATION_CAP -> {
                    g.drawPolygon(new int[]{x+2, x+10, x+18, x+10}, new int[]{y+8, y+3, y+8, y+13}, 4);
                    g.drawArc(x+5, y+9, 10, 6, 180, 180);
                    g.drawLine(x+18, y+8, x+18, y+15);
                }
                case DOCUMENT_STACK -> {
                    g.drawRect(x+4, y+2, 12, 14);
                    g.drawRect(x+2, y+5, 12, 14);
                }
                case HOURGLASS -> {
                    g.drawPolygon(new int[]{x+4, x+16, x+4, x+16}, new int[]{y+3, y+3, y+17, y+17}, 4);
                    g.drawLine(x+3, y+3, x+17, y+3);
                    g.drawLine(x+3, y+17, x+17, y+17);
                }
                case SEARCH -> {
                    g.drawOval(x+2, y+2, 11, 11);
                    g.drawLine(x+10, y+10, x+17, y+17);
                }
                case EXPORT -> {
                    g.drawLine(x+10, y+2, x+10, y+12);
                    g.drawLine(x+6, y+6, x+10, y+2);
                    g.drawLine(x+14, y+6, x+10, y+2);
                    g.drawArc(x+3, y+10, 14, 8, 180, 180);
                }
            }
            g.dispose();
        }
    }
}
