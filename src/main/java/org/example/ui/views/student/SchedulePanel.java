package org.example.ui.views.student;

import org.example.model.ScheduleItem;
import org.example.model.Student;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SchedulePanel extends JPanel {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color PAGE = new Color(247, 248, 246);

    private final Student student;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"CODE", "COURSE TITLE", "DAY", "TIME", "ROOM"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable scheduleTable = new JTable(tableModel);
    private final JComboBox<String> semesterSelector = new JComboBox<>(new String[]{"Enrolled schedule"});

    public SchedulePanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(0, 20));
        setBackground(PAGE);
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        add(createPageHeader(), BorderLayout.NORTH);
        add(createScheduleCard(), BorderLayout.CENTER);
        loadSchedule("Enrolled schedule");
        semesterSelector.addActionListener(event -> loadSchedule((String) semesterSelector.getSelectedItem()));
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new javax.swing.BoxLayout(titles, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel("My Schedule");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("My schedule for the whole semester");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);
        titles.add(title);
        titles.add(subtitle);
        JPanel accent = new JPanel();
        accent.setBackground(GOLD);
        accent.setPreferredSize(new Dimension(32, 3));
        titles.add(javax.swing.Box.createVerticalStrut(9));
        titles.add(accent);
        header.add(titles, BorderLayout.WEST);

        semesterSelector.setFont(new Font("SansSerif", Font.BOLD, 12));
        semesterSelector.setForeground(TEXT);
        semesterSelector.setBackground(Color.WHITE);
        semesterSelector.setPreferredSize(new Dimension(280, 40));
        header.add(semesterSelector, BorderLayout.EAST);
        return header;
    }

    private JPanel createScheduleCard() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 234, 229)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 2));
        heading.setOpaque(false);
        JLabel headingLabel = new JLabel("Selected Semester");
        headingLabel.setFont(new Font("Serif", Font.BOLD, 20));
        headingLabel.setForeground(TEXT);
        JLabel description = new JLabel("List of subjects you have for the selected semester.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 11));
        description.setForeground(MUTED);
        heading.add(headingLabel);
        heading.add(description);
        cardHeader.add(heading, BorderLayout.WEST);

        JButton download = new JButton("Download Schedule");
        download.setFont(new Font("SansSerif", Font.BOLD, 11));
        download.setForeground(Color.WHITE);
        download.setBackground(DEEP_GREEN);
        download.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        download.setFocusPainted(false);
        download.setMargin(new Insets(0, 0, 0, 0));
        download.addActionListener(event -> downloadSchedule());
        cardHeader.add(download, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        configureTable();
        card.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        return card;
    }

    private void configureTable() {
        scheduleTable.setRowHeight(48);
        scheduleTable.setShowGrid(true);
        scheduleTable.setGridColor(new Color(229, 232, 227));
        scheduleTable.setIntercellSpacing(new Dimension(1, 1));
        scheduleTable.setFillsViewportHeight(true);
        scheduleTable.setFont(new Font("SansSerif", Font.PLAIN, 11));
        scheduleTable.setForeground(TEXT);
        scheduleTable.setBackground(Color.WHITE);
        scheduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        scheduleTable.getTableHeader().setForeground(DEEP_GREEN);
        scheduleTable.getTableHeader().setBackground(new Color(241, 244, 237));
        scheduleTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        scheduleTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DEEP_GREEN));
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(330);
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        for (int column = 0; column <= 4; column++) {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(column >= 2 ? SwingConstants.CENTER : SwingConstants.LEFT);
            final int tableColumn = column;
            renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                               boolean focused, int row, int column) {
                    Component component = super.getTableCellRendererComponent(
                            table, value, selected, focused, row, column);
                    setHorizontalAlignment(tableColumn >= 2 ? SwingConstants.CENTER : SwingConstants.LEFT);
                    setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                    if (!selected) {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 247));
                    }
                    return component;
                }
            };
            scheduleTable.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
    }

    private void loadSchedule(String semester) {
        tableModel.setRowCount(0);
        for (int index = 0; index < student.getSchedule().size(); index++) {
            ScheduleItem item = student.getSchedule().get(index);
            tableModel.addRow(new Object[]{
                    item.getCourse().getCode(),
                    item.getCourse().getTitle(),
                    item.getDayOfWeek(),
                    formatTime(item.getTime()),
                    item.getRoom()
            });
        }
    }

    private String formatTime(String time) {
        return time.replace("\n", " - ");
    }

    private void downloadSchedule() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "There are no subjects in the selected semester.",
                    "Download Schedule", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Schedule as PDF");
        chooser.setSelectedFile(new File("rey-sis-schedule.pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedFile = chooser.getSelectedFile();
        if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".pdf");
        }
        try {
            writePdf(selectedFile);
            JOptionPane.showMessageDialog(this, "Schedule exported successfully to:\n" + selectedFile.getAbsolutePath(),
                    "Download Schedule", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Unable to export the schedule PDF:\n" + exception.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writePdf(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("BT /F1 20 Tf 55 560 Td (REY SIS - My Schedule) Tj ET\n");
        content.append("BT /F1 10 Tf 55 540 Td (Selected Semester: ")
                .append(pdfEscape((String) semesterSelector.getSelectedItem())).append(") Tj ET\n");
        content.append("0.80 0.82 0.78 RG 55 520 m 737 520 l S\n");

        String[] headers = {"CODE", "COURSE TITLE", "DAY", "TIME", "ROOM"};
        int[] xPositions = {60, 180, 500, 570, 680};
        int y = 500;
        content.append("BT /F1 9 Tf ");
        for (int index = 0; index < headers.length; index++) {
            appendPdfText(content, headers[index], xPositions[index], y);
        }
        content.append("ET\n");
        y = 475;
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            content.append("0.86 0.87 0.84 RG 55 ").append(y - 10).append(" m 737 ").append(y - 10).append(" l S\n");
            content.append("BT /F1 9 Tf ");
            for (int column = 0; column < tableModel.getColumnCount(); column++) {
                appendPdfText(content, String.valueOf(tableModel.getValueAt(row, column)), xPositions[column], y);
            }
            content.append("ET\n");
            y -= 32;
        }
        content.append("0.80 0.82 0.78 RG 55 ").append(y + 18).append(" m 737 ").append(y + 18).append(" l S\n");

        byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
        List<byte[]> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add("<< /Type /Pages /Kids [3 0 R] /Count 1 >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 792 612] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>".getBytes(StandardCharsets.ISO_8859_1));
        objects.add(("<< /Length " + contentBytes.length + " >>\nstream\n"
                + content + "endstream").getBytes(StandardCharsets.ISO_8859_1));

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int index = 0; index < objects.size(); index++) {
            offsets.add(pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length);
            pdf.append(index + 1).append(" 0 obj\n");
            pdf.append(new String(objects.get(index), StandardCharsets.ISO_8859_1));
            pdf.append("\nendobj\n");
        }
        int xrefOffset = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length;
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n0000000000 65535 f \n");
        for (int offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }
        pdf.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\nstartxref\n")
                .append(xrefOffset).append("\n%%EOF");
        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(pdf.toString().getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    private String pdfEscape(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private void appendPdfText(StringBuilder content, String text, int x, int y) {
        content.append("1 0 0 1 ").append(x).append(" ").append(y).append(" Tm (")
                .append(pdfEscape(text)).append(") Tj ");
    }
}
