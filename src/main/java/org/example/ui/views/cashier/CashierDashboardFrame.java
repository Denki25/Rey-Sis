package org.example.ui.views.cashier;

import org.example.model.Cashier;
import org.example.data.PaymentRepository;
import org.example.service.CashierPaymentService;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CashierDashboardFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | Cashier Dashboard");
    private final Cashier cashier;
    private final CashierPaymentService paymentService = new CashierPaymentService();
    private final List<PaymentRowData> paymentsData = new ArrayList<>();
    private DefaultTableModel paymentModel;
    private JTable paymentTable;
    private TableRowSorter<DefaultTableModel> paymentSorter;
    private JComboBox<String> transactionFilter;
    private JTextField searchField;
    private JLabel todaysCollectionsValue;
    private JLabel pendingVerificationsValue;
    private JLabel totalCollectedValue;

    public CashierDashboardFrame(Cashier cashier) {
        this.cashier = cashier;
        initMockPayments();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1160, 780));
        window.setSize(1360, 920);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private void initMockPayments() {
        try {
            for (PaymentRepository.PaymentRecord payment : paymentService.findRecent()) {
                paymentsData.add(new PaymentRowData(payment.referenceNumber(), payment.studentName(), payment.programId(),
                        formatAmount(payment.amount()), payment.paymentType(), payment.status(), payment.paymentDate().toString()));
            }
        } catch (SQLException | SecurityException exception) {
            JOptionPane.showMessageDialog(window, "Unable to load payment records. Run the payments SQL migration first.",
                    "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
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

        navigation.add(createNavigationButton("Dashboard", IconType.DASHBOARD, true));
        navigation.add(createNavigationButton("Collect Payment", IconType.ENROLLMENT, false));
        navigation.add(createNavigationButton("Transaction", IconType.RECORDS, false));
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

        searchField = new JTextField("Search student name, ID, or reference number...");
        searchField.setForeground(MUTED);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(350, 24));

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search student name, ID, or reference number...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search student name, ID, or reference number...");
                    searchField.setForeground(MUTED);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { applyPaymentFilters(); }
            public void removeUpdate(DocumentEvent event) { applyPaymentFilters(); }
            public void changedUpdate(DocumentEvent event) { applyPaymentFilters(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(searchPanel);
        header.add(leftContainer, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightControls.setOpaque(false);

        JPanel bellPanel = new JPanel(new BorderLayout());
        bellPanel.setOpaque(false);
        JLabel bellIcon = new JLabel(new VectorIcon(VectorIcon.Type.BELL, MUTED));
        JLabel badge = new JLabel("2", SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 9));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(GOLD);
        badge.setPreferredSize(new Dimension(14, 14));
        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(badge);
        bellPanel.add(badgeWrapper, BorderLayout.NORTH);
        bellPanel.add(bellIcon, BorderLayout.CENTER);
        rightControls.add(bellPanel);

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

        JLabel chevron = new JLabel(" \u2304 ");
        chevron.setForeground(MUTED);
        userBadge.add(chevron);
        rightControls.add(userBadge);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(PAGE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 28, 20, 28));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Cashier Dashboard");
        heading.setFont(new Font("Serif", Font.BOLD, 28));
        heading.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Monitor student payments, verify transactions, and review financial logs.");
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

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 16));
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(16, 28, 24, 28));

        JPanel summaryCardsContainer = new JPanel(new GridLayout(1, 4, 16, 0));
        summaryCardsContainer.setOpaque(false);
        summaryCardsContainer.add(createSummaryBlock(formatAmount(calculateTodaysCollections()), "Today's Collections", new Color(235, 242, 233)));
        summaryCardsContainer.add(createSummaryBlock(String.valueOf(countPendingPayments()), "Pending Verifications", new Color(248, 237, 219)));
        summaryCardsContainer.add(createSummaryButtonBlock("Verify Payment", "Action Required", new Color(235, 242, 233)));
        summaryCardsContainer.add(createSummaryBlock(formatAmount(calculateMonthCollected()), "Total Collected (Month)", new Color(248, 237, 219)));

        contentWrapper.add(summaryCardsContainer, BorderLayout.NORTH);

        JPanel tablesAndSummary = new JPanel();
        tablesAndSummary.setLayout(new BoxLayout(tablesAndSummary, BoxLayout.Y_AXIS));
        tablesAndSummary.setOpaque(false);
        tablesAndSummary.add(Box.createVerticalStrut(10));
        tablesAndSummary.add(createDynamicPaymentsTableCard());

        contentWrapper.add(tablesAndSummary, BorderLayout.CENTER);

        body.add(titlePanel);
        body.add(contentWrapper);

        return body;
    }

    private JPanel createSummaryBlock(String value, String label, Color bgColor) {
        JPanel block = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        valLabel.setForeground(TEXT);
        valLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if ("Today's Collections".equals(label)) {
            todaysCollectionsValue = valLabel;
        } else if ("Pending Verifications".equals(label)) {
            pendingVerificationsValue = valLabel;
        } else if ("Total Collected (Month)".equals(label)) {
            totalCollectedValue = valLabel;
        }

        JLabel subLabel = new JLabel(label);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLabel.setForeground(MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        block.add(valLabel);
        block.add(Box.createVerticalStrut(6));
        block.add(subLabel);

        return block;
    }

    private JPanel createSummaryButtonBlock(String titleButton, String label, Color bgColor) {
        JPanel block = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton actionBtn = new JButton(titleButton);
        actionBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setBackground(DEEP_GREEN);
        actionBtn.setFocusPainted(false);
        actionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionBtn.addActionListener(e -> verifySelectedPayment());

        JLabel subLabel = new JLabel(label);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLabel.setForeground(MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        block.add(actionBtn);
        block.add(Box.createVerticalStrut(8));
        block.add(subLabel);

        return block;
    }

    private JPanel createDynamicPaymentsTableCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titleGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleGroup.setOpaque(false);
        titleGroup.add(new JLabel(new VectorIcon(VectorIcon.Type.BAR_CHART, DEEP_GREEN)));
        JLabel title = new JLabel("Recent Student Transactions");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(DEEP_GREEN);
        titleGroup.add(title);
        header.add(titleGroup, BorderLayout.WEST);

        transactionFilter = new JComboBox<>(new String[]{"All Transactions", "Verified", "Pending"});
        transactionFilter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        transactionFilter.setForeground(TEXT);
        transactionFilter.setBackground(Color.WHITE);
        transactionFilter.setFocusable(false);
        transactionFilter.addActionListener(event -> applyPaymentFilters());
        header.add(transactionFilter, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        paymentModel = new DefaultTableModel(new Object[]{"REF NO.", "STUDENT NAME", "PROGRAM / ID", "AMOUNT", "FEE TYPE", "STATUS", "TIMESTAMP"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshPaymentModel();

        paymentTable = new JTable(paymentModel);
        paymentTable.setRowSorter(new TableRowSorter<>(paymentModel));
        paymentSorter = (TableRowSorter<DefaultTableModel>) paymentTable.getRowSorter();
        paymentTable.setRowHeight(42);
        paymentTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        paymentTable.setForeground(TEXT);
        paymentTable.setGridColor(new Color(242, 243, 240));
        paymentTable.setShowVerticalLines(false);
        paymentTable.setFillsViewportHeight(true);
        paymentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        paymentTable.getTableHeader().setForeground(MUTED);
        paymentTable.getTableHeader().setBackground(new Color(248, 249, 247));
        paymentTable.getTableHeader().setPreferredSize(new Dimension(0, 34));

        int[] widths = {85, 150, 135, 115, 125, 90, 135};
        for (int index = 0; index < widths.length; index++) {
            paymentTable.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        }
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                            boolean focused, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 11));
                label.setForeground("Verified".equals(value) ? new Color(34, 139, 34) :
                        "Pending".equals(value) ? new Color(218, 145, 33) : new Color(190, 65, 65));
                return label;
            }
        };
        paymentTable.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);
        paymentTable.getColumnModel().getColumn(3).setCellRenderer(centeredRenderer());
        paymentTable.getColumnModel().getColumn(6).setCellRenderer(centeredRenderer());
        paymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && paymentTable.getSelectedRow() >= 0) {
                    int modelRow = paymentTable.convertRowIndexToModel(paymentTable.getSelectedRow());
                    PaymentRowData payment = paymentsData.get(modelRow);
                    if ("Pending".equals(payment.status)) {
                        showPaymentVerificationDialog(payment);
                    }
                }
            }
        });

        card.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        applyPaymentFilters();
        return card;
    }

    private DefaultTableCellRenderer centeredRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        return renderer;
    }

    private void refreshPaymentModel() {
        if (paymentModel == null) return;
        paymentModel.setRowCount(0);
        for (PaymentRowData payment : paymentsData) {
            paymentModel.addRow(new Object[]{payment.refNo, payment.studentName, payment.programId,
                    formatAmount(parseAmount(payment.amount)), payment.feeType, payment.status, payment.timestamp});
        }
    }

    private void applyPaymentFilters() {
        if (paymentSorter == null) return;
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (query.equals("search student name, id, or reference number...")) query = "";
        String status = transactionFilter == null ? "All Transactions" : String.valueOf(transactionFilter.getSelectedItem());
        String search = query;
        paymentSorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                String ref = String.valueOf(entry.getValue(0)).toLowerCase(Locale.ROOT);
                String name = String.valueOf(entry.getValue(1)).toLowerCase(Locale.ROOT);
                String programId = String.valueOf(entry.getValue(2)).toLowerCase(Locale.ROOT);
                String rowStatus = String.valueOf(entry.getValue(5));
                boolean matchesSearch = search.isEmpty() || ref.contains(search) || name.contains(search) || programId.contains(search);
                boolean matchesStatus = "All Transactions".equals(status) || status.equals(rowStatus);
                return matchesSearch && matchesStatus;
            }
        });
    }

    private void verifySelectedPayment() {
        PaymentRowData payment = null;
        if (paymentTable != null && paymentTable.getSelectedRow() >= 0) {
            int modelRow = paymentTable.convertRowIndexToModel(paymentTable.getSelectedRow());
            payment = paymentsData.get(modelRow);
        }
        if (payment == null || !"Pending".equals(payment.status)) {
            for (PaymentRowData candidate : paymentsData) {
                if ("Pending".equals(candidate.status)) {
                    payment = candidate;
                    break;
                }
            }
        }
        if (payment == null) {
            JOptionPane.showMessageDialog(window, "There are no pending payments to verify.", "No Pending Payments", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showPaymentVerificationDialog(payment);
    }

    private void showPaymentVerificationDialog(PaymentRowData payment) {
        javax.swing.JDialog dialog = new javax.swing.JDialog(window, "Verify Payment", true);
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel heading = new JLabel("Payment Verification");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(DEEP_GREEN);
        content.add(heading, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridLayout(0, 2, 10, 10));
        details.add(new JLabel("Reference No.")); details.add(new JLabel(payment.refNo));
        details.add(new JLabel("Student")); details.add(new JLabel(payment.studentName));
        details.add(new JLabel("Program / ID")); details.add(new JLabel(payment.programId));
        details.add(new JLabel("Amount")); details.add(new JLabel(formatAmount(parseAmount(payment.amount))));
        details.add(new JLabel("Fee Type")); details.add(new JLabel(payment.feeType));
        details.add(new JLabel("Timestamp")); details.add(new JLabel(payment.timestamp));
        content.add(details, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton reject = new JButton("Reject");
        JButton approve = new JButton("Approve / Verify");
        approve.setBackground(DEEP_GREEN);
        approve.setForeground(Color.WHITE);
        approve.setFocusPainted(false);
        reject.addActionListener(event -> {
            payment.status = "Rejected";
            refreshPaymentModel();
            updatePaymentMetrics();
            applyPaymentFilters();
            dialog.dispose();
            JOptionPane.showMessageDialog(window, "Payment " + payment.refNo + " was rejected.", "Payment Rejected", JOptionPane.WARNING_MESSAGE);
        });
        approve.addActionListener(event -> {
            payment.status = "Verified";
            refreshPaymentModel();
            updatePaymentMetrics();
            applyPaymentFilters();
            dialog.dispose();
            JOptionPane.showMessageDialog(window,
                    "Payment verified successfully.\nReceipt ready to print: " + payment.refNo,
                    "Payment Verified", JOptionPane.INFORMATION_MESSAGE);
        });
        actions.add(reject);
        actions.add(approve);
        content.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.setSize(460, 340);
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    private void updatePaymentMetrics() {
        if (todaysCollectionsValue != null) todaysCollectionsValue.setText(formatAmount(calculateTodaysCollections()));
        if (pendingVerificationsValue != null) pendingVerificationsValue.setText(String.valueOf(countPendingPayments()));
        if (totalCollectedValue != null) totalCollectedValue.setText(formatAmount(calculateMonthCollected()));
    }

    private double calculateTodaysCollections() {
        double total = 0;
        for (PaymentRowData payment : paymentsData) {
            if ("Verified".equals(payment.status) && payment.timestamp.toLowerCase(Locale.ROOT).contains("today")) total += parseAmount(payment.amount);
        }
        return total;
    }

    private double calculateMonthCollected() {
        double total = 0;
        for (PaymentRowData payment : paymentsData) {
            if ("Verified".equals(payment.status)) total += parseAmount(payment.amount);
        }
        return total;
    }

    private int countPendingPayments() {
        int count = 0;
        for (PaymentRowData payment : paymentsData) if ("Pending".equals(payment.status)) count++;
        return count;
    }

    private double parseAmount(String amount) {
        String numeric = amount.replaceAll("[^0-9.,]", "").replace(",", "");
        return numeric.isEmpty() ? 0 : Double.parseDouble(numeric);
    }

    private String formatAmount(double amount) {
        return String.format(Locale.US, "₱ %,.2f", amount);
    }

    private JPanel createPaymentsTableCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleGrp.setOpaque(false);
        titleGrp.add(new JLabel(new VectorIcon(VectorIcon.Type.BAR_CHART, DEEP_GREEN)));
        JLabel title = new JLabel("Recent Student Transactions");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(DEEP_GREEN);
        titleGrp.add(title);
        header.add(titleGrp, BorderLayout.WEST);

        String[] filters = {"All Transactions", "Verified", "Pending"};
        JComboBox<String> filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        filterCombo.setForeground(TEXT);
        filterCombo.setBackground(Color.WHITE);
        filterCombo.setFocusable(false);
        filterCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        header.add(filterCombo, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        JPanel tableContainer = new JPanel();
        tableContainer.setOpaque(false);
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));

        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(8, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addTableHeaderCell(tableHeader, "Ref No.", 0, 0.12, gbc);
        addTableHeaderCell(tableHeader, "Student Name", 1, 0.20, gbc);
        addTableHeaderCell(tableHeader, "Program / ID", 2, 0.18, gbc);
        addTableHeaderCell(tableHeader, "Amount", 3, 0.15, gbc);
        addTableHeaderCell(tableHeader, "Fee Type", 4, 0.15, gbc);
        addTableHeaderCell(tableHeader, "Status", 5, 0.10, gbc, SwingConstants.CENTER);
        addTableHeaderCell(tableHeader, "Timestamp", 6, 0.10, gbc, SwingConstants.RIGHT);

        tableContainer.add(tableHeader);

        for (PaymentRowData data : paymentsData) {
            JPanel row = new JPanel(new GridBagLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(242, 243, 240)),
                    BorderFactory.createEmptyBorder(14, 12, 14, 12)
            ));

            JLabel cRef = new JLabel(data.refNo);
            cRef.setFont(new Font("SansSerif", Font.BOLD, 12));
            cRef.setForeground(DEEP_GREEN);
            gbc.gridx = 0; gbc.weightx = 0.12; row.add(cRef, gbc);

            JLabel cName = new JLabel(data.studentName);
            cName.setFont(new Font("SansSerif", Font.BOLD, 12));
            cName.setForeground(TEXT);
            gbc.gridx = 1; gbc.weightx = 0.20; row.add(cName, gbc);

            JLabel cProgId = new JLabel(data.programId);
            cProgId.setFont(new Font("SansSerif", Font.PLAIN, 11));
            cProgId.setForeground(MUTED);
            gbc.gridx = 2; gbc.weightx = 0.18; row.add(cProgId, gbc);

            JLabel cAmount = new JLabel(data.amount);
            cAmount.setFont(new Font("SansSerif", Font.BOLD, 12));
            cAmount.setForeground(TEXT);
            gbc.gridx = 3; gbc.weightx = 0.15; row.add(cAmount, gbc);

            JLabel cFeeType = new JLabel(data.feeType);
            cFeeType.setFont(new Font("SansSerif", Font.PLAIN, 11));
            cFeeType.setForeground(TEXT);
            gbc.gridx = 4; gbc.weightx = 0.15; row.add(cFeeType, gbc);

            JLabel cStatus = new JLabel(data.status, SwingConstants.CENTER);
            cStatus.setFont(new Font("SansSerif", Font.BOLD, 11));
            cStatus.setForeground(data.status.equals("Verified") ? new Color(34, 139, 34) : new Color(218, 145, 33));
            gbc.gridx = 5; gbc.weightx = 0.10; row.add(cStatus, gbc);

            JLabel cTime = new JLabel(data.timestamp, SwingConstants.RIGHT);
            cTime.setFont(new Font("SansSerif", Font.PLAIN, 11));
            cTime.setForeground(MUTED);
            gbc.gridx = 6; gbc.weightx = 0.10; row.add(cTime, gbc);

            tableContainer.add(row);
        }

        card.add(tableContainer, BorderLayout.CENTER);
        return card;
    }

    private void addTableHeaderCell(JPanel header, String text, int gridx, double weightx, GridBagConstraints gbc) {
        addTableHeaderCell(header, text, gridx, weightx, gbc, SwingConstants.LEFT);
    }

    private void addTableHeaderCell(JPanel header, String text, int gridx, double weightx, GridBagConstraints gbc, int alignment) {
        JLabel label = new JLabel(text, alignment);
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

    private void showDashboardMessage() {
        JOptionPane.showMessageDialog(window, "Cashier Dashboard is already open.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showComingSoon(String module) {
        JOptionPane.showMessageDialog(window, module + " is coming next.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = CashierDashboardFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static class PaymentRowData {
        String refNo;
        String studentName;
        String programId;
        String amount;
        String feeType;
        String status;
        String timestamp;

        PaymentRowData(String refNo, String studentName, String programId, String amount, String feeType, String status, String timestamp) {
            this.refNo = refNo;
            this.studentName = studentName;
            this.programId = programId;
            this.amount = amount;
            this.feeType = feeType;
            this.status = status;
            this.timestamp = timestamp;
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
        enum Type { SEARCH, BELL, USER_AVATAR, BAR_CHART }

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
                case BELL -> {
                    g.drawArc(x + 4, y + 3, 10, 10, 0, 180);
                    g.drawLine(x + 4, y + 8, x + 4, y + 13);
                    g.drawLine(x + 14, y + 8, x + 14, y + 13);
                    g.drawLine(x + 2, y + 13, x + 16, y + 13);
                    g.drawArc(x + 7, y + 13, 4, 4, 180, 180);
                }
                case USER_AVATAR -> {
                    g.drawOval(x + 5, y + 2, 8, 8);
                    g.drawArc(x + 2, y + 9, 14, 8, 0, 180);
                }
                case BAR_CHART -> {
                    g.drawRect(x + 2, y + 8, 3, 6);
                    g.drawRect(x + 7, y + 4, 3, 10);
                    g.drawRect(x + 12, y + 2, 3, 12);
                }
            }
            g.dispose();
        }
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
