package org.example.ui.views.cashier;

import org.example.model.Cashier;
import org.example.ui.LoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class CashierReconsilationFrame {
    private static final Color DEEP_GREEN = new Color(0, 59, 44);
    private static final Color DARK_GREEN = new Color(0, 47, 36);
    private static final Color GOLD = new Color(211, 164, 41);
    private static final Color PAGE = new Color(247, 248, 246);
    private static final Color TEXT = new Color(18, 43, 39);
    private static final Color MUTED = new Color(106, 112, 111);
    private static final Color BORDER = new Color(234, 235, 231);

    private final JFrame window = new JFrame("REY SIS | Cashier - Reconciliation");
    private final Cashier cashier;

    public CashierReconsilationFrame() {
        this(null);
    }

    public CashierReconsilationFrame(Cashier cashier) {
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
        navigation.add(createNavigationButton("Reconciliation", IconType.RECONCILIATION, true));
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

        JTextField searchField = new JTextField("Search...");
        searchField.setForeground(MUTED);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(350, 24));
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
        JLabel userSub = new JLabel("Counter 03");
        userSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        userSub.setForeground(MUTED);
        userText.add(userName);
        userText.add(userSub);
        userBadge.add(avatar);
        userBadge.add(userText);

        header.add(rightControls, BorderLayout.EAST);
        rightControls.add(userBadge);

        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(PAGE);

        // Header Section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(28, 28, 20, 28));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("End-of-Shift Reconciliation");
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel("Counter 03 • " + (cashier != null ? cashier.getName() : "Maria Santos") + " • Oct 5, 2026");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        titlePanel.add(heading);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        // 3-Column Reconciliation Cards
        JPanel gridWrapper = new JPanel(new GridLayout(1, 3, 24, 0));
        gridWrapper.setOpaque(false);
        gridWrapper.setBorder(BorderFactory.createEmptyBorder(0, 28, 24, 28));

        gridWrapper.add(createReconCard("Cash Drawer", "SYSTEM EXPECTED", "P 54,000.00", "ACTUAL COUNT", "P 54,000.00", "Balanced: P 0.00", true));
        gridWrapper.add(createReconCard("Digital (GCash/Maya)", "SYSTEM EXPECTED", "P 73,650.00", "TERMINAL BATCH TOTAL", "P 73,650.00", "Balanced: P 0.00", true));
        gridWrapper.add(createReconCard("Bank / Card", "SYSTEM EXPECTED", "P 21,000.00", "POS TERMINAL TOTAL", "P 20,500.00", "Short: -P 500.00", false));

        // Bottom Total Block
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(0, 28, 28, 28));
        bottomWrapper.add(createTotalCard(), BorderLayout.CENTER);

        body.add(titlePanel);
        body.add(gridWrapper);
        body.add(bottomWrapper);

        return body;
    }

    private JPanel createReconCard(String title, String expLabel, String expVal, String actLabel, String actVal, String status, boolean isBalanced) {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLbl.setForeground(TEXT);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel eLabel = new JLabel(expLabel);
        eLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        eLabel.setForeground(MUTED);
        eLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel eVal = new JLabel(expVal);
        eVal.setFont(new Font("SansSerif", Font.BOLD, 18));
        eVal.setForeground(TEXT);
        eVal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel aLabel = new JLabel(actLabel);
        aLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        aLabel.setForeground(MUTED);
        aLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField actField = new JTextField(actVal);
        actField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        actField.setForeground(TEXT);
        actField.setBackground(PAGE);
        actField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isBalanced ? BORDER : new Color(245, 166, 35), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        actField.setAlignmentX(Component.LEFT_ALIGNMENT);
        actField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel statusBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isBalanced ? new Color(230, 245, 233) : new Color(254, 240, 228));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusBox.setOpaque(false);
        statusBox.setLayout(new BorderLayout());
        statusBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        statusBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLbl = new JLabel(status, SwingConstants.CENTER);
        statusLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLbl.setForeground(isBalanced ? new Color(34, 139, 34) : new Color(218, 100, 33));
        statusBox.add(statusLbl, BorderLayout.CENTER);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(24));
        card.add(eLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(eVal);
        card.add(Box.createVerticalStrut(32));
        card.add(aLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(actField);
        card.add(Box.createVerticalStrut(14));
        card.add(statusBox);

        return card;
    }

    private JPanel createTotalCard() {
        CardPanel card = new CardPanel(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel tLbl = new JLabel("Total Shift Collection");
        tLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        tLbl.setForeground(TEXT);

        JLabel tVal = new JLabel("P 148,650.00");
        tVal.setFont(new Font("SansSerif", Font.BOLD, 24));
        tVal.setForeground(TEXT);

        leftPanel.add(tLbl);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(tVal);

        JButton submitBtn = new JButton("Submit Settlement");
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setBackground(new Color(0, 71, 53));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        submitBtn.addActionListener(e -> JOptionPane.showMessageDialog(window, "Settlement Submitted Successfully.", "REY SIS", JOptionPane.INFORMATION_MESSAGE));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        rightPanel.setOpaque(false);
        rightPanel.add(submitBtn);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private void signOut() {
        window.dispose();
        new LoginFrame().showWindow();
    }

    private static BufferedImage loadImage(String resourcePath) {
        try (InputStream stream = CashierReconsilationFrame.class.getResourceAsStream(resourcePath)) {
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