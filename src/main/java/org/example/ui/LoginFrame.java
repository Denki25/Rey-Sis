package org.example.ui;

import org.example.auth.LoginValidator;
import org.example.data.MockStudentRepository;
import org.example.ui.views.admin.AdminDashboardFrame;
import org.example.ui.views.cashier.CashierDashboardFrame;
import org.example.ui.views.student.StudentDashboardFrame;
import org.example.model.Cashier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

public class LoginFrame {
    private static final Color DEEP_GREEN = new Color(7, 57, 43);
    private static final Color GOLD = new Color(207, 160, 48);
    private static final Color TEXT = new Color(22, 30, 45);
    private static final Color MUTED = new Color(125, 135, 151);
    private static final String REMEMBERED_USERNAME_KEY = "rememberedUsername";

    private final JFrame window = new JFrame("REY SIS | Login");
    private final Preferences preferences = Preferences.userNodeForPackage(LoginFrame.class);
    private final PromptTextField usernameField = new PromptTextField("Enter your username");
    private final PromptPasswordField passwordField = new PromptPasswordField("Enter your password");
    private final JCheckBox rememberMe = new JCheckBox("Remember me");

    public LoginFrame() {
        loadRememberedUsername();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(960, 620));
        window.setSize(1200, 760);
        window.setLocationRelativeTo(null);
        JPanel content = new JPanel(new GridLayout(1, 2));
        content.add(new WelcomePanel());
        content.add(createLoginPanel());
        window.setContentPane(content);
    }

    public void showWindow() {
        window.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(new Color(247, 249, 250));
        RoundedPanel card = new RoundedPanel(22, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 42, 30, 42));
        card.add(createHelpRow(), BorderLayout.NORTH);
        card.add(createLoginForm(), BorderLayout.CENTER);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(20, 18, 20, 18);
        area.add(card, constraints);
        return area;
    }

    private JPanel createHelpRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        row.setOpaque(false);
        JLabel help = new JLabel("Need Help?");
        help.setFont(new Font("SansSerif", Font.PLAIN, 12));
        help.setForeground(new Color(63, 72, 88));
        JButton helpButton = new JButton(new HelpIcon());
        helpButton.setPreferredSize(new Dimension(22, 22));
        helpButton.setForeground(new Color(119, 131, 148));
        helpButton.setContentAreaFilled(false);
        helpButton.setBorder(BorderFactory.createLineBorder(new Color(170, 180, 192), 1, true));
        helpButton.setFocusPainted(false);
        helpButton.setToolTipText("Help");
        helpButton.addActionListener(event -> showMessage("For assistance, contact the REY SIS support team."));
        row.add(help);
        row.add(helpButton);
        return row;
    }

    private JPanel createLoginForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(30, 0, 8, 0);
        form.add(new LogoPanel(), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(28, 0, 0, 0);
        JLabel title = new JLabel("Welcome Back");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setForeground(TEXT);
        form.add(title, constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 22, 0);
        JLabel subtitle = new JLabel("Sign in to your REY SIS account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);
        form.add(subtitle, constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 5, 0);
        form.add(fieldLabel("Username"), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 12, 0);
        form.add(createInput(usernameField, "Enter your username", new UserIcon()), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 5, 0);
        form.add(fieldLabel("Password"), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 10, 0);
        form.add(createPasswordInput(), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 13, 0);
        form.add(createOptionsRow(), constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 16, 0);
        JButton signInButton = createSignInButton();
        form.add(signInButton, constraints);
        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 0, 0);
        form.add(createDivider(), constraints);
        usernameField.addActionListener(this::attemptLogin);
        passwordField.addActionListener(this::attemptLogin);
        return form;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT);
        return label;
    }

    private JPanel createInput(JTextField field, String placeholder, javax.swing.Icon icon) {
        InputPanel inputPanel = new InputPanel(icon);
        field.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 10));
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setForeground(MUTED);
        field.setOpaque(false);
        inputPanel.add(field, BorderLayout.CENTER);
        return inputPanel;
    }

    private JPanel createPasswordInput() {
        InputPanel inputPanel = new InputPanel(new LockIcon());
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passwordField.setForeground(MUTED);
        passwordField.setOpaque(false);
        inputPanel.add(passwordField, BorderLayout.CENTER);
        JButton visibilityButton = new JButton(new EyeIcon());
        visibilityButton.setToolTipText("Show password");
        visibilityButton.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        visibilityButton.setContentAreaFilled(false);
        visibilityButton.addActionListener(event -> {
            char echo = passwordField.getEchoChar();
            passwordField.setEchoChar(echo == 0 ? '\u2022' : (char) 0);
            visibilityButton.setToolTipText(echo == 0 ? "Hide password" : "Show password");
        });
        inputPanel.add(visibilityButton, BorderLayout.EAST);
        return inputPanel;
    }

    private JPanel createOptionsRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        rememberMe.setFont(new Font("SansSerif", Font.PLAIN, 11));
        rememberMe.setForeground(new Color(66, 74, 87));
        rememberMe.setOpaque(false);
        rememberMe.setFocusPainted(false);
        rememberMe.addActionListener(event -> {
            if (!rememberMe.isSelected()) {
                preferences.remove(REMEMBERED_USERNAME_KEY);
            }
        });
        JButton forgotButton = new JButton("Forgot password?");
        forgotButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        forgotButton.setForeground(new Color(181, 133, 25));
        forgotButton.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 0));
        forgotButton.setContentAreaFilled(false);
        forgotButton.addActionListener(event -> showMessage("Password recovery will be connected to authentication later."));
        row.add(rememberMe, BorderLayout.WEST);
        row.add(forgotButton, BorderLayout.EAST);
        return row;
    }

    private JButton createSignInButton() {
        JButton button = new JButton("Sign In", new ArrowIcon());
        button.setPreferredSize(new Dimension(0, 42));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(GOLD);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setIconTextGap(9);
        button.addActionListener(this::attemptLogin);
        return button;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel(new GridLayout(1, 3, 12, 0));
        divider.setOpaque(false);
        divider.add(new JSeparatorPanel());
        JLabel or = new JLabel("or", SwingConstants.CENTER);
        or.setFont(new Font("SansSerif", Font.PLAIN, 10));
        or.setForeground(new Color(164, 173, 185));
        divider.add(or);
        divider.add(new JSeparatorPanel());
        return divider;
    }

    private void attemptLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();

        // Validate empty fields
        String validationMessage = LoginValidator.validate(username, passwordChars);
        if (validationMessage != null) {
            showMessage(validationMessage);
            return;
        }

        String password = new String(passwordChars);

        // Role-Based Authentication Logic
        if (username.equalsIgnoreCase("admin") && password.equals("admin123")) {
            window.dispose();
            new AdminDashboardFrame().showWindow();
        } else if (username.equalsIgnoreCase("cashier") && password.equals("cashier123")) {
            window.dispose(); // Closes the login window
            Cashier loggedInCashier = new Cashier("Head Cashier", "CASH-001");
            new CashierDashboardFrame(loggedInCashier).showWindow();
        } else if (username.equalsIgnoreCase("student") && password.equals("student123")) {
            window.dispose();
            new StudentDashboardFrame(MockStudentRepository.getSampleStudent()).showWindow();
        } else {
            showMessage("Invalid username or password.");
        }

        boolean validCredentials = username.equalsIgnoreCase("admin") && password.equals("admin123")
                || username.equalsIgnoreCase("cashier") && password.equals("cashier123")
                || username.equalsIgnoreCase("student") && password.equals("student123");
        if (validCredentials) {
            saveRememberedUsername(username);
        }

        Arrays.fill(passwordChars, '0');
    }

    private void loadRememberedUsername() {
        String rememberedUsername = preferences.get(REMEMBERED_USERNAME_KEY, "");
        if (!rememberedUsername.isBlank()) {
            usernameField.setText(rememberedUsername);
            rememberMe.setSelected(true);
        }
    }

    private void saveRememberedUsername(String username) {
        if (rememberMe.isSelected()) {
            preferences.put(REMEMBERED_USERNAME_KEY, username);
        } else {
            preferences.remove(REMEMBERED_USERNAME_KEY);
        }
    }

    private void showMessage(String message) {
        JDialog dialog = new JDialog(window, "REY SIS", true);
        dialog.setLayout(new BorderLayout(16, 16));
        JLabel text = new JLabel("<html>" + message.replace("\n", "<br>") + "</html>");
        text.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));
        JButton close = new JButton("OK");
        close.addActionListener(event -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(close);
        dialog.add(text, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    private static class WelcomePanel extends JPanel {
        private final BufferedImage backgroundImage = loadFirstImage("/images/university-building.jpg");

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            int width = getWidth();
            int height = getHeight();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundImage != null) {
                drawCoverImage(g, backgroundImage, width, height);
            } else {
                g.setColor(new Color(3, 34, 27));
                g.fillRect(0, 0, width, height);
            }
            g.setPaint(new GradientPaint(0, 0, new Color(5, 48, 37, 195), width, 0, new Color(5, 48, 37, 80)));
            g.fillRect(0, 0, width, height);
            g.setPaint(new GradientPaint(0, 0, new Color(4, 44, 34, 75), 0, height, new Color(3, 34, 27, 230)));
            g.fillRect(0, 0, width, height);
            int left = 46;
            g.setColor(new Color(232, 235, 228));
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("REY UNIVERSITY", left, 40);
            g.setColor(GOLD);
            g.fillRect(left, 51, 34, 2);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 48));
            g.drawString("Your", left, height / 2 - 95);
            g.setColor(new Color(222, 170, 48));
            g.setFont(new Font("SansSerif", Font.ITALIC, 48));
            g.drawString("Academic", left, height / 2 - 40);
            g.drawString("Journey,", left, height / 2 + 14);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 48));
            g.drawString("Simplified.", left, height / 2 + 68);
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g.setColor(new Color(224, 230, 224));
            g.drawString("REY SIS provides a seamless and secure experience", left + 4, height - 194);
            g.drawString("for enrollment, academic records, and more \u2014", left + 4, height - 175);
            g.drawString("designed for the REY University community.", left + 4, height - 156);
            drawFeature(g, left + 12, height - 98, "enroll", "Enroll", "with ease");
            drawFeature(g, left + 136, height - 98, "progress", "Track", "your progress");
            drawFeature(g, left + 260, height - 98, "users", "Access", "what you need");
            g.setColor(GOLD);
            g.fillRoundRect(left + 7, height - 28, 20, 5, 5, 5);
            g.dispose();
        }

        private void drawFeature(Graphics2D g, int x, int y, String icon, String title, String subtitle) {
            javax.swing.Icon featureIcon = switch (title) {
                case "Enroll" -> new EnrollIcon();
                case "Track" -> new ProgressIcon();
                default -> new UsersIcon();
            };
            featureIcon.paintIcon(this, g, x, y - 26);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            g.drawString(title, x, y + 13);
            g.setColor(new Color(205, 218, 211));
            g.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g.drawString(subtitle, x - 5, y + 27);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color color;

        RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class InputPanel extends JPanel {
        InputPanel(javax.swing.Icon icon) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setPreferredSize(new Dimension(20, 0));
            add(iconLabel, BorderLayout.WEST);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(248, 249, 251));
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g.setColor(new Color(226, 229, 234));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g.dispose();
            super.paintComponent(graphics);
        }

        public Dimension getPreferredSize() { return new Dimension(0, 40); }
    }

    private static class JSeparatorPanel extends JPanel {
        JSeparatorPanel() {
            setPreferredSize(new Dimension(100, 1));
            setBackground(new Color(226, 229, 234));
        }
    }

    private static class LogoPanel extends JPanel {
        private final BufferedImage logoImage = loadFirstImage("/images/rey-sis-logo.png");

        LogoPanel() {
            setPreferredSize(new Dimension(0, 150));
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int center = getWidth() / 2;
            int top = 12;
            if (logoImage != null) {
                drawContainedImage(g, logoImage, center - 62, top, 124, 136);
                g.setColor(new Color(222, 174, 61));
                g.fillRect(center - 62, top + 145, 40, 1);
                g.fillRect(center + 22, top + 145, 40, 1);
                g.dispose();
                return;
            }
            g.dispose();
        }
    }

    private static BufferedImage loadFirstImage(String... resourcePaths) {
        for (String resourcePath : resourcePaths) {
            try (InputStream stream = LoginFrame.class.getResourceAsStream(resourcePath)) {
                if (stream != null) {
                    BufferedImage image = ImageIO.read(stream);
                    if (image != null) {
                        return image;
                    }
                }
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    private static void drawCoverImage(Graphics2D graphics, BufferedImage image, int width, int height) {
        double scale = Math.max((double) width / image.getWidth(), (double) height / image.getHeight());
        int imageWidth = (int) Math.ceil(image.getWidth() * scale);
        int imageHeight = (int) Math.ceil(image.getHeight() * scale);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.drawImage(image, x, y, imageWidth, imageHeight, null);
    }

    private static void drawContainedImage(Graphics2D graphics, BufferedImage image, int x, int y, int width, int height) {
        double scale = Math.min((double) width / image.getWidth(), (double) height / image.getHeight());
        int imageWidth = (int) Math.floor(image.getWidth() * scale);
        int imageHeight = (int) Math.floor(image.getHeight() * scale);
        int imageX = x + (width - imageWidth) / 2;
        int imageY = y + (height - imageHeight) / 2;
        graphics.drawImage(image, imageX, imageY, imageWidth, imageHeight, null);
    }

    private static abstract class SimpleIcon implements javax.swing.Icon {
        public int getIconWidth() { return 18; }
        public int getIconHeight() { return 18; }
    }

    private static class HelpIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = iconGraphics(graphics);
            g.setColor(new Color(119, 131, 148));
            g.setStroke(new BasicStroke(1.2f));
            g.drawOval(x + 2, y + 2, 14, 14);
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            g.drawString("?", x + 6, y + 13);
            g.dispose();
        }
    }

    private static class ArrowIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = iconGraphics(graphics);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 2, y + 9, x + 15, y + 9);
            g.drawLine(x + 10, y + 4, x + 15, y + 9);
            g.drawLine(x + 10, y + 14, x + 15, y + 9);
            g.dispose();
        }
    }

    private static class EnrollIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = iconGraphics(graphics);
            g.setColor(GOLD);
            g.setStroke(new BasicStroke(1.4f));
            g.drawRect(x + 2, y + 3, 14, 13);
            g.drawLine(x + 9, y + 3, x + 9, y + 16);
            g.dispose();
        }
    }

    private static class ProgressIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = iconGraphics(graphics);
            g.setColor(GOLD);
            g.setStroke(new BasicStroke(1.8f));
            g.drawLine(x + 4, y + 15, x + 4, y + 9);
            g.drawLine(x + 9, y + 15, x + 9, y + 4);
            g.drawLine(x + 14, y + 15, x + 14, y + 7);
            g.dispose();
        }
    }

    private static class UsersIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = iconGraphics(graphics);
            g.setColor(GOLD);
            g.setStroke(new BasicStroke(1.4f));
            g.drawOval(x + 5, y + 2, 6, 6);
            g.drawArc(x + 2, y + 9, 12, 8, 0, 180);
            g.drawArc(x + 9, y + 6, 7, 7, 275, 180);
            g.dispose();
        }
    }

    private static Graphics2D iconGraphics(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    private static class PromptTextField extends JTextField {
        private final String prompt;

        PromptTextField(String prompt) {
            this.prompt = prompt;
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getText().isEmpty()) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setColor(MUTED);
                g.setFont(getFont());
                g.drawString(prompt, 4, getHeight() / 2 + 5);
                g.dispose();
            }
        }
    }

    private static class PromptPasswordField extends JPasswordField {
        private final String prompt;

        PromptPasswordField(String prompt) {
            this.prompt = prompt;
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getPassword().length == 0) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setColor(MUTED);
                g.setFont(getFont());
                g.drawString(prompt, 4, getHeight() / 2 + 5);
                g.dispose();
            }
        }
    }

    private static class UserIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(new Color(154, 165, 180));
            g.setStroke(new BasicStroke(1.4f));
            g.drawOval(x + 5, y + 2, 6, 6);
            g.drawArc(x + 2, y + 9, 12, 8, 0, 180);
            g.dispose();
        }
    }

    private static class LockIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(new Color(154, 165, 180));
            g.setStroke(new BasicStroke(1.4f));
            g.drawRoundRect(x + 3, y + 8, 12, 9, 2, 2);
            g.drawArc(x + 5, y + 2, 8, 11, 0, 180);
            g.dispose();
        }
    }

    private static class EyeIcon extends SimpleIcon {
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(new Color(143, 155, 173));
            g.setStroke(new BasicStroke(1.4f));
            g.drawOval(x + 2, y + 5, 14, 8);
            g.fillOval(x + 7, y + 8, 4, 4);
            g.dispose();
        }
    }
}
