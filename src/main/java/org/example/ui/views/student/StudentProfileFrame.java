package org.example.ui.views.student;

import org.example.model.Student;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StudentProfileFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | My Profile");
    private final Student student;
    private final JTextField nameField = new JTextField();
    private final JTextField studentIdField = new JTextField();
    private final JTextField dateOfBirthField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JLabel saveStatus = new JLabel(" ");
    private final AvatarPanel avatarPanel;
    private JLabel profileNameLabel;
    private JLabel profileDetailsLabel;
    private boolean editing;

    public StudentProfileFrame(Student student) {
        this.student = student;
        this.avatarPanel = new AvatarPanel();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(1060, 680));
        window.setSize(1360, 820);
        window.setLocationRelativeTo(null);
        window.setContentPane(createContent());
        setEditing(false);
    }

    public void showWindow() {
        window.setVisible(true);
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
        navigation.add(createNavigationButton("My Profile", StudentDashboardFrame.IconType.PROFILE, true));
        navigation.add(createNavigationButton("Enrollment", StudentDashboardFrame.IconType.ENROLLMENT, false));
        navigation.add(createNavigationButton("My Schedule", StudentDashboardFrame.IconType.CALENDAR, false));
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

    private void openEnrollment() {
        window.dispose();
        new StudentEnrollmentFrame(student).showWindow();
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
                } else if (text.equals("Enrollment")) {
                    openEnrollment(); // Replace showComingSoon("Enrollment")
                } else if (text.equals("My Profile")) {
                    showProfileMessage();
                } else if (text.equals("My Schedule")) {
                    window.dispose();
                    new StudentScheduleFrame(student).showWindow();
                }else if (text.equals("Requests")) {
                    window.dispose();
                    new StudentRequestFrame(student).showWindow(); // <--- Add this routing
                }else {
                    showComingSoon(text);
                }
            });
        }
        return button;
    }

    private JPanel createPageHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(18, 24, 18, 24)));
        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel("My Profile");
        heading.setFont(new Font("Serif", Font.BOLD, 24));
        heading.setForeground(TEXT);
        JLabel subtitle = new JLabel("Manage your personal information and account details");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(MUTED);
        title.add(heading);
        title.add(Box.createVerticalStrut(4));
        title.add(subtitle);
        header.add(title, BorderLayout.WEST);
        return header;
    }



    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(PAGE);
        body.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));
        body.setPreferredSize(new Dimension(1120, 680));
        body.add(createProfileHeader(), BorderLayout.NORTH);
        JPanel cards = new JPanel(new GridLayout(1, 2, 14, 0));
        cards.setOpaque(false);
        cards.add(createPersonalCard());
        cards.add(createAcademicCard());
        body.add(cards, BorderLayout.CENTER);
        body.add(createSummaryCards(), BorderLayout.SOUTH);
        return body;
    }

    private JPanel createProfileHeader() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(20, 0));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        card.add(avatarPanel, BorderLayout.WEST);
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        profileNameLabel = new JLabel(student.getName());
        profileNameLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        profileNameLabel.setForeground(TEXT);
        details.add(profileNameLabel);
        details.add(Box.createVerticalStrut(5));
        profileDetailsLabel = detailLabel("");
        details.add(profileDetailsLabel);
        refreshProfileHeader();
        card.add(details, BorderLayout.CENTER);
        JButton edit = actionButton("Edit Profile", () -> setEditing(!editing));
        card.add(edit, BorderLayout.EAST);
        return card;
    }

    private JLabel detailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setForeground(MUTED);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }

    private JPanel createPersonalCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.add(cardHeader("Personal Information", "Editable details"), BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(6, 1, 0, 8));
        fields.setOpaque(false);
        configureField(nameField, student.getName());
        configureField(studentIdField, student.getStudentId());
        configureField(dateOfBirthField, student.getDateOfBirth());
        configureField(addressField, student.getAddress());
        configureField(contactField, student.getContactNumber());
        configureField(emailField, student.getEmail());
        addField(fields, "Full Name", nameField);
        addField(fields, "Student ID", studentIdField);
        addField(fields, "Date of Birth", dateOfBirthField);
        addField(fields, "Address", addressField);
        addField(fields, "Contact Number", contactField);
        addField(fields, "Email Address", emailField);
        card.add(fields, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton cancel = actionButton("Cancel", () -> loadFields());
        JButton save = new JButton("Save Changes");
        save.setFont(new Font("SansSerif", Font.BOLD, 10));
        save.setForeground(Color.WHITE);
        save.setBackground(GOLD);
        save.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        save.setFocusPainted(false);
        save.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        save.addActionListener(event -> saveChanges());
        actions.add(cancel);
        actions.add(save);
        actions.add(saveStatus);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAcademicCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(cardHeader("Academic Information", "View only"), BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(6, 1, 0, 8));
        fields.setOpaque(false);
        addReadOnlyRow(fields, "Program", student.getProgram());
        addReadOnlyRow(fields, "Year Level", student.getYearLevel());
        addReadOnlyRow(fields, "Section", student.getSection());
        addReadOnlyRow(fields, "Academic Status", student.getAcademicStatus());
        addReadOnlyRow(fields, "Curriculum Year", student.getCurriculumYear());
        addReadOnlyRow(fields, "Adviser", student.getAdviser());
        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSummaryCards() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);
        cards.add(summaryCard("Enrolled Subjects", String.valueOf(student.getEnrolledSubjects()), StudentDashboardFrame.IconType.ENROLLMENT, DEEP_GREEN));
        cards.add(summaryCard("Year Level", student.getYearLevel(), StudentDashboardFrame.IconType.PROFILE, GOLD));
        cards.add(summaryCard("Units Enrolled", student.getEnrolledUnits() + " / " + student.getMaximumUnits(), StudentDashboardFrame.IconType.STATS, DEEP_GREEN));
        cards.add(summaryCard("Academic Status", student.getAcademicStatus(), StudentDashboardFrame.IconType.STAR, GOLD));
        return cards;
    }

    private JPanel summaryCard(String title, String value, StudentDashboardFrame.IconType iconType, Color iconColor) {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout(5, 4));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 10));
        card.add(new JLabel(new StudentDashboardFrame.DashboardIcon(iconType, iconColor)), BorderLayout.NORTH);
        JLabel number = new JLabel(value);
        number.setFont(new Font("SansSerif", Font.BOLD, 22));
        number.setForeground(TEXT);
        card.add(number, BorderLayout.CENTER);
        card.add(smallLabel(title, 9, MUTED), BorderLayout.SOUTH);
        return card;
    }

    private JPanel cardHeader(String title, String badgeText) {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 17));
        titleLabel.setForeground(TEXT);
        header.add(titleLabel, BorderLayout.WEST);
        JLabel badge = new JLabel(badgeText, SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.PLAIN, 9));
        badge.setForeground(badgeText.equals("View only") ? MUTED : DEEP_GREEN);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 7, 4, 7));
        badge.setOpaque(true);
        badge.setBackground(badgeText.equals("View only") ? new Color(241, 242, 239) : new Color(232, 242, 236));
        header.add(badge, BorderLayout.EAST);
        return header;
    }

    private void addField(JPanel parent, String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.add(smallLabel(label, 10, MUTED), BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        parent.add(row);
    }

    private void addReadOnlyRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.add(smallLabel(label, 10, MUTED), BorderLayout.WEST);
        row.add(smallLabel(value, 10, TEXT), BorderLayout.CENTER);
        parent.add(row);
    }

    private void configureField(JTextField field, String value) {
        field.setText(value);
        field.setFont(new Font("SansSerif", Font.PLAIN, 10));
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
    }

    private void loadFields() {
        nameField.setText(student.getName());
        studentIdField.setText(student.getStudentId());
        dateOfBirthField.setText(student.getDateOfBirth());
        addressField.setText(student.getAddress());
        contactField.setText(student.getContactNumber());
        emailField.setText(student.getEmail());
        setEditing(false);
    }

    private void setEditing(boolean value) {
        editing = value;
        nameField.setEditable(value);
        dateOfBirthField.setEditable(value);
        addressField.setEditable(value);
        contactField.setEditable(value);
        emailField.setEditable(value);
        studentIdField.setEditable(false);
        Color background = value ? Color.WHITE : new Color(247, 248, 246);
        nameField.setBackground(background);
        studentIdField.setBackground(new Color(241, 242, 239));
        dateOfBirthField.setBackground(background);
        addressField.setBackground(background);
        contactField.setBackground(background);
        emailField.setBackground(background);
    }

    private void saveChanges() {
        if (nameField.getText().isBlank() || addressField.getText().isBlank()
                || contactField.getText().isBlank() || emailField.getText().isBlank()) {
            saveStatus.setForeground(new Color(190, 70, 55));
            saveStatus.setText("Complete required fields");
            return;
        }
        student.setDateOfBirth(dateOfBirthField.getText().trim());
        student.setName(nameField.getText().trim());
        student.setAddress(addressField.getText().trim());
        student.setContactNumber(contactField.getText().trim());
        student.setEmail(emailField.getText().trim());
        refreshProfileHeader();
        saveStatus.setForeground(DEEP_GREEN);
        saveStatus.setText("Saved");
        setEditing(false);
    }

    private void refreshProfileHeader() {
        if (profileNameLabel != null) {
            profileNameLabel.setText(student.getName());
        }
        if (profileDetailsLabel != null) {
            profileDetailsLabel.setText(student.getStudentId() + "  |  " + student.getProgram()
                    + "  |  " + student.getYearLevel() + "  |  " + student.getEmail()
                    + "  |  " + student.getContactNumber() + "  |  " + student.getLocation());
        }
    }

    private JButton actionButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 9));
        button.setForeground(DEEP_GREEN);
        button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DEEP_GREEN));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(1, 2, 1, 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(event -> action.run());
        return button;
    }

    private JLabel smallLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }

    private void openDashboard() {
        window.dispose();
        new StudentDashboardFrame(student).showWindow();
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private void showProfileMessage() {
        JOptionPane.showMessageDialog(window, "You are already viewing My Profile.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showComingSoon(String module) {
        JOptionPane.showMessageDialog(window, module + " is coming next.", "REY SIS", JOptionPane.INFORMATION_MESSAGE);
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = StudentProfileFrame.class.getResourceAsStream(resourcePath)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception exception) {
            return null;
        }
    }

    private final class AvatarPanel extends JPanel {
        private BufferedImage image;

        AvatarPanel() {
            setPreferredSize(new Dimension(112, 112));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            loadSavedImage();
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent event) {
                    chooseImage();
                }
            });
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 8;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g.setColor(new Color(231, 241, 234));
            g.fillOval(x, y, size, size);
            if (image != null) {
                ShapeClip.paintContainedCircle(g, image, x, y, size);
            } else {
                g.setColor(DEEP_GREEN);
                g.setFont(new Font("SansSerif", Font.BOLD, 31));
                String initials = "JR";
                int width = g.getFontMetrics().stringWidth(initials);
                g.drawString(initials, getWidth() / 2 - width / 2, getHeight() / 2 + 11);
            }
            g.setColor(GOLD);
            g.setStroke(new java.awt.BasicStroke(3));
            g.drawOval(x, y, size, size);
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g.setColor(DEEP_GREEN);
            g.drawString("Click to change", 16, getHeight() - 1);
            g.dispose();
        }

        private void loadSavedImage() {
            if (student.getAvatarPath() != null) {
                try {
                    image = ImageIO.read(new File(student.getAvatarPath()));
                } catch (Exception ignored) {
                    image = null;
                }
            }
        }

        private void chooseImage() {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose profile picture");
            chooser.setFileFilter(new FileNameExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));
            if (chooser.showOpenDialog(window) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            try {
                BufferedImage selected = ImageIO.read(chooser.getSelectedFile());
                if (selected == null) {
                    throw new IllegalArgumentException("Unsupported image");
                }
                Path directory = Paths.get(System.getProperty("user.home"), ".rey-sis");
                Files.createDirectories(directory);
                Path target = directory.resolve("profile-avatar.png");
                ImageIO.write(selected, "png", target.toFile());
                image = selected;
                student.setAvatarPath(target.toString());
                repaint();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(window, "The selected image could not be saved.", "REY SIS", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ShapeClip {
        private static void paintContainedCircle(Graphics2D graphics, BufferedImage image, int x, int y, int size) {
            java.awt.Shape oldClip = graphics.getClip();
            graphics.setClip(new java.awt.geom.Ellipse2D.Double(x, y, size, size));
            double scale = Math.max((double) size / image.getWidth(), (double) size / image.getHeight());
            int width = (int) Math.ceil(image.getWidth() * scale);
            int height = (int) Math.ceil(image.getHeight() * scale);
            graphics.drawImage(image, x + (size - width) / 2, y + (size - height) / 2, width, height, null);
            graphics.setClip(oldClip);
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
            g.setColor(background);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.setColor(BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
