package org.example.ui.views.cashier;

import org.example.model.Cashier;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CashierStudentFrame {
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | Cashier - Student Ledger Accounts");
    private final Cashier cashier;
    private final List<StudentRowData> studentData = new ArrayList<>();
    private DefaultTableModel studentModel;
    private JTable studentTable;
    private TableRowSorter<DefaultTableModel> studentSorter;
    private JTextField searchField;

    public CashierStudentFrame() {
        this(null);
    }

    public CashierStudentFrame(Cashier cashier) {
        this.cashier = cashier;
        initMockStudents();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1160, 780));
        window.setSize(1360, 920);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private void initMockStudents() {
        studentData.add(new StudentRowData("2025-0011", "Justine Rivera", "BSIT - 3rd Year", "₱ 18,250.00", "Pending Dues", new Color(218, 145, 33)));
        studentData.add(new StudentRowData("2024-0187", "Angela D. Cruz", "BSBA - 3rd Year", "₱ 0.00", "Cleared", new Color(34, 139, 34)));
        studentData.add(new StudentRowData("2023-0744", "Lea Castillo", "BSED - 4th Year", "₱ 12,800.00", "Overdue", new Color(200, 50, 50)));
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

        // *** IMPORTANT: Change the 'true' flag below depending on which file you are in! ***
        navigation.add(createNavigationButton("Dashboard", IconType.DASHBOARD, false));
        navigation.add(createNavigationButton("Collect Payment", IconType.ENROLLMENT, false));
        navigation.add(createNavigationButton("Transaction", IconType.RECORDS, false));
        navigation.add(createNavigationButton("Student Accounts", IconType.PROFILE, true));
        navigation.add(createNavigationButton("Reconciliation", IconType.RECONCILIATION, false));
        navigation.add(createNavigationButton("Reports", IconType.REPORTS, false));
        sidebar.add(navigation, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        JButton signOut = createNavigationButton("Sign Out", IconType.SIGN_OUT, false);
        signOut.addActionListener(event -> {
            window.dispose();
            new LoginFrame().showWindow();
        });
        bottom.add(signOut, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavigationButton(String text, IconType iconType, boolean active) {
        JButton button = new JButton(text, new cashierIcon(iconType, active ? GOLD : new Color(202, 219, 211)));
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

        if (text.equals("Sign Out")) return button;

        button.addActionListener(event -> {
            if (active) return;
            window.dispose();

            // Safely grabs your session regardless of which frame you are jumping from
            Cashier session = null;
            try {
                java.lang.reflect.Field field = this.getClass().getDeclaredField("cashier");
                field.setAccessible(true);
                session = (Cashier) field.get(this);
            } catch (Exception ignored) {}

            switch (text) {
                case "Dashboard" -> new CashierDashboardFrame(session).showWindow();
                case "Collect Payment" -> new CashierCollectFrame(session).showWindow();
                case "Transaction" -> new CashierTransacFrame(session).showWindow();
                case "Student Accounts" -> new CashierStudentFrame(session).showWindow();
                case "Reconciliation" -> new CashierReconsilationFrame(session).showWindow();
                case "Reports" -> new CashierReportsFrame().showWindow();
            }
        });

        return button;
    }

    private JPanel createTopNavigation() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel searchIcon = new JLabel(new VectorIcon(VectorIcon.Type.SEARCH, MUTED));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        searchPanel.add(searchIcon, BorderLayout.WEST);

        searchField = new JTextField("Search student ID, name, OR number...");
        searchField.setForeground(MUTED);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(350, 24));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { applyStudentFilter(); }
            public void removeUpdate(DocumentEvent event) { applyStudentFilter(); }
            public void changedUpdate(DocumentEvent event) { applyStudentFilter(); }
        });

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search student ID, name, OR number...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search student ID, name, OR number...");
                    searchField.setForeground(MUTED);
                }
            }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(searchPanel);
        header.add(leftContainer, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightControls.setOpaque(false);

        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userBadge.setOpaque(false);
        JLabel avatar = new JLabel(new VectorIcon(VectorIcon.Type.USER_AVATAR, MUTED));
        JPanel userText = new JPanel();
        userText.setOpaque(false);
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(cashier != null ? cashier.getName() : "Head Cashier");
        userName.setFont(new Font("SansSerif", Font.BOLD, 12));
        userName.setForeground(TEXT);
        JLabel userSub = new JLabel("Finance Department · Cashier");
        userSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        userSub.setForeground(MUTED);
        userText.add(userName);
        userText.add(userSub);
        userBadge.add(avatar);
        userBadge.add(userText);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(PAGE);

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 16));
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        JLabel heading = new JLabel("Student Ledger Accounts");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(TEXT);

        JPanel topTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topTitlePanel.setOpaque(false);
        topTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        topTitlePanel.add(heading);

        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setOpaque(false);
        mainLayout.add(topTitlePanel, BorderLayout.NORTH);
        mainLayout.add(createStudentTableCard(), BorderLayout.CENTER);

        contentWrapper.add(mainLayout, BorderLayout.CENTER);
        body.add(contentWrapper);

        return body;
    }

    private JPanel createStudentTableCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        studentModel = new DefaultTableModel(new Object[]{"STUDENT ID", "NAME", "PROGRAM / YEAR", "OUTSTANDING", "ACCOUNT STATUS"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (StudentRowData data : studentData) {
            studentModel.addRow(new Object[]{data.studentId, data.name, data.programYr, data.outstanding, data.status});
        }
        studentTable = new JTable(studentModel);
        studentSorter = new TableRowSorter<>(studentModel);
        studentTable.setRowSorter(studentSorter);
        studentTable.setRowHeight(46);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        studentTable.setGridColor(new Color(242, 243, 240));
        studentTable.setShowVerticalLines(false);
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2 && studentTable.getSelectedRow() >= 0) showLedger(studentAtSelectedRow());
            }
        });
        card.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return card;
    }

    private StudentRowData studentAtSelectedRow() {
        int modelRow = studentTable.convertRowIndexToModel(studentTable.getSelectedRow());
        return studentData.get(modelRow);
    }

    private void applyStudentFilter() {
        if (studentSorter == null) return;
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (query.equals("search student id, name, or number...")) query = "";
        String search = query;
        studentSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                return search.isEmpty()
                        || String.valueOf(entry.getValue(0)).toLowerCase(Locale.ROOT).contains(search)
                        || String.valueOf(entry.getValue(1)).toLowerCase(Locale.ROOT).contains(search)
                        || String.valueOf(entry.getValue(2)).toLowerCase(Locale.ROOT).contains(search);
            }
        });
    }

    private void showLedger(StudentRowData student) {
        JDialog dialog = new JDialog(window, "Student Ledger Details", true);
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        JLabel header = new JLabel("Student Ledger Details");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(DARK_GREEN);
        content.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JPanel info = new JPanel(new GridLayout(0, 2, 8, 6));
        info.setBorder(BorderFactory.createTitledBorder("Student Information"));
        info.add(new JLabel("Student ID")); info.add(new JLabel(student.studentId));
        info.add(new JLabel("Full Name")); info.add(new JLabel(student.name));
        info.add(new JLabel("Program & Year")); info.add(new JLabel(student.programYr));
        info.add(new JLabel("Account Status")); info.add(new JLabel(student.status));
        center.add(info);
        center.add(Box.createVerticalStrut(10));

        JLabel feesTitle = new JLabel("Itemized Fee History");
        feesTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        center.add(feesTitle);
        DefaultTableModel fees = new DefaultTableModel(new Object[]{"FEE DESCRIPTION", "ASSESSMENT DATE", "ORIGINAL", "PAID", "BALANCE"}, 0);
        double balance = parseAmount(student.outstanding);
        fees.addRow(new Object[]{"Tuition Fee - 1st Semester", "Oct 01, 2026", formatAmount(balance + 12500), formatAmount(12500), formatAmount(balance)});
        fees.addRow(new Object[]{"Miscellaneous Fees", "Oct 01, 2026", formatAmount(4500), formatAmount(student.status.equals("Cleared") ? 4500 : 0), formatAmount(student.status.equals("Cleared") ? 0 : 4500)});
        JTable feesTable = new JTable(fees);
        feesTable.setEnabled(false);
        center.add(new JScrollPane(feesTable));
        center.add(Box.createVerticalStrut(10));

        JLabel transactionsTitle = new JLabel("Transaction Log");
        transactionsTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        center.add(transactionsTitle);
        DefaultTableModel transactions = new DefaultTableModel(new Object[]{"DATE", "OR NUMBER", "METHOD", "AMOUNT PAID"}, 0);
        transactions.addRow(new Object[]{"Oct 05, 2026", "OR-261005-126", "GCash", formatAmount(12500)});
        transactions.addRow(new Object[]{"Sep 20, 2026", "OR-260920-102", "Cash", formatAmount(5000)});
        JTable transactionTable = new JTable(transactions);
        transactionTable.setEnabled(false);
        center.add(new JScrollPane(transactionTable));
        content.add(center, BorderLayout.CENTER);

        JButton process = new JButton("Process Payment");
        JButton print = new JButton("Print Statement of Account");
        process.setBackground(DARK_GREEN); process.setForeground(Color.WHITE); process.setFocusPainted(false);
        process.addActionListener(event -> {
            dialog.dispose();
            window.dispose();
            CashierCollectFrame collect = new CashierCollectFrame(cashier);
            collect.selectStudentById(student.studentId);
            collect.showWindow();
        });
        print.addActionListener(event -> printStatement(student));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(print); actions.add(process);
        content.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.setSize(760, 650);
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    private void printStatement(StudentRowData student) {
        JTextArea statement = new JTextArea("REY SIS UNIVERSITY\nSTATEMENT OF ACCOUNT\n\n"
                + "Student: " + student.name + "\nStudent ID: " + student.studentId + "\n"
                + "Program: " + student.programYr + "\n\nOutstanding Balance: " + student.outstanding);
        statement.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statement.setEditable(false);
        try {
            statement.print();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(window, "Unable to print statement: " + exception.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseAmount(String value) {
        String numeric = value.replaceAll("[^0-9.,]", "").replace(",", "");
        return numeric.isEmpty() ? 0 : Double.parseDouble(numeric);
    }

    private String formatAmount(double value) {
        return String.format(Locale.US, "₱ %,.2f", value);
    }

    private JPanel createLegacyStudentTableCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel tableContainer = new JPanel();
        tableContainer.setOpaque(false);
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));

        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(8, 12, 12, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addTableHeaderCell(tableHeader, "STUDENT ID", 0, 0.22, gbc);
        addTableHeaderCell(tableHeader, "NAME", 1, 0.28, gbc);
        addTableHeaderCell(tableHeader, "PROGRAM/YR", 2, 0.22, gbc);
        addTableHeaderCell(tableHeader, "OUTSTANDING", 3, 0.16, gbc);
        addTableHeaderCell(tableHeader, "ACCOUNT STATUS", 4, 0.12, gbc);

        tableContainer.add(tableHeader);

        for (int i = 0; i < studentData.size(); i++) {
            StudentRowData data = studentData.get(i);
            boolean isSelectedRow = (i == 1); // Highlights the second row matching wireframe selection

            JPanel row = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    if (isSelectedRow) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(new Color(230, 243, 255));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };
            row.setOpaque(isSelectedRow);
            row.setBorder(BorderFactory.createCompoundBorder(
                    isSelectedRow ? BorderFactory.createLineBorder(new Color(51, 153, 255), 1) : BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(242, 243, 240)),
                    BorderFactory.createEmptyBorder(14, 12, 14, 12)
            ));

            JLabel cId = new JLabel(data.studentId);
            cId.setFont(new Font("SansSerif", Font.BOLD, 12));
            cId.setForeground(TEXT);
            gbc.gridx = 0; gbc.weightx = 0.22; row.add(cId, gbc);

            JLabel cName = new JLabel(data.name);
            cName.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cName.setForeground(TEXT);
            gbc.gridx = 1; gbc.weightx = 0.28; row.add(cName, gbc);

            JLabel cProg = new JLabel(data.programYr);
            cProg.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cProg.setForeground(MUTED);
            gbc.gridx = 2; gbc.weightx = 0.22; row.add(cProg, gbc);

            JLabel cOut = new JLabel(data.outstanding);
            cOut.setFont(new Font("SansSerif", Font.BOLD, 12));
            cOut.setForeground(TEXT);
            gbc.gridx = 3; gbc.weightx = 0.16; row.add(cOut, gbc);

            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            statusPanel.setOpaque(false);
            JLabel statusDot = new JLabel("\u2022");
            statusDot.setFont(new Font("SansSerif", Font.BOLD, 16));
            statusDot.setForeground(data.statusColor);
            JLabel cStatus = new JLabel(data.status);
            cStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cStatus.setForeground(TEXT);
            statusPanel.add(statusDot);
            statusPanel.add(cStatus);

            gbc.gridx = 4; gbc.weightx = 0.12; row.add(statusPanel, gbc);

            tableContainer.add(row);
        }

        card.add(tableContainer, BorderLayout.CENTER);
        return card;
    }

    private void addTableHeaderCell(JPanel header, String text, int gridx, double weightx, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MUTED);
        gbc.gridx = gridx;
        gbc.weightx = weightx;
        header.add(label, gbc);
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = CashierStudentFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static class StudentRowData {
        String studentId;
        String name;
        String programYr;
        String outstanding;
        String status;
        Color statusColor;

        StudentRowData(String studentId, String name, String programYr, String outstanding, String status, Color statusColor) {
            this.studentId = studentId;
            this.name = name;
            this.programYr = programYr;
            this.outstanding = outstanding;
            this.status = status;
            this.statusColor = statusColor;
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



    static class CashierIcon implements javax.swing.Icon {
        private final IconType type;
        private final Color color;

        CashierIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }
        public enum IconType {
            DASHBOARD, ENROLLMENT, PROFILE, RECORDS, RECONCILIATION, REPORTS, SIGN_OUT
        }

        public int getIconWidth() { return 19; }
        public int getIconHeight() { return 19; }

        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (type) {
                case DASHBOARD -> {
                    g.fillRect(x + 2, y + 8, 5, 8);
                    g.fillRect(x + 10, y + 4, 5, 12);
                    g.drawRect(x + 2, y + 8, 5, 8);
                    g.drawRect(x + 10, y + 4, 5, 12);
                }
                case ENROLLMENT -> {
                    g.drawRect(x + 2, y + 3, 15, 13);
                    g.drawLine(x + 5, y + 7, x + 14, y + 7);
                    g.drawLine(x + 5, y + 11, x + 11, y + 11);
                }
                case PROFILE -> {
                    g.drawOval(x + 6, y + 3, 6, 6);
                    g.drawArc(x + 3, y + 11, 12, 6, 0, 180);
                }
                case RECORDS -> {
                    g.drawRect(x + 3, y + 2, 12, 15);
                    g.drawLine(x + 6, y + 6, x + 12, y + 6);
                    g.drawLine(x + 6, y + 10, x + 12, y + 10);
                }
                case RECONCILIATION -> {
                    g.drawRect(x + 2, y + 2, 14, 14);
                    g.drawLine(x + 6, y + 10, x + 9, y + 7);
                    g.drawLine(x + 9, y + 7, x + 13, y + 11);
                }
                case REPORTS -> {
                    g.drawArc(x + 4, y + 3, 10, 10, 0, 180);
                    g.drawLine(x + 2, y + 13, x + 16, y + 13);
                    g.drawOval(x + 8, y + 13, 2, 2);
                }
                case SIGN_OUT -> {
                    g.drawLine(x + 8, y + 4, x + 3, y + 4);
                    g.drawLine(x + 3, y + 4, x + 3, y + 14);
                    g.drawLine(x + 3, y + 14, x + 8, y + 14);
                    g.drawLine(x + 10, y + 9, x + 15, y + 9);
                    g.drawLine(x + 13, y + 6, x + 16, y + 9);
                    g.drawLine(x + 13, y + 12, x + 16, y + 9);
                }
            }
            g.dispose();
        }
    }

    private static class VectorIcon implements javax.swing.Icon {
        enum Type { SEARCH, USER_AVATAR }

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
                case USER_AVATAR -> {
                    g.drawOval(x + 5, y + 2, 8, 8);
                    g.drawArc(x + 2, y + 9, 14, 8, 0, 180);
                }
            }
            g.dispose();
        }
    }

    public enum IconType {
        DASHBOARD, ENROLLMENT, PROFILE, RECORDS, RECONCILIATION, REPORTS, SIGN_OUT
    }

    public static class cashierIcon implements javax.swing.Icon {
        private final IconType type;
        private final Color color;

        public cashierIcon(IconType type, Color color) {
            this.type = type;
            this.color = color;
        }

        public int getIconWidth() { return 19; }
        public int getIconHeight() { return 19; }

        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (type) {
                case DASHBOARD -> {
                    g.fillRect(x + 2, y + 8, 5, 8);
                    g.fillRect(x + 10, y + 4, 5, 12);
                    g.drawRect(x + 2, y + 8, 5, 8);
                    g.drawRect(x + 10, y + 4, 5, 12);
                }
                case ENROLLMENT -> { // Collect Payment (Credit Card)
                    g.drawRect(x + 2, y + 3, 15, 13);
                    g.drawLine(x + 5, y + 7, x + 14, y + 7);
                    g.drawLine(x + 5, y + 11, x + 11, y + 11);
                }
                case RECORDS -> { // Transaction (Receipt)
                    g.drawRect(x + 3, y + 2, 12, 15);
                    g.drawLine(x + 6, y + 6, x + 12, y + 6);
                    g.drawLine(x + 6, y + 10, x + 12, y + 10);
                }
                case PROFILE -> { // Student Accounts (User)
                    g.drawOval(x + 6, y + 3, 6, 6);
                    g.drawArc(x + 3, y + 11, 12, 6, 0, 180);
                }
                case RECONCILIATION -> { // Scales
                    g.drawLine(x + 9, y + 2, x + 9, y + 14);
                    g.drawLine(x + 5, y + 14, x + 13, y + 14);
                    g.drawLine(x + 3, y + 5, x + 15, y + 5);
                    g.drawLine(x + 3, y + 5, x + 1, y + 9);
                    g.drawLine(x + 3, y + 5, x + 5, y + 9);
                    g.drawLine(x + 1, y + 9, x + 5, y + 9);
                    g.drawLine(x + 15, y + 5, x + 13, y + 9);
                    g.drawLine(x + 15, y + 5, x + 17, y + 9);
                    g.drawLine(x + 13, y + 9, x + 17, y + 9);
                }
                case REPORTS -> { // Bar Chart
                    g.drawRect(x + 2, y + 8, 3, 6);
                    g.drawRect(x + 7, y + 4, 3, 10);
                    g.drawRect(x + 12, y + 2, 3, 12);
                }
                case SIGN_OUT -> {
                    g.drawLine(x + 8, y + 4, x + 3, y + 4);
                    g.drawLine(x + 3, y + 4, x + 3, y + 14);
                    g.drawLine(x + 3, y + 14, x + 8, y + 14);
                    g.drawLine(x + 10, y + 9, x + 15, y + 9);
                    g.drawLine(x + 13, y + 6, x + 16, y + 9);
                    g.drawLine(x + 13, y + 12, x + 16, y + 9);
                }
            }
            g.dispose();
        }
    }
}
