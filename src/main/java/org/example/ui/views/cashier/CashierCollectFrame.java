package org.example.ui.views.cashier;

import org.example.model.Cashier;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class CashierCollectFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | Cashier - Collection & Payments");
    private final Cashier cashier;

    // Form fields matching wireframe
    private final JLabel studentInfoLabel = new JLabel("2025-0011 | Justine Rivera");
    private final JTextField amountTenderedField = new JTextField("₱ 20,000.00");
    private final JLabel totalAmountDueLabel = new JLabel("₱ 18,250.00");
    private final JLabel changeLabel = new JLabel("₱ 1,750.00");

    // Payment method active tracking button reference
    private JButton selectedPaymentBtn;
    private final List<CollectStudent> students = new ArrayList<>();
    private CollectStudent selectedStudent;
    private double selectedBalanceAmount = 18250;
    private ButtonGroup balanceGroup;
    private JPanel balancesPanel;
    private JLabel cashTenderedTitle;
    private JLabel referenceTitle;
    private JTextField referenceField;
    private String selectedPaymentMethod = "Cash";
    private static final AtomicInteger RECEIPT_SEQUENCE = new AtomicInteger(8800);

    public CashierCollectFrame() {
        this(null);
    }

    public CashierCollectFrame(Cashier cashier) {
        this.cashier = cashier;
        initMockStudents();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1160, 780));
        window.setSize(1360, 920);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    private void initMockStudents() {
        students.add(new CollectStudent("2025-0011", "Justine Rivera", "BSIT", 18250, 4500));
        students.add(new CollectStudent("2025-0042", "Maria Santos", "BSBA", 3200, 1800));
        students.add(new CollectStudent("2025-0105", "Juan Dela Cruz", "BSCE", 15000, 2500));
        selectedStudent = students.get(0);
    }

    public void showWindow() {
        window.setVisible(true);
    }

    public void selectStudentById(String studentId) {
        for (CollectStudent student : students) {
            if (student.id.equals(studentId)) {
                selectedStudent = student;
                studentInfoLabel.setText(student.id + " | " + student.name);
                refreshStudentBalances();
                return;
            }
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

        navigation.add(createNavigationButton("Dashboard", IconType.DASHBOARD, false));
        navigation.add(createNavigationButton("Collect Payment", IconType.ENROLLMENT, true));
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

        JTextField searchField = new JTextField("Search student ID, name, OR number...");
        searchField.setForeground(TEXT);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(350, 24));
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(searchPanel);
        header.add(leftContainer, BorderLayout.WEST);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(PAGE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(PAGE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 28, 15, 28));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Collect Payment");
        heading.setFont(new Font("Serif", Font.BOLD, 28));
        heading.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Look up student accounts and process new transactions.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);

        JPanel gridWrapper = new JPanel(new GridLayout(1, 2, 20, 0));
        gridWrapper.setOpaque(false);
        gridWrapper.setBorder(BorderFactory.createEmptyBorder(0, 28, 28, 28));

        gridWrapper.add(createLeftFormCard());
        gridWrapper.add(createRightPaymentCard());

        body.add(titlePanel);
        body.add(gridWrapper);

        return body;
    }

    private JPanel createLeftFormCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel s1Title = new JLabel("1. Select Student");
        s1Title.setFont(new Font("SansSerif", Font.BOLD, 13));
        s1Title.setForeground(TEXT);
        card.add(s1Title);
        card.add(Box.createVerticalStrut(8));

        JPanel selectStudentContainer = new JPanel(new BorderLayout());
        selectStudentContainer.setBackground(new Color(250, 251, 249));
        selectStudentContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        studentInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        studentInfoLabel.setForeground(TEXT);
        selectStudentContainer.add(studentInfoLabel, BorderLayout.WEST);

        JButton changeBtn = new JButton("CHANGE");
        changeBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        changeBtn.setForeground(new Color(34, 139, 34));
        changeBtn.setBorder(null);
        changeBtn.setContentAreaFilled(false);
        changeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeBtn.addActionListener(e -> showStudentLookupDialog());
        selectStudentContainer.add(changeBtn, BorderLayout.EAST);

        selectStudentContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        card.add(selectStudentContainer);
        card.add(Box.createVerticalStrut(24));

        JLabel s2Title = new JLabel("2. Outstanding Balances");
        s2Title.setFont(new Font("SansSerif", Font.BOLD, 13));
        s2Title.setForeground(TEXT);
        card.add(s2Title);
        card.add(Box.createVerticalStrut(8));

        balancesPanel = new JPanel();
        balancesPanel.setOpaque(false);
        balancesPanel.setLayout(new BoxLayout(balancesPanel, BoxLayout.Y_AXIS));
        card.add(balancesPanel);
        refreshStudentBalances();

        return card;
    }

    private void refreshStudentBalances() {
        if (balancesPanel == null || selectedStudent == null) return;
        balancesPanel.removeAll();
        balanceGroup = new ButtonGroup();
        balancesPanel.add(createSelectableBalanceRow("Tuition Fee - 1st Semester", "Due: Oct 10, 2026", selectedStudent.tuitionBalance, true));
        balancesPanel.add(Box.createVerticalStrut(10));
        balancesPanel.add(createSelectableBalanceRow("Miscellaneous Fees", "Due: Nov 15, 2026", selectedStudent.miscellaneousBalance, false));
        balancesPanel.revalidate();
        balancesPanel.repaint();
        selectBalance(selectedStudent.tuitionBalance);
    }

    private JPanel createSelectableBalanceRow(String title, String dueDate, double amount, boolean selected) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(250, 251, 249));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selected ? DEEP_GREEN : BORDER, selected ? 2 : 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel leftPart = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPart.setOpaque(false);

        JRadioButton radio = new JRadioButton();
        radio.setSelected(selected);
        balanceGroup.add(radio);
        radio.addItemListener(event -> {
            if (radio.isSelected()) selectBalance(amount);
        });
        radio.setOpaque(false);
        leftPart.add(radio);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        tLbl.setForeground(TEXT);
        JLabel dLbl = new JLabel(dueDate);
        dLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        dLbl.setForeground(MUTED);
        textPanel.add(tLbl);
        textPanel.add(dLbl);
        leftPart.add(textPanel);

        row.add(leftPart, BorderLayout.WEST);

        JLabel amtLbl = new JLabel(formatAmount(amount));
        amtLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        amtLbl.setForeground(TEXT);
        row.add(amtLbl, BorderLayout.EAST);

        return row;
    }

    private void selectBalance(double amount) {
        selectedBalanceAmount = amount;
        totalAmountDueLabel.setText(formatAmount(amount));
        updateChange();
    }

    private void showStudentLookupDialog() {
        JDialog dialog = new JDialog(window, "Select Student", true);
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JTextField search = new JTextField();
        search.setToolTipText("Search by student ID or name");
        content.add(search, BorderLayout.NORTH);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"STUDENT ID", "NAME", "PROGRAM"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (CollectStudent student : students) model.addRow(new Object[]{student.id, student.name, student.program});
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton select = new JButton("Select Student");
        select.addActionListener(event -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Select a student first.", "Student Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedStudent = students.get(row);
            studentInfoLabel.setText(selectedStudent.id + " | " + selectedStudent.name);
            refreshStudentBalances();
            dialog.dispose();
        });
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(select);
        content.add(footer, BorderLayout.SOUTH);
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String query = search.getText().trim().toLowerCase(Locale.ROOT);
                table.clearSelection();
                for (int row = 0; row < model.getRowCount(); row++) {
                    String id = String.valueOf(model.getValueAt(row, 0)).toLowerCase(Locale.ROOT);
                    String name = String.valueOf(model.getValueAt(row, 1)).toLowerCase(Locale.ROOT);
                    if (id.contains(query) || name.contains(query)) {
                        table.setRowSelectionInterval(row, row);
                        break;
                    }
                }
            }
            public void insertUpdate(DocumentEvent event) { filter(); }
            public void removeUpdate(DocumentEvent event) { filter(); }
            public void changedUpdate(DocumentEvent event) { filter(); }
        });
        dialog.setContentPane(content);
        dialog.setSize(480, 340);
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    private JPanel createRightPaymentCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel pTitle = new JLabel("3. Payment Details");
        pTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        pTitle.setForeground(TEXT);
        card.add(pTitle);
        card.add(Box.createVerticalStrut(10));

        JLabel amtTitle = new JLabel("AMOUNT TO PAY");
        amtTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        amtTitle.setForeground(MUTED);
        card.add(amtTitle);
        card.add(Box.createVerticalStrut(4));

        JPanel amountDisplayBox = new JPanel(new BorderLayout());
        amountDisplayBox.setBackground(new Color(250, 251, 249));
        amountDisplayBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        totalAmountDueLabel.setFont(new Font("Serif", Font.BOLD, 24));
        totalAmountDueLabel.setForeground(TEXT);
        amountDisplayBox.add(totalAmountDueLabel, BorderLayout.WEST);
        amountDisplayBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        card.add(amountDisplayBox);
        card.add(Box.createVerticalStrut(14));

        JLabel methodTitle = new JLabel("PAYMENT METHOD");
        methodTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        methodTitle.setForeground(MUTED);
        card.add(methodTitle);
        card.add(Box.createVerticalStrut(8));

        JPanel methodsGrid = new JPanel(new GridLayout(2, 2, 8, 8));
        methodsGrid.setOpaque(false);
        methodsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JButton cashBtn = createPaymentMethodButton("Cash", true);
        JButton gcashBtn = createPaymentMethodButton("GCash", false);
        JButton cardBtn = createPaymentMethodButton("Card", false);
        JButton bankBtn = createPaymentMethodButton("Bank", false);

        selectedPaymentBtn = cashBtn;

        methodsGrid.add(cashBtn);
        methodsGrid.add(gcashBtn);
        methodsGrid.add(cardBtn);
        methodsGrid.add(bankBtn);
        card.add(methodsGrid);
        card.add(Box.createVerticalStrut(14));

        cashTenderedTitle = new JLabel("CASH TENDERED");
        JLabel tenderedTitle = cashTenderedTitle;
        tenderedTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        tenderedTitle.setForeground(MUTED);
        card.add(tenderedTitle);
        card.add(Box.createVerticalStrut(4));

        amountTenderedField.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountTenderedField.setForeground(TEXT);
        amountTenderedField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        amountTenderedField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        amountTenderedField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { updateChange(); }
            public void removeUpdate(DocumentEvent event) { updateChange(); }
            public void changedUpdate(DocumentEvent event) { updateChange(); }
        });
        card.add(amountTenderedField);
        card.add(Box.createVerticalStrut(12));

        JPanel changeRow = new JPanel(new BorderLayout());
        changeRow.setOpaque(false);
        changeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel changeText = new JLabel("CHANGE");
        changeText.setFont(new Font("SansSerif", Font.BOLD, 11));
        changeText.setForeground(MUTED);
        changeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        changeLabel.setForeground(TEXT);
        changeRow.add(changeText, BorderLayout.WEST);
        changeRow.add(changeLabel, BorderLayout.EAST);
        card.add(changeRow);
        card.add(Box.createVerticalStrut(20));

        referenceTitle = new JLabel("REFERENCE / TRANSACTION NO.");
        referenceTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        referenceTitle.setForeground(MUTED);
        referenceField = new JTextField();
        referenceField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        referenceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        referenceTitle.setVisible(false);
        referenceField.setVisible(false);
        card.add(referenceTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(referenceField);
        card.add(Box.createVerticalStrut(12));

        JButton confirmBtn = new JButton("Confirm Payment");
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setBackground(DEEP_GREEN);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> processPayment());
        card.add(confirmBtn);

        return card;
    }

    private JButton createPaymentMethodButton(String text, boolean selected) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateButtonStyle(btn, selected);

        btn.addActionListener(e -> {
            if (selectedPaymentBtn != null) {
                updateButtonStyle(selectedPaymentBtn, false);
            }
            selectedPaymentBtn = btn;
            selectedPaymentMethod = text;
            updateButtonStyle(btn, true);
            updatePaymentInputVisibility();
        });
        return btn;
    }

    private void updatePaymentInputVisibility() {
        boolean cash = "Cash".equals(selectedPaymentMethod);
        if (cashTenderedTitle != null) {
            cashTenderedTitle.setText(cash ? "CASH TENDERED" : "PAYMENT CONFIRMATION");
            cashTenderedTitle.setVisible(true);
        }
        if (amountTenderedField != null) amountTenderedField.setVisible(cash);
        if (referenceTitle != null) referenceTitle.setVisible(!cash);
        if (referenceField != null) referenceField.setVisible(!cash);
        if (window.getContentPane() != null) {
            window.getContentPane().revalidate();
            window.getContentPane().repaint();
        }
    }

    private void updateChange() {
        double tendered = parseAmount(amountTenderedField.getText());
        double change = Math.max(0, tendered - selectedBalanceAmount);
        changeLabel.setText(formatAmount(change));
    }

    private void processPayment() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(window, "Select a student before processing payment.", "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedBalanceAmount <= 0) {
            JOptionPane.showMessageDialog(window, "The selected balance has already been paid.", "No Balance Due", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if ("Cash".equals(selectedPaymentMethod)) {
            double tendered = parseAmount(amountTenderedField.getText());
            if (tendered < selectedBalanceAmount) {
                JOptionPane.showMessageDialog(window, "Cash tendered is less than the amount to pay.", "Insufficient Payment", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else if (referenceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(window, "Enter the reference or transaction number.", "Reference Required", JOptionPane.WARNING_MESSAGE);
            referenceField.requestFocusInWindow();
            return;
        }

        double paidAmount = selectedBalanceAmount;
        String orNumber = "OR-2026-" + RECEIPT_SEQUENCE.incrementAndGet();
        if (selectedStudent.tuitionBalance == selectedBalanceAmount) selectedStudent.tuitionBalance = 0;
        else selectedStudent.miscellaneousBalance = 0;
        refreshStudentBalances();
        showReceiptPreview(orNumber, paidAmount);
    }

    private void showReceiptPreview(String orNumber, double paidAmount) {
        JDialog dialog = new JDialog(window, "Official Receipt", true);
        JTextArea receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receipt.setText("REY SIS UNIVERSITY\nOFFICIAL PAYMENT RECEIPT\n\n"
                + "OR Number: " + orNumber + "\n"
                + "Student:   " + selectedStudent.name + "\n"
                + "Student ID:" + selectedStudent.id + "\n"
                + "Fee:       " + formatAmount(paidAmount) + "\n"
                + "Method:    " + selectedPaymentMethod + "\n"
                + "Amount:    " + formatAmount(paidAmount) + "\n\n"
                + "Thank you for your payment.");
        JButton print = new JButton("Print Receipt");
        print.addActionListener(event -> {
            try {
                receipt.print();
                JOptionPane.showMessageDialog(dialog, "Receipt sent to the printer.", "Print Receipt", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(dialog, "Unable to print receipt: " + exception.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton close = new JButton("Close");
        close.addActionListener(event -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(close);
        actions.add(print);
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        content.add(new JScrollPane(receipt), BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.setSize(430, 390);
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    private double parseAmount(String value) {
        String numeric = value.replaceAll("[^0-9.,]", "").replace(",", "");
        return numeric.isEmpty() ? 0 : Double.parseDouble(numeric);
    }

    private String formatAmount(double value) {
        return String.format(Locale.US, "₱ %,.2f", value);
    }

    private void updateButtonStyle(JButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(new Color(235, 242, 233));
            btn.setForeground(DEEP_GREEN);
            btn.setBorder(BorderFactory.createLineBorder(DEEP_GREEN, 1, true));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXT);
            btn.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        }
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = CashierCollectFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private static class CollectStudent {
        final String id;
        final String name;
        final String program;
        double tuitionBalance;
        double miscellaneousBalance;

        CollectStudent(String id, String name, String program, double tuitionBalance, double miscellaneousBalance) {
            this.id = id;
            this.name = name;
            this.program = program;
            this.tuitionBalance = tuitionBalance;
            this.miscellaneousBalance = miscellaneousBalance;
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
