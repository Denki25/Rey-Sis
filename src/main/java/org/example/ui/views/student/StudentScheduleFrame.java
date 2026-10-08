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
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class StudentScheduleFrame {
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private final JFrame window = new JFrame("REY SIS | My Schedule");
    private final Student student;

    public StudentScheduleFrame(Student student) {
        this.student = student;
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1060, 680));
        window.setSize(1360, 820);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(createSidebar(), BorderLayout.WEST);
        content.add(new SchedulePanel(student), BorderLayout.CENTER);
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
        JLabel logo = new JLabel(loadLogo());
        logo.setAlignmentX(0.5f);
        top.add(logo);
        top.add(Box.createVerticalStrut(8));
        JLabel brand = new JLabel("REY SIS");
        brand.setFont(new Font("SansSerif", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(0.5f);
        top.add(brand);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
        navigation.add(navigationButton("Dashboard", StudentDashboardFrame.IconType.DASHBOARD, false));
        navigation.add(navigationButton("My Profile", StudentDashboardFrame.IconType.PROFILE, false));
        navigation.add(navigationButton("Enrollment", StudentDashboardFrame.IconType.ENROLLMENT, false));
        navigation.add(navigationButton("My Schedule", StudentDashboardFrame.IconType.CALENDAR, true));
        sidebar.add(navigation, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        bottom.add(navigationButton("Sign Out", StudentDashboardFrame.IconType.SIGN_OUT, false), BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton navigationButton(String text, StudentDashboardFrame.IconType icon, boolean active) {
        JButton button = new JButton(text, new StudentDashboardFrame.DashboardIcon(
                icon, active ? GOLD : new Color(202, 219, 211)));
        button.setPreferredSize(new Dimension(205, 46));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setMargin(new Insets(0, 25, 0, 12));
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setForeground(active ? GOLD : Color.WHITE);
        button.setBackground(active ? new Color(26, 86, 66) : DARK_GREEN);
        button.setBorder(active
                ? BorderFactory.createMatteBorder(0, 4, 0, 0, GOLD)
                : BorderFactory.createEmptyBorder(0, 4, 0, 0));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(event -> navigate(text));
        return button;
    }

    private void navigate(String text) {
        if (text.equals("My Schedule")) {
            return;
        }
        window.dispose();
        if (text.equals("Dashboard")) {
            new StudentDashboardFrame(student).showWindow();
        } else if (text.equals("My Profile")) {
            new StudentProfileFrame(student).showWindow();
        } else if (text.equals("Enrollment")) {
            new StudentEnrollmentFrame(student).showWindow();
        } else if (text.equals("Sign Out")) {
            new LoginFrame().showWindow();
        }
    }

    private javax.swing.ImageIcon loadLogo() {
        try (InputStream stream = getClass().getResourceAsStream("/images/rey-sis-logo.png")) {
            if (stream != null) {
                BufferedImage image = ImageIO.read(stream);
                int height = 118;
                int width = Math.max(1, image.getWidth() * height / image.getHeight());
                return new javax.swing.ImageIcon(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {
            // The text brand remains visible if the optional image cannot be loaded.
        }
        return new javax.swing.ImageIcon();
    }
}
