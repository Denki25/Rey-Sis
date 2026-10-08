package org.example.ui.views.student;

import org.example.model.Student;
import org.example.data.BillingSettingsRepository.BillingSettings;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

public class AssessmentDialog extends JDialog {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);
    private final Runnable onSubmitted;
    private final double totalAmount;
    private final BillingSettings billingSettings;
    private final JPanel installmentPreview = new JPanel(new GridLayout(3, 1, 0, 4));

    public AssessmentDialog(java.awt.Frame owner, Student student, List<SubjectLine> subjects,
                            int totalUnits, double baseTuition, BillingSettings billingSettings,
                            Runnable onSubmitted) {
        super(owner, "REY SIS | Assessment", true);
        this.onSubmitted = onSubmitted;
        this.billingSettings = billingSettings;
        this.totalAmount = baseTuition + billingSettings.totalMiscellaneousFees();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 680);
        setMinimumSize(new Dimension(700, 600));
        setLocationRelativeTo(owner);
        setContentPane(createContent(student, subjects, totalUnits, baseTuition));
    }

    private JPanel createContent(Student student, List<SubjectLine> subjects, int totalUnits, double baseTuition) {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(new Color(247, 248, 246));
        content.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));
        content.add(createHeader(student), BorderLayout.NORTH);
        content.add(createCenter(subjects, totalUnits, baseTuition), BorderLayout.CENTER);
        content.add(createActions(), BorderLayout.SOUTH);
        return content;
    }

    private JPanel createHeader(Student student) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Assessment");
        title.setFont(new Font("Serif", Font.BOLD, 25));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);
        JPanel details = new JPanel(new GridLayout(3, 1));
        details.setOpaque(false);
        details.add(infoLabel(student.getName(), true));
        details.add(infoLabel(student.getStudentId(), false));
        details.add(infoLabel("AY 2026-2027 - 2nd Semester", false));
        header.add(details, BorderLayout.EAST);
        return header;
    }

    private JPanel createCenter(List<SubjectLine> subjects, int totalUnits, double baseTuition) {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(createStatement(subjects, totalUnits, baseTuition), BorderLayout.CENTER);
        center.add(createPaymentOptions(), BorderLayout.SOUTH);
        return center;
    }

    private JPanel createStatement(List<SubjectLine> subjects, int totalUnits, double baseTuition) {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JLabel title = new JLabel("Itemized Statement of Account");
        title.setFont(new Font("Serif", Font.BOLD, 17));
        title.setForeground(TEXT);
        card.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"DESCRIPTION", "DETAIL", "AMOUNT"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        model.addRow(new Object[]{"Tuition Fee",
                totalUnits + " units x " + money(billingSettings.tuitionRatePerUnit()) + "/unit", money(baseTuition)});
        model.addRow(new Object[]{"Library Fee", "Miscellaneous", money(billingSettings.libraryFee())});
        model.addRow(new Object[]{"Registration Fee", "Miscellaneous", money(billingSettings.registrationFee())});
        model.addRow(new Object[]{"IT & Lab Fee", "Miscellaneous", money(billingSettings.itLabFee())});
        model.addRow(new Object[]{"Athletics", "Miscellaneous", money(billingSettings.athleticsFee())});
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setFont(new Font("SansSerif", Font.PLAIN, 10));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 9));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer();
        amountRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel total = new JPanel(new BorderLayout());
        total.setOpaque(false);
        total.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JLabel totalLabel = new JLabel("Total Amount Due");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        totalLabel.setForeground(TEXT);
        JLabel totalValue = new JLabel(money(totalAmount), SwingConstants.RIGHT);
        totalValue.setFont(new Font("SansSerif", Font.BOLD, 17));
        totalValue.setForeground(DEEP_GREEN);
        total.add(totalLabel, BorderLayout.WEST);
        total.add(totalValue, BorderLayout.EAST);
        card.add(total, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createPaymentOptions() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("Payment Options");
        title.setFont(new Font("Serif", Font.BOLD, 16));
        title.setForeground(TEXT);
        card.add(title, BorderLayout.NORTH);

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        options.setOpaque(false);
        JRadioButton fullPayment = new JRadioButton("Full Payment", true);
        JRadioButton installment = new JRadioButton("Installment Plan");
        fullPayment.setOpaque(false);
        installment.setOpaque(false);
        fullPayment.setFont(new Font("SansSerif", Font.PLAIN, 10));
        installment.setFont(new Font("SansSerif", Font.PLAIN, 10));
        ButtonGroup group = new ButtonGroup();
        group.add(fullPayment);
        group.add(installment);
        options.add(fullPayment);
        options.add(installment);
        card.add(options, BorderLayout.CENTER);

        installmentPreview.setOpaque(false);
        installmentPreview.setBorder(BorderFactory.createEmptyBorder(4, 8, 0, 8));
        installmentPreview.add(paymentRow("Downpayment (40%)", totalAmount * 0.40));
        installmentPreview.add(paymentRow("Midterms (30%)", totalAmount * 0.30));
        installmentPreview.add(paymentRow("Finals (30%)", totalAmount * 0.30));
        installmentPreview.setVisible(false);
        card.add(installmentPreview, BorderLayout.SOUTH);
        installment.addActionListener(event -> installmentPreview.setVisible(true));
        fullPayment.addActionListener(event -> installmentPreview.setVisible(false));
        return card;
    }

    private JPanel paymentRow(String label, double amount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(infoLabel(label, false), BorderLayout.WEST);
        row.add(infoLabel(money(amount), false), BorderLayout.EAST);
        return row;
    }

    private JPanel createActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton back = new JButton("Back to Selected Subjects");
        styleSecondary(back);
        back.addActionListener(event -> dispose());
        JButton confirm = new JButton("Confirm & Submit Enrollment");
        confirm.setFont(new Font("SansSerif", Font.BOLD, 10));
        confirm.setForeground(Color.WHITE);
        confirm.setBackground(DEEP_GREEN);
        confirm.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        confirm.setFocusPainted(false);
        confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirm.addActionListener(event -> confirmEnrollment());
        actions.add(back);
        actions.add(confirm);
        return actions;
    }

    private void confirmEnrollment() {
        if (onSubmitted != null) {
            onSubmitted.run();
        }
        JOptionPane.showMessageDialog(this, "Enrollment submitted successfully.\nStatus: Officially Enrolled", "Enrollment Confirmed", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private JLabel infoLabel(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, bold ? 11 : 9));
        label.setForeground(bold ? TEXT : MUTED);
        return label;
    }

    private void styleSecondary(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 10));
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String money(double amount) {
        return String.format("₱ %,.2f", amount);
    }

    public static final class SubjectLine {
        private final String code;
        private final String title;
        private final int units;

        public SubjectLine(String code, String title, int units) {
            this.code = code;
            this.title = title;
            this.units = units;
        }
    }

    private static class CardPanel extends JPanel {
        private final Color background;

        CardPanel(Color background) {
            this.background = background;
            setOpaque(false);
        }

        protected void paintComponent(java.awt.Graphics graphics) {
            java.awt.Graphics2D g = (java.awt.Graphics2D) graphics.create();
            g.setColor(background);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.setColor(BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
