package org.example.ui.views.admin;

import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

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

    public AdminValidationFrame() {
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
        JLabel pageTitle = new JLabel("Enrollment Validation Queue");
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
        columnsPanel.add(createPendingApprovalList(), colGbc);

        // Right Column (Inspection Panel)
        colGbc.gridx = 1;
        colGbc.weightx = 0.4;
        colGbc.insets = new Insets(0, 15, 0, 0);
        columnsPanel.add(createInspectionPanel(), colGbc);

        body.add(columnsPanel, gbc);

        return body;
    }

    private JPanel createStatsRow() {
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(0, 100));

        stats.add(createStatCard("Pending Reviews", "124", ORANGE_TEXT));
        stats.add(createStatCard("Detected Conflicts", "45", RED_TEXT));
        stats.add(createStatCard("Cleared Today", "890", GREEN_TEXT));

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

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valLabel);

        return card;
    }

    private JPanel createPendingApprovalList() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JLabel title = new JLabel("Pending Approval List");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        container.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        list.add(createStudentCard("Prince Cariaga", "ID: 2025-0013 • BS Information Tech (3rd Year)",
                "⚠ Missing Pre-requisite: COMSCI 3110", 0, true));
        list.add(Box.createVerticalStrut(15));
        list.add(createStudentCard("Anna Delos Reyes", "ID: 2024-0089 • BS Psychology (4th Year)",
                "⚠ Units Overload Request (21u)", 1, false));
        list.add(Box.createVerticalStrut(15));
        list.add(createStudentCard("Mark Mendoza", "ID: 2025-0012 • BS Civil Engineering (2nd Year)",
                "✔ All Pre-requisites Clear", 2, false));

        // Push items to top
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
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        card.setPreferredSize(new Dimension(0, 125));

        // Left Side Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_DARK);

        JLabel detailLabel = new JLabel(details);
        detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detailLabel.setForeground(TEXT_MUTED);

        JPanel badge = createBadge(badgeText, badgeType);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(detailLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(badge);

        // Right Side Button
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);

        JButton inspectBtn = new JButton("Inspect Load");
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

    private JPanel createInspectionPanel() {
        RoundedPanel panel = new RoundedPanel(16, Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(INACTIVE_CARD_BORDER, 1));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel headerLabel = new JLabel("Student Load Inspection");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerLabel.setForeground(Color.WHITE);
        header.add(headerLabel, BorderLayout.CENTER);

        // Fix top corners being rounded on the container but not the header
        RoundedTopPanel roundedHeader = new RoundedTopPanel(16, SIDEBAR_BG);
        roundedHeader.setLayout(new BorderLayout());
        roundedHeader.add(header);
        panel.add(roundedHeader, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Student Info
        JLabel nameLabel = new JLabel("Prince Cariaga");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_DARK);

        JLabel idLabel = new JLabel("2025-0013 | BSIT - 3rd Year");
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        idLabel.setForeground(TEXT_MUTED);

        content.add(nameLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(idLabel);
        content.add(Box.createVerticalStrut(15));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        content.add(sep);
        content.add(Box.createVerticalStrut(15));

        // Courses Title
        JLabel coursesTitle = new JLabel("Requested Courses (18 Units)");
        coursesTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        coursesTitle.setForeground(SIDEBAR_BG);
        content.add(coursesTitle);
        content.add(Box.createVerticalStrut(15));

        // Course List
        content.add(createCourseCard("COMSCI 2110 - OOP", "TTh 09:00 - 11:00 AM | 3 Units", true, null));
        content.add(Box.createVerticalStrut(10));
        content.add(createCourseCard("COMSCI 3110 - Algorithms", "MWF 01:00 - 02:00 PM | 3 Units", false, "Failed Pre-req: COMSCI 2100"));
        content.add(Box.createVerticalStrut(10));
        content.add(createCourseCard("MATH 1013 - Discrete Math", "TTh 11:30 - 01:00 PM | 3 Units", true, null));
        content.add(Box.createVerticalStrut(20));

        // Summary Box
        RoundedPanel summaryBox = new RoundedPanel(8, new Color(249, 246, 238)); // Beige tint
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.Y_AXIS));
        summaryBox.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel totalUnits = new JLabel("Total Units: 18.0");
        totalUnits.setFont(new Font("SansSerif", Font.BOLD, 13));
        totalUnits.setForeground(TEXT_DARK);

        JLabel assessment = new JLabel("Assessment: ₱24,500.00");
        assessment.setFont(new Font("SansSerif", Font.BOLD, 13));
        assessment.setForeground(TEXT_DARK);

        summaryBox.add(totalUnits);
        summaryBox.add(Box.createVerticalStrut(10));
        summaryBox.add(assessment);

        content.add(summaryBox);
        content.add(Box.createVerticalGlue());

        // Buttons
        JButton approveBtn = new JButton("Approve Enrollment");
        approveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setBackground(new Color(25, 160, 80)); // Brighter green for approval
        approveBtn.setBorder(new EmptyBorder(12, 0, 12, 0));
        approveBtn.setFocusPainted(false);
        approveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        approveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton rejectBtn = new JButton("Reject & Send Feedback");
        rejectBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        rejectBtn.setForeground(RED_TEXT);
        rejectBtn.setBackground(Color.WHITE);
        rejectBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(250, 180, 180), 1),
                new EmptyBorder(11, 0, 11, 0)
        ));
        rejectBtn.setFocusPainted(false);
        rejectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rejectBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        content.add(Box.createVerticalStrut(20));
        content.add(approveBtn);
        content.add(Box.createVerticalStrut(10));
        content.add(rejectBtn);

        panel.add(content, BorderLayout.CENTER);
        return panel;
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