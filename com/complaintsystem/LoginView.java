package com.complaintsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

public class LoginView extends JFrame {

    private final DataStore store;
    private JTextField      txtUsername;
    private JPasswordField  txtPassword;
    private JLabel          lblError;
    private JLabel          lblClock;

    public LoginView() {
        this.store = DataStore.getInstance();
        initFrame();
        buildUI();
        setVisible(true);
    }

    private void initFrame() {
        setTitle("Complaint Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new GridBagLayout());
    }

    private void buildUI() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(ThemeManager.GOLD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 500));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(6, 0, 6, 0);
        gbc.gridx   = 0;
        gbc.weightx = 1.0;

        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoRow.setOpaque(false);
        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.GOLD_BORDER);
                g2.fillOval(0, 0, 64, 64);
                g2.setColor(ThemeManager.GOLD_PRIMARY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("C", (64 - fm.stringWidth("C")) / 2, (64 + fm.getAscent()) / 2 - 4);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(64, 64));
        logo.setOpaque(false);
        logoRow.add(logo);

        JLabel appName = new JLabel("COMPLAINT SYSTEM", SwingConstants.CENTER);
        appName.setFont(ThemeManager.FONT_TITLE);
        appName.setForeground(ThemeManager.GOLD_PRIMARY);

        JLabel appSub = new JLabel(store.getSetting("app.org"), SwingConstants.CENTER);
        appSub.setFont(ThemeManager.FONT_SMALL);
        appSub.setForeground(ThemeManager.TEXT_MUTED);

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.GOLD_BORDER);
        sep.setBackground(ThemeManager.GOLD_BORDER);

        JLabel lblUser = ThemeManager.createGoldLabel("Username");
        txtUsername = ThemeManager.createStyledTextField();
        txtUsername.setPreferredSize(new Dimension(0, 38));

        JLabel lblPass = ThemeManager.createGoldLabel("Password");
        txtPassword = new JPasswordField();
        txtPassword.setBackground(ThemeManager.BG_INPUT);
        txtPassword.setForeground(ThemeManager.TEXT_PRIMARY);
        txtPassword.setCaretColor(ThemeManager.GOLD_PRIMARY);
        txtPassword.setFont(ThemeManager.FONT_BODY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtPassword.setPreferredSize(new Dimension(0, 38));
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(ThemeManager.FONT_SMALL);
        lblError.setForeground(ThemeManager.STATUS_OPEN);

        JButton btnLogin = ThemeManager.createGoldButton("LOGIN");
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLogin.addActionListener(e -> doLogin());

        JPanel hintPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        hintPanel.setBackground(ThemeManager.BG_INPUT);
        hintPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        JLabel hintTitle = ThemeManager.createMutedLabel("Default Credentials:");
        hintTitle.setForeground(ThemeManager.GOLD_DIM);
        hintTitle.setFont(ThemeManager.FONT_LABEL);
        hintPanel.add(hintTitle);
        hintPanel.add(ThemeManager.createMutedLabel("Admin:    admin / admin123"));
        hintPanel.add(ThemeManager.createMutedLabel("Agent:    agent1 / agent123"));
        hintPanel.add(ThemeManager.createMutedLabel("Citizen:  citizen1 / pass123"));

        gbc.gridy = 0; card.add(logoRow,   gbc);
        gbc.gridy = 1; card.add(appName,   gbc);
        gbc.gridy = 2; card.add(appSub,    gbc);
        gbc.gridy = 3; gbc.insets = new Insets(14, 0, 14, 0);
        card.add(sep, gbc);
        gbc.insets = new Insets(4, 0, 2, 0);
        gbc.gridy = 4; card.add(lblUser,     gbc);
        gbc.gridy = 5; card.add(txtUsername, gbc);
        gbc.gridy = 6; card.add(lblPass,     gbc);
        gbc.gridy = 7; card.add(txtPassword, gbc);
        gbc.gridy = 8; card.add(lblError,    gbc);
        gbc.gridy = 9; gbc.insets = new Insets(8, 0, 14, 0);
        card.add(btnLogin, gbc);
        gbc.gridy = 10; gbc.insets = new Insets(4, 0, 4, 0);
        card.add(hintPanel, gbc);

        getContentPane().add(card);
        txtUsername.requestFocusInWindow();
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username and password are required.");
            return;
        }

        Optional<User> user = store.authenticate(username, password);
        if (user.isPresent()) {
            user.get().setLastLogin(System.currentTimeMillis());
            store.updateUser(user.get());
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardView(user.get()));
        } else {
            lblError.setText("Invalid credentials or account inactive.");
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }
}
