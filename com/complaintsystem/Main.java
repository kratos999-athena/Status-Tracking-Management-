package com.complaintsystem;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ThemeManager.applyGlobalDefaults();
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                ThemeManager.applyGlobalDefaults();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DataStore.getInstance();
            SplashScreen splash = new SplashScreen();
            splash.show(2000, () -> new LoginView());
        });
    }

    static class SplashScreen extends JWindow {

        SplashScreen() {
            getContentPane().setBackground(ThemeManager.BG_PRIMARY);
            setSize(520, 300);
            setLocationRelativeTo(null);
        }

        void show(int durationMs, Runnable onComplete) {
            JPanel content = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, ThemeManager.BG_PRIMARY,
                        getWidth(), getHeight(), ThemeManager.BG_PANEL);
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(ThemeManager.GOLD_BORDER);
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    g2.setColor(new Color(ThemeManager.GOLD_BORDER.getRed(),
                        ThemeManager.GOLD_BORDER.getGreen(), ThemeManager.GOLD_BORDER.getBlue(), 60));
                    g2.drawRect(3, 3, getWidth() - 7, getHeight() - 7);
                    g2.dispose();
                }
            };
            content.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.insets = new Insets(6, 0, 6, 0);

            JLabel lbl1 = new JLabel("CENTRALIZED COMPLAINT", SwingConstants.CENTER);
            lbl1.setFont(new Font("SansSerif", Font.BOLD, 24));
            lbl1.setForeground(ThemeManager.GOLD_PRIMARY);

            JLabel lbl2 = new JLabel("REGISTRATION & TRACKING SYSTEM", SwingConstants.CENTER);
            lbl2.setFont(new Font("SansSerif", Font.BOLD, 18));
            lbl2.setForeground(ThemeManager.GOLD_DIM);

            JLabel org = new JLabel(DataStore.getInstance().getSetting("app.org"), SwingConstants.CENTER);
            org.setFont(ThemeManager.FONT_SMALL);
            org.setForeground(ThemeManager.TEXT_MUTED);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setPreferredSize(new Dimension(400, 6));
            bar.setBackground(ThemeManager.BG_INPUT);
            bar.setForeground(ThemeManager.GOLD_PRIMARY);
            bar.setBorderPainted(false);
            bar.setIndeterminate(false);
            bar.setValue(0);

            JLabel loading = ThemeManager.createMutedLabel("Initializing system...");
            loading.setHorizontalAlignment(SwingConstants.CENTER);

            content.add(lbl1,    gbc);
            content.add(lbl2,    gbc);
            content.add(org,     gbc);
            content.add(bar,     gbc);
            content.add(loading, gbc);

            getContentPane().add(content);
            setVisible(true);

            java.util.Timer fillTimer = new java.util.Timer();
            final int[] progress = {0};
            fillTimer.scheduleAtFixedRate(new java.util.TimerTask() {
                @Override
                public void run() {
                    progress[0] += 2;
                    final int p = progress[0];
                    SwingUtilities.invokeLater(() -> {
                        bar.setValue(p);
                        if (p >= 40)  loading.setText("Loading data store...");
                        if (p >= 70)  loading.setText("Preparing interface...");
                        if (p >= 90)  loading.setText("Almost ready...");
                        if (p >= 100) {
                            fillTimer.cancel();
                            dispose();
                            onComplete.run();
                        }
                    });
                }
            }, 0, durationMs / 50);
        }
    }
}
