package org.example.ui.views.admin;

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
import java.io.InputStream;

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

    public AdminMasterlistFrame() {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1280, 800));
        window.setSize(1360, 900);
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
        body.add(createTableContainer(), gbc);

        return body;
    }

    private JPanel createQuickStats() {
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);

        stats.add(createStatCard("Total Enrolled Students", "3,482", IconType.GRADUATION_CAP, STAT_GREEN_BG));
        stats.add(createStatCard("Active Regular Students", "3,120", IconType.DOCUMENT_STACK, STAT_YELLOW_BG));
        stats.add(createStatCard("Pending Registrations", "362", IconType.HOURGLASS, STAT_BLUE_BG));

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

        searchBox.add(searchIcon);
        searchBox.add(searchField);
        left.add(searchBox);

        // Dropdowns
        left.add(createStyledComboBox(new String[]{"Program: All", "BS Information Tech", "BS Civil Engineering", "BS Accountancy", "BS Psychology", "BS Computer Science"}));
        left.add(createStyledComboBox(new String[]{"Year Level: All", "1st Year", "2nd Year", "3rd Year", "4th Year"}));

        toolbar.add(left, BorderLayout.WEST);

        // Right Controls: Export CSV & Add Student
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

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

        JButton addStudentBtn = new JButton("+ Add Student");
        addStudentBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addStudentBtn.setForeground(Color.WHITE);
        addStudentBtn.setBackground(SIDEBAR_BG);
        addStudentBtn.setBorder(new EmptyBorder(11, 22, 11, 22));
        addStudentBtn.setFocusPainted(false);
        addStudentBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        right.add(exportBtn);
        right.add(addStudentBtn);

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

    private JPanel createTableContainer() {
        RoundedPanel container = new RoundedPanel(16, Color.WHITE);
        container.setLayout(new BorderLayout());
        container.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 1));

        String[] columns = {"STUDENT ID", "FULL NAME", "PROGRAM & YEAR", "STATUS", "ACTION"};
        Object[][] data = {
                {"2025-0011", "Justine Rivera", "BS Information Tech - 3rd Year", "Enrolled", "View Profile | Edit"},
                {"2025-0012", "Mark Mendoza", "BS Civil Engineering - 2nd Year", "Unenrolled", "View Profile | Edit"},
                {"2026-0145", "Sophia Lauren", "BS Accountancy - 1st Year", "Pending", "View Profile | Edit"},
                {"2025-0013", "Prince Cariaga", "BS Information Tech - 3rd Year", "Conflict", "View Profile | Edit"},
                {"2024-0089", "Anna Delos Reyes", "BS Psychology - 4th Year", "Enrolled", "View Profile | Edit"},
                {"2025-0210", "Kevin Tan", "BS Computer Science - 2nd Year", "Enrolled", "View Profile | Edit"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
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

        JLabel info = new JLabel("Showing 1 to 6 of 3,482 entries");
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