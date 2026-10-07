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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CashierTransacFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | Cashier - Transactions History");
    private final Cashier cashier;
    private final List<TransactionRowData> transactionsData = new ArrayList<>();
    private DefaultTableModel transactionModel;
    private JTable transactionTable;
    private TableRowSorter<DefaultTableModel> transactionSorter;
    private JTextField searchField;
    private JComboBox<String> dateFilter;
    private JComboBox<String> methodFilter;
    private JComboBox<String> statusFilter;
    private JLabel paginationInfo;
    private int currentPage = 1;
    private int filteredCount;
    private static final int PAGE_SIZE = 10;

    public CashierTransacFrame() {
        this(null);
    }

    public CashierTransacFrame(Cashier cashier) {
        this.cashier = cashier;
        initMockTransactions();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1160, 780));
        window.setSize(1360, 920);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private void initMockTransactions() {
        transactionsData.add(new TransactionRowData("2:42 PM", "Angela D. Cruz", "2024-0187", "OR-261005-126", "GCash", "₱12,500", "Paid"));
        transactionsData.add(new TransactionRowData("2:36 PM", "Marco Villanueva", "2025-0042", "OR-261005-125", "Cash", "₱8,000", "Paid"));
        transactionsData.add(new TransactionRowData("2:28 PM", "Kyla Mae Reyes", "2023-0931", "OR-261005-124", "Card", "₱21,750", "Partial"));
        transactionsData.add(new TransactionRowData("2:17 PM", "Noel P. Garcia", "2025-0114", "OR-261005-123", "Bank", "₱15,200", "Pending"));
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
        navigation.add(createNavigationButton("Transaction", IconType.RECORDS, true));
        navigation.add(createNavigationButton("Student Accounts", IconType.PROFILE, false));
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
        JButton button = new JButton(text, new CashierIcon(iconType, active ? GOLD : new Color(202, 219, 211)));
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
        searchField.setForeground(TEXT);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(350, 24));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { resetAndFilter(); }
            public void removeUpdate(DocumentEvent event) { resetAndFilter(); }
            public void changedUpdate(DocumentEvent event) { resetAndFilter(); }
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
        JLabel userName = new JLabel(cashier != null ? cashier.getName() : "Maria Santos");
        userName.setFont(new Font("SansSerif", Font.BOLD, 12));
        userName.setForeground(TEXT);
        JLabel userSub = new JLabel("Cashier - Counter 03");
        userSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        userSub.setForeground(MUTED);
        userText.add(userName);
        userText.add(userSub);
        userBadge.add(avatar);
        userBadge.add(userText);

        rightControls.add(userBadge);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        JLabel heading = new JLabel("Transactions Masterlist");
        heading.setFont(new Font("Serif", Font.BOLD, 26));
        heading.setForeground(DEEP_GREEN);

        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel filterWrapper = new JPanel(new BorderLayout(10, 0));
        filterWrapper.setOpaque(false);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        filterLabel.setForeground(TEXT);
        filters.add(filterLabel);
        dateFilter = createFilterCombo(new String[]{"Date: All", "Date: Today"});
        methodFilter = createFilterCombo(new String[]{"Method: All", "Method: Cash", "Method: GCash", "Method: Card", "Method: Bank"});
        statusFilter = createFilterCombo(new String[]{"Status: All", "Status: Paid", "Status: Partial", "Status: Pending", "Status: Voided"});
        filters.add(dateFilter); filters.add(methodFilter); filters.add(statusFilter);
        filterWrapper.add(filters, BorderLayout.WEST);

        JButton export = new JButton("Export !");
        export.setFocusPainted(false);
        export.setForeground(DEEP_GREEN);
        export.addActionListener(event -> exportTransactions());
        filterWrapper.add(export, BorderLayout.EAST);
        card.add(filterWrapper, BorderLayout.NORTH);

        transactionModel = new DefaultTableModel(new Object[]{"TIME", "STUDENT", "STUDENT ID", "RECEIPT NO.", "METHOD", "AMOUNT", "STATUS", "ACTION"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (TransactionRowData data : transactionsData) {
            transactionModel.addRow(new Object[]{data.time, data.studentName, data.studentId, data.receiptNo, data.method, data.amount, data.status, "..."});
        }
        transactionTable = new JTable(transactionModel);
        transactionSorter = new TableRowSorter<>(transactionModel);
        transactionTable.setRowSorter(transactionSorter);
        transactionTable.setRowHeight(42);
        transactionTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        transactionTable.setGridColor(new Color(242, 243, 240));
        transactionTable.setShowVerticalLines(false);
        transactionTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        transactionTable.getColumnModel().getColumn(7).setPreferredWidth(55);
        transactionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent event) { showActionMenu(event); }
            public void mouseReleased(java.awt.event.MouseEvent event) { showActionMenu(event); }
        });
        card.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        paginationInfo = new JLabel();
        paginationInfo.setForeground(MUTED);
        footer.add(paginationInfo, BorderLayout.WEST);
        JPanel pages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pages.setOpaque(false);
        JButton previous = new JButton("<");
        JButton next = new JButton(">");
        previous.addActionListener(event -> changePage(-1));
        next.addActionListener(event -> changePage(1));
        pages.add(previous); pages.add(next);
        footer.add(pages, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);
        body.add(heading, BorderLayout.NORTH);
        body.add(card, BorderLayout.CENTER);
        dateFilter.addActionListener(event -> resetAndFilter());
        methodFilter.addActionListener(event -> resetAndFilter());
        statusFilter.addActionListener(event -> resetAndFilter());
        rebuildTransactionFilter();
        return body;
    }

    private JPanel createLegacyBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        JLabel heading = new JLabel("Transactions Masterlist");
        heading.setFont(new Font("Serif", Font.BOLD, 26));
        heading.setForeground(DEEP_GREEN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(heading);
        body.add(Box.createVerticalStrut(16));

        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        filterLabel.setForeground(TEXT);
        filterBar.add(filterLabel);

        filterBar.add(createDropdownFilter("Date: Today ▾"));
        filterBar.add(createDropdownFilter("Method: All ▾"));
        filterBar.add(createDropdownFilter("Status: All ▾"));

        // Push Export button to the right side of the filter bar using a border layout
        JPanel filterWrapper = new JPanel(new BorderLayout());
        filterWrapper.setOpaque(false);
        filterWrapper.add(filterBar, BorderLayout.WEST);

        JButton exportBtn = new JButton("Export !");
        exportBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        exportBtn.setForeground(DEEP_GREEN);
        exportBtn.setBackground(new Color(235, 242, 233));
        exportBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 195), 1, true),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(e -> JOptionPane.showMessageDialog(window, "Exporting transaction logs...", "REY SIS", JOptionPane.INFORMATION_MESSAGE));
        filterWrapper.add(exportBtn, BorderLayout.EAST);

        card.add(filterWrapper, BorderLayout.NORTH);

        // Masterlist Table
        JPanel tableContainer = new JPanel();
        tableContainer.setOpaque(false);
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));

        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(8, 8, 10, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addTableHeaderCell(tableHeader, "TIME", 0, 0.15, gbc);
        addTableHeaderCell(tableHeader, "STUDENT", 1, 0.25, gbc);
        addTableHeaderCell(tableHeader, "RECEIPT NO.", 2, 0.20, gbc);
        addTableHeaderCell(tableHeader, "METHOD", 3, 0.15, gbc);
        addTableHeaderCell(tableHeader, "AMOUNT", 4, 0.15, gbc);
        addTableHeaderCell(tableHeader, "STATUS", 5, 0.10, gbc);

        tableContainer.add(tableHeader);

        for (TransactionRowData rowData : transactionsData) {
            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(242, 243, 240)),
                    BorderFactory.createEmptyBorder(14, 8, 14, 8)
            ));

            JLabel cTime = new JLabel(rowData.time);
            cTime.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cTime.setForeground(MUTED);
            gbc.gridx = 0; gbc.weightx = 0.15; row.add(cTime, gbc);

            JPanel studentPanel = new JPanel();
            studentPanel.setOpaque(false);
            studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));
            JLabel nameLbl = new JLabel(rowData.studentName);
            nameLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            nameLbl.setForeground(TEXT);
            JLabel idLbl = new JLabel(rowData.studentId);
            idLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            idLbl.setForeground(MUTED);
            studentPanel.add(nameLbl);
            studentPanel.add(idLbl);
            gbc.gridx = 1; gbc.weightx = 0.25; row.add(studentPanel, gbc);

            JLabel cReceipt = new JLabel(rowData.receiptNo);
            cReceipt.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cReceipt.setForeground(TEXT);
            gbc.gridx = 2; gbc.weightx = 0.20; row.add(cReceipt, gbc);

            JLabel cMethod = new JLabel(rowData.method);
            cMethod.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cMethod.setForeground(TEXT);
            gbc.gridx = 3; gbc.weightx = 0.15; row.add(cMethod, gbc);

            JLabel cAmount = new JLabel(rowData.amount);
            cAmount.setFont(new Font("SansSerif", Font.BOLD, 12));
            cAmount.setForeground(TEXT);
            gbc.gridx = 4; gbc.weightx = 0.15; row.add(cAmount, gbc);

            JPanel statusActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            statusActionPanel.setOpaque(false);

            JLabel statusBadge = new JLabel(rowData.status, SwingConstants.CENTER);
            statusBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
            statusBadge.setOpaque(true);
            statusBadge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

            if (rowData.status.equals("Paid")) {
                statusBadge.setBackground(new Color(235, 245, 235));
                statusBadge.setForeground(new Color(34, 139, 34));
            } else if (rowData.status.equals("Partial")) {
                statusBadge.setBackground(new Color(254, 243, 226));
                statusBadge.setForeground(new Color(211, 140, 21));
            } else {
                statusBadge.setBackground(new Color(254, 243, 226));
                statusBadge.setForeground(new Color(218, 145, 33));
            }

            statusActionPanel.add(statusBadge);

            JLabel dots = new JLabel("⋮");
            dots.setFont(new Font("SansSerif", Font.BOLD, 16));
            dots.setForeground(MUTED);
            statusActionPanel.add(dots);

            gbc.gridx = 5; gbc.weightx = 0.10; row.add(statusActionPanel, gbc);

            tableContainer.add(row);
        }

        card.add(tableContainer, BorderLayout.CENTER);

        // Pagination Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JLabel paginationInfo = new JLabel("Showing 1 to 10 of 126 entries");
        paginationInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        paginationInfo.setForeground(MUTED);
        footer.add(paginationInfo, BorderLayout.WEST);

        JLabel paginationLinks = new JLabel("< 1 2 3 ... 13");
        paginationLinks.setFont(new Font("SansSerif", Font.BOLD, 11));
        paginationLinks.setForeground(TEXT);
        footer.add(paginationLinks, BorderLayout.EAST);

        card.add(footer, BorderLayout.SOUTH);

        body.add(card);
        return body;
    }

    private JComboBox<String> createFilterCombo(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        combo.setForeground(TEXT);
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        return combo;
    }

    private void resetAndFilter() {
        currentPage = 1;
        rebuildTransactionFilter();
    }

    private void rebuildTransactionFilter() {
        if (transactionSorter == null) return;
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (query.equals("search student id, name, or number...")) query = "";
        String date = dateFilter == null ? "Date: All" : String.valueOf(dateFilter.getSelectedItem());
        String method = methodFilter == null ? "Method: All" : String.valueOf(methodFilter.getSelectedItem());
        String status = statusFilter == null ? "Status: All" : String.valueOf(statusFilter.getSelectedItem());
        String search = query;
        filteredCount = 0;
        transactionSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String time = String.valueOf(entry.getValue(0));
                String student = String.valueOf(entry.getValue(1)).toLowerCase(Locale.ROOT);
                String id = String.valueOf(entry.getValue(2)).toLowerCase(Locale.ROOT);
                String receipt = String.valueOf(entry.getValue(3)).toLowerCase(Locale.ROOT);
                String rowMethod = String.valueOf(entry.getValue(4));
                String rowStatus = String.valueOf(entry.getValue(6));
                boolean matchesSearch = search.isEmpty() || student.contains(search) || id.contains(search) || receipt.contains(search);
                boolean matchesDate = !"Date: Today".equals(date) || !time.toLowerCase(Locale.ROOT).contains("yesterday");
                boolean matchesMethod = "Method: All".equals(method) || method.endsWith(rowMethod);
                boolean matchesStatus = "Status: All".equals(status) || status.endsWith(rowStatus);
                if (!matchesSearch || !matchesDate || !matchesMethod || !matchesStatus) return false;
                int matchIndex = filteredCount++;
                int first = (currentPage - 1) * PAGE_SIZE;
                return matchIndex >= first && matchIndex < first + PAGE_SIZE;
            }
        });
        updatePaginationInfo();
    }

    private void updatePaginationInfo() {
        if (paginationInfo == null) return;
        int start = filteredCount == 0 ? 0 : (currentPage - 1) * PAGE_SIZE + 1;
        int end = Math.min(currentPage * PAGE_SIZE, filteredCount);
        paginationInfo.setText("Showing " + start + " to " + end + " of " + filteredCount + " entries");
    }

    private void changePage(int direction) {
        int pageCount = Math.max(1, (filteredCount + PAGE_SIZE - 1) / PAGE_SIZE);
        currentPage = Math.max(1, Math.min(pageCount, currentPage + direction));
        rebuildTransactionFilter();
    }

    private TransactionRowData transactionAtViewRow(int viewRow) {
        int modelRow = transactionTable.convertRowIndexToModel(viewRow);
        return transactionsData.get(modelRow);
    }

    private void showActionMenu(java.awt.event.MouseEvent event) {
        if (transactionTable == null || !SwingUtilities.isLeftMouseButton(event) && !SwingUtilities.isRightMouseButton(event)) return;
        int row = transactionTable.rowAtPoint(event.getPoint());
        int column = transactionTable.columnAtPoint(event.getPoint());
        if (row < 0 || column != 7) return;
        transactionTable.setRowSelectionInterval(row, row);
        TransactionRowData transaction = transactionAtViewRow(row);
        JPopupMenu menu = new JPopupMenu();
        JMenuItem view = new JMenuItem("View Receipt");
        JMenuItem voidItem = new JMenuItem("Void Transaction");
        view.addActionListener(action -> showReceipt(transaction));
        voidItem.addActionListener(action -> voidTransaction(transaction));
        menu.add(view);
        menu.add(voidItem);
        menu.show(transactionTable, event.getX(), event.getY());
    }

    private void showReceipt(TransactionRowData transaction) {
        JTextArea receipt = new JTextArea("REY SIS UNIVERSITY\nOFFICIAL PAYMENT RECEIPT\n\n"
                + "Receipt No.: " + transaction.receiptNo + "\n"
                + "Student:     " + transaction.studentName + "\n"
                + "Student ID:  " + transaction.studentId + "\n"
                + "Payment:     " + transaction.amount + "\n"
                + "Method:      " + transaction.method + "\n"
                + "Status:      " + transaction.status + "\n"
                + "Time:        " + transaction.time);
        receipt.setEditable(false);
        receipt.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(window, new JScrollPane(receipt), "View Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void voidTransaction(TransactionRowData transaction) {
        int choice = JOptionPane.showConfirmDialog(window,
                "Void transaction " + transaction.receiptNo + "? This requires cashier override.",
                "Void Transaction", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            transaction.status = "Voided";
            int modelRow = transactionsData.indexOf(transaction);
            transactionModel.setValueAt("Voided", modelRow, 6);
            rebuildTransactionFilter();
            JOptionPane.showMessageDialog(window, "Transaction marked as Voided.", "Transaction Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportTransactions() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("rey-sis-transactions.csv"));
        if (chooser.showSaveDialog(window) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter writer = new PrintWriter(chooser.getSelectedFile(), StandardCharsets.UTF_8)) {
            writer.println("TIME,STUDENT,STUDENT ID,RECEIPT NO.,METHOD,AMOUNT,STATUS");
            for (int row = 0; row < transactionTable.getRowCount(); row++) {
                int modelRow = transactionTable.convertRowIndexToModel(row);
                TransactionRowData data = transactionsData.get(modelRow);
                writer.println(csv(data.time) + "," + csv(data.studentName) + "," + csv(data.studentId) + ","
                        + csv(data.receiptNo) + "," + csv(data.method) + "," + csv(data.amount) + "," + csv(data.status));
            }
            JOptionPane.showMessageDialog(window, "Transactions exported successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(window, "Unable to export transactions: " + exception.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private JPanel createDropdownFilter(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXT);
        p.add(lbl, BorderLayout.CENTER);
        return p;
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
        try (InputStream stream = CashierTransacFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static class TransactionRowData {
        String time;
        String studentName;
        String studentId;
        String receiptNo;
        String method;
        String amount;
        String status;

        TransactionRowData(String time, String studentName, String studentId, String receiptNo, String method, String amount, String status) {
            this.time = time;
            this.studentName = studentName;
            this.studentId = studentId;
            this.receiptNo = receiptNo;
            this.method = method;
            this.amount = amount;
            this.status = status;
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

    public static class VectorIcon implements Icon {
        public enum Type { SEARCH, USER_AVATAR }
        private final Type type;
        private final Color color;

        public VectorIcon(Type type, Color color) {
            this.type = type;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            if (type == Type.SEARCH) {
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x + 2, y + 2, 10, 10);
                g2.drawLine(x + 10, y + 10, x + 16, y + 16);
            } else if (type == Type.USER_AVATAR) {
                g2.fillOval(x + 4, y + 2, 10, 10);
                g2.fillArc(x, y + 14, 18, 12, 0, 180);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return 20; }
        @Override
        public int getIconHeight() { return 20; }
    }
    public enum IconType {
        DASHBOARD, ENROLLMENT, PROFILE, RECORDS, RECONCILIATION, REPORTS, SIGN_OUT
    }

    public static class CashierIcon implements javax.swing.Icon {
        private final IconType type;
        private final Color color;

        public CashierIcon(IconType type, Color color) {
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
