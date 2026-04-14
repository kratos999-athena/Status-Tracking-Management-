package com.complaintsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class ThemeManager {

    public static final Color BG_PRIMARY     = new Color(10, 10, 15);
    public static final Color BG_SECONDARY   = new Color(18, 18, 26);
    public static final Color BG_PANEL       = new Color(24, 24, 36);
    public static final Color BG_CARD        = new Color(30, 30, 46);
    public static final Color BG_INPUT       = new Color(20, 20, 32);
    public static final Color BG_TABLE_ROW   = new Color(22, 22, 34);
    public static final Color BG_TABLE_ALT   = new Color(28, 28, 42);
    public static final Color BG_TABLE_SEL   = new Color(60, 45, 5);

    public static final Color GOLD_PRIMARY   = new Color(212, 175, 55);
    public static final Color GOLD_LIGHT     = new Color(255, 215, 80);
    public static final Color GOLD_DIM       = new Color(160, 130, 40);
    public static final Color GOLD_ACCENT    = new Color(230, 190, 70);
    public static final Color GOLD_BORDER    = new Color(100, 80, 20);

    public static final Color TEXT_PRIMARY   = new Color(230, 220, 190);
    public static final Color TEXT_SECONDARY = new Color(160, 150, 120);
    public static final Color TEXT_MUTED     = new Color(100, 95, 75);

    public static final Color STATUS_OPEN      = new Color(220, 100, 60);
    public static final Color STATUS_PROGRESS  = new Color(200, 160, 40);
    public static final Color STATUS_RESOLVED  = new Color(70, 180, 100);
    public static final Color STATUS_CLOSED    = new Color(100, 100, 120);

    public static final Color PRIORITY_LOW    = new Color(80, 160, 80);
    public static final Color PRIORITY_MEDIUM = new Color(200, 160, 40);
    public static final Color PRIORITY_HIGH   = new Color(220, 100, 60);
    public static final Color PRIORITY_CRIT   = new Color(200, 50, 50);

    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_BTN     = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_LABEL   = new Font("SansSerif", Font.BOLD, 12);

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background", BG_SECONDARY);
        UIManager.put("OptionPane.background", BG_SECONDARY);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", BG_CARD);
        UIManager.put("Button.foreground", GOLD_PRIMARY);
        UIManager.put("Button.font", FONT_BTN);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("TextField.background", BG_INPUT);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", GOLD_PRIMARY);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("TextArea.background", BG_INPUT);
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground", GOLD_PRIMARY);
        UIManager.put("TextArea.font", FONT_BODY);
        UIManager.put("ComboBox.background", BG_INPUT);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("ComboBox.selectionBackground", BG_TABLE_SEL);
        UIManager.put("ComboBox.selectionForeground", GOLD_LIGHT);
        UIManager.put("List.background", BG_INPUT);
        UIManager.put("List.foreground", TEXT_PRIMARY);
        UIManager.put("List.selectionBackground", BG_TABLE_SEL);
        UIManager.put("List.selectionForeground", GOLD_LIGHT);
        UIManager.put("ScrollBar.background", BG_PRIMARY);
        UIManager.put("ScrollBar.thumb", GOLD_BORDER);
        UIManager.put("ScrollBar.track", BG_PRIMARY);
        UIManager.put("ScrollPane.background", BG_PRIMARY);
        UIManager.put("Viewport.background", BG_PRIMARY);
        UIManager.put("Table.background", BG_TABLE_ROW);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.selectionBackground", BG_TABLE_SEL);
        UIManager.put("Table.selectionForeground", GOLD_LIGHT);
        UIManager.put("Table.gridColor", new Color(40, 40, 55));
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.background", BG_PANEL);
        UIManager.put("TableHeader.foreground", GOLD_PRIMARY);
        UIManager.put("TableHeader.font", FONT_HEADING);
        UIManager.put("TabbedPane.background", BG_SECONDARY);
        UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected", BG_CARD);
        UIManager.put("TabbedPane.contentAreaColor", BG_SECONDARY);
        UIManager.put("TabbedPane.font", FONT_HEADING);
        UIManager.put("PopupMenu.background", BG_CARD);
        UIManager.put("PopupMenu.foreground", TEXT_PRIMARY);
        UIManager.put("MenuItem.background", BG_CARD);
        UIManager.put("MenuItem.foreground", TEXT_PRIMARY);
        UIManager.put("MenuItem.selectionBackground", BG_TABLE_SEL);
        UIManager.put("MenuItem.selectionForeground", GOLD_LIGHT);
        UIManager.put("Separator.foreground", GOLD_BORDER);
        UIManager.put("ToolTip.background", BG_CARD);
        UIManager.put("ToolTip.foreground", GOLD_LIGHT);
        UIManager.put("ToolTip.font", FONT_SMALL);
        UIManager.put("ScrollBar.width", 8);
    }

    public static JButton createGoldButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(80, 60, 5));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(60, 48, 5));
                } else {
                    g2.setColor(BG_CARD);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(GOLD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(GOLD_PRIMARY);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = createGoldButton(text);
        btn.setForeground(STATUS_OPEN);
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = createGoldButton(text);
        btn.setForeground(STATUS_RESOLVED);
        return btn;
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(GOLD_PRIMARY);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    public static JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setBackground(BG_INPUT);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(GOLD_PRIMARY);
        area.setFont(FONT_BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return area;
    }

    public static JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_BODY);
        combo.setBorder(BorderFactory.createLineBorder(GOLD_BORDER, 1));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? BG_TABLE_SEL : BG_INPUT);
                setForeground(isSelected ? GOLD_LIGHT : TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return combo;
    }

    public static JLabel createGoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(GOLD_PRIMARY);
        label.setFont(FONT_HEADING);
        return label;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(FONT_BODY);
        return label;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        label.setFont(FONT_SMALL);
        return label;
    }

    public static Border createGoldBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GOLD_BORDER, 1),
            title
        );
        border.setTitleColor(GOLD_PRIMARY);
        border.setTitleFont(FONT_HEADING);
        return border;
    }

    public static Border createGoldBorderNoTitle() {
        return BorderFactory.createLineBorder(GOLD_BORDER, 1);
    }

    public static JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBackground(BG_PRIMARY);
        scroll.getViewport().setBackground(BG_PRIMARY);
        scroll.setBorder(BorderFactory.createLineBorder(GOLD_BORDER, 1));
        scroll.getVerticalScrollBar().setBackground(BG_PRIMARY);
        scroll.getHorizontalScrollBar().setBackground(BG_PRIMARY);
        return scroll;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        return panel;
    }

    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_MUTED;
        switch (status.toUpperCase()) {
            case "OPEN":        return STATUS_OPEN;
            case "IN_PROGRESS": return STATUS_PROGRESS;
            case "RESOLVED":    return STATUS_RESOLVED;
            case "CLOSED":      return STATUS_CLOSED;
            default:            return TEXT_MUTED;
        }
    }

    public static Color getPriorityColor(String priority) {
        if (priority == null) return TEXT_MUTED;
        switch (priority.toUpperCase()) {
            case "LOW":      return PRIORITY_LOW;
            case "MEDIUM":   return PRIORITY_MEDIUM;
            case "HIGH":     return PRIORITY_HIGH;
            case "CRITICAL": return PRIORITY_CRIT;
            default:         return TEXT_MUTED;
        }
    }
}
