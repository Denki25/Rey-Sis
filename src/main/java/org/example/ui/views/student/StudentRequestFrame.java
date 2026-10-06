package org.example.ui.views.student;

import org.example.model.Student;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StudentRequestFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color RED_ICON = new Color(220, 53, 69);
    private static final Color RED_BG = new Color(253, 237, 237);
    private static final Color YELLOW_DOT = new Color(245, 195, 34);

    private final JFrame window = new JFrame("REY SIS | Document Request");
    private final Student student;
    private final List<DocRequestData> docRequests = new ArrayList<>();
    private JPanel tableContainer;

    public StudentRequestFrame(Student student) {
        this.student = student;
        initMockData();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1160, 780));
        window.setSize(1360, 860);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private void initMockData() {
        docRequests.add(new DocRequestData("CERTIFICATE OF GRADES", "Shows your official marks/grades for specific semesters or your entire stay.", 1));
        docRequests.add(new DocRequestData("CERTIFICATE OF GRADES", "Shows your official marks/grades for specific semesters or your entire stay.", 2));
        docRequests.add(new DocRequestData("CERTIFICATE OF ENROLLMENT", "Proves you are currently a registered student for the active semester.", 1));
        docRequests.add(new DocRequestData("CERTIFICATE OF REGISTRATION", "States the exact number of academic credits/units you have successfully completed.", 1));
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(createSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(PAGE);
        main.add(createPageHeader(), BorderLayout.NORTH);

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
        navigation.add(createNavigationButton("Grades", StudentDashboardFrame.IconType.GRADES, false));
        navigation.add(createNavigationButton("Academic Records", StudentDashboardFrame.IconType.RECORDS, false));
        navigation.add(createNavigationButton("Requests", StudentDashboardFrame.IconType.REQUESTS, true));
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
                if (text.equals("Dashboard")) openDashboard();
                else if (text.equals("My Profile")) openProfile();
                else if (text.equals("Enrollment")) openEnrollment();
                else if (text.equals("Grades")) openGrades();
                else if (text.equals("Requests")) showRequestsMessage();
                else showComingSoon(text);
            });
        }
        return button;
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PAGE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Document Request");
        heading.setFont(new Font("Serif", Font.BOLD, 28));
        heading.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Select the semester and proceed with your request");
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

        header.add(titlePanel, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightControls.setOpaque(false);

        // Notification Bell with Badge
        JPanel bellPanel = new JPanel(new BorderLayout());
        bellPanel.setOpaque(false);
        JLabel bellIcon = new JLabel(new VectorIcon(VectorIcon.Type.BELL, MUTED));

        JLabel badge = new JLabel("3", SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 9));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(GOLD);
        badge.setPreferredSize(new Dimension(14, 14));
        // Mocking overlapping badge by using border insets
        badge.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(badge);

        bellPanel.add(badgeWrapper, BorderLayout.NORTH);
        bellPanel.add(bellIcon, BorderLayout.CENTER);

        rightControls.add(bellPanel);

        // User Badge
        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userBadge.setOpaque(false);

        JLabel avatar = new JLabel(new VectorIcon(VectorIcon.Type.USER_AVATAR, MUTED));

        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(student != null ? student.getName() : "Justine Rivera");
        userName.setFont(new Font("SansSerif", Font.BOLD, 12));
        userName.setForeground(TEXT);

        JLabel userSub = new JLabel(student != null ? student.getProgram() + " - " + student.getStudentId() : "BSIT - 2025-0011");
        userSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        userSub.setForeground(MUTED);

        userText.add(userName);
        userText.add(userSub);

        userBadge.add(avatar);
        userBadge.add(userText);

        JLabel chevron = new JLabel(" \u2304 "); // Down chevron
        chevron.setForeground(MUTED);
        userBadge.add(chevron);

        rightControls.add(userBadge);
        header.add(rightControls, BorderLayout.EAST);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(12, 28, 24, 28));

        body.add(createDocumentTableCard(), BorderLayout.NORTH);
        body.add(createBottomCards(), BorderLayout.CENTER);

        return body;
    }

    private JPanel createDocumentTableCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Top Header of the Card
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        header.setOpaque(false);
        header.add(new JLabel(new VectorIcon(VectorIcon.Type.BOOK, DEEP_GREEN)));

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Select Document Type");
        title.setFont(new Font("Serif", Font.BOLD, 22));
        title.setForeground(DEEP_GREEN);
        JLabel sub = new JLabel("Select the document and proceed with your request");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);
        titleText.add(title);
        titleText.add(sub);
        header.add(titleText);

        card.add(header, BorderLayout.NORTH);

        tableContainer = new JPanel();
        tableContainer.setOpaque(false);
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));

        refreshTable();
        card.add(tableContainer, BorderLayout.CENTER);

        // Bottom Actions inside the Card
        JPanel bottomActions = new JPanel(new BorderLayout());
        bottomActions.setOpaque(false);
        bottomActions.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JButton addBtn = new JButton("ADD ANOTHER DOCUMENT FOR REQUEST", new VectorIcon(VectorIcon.Type.PLUS_CIRCLE, TEXT));
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        addBtn.setForeground(TEXT);
        addBtn.setBackground(Color.WHITE);
        addBtn.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 14));
        addBtn.setFocusPainted(false);
        addBtn.setContentAreaFilled(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> promptAddDocument());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(addBtn);
        bottomActions.add(leftPanel, BorderLayout.WEST);

        JButton downloadBtn = new JButton("DOWNLOAD FILES");
        downloadBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setBackground(BLACK);
        downloadBtn.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        downloadBtn.setFocusPainted(false);
        downloadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadBtn.addActionListener(e -> {
            if (docRequests.isEmpty()) {
                JOptionPane.showMessageDialog(window, "No documents selected for download.", "REY SIS", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(window, "Downloading " + docRequests.size() + " document(s)...", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        bottomActions.add(downloadBtn, BorderLayout.EAST);
        card.add(bottomActions, BorderLayout.SOUTH);

        return card;
    }

    private void refreshTable() {
        tableContainer.removeAll();

        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setOpaque(true);
        tableHeader.setBackground(new Color(249, 250, 248));
        tableHeader.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addTableHeaderCell(tableHeader, "DOCUMENT TYPE", 0, 0.30, gbc, SwingConstants.LEFT);
        addTableHeaderCell(tableHeader, "DESCRIPTION", 1, 0.50, gbc, SwingConstants.LEFT);
        addTableHeaderCell(tableHeader, "SEMESTER", 2, 0.10, gbc, SwingConstants.CENTER);
        addTableHeaderCell(tableHeader, "ACTION", 3, 0.10, gbc, SwingConstants.CENTER);

        tableContainer.add(tableHeader);

        for (DocRequestData data : docRequests) {
            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));

            JLabel cType = new JLabel("<html><div style='width:150px;'>" + data.type + "</div></html>");
            cType.setFont(new Font("SansSerif", Font.BOLD, 11));
            cType.setForeground(TEXT);
            gbc.gridx = 0; gbc.weightx = 0.30; gbc.anchor = GridBagConstraints.NORTHWEST; row.add(cType, gbc);

            JLabel cDesc = new JLabel("<html><div style='width:250px;'>" + data.description + "</div></html>");
            cDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
            cDesc.setForeground(TEXT);
            gbc.gridx = 1; gbc.weightx = 0.50; gbc.anchor = GridBagConstraints.NORTHWEST; row.add(cDesc, gbc);

            JLabel cSem = new JLabel(String.valueOf(data.semester), SwingConstants.CENTER);
            cSem.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cSem.setForeground(TEXT);
            gbc.gridx = 2; gbc.weightx = 0.10; gbc.anchor = GridBagConstraints.CENTER; row.add(cSem, gbc);

            JButton trashBtn = new JButton(new VectorIcon(VectorIcon.Type.TRASH, RED_ICON));
            trashBtn.setContentAreaFilled(false);
            trashBtn.setBorderPainted(false);
            trashBtn.setFocusPainted(false);
            trashBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            trashBtn.addActionListener(e -> {
                docRequests.remove(data);
                refreshTable();
            });

            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            actionPanel.setOpaque(false);
            actionPanel.add(trashBtn);

            gbc.gridx = 3; gbc.weightx = 0.10; gbc.anchor = GridBagConstraints.CENTER; row.add(actionPanel, gbc);

            tableContainer.add(row);
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private void addTableHeaderCell(JPanel header, String text, int gridx, double weightx, GridBagConstraints gbc, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MUTED);
        gbc.gridx = gridx;
        gbc.weightx = weightx;
        header.add(label, gbc);
    }

    private void promptAddDocument() {
        String[] options = {"Certificate of Grades", "Certificate of Enrollment", "Certificate of Registration", "Transcript of Records"};
        String selection = (String) JOptionPane.showInputDialog(window,
                "Select document type to add:",
                "Add Document",
                JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

        if (selection != null && !selection.isBlank()) {
            docRequests.add(new DocRequestData(
                    selection.toUpperCase(),
                    "Description for " + selection + " generated based on selection.",
                    1
            ));
            refreshTable();
        }
    }

    private JPanel createBottomCards() {
        JPanel bottomContainer = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomContainer.setOpaque(false);

        // Recent Announcements Card
        CardPanel announcementCard = new CardPanel(Color.WHITE);
        announcementCard.setLayout(new BorderLayout());
        announcementCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel aHeader = createCardHeader(VectorIcon.Type.MEGAPHONE, "Recent Announcements");
        announcementCard.add(aHeader, BorderLayout.NORTH);

        JPanel aList = new JPanel();
        aList.setOpaque(false);
        aList.setLayout(new BoxLayout(aList, BoxLayout.Y_AXIS));
        aList.add(Box.createVerticalStrut(10));
        aList.add(createListRow(VectorIcon.Type.DOT, YELLOW_DOT, "Your grades for this semester are now available.", "Oct 5, 2025", null));
        aList.add(createListRow(VectorIcon.Type.DOT, YELLOW_DOT, "Enrollment for midyear term will start next month.", "Oct 4, 2025", null));
        aList.add(createListRow(VectorIcon.Type.DOT, YELLOW_DOT, "System maintenance this Saturday, 8:00 PM.", "Oct 3, 2025", null));
        announcementCard.add(aList, BorderLayout.CENTER);

        // My Tasks Card
        CardPanel tasksCard = new CardPanel(Color.WHITE);
        tasksCard.setLayout(new BorderLayout());
        tasksCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel tHeader = createCardHeader(VectorIcon.Type.CLIPBOARD, "My Tasks");
        tasksCard.add(tHeader, BorderLayout.NORTH);

        JPanel tList = new JPanel();
        tList.setOpaque(false);
        tList.setLayout(new BoxLayout(tList, BoxLayout.Y_AXIS));
        tList.add(Box.createVerticalStrut(10));
        tList.add(createListRow(VectorIcon.Type.BOX, MUTED, "Settle tuition fee", null, "Due Oct 10"));
        tList.add(createListRow(VectorIcon.Type.BOX, MUTED, "Evaluate professors", null, "Due Oct 15"));
        tList.add(createListRow(VectorIcon.Type.BOX, MUTED, "Update personal information", null, null));
        tList.add(createListRow(VectorIcon.Type.BOX, MUTED, "Apply for scholarship (optional)", null, null));
        tasksCard.add(tList, BorderLayout.CENTER);

        bottomContainer.add(announcementCard);
        bottomContainer.add(tasksCard);
        return bottomContainer;
    }

    private JPanel createCardHeader(VectorIcon.Type icon, String titleText) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleGroup.setOpaque(false);
        titleGroup.add(new JLabel(new VectorIcon(icon, DEEP_GREEN)));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Serif", Font.BOLD, 18));
        title.setForeground(DEEP_GREEN);
        titleGroup.add(title);
        header.add(titleGroup, BorderLayout.WEST);

        JLabel viewAll = new JLabel("View All \u2192");
        viewAll.setFont(new Font("SansSerif", Font.PLAIN, 10));
        viewAll.setForeground(MUTED);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(viewAll, BorderLayout.EAST);

        return header;
    }

    private JPanel createListRow(VectorIcon.Type iconType, Color iconColor, String mainText, String rightDate, String rightPill) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leftGroup.setOpaque(false);
        leftGroup.add(new JLabel(new VectorIcon(iconType, iconColor)));

        JLabel textLabel = new JLabel(mainText);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textLabel.setForeground(TEXT);
        leftGroup.add(textLabel);

        row.add(leftGroup, BorderLayout.WEST);

        if (rightDate != null) {
            JLabel dateLabel = new JLabel(rightDate);
            dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            dateLabel.setForeground(MUTED);
            row.add(dateLabel, BorderLayout.EAST);
        } else if (rightPill != null) {
            JLabel pillLabel = new JLabel(rightPill);
            pillLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
            pillLabel.setForeground(RED_ICON);
            pillLabel.setBackground(RED_BG);
            pillLabel.setOpaque(true);
            pillLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

            JPanel pillWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            pillWrapper.setOpaque(false);
            pillWrapper.add(pillLabel);
            row.add(pillWrapper, BorderLayout.EAST);
        }

        return row;
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

    private void openGrades() {
        window.dispose();
        new StudentGradesFrame(student).showWindow();
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private void showRequestsMessage() {
        JOptionPane.showMessageDialog(window, "You are already viewing Document Request.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showComingSoon(String module) {
        JOptionPane.showMessageDialog(window, module + " is coming next.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = StudentRequestFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static class DocRequestData {
        String type;
        String description;
        int semester;

        DocRequestData(String type, String description, int semester) {
            this.type = type;
            this.description = description;
            this.semester = semester;
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
        enum Type { USER_AVATAR, BELL, BOOK, TRASH, PLUS_CIRCLE, MEGAPHONE, CLIPBOARD, DOT, BOX }

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
                case USER_AVATAR -> {
                    g.drawOval(x + 5, y + 2, 8, 8);
                    g.drawArc(x + 2, y + 9, 14, 8, 0, 180);
                }
                case BELL -> {
                    g.drawArc(x + 4, y + 3, 10, 10, 0, 180);
                    g.drawLine(x + 4, y + 8, x + 4, y + 13);
                    g.drawLine(x + 14, y + 8, x + 14, y + 13);
                    g.drawLine(x + 2, y + 13, x + 16, y + 13);
                    g.drawArc(x + 7, y + 13, 4, 4, 180, 180);
                }
                case BOOK -> {
                    g.drawRect(x + 2, y + 3, 6, 12);
                    g.drawRect(x + 8, y + 3, 6, 12);
                    g.drawLine(x + 8, y + 3, x + 8, y + 15);
                    g.drawLine(x + 4, y + 6, x + 6, y + 6);
                    g.drawLine(x + 10, y + 6, x + 12, y + 6);
                }
                case TRASH -> {
                    g.drawRect(x + 5, y + 5, 8, 10);
                    g.drawLine(x + 3, y + 5, x + 15, y + 5);
                    g.drawArc(x + 7, y + 3, 4, 4, 0, 180);
                    g.drawLine(x + 7, y + 8, x + 7, y + 12);
                    g.drawLine(x + 11, y + 8, x + 11, y + 12);
                }
                case PLUS_CIRCLE -> {
                    g.drawOval(x + 1, y + 1, 16, 16);
                    g.drawLine(x + 9, y + 5, x + 9, y + 13);
                    g.drawLine(x + 5, y + 9, x + 13, y + 9);
                }
                case MEGAPHONE -> {
                    Polygon p = new Polygon(new int[]{x + 5, x + 13, x + 13, x + 5}, new int[]{y + 6, y + 3, y + 15, y + 12}, 4);
                    g.drawPolygon(p);
                    g.drawRect(x + 2, y + 7, 3, 4);
                    g.drawArc(x + 11, y + 7, 4, 4, -90, 180);
                }
                case CLIPBOARD -> {
                    g.drawRect(x + 4, y + 4, 10, 12);
                    g.drawRect(x + 7, y + 2, 4, 3);
                    g.drawLine(x + 7, y + 8, x + 11, y + 8);
                    g.drawLine(x + 7, y + 11, x + 11, y + 11);
                    g.drawLine(x + 6, y + 8, x + 6, y + 8);
                    g.drawLine(x + 6, y + 11, x + 6, y + 11);
                }
                case DOT -> {
                    g.fillOval(x + 6, y + 6, 6, 6);
                }
                case BOX -> {
                    g.drawRect(x + 4, y + 4, 10, 10);
                }
            }
            g.dispose();
        }
    }
}