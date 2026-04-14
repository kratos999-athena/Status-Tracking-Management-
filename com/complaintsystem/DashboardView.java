package com.complaintsystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class DashboardView extends JFrame {

    private final DataStore  store;
    private       User       currentUser;

    private JLabel           lblUserName;
    private JLabel           lblUserRole;
    private JLabel           lblStatusBar;
    private JLabel           lblTotalStat;
    private JLabel           lblOpenStat;
    private JLabel           lblResolvedStat;
    private JLabel           lblCriticalStat;
    private JTabbedPane      tabbedPane;
    private ComplaintTable   complaintTable;
    private JTextField       txtSearch;
    private JComboBox<String> cmbFilterStatus;
    private JComboBox<String> cmbFilterPriority;
    private JComboBox<String> cmbFilterCategory;
    private JPanel           detailPanel;
    private Complaint        selectedComplaint;
    private Timer            refreshTimer;

    public DashboardView(User user) {
        this.currentUser = user;
        this.store       = DataStore.getInstance();
        initFrame();
        buildUI();
        refreshStats();
        startRefreshTimer();
        setVisible(true);
    }

    private void initFrame() {
        setTitle("Centralized Complaint Registration & Status Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 820));
        setPreferredSize(new Dimension(1440, 900));
        getContentPane().setBackground(ThemeManager.BG_PRIMARY);
        setLayout(new BorderLayout());
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        add(buildTopBar(),      BorderLayout.NORTH);
        add(buildSideBar(),     BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.BG_PANEL);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(ThemeManager.GOLD_BORDER);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 64));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        bar.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JPanel logoBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.GOLD_BORDER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(ThemeManager.GOLD_PRIMARY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("C")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("C", x, y);
                g2.dispose();
            }
        };
        logoBox.setPreferredSize(new Dimension(42, 42));
        logoBox.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 0));
        titlePanel.setOpaque(false);
        JLabel appTitle = new JLabel("COMPLAINT MANAGEMENT SYSTEM");
        appTitle.setFont(ThemeManager.FONT_TITLE);
        appTitle.setForeground(ThemeManager.GOLD_PRIMARY);
        JLabel appSubTitle = new JLabel(store.getSetting("app.org"));
        appSubTitle.setFont(ThemeManager.FONT_SMALL);
        appSubTitle.setForeground(ThemeManager.TEXT_MUTED);
        titlePanel.add(appTitle);
        titlePanel.add(appSubTitle);

        leftPanel.add(logoBox);
        leftPanel.add(titlePanel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setOpaque(false);

        JPanel userInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        userInfo.setOpaque(false);
        lblUserName = new JLabel(currentUser.getFullName());
        lblUserName.setFont(ThemeManager.FONT_HEADING);
        lblUserName.setForeground(ThemeManager.TEXT_PRIMARY);
        lblUserName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUserRole = new JLabel(currentUser.getRoleDisplayName() + " | " + currentUser.getDepartment());
        lblUserRole.setFont(ThemeManager.FONT_SMALL);
        lblUserRole.setForeground(ThemeManager.GOLD_DIM);
        lblUserRole.setHorizontalAlignment(SwingConstants.RIGHT);
        userInfo.add(lblUserName);
        userInfo.add(lblUserRole);

        JButton btnLogout = ThemeManager.createDangerButton("Logout");
        btnLogout.setPreferredSize(new Dimension(90, 32));
        btnLogout.addActionListener(e -> handleLogout());

        rightPanel.add(userInfo);
        rightPanel.add(btnLogout);

        bar.add(leftPanel,  BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSideBar() {
        JPanel side = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.BG_PANEL);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(ThemeManager.GOLD_BORDER);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        side.setPreferredSize(new Dimension(220, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        side.setOpaque(false);

        side.add(buildStatCard("TOTAL", "0", ThemeManager.GOLD_PRIMARY, "lblTotal"));
        side.add(Box.createVerticalStrut(10));
        side.add(buildStatCard("OPEN", "0", ThemeManager.STATUS_OPEN, "lblOpen"));
        side.add(Box.createVerticalStrut(10));
        side.add(buildStatCard("RESOLVED", "0", ThemeManager.STATUS_RESOLVED, "lblResolved"));
        side.add(Box.createVerticalStrut(10));
        side.add(buildStatCard("CRITICAL", "0", ThemeManager.PRIORITY_CRIT, "lblCritical"));
        side.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.GOLD_BORDER);
        sep.setBackground(ThemeManager.GOLD_BORDER);
        sep.setMaximumSize(new Dimension(200, 2));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(sep);
        side.add(Box.createVerticalStrut(20));

        String[] navItems = {"Dashboard", "My Complaints", "All Complaints", "Reports"};
        for (String item : navItems) {
            side.add(buildNavButton(item));
            side.add(Box.createVerticalStrut(4));
        }

        if (currentUser.getRole() == User.Role.ADMIN) {
            side.add(Box.createVerticalStrut(10));
            JSeparator sep2 = new JSeparator();
            sep2.setForeground(ThemeManager.GOLD_BORDER);
            sep2.setBackground(ThemeManager.GOLD_BORDER);
            sep2.setMaximumSize(new Dimension(200, 2));
            sep2.setAlignmentX(Component.CENTER_ALIGNMENT);
            side.add(sep2);
            side.add(Box.createVerticalStrut(10));
            side.add(buildNavButton("User Management"));
            side.add(Box.createVerticalStrut(4));
            side.add(buildNavButton("Settings"));
        }

        side.add(Box.createVerticalGlue());
        JLabel version = ThemeManager.createMutedLabel("v" + store.getSetting("app.version"));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(version);

        return side;
    }

    private JPanel buildStatCard(String label, String value, Color color, String tag) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 4));
        card.setBackground(ThemeManager.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, color),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        card.setMaximumSize(new Dimension(200, 72));
        card.setPreferredSize(new Dimension(200, 72));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(value);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 28));
        lbl.setForeground(color);

        JLabel lblTag = new JLabel(label);
        lblTag.setFont(ThemeManager.FONT_SMALL);
        lblTag.setForeground(ThemeManager.TEXT_MUTED);

        card.add(lbl);
        card.add(lblTag);

        switch (tag) {
            case "lblTotal":    lblTotalStat    = lbl; break;
            case "lblOpen":     lblOpenStat     = lbl; break;
            case "lblResolved": lblResolvedStat = lbl; break;
            case "lblCritical": lblCriticalStat = lbl; break;
        }
        return card;
    }

    private JButton buildNavButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(ThemeManager.BG_TABLE_SEL);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(ThemeManager.FONT_BODY);
        btn.setForeground(ThemeManager.TEXT_SECONDARY);
        btn.setBackground(null);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 36));
        btn.setPreferredSize(new Dimension(200, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(ThemeManager.GOLD_PRIMARY); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(ThemeManager.TEXT_SECONDARY); }
        });
        btn.addActionListener(e -> handleNavAction(label));
        return btn;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(ThemeManager.BG_SECONDARY);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(ThemeManager.BG_SECONDARY);
        tabbedPane.setForeground(ThemeManager.GOLD_PRIMARY);
        tabbedPane.setFont(ThemeManager.FONT_HEADING);

        tabbedPane.addTab("  Complaint Tracker  ", buildTrackerTab());
        tabbedPane.addTab("  Register Complaint  ", buildRegisterTab());
        tabbedPane.addTab("  Complaint Detail  ", buildDetailTab());
        tabbedPane.addTab("  Analytics  ", buildAnalyticsTab());

        if (currentUser.getRole() == User.Role.ADMIN) {
            tabbedPane.addTab("  User Management  ", buildUserManagementTab());
        }

        main.add(tabbedPane, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildTrackerTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(ThemeManager.BG_SECONDARY);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(buildFilterBar(),     BorderLayout.NORTH);
        panel.add(buildTableSection(),  BorderLayout.CENTER);
        panel.add(buildTableActions(),  BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bar.setBackground(ThemeManager.BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        bar.add(ThemeManager.createGoldLabel("Filter:"));

        txtSearch = ThemeManager.createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Search complaints...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { applyFilters(); }
            @Override public void removeUpdate(DocumentEvent e)  { applyFilters(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        bar.add(txtSearch);

        String[] statuses = {"All Status", "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"};
        cmbFilterStatus = ThemeManager.createStyledCombo(statuses);
        cmbFilterStatus.setPreferredSize(new Dimension(140, 32));
        cmbFilterStatus.addActionListener(e -> applyFilters());
        bar.add(cmbFilterStatus);

        String[] priorities = {"All Priority", "LOW", "MEDIUM", "HIGH", "CRITICAL"};
        cmbFilterPriority = ThemeManager.createStyledCombo(priorities);
        cmbFilterPriority.setPreferredSize(new Dimension(140, 32));
        cmbFilterPriority.addActionListener(e -> applyFilters());
        bar.add(cmbFilterPriority);

        List<String> catItems = new ArrayList<>();
        catItems.add("All Category");
        for (Complaint.Category cat : Complaint.Category.values()) {
            catItems.add(cat.name().replace("_", " "));
        }
        cmbFilterCategory = ThemeManager.createStyledCombo(catItems.toArray(new String[0]));
        cmbFilterCategory.setPreferredSize(new Dimension(160, 32));
        cmbFilterCategory.addActionListener(e -> applyFilters());
        bar.add(cmbFilterCategory);

        JButton btnClear = ThemeManager.createGoldButton("Clear");
        btnClear.setPreferredSize(new Dimension(80, 32));
        btnClear.addActionListener(e -> clearFilters());
        bar.add(btnClear);

        JButton btnRefresh = ThemeManager.createGoldButton("Refresh");
        btnRefresh.setPreferredSize(new Dimension(90, 32));
        btnRefresh.addActionListener(e -> refreshTable());
        bar.add(btnRefresh);

        return bar;
    }

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.BG_SECONDARY);

        complaintTable = new ComplaintTable();
        JScrollPane scroll = ThemeManager.createStyledScrollPane(complaintTable);
        complaintTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTableActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(ThemeManager.BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        JButton btnView = ThemeManager.createGoldButton("View Detail");
        btnView.addActionListener(e -> handleViewDetail());

        JButton btnChangeStatus = ThemeManager.createGoldButton("Change Status");
        btnChangeStatus.addActionListener(e -> handleChangeStatus());

        JButton btnAssign = ThemeManager.createGoldButton("Assign Agent");
        btnAssign.addActionListener(e -> handleAssignAgent());

        JButton btnDelete = ThemeManager.createDangerButton("Delete");
        btnDelete.addActionListener(e -> handleDelete());

        JButton btnExport = ThemeManager.createGoldButton("Export CSV");
        btnExport.addActionListener(e -> handleExportCSV());

        panel.add(btnView);
        panel.add(btnChangeStatus);
        if (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.AGENT) {
            panel.add(btnAssign);
        }
        if (currentUser.getRole() == User.Role.ADMIN) {
            panel.add(btnDelete);
        }
        panel.add(btnExport);

        return panel;
    }

    private JPanel buildRegisterTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(ThemeManager.BG_SECONDARY);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeManager.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(24, 30, 24, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(6, 4, 6, 4);
        gbc.anchor  = GridBagConstraints.WEST;

        JLabel formTitle = new JLabel("REGISTER NEW COMPLAINT");
        formTitle.setFont(ThemeManager.FONT_TITLE);
        formTitle.setForeground(ThemeManager.GOLD_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        form.add(formTitle, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.GOLD_BORDER);
        gbc.gridy = 1; gbc.gridwidth = 4;
        form.add(sep, gbc);

        gbc.gridwidth = 1;

        JTextField txtTitle = ThemeManager.createStyledTextField();
        txtTitle.setPreferredSize(new Dimension(340, 34));
        addFormRow(form, gbc, "Complaint Title *", txtTitle, 2, 0);

        JTextField txtLocation = ThemeManager.createStyledTextField();
        txtLocation.setPreferredSize(new Dimension(340, 34));
        addFormRow(form, gbc, "Location *", txtLocation, 2, 2);

        JTextArea txtDesc = ThemeManager.createStyledTextArea(5, 40);
        JScrollPane descScroll = ThemeManager.createStyledScrollPane(txtDesc);
        descScroll.setPreferredSize(new Dimension(740, 100));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        form.add(ThemeManager.createGoldLabel("Description *"), gbc);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        form.add(descScroll, gbc);

        String[] priorities = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        JComboBox<String> cmbPriority = ThemeManager.createStyledCombo(priorities);
        cmbPriority.setSelectedIndex(1);

        List<String> catNames = new ArrayList<>();
        for (Complaint.Category c : Complaint.Category.values()) catNames.add(c.name().replace("_", " "));
        JComboBox<String> cmbCategory = ThemeManager.createStyledCombo(catNames.toArray(new String[0]));

        JTextField txtPhone = ThemeManager.createStyledTextField();

        addFormRow(form, gbc, "Priority *", cmbPriority, 6, 0);
        addFormRow(form, gbc, "Category *", cmbCategory, 6, 2);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        form.add(ThemeManager.createGoldLabel("Contact Phone"), gbc);
        gbc.gridy = 9; gbc.gridwidth = 2;
        form.add(txtPhone, gbc);

        JLabel lblStatus = ThemeManager.createMutedLabel("");
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 4;
        gbc.insets = new Insets(8, 4, 4, 4);
        form.add(lblStatus, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnPanel.setOpaque(false);
        JButton btnSubmit = ThemeManager.createGoldButton("Submit Complaint");
        btnSubmit.setPreferredSize(new Dimension(180, 38));
        JButton btnReset = ThemeManager.createDangerButton("Reset Form");
        btnReset.setPreferredSize(new Dimension(120, 38));
        btnPanel.add(btnSubmit);
        btnPanel.add(btnReset);

        gbc.gridy = 11; gbc.gridwidth = 4;
        gbc.insets = new Insets(16, 4, 4, 4);
        form.add(btnPanel, gbc);

        btnSubmit.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            String desc  = txtDesc.getText().trim();
            String loc   = txtLocation.getText().trim();
            String phone = txtPhone.getText().trim();
            String prioStr = (String) cmbPriority.getSelectedItem();
            String catRaw  = (String) cmbCategory.getSelectedItem();

            if (title.isEmpty() || desc.isEmpty() || loc.isEmpty()) {
                lblStatus.setForeground(ThemeManager.STATUS_OPEN);
                lblStatus.setText("Title, Description and Location are required fields.");
                return;
            }
            Complaint.Priority prio = Complaint.Priority.valueOf(prioStr);
            Complaint.Category cat  = Complaint.Category.valueOf(catRaw.replace(" ", "_"));

            Complaint c = new Complaint(title, desc, Complaint.Status.OPEN, prio, cat,
                currentUser.getId(), loc, phone);
            store.addComplaint(c);
            refreshStats();
            refreshTable();
            lblStatus.setForeground(ThemeManager.STATUS_RESOLVED);
            lblStatus.setText("Complaint registered! Tracking #: " + c.getTrackingNumber());
            txtTitle.setText("");
            txtDesc.setText("");
            txtLocation.setText("");
            txtPhone.setText("");
            cmbPriority.setSelectedIndex(1);
            cmbCategory.setSelectedIndex(0);
            tabbedPane.setSelectedIndex(0);
        });

        btnReset.addActionListener(e -> {
            txtTitle.setText("");
            txtDesc.setText("");
            txtLocation.setText("");
            txtPhone.setText("");
            cmbPriority.setSelectedIndex(1);
            cmbCategory.setSelectedIndex(0);
            lblStatus.setText("");
        });

        outer.add(form);
        return outer;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, String label, JComponent field, int row, int col) {
        gbc.gridx = col;   gbc.gridy = row;     gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 4, 2, 4);
        form.add(ThemeManager.createGoldLabel(label), gbc);
        gbc.gridx = col;   gbc.gridy = row + 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 4, 6, 4);
        form.add(field, gbc);
    }

    private JPanel buildDetailTab() {
        detailPanel = new JPanel(new BorderLayout(12, 12));
        detailPanel.setBackground(ThemeManager.BG_SECONDARY);
        detailPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel placeholder = ThemeManager.createMutedLabel("Select a complaint from the Tracker tab to view its details here.");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        placeholder.setFont(ThemeManager.FONT_HEADING);
        detailPanel.add(placeholder, BorderLayout.CENTER);

        return detailPanel;
    }

    private JPanel buildAnalyticsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 14, 14));
        panel.setBackground(ThemeManager.BG_SECONDARY);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(buildStatusChart());
        panel.add(buildPriorityChart());
        panel.add(buildCategoryBreakdown());
        panel.add(buildRecentActivity());

        return panel;
    }

    private JPanel buildStatusChart() {
        return new ChartPanel("STATUS DISTRIBUTION", () -> {
            Map<Complaint.Status, Long> data = store.getStatusSummary();
            long total = data.values().stream().mapToLong(Long::longValue).sum();
            List<ChartPanel.BarEntry> entries = new ArrayList<>();
            Color[] colors = {ThemeManager.STATUS_OPEN, ThemeManager.STATUS_PROGRESS,
                              ThemeManager.STATUS_RESOLVED, ThemeManager.STATUS_CLOSED};
            int i = 0;
            for (Map.Entry<Complaint.Status, Long> en : data.entrySet()) {
                entries.add(new ChartPanel.BarEntry(en.getKey().name(), en.getValue(),
                    total == 0 ? 0 : (double) en.getValue() / total, colors[i++ % colors.length]));
            }
            return entries;
        });
    }

    private JPanel buildPriorityChart() {
        return new ChartPanel("PRIORITY DISTRIBUTION", () -> {
            Map<Complaint.Priority, Long> data = store.getPrioritySummary();
            long total = data.values().stream().mapToLong(Long::longValue).sum();
            List<ChartPanel.BarEntry> entries = new ArrayList<>();
            Color[] colors = {ThemeManager.PRIORITY_LOW, ThemeManager.PRIORITY_MEDIUM,
                              ThemeManager.PRIORITY_HIGH, ThemeManager.PRIORITY_CRIT};
            int i = 0;
            for (Map.Entry<Complaint.Priority, Long> en : data.entrySet()) {
                entries.add(new ChartPanel.BarEntry(en.getKey().name(), en.getValue(),
                    total == 0 ? 0 : (double) en.getValue() / total, colors[i++ % colors.length]));
            }
            return entries;
        });
    }

    private JPanel buildCategoryBreakdown() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(ThemeManager.createGoldBorder("CATEGORY BREAKDOWN"));

        String[] cols = {"Category", "Count"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        Map<Complaint.Category, Long> data = store.getCategorySummary();
        for (Map.Entry<Complaint.Category, Long> en : data.entrySet()) {
            model.addRow(new Object[]{en.getKey().name().replace("_", " "), en.getValue()});
        }

        JTable table = new JTable(model);
        table.setBackground(ThemeManager.BG_TABLE_ROW);
        table.setForeground(ThemeManager.TEXT_PRIMARY);
        table.setGridColor(new Color(40, 40, 55));
        table.setRowHeight(24);
        table.getTableHeader().setBackground(ThemeManager.BG_PANEL);
        table.getTableHeader().setForeground(ThemeManager.GOLD_PRIMARY);
        table.getTableHeader().setFont(ThemeManager.FONT_LABEL);
        table.setFont(ThemeManager.FONT_BODY);
        table.setSelectionBackground(ThemeManager.BG_TABLE_SEL);
        table.setSelectionForeground(ThemeManager.GOLD_LIGHT);

        panel.add(ThemeManager.createStyledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRecentActivity() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(ThemeManager.createGoldBorder("RECENT ACTIVITY"));

        JTextArea area = ThemeManager.createStyledTextArea(10, 30);
        area.setEditable(false);
        area.setFont(ThemeManager.FONT_MONO);
        area.setForeground(ThemeManager.TEXT_SECONDARY);
        area.setBackground(ThemeManager.BG_INPUT);

        StringBuilder sb = new StringBuilder();
        List<Complaint> all = store.getAllComplaints();
        all.sort((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()));
        int limit = Math.min(all.size(), 10);
        for (int i = 0; i < limit; i++) {
            Complaint c = all.get(i);
            sb.append(c.getFormattedUpdatedAt()).append("\n");
            sb.append("  ").append(c.getTrackingNumber()).append(" - ").append(c.getTitle()).append("\n");
            sb.append("  Status: ").append(c.getStatus()).append(" | Priority: ").append(c.getPriority()).append("\n\n");
        }
        area.setText(sb.toString());

        panel.add(ThemeManager.createStyledScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildUserManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(ThemeManager.BG_SECONDARY);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel title = ThemeManager.createGoldLabel("USER MANAGEMENT");
        title.setFont(ThemeManager.FONT_TITLE);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Username", "Full Name", "Role", "Department", "Email", "Active"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (User u : store.getAllUsers()) {
            model.addRow(new Object[]{
                u.getUsername(), u.getFullName(), u.getRoleDisplayName(),
                u.getDepartment(), u.getEmail(), u.isActive() ? "Yes" : "No"
            });
        }

        JTable userTable = new JTable(model);
        userTable.setBackground(ThemeManager.BG_TABLE_ROW);
        userTable.setForeground(ThemeManager.TEXT_PRIMARY);
        userTable.setGridColor(new Color(40, 40, 55));
        userTable.setRowHeight(28);
        userTable.getTableHeader().setBackground(ThemeManager.BG_PANEL);
        userTable.getTableHeader().setForeground(ThemeManager.GOLD_PRIMARY);
        userTable.getTableHeader().setFont(ThemeManager.FONT_HEADING);
        userTable.setFont(ThemeManager.FONT_BODY);
        userTable.setSelectionBackground(ThemeManager.BG_TABLE_SEL);
        userTable.setSelectionForeground(ThemeManager.GOLD_LIGHT);

        panel.add(ThemeManager.createStyledScrollPane(userTable), BorderLayout.CENTER);

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actionBar.setBackground(ThemeManager.BG_PANEL);
        actionBar.setBorder(BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1));

        JButton btnAdd = ThemeManager.createGoldButton("Add User");
        btnAdd.addActionListener(e -> showAddUserDialog(model));
        JButton btnToggle = ThemeManager.createGoldButton("Toggle Active");
        btnToggle.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { showInfo("Select a user first."); return; }
            String uname = (String) model.getValueAt(row, 0);
            store.getUserByUsername(uname).ifPresent(u -> {
                u.setActive(!u.isActive());
                store.updateUser(u);
                model.setValueAt(u.isActive() ? "Yes" : "No", row, 5);
            });
        });
        actionBar.add(btnAdd);
        actionBar.add(btnToggle);
        panel.add(actionBar, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddUserDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setBackground(ThemeManager.BG_SECONDARY);
        dialog.getContentPane().setBackground(ThemeManager.BG_SECONDARY);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(ThemeManager.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField fUsername   = ThemeManager.createStyledTextField();
        JTextField fFullName   = ThemeManager.createStyledTextField();
        JTextField fEmail      = ThemeManager.createStyledTextField();
        JTextField fDept       = ThemeManager.createStyledTextField();
        JTextField fPassword   = ThemeManager.createStyledTextField();
        String[] roles = {"CITIZEN", "AGENT", "ADMIN"};
        JComboBox<String> fRole = ThemeManager.createStyledCombo(roles);

        form.add(ThemeManager.createGoldLabel("Username:"));    form.add(fUsername);
        form.add(ThemeManager.createGoldLabel("Full Name:"));   form.add(fFullName);
        form.add(ThemeManager.createGoldLabel("Email:"));       form.add(fEmail);
        form.add(ThemeManager.createGoldLabel("Department:"));  form.add(fDept);
        form.add(ThemeManager.createGoldLabel("Password:"));    form.add(fPassword);
        form.add(ThemeManager.createGoldLabel("Role:"));        form.add(fRole);

        JLabel lblErr = ThemeManager.createMutedLabel("");
        lblErr.setForeground(ThemeManager.STATUS_OPEN);

        JButton btnSave = ThemeManager.createGoldButton("Save User");
        btnSave.addActionListener(ev -> {
            if (fUsername.getText().trim().isEmpty() || fFullName.getText().trim().isEmpty()
                    || fPassword.getText().trim().isEmpty()) {
                lblErr.setText("Username, name and password are required.");
                return;
            }
            if (store.getUserByUsername(fUsername.getText().trim()).isPresent()) {
                lblErr.setText("Username already exists.");
                return;
            }
            User.Role role = User.Role.valueOf((String) fRole.getSelectedItem());
            User u = new User(fUsername.getText().trim(), User.hashPassword(fPassword.getText().trim()),
                fFullName.getText().trim(), fEmail.getText().trim(), fDept.getText().trim(), role);
            store.addUser(u);
            model.addRow(new Object[]{u.getUsername(), u.getFullName(), u.getRoleDisplayName(),
                u.getDepartment(), u.getEmail(), "Yes"});
            dialog.dispose();
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(ThemeManager.BG_CARD);
        btnRow.add(btnSave);
        btnRow.add(lblErr);

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBackground(ThemeManager.BG_SECONDARY);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(form, BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.setVisible(true);
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ThemeManager.BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.GOLD_BORDER),
            BorderFactory.createEmptyBorder(4, 16, 4, 16)
        ));
        bar.setPreferredSize(new Dimension(0, 26));

        lblStatusBar = new JLabel("System ready. Logged in as: " + currentUser.getUsername());
        lblStatusBar.setFont(ThemeManager.FONT_SMALL);
        lblStatusBar.setForeground(ThemeManager.TEXT_MUTED);

        JLabel lblTime = new JLabel();
        lblTime.setFont(ThemeManager.FONT_MONO);
        lblTime.setForeground(ThemeManager.GOLD_DIM);

        Timer clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                String time = new SimpleDateFormat("EEE, dd MMM yyyy  HH:mm:ss").format(new Date());
                SwingUtilities.invokeLater(() -> lblTime.setText(time));
            }
        }, 0, 1000);

        bar.add(lblStatusBar, BorderLayout.WEST);
        bar.add(lblTime, BorderLayout.EAST);
        return bar;
    }

    private void refreshStats() {
        if (lblTotalStat    != null) lblTotalStat.setText(String.valueOf(store.getTotalComplaints()));
        if (lblOpenStat     != null) lblOpenStat.setText(String.valueOf(store.getOpenCount()));
        if (lblResolvedStat != null) lblResolvedStat.setText(String.valueOf(store.getResolvedCount()));
        if (lblCriticalStat != null) lblCriticalStat.setText(String.valueOf(store.getCriticalCount()));
    }

    private void refreshTable() {
        if (complaintTable != null) {
            complaintTable.loadData(store.getAllComplaints());
        }
        refreshStats();
    }

    private void applyFilters() {
        String search   = txtSearch   != null ? txtSearch.getText().trim() : "";
        String status   = cmbFilterStatus   != null && cmbFilterStatus.getSelectedIndex() > 0
                          ? (String) cmbFilterStatus.getSelectedItem() : null;
        String priority = cmbFilterPriority != null && cmbFilterPriority.getSelectedIndex() > 0
                          ? (String) cmbFilterPriority.getSelectedItem() : null;
        String category = cmbFilterCategory != null && cmbFilterCategory.getSelectedIndex() > 0
                          ? ((String) cmbFilterCategory.getSelectedItem()).replace(" ", "_") : null;

        List<Complaint> all = store.getAllComplaints();
        List<Complaint> filtered = new ArrayList<>();
        for (Complaint c : all) {
            boolean match = true;
            if (!search.isEmpty()) {
                boolean found = c.getTitle().toLowerCase().contains(search.toLowerCase())
                    || c.getTrackingNumber().toLowerCase().contains(search.toLowerCase())
                    || c.getLocation().toLowerCase().contains(search.toLowerCase())
                    || c.getDescription().toLowerCase().contains(search.toLowerCase());
                if (!found) match = false;
            }
            if (match && status != null && !c.getStatus().name().equals(status)) match = false;
            if (match && priority != null && !c.getPriority().name().equals(priority)) match = false;
            if (match && category != null && !c.getCategory().name().equals(category)) match = false;
            if (match) filtered.add(c);
        }
        complaintTable.loadData(filtered);
        lblStatusBar.setText("Showing " + filtered.size() + " complaint(s).");
    }

    private void clearFilters() {
        if (txtSearch != null)          txtSearch.setText("");
        if (cmbFilterStatus != null)    cmbFilterStatus.setSelectedIndex(0);
        if (cmbFilterPriority != null)  cmbFilterPriority.setSelectedIndex(0);
        if (cmbFilterCategory != null)  cmbFilterCategory.setSelectedIndex(0);
        refreshTable();
    }

    private void handleTableSelection() {
        Complaint c = complaintTable.getSelectedComplaint();
        if (c != null) {
            selectedComplaint = c;
            lblStatusBar.setText("Selected: [" + c.getTrackingNumber() + "] " + c.getTitle());
        }
    }

    private void handleViewDetail() {
        Complaint c = complaintTable.getSelectedComplaint();
        if (c == null) { showInfo("Please select a complaint from the table."); return; }
        selectedComplaint = c;
        populateDetailPanel(c);
        tabbedPane.setSelectedIndex(2);
    }

    private void populateDetailPanel(Complaint c) {
        detailPanel.removeAll();
        detailPanel.setLayout(new BorderLayout(12, 12));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ThemeManager.BG_PANEL);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JLabel lblTitle = new JLabel(c.getTitle());
        lblTitle.setFont(ThemeManager.FONT_TITLE);
        lblTitle.setForeground(ThemeManager.GOLD_PRIMARY);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badges.setOpaque(false);
        badges.add(createBadge(c.getStatus().name(), ThemeManager.getStatusColor(c.getStatus().name())));
        badges.add(createBadge(c.getPriority().name(), ThemeManager.getPriorityColor(c.getPriority().name())));
        badges.add(createBadge(c.getTrackingNumber(), ThemeManager.GOLD_DIM));

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(badges, BorderLayout.EAST);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 0));
        center.setBackground(ThemeManager.BG_SECONDARY);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 8, 6));
        infoPanel.setBackground(ThemeManager.BG_CARD);
        infoPanel.setBorder(ThemeManager.createGoldBorder("COMPLAINT INFORMATION"));

        addDetailRow(infoPanel, "Tracking #:", c.getTrackingNumber());
        addDetailRow(infoPanel, "Category:", c.getCategoryDisplayName());
        addDetailRow(infoPanel, "Location:", c.getLocation());
        addDetailRow(infoPanel, "Contact:", c.getContactPhone().isEmpty() ? "N/A" : c.getContactPhone());
        addDetailRow(infoPanel, "Submitted:", c.getFormattedCreatedAt());
        addDetailRow(infoPanel, "Last Updated:", c.getFormattedUpdatedAt());
        addDetailRow(infoPanel, "Resolved On:", c.getFormattedResolvedAt());
        addDetailRow(infoPanel, "Age (hours):", String.valueOf(c.getAgeInHours()));

        String assignedName = "Unassigned";
        if (c.getAssignedToUserId() != null) {
            Optional<User> agent = store.getUserById(c.getAssignedToUserId());
            assignedName = agent.map(User::getFullName).orElse("Unknown");
        }
        addDetailRow(infoPanel, "Assigned To:", assignedName);

        String submitterName = "Unknown";
        Optional<User> sub = store.getUserById(c.getSubmittedByUserId());
        if (sub.isPresent()) submitterName = sub.get().getFullName();
        addDetailRow(infoPanel, "Submitted By:", submitterName);

        JPanel descPanel = new JPanel(new BorderLayout(0, 8));
        descPanel.setBackground(ThemeManager.BG_CARD);
        descPanel.setBorder(ThemeManager.createGoldBorder("DESCRIPTION & RESOLUTION"));

        JTextArea descArea = ThemeManager.createStyledTextArea(4, 30);
        descArea.setText(c.getDescription());
        descArea.setEditable(false);

        JTextArea resArea = ThemeManager.createStyledTextArea(3, 30);
        resArea.setText(c.getResolutionNote().isEmpty() ? "No resolution note yet." : c.getResolutionNote());
        resArea.setEditable(false);

        JLabel lblDescTitle = ThemeManager.createGoldLabel("Description:");
        JLabel lblResTitle  = ThemeManager.createGoldLabel("Resolution Note:");
        JPanel descInner = new JPanel(new GridLayout(2, 1, 0, 8));
        descInner.setOpaque(false);
        descInner.add(lblDescTitle);
        descInner.add(ThemeManager.createStyledScrollPane(descArea));
        JPanel resInner = new JPanel(new GridLayout(2, 1, 0, 8));
        resInner.setOpaque(false);
        resInner.add(lblResTitle);
        resInner.add(ThemeManager.createStyledScrollPane(resArea));
        JPanel combined = new JPanel(new GridLayout(2, 1, 0, 10));
        combined.setOpaque(false);
        combined.add(descInner);
        combined.add(resInner);
        descPanel.add(combined, BorderLayout.CENTER);

        center.add(infoPanel);
        center.add(descPanel);

        JPanel auditPanel = new JPanel(new BorderLayout());
        auditPanel.setBackground(ThemeManager.BG_CARD);
        auditPanel.setBorder(ThemeManager.createGoldBorder("AUDIT TRAIL"));

        JTextArea auditArea = ThemeManager.createStyledTextArea(6, 60);
        auditArea.setEditable(false);
        auditArea.setFont(ThemeManager.FONT_MONO);
        StringBuilder audit = new StringBuilder();
        List<Complaint.AuditEntry> trail = c.getAuditTrail();
        for (int i = trail.size() - 1; i >= 0; i--) {
            audit.append(trail.get(i).toString()).append("\n");
        }
        auditArea.setText(audit.toString());
        auditArea.setCaretPosition(0);
        auditPanel.add(ThemeManager.createStyledScrollPane(auditArea), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        btnRow.setBackground(ThemeManager.BG_PANEL);
        btnRow.setBorder(BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1));

        JButton btnStatusDetail = ThemeManager.createGoldButton("Change Status");
        btnStatusDetail.addActionListener(e -> handleChangeStatusForComplaint(c));
        JButton btnAssignDetail  = ThemeManager.createGoldButton("Assign Agent");
        btnAssignDetail.addActionListener(e -> handleAssignForComplaint(c));
        JButton btnNoteDetail    = ThemeManager.createGoldButton("Add Note");
        btnNoteDetail.addActionListener(e -> handleAddNote(c));
        JButton btnBack          = ThemeManager.createDangerButton("Back to Tracker");
        btnBack.addActionListener(e -> tabbedPane.setSelectedIndex(0));

        btnRow.add(btnStatusDetail);
        btnRow.add(btnAssignDetail);
        btnRow.add(btnNoteDetail);
        btnRow.add(btnBack);

        detailPanel.add(topBar,     BorderLayout.NORTH);
        detailPanel.add(center,     BorderLayout.CENTER);
        detailPanel.add(auditPanel, BorderLayout.SOUTH);
        detailPanel.add(btnRow,     BorderLayout.PAGE_END);

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JLabel createBadge(String text, Color color) {
        JLabel badge = new JLabel(" " + text + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(color);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setForeground(color);
        badge.setFont(ThemeManager.FONT_LABEL);
        badge.setOpaque(false);
        return badge;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel lbl = ThemeManager.createMutedLabel(label);
        JLabel val = ThemeManager.createBodyLabel(value);
        panel.add(lbl);
        panel.add(val);
    }

    private void handleChangeStatus() {
        Complaint c = complaintTable.getSelectedComplaint();
        if (c == null) { showInfo("Select a complaint first."); return; }
        handleChangeStatusForComplaint(c);
    }

    private void handleChangeStatusForComplaint(Complaint c) {
        String[] statuses = {"OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"};
        JComboBox<String> combo = ThemeManager.createStyledCombo(statuses);
        combo.setSelectedItem(c.getStatus().name());

        JTextArea noteArea = ThemeManager.createStyledTextArea(3, 30);
        noteArea.setBorder(BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1));

        JPanel dlgPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        dlgPanel.setBackground(ThemeManager.BG_CARD);
        dlgPanel.add(ThemeManager.createGoldLabel("New Status:"));
        dlgPanel.add(combo);
        dlgPanel.add(ThemeManager.createGoldLabel("Change Note (optional):"));
        dlgPanel.add(noteArea);

        int result = JOptionPane.showConfirmDialog(this, dlgPanel, "Change Complaint Status",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Complaint.Status newStatus = Complaint.Status.valueOf((String) combo.getSelectedItem());
            c.updateStatus(newStatus, currentUser.getUsername(), noteArea.getText().trim());
            store.updateComplaint(c);
            refreshTable();
            refreshStats();
            if (selectedComplaint != null && selectedComplaint.getId().equals(c.getId())) {
                populateDetailPanel(c);
            }
            setStatus("Status updated to " + newStatus + " for " + c.getTrackingNumber());
        }
    }

    private void handleAssignAgent() {
        Complaint c = complaintTable.getSelectedComplaint();
        if (c == null) { showInfo("Select a complaint first."); return; }
        handleAssignForComplaint(c);
    }

    private void handleAssignForComplaint(Complaint c) {
        List<User> agents = store.getAgents();
        if (agents.isEmpty()) { showInfo("No agents available."); return; }
        String[] agentNames = agents.stream().map(u -> u.getFullName() + " (" + u.getDepartment() + ")")
            .toArray(String[]::new);
        JComboBox<String> combo = ThemeManager.createStyledCombo(agentNames);
        int result = JOptionPane.showConfirmDialog(this, combo, "Assign Agent",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            User agent = agents.get(combo.getSelectedIndex());
            c.setAssignedToUserId(agent.getId());
            c.addAuditEntry(currentUser.getUsername(), "Assigned to agent: " + agent.getFullName());
            if (c.getStatus() == Complaint.Status.OPEN) {
                c.updateStatus(Complaint.Status.IN_PROGRESS, currentUser.getUsername(), "Auto-updated on assignment");
            }
            store.updateComplaint(c);
            refreshTable();
            refreshStats();
            if (selectedComplaint != null && selectedComplaint.getId().equals(c.getId())) {
                populateDetailPanel(c);
            }
            setStatus("Assigned " + c.getTrackingNumber() + " to " + agent.getFullName());
        }
    }

    private void handleAddNote(Complaint c) {
        JTextArea noteArea = ThemeManager.createStyledTextArea(4, 30);
        noteArea.setBorder(BorderFactory.createLineBorder(ThemeManager.GOLD_BORDER, 1));
        int result = JOptionPane.showConfirmDialog(this, noteArea, "Add Audit Note",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && !noteArea.getText().trim().isEmpty()) {
            c.addAuditEntry(currentUser.getUsername(), noteArea.getText().trim());
            store.updateComplaint(c);
            populateDetailPanel(c);
            setStatus("Note added to " + c.getTrackingNumber());
        }
    }

    private void handleDelete() {
        Complaint c = complaintTable.getSelectedComplaint();
        if (c == null) { showInfo("Select a complaint first."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete complaint: " + c.getTrackingNumber() + "?\nThis action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            store.deleteComplaint(c.getId());
            refreshTable();
            refreshStats();
            setStatus("Complaint " + c.getTrackingNumber() + " deleted.");
        }
    }

    private void handleExportCSV() {
        List<Complaint> data = complaintTable.getCurrentData();
        StringBuilder sb = new StringBuilder();
        sb.append("Tracking#,Title,Status,Priority,Category,Location,Submitted,Updated\n");
        for (Complaint c : data) {
            sb.append(c.getTrackingNumber()).append(",")
              .append(escapeCsv(c.getTitle())).append(",")
              .append(c.getStatus()).append(",")
              .append(c.getPriority()).append(",")
              .append(c.getCategoryDisplayName()).append(",")
              .append(escapeCsv(c.getLocation())).append(",")
              .append(c.getFormattedCreatedAt()).append(",")
              .append(c.getFormattedUpdatedAt()).append("\n");
        }
        JTextArea area = ThemeManager.createStyledTextArea(20, 60);
        area.setText(sb.toString());
        JOptionPane.showMessageDialog(this, ThemeManager.createStyledScrollPane(area),
            "CSV Export (" + data.size() + " records)", JOptionPane.PLAIN_MESSAGE);
        setStatus("Exported " + data.size() + " records to CSV view.");
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private void handleNavAction(String label) {
        switch (label) {
            case "Dashboard":
                tabbedPane.setSelectedIndex(0);
                clearFilters();
                break;
            case "My Complaints":
                tabbedPane.setSelectedIndex(0);
                clearFilters();
                complaintTable.loadData(store.getComplaintsByUser(currentUser.getId()));
                setStatus("Showing my complaints.");
                break;
            case "All Complaints":
                tabbedPane.setSelectedIndex(0);
                clearFilters();
                break;
            case "Reports":
                tabbedPane.setSelectedIndex(3);
                break;
            case "User Management":
                if (tabbedPane.getTabCount() > 4) tabbedPane.setSelectedIndex(4);
                break;
            case "Settings":
                showSettingsDialog();
                break;
        }
    }

    private void showSettingsDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 8));
        panel.setBackground(ThemeManager.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextField fOrg  = ThemeManager.createStyledTextField();
        fOrg.setText(store.getSetting("app.org"));
        panel.add(ThemeManager.createGoldLabel("Organization Name:"));
        panel.add(fOrg);

        int result = JOptionPane.showConfirmDialog(this, panel, "Application Settings",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            store.setSetting("app.org", fOrg.getText().trim());
            setStatus("Settings saved.");
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (refreshTimer != null) refreshTimer.cancel();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginView());
        }
    }

    private void startRefreshTimer() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                SwingUtilities.invokeLater(() -> refreshStats());
            }
        }, 30000, 30000);
    }

    private void setStatus(String msg) {
        if (lblStatusBar != null) lblStatusBar.setText(msg);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    static class ComplaintTable extends JTable {

        private final DefaultTableModel model;
        private List<Complaint> currentData = new ArrayList<>();

        private static final String[] COLUMNS = {
            "#", "Tracking No.", "Title", "Category", "Status", "Priority", "Location", "Submitted", "Age(h)"
        };

        ComplaintTable() {
            model = new DefaultTableModel(COLUMNS, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            setModel(model);
            applyStyle();
        }

        private void applyStyle() {
            setBackground(ThemeManager.BG_TABLE_ROW);
            setForeground(ThemeManager.TEXT_PRIMARY);
            setGridColor(new Color(35, 35, 50));
            setRowHeight(30);
            setFont(ThemeManager.FONT_BODY);
            setSelectionBackground(ThemeManager.BG_TABLE_SEL);
            setSelectionForeground(ThemeManager.GOLD_LIGHT);
            setShowGrid(true);
            setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setFillsViewportHeight(true);

            getTableHeader().setBackground(ThemeManager.BG_PANEL);
            getTableHeader().setForeground(ThemeManager.GOLD_PRIMARY);
            getTableHeader().setFont(ThemeManager.FONT_HEADING);
            getTableHeader().setReorderingAllowed(false);
            getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeManager.GOLD_BORDER));

            int[] widths = {40, 130, 200, 120, 100, 90, 150, 130, 60};
            for (int i = 0; i < widths.length; i++) {
                getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }

            setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setBackground(isSelected ? ThemeManager.BG_TABLE_SEL : (row % 2 == 0 ? ThemeManager.BG_TABLE_ROW : ThemeManager.BG_TABLE_ALT));
                    setForeground(isSelected ? ThemeManager.GOLD_LIGHT : ThemeManager.TEXT_PRIMARY);
                    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                    setFont(ThemeManager.FONT_BODY);

                    if (value != null && column == 4) {
                        Color c = ThemeManager.getStatusColor(value.toString());
                        setForeground(isSelected ? ThemeManager.GOLD_LIGHT : c);
                    }
                    if (value != null && column == 5) {
                        Color c = ThemeManager.getPriorityColor(value.toString());
                        setForeground(isSelected ? ThemeManager.GOLD_LIGHT : c);
                    }
                    return this;
                }
            });
        }

        public void loadData(List<Complaint> complaints) {
            this.currentData = new ArrayList<>(complaints);
            model.setRowCount(0);
            int idx = 1;
            for (Complaint c : complaints) {
                model.addRow(new Object[]{
                    idx++,
                    c.getTrackingNumber(),
                    c.getTitle(),
                    c.getCategoryDisplayName(),
                    c.getStatus().name(),
                    c.getPriority().name(),
                    c.getLocation(),
                    c.getFormattedCreatedAt(),
                    c.getAgeInHours()
                });
            }
        }

        public Complaint getSelectedComplaint() {
            int row = getSelectedRow();
            if (row < 0 || row >= currentData.size()) return null;
            return currentData.get(row);
        }

        public List<Complaint> getCurrentData() {
            return new ArrayList<>(currentData);
        }
    }

    static class ChartPanel extends JPanel {

        interface DataProvider {
            List<BarEntry> getData();
        }

        static class BarEntry {
            final String label;
            final long   value;
            final double ratio;
            final Color  color;
            BarEntry(String label, long value, double ratio, Color color) {
                this.label = label; this.value = value; this.ratio = ratio; this.color = color;
            }
        }

        private final String       title;
        private final DataProvider provider;

        ChartPanel(String title, DataProvider provider) {
            this.title    = title;
            this.provider = provider;
            setBackground(ThemeManager.BG_CARD);
            setBorder(ThemeManager.createGoldBorder(title));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<BarEntry> data = provider.getData();
            if (data.isEmpty()) { g2.dispose(); return; }

            int padL = 30, padR = 20, padT = 30, padB = 50;
            int w    = getWidth()  - padL - padR;
            int h    = getHeight() - padT - padB;
            int n    = data.size();
            int barW = Math.max(1, w / n - 12);

            g2.setColor(new Color(50, 50, 70));
            for (int i = 1; i <= 4; i++) {
                int y = padT + h - (h * i / 4);
                g2.drawLine(padL, y, padL + w, y);
            }

            g2.setFont(ThemeManager.FONT_SMALL);

            for (int i = 0; i < n; i++) {
                BarEntry entry = data.get(i);
                int x    = padL + i * (w / n) + (w / n - barW) / 2;
                int barH = (int)(h * entry.ratio);
                int y    = padT + h - barH;

                g2.setColor(new Color(entry.color.getRed(), entry.color.getGreen(), entry.color.getBlue(), 60));
                g2.fillRect(x, padT, barW, h);

                GradientPaint gp = new GradientPaint(x, y, entry.color,
                    x + barW, padT + h, new Color(entry.color.getRed(), entry.color.getGreen(), entry.color.getBlue(), 180));
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barW, barH, 4, 4);

                g2.setColor(entry.color);
                g2.setFont(ThemeManager.FONT_LABEL);
                FontMetrics fm = g2.getFontMetrics();
                String val = String.valueOf(entry.value);
                g2.drawString(val, x + (barW - fm.stringWidth(val)) / 2, y - 4);

                g2.setColor(ThemeManager.TEXT_MUTED);
                g2.setFont(ThemeManager.FONT_SMALL);
                FontMetrics fmS = g2.getFontMetrics();
                String lbl = entry.label.length() > 8 ? entry.label.substring(0, 7) + "." : entry.label;
                g2.drawString(lbl, x + (barW - fmS.stringWidth(lbl)) / 2, padT + h + 16);
            }

            g2.setColor(ThemeManager.GOLD_BORDER);
            g2.drawLine(padL, padT, padL, padT + h);
            g2.drawLine(padL, padT + h, padL + w, padT + h);

            g2.dispose();
        }
    }
}
