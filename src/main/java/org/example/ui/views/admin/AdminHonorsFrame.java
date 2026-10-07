package org.example.ui.views.admin;

import org.example.data.RankingRepository;
import org.example.service.RankingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public final class AdminHonorsFrame {
    private final JFrame window = new JFrame("REY SIS | Honors List");
    private final RankingService rankingService = new RankingService();

    public AdminHonorsFrame() {
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(760, 500);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JLabel title = new JLabel("Ranking / Honors List");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Rank", "Student", "Program", "GPA"}, 0);
        try {
            for (RankingRepository.RankingEntry entry : rankingService.findTop(20)) {
                model.addRow(new Object[]{entry.rank(), entry.studentName() + " (" + entry.studentId() + ")",
                        entry.program(), String.format("%.2f", entry.gpa())});
            }
        } catch (SQLException | SecurityException exception) {
            JOptionPane.showMessageDialog(window, "Unable to load the honors list.", "REY SIS", JOptionPane.ERROR_MESSAGE);
        }
        panel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        return panel;
    }
}
