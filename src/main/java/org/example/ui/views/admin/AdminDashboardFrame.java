package org.example.ui.views.admin;

import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class AdminDashboardFrame {
    private static final Color SIDEBAR_BG = new Color(7, 43, 33);
    private static final Color SIDEBAR_ACTIVE = new Color(20, 60, 48);
    private static final Color GOLD = new Color(207, 160, 48);
    private static final Color PAGE_BG = new Color(250, 252, 253);
    private static final Color TEXT_DARK = new Color(22, 30, 45);
    private static final Color TEXT_MUTED = new Color(119, 131, 148);

    // Quick Stats Colors
    private static final Color STAT_GREEN_BG = new Color(236, 245, 241);
    private static final Color STAT_YELLOW_BG = new Color(253, 248, 237);
    private final JFrame window = new JFrame("REY SIS | Admin Dashboard");
    private final DefaultListModel<String> taskModel = new DefaultListModel<>();
    private String adminName = "Dr. Maria Santos";
    private String adminRole = "Chief Registrar";
    private JLabel adminHeaderName;
    private JLabel adminHeaderRole;

    public AdminDashboardFrame() {
        taskModel.addElement("Clear pending IT validations");
        taskModel.addElement("Merge overlapping BSCE sections");
        taskModel.addElement("Export weekly enrollment audit report");
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
        main.add(createHeader(), BorderLayout.NORTH);

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
        navPanel.add(createNavButton("Dashboard", IconType.HOME, true)); // Set 'true' for the active page
        navPanel.add(createNavButton("Student Masterlist", IconType.LIST, false));
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

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 235, 240)));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(SIDEBAR_BG);
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        right.setOpaque(false);

        // Bell Notification
        JPanel bellWrapper = new JPanel(new BorderLayout());
        bellWrapper.setOpaque(false);
        bellWrapper.add(new JLabel(new VectorIcon(IconType.BELL, TEXT_DARK)), BorderLayout.CENTER);
        right.add(bellWrapper);

        // Profile Avatar
        JLabel avatar = new JLabel("MS", SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 12));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(SIDEBAR_BG);
        avatar.setPreferredSize(new Dimension(36, 36));
        // Circular Avatar via rounded border simulation or custom paint, simplified using standard rect for now, will override paint if needed.
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_BG);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatarPanel.setLayout(new BorderLayout());
        avatarPanel.setPreferredSize(new Dimension(36, 36));
        avatarPanel.setOpaque(false);
        JLabel ms = new JLabel("MS", SwingConstants.CENTER);
        ms.setForeground(Color.WHITE);
        ms.setFont(new Font("SansSerif", Font.BOLD, 14));
        avatarPanel.add(ms, BorderLayout.CENTER);
        right.add(avatarPanel);

        // Profile Text
        JPanel profileText = new JPanel();
        profileText.setLayout(new BoxLayout(profileText, BoxLayout.Y_AXIS));
        profileText.setOpaque(false);

        adminHeaderName = new JLabel(adminName);
        JLabel name = adminHeaderName;
        name.setFont(new Font("SansSerif", Font.BOLD, 13));
        name.setForeground(TEXT_DARK);

        adminHeaderRole = new JLabel(adminRole + " - 99-001");
        JLabel role = adminHeaderRole;
        role.setFont(new Font("SansSerif", Font.PLAIN, 11));
        role.setForeground(TEXT_MUTED);

        profileText.add(name);
        profileText.add(role);

        right.add(profileText);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(PAGE_BG);
        body.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Welcome Banner
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        body.add(new WelcomeBanner(), gbc);

        // Quick Stats
        gbc.gridy = 1;
        body.add(createQuickStats(), gbc);

        // Split Layout Content (Queues & Admin Info)
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        body.add(createSplitContent(), gbc);

        return body;
    }

    private JPanel createQuickStats() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 20, 0));
        stats.setOpaque(false);

        stats.add(createStatCard("Total Enrolled", "3,482", IconType.USER_OUTLINE, SIDEBAR_BG, STAT_GREEN_BG));
        stats.add(createStatCard("Pending Validation", "124", IconType.DOCUMENT, SIDEBAR_BG, STAT_YELLOW_BG));
        stats.add(createStatCard("Full Sections", "15", IconType.GRID, SIDEBAR_BG, STAT_GREEN_BG));
        stats.add(createStatCard("System Status", "Active", IconType.CHECK, SIDEBAR_BG, STAT_YELLOW_BG));

        return stats;
    }

    private JPanel createStatCard(String title, String value, IconType iconType, Color iconColor, Color bgColor) {
        RoundedPanel card = new RoundedPanel(16, bgColor);
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel icon = new JLabel(new VectorIcon(iconType, iconColor));
        icon.setVerticalAlignment(SwingConstants.TOP);
        card.add(icon, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valLabel.setForeground(SIDEBAR_BG);
        textPanel.add(valLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setForeground(new Color(74, 88, 101));
        textPanel.add(titleLabel, BorderLayout.SOUTH);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSplitContent() {
        JPanel split = new JPanel(new GridBagLayout());
        split.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 30);

        // Left Area (70%)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.72;
        gbc.weighty = 1.0;
        split.add(createLeftContentArea(), gbc);

        // Right Area (28%)
        gbc.gridx = 1;
        gbc.weightx = 0.28;
        gbc.insets = new Insets(0, 0, 0, 0);
        split.add(createRightContentArea(), gbc);

        return split;
    }

    private JPanel createLeftContentArea() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Recent Validations Queue
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        left.add(createSectionHeader("Recent Validations Queue", "View Full Queue \u2192"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);

        JPanel queueList = new JPanel();
        queueList.setLayout(new BoxLayout(queueList, BoxLayout.Y_AXIS));
        queueList.setOpaque(false);

        queueList.add(createQueueRow("Justine Rivera", "2025-0011", "BS Information Technology", "3rd Year", "Cleared", new Color(38, 194, 129)));
        queueList.add(Box.createVerticalStrut(10));
        queueList.add(createQueueRow("Mark Mendoza", "2025-0012", "BS Civil Engineering", "2nd Year", "Pending", new Color(243, 156, 18)));
        queueList.add(Box.createVerticalStrut(10));
        queueList.add(createQueueRow("Prince Cariaga", "2025-0013", "BS Information Technology", "3rd Year", "Conflict", new Color(231, 76, 60)));
        left.add(queueList, gbc);

        // My Tasks spans the full width below the validation queue.
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        left.add(createTasksPanel(), gbc);
        return left;
    }

    private JPanel createTasksPanel() {
        JPanel tasks = new JPanel(new BorderLayout(0, 15));
        tasks.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(createSectionHeader("My Tasks", null), BorderLayout.WEST);
        JButton addTask = new JButton("+ Add Task");
        addTask.setFont(new Font("SansSerif", Font.BOLD, 11));
        addTask.setForeground(SIDEBAR_BG);
        addTask.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SIDEBAR_BG));
        addTask.setContentAreaFilled(false);
        addTask.setFocusPainted(false);
        addTask.addActionListener(event -> addTask());
        header.add(addTask, BorderLayout.EAST);
        tasks.add(header, BorderLayout.NORTH);

        JPanel taskList = new JPanel();
        taskList.setLayout(new BoxLayout(taskList, BoxLayout.Y_AXIS));
        taskList.setOpaque(false);
        refreshTaskList(taskList);
        taskModel.addListDataListener(new javax.swing.event.ListDataListener() {
            @Override
            public void intervalAdded(javax.swing.event.ListDataEvent event) {
                refreshTaskList(taskList);
            }

            @Override
            public void intervalRemoved(javax.swing.event.ListDataEvent event) {
                refreshTaskList(taskList);
            }

            @Override
            public void contentsChanged(javax.swing.event.ListDataEvent event) {
                refreshTaskList(taskList);
            }
        });
        tasks.add(taskList, BorderLayout.CENTER);
        return tasks;
    }

    private void addTask() {
        String title = JOptionPane.showInputDialog(window, "Enter task title:", "Add Admin Task", JOptionPane.PLAIN_MESSAGE);
        if (title != null && !title.trim().isEmpty()) {
            taskModel.addElement(title.trim());
        }
    }

    private void refreshTaskList(JPanel taskList) {
        taskList.removeAll();
        for (int index = 0; index < taskModel.size(); index++) {
            String task = taskModel.getElementAt(index);
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            JCheckBox checkBox = new JCheckBox();
            checkBox.setOpaque(false);
            checkBox.setFocusPainted(false);
            JLabel taskLabel = new JLabel(task);
            taskLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            taskLabel.setForeground(TEXT_DARK);
            checkBox.addActionListener(event -> {
                if (checkBox.isSelected()) {
                    taskLabel.setText("<html><strike>" + task + "</strike></html>");
                    taskLabel.setForeground(TEXT_MUTED);
                } else {
                    taskLabel.setText(task);
                    taskLabel.setForeground(TEXT_DARK);
                }
            });
            row.add(checkBox, BorderLayout.WEST);
            row.add(taskLabel, BorderLayout.CENTER);
            taskList.add(row);
        }
        taskList.revalidate();
        taskList.repaint();
    }

    private JPanel createRightContentArea() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Admin Info Header
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        right.add(createSectionHeader("Admin Info", "Edit Profile"), gbc);

        // Admin Info Box
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        right.add(createAdminInfoPanel(), gbc);

        // Total Enrollment Header
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        JPanel teHeader = new JPanel(new BorderLayout());
        teHeader.setOpaque(false);
        JLabel teLabel = new JLabel("Total Enrollment");
        teLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        teLabel.setForeground(TEXT_DARK);
        teLabel.setIcon(new VectorIcon(IconType.LAYOUT, SIDEBAR_BG)); // Approximate small square icon
        teLabel.setIconTextGap(8);
        teHeader.add(teLabel, BorderLayout.WEST);
        right.add(teHeader, gbc);

        // Chart Panel
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        right.add(createChartPanel(), gbc);

        return right;
    }

    private JPanel createSectionHeader(String title, String action) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 15));
        t.setForeground(TEXT_DARK);
        header.add(t, BorderLayout.WEST);

        if (action != null) {
            JButton a = new JButton(action);
            a.setFont(new Font("SansSerif", Font.BOLD, 12));
            a.setForeground(SIDEBAR_BG);
            a.setContentAreaFilled(false);
            a.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SIDEBAR_BG));
            a.setFocusPainted(false);
            a.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (action.equals("View Full Queue \u2192")) {
                a.addActionListener(event -> {
                    window.dispose();
                    new AdminValidationFrame().showWindow();
                });
            } else if (action.equals("Edit Profile")) {
                a.addActionListener(event -> showEditProfileDialog());
            }
            header.add(a, BorderLayout.EAST);
        }
        return header;
    }

    private JPanel createQueueRow(String name, String id, String course, String year, String status, Color statusColor) {
        RoundedPanel panel = new RoundedPanel(12, Color.WHITE);
        panel.setLayout(new BorderLayout());

        // Left color bar
        JPanel colorBar = new JPanel();
        colorBar.setBackground(statusColor);
        colorBar.setPreferredSize(new Dimension(4, 0));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name & ID
        JPanel col1 = new JPanel(new GridLayout(2, 1, 0, 4));
        col1.setOpaque(false);
        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLbl.setForeground(TEXT_DARK);
        JLabel idLbl = new JLabel(id);
        idLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        idLbl.setForeground(TEXT_MUTED);
        col1.add(nameLbl);
        col1.add(idLbl);
        gbc.gridx = 0; gbc.weightx = 0.3; content.add(col1, gbc);

        // Course & Year
        JPanel col2 = new JPanel(new GridLayout(2, 1, 0, 4));
        col2.setOpaque(false);
        JLabel courseLbl = new JLabel(course);
        courseLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        courseLbl.setForeground(TEXT_MUTED);
        JLabel yearLbl = new JLabel(year);
        yearLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        yearLbl.setForeground(TEXT_MUTED);
        col2.add(courseLbl);
        col2.add(yearLbl);
        gbc.gridx = 1; gbc.weightx = 0.4; content.add(col2, gbc);

        // Status Badge
        JPanel col3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        col3.setOpaque(false);
        JLabel badge = new JLabel(status, SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setForeground(statusColor.darker());
        badge.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(6, 16, 6, 16));

        // Ensure badge has rounded corners
        JPanel badgeWrapper = new RoundedPanel(12, badge.getBackground());
        badgeWrapper.setLayout(new BorderLayout());
        badgeWrapper.add(badge);
        badge.setBackground(new Color(0,0,0,0)); // Transparent to let wrapper show

        col3.add(badgeWrapper);
        gbc.gridx = 2; gbc.weightx = 0.2; content.add(col3, gbc);

        // Action Link
        JPanel col4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        col4.setOpaque(false);
        JLabel action = new JLabel("Review Load");
        action.setFont(new Font("SansSerif", Font.BOLD, 12));
        action.setForeground(SIDEBAR_BG);
        action.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        action.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                showReviewDialog(name, id, course, year, status);
            }
        });
        col4.add(action);
        gbc.gridx = 3; gbc.weightx = 0.1; content.add(col4, gbc);

        // Assemble
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(colorBar, BorderLayout.WEST);
        wrapper.add(content, BorderLayout.CENTER);
        panel.add(wrapper);

        // Wrap again to handle clipping of the left border nicely (so it respects rounding)
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(panel);
        return outer;
    }

    private void showReviewDialog(String name, String id, String course, String year, String status) {
        String details = "<html><b>Student:</b> " + name
                + "<br><b>Student ID:</b> " + id
                + "<br><b>Program:</b> " + course
                + "<br><b>Year Level:</b> " + year
                + "<br><b>Validation Status:</b> " + status + "</html>";
        JOptionPane.showMessageDialog(window, details, "Review Student Load", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEditProfileDialog() {
        JTextField nameField = new JTextField(adminName);
        JTextField roleField = new JTextField(adminRole);
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Role:"));
        form.add(roleField);
        int result = JOptionPane.showConfirmDialog(window, form, "Edit Admin Profile",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION
                && !nameField.getText().trim().isEmpty()
                && !roleField.getText().trim().isEmpty()) {
            adminName = nameField.getText().trim();
            adminRole = roleField.getText().trim();
            adminHeaderName.setText(adminName);
            adminHeaderRole.setText(adminRole + " - 99-001");
            JOptionPane.showMessageDialog(window, "Admin profile changes saved for this session.",
                    "Profile Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createAnnouncement(String text, String date) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel dot = new JLabel("•");
        dot.setFont(new Font("SansSerif", Font.BOLD, 24));
        dot.setForeground(GOLD);
        dot.setBorder(BorderFactory.createEmptyBorder(-6, 0, 0, 0)); // align visual center

        JLabel msg = new JLabel(text);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 12));
        msg.setForeground(TEXT_DARK);

        left.add(dot);
        left.add(msg);

        JLabel dt = new JLabel(date);
        dt.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dt.setForeground(TEXT_MUTED);

        panel.add(left, BorderLayout.WEST);
        panel.add(dt, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTaskRow(String text, String dueDate) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JCheckBox cb = new JCheckBox();
        cb.setOpaque(false);
        cb.setFocusPainted(false);

        JLabel msg = new JLabel(text);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 12));
        msg.setForeground(TEXT_DARK);

        left.add(cb);
        left.add(msg);
        panel.add(left, BorderLayout.WEST);

        if (dueDate != null) {
            JLabel badge = new JLabel(dueDate, SwingConstants.CENTER);
            badge.setFont(new Font("SansSerif", Font.BOLD, 10));
            badge.setForeground(new Color(220, 53, 69));
            badge.setBackground(new Color(253, 237, 237));
            badge.setOpaque(true);
            badge.setBorder(new EmptyBorder(4, 10, 4, 10));

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            right.setOpaque(false);
            RoundedPanel wrapper = new RoundedPanel(10, badge.getBackground());
            wrapper.setLayout(new BorderLayout());
            badge.setBackground(new Color(0,0,0,0));
            wrapper.add(badge);
            right.add(wrapper);
            panel.add(right, BorderLayout.EAST);
        }
        return panel;
    }

    private JPanel createAdminInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Large Avatar outline
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 4;
        gbc.weightx = 0.2; gbc.insets = new Insets(0, 0, 0, 15);
        JLabel largeAvatar = new JLabel(new VectorIcon(IconType.USER_AVATAR_LARGE, SIDEBAR_BG));
        panel.add(largeAvatar, gbc);

        gbc.gridheight = 1; gbc.weightx = 0.4; gbc.insets = new Insets(6, 0, 6, 0);

        addInfoRow(panel, "Admin ID", "99-001", 0, gbc);
        addInfoRow(panel, "Role", adminRole, 1, gbc);
        addInfoRow(panel, "Term", "1st Sem, 2025", 2, gbc);

        return panel;
    }

    private void addInfoRow(JPanel parent, String label, String val, int row, GridBagConstraints gbc) {
        gbc.gridy = row;

        gbc.gridx = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        parent.add(lbl, gbc);

        gbc.gridx = 2;
        JLabel valLbl = new JLabel(val);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        valLbl.setForeground(TEXT_DARK);
        parent.add(valLbl, gbc);
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // The Custom Donut Chart
        JPanel chartView = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 40;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Base background ring (30%)
                g2.setColor(new Color(248, 241, 226));
                g2.fillArc(x, y, size, size, 0, 360);

                // Completed ring (70%)
                g2.setColor(SIDEBAR_BG);
                g2.fillArc(x, y, size, size, 90, (int)(-360 * 0.70));

                // Inner cutout
                int innerSize = (int)(size * 0.75);
                int ix = x + (size - innerSize) / 2;
                int iy = y + (size - innerSize) / 2;
                g2.setColor(PAGE_BG);
                g2.fillOval(ix, iy, innerSize, innerSize);

                // Text
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 26));
                String pct = "70%";
                int tx = x + (size - g2.getFontMetrics().stringWidth(pct)) / 2;
                g2.drawString(pct, tx, y + size/2 + 5);

                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                String sub = "Complete";
                int sx = x + (size - g2.getFontMetrics().stringWidth(sub)) / 2;
                g2.drawString(sub, sx, y + size/2 + 20);

                g2.dispose();
            }
        };
        chartView.setPreferredSize(new Dimension(200, 200));
        chartView.setOpaque(false);
        panel.add(chartView, BorderLayout.CENTER);

        // Legend
        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setOpaque(false);
        legend.setBorder(new EmptyBorder(10, 20, 0, 0));

        legend.add(createLegendItem("Enrolled (3.4k)", SIDEBAR_BG));
        legend.add(Box.createVerticalStrut(8));
        legend.add(createLegendItem("Pending (124)", GOLD));

        panel.add(legend, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(8, 8));
        dot.setOpaque(false);

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);

        p.add(dot);
        p.add(lbl);
        return p;
    }

    // --- Custom UI Components --- //

    private static class WelcomeBanner extends JPanel {
        public WelcomeBanner() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 140));
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(25, 30, 25, 30));

            JPanel textContent = new JPanel();
            textContent.setLayout(new BoxLayout(textContent, BoxLayout.Y_AXIS));
            textContent.setOpaque(false);

            JLabel greeting = new JLabel("Good day,");
            greeting.setFont(new Font("SansSerif", Font.BOLD, 14));
            greeting.setForeground(SIDEBAR_BG);

            JLabel name = new JLabel("Admin Santos!");
            name.setFont(new Font("SansSerif", Font.BOLD, 32));
            name.setForeground(SIDEBAR_BG);

            JLabel sub = new JLabel("Ensure smooth operations. Review enrollments and manage university data.");
            sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
            sub.setForeground(new Color(90, 110, 100));

            textContent.add(greeting);
            textContent.add(name);
            textContent.add(Box.createVerticalStrut(8));
            textContent.add(sub);

            add(textContent, BorderLayout.WEST);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Base rounded rect
            Shape clip = new RoundRectangle2D.Float(0, 0, w, h, 20, 20);
            g2.setClip(clip);

            // Light background
            g2.setColor(STAT_GREEN_BG);
            g2.fillRect(0, 0, w, h);

            // Gold triangle slice
            g2.setColor(GOLD);
            Polygon gold = new Polygon(
                    new int[] { (int)(w * 0.65) - 40, w, w },
                    new int[] { 0, 0, h },
                    3
            );
            g2.fill(gold);

            // Dark green angled shape
            g2.setColor(SIDEBAR_BG);
            Polygon dark = new Polygon(
                    new int[] { (int)(w * 0.65), w, w, (int)(w * 0.85) },
                    new int[] { 0, 0, h, h },
                    4
            );
            g2.fill(dark);

            g2.dispose();
        }
    }

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
            try (InputStream is = AdminDashboardFrame.class.getResourceAsStream("/images/images/Frame 6 (1).png")) {
                return is == null ? null : ImageIO.read(is);
            } catch (Exception e) { return null; }
        }
    }

    public enum IconType {
        HOME, LIST, WINDOW, LAYOUT, USER_OUTLINE, LOGOUT, BELL, DOCUMENT, GRID, CHECK, USER_AVATAR_LARGE
    }

    private static class VectorIcon implements javax.swing.Icon {
        private final IconType type;
        private final Color color;
        VectorIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }
        public int getIconWidth() { return type == IconType.USER_AVATAR_LARGE ? 48 : 22; }
        public int getIconHeight() { return type == IconType.USER_AVATAR_LARGE ? 48 : 22; }

        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case HOME -> {
                    g.drawPolygon(new int[]{x+3, x+11, x+19}, new int[]{x+11, y+3, y+11}, 3);
                    g.drawRect(x+5, y+11, 12, 8);
                }
                case LIST -> {
                    g.drawRect(x+3, y+5, 16, 12);
                    g.drawLine(x+6, y+9, x+10, y+9);
                    g.drawLine(x+6, y+13, x+16, y+13);
                }
                case WINDOW -> {
                    g.drawRect(x+3, y+4, 16, 14);
                    g.drawLine(x+3, y+8, x+19, y+8);
                    g.drawRect(x+6, y+11, 10, 4);
                }
                case LAYOUT -> {
                    g.drawRect(x+3, y+3, 16, 16);
                    g.drawLine(x+9, y+3, x+9, y+19);
                    g.drawLine(x+9, y+11, x+19, y+11);
                }
                case USER_OUTLINE -> {
                    g.drawOval(x+7, y+3, 8, 8);
                    g.drawArc(x+3, y+11, 16, 10, 0, 180);
                }
                case USER_AVATAR_LARGE -> {
                    g.setStroke(new BasicStroke(2.5f));
                    g.drawOval(x+14, y+6, 20, 20);
                    g.drawArc(x+4, y+26, 40, 20, 0, 180);
                }
                case LOGOUT -> {
                    g.drawRect(x+6, y+4, 12, 14);
                    g.drawLine(x+2, y+11, x+10, y+11);
                    g.drawLine(x+2, y+11, x+5, y+8);
                    g.drawLine(x+2, y+11, x+5, y+14);
                }
                case BELL -> {
                    g.drawArc(x+5, y+4, 12, 12, 0, 180);
                    g.drawLine(x+5, y+10, x+5, y+16);
                    g.drawLine(x+17, y+10, x+17, y+16);
                    g.drawLine(x+3, y+16, x+19, y+16);
                    g.drawArc(x+9, y+16, 4, 4, 180, 180);
                }
                case DOCUMENT -> {
                    g.drawRect(x+4, y+3, 14, 16);
                    g.drawLine(x+8, y+7, x+14, y+7);
                    g.drawLine(x+8, y+11, x+14, y+11);
                    g.drawLine(x+8, y+15, x+11, y+15);
                }
                case GRID -> {
                    g.drawRect(x+3, y+3, 7, 7);
                    g.drawRect(x+12, y+3, 7, 7);
                    g.drawRect(x+3, y+12, 7, 7);
                    g.drawRect(x+12, y+12, 7, 7);
                }
                case CHECK -> {
                    g.drawLine(x+4, y+11, x+9, y+16);
                    g.drawLine(x+9, y+16, x+18, y+5);
                }
            }
            g.dispose();
        }
    }
}
