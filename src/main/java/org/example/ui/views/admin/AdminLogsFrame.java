package org.example.ui.views.admin;

import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AdminLogsFrame extends JFrame {

    private final Color BRAND_GREEN = new Color(11, 59, 36);
    private final Color BRAND_YELLOW = new Color(223, 179, 61);
    private final Color SIDEBAR_BG = new Color(7, 43, 33);
    private final Color SIDEBAR_ACTIVE = new Color(20, 60, 48);
    private final Color BG_LIGHT = new Color(248, 250, 252);
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color TEXT_MUTED = new Color(100, 116, 139);
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color GOLD = new Color(207, 160, 48);
    private final List<AuditLog> auditLogs = new ArrayList<>();
    private DefaultTableModel auditModel;
    private JTable auditTable;
    private TableRowSorter<DefaultTableModel> auditSorter;
    private JComboBox<String> dateFilter;
    private JComboBox<String> moduleFilter;
    private JComboBox<String> statusFilter;
    private JLabel entriesLabel;
    private JLabel pageLabel;
    private int currentPage;
    private int filteredAuditCount;
    private static final int PAGE_SIZE = 10;

    public AdminLogsFrame() {
        initializeAuditLogs();
        setTitle("REY SIS - System Audit Logs");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createDynamicMainContent(), BorderLayout.CENTER);
    }

    private void initializeAuditLogs() {
        auditLogs.add(new AuditLog("Oct 5, 2026 09:35 AM", "admin.super", "192.168.1.105", "Updated Section Capacity", "Section OOP202-A", "Success", "Courses"));
        auditLogs.add(new AuditLog("Oct 5, 2026 08:12 AM", "unknown_user", "112.204.15.8", "Failed Login Attempt", "Authentication Auth/Login", "Failed", "Auth"));
        auditLogs.add(new AuditLog("Oct 4, 2026 04:45 PM", "registrar.main", "192.168.1.55", "Deleted Old Course Data", "Course ACC100 (Archived)", "Warning", "Courses"));
        auditLogs.add(new AuditLog("Oct 4, 2026 02:30 PM", "faculty.smith", "192.168.2.14", "Exported Grade Sheet", "Section DBMS101-C", "Success", "Student"));
        auditLogs.add(new AuditLog("Oct 3, 2026 11:20 AM", "admin.super", "192.168.1.105", "Approved Enrollment", "Student 2025-0013", "Success", "Student"));
        auditLogs.add(new AuditLog("Oct 3, 2026 10:05 AM", "registrar.main", "192.168.1.55", "Updated Student Profile", "Student 2025-0011", "Success", "Student"));
        auditLogs.add(new AuditLog("Oct 2, 2026 03:15 PM", "unknown_user", "112.204.15.8", "Failed Login Attempt", "Authentication Auth/Login", "Failed", "Auth"));
        auditLogs.add(new AuditLog("Oct 2, 2026 01:40 PM", "admin.super", "192.168.1.105", "Added New Section", "Section OS301-A", "Success", "Courses"));
        auditLogs.add(new AuditLog("Oct 1, 2026 04:10 PM", "faculty.smith", "192.168.2.14", "Viewed Student Records", "Masterlist", "Warning", "Student"));
        auditLogs.add(new AuditLog("Oct 1, 2026 09:00 AM", "registrar.main", "192.168.1.55", "Signed In", "Admin Portal", "Success", "Auth"));
        auditLogs.add(new AuditLog("Sep 30, 2026 02:20 PM", "admin.super", "192.168.1.105", "Edited Course Details", "Course DBMS101", "Success", "Courses"));
        auditLogs.add(new AuditLog("Sep 29, 2026 08:45 AM", "unknown_user", "112.204.15.8", "Failed Login Attempt", "Authentication Auth/Login", "Failed", "Auth"));
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
        navPanel.add(createNavButton("Courses & Sections", IconType.LAYOUT, false));
        navPanel.add(createNavButton("System Audit", IconType.USER_OUTLINE, true));
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

    private JPanel createDynamicMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        JLabel title = new JLabel("System Audit Logs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_DARK);
        top.add(title, BorderLayout.WEST);
        JButton export = new JButton("Export CSV");
        export.setFont(new Font("Segoe UI", Font.BOLD, 13));
        export.setForeground(TEXT_DARK);
        export.setBackground(Color.WHITE);
        export.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(8, 20, 8, 20)));
        export.setFocusPainted(false);
        export.addActionListener(event -> exportAuditLogs());
        top.add(export, BorderLayout.EAST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filters.setBackground(Color.WHITE);
        filters.setBorder(new EmptyBorder(20, 0, 20, 0));
        dateFilter = createCombo(new String[]{"All Dates", "Oct 1 - Oct 5, 2026", "Sep 29 - Sep 30, 2026"}, 180);
        moduleFilter = createCombo(new String[]{"All Modules", "Auth", "Courses", "Student"}, 150);
        statusFilter = createCombo(new String[]{"All Statuses", "Success", "Failed", "Warning"}, 130);
        JButton filter = new JButton("Filter");
        filter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filter.setForeground(Color.WHITE);
        filter.setBackground(BRAND_GREEN);
        filter.setBorder(new EmptyBorder(9, 25, 9, 25));
        filter.setFocusPainted(false);
        filter.addActionListener(event -> applyAuditFilters());
        filters.add(dateFilter); filters.add(moduleFilter); filters.add(statusFilter); filters.add(filter);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(top, BorderLayout.NORTH);
        topSection.add(filters, BorderLayout.SOUTH);
        main.add(topSection, BorderLayout.NORTH);

        String[] columns = {"TIMESTAMP", "USER / IP", "ACTION DETAILS", "TARGET", "MODULE", "STATUS"};
        auditModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (AuditLog log : auditLogs) {
            auditModel.addRow(new Object[]{log.timestamp, log.user + "\n" + log.ip, log.action, log.target, log.module, log.status});
        }
        auditTable = new JTable(auditModel);
        auditSorter = new TableRowSorter<>(auditModel);
        auditTable.setRowSorter(auditSorter);
        auditTable.setRowHeight(44);
        auditTable.setShowGrid(false);
        auditTable.setIntercellSpacing(new Dimension(0, 1));
        auditTable.setFillsViewportHeight(true);
        auditTable.getTableHeader().setPreferredSize(new Dimension(0, 42));
        auditTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        auditTable.getTableHeader().setForeground(TEXT_MUTED);
        auditTable.getTableHeader().setBackground(BG_LIGHT);
        int[] widths = {170, 150, 190, 190, 90, 90};
        for (int index = 0; index < widths.length; index++) auditTable.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        JScrollPane tableScroll = new JScrollPane(auditTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        main.add(tableScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(15, 0, 0, 0));
        entriesLabel = new JLabel();
        entriesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        entriesLabel.setForeground(TEXT_MUTED);
        footer.add(entriesLabel, BorderLayout.WEST);
        JPanel pages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pages.setBackground(Color.WHITE);
        JButton previous = createPageButton("<", false);
        JButton next = createPageButton(">", false);
        pageLabel = new JLabel("1");
        pageLabel.setBorder(new EmptyBorder(8, 10, 8, 10));
        previous.addActionListener(event -> changePage(-1));
        next.addActionListener(event -> changePage(1));
        pages.add(previous); pages.add(pageLabel); pages.add(next);
        footer.add(pages, BorderLayout.EAST);
        main.add(footer, BorderLayout.SOUTH);
        applyAuditFilters();
        return main;
    }

    private JComboBox<String> createCombo(String[] values, int width) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setForeground(TEXT_MUTED);
        combo.setBackground(BG_LIGHT);
        combo.setPreferredSize(new Dimension(width, 35));
        return combo;
    }

    private void applyAuditFilters() {
        currentPage = 0;
        auditSorter.setRowFilter(createAuditFilter());
        updateAuditFooter();
    }

    private RowFilter<DefaultTableModel, Integer> createAuditFilter() {
        String date = String.valueOf(dateFilter.getSelectedItem());
        String module = String.valueOf(moduleFilter.getSelectedItem());
        String status = String.valueOf(statusFilter.getSelectedItem());
        List<Integer> matchingRows = new ArrayList<>();
        for (int index = 0; index < auditLogs.size(); index++) {
            AuditLog log = auditLogs.get(index);
            boolean dateMatch = date.equals("All Dates") || (date.startsWith("Oct") && log.timestamp.startsWith("Oct"))
                    || (date.startsWith("Sep 29") && log.timestamp.startsWith("Sep 29"))
                    || (date.startsWith("Sep 29") && log.timestamp.startsWith("Sep 30"));
            boolean moduleMatch = module.equals("All Modules") || log.module.equals(module);
            boolean statusMatch = status.equals("All Statuses") || log.status.equals(status);
            if (dateMatch && moduleMatch && statusMatch) matchingRows.add(index);
        }
        filteredAuditCount = matchingRows.size();
        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, matchingRows.size());
        Set<Integer> visibleRows = new HashSet<>(matchingRows.subList(Math.min(from, to), to));
        return new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                return visibleRows.contains(entry.getIdentifier());
            }
        };
    }

    private void changePage(int direction) {
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredAuditCount / PAGE_SIZE));
        currentPage = Math.max(0, Math.min(currentPage + direction, totalPages - 1));
        auditSorter.setRowFilter(createAuditFilter());
        updateAuditFooter();
    }

    private void updateAuditFooter() {
        if (auditTable == null) return;
        int total = filteredAuditCount;
        int start = total == 0 ? 0 : currentPage * PAGE_SIZE + 1;
        int end = Math.min(total, (currentPage + 1) * PAGE_SIZE);
        entriesLabel.setText("Showing " + start + " to " + end + " of " + total + " entries");
        pageLabel.setText(String.valueOf(currentPage + 1));
    }

    private void exportAuditLogs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Audit Logs CSV");
        chooser.setSelectedFile(new File("system-audit-logs.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) file = new File(file.getParentFile(), file.getName() + ".csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Timestamp,User,IP,Action,Target,Status\n");
            for (int viewRow = 0; viewRow < auditTable.getRowCount(); viewRow++) {
                AuditLog log = auditLogs.get(auditTable.convertRowIndexToModel(viewRow));
                writer.write(csv(log.timestamp) + "," + csv(log.user) + "," + csv(log.ip) + ","
                        + csv(log.action) + "," + csv(log.target) + "," + csv(log.status) + "\n");
            }
            JOptionPane.showMessageDialog(this, "Audit logs exported successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Unable to export audit logs: " + exception.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }

    private static class AuditLog {
        private final String timestamp, user, ip, action, target, status, module;
        AuditLog(String timestamp, String user, String ip, String action, String target, String status, String module) {
            this.timestamp = timestamp; this.user = user; this.ip = ip; this.action = action;
            this.target = target; this.status = status; this.module = module;
        }
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(40, 40, 40, 40));

        // --- Top Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("System Audit Logs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_DARK);

        RoundedPanel exportBtn = new RoundedPanel(8, Color.WHITE, BORDER_COLOR);
        exportBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel exportLbl = new JLabel("Export CSV");
        exportLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportLbl.setForeground(TEXT_DARK);
        exportBtn.add(exportLbl);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(exportBtn, BorderLayout.EAST);

        // --- Filters Area ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        filterPanel.add(createFilterDropdown("Oct 1 - Oct 5, 2026", 180));
        filterPanel.add(createFilterDropdown("All Modules", 150));
        filterPanel.add(createFilterDropdown("All Statuses", 130));

        RoundedPanel filterBtn = new RoundedPanel(8, BRAND_GREEN, BRAND_GREEN);
        filterBtn.setBorder(new EmptyBorder(8, 25, 8, 25));
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel filterLbl = new JLabel("Filter");
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterLbl.setForeground(Color.WHITE);
        filterBtn.add(filterLbl);
        filterPanel.add(filterBtn);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(filterPanel, BorderLayout.SOUTH);

        // --- Table Area ---
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);

        // Table Header
        JPanel tableHeader = new JPanel(new GridLayout(1, 4, 10, 0));
        tableHeader.setBackground(BG_LIGHT);
        tableHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        String[] headers = {"Timestamp", "User / IP", "Action Details", "Status"};
        for (String h : headers) {
            JLabel hLbl = new JLabel(h);
            hLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            hLbl.setForeground(TEXT_MUTED);
            tableHeader.add(hLbl);
        }

        // Table Body
        JPanel tableBody = new JPanel();
        tableBody.setLayout(new BoxLayout(tableBody, BoxLayout.Y_AXIS));
        tableBody.setBackground(Color.WHITE);

        tableBody.add(createLogRow("Oct 5, 2026", "09:35 AM", "admin.super", "192.168.1.105", "Updated Section Capacity", "Target: Section OOP202-A", "Success"));
        tableBody.add(createLogRow("Oct 5, 2026", "08:12 AM", "unknown_user", "112.204.15.8", "Failed Login Attempt", "Target: Authentication Auth/Login", "Failed"));
        tableBody.add(createLogRow("Oct 4, 2026", "16:45 PM", "registrar.main", "192.168.1.55", "Deleted Old Course Data", "Target: Course ACC100 (Archived)", "Warning"));
        tableBody.add(createLogRow("Oct 4, 2026", "14:30 PM", "faculty.smith", "192.168.2.14", "Exported Grade Sheet", "Target: Section DBMS101-C", "Success"));

        tableContainer.add(tableHeader, BorderLayout.NORTH);
        tableContainer.add(tableBody, BorderLayout.CENTER);

        // --- Pagination Area ---
        JPanel paginationPanel = new JPanel(new BorderLayout());
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel entriesLbl = new JLabel("Showing 1 to 4 of 128 entries");
        entriesLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        entriesLbl.setForeground(TEXT_MUTED);

        JPanel pageBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pageBtns.setBackground(Color.WHITE);
        pageBtns.add(createPageButton("<", false));
        pageBtns.add(createPageButton("1", true));
        pageBtns.add(createPageButton(">", false));

        paginationPanel.add(entriesLbl, BorderLayout.WEST);
        paginationPanel.add(pageBtns, BorderLayout.EAST);

        // Put it all together
        main.add(topSection, BorderLayout.NORTH);
        main.add(tableContainer, BorderLayout.CENTER);
        main.add(paginationPanel, BorderLayout.SOUTH);

        return main;
    }

    private JPanel createFilterDropdown(String text, int width) {
        RoundedPanel pnl = new RoundedPanel(8, BG_LIGHT, BORDER_COLOR);
        pnl.setPreferredSize(new Dimension(width, 35));
        pnl.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_MUTED);
        pnl.add(lbl);
        return pnl;
    }

    private JPanel createLogRow(String date, String time, String user, String ip, String action, String target, String status) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(15, 20, 15, 20)
        ));

        // Col 1: Timestamp
        row.add(createTwoLineCell(date, time, true));

        // Col 2: User / IP
        row.add(createTwoLineCell(user, ip, true));

        // Col 3: Action Details
        row.add(createTwoLineCell(action, target, false));

        // Col 4: Status Badge
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        statusContainer.setBackground(Color.WHITE);

        Color bgCol = Color.WHITE, fgCol = Color.BLACK;
        if (status.equals("Success")) {
            bgCol = new Color(209, 250, 229); // Light green
            fgCol = new Color(6, 95, 70);    // Dark green
        } else if (status.equals("Failed")) {
            bgCol = new Color(254, 226, 226); // Light red
            fgCol = new Color(153, 27, 27);   // Dark red
        } else if (status.equals("Warning")) {
            bgCol = new Color(254, 243, 199); // Light yellow
            fgCol = new Color(146, 64, 14);   // Dark yellow/orange
        }

        RoundedPanel badge = new RoundedPanel(15, bgCol, new Color(0,0,0,0));
        badge.setBorder(new EmptyBorder(4, 15, 4, 15));
        JLabel statusLbl = new JLabel(status);
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLbl.setForeground(fgCol);
        badge.add(statusLbl);

        statusContainer.add(badge);
        row.add(statusContainer);

        return row;
    }

    private JPanel createTwoLineCell(String line1, String line2, boolean firstBold) {
        JPanel pnl = new JPanel(new GridLayout(2, 1, 0, 4));
        pnl.setBackground(Color.WHITE);

        JLabel l1 = new JLabel(line1);
        l1.setFont(new Font("Segoe UI", firstBold ? Font.BOLD : Font.PLAIN, 13));
        l1.setForeground(TEXT_DARK);

        JLabel l2 = new JLabel(line2);
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l2.setForeground(TEXT_MUTED);

        pnl.add(l1);
        pnl.add(l2);
        return pnl;
    }

    private JButton createPageButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(30, 30));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(active ? Color.WHITE : TEXT_MUTED);
        button.setBackground(active ? BRAND_GREEN : BG_LIGHT);
        button.setBorder(BorderFactory.createLineBorder(active ? BRAND_GREEN : BORDER_COLOR));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // --- Custom UI Component ---
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
            new AdminLogsFrame().setVisible(true);
        });
    }

    public enum IconType {
        HOME, LIST, WINDOW, LAYOUT, USER_OUTLINE, LOGOUT
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
            }
            g.dispose();
        }
    }
}
