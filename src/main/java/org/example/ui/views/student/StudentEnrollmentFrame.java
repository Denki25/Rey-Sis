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
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.JCheckBox;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.TableModelEvent;
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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class StudentEnrollmentFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);
    private static final Color RED_ACCENT = new Color(215, 68, 62);
    private static final Color SUMMARY_BG = new Color(241, 247, 244);
    private static final Color STATUS_BG = new Color(254, 250, 235);

    private final JFrame window = new JFrame("REY SIS | Enrollment");
    private final Student student;
    private final DefaultTableModel selectedSubjectsModel = new DefaultTableModel(
            new Object[]{"CODE", "COURSE TITLE", "UNITS", "ACTION"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }
    };
    private final List<String> passedCourses = List.of("COMSCI 2100", "INTECH 1100", "MATH 1100", "SOCSCI 1100", "PATHFIT 1");
    private final List<SubjectRowData> selectedSubjects = new ArrayList<>();

    private JLabel totalSubjectsLabel;
    private JLabel totalUnitsLabel;
    private JLabel estimatedTuitionLabel;
    private JTable subjectsTable;
    private JPanel subjectsTableContainer;
    private JButton addSubjectButton;
    private JButton browseSubjectsButton;
    private JButton continueButton;

    public StudentEnrollmentFrame(Student student) {
        this.student = student;
        initDefaultSubjects();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1060, 680));
        window.setSize(1360, 820);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private void initDefaultSubjects() {
        selectedSubjectsModel.addRow(new Object[]{"COMSCI 2100", "Object-Oriented Programming", 3, "Remove"});
        selectedSubjectsModel.addRow(new Object[]{"INTECH 1100", "Discrete Mathematics", 3, "Remove"});
        selectedSubjectsModel.addRow(new Object[]{"MATH 1100", "Mathematics in the Modern World", 3, "Remove"});
        selectedSubjectsModel.addRow(new Object[]{"SOCSCI 1100", "Ethics", 3, "Remove"});
        selectedSubjectsModel.addRow(new Object[]{"PATHFIT 1", "Movement Competency Training", 2, "Remove"});
        selectedSubjectsModel.addTableModelListener(event -> {
            if (event.getType() == TableModelEvent.INSERT || event.getType() == TableModelEvent.DELETE
                    || event.getType() == TableModelEvent.UPDATE) {
                updateSummary();
            }
        });
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
        navigation.add(createNavigationButton("Enrollment", StudentDashboardFrame.IconType.ENROLLMENT, true));
        navigation.add(createNavigationButton("My Schedule", StudentDashboardFrame.IconType.CALENDAR, false));
        navigation.add(createNavigationButton("Grades", StudentDashboardFrame.IconType.GRADES, false));
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
                    showEnrollmentMessage();
                } else if (text.equals("Grades")) {
                    window.dispose();
                    new StudentGradesFrame(student).showWindow();
                } else if (text.equals("My Schedule")) {
                    window.dispose();
                    new StudentScheduleFrame(student).showWindow();
                }else if (text.equals("Requests")) {
                    window.dispose();
                    new StudentRequestFrame(student).showWindow(); // <--- Add this routing
                } else {
                    showComingSoon(text);
                }
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

        JLabel heading = new JLabel("Enrollment");
        heading.setFont(new Font("Serif", Font.BOLD, 28));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel("Select subjects for the current semester and proceed with your enrollment");
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

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightControls.setOpaque(false);

        String[] semesters = {"AY 2026 - 2027  ·  1st Semester", "AY 2026 - 2027  ·  2nd Semester"};
        JComboBox<String> semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setFont(new Font("SansSerif", Font.BOLD, 11));
        semesterCombo.setForeground(TEXT);
        semesterCombo.setBackground(Color.WHITE);
        semesterCombo.setFocusable(false);
        semesterCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        rightControls.add(semesterCombo);

        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userBadge.setOpaque(false);

        JLabel bell = new JLabel(new VectorIcon(VectorIcon.Type.BELL, MUTED));
        bell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel avatar = new JLabel(new VectorIcon(VectorIcon.Type.USER_AVATAR, DEEP_GREEN));

        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(student != null ? student.getName() : "Justine Rivera");
        userName.setFont(new Font("SansSerif", Font.BOLD, 11));
        userName.setForeground(TEXT);

        JLabel userSub = new JLabel("BSIT · 2025-0011");
        userSub.setFont(new Font("SansSerif", Font.PLAIN, 9));
        userSub.setForeground(MUTED);

        userText.add(userName);
        userText.add(userSub);

        userBadge.add(bell);
        userBadge.add(Box.createHorizontalStrut(6));
        userBadge.add(avatar);
        userBadge.add(userText);

        rightControls.add(userBadge);
        rightControls.removeAll();
        header.add(rightControls, BorderLayout.EAST);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(12, 28, 24, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Column: Selected subjects table & search section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.67;
        gbc.insets = new Insets(0, 0, 0, 16);
        body.add(createLeftColumn(), gbc);

        // Right Column: Summary & Status
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.33;
        gbc.insets = new Insets(0, 0, 0, 0);
        body.add(createRightColumn(), gbc);

        return body;
    }

    private JPanel createLeftColumn() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        // Selected Subjects Main Card
        CardPanel mainCard = new CardPanel(Color.WHITE);
        mainCard.setLayout(new BorderLayout(0, 14));
        mainCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JPanel titleGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleGrp.setOpaque(false);
        titleGrp.add(new JLabel(new VectorIcon(VectorIcon.Type.BOOK, DEEP_GREEN)));

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Selected Subjects");
        title.setFont(new Font("Serif", Font.BOLD, 18));
        title.setForeground(TEXT);
        JLabel sub = new JLabel("List of subjects you have added to your enrollment.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sub.setForeground(MUTED);
        titleText.add(title);
        titleText.add(sub);
        titleGrp.add(titleText);
        cardHeader.add(titleGrp, BorderLayout.WEST);

        addSubjectButton = new JButton("+ Add Subject");
        addSubjectButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        addSubjectButton.setForeground(Color.WHITE);
        addSubjectButton.setBackground(DEEP_GREEN);
        addSubjectButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        addSubjectButton.setFocusPainted(false);
        addSubjectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addSubjectButton.addActionListener(e -> promptAddSubject());
        cardHeader.add(addSubjectButton, BorderLayout.EAST);

        mainCard.add(cardHeader, BorderLayout.NORTH);

        subjectsTable = createSubjectsTable();
        JScrollPane subjectsScrollPane = new JScrollPane(subjectsTable);
        subjectsScrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        subjectsScrollPane.setPreferredSize(new Dimension(0, 290));
        mainCard.add(subjectsScrollPane, BorderLayout.CENTER);
        left.add(mainCard);

        left.add(Box.createVerticalStrut(14));

        // Add More Subjects Search Card
        CardPanel searchCard = new CardPanel(Color.WHITE);
        searchCard.setLayout(new BorderLayout());
        searchCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        searchLeft.setOpaque(false);
        searchLeft.add(new JLabel(new VectorIcon(VectorIcon.Type.SEARCH, DEEP_GREEN)));

        JPanel searchMsg = new JPanel();
        searchMsg.setOpaque(false);
        searchMsg.setLayout(new BoxLayout(searchMsg, BoxLayout.Y_AXIS));
        JLabel sTitle = new JLabel("Add More Subjects");
        sTitle.setFont(new Font("Serif", Font.BOLD, 15));
        sTitle.setForeground(TEXT);
        JLabel sSub = new JLabel("Browse available subjects for the current semester.");
        sSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sSub.setForeground(MUTED);
        searchMsg.add(sTitle);
        searchMsg.add(sSub);
        searchLeft.add(searchMsg);

        searchCard.add(searchLeft, BorderLayout.WEST);

        JButton browseBtn = new JButton("Browse Subjects  →");
        browseBtn.setFont(new Font("SansSerif", Font.BOLD, 10));
        browseSubjectsButton = browseBtn;
        browseBtn.setForeground(GOLD);
        browseBtn.setBackground(Color.WHITE);
        browseBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1, true),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        browseBtn.addActionListener(e -> promptAddSubject());
        searchCard.add(browseBtn, BorderLayout.EAST);

        left.add(searchCard);

        return left;
    }

    private JTable createSubjectsTable() {
        JTable table = new JTable(selectedSubjectsModel);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(235, 244, 239));
        table.setSelectionForeground(TEXT);
        table.setFont(new Font("SansSerif", Font.PLAIN, 10));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 9));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(new TableActionRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new TableActionEditor(new JCheckBox(), this::removeSelectedSubject));
        table.getColumnModel().getColumn(0).setPreferredWidth(125);
        table.getColumnModel().getColumn(1).setPreferredWidth(330);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        return table;
    }

    private void removeSelectedSubject(int row) {
        if (row >= 0 && row < selectedSubjectsModel.getRowCount()) {
            selectedSubjectsModel.removeRow(row);
        }
    }

    private void renderSubjectsTable() {
        subjectsTableContainer.removeAll();

        // Table Header
        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(6, 12, 8, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel hCode = new JLabel("CODE");
        hCode.setFont(new Font("SansSerif", Font.BOLD, 9));
        hCode.setForeground(MUTED);
        gbc.gridx = 0; gbc.weightx = 0.22; tableHeader.add(hCode, gbc);

        JLabel hTitle = new JLabel("COURSE TITLE");
        hTitle.setFont(new Font("SansSerif", Font.BOLD, 9));
        hTitle.setForeground(MUTED);
        gbc.gridx = 1; gbc.weightx = 0.48; tableHeader.add(hTitle, gbc);

        JLabel hUnits = new JLabel("UNITS", SwingConstants.CENTER);
        hUnits.setFont(new Font("SansSerif", Font.BOLD, 9));
        hUnits.setForeground(MUTED);
        gbc.gridx = 2; gbc.weightx = 0.15; tableHeader.add(hUnits, gbc);

        JLabel hAction = new JLabel("ACTION", SwingConstants.CENTER);
        hAction.setFont(new Font("SansSerif", Font.BOLD, 9));
        hAction.setForeground(MUTED);
        gbc.gridx = 3; gbc.weightx = 0.15; tableHeader.add(hAction, gbc);

        subjectsTableContainer.add(tableHeader);

        // Rows
        for (int i = 0; i < selectedSubjects.size(); i++) {
            SubjectRowData data = selectedSubjects.get(i);
            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(242, 243, 240)),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));

            JLabel cCode = new JLabel(data.code);
            cCode.setFont(new Font("SansSerif", Font.BOLD, 10));
            cCode.setForeground(TEXT);
            gbc.gridx = 0; gbc.weightx = 0.22; row.add(cCode, gbc);

            JLabel cTitle = new JLabel(data.title);
            cTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
            cTitle.setForeground(TEXT);
            gbc.gridx = 1; gbc.weightx = 0.48; row.add(cTitle, gbc);

            JLabel cUnits = new JLabel(String.valueOf(data.units), SwingConstants.CENTER);
            cUnits.setFont(new Font("SansSerif", Font.PLAIN, 10));
            cUnits.setForeground(TEXT);
            gbc.gridx = 2; gbc.weightx = 0.15; row.add(cUnits, gbc);

            JLabel trash = new JLabel(new VectorIcon(VectorIcon.Type.TRASH, RED_ACCENT), SwingConstants.CENTER);
            trash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final int index = i;
            trash.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedSubjects.remove(index);
                    renderSubjectsTable();
                    updateSummary();
                }
            });
            gbc.gridx = 3; gbc.weightx = 0.15; row.add(trash, gbc);

            subjectsTableContainer.add(row);
        }

        subjectsTableContainer.revalidate();
        subjectsTableContainer.repaint();
    }

    private JPanel createRightColumn() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        // Enrollment Summary Card
        CardPanel summaryCard = new CardPanel(Color.WHITE);
        summaryCard.setLayout(new BorderLayout(0, 12));
        summaryCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel sumHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sumHeader.setOpaque(false);
        sumHeader.add(new JLabel(new VectorIcon(VectorIcon.Type.SUMMARY_DOC, DEEP_GREEN)));

        JPanel sumTitleText = new JPanel();
        sumTitleText.setOpaque(false);
        sumTitleText.setLayout(new BoxLayout(sumTitleText, BoxLayout.Y_AXIS));
        JLabel sTitle = new JLabel("Enrollment Summary");
        sTitle.setFont(new Font("Serif", Font.BOLD, 17));
        sTitle.setForeground(TEXT);
        JLabel sSub = new JLabel("Overview of your selected subjects.");
        sSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sSub.setForeground(MUTED);
        sumTitleText.add(sTitle);
        sumTitleText.add(sSub);
        sumHeader.add(sumTitleText);
        summaryCard.add(sumHeader, BorderLayout.NORTH);

        CardPanel innerSummary = new CardPanel(SUMMARY_BG);
        innerSummary.setLayout(new GridLayout(3, 1, 0, 10));
        innerSummary.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        totalSubjectsLabel = new JLabel(String.valueOf(selectedSubjectsModel.getRowCount()), SwingConstants.RIGHT);
        totalSubjectsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalSubjectsLabel.setForeground(TEXT);

        totalUnitsLabel = new JLabel(String.valueOf(calculateTotalUnits()), SwingConstants.RIGHT);
        totalUnitsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalUnitsLabel.setForeground(TEXT);

        estimatedTuitionLabel = new JLabel(String.format("₱ %,.2f", calculateTuition()), SwingConstants.RIGHT);
        estimatedTuitionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        estimatedTuitionLabel.setForeground(TEXT);

        innerSummary.add(createSummaryRow("Total Subjects", totalSubjectsLabel, VectorIcon.Type.BOOK));
        innerSummary.add(createSummaryRow("Total Units", totalUnitsLabel, VectorIcon.Type.LAYERS));
        innerSummary.add(createSummaryRow("Estimated Tuition", estimatedTuitionLabel, VectorIcon.Type.TUITION));

        summaryCard.add(innerSummary, BorderLayout.CENTER);
        right.add(summaryCard);

        right.add(Box.createVerticalStrut(14));

        // Enrollment Status Card
        CardPanel statusCard = new CardPanel(Color.WHITE);
        statusCard.setLayout(new BorderLayout(0, 12));
        statusCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel statHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statHeader.setOpaque(false);
        statHeader.add(new JLabel(new VectorIcon(VectorIcon.Type.INFO_CIRCLE, DEEP_GREEN)));

        JPanel statTitleText = new JPanel();
        statTitleText.setOpaque(false);
        statTitleText.setLayout(new BoxLayout(statTitleText, BoxLayout.Y_AXIS));
        JLabel stTitle = new JLabel("Enrollment Status");
        stTitle.setFont(new Font("Serif", Font.BOLD, 17));
        stTitle.setForeground(TEXT);
        JLabel stSub = new JLabel("Your current enrollment status.");
        stSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        stSub.setForeground(MUTED);
        statTitleText.add(stTitle);
        statTitleText.add(stSub);
        statHeader.add(statTitleText);
        statusCard.add(statHeader, BorderLayout.NORTH);

        CardPanel innerStatus = new CardPanel(STATUS_BG);
        innerStatus.setLayout(new BorderLayout(10, 0));
        innerStatus.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JLabel clockIcon = new JLabel(new VectorIcon(VectorIcon.Type.CLOCK, GOLD));
        innerStatus.add(clockIcon, BorderLayout.WEST);

        JPanel statusText = new JPanel();
        statusText.setOpaque(false);
        statusText.setLayout(new BoxLayout(statusText, BoxLayout.Y_AXIS));

        JLabel statusTitle = new JLabel("Draft - Not yet submitted");
        statusTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusTitle.setForeground(TEXT);

        JLabel statusDesc = new JLabel("<html>You can still add or remove subjects before submitting your enrollment.</html>");
        statusDesc.setFont(new Font("SansSerif", Font.PLAIN, 9));
        statusDesc.setForeground(MUTED);

        statusText.add(statusTitle);
        statusText.add(Box.createVerticalStrut(2));
        statusText.add(statusDesc);

        innerStatus.add(statusText, BorderLayout.CENTER);
        statusCard.add(innerStatus, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actions.setOpaque(false);

        JButton saveDraftBtn = new JButton("Save as Draft");
        saveDraftBtn.setFont(new Font("SansSerif", Font.BOLD, 10));
        saveDraftBtn.setForeground(TEXT);
        saveDraftBtn.setBackground(Color.WHITE);
        saveDraftBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        saveDraftBtn.setFocusPainted(false);
        saveDraftBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveDraftBtn.addActionListener(e -> JOptionPane.showMessageDialog(window, "Enrollment draft saved successfully!", "REY SIS", JOptionPane.INFORMATION_MESSAGE));

        JButton continueBtn = new JButton("Continue to Assessment  →");
        continueBtn.setFont(new Font("SansSerif", Font.BOLD, 10));
        continueButton = continueBtn;
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setBackground(DEEP_GREEN);
        continueBtn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        continueBtn.setFocusPainted(false);
        continueBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continueBtn.addActionListener(e -> openAssessment());

        actions.add(saveDraftBtn);
        actions.add(continueBtn);

        statusCard.add(actions, BorderLayout.SOUTH);
        right.add(statusCard);

        return right;
    }

    private JPanel createSummaryRow(String labelText, JLabel valueLabel, VectorIcon.Type iconType) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(new JLabel(new VectorIcon(iconType, DEEP_GREEN)));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(MUTED);
        left.add(lbl);

        row.add(left, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private int calculateTotalUnits() {
        int sum = 0;
        for (int row = 0; row < selectedSubjectsModel.getRowCount(); row++) {
            sum += ((Number) selectedSubjectsModel.getValueAt(row, 2)).intValue();
        }
        return sum;
    }

    private double calculateTuition() {
        return calculateTotalUnits() * 1800.0;
    }

    private void updateSummary() {
        if (totalSubjectsLabel != null) {
            totalSubjectsLabel.setText(String.valueOf(selectedSubjectsModel.getRowCount()));
            totalUnitsLabel.setText(String.valueOf(calculateTotalUnits()));
            estimatedTuitionLabel.setText(String.format("₱ %,.2f", calculateTuition()));
        }
    }

    private void promptAddSubject() {
        openAvailableSubjectsDialog();
    }

    private void openAssessment() {
        if (selectedSubjectsModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(window,
                    "Please select at least one subject before proceeding to Assessment.",
                    "No Subjects Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<AssessmentDialog.SubjectLine> subjects = new ArrayList<>();
        for (int row = 0; row < selectedSubjectsModel.getRowCount(); row++) {
            subjects.add(new AssessmentDialog.SubjectLine(
                    String.valueOf(selectedSubjectsModel.getValueAt(row, 0)),
                    String.valueOf(selectedSubjectsModel.getValueAt(row, 1)),
                    ((Number) selectedSubjectsModel.getValueAt(row, 2)).intValue()));
        }
        AssessmentDialog dialog = new AssessmentDialog(window, student, subjects,
                calculateTotalUnits(), calculateTuition(), this::markEnrollmentSubmitted);
        dialog.setVisible(true);
    }

    private void markEnrollmentSubmitted() {
        subjectsTable.setEnabled(false);
        addSubjectButton.setEnabled(false);
        browseSubjectsButton.setEnabled(false);
        continueButton.setEnabled(false);
    }

    private void openAvailableSubjectsDialog() {
        List<AvailableSubject> availableSubjects = List.of(
                new AvailableSubject("COMSCI 2200", "Data Structures and Algorithms", 3, "COMSCI 2100"),
                new AvailableSubject("INTECH 1200", "Web Systems and Technologies", 3, "INTECH 1100"),
                new AvailableSubject("MATH 1200", "Statistics for Computing", 3, "MATH 1100"),
                new AvailableSubject("PATHFIT 2", "Exercise and Sports", 2, "PATHFIT 1")
        );
        JDialog dialog = new JDialog(window, "Available Subjects", true);
        dialog.setLayout(new BorderLayout(0, 12));
        dialog.setMinimumSize(new Dimension(760, 360));
        dialog.setSize(820, 430);
        dialog.setLocationRelativeTo(window);

        JLabel heading = new JLabel("Available Subjects");
        heading.setFont(new Font("Serif", Font.BOLD, 20));
        heading.setForeground(TEXT);
        heading.setBorder(BorderFactory.createEmptyBorder(16, 18, 0, 18));
        dialog.add(heading, BorderLayout.NORTH);

        DefaultTableModel availableModel = new DefaultTableModel(
                new Object[]{"CODE", "COURSE TITLE", "UNITS", "PREREQUISITES", "ACTION"}, 0) {
            public boolean isCellEditable(int row, int column) { return column == 4; }
        };
        for (AvailableSubject subject : availableSubjects) {
            availableModel.addRow(new Object[]{subject.code, subject.title, subject.units, subject.prerequisite, "Add"});
        }
        JTable availableTable = new JTable(availableModel);
        availableTable.setRowHeight(40);
        availableTable.setShowGrid(false);
        availableTable.setFont(new Font("SansSerif", Font.PLAIN, 10));
        availableTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 9));
        availableTable.getTableHeader().setForeground(MUTED);
        availableTable.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);
        left.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        DefaultTableCellRenderer centered = new DefaultTableCellRenderer();
        centered.setHorizontalAlignment(SwingConstants.CENTER);
        availableTable.getColumnModel().getColumn(0).setCellRenderer(left);
        availableTable.getColumnModel().getColumn(1).setCellRenderer(left);
        availableTable.getColumnModel().getColumn(2).setCellRenderer(centered);
        availableTable.getColumnModel().getColumn(3).setCellRenderer(left);
        availableTable.getColumnModel().getColumn(4).setCellRenderer(new TableActionRenderer());
        availableTable.getColumnModel().getColumn(4).setCellEditor(new TableActionEditor(new JCheckBox(), row -> {
            addAvailableSubject(availableSubjects.get(row));
            if (dialog.isDisplayable()) {
                dialog.dispose();
            }
        }));
        JScrollPane scrollPane = new JScrollPane(availableTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(event -> dialog.dispose());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 18));
        footer.add(close);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addAvailableSubject(AvailableSubject subject) {
        for (int row = 0; row < selectedSubjectsModel.getRowCount(); row++) {
            if (subject.code.equalsIgnoreCase(String.valueOf(selectedSubjectsModel.getValueAt(row, 0)))) {
                JOptionPane.showMessageDialog(window, "This subject is already selected.", "Cannot Add Subject", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (calculateTotalUnits() + subject.units > 24) {
            JOptionPane.showMessageDialog(window, "You cannot exceed the 24-unit semester limit.", "Cannot Add Subject", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!subject.prerequisite.isBlank() && !passedCourses.contains(subject.prerequisite)) {
            JOptionPane.showMessageDialog(window, "Prerequisite required: " + subject.prerequisite, "Cannot Add Subject", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedSubjectsModel.addRow(new Object[]{subject.code, subject.title, subject.units, "Remove"});
    }

    private void openDashboard() {
        window.dispose();
        new StudentDashboardFrame(student).showWindow();
    }

    private void openProfile() {
        window.dispose();
        new StudentProfileFrame(student).showWindow();
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private void showEnrollmentMessage() {
        JOptionPane.showMessageDialog(window, "You are already viewing Enrollment.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showComingSoon(String module) {
        JOptionPane.showMessageDialog(window, module + " is coming next.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = StudentEnrollmentFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static class AvailableSubject {
        private final String code;
        private final String title;
        private final int units;
        private final String prerequisite;

        private AvailableSubject(String code, String title, int units, String prerequisite) {
            this.code = code;
            this.title = title;
            this.units = units;
            this.prerequisite = prerequisite;
        }
    }

    private static class TableActionRenderer extends JButton implements TableCellRenderer {
        TableActionRenderer() {
            setFont(new Font("SansSerif", Font.BOLD, 10));
            setForeground(DEEP_GREEN);
            setBackground(new Color(232, 242, 236));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setFocusPainted(false);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focused, int row, int column) {
            setText(String.valueOf(value));
            return this;
        }
    }

    private static class TableActionEditor extends DefaultCellEditor implements TableCellEditor {
        private final JButton button = new JButton();
        private final IntConsumer action;
        private int row;

        TableActionEditor(JCheckBox checkBox, IntConsumer action) {
            super(checkBox);
            this.action = action;
            button.setFont(new Font("SansSerif", Font.BOLD, 10));
            button.setForeground(DEEP_GREEN);
            button.setBackground(new Color(232, 242, 236));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setFocusPainted(false);
            button.addActionListener(event -> {
                fireEditingStopped();
                action.accept(row);
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean selected,
                                                     int row, int column) {
            this.row = table.convertRowIndexToModel(row);
            button.setText(String.valueOf(value));
            return button;
        }

        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    private static class SubjectRowData {
        String code;
        String title;
        int units;

        SubjectRowData(String code, String title, int units) {
            this.code = code;
            this.title = title;
            this.units = units;
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
        enum Type { TRASH, BOOK, LAYERS, TUITION, INFO_CIRCLE, CLOCK, SEARCH, PLUS_SQUARE, SUMMARY_DOC, BELL, USER_AVATAR }

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
                case TRASH -> {
                    g.drawLine(x + 4, y + 5, x + 14, y + 5);
                    g.drawLine(x + 7, y + 3, x + 11, y + 3);
                    g.drawRect(x + 5, y + 5, 8, 9);
                    g.drawLine(x + 7, y + 7, x + 7, y + 12);
                    g.drawLine(x + 11, y + 7, x + 11, y + 12);
                }
                case BOOK -> {
                    g.drawRect(x + 3, y + 3, 12, 12);
                    g.drawLine(x + 3, y + 7, x + 15, y + 7);
                }
                case LAYERS -> {
                    g.drawRect(x + 3, y + 3, 12, 4);
                    g.drawRect(x + 3, y + 9, 12, 4);
                }
                case TUITION -> {
                    g.drawRect(x + 3, y + 5, 12, 9);
                    g.drawOval(x + 7, y + 7, 4, 4);
                }
                case INFO_CIRCLE -> {
                    g.drawOval(x + 2, y + 2, 14, 14);
                    g.drawLine(x + 9, y + 5, x + 9, y + 7);
                    g.drawLine(x + 9, y + 9, x + 9, y + 13);
                }
                case CLOCK -> {
                    g.drawOval(x + 2, y + 2, 14, 14);
                    g.drawLine(x + 9, y + 5, x + 9, y + 9);
                    g.drawLine(x + 9, y + 9, x + 12, y + 9);
                }
                case SEARCH -> {
                    g.drawOval(x + 3, y + 3, 9, 9);
                    g.drawLine(x + 10, y + 10, x + 15, y + 15);
                }
                case PLUS_SQUARE -> {
                    g.fillRect(x + 2, y + 2, 14, 14);
                    g.setColor(Color.WHITE);
                    g.drawLine(x + 9, y + 5, x + 9, y + 13);
                    g.drawLine(x + 5, y + 9, x + 13, y + 9);
                }
                case SUMMARY_DOC -> {
                    g.drawRoundRect(x + 4, y + 2, 10, 14, 2, 2);
                    g.drawLine(x + 7, y + 6, x + 11, y + 6);
                    g.drawLine(x + 7, y + 10, x + 11, y + 10);
                }
                case BELL -> {
                    g.drawArc(x + 4, y + 2, 10, 10, 0, 180);
                    g.drawLine(x + 3, y + 12, x + 15, y + 12);
                    g.drawOval(x + 8, y + 13, 2, 2);
                }
                case USER_AVATAR -> {
                    g.drawOval(x + 5, y + 2, 8, 8);
                    g.drawArc(x + 2, y + 9, 14, 8, 0, 180);
                }
            }
            g.dispose();
        }
    }
}
