package org.example.ui.views.cashier;

import org.example.data.PaymentRepository;
import org.example.model.Cashier;
import org.example.service.CashierPaymentService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CashierReportsFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | Cashier - Reports");
    private final Cashier cashier;
    private final CashierPaymentService paymentService = new CashierPaymentService();
    private final List<ReportData> reports = new ArrayList<>();
    private DefaultTableModel reportModel;
    private TableRowSorter<DefaultTableModel> reportSorter;
    private JTextField searchField;

    public CashierReportsFrame() {
        this(null);
    }

    public CashierReportsFrame(Cashier cashier) {
        this.cashier = cashier;

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1160, 780));
        window.setSize(1360, 920);
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
        navigation.add(createNavigationButton("Collect Payment", IconType.ENROLLMENT, false));
        navigation.add(createNavigationButton("Transaction", IconType.RECORDS, false));
        navigation.add(createNavigationButton("Student Accounts", IconType.PROFILE, false));
        navigation.add(createNavigationButton("Reconciliation", IconType.RECONCILIATION, false));
        navigation.add(createNavigationButton("Reports", IconType.REPORTS, true));
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

            switch (text) {
                case "Dashboard" -> new CashierDashboardFrame(cashier).showWindow();
                case "Collect Payment" -> new CashierCollectFrame(cashier).showWindow();
                case "Transaction" -> new CashierTransacFrame(cashier).showWindow();
                case "Student Accounts" -> new CashierStudentFrame(cashier).showWindow();
                case "Reconciliation" -> new CashierReconsilationFrame(cashier).showWindow();
                case "Reports" -> new CashierReportsFrame(cashier).showWindow();
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

        searchField = new JTextField("Search reports or keywords...");
        searchField.setForeground(MUTED);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(350, 24));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { applyReportFilter(); }
            public void removeUpdate(DocumentEvent event) { applyReportFilter(); }
            public void changedUpdate(DocumentEvent event) { applyReportFilter(); }
        });

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search reports or keywords...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search reports or keywords...");
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
        JLabel userName = new JLabel(cashier != null ? cashier.getName() : "Cashier");
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

        JLabel heading = new JLabel("Financial Reports");
        heading.setFont(new Font("Serif", Font.BOLD, 28));
        heading.setForeground(DEEP_GREEN);

        JLabel subtitle = new JLabel("Generate reports from recorded payments. Report history is kept for this session only.");
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

        JPanel contentWrapper = new JPanel(new BorderLayout(0, 24));
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Report Generation Cards
        JPanel reportCardsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        reportCardsContainer.setOpaque(false);
        reportCardsContainer.add(createReportCard("Daily Collection", "End-of-day transaction summary", "Generate PDF", DEEP_GREEN, Color.WHITE));
        reportCardsContainer.add(createReportCard("Monthly Revenue", "Comprehensive 30-day overview", "Export CSV", new Color(248, 237, 219), TEXT));
        reportCardsContainer.add(createReportCard("Reconciliation Log", "Detailed shift balance audits", "Generate PDF", Color.WHITE, TEXT));

        contentWrapper.add(reportCardsContainer, BorderLayout.NORTH);

        // Recent Generated Reports Table
        contentWrapper.add(createRecentReportsTable(), BorderLayout.CENTER);

        body.add(titlePanel);
        body.add(contentWrapper);

        return body;
    }

    private JPanel createReportCard(String title, String subtitle, String btnText, Color bgColor, Color fgColor) {
        CardPanel card = new CardPanel(bgColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLbl.setForeground(fgColor);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subLbl.setForeground(bgColor == Color.WHITE || bgColor.equals(new Color(248, 237, 219)) ? MUTED : new Color(202, 219, 211));
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton actionBtn = new JButton(btnText);
        actionBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        actionBtn.setForeground(bgColor == DEEP_GREEN ? DEEP_GREEN : Color.WHITE);
        actionBtn.setBackground(bgColor == DEEP_GREEN ? Color.WHITE : DEEP_GREEN);
        actionBtn.setFocusPainted(false);
        actionBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtn.setMaximumSize(new Dimension(160, 36));
        actionBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionBtn.addActionListener(e -> generateReport(title, btnText));

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(subLbl);
        card.add(Box.createVerticalStrut(20));
        card.add(actionBtn);

        return card;
    }

    private JPanel createRecentReportsTable() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel title = new JLabel("Reports Generated This Session");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(DEEP_GREEN);
        card.add(title, BorderLayout.NORTH);

        reportModel = new DefaultTableModel(new Object[]{"REPORT NAME", "FORMAT", "DATE GENERATED", "ACTION"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        refreshReportModel();
        JTable table = new JTable(reportModel);
        reportSorter = new TableRowSorter<>(reportModel);
        table.setRowSorter(reportSorter);
        table.setRowHeight(42);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setGridColor(new Color(242, 243, 240));
        table.setShowVerticalLines(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                int column = table.columnAtPoint(event.getPoint());
                if (row >= 0 && column == 3) {
                    int modelRow = table.convertRowIndexToModel(row);
                    downloadReport(reports.get(modelRow));
                }
            }
        });
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private void refreshReportModel() {
        if (reportModel == null) return;
        reportModel.setRowCount(0);
        for (ReportData report : reports) reportModel.addRow(new Object[]{report.name, report.format, report.generatedAt, "Download"});
    }

    private void applyReportFilter() {
        if (reportSorter == null) return;
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        if (query.equals("search reports or keywords...")) query = "";
        String search = query;
        reportSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                return search.isEmpty()
                        || String.valueOf(entry.getValue(0)).toLowerCase(Locale.ROOT).contains(search)
                        || String.valueOf(entry.getValue(1)).toLowerCase(Locale.ROOT).contains(search);
            }
        });
    }

    private void generateReport(String title, String actionText) {
        String format = actionText.contains("CSV") ? "CSV" : "PDF";
        String reportTitle = title + " - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US));
        try {
            List<PaymentRepository.PaymentRecord> payments = findReportPayments(title);
            ReportData report = new ReportData(reportTitle, format, currentTimestamp(), payments);
            reports.add(0, report);
            refreshReportModel();
            applyReportFilter();
            if ("CSV".equals(format)) {
                exportMonthlyRevenue(payments);
            } else {
                showGeneratedReport(title, payments);
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(window, "Unable to load payment data for this report.",
                    "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<PaymentRepository.PaymentRecord> findReportPayments(String title) throws java.sql.SQLException {
        LocalDate today = LocalDate.now();
        if (title.startsWith("Monthly Revenue")) {
            return paymentService.findPaymentsBetween(today.withDayOfMonth(1), today.withDayOfMonth(1).plusMonths(1));
        }
        return paymentService.findPaymentsBetween(today, today.plusDays(1));
    }

    private void showGeneratedReport(String title, List<PaymentRepository.PaymentRecord> payments) {
        JTextArea report = new JTextArea(buildReportText(title, payments));
        report.setEditable(false);
        report.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JButton print = new JButton("Print Report");
        print.addActionListener(event -> {
            try { report.print(); }
            catch (Exception exception) { JOptionPane.showMessageDialog(window, exception.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE); }
        });
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JScrollPane(report), BorderLayout.CENTER);
        panel.add(print, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(window, panel, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private String buildReportText(String title, List<PaymentRepository.PaymentRecord> payments) {
        java.util.Map<String, Double> totals = new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        double total = 0;
        for (PaymentRepository.PaymentRecord payment : payments) {
            if (!"VERIFIED".equalsIgnoreCase(payment.status())) continue;
            totals.merge(payment.paymentType(), payment.amount(), Double::sum);
            total += payment.amount();
        }
        StringBuilder report = new StringBuilder("REY SIS UNIVERSITY\n")
                .append(title.toUpperCase(Locale.ROOT)).append(" REPORT\n\n")
                .append("Generated: ").append(currentTimestamp()).append("\n\n");
        for (java.util.Map.Entry<String, Double> entry : totals.entrySet()) {
            report.append(entry.getKey()).append(": ").append(formatAmount(entry.getValue())).append('\n');
        }
        report.append("Verified total: ").append(formatAmount(total)).append("\n")
                .append("Payment rows: ").append(payments.size()).append('\n');
        if (title.startsWith("Daily Collection")) {
            report.append("\nDate: ").append(LocalDate.now());
        } else if (title.startsWith("Reconciliation")) {
            report.append("\nSettlement variance is not available: the current database does not store settlement records.");
        }
        return report.toString();
    }

    private void exportMonthlyRevenue(List<PaymentRepository.PaymentRecord> payments) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("rey-sis-monthly-revenue.csv"));
        if (chooser.showSaveDialog(window) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter writer = new PrintWriter(chooser.getSelectedFile(), StandardCharsets.UTF_8)) {
            writer.println("DATE,REFERENCE,STUDENT,PROGRAM OR ID,METHOD,AMOUNT,STATUS");
            for (PaymentRepository.PaymentRecord payment : payments) {
                writer.println(csv(payment.paymentDate().toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) + ","
                        + csv(payment.referenceNumber()) + "," + csv(payment.studentName()) + ","
                        + csv(payment.programId()) + "," + csv(payment.paymentType()) + ","
                        + payment.amount() + "," + csv(payment.status()));
            }
            JOptionPane.showMessageDialog(window, "Monthly revenue CSV exported successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(window, "Unable to export report: " + exception.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void downloadReport(ReportData report) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(report.name.replaceAll("[^a-zA-Z0-9.-]", "_") + "." + report.format.toLowerCase(Locale.ROOT)));
        if (chooser.showSaveDialog(window) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter writer = new PrintWriter(chooser.getSelectedFile(), StandardCharsets.UTF_8)) {
            if ("CSV".equals(report.format)) {
                writer.println("DATE,REFERENCE,STUDENT,PROGRAM OR ID,METHOD,AMOUNT,STATUS");
                for (PaymentRepository.PaymentRecord payment : report.payments) {
                    writer.println(csv(payment.paymentDate().toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) + ","
                            + csv(payment.referenceNumber()) + "," + csv(payment.studentName()) + ","
                            + csv(payment.programId()) + "," + csv(payment.paymentType()) + ","
                            + payment.amount() + "," + csv(payment.status()));
                }
            } else {
                writer.print(buildReportText(report.name, report.payments));
            }
            JOptionPane.showMessageDialog(window, "Report downloaded successfully.", "Download Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(window, "Unable to save report: " + exception.getMessage(), "Download Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String currentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy, h:mm a", Locale.US));
    }

    private String formatAmount(double amount) {
        return String.format(Locale.US, "₱ %,.2f", amount);
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static class ReportData {
        final String name;
        final String format;
        final String generatedAt;
        final List<PaymentRepository.PaymentRecord> payments;

        ReportData(String name, String format, String generatedAt, List<PaymentRepository.PaymentRecord> payments) {
            this.name = name;
            this.format = format;
            this.generatedAt = generatedAt;
            this.payments = List.copyOf(payments);
        }
    }

    private JPanel createLegacyRecentReportsTable() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Recently Generated Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(DEEP_GREEN);
        header.add(title, BorderLayout.WEST);

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

        addTableHeaderCell(tableHeader, "Report Name", 0, 0.35, gbc);
        addTableHeaderCell(tableHeader, "Format", 1, 0.15, gbc);
        addTableHeaderCell(tableHeader, "Date Generated", 2, 0.25, gbc);
        addTableHeaderCell(tableHeader, "Action", 3, 0.25, gbc, SwingConstants.RIGHT);

        tableContainer.add(tableHeader);

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

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = CashierReportsFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
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
                case ENROLLMENT -> {
                    g.drawRect(x + 2, y + 3, 15, 13);
                    g.drawLine(x + 5, y + 7, x + 14, y + 7);
                    g.drawLine(x + 5, y + 11, x + 11, y + 11);
                }
                case RECORDS -> {
                    g.drawRect(x + 3, y + 2, 12, 15);
                    g.drawLine(x + 6, y + 6, x + 12, y + 6);
                    g.drawLine(x + 6, y + 10, x + 12, y + 10);
                }
                case PROFILE -> {
                    g.drawOval(x + 6, y + 3, 6, 6);
                    g.drawArc(x + 3, y + 11, 12, 6, 0, 180);
                }
                case RECONCILIATION -> {
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
                case REPORTS -> {
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
