package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Main_class extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color NAVY        = new Color( 26,  54, 105);
    private static final Color NAVY_DARK   = new Color( 18,  38,  76);
    private static final Color ACCENT      = new Color( 66, 153, 225);
    private static final Color WHITE       = Color.WHITE;
    private static final Color BG          = new Color(244, 246, 250);
    private static final Color BORDER_COL  = new Color(214, 220, 232);
    private static final Color TEXT_DARK   = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY   = new Color(100, 115, 140);
    private static final Color TEXT_DIM    = new Color(160, 170, 185);
    private static final Color C_GREEN     = new Color( 34, 139,  87);
    private static final Color C_GREEN_H   = new Color( 22, 108,  67);
    private static final Color C_AMBER     = new Color(217, 119,   6);
    private static final Color C_PURPLE    = new Color(124,  58, 237);
    private static final Color DANGER      = new Color(185,  28,  28);
    private static final Color ROW_ALT     = new Color(250, 251, 254);
    private static final Color ROW_SEL     = new Color(219, 234, 254);
    private static final Color HDR_BG      = new Color(248, 250, 253);

    // Avatar palette — one per initial hash bucket
    private static final Color[] AV = {
        new Color( 99, 102, 241), // indigo
        new Color( 16, 185, 129), // emerald
        new Color(245, 158,  11), // amber
        new Color(239,  68,  68), // red
        new Color( 59, 130, 246), // blue
        new Color(168,  85, 247), // purple
        new Color( 20, 184, 166), // teal
        new Color(249, 115,  22), // orange
    };

    // ── State ──────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable empTable;
    private JTextField searchField;
    private JComboBox<String> filterDesig;
    private JComboBox<String> filterEdu;
    private JLabel countLabel;
    private final List<String> empIds   = new ArrayList<>();
    private final List<String> empNames = new ArrayList<>();

    Main_class() {
        setTitle("Employee Management System — Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int[] stats = loadStats();

        // ── 1. Painted background panel ────────────────────────────────
        // Layout zones:
        //   [0  – 68 ] Navy header
        //   [76 – 172] Stat cards (h=96)
        //   [180– 276] White toolbar strip (h=96) — 2 rows
        //              Row 1 (y≈192): count label · search · print · add
        //              Row 2 (y≈240): filter label · designation · education · clear
        //   [277– 624] Table scrollpane
        //   [648– 684] Footer strip
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth(), H = getHeight();

                // Page background
                g2.setColor(BG);
                g2.fillRect(0, 0, W, H);

                // Navy header bar
                g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                g2.fillRect(0, 0, W, 68);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.drawString("Employee Management System", 24, 30);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Human Resource & Administration Dashboard", 24, 50);

                // White toolbar strip — 2 rows (search+actions / filters)
                g2.setColor(WHITE);
                g2.fillRect(0, 180, W, 96);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 180, W, 180);          // top border
                g2.setColor(new Color(232, 237, 247));
                g2.drawLine(24, 228, W - 24, 228);    // row divider (inset)
                g2.setColor(BORDER_COL);
                g2.drawLine(0, 276, W, 276);           // bottom border

                // Footer strip
                g2.setColor(new Color(237, 240, 247));
                g2.fillRect(0, H - 38, W, 38);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, H - 38, W, H - 38);
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                FontMetrics fm = g2.getFontMetrics();
                String copy = "© 2025 Employee Management System  ·  v1.0.0";
                g2.drawString(copy, W / 2 - fm.stringWidth(copy) / 2, H - 14);
            }
        };
        bg.setBounds(0, 0, 1100, 700);
        bg.setLayout(null);
        add(bg);

        // ── 2. Header — Analytics + Report + Payroll + Leave + Logout ────
        // Layout (left → right): Analytics(510) Report(606) Payroll(694) Leave(790) Logout(900)

        JButton analyticsBtn = new JButton("📊 Analytics");
        analyticsBtn.setBounds(510, 18, 88, 30);
        analyticsBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        analyticsBtn.setForeground(WHITE);
        analyticsBtn.setFocusPainted(false);
        analyticsBtn.setContentAreaFilled(false);
        analyticsBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        analyticsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        analyticsBtn.addActionListener(e -> { dispose(); new PerformanceDashboard(); });
        analyticsBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { analyticsBtn.setBorder(new LineBorder(WHITE, 1)); }
            public void mouseExited (MouseEvent e) { analyticsBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        bg.add(analyticsBtn);

        JButton reportBtn = new JButton("📋 Report");
        reportBtn.setBounds(608, 18, 78, 30);
        reportBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        reportBtn.setForeground(WHITE);
        reportBtn.setFocusPainted(false);
        reportBtn.setContentAreaFilled(false);
        reportBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        reportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reportBtn.addActionListener(e -> MonthlyReportDialog.show(this));
        reportBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { reportBtn.setBorder(new LineBorder(WHITE, 1)); }
            public void mouseExited (MouseEvent e) { reportBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        bg.add(reportBtn);

        JButton payrollBtn = new JButton("Payroll");
        payrollBtn.setBounds(696, 18, 84, 30);
        payrollBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        payrollBtn.setForeground(WHITE);
        payrollBtn.setFocusPainted(false);
        payrollBtn.setContentAreaFilled(false);
        payrollBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        payrollBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payrollBtn.addActionListener(e -> { dispose(); new PayrollHistory(); });
        payrollBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { payrollBtn.setBorder(new LineBorder(WHITE, 1)); }
            public void mouseExited (MouseEvent e) { payrollBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        bg.add(payrollBtn);

        JButton leaveBtn = new JButton("Leave Mgmt");
        leaveBtn.setBounds(790, 18, 100, 30);
        leaveBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        leaveBtn.setForeground(WHITE);
        leaveBtn.setFocusPainted(false);
        leaveBtn.setContentAreaFilled(false);
        leaveBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        leaveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leaveBtn.addActionListener(e -> { dispose(); new LeaveManagement(); });
        leaveBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { leaveBtn.setBorder(new LineBorder(WHITE, 1)); }
            public void mouseExited (MouseEvent e) { leaveBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        bg.add(leaveBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(900, 18, 84, 30);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        logoutBtn.setForeground(WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> { dispose(); new Login(); });
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { logoutBtn.setBorder(new LineBorder(WHITE, 1)); }
            public void mouseExited (MouseEvent e) { logoutBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        bg.add(logoutBtn);

        // ── 3. Stat cards — y=76, h=96 ────────────────────────────────
        // 4 cards: (1100 - 2×22 - 3×16) / 4 = (1100 - 44 - 48) / 4 = 252
        // x: 22, 22+252+16=290, 290+252+16=558, 558+252+16=826
        addStatCard(bg, "Monthly Payroll",  String.format("%,d", stats[0]), "BDT · Net Pay Total", NAVY,     22,  76, 252, 96);
        addStatCard(bg, "Average Salary",  String.format("%,d", stats[1]), "BDT · Per Month",     C_GREEN,  290, 76, 252, 96);
        addStatCard(bg, "Designations",    String.valueOf(stats[2]),        "Unique Job Roles",    C_AMBER,  558, 76, 252, 96);
        addStatCard(bg, "Pending Leaves",  String.valueOf(stats[3]),        "Awaiting Review",     DANGER,   826, 76, 252, 96);

        // ── 4. Toolbar — y=180, h=48 ──────────────────────────────────

        // "All Employees (n)" label (left side of toolbar)
        countLabel = new JLabel("All Employees  (" + stats[4] + ")");
        countLabel.setBounds(24, 192, 230, 26);
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        countLabel.setForeground(TEXT_DARK);
        bg.add(countLabel);

        // Search field (center-left)
        searchField = new JTextField("Search by name, designation or ID...");
        searchField.setBounds(264, 193, 280, 26);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setForeground(TEXT_GRAY);
        searchField.setBackground(new Color(248, 250, 253));
        searchField.setCaretColor(NAVY);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getForeground().equals(TEXT_GRAY)) {
                    searchField.setText(""); searchField.setForeground(TEXT_DARK);
                }
                searchField.setBorder(new CompoundBorder(
                        new LineBorder(ACCENT, 2), BorderFactory.createEmptyBorder(0, 9, 0, 9)));
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(TEXT_GRAY);
                    searchField.setText("Search by name, designation or ID...");
                }
                searchField.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_COL, 1), BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { applyFilters(); }
        });
        bg.add(searchField);

        // Print button (right side)
        JButton printBtn = makeSecondaryBtn("Print");
        printBtn.setBounds(836, 193, 82, 26);
        printBtn.addActionListener(e -> {
            try { empTable.print(); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        bg.add(printBtn);

        // Add Employee button (rightmost)
        JButton addBtn = makePrimaryBtn("+ Add Employee", C_GREEN, C_GREEN_H);
        addBtn.setBounds(928, 193, 148, 26);
        addBtn.addActionListener(e -> { dispose(); new AddEmployee(); });
        bg.add(addBtn);

        // ── Filter row (row 2 of toolbar, y≈236) ──────────────────────
        JLabel filterLbl = new JLabel("Filter by:");
        filterLbl.setBounds(24, 240, 70, 26);
        filterLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        filterLbl.setForeground(TEXT_GRAY);
        bg.add(filterLbl);

        // Designation filter
        filterDesig = new JComboBox<>(buildFilterOptions("designation"));
        filterDesig.setBounds(100, 238, 210, 30);
        styleFilterCombo(filterDesig, "Designation");
        filterDesig.addActionListener(e -> applyFilters());
        bg.add(filterDesig);

        // Education filter
        filterEdu = new JComboBox<>(buildFilterOptions("education"));
        filterEdu.setBounds(322, 238, 190, 30);
        styleFilterCombo(filterEdu, "Education");
        filterEdu.addActionListener(e -> applyFilters());
        bg.add(filterEdu);

        // Clear filters button
        JButton clearBtn = makeSecondaryBtn("✕  Clear");
        clearBtn.setBounds(524, 238, 96, 30);
        clearBtn.addActionListener(e -> {
            filterDesig.setSelectedIndex(0);
            filterEdu.setSelectedIndex(0);
            if (!searchField.getForeground().equals(TEXT_GRAY)) searchField.setText("");
            applyFilters();
        });
        bg.add(clearBtn);

        // Export CSV button (14-C)
        JButton exportCsvBtn = makeSecondaryBtn("⬇ Export CSV");
        exportCsvBtn.setBounds(632, 238, 118, 30);
        exportCsvBtn.addActionListener(e -> CsvExporter.export(this, tableModel, "employees"));
        bg.add(exportCsvBtn);

        // ── 5. Employee table — y=277 ──────────────────────────────────
        String[] cols = {"Name", "Designation", "Education", "Salary (BDT)", "Phone", "Emp ID", ""};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        empTable = new JTable(tableModel);
        empTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        empTable.setRowHeight(44);
        empTable.setShowGrid(false);
        empTable.setIntercellSpacing(new Dimension(0, 0));
        empTable.setSelectionBackground(ROW_SEL);
        empTable.setSelectionForeground(TEXT_DARK);
        empTable.setFillsViewportHeight(true);
        empTable.setBackground(WHITE);
        empTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        empTable.getTableHeader().setReorderingAllowed(false);
        empTable.getTableHeader().setResizingAllowed(false);

        // Column widths summing to ≈ 1100 (scroll is full-width)
        // Name=270, Desig=190, Edu=110, Salary=130, Phone=148, EmpId=130, Actions=52 → 1030
        // Use AUTO_RESIZE_OFF and let scroll handle the rest
        // Columns sum to 1083 = (scroll 1100) − (scrollbar 17).
        // No scrollbar → 17 px neat gap. Scrollbar visible → fills perfectly.
        // Name=290, Desig=205, Edu=112, Salary=132, Phone=150, EmpID=126, Actions=68 → 1083
        int[] cw = {290, 205, 112, 132, 150, 126, 68};
        for (int i = 0; i < cw.length; i++) {
            TableColumn tc = empTable.getColumnModel().getColumn(i);
            tc.setPreferredWidth(cw[i]);
            if (i == 6) { tc.setMaxWidth(68); tc.setMinWidth(68); }
        }

        applyTableStyle();

        // ── 3-dot click handler: direct MouseListener on the table ─────
        // This is far more reliable than AbstractCellEditor because it
        // never detaches the component before the popup is shown.
        empTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = empTable.columnAtPoint(e.getPoint());
                int row = empTable.rowAtPoint(e.getPoint());
                if (col == 6 && row >= 0 && row < empIds.size()) {
                    empTable.setRowSelectionInterval(row, row);
                    String empid = empIds.get(row);
                    String name  = empNames.get(row);
                    JPopupMenu menu = buildContextMenu(empid, name);
                    // Position: right-align menu with the dot cell, open downward
                    Rectangle cell = empTable.getCellRect(row, col, false);
                    int menuX = cell.x + cell.width - menu.getPreferredSize().width;
                    int menuY = cell.y + cell.height;
                    menu.show(empTable, menuX, menuY);
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = empTable.columnAtPoint(e.getPoint());
                empTable.setCursor(col == 6
                        ? new Cursor(Cursor.HAND_CURSOR)
                        : new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        // Also update cursor on move (mouseMoved is in MouseMotionListener)
        empTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = empTable.columnAtPoint(e.getPoint());
                empTable.setCursor(col == 6
                        ? new Cursor(Cursor.HAND_CURSOR)
                        : new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // Scroll pane — starts after filter row (y=277)
        // Height = 700 - 277 - 38(footer) - 10(gap) = 375
        JScrollPane scroll = new JScrollPane(empTable);
        scroll.setBounds(0, 277, 1100, 375);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        scroll.getViewport().setBackground(WHITE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bg.add(scroll);

        // ── 6. Payroll reminder banner (14-B) — floats above footer ────
        // Shown only when current-month payroll hasn't been processed
        JPanel reminderBanner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 247, 230));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(253, 186, 116));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.setColor(new Color(195, 150, 20));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, 4, getHeight(), 4, 4));
            }
        };
        reminderBanner.setBounds(0, 620, 1100, 42);
        reminderBanner.setLayout(null);
        reminderBanner.setVisible(false);

        JLabel reminderLbl = new JLabel("⚠  Payroll for this month has not been processed yet.");
        reminderLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        reminderLbl.setForeground(new Color(146, 64, 14));
        reminderLbl.setBounds(14, 0, 500, 42);
        reminderBanner.add(reminderLbl);

        JButton processNowBtn = makePrimaryBtn("Process Now", new Color(217, 119, 6), new Color(180, 98, 0));
        processNowBtn.setBounds(530, 7, 120, 28);
        processNowBtn.addActionListener(e -> { dispose(); new PayrollProcess(); });
        reminderBanner.add(processNowBtn);

        JButton dismissBannerBtn = new JButton("✕");
        dismissBannerBtn.setBounds(1060, 7, 28, 28);
        dismissBannerBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dismissBannerBtn.setForeground(new Color(146, 64, 14));
        dismissBannerBtn.setBackground(new Color(255, 247, 230));
        dismissBannerBtn.setFocusPainted(false);
        dismissBannerBtn.setBorder(BorderFactory.createEmptyBorder());
        dismissBannerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dismissBannerBtn.addActionListener(e -> reminderBanner.setVisible(false));
        reminderBanner.add(dismissBannerBtn);

        bg.add(reminderBanner);

        // ── 7. Load data + check payroll reminder ──────────────────────
        loadEmployees("");
        checkPayrollReminder(reminderBanner, reminderLbl);

        // ── 8. Auto-refresh timer (14-E) — refreshes table every 60s ──
        javax.swing.Timer autoRefresh = new javax.swing.Timer(60_000, e -> applyFilters());
        autoRefresh.setInitialDelay(60_000);
        autoRefresh.start();

        // Auto-dismiss reminder banner after 10 seconds
        javax.swing.Timer bannerTimer = new javax.swing.Timer(10_000, e -> reminderBanner.setVisible(false));
        bannerTimer.setRepeats(false);
        bannerTimer.start();

        setVisible(true);
    }

    // ── Table styling (renderers + header) ────────────────────────────
    private void applyTableStyle() {
        // Default renderer for text columns
        empTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lbl.setForeground(TEXT_GRAY);
                lbl.setBackground(sel ? ROW_SEL : (row % 2 == 0 ? WHITE : ROW_ALT));
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                        BorderFactory.createEmptyBorder(0, 16, 0, 16)));
                return lbl;
            }
        });

        // Header renderer
        empTable.getTableHeader().setPreferredSize(new Dimension(0, 42));
        empTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setBackground(HDR_BG);
                lbl.setForeground(TEXT_GRAY);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COL),
                        BorderFactory.createEmptyBorder(0, 16, 0, 16)));
                return lbl;
            }
        });

        // Column-specific renderers (override default; set AFTER setDefaultRenderer)
        empTable.getColumnModel().getColumn(0).setCellRenderer(new AvatarNameRenderer());
        empTable.getColumnModel().getColumn(6).setCellRenderer(new DotMenuRenderer());
    }

    // ── Apply all active filters and reload table ──────────────────────
    private void applyFilters() {
        String text   = (searchField == null || searchField.getForeground().equals(TEXT_GRAY))
                        ? "" : searchField.getText().trim();
        String desig  = (filterDesig == null || filterDesig.getSelectedIndex() == 0)
                        ? "" : (String) filterDesig.getSelectedItem();
        String edu    = (filterEdu   == null || filterEdu.getSelectedIndex()   == 0)
                        ? "" : (String) filterEdu.getSelectedItem();
        loadEmployees(text, desig, edu);
    }

    // ── Load employees from DB with combined filters ────────────────────
    void loadEmployees(String search, String desig, String edu) {
        tableModel.setRowCount(0);
        empIds.clear();
        empNames.clear();
        try {
            Conn c = new Conn();
            StringBuilder sql = new StringBuilder(
                "SELECT name,designation,education,salary,phone,empid FROM employee");

            List<String> conditions = new ArrayList<>();
            if (search != null && !search.isEmpty()) {
                conditions.add("(name LIKE '%" + search + "%'"
                        + " OR designation LIKE '%" + search + "%'"
                        + " OR empid LIKE '%" + search + "%')");
            }
            if (desig != null && !desig.isEmpty()) {
                conditions.add("designation = '" + desig + "'");
            }
            if (edu != null && !edu.isEmpty()) {
                conditions.add("education = '" + edu + "'");
            }
            if (!conditions.isEmpty()) {
                sql.append(" WHERE ").append(String.join(" AND ", conditions));
            }
            sql.append(" ORDER BY empid");

            ResultSet rs = c.statement.executeQuery(sql.toString());
            while (rs.next()) {
                String eid  = rs.getString("empid");
                String name = rs.getString("name");
                empIds.add(eid);
                empNames.add(name);
                tableModel.addRow(new Object[]{
                    name,
                    rs.getString("designation"),
                    rs.getString("education"),
                    rs.getString("salary"),
                    rs.getString("phone"),
                    eid, "..."
                });
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }

        // Re-apply column renderers after model change
        empTable.getColumnModel().getColumn(0).setCellRenderer(new AvatarNameRenderer());
        empTable.getColumnModel().getColumn(6).setCellRenderer(new DotMenuRenderer());

        // Update count label
        if (countLabel != null) {
            boolean active = (search != null && !search.isEmpty())
                          || (desig  != null && !desig.isEmpty())
                          || (edu    != null && !edu.isEmpty());
            countLabel.setText((active ? "Filtered" : "All") + " Employees  (" + empIds.size() + ")");
        }
    }

    // Convenience — initial load with no filters
    void loadEmployees(String filter) {
        loadEmployees(filter, "", "");
    }

    // ── Load summary stats ─────────────────────────────────────────────
    // s[0]=Monthly payroll net (falls back to SUM salary)
    // s[1]=AVG(salary)  s[2]=distinct designations
    // s[3]=pending leaves  s[4]=total employee count
    private int[] loadStats() {
        int[] s = {0, 0, 0, 0, 0};
        try {
            Conn c = new Conn();
            ResultSet rs;
            // s[0]: try current-month payroll net_pay total, fall back to salary sum
            boolean payrollLoaded = false;
            try {
                rs = c.statement.executeQuery(
                    "SELECT ROUND(SUM(net_pay),0) FROM payroll" +
                    " WHERE pay_month=DATE_FORMAT(NOW(),'%Y-%m') AND status IN ('Processed','Paid')");
                if (rs.next()) {
                    int v = rs.getInt(1);
                    if (v > 0) { s[0] = v; payrollLoaded = true; }
                }
            } catch (Exception ignored) {}
            if (!payrollLoaded) {
                rs = c.statement.executeQuery("SELECT ROUND(SUM(CAST(salary AS DECIMAL(12,2))),0) FROM employee");
                if (rs.next()) s[0] = rs.getInt(1);
            }
            rs = c.statement.executeQuery("SELECT ROUND(AVG(CAST(salary AS DECIMAL(10,2))),0) FROM employee");
            if (rs.next()) s[1] = rs.getInt(1);
            rs = c.statement.executeQuery("SELECT COUNT(DISTINCT designation) FROM employee");
            if (rs.next()) s[2] = rs.getInt(1);
            try {
                rs = c.statement.executeQuery(
                    "SELECT COUNT(*) FROM leave_application WHERE status='Pending'");
                if (rs.next()) s[3] = rs.getInt(1);
            } catch (Exception ignored) { s[3] = 0; } // table may not exist yet
            rs = c.statement.executeQuery("SELECT COUNT(*) FROM employee");
            if (rs.next()) s[4] = rs.getInt(1);
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }
        return s;
    }

    // ── Check if this month's payroll is unprocessed (14-B) ───────────
    private void checkPayrollReminder(JPanel banner, JLabel lbl) {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT COUNT(*) FROM payroll WHERE pay_month=DATE_FORMAT(NOW(),'%Y-%m')");
            if (rs.next() && rs.getInt(1) == 0) {
                String monthName = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy",
                        java.util.Locale.ENGLISH));
                lbl.setText("⚠  Payroll for " + monthName + " has not been processed yet.");
                banner.setVisible(true);
            }
            c.close();
        } catch (Exception ignored) {}
    }

    // ── Employee details dialog ────────────────────────────────────────
    void showDetails(String empid) {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT * FROM employee WHERE empid='" + empid + "'");
            if (!rs.next()) { c.close(); return; }

            String empName = rs.getString("name");
            String[][] fields = {
                {"Employee ID",   rs.getString("empid")},
                {"Full Name",     rs.getString("name")},
                {"Father's Name", rs.getString("fname")},
                {"Date of Birth", rs.getString("dob")},
                {"Designation",   rs.getString("designation")},
                {"Education",     rs.getString("education")},
                {"Salary",        "BDT " + rs.getString("salary")},
                {"Phone",         rs.getString("phone")},
                {"Email",         rs.getString("email")},
                {"Address",       rs.getString("address")},
            };
            c.close();

            JDialog dlg = new JDialog(this, "Employee Details", true);
            dlg.setSize(460, 520);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(null);
            dlg.setResizable(false);
            dlg.getContentPane().setBackground(WHITE);

            // Dialog header
            JPanel hdr = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    int W = getWidth(), H = getHeight();
                    g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                    g2.fillRect(0, 0, W, H);
                    // Avatar
                    Color ac = AV[Math.abs(empName.hashCode()) % AV.length];
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillOval(18, 12, 46, 46);
                    g2.setColor(ac);
                    g2.fillOval(20, 14, 42, 42);
                    g2.setColor(WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                    FontMetrics fm = g2.getFontMetrics();
                    String init = empName.isEmpty() ? "?" : String.valueOf(empName.charAt(0)).toUpperCase();
                    g2.drawString(init, 41 - fm.stringWidth(init) / 2, 44);
                    // Name + ID
                    g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                    g2.setColor(WHITE);
                    g2.drawString(empName, 76, 32);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    g2.setColor(new Color(180, 200, 230));
                    g2.drawString("Employee ID: " + empid, 76, 52);
                }
            };
            hdr.setBounds(0, 0, 460, 70);
            dlg.add(hdr);

            // Field rows
            int fy = 82;
            for (String[] f : fields) {
                JLabel key = new JLabel(f[0]);
                key.setBounds(22, fy, 150, 20);
                key.setFont(new Font("SansSerif", Font.BOLD, 11));
                key.setForeground(TEXT_GRAY);
                dlg.add(key);

                JLabel val = new JLabel(f[1] != null && !f[1].isBlank() ? f[1] : "—");
                val.setBounds(188, fy, 250, 20);
                val.setFont(new Font("SansSerif", Font.PLAIN, 12));
                val.setForeground(TEXT_DARK);
                dlg.add(val);

                JPanel sep = new JPanel();
                sep.setBackground(new Color(236, 240, 248));
                sep.setBounds(22, fy + 22, 416, 1);
                dlg.add(sep);

                fy += 34;
            }

            // Action buttons
            JButton editBtn = makePrimaryBtn("Edit Employee", C_AMBER, new Color(180, 98, 0));
            editBtn.setBounds(70, fy + 10, 140, 32);
            editBtn.addActionListener(ev -> { dlg.dispose(); dispose(); new UpdateEmployee(empid); });
            dlg.add(editBtn);

            JButton closeBtn = makeSecondaryBtn("Close");
            closeBtn.setBounds(250, fy + 10, 140, 32);
            closeBtn.addActionListener(ev -> dlg.dispose());
            dlg.add(closeBtn);

            dlg.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load details: " + ex.getMessage());
        }
    }

    // ── Confirm + delete employee ──────────────────────────────────────
    void confirmAndDelete(String empid, String name) {
        int r = JOptionPane.showConfirmDialog(this,
                "Delete \"" + name + "\" (" + empid + ")?\n\nThis action is permanent and cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            try {
                Conn c = new Conn();
                c.statement.executeUpdate("DELETE FROM employee WHERE empid='" + empid + "'");
                c.close();
                JOptionPane.showMessageDialog(this,
                        "\"" + name + "\" was deleted successfully.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new Main_class();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Stat card panel ────────────────────────────────────────────────
    private void addStatCard(JPanel parent, String title, String value, String sub,
                             Color accent, int x, int y, int w, int h) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth(), H = getHeight();

                // White card with shadow-like border
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, W, H, 10, 10));
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, W - 1, H - 1, 10, 10));

                // Left accent stripe
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, H, 5, 5));

                // Icon circle (right side)
                int iD = 42, iX = W - 60, iY = (H - iD) / 2;
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 14));
                g2.fillOval(iX, iY, iD, iD);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120));
                g2.setFont(new Font("SansSerif", Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                String icon = title.startsWith("Monthly") ? "$" :
                              title.startsWith("Total S") ? "$" :
                              title.startsWith("Avg")     ? "~" :
                              title.startsWith("Des")     ? "D" :
                              title.startsWith("Pending") ? "!" : "?";
                g2.drawString(icon, iX + iD/2 - fm.stringWidth(icon)/2, iY + iD/2 + fm.getAscent()/2 - 3);

                // Title label (small)
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString(title.toUpperCase(), 18, 24);

                // Value (big bold number — smaller font for long strings)
                g2.setColor(TEXT_DARK);
                int vSize = value.length() > 7 ? 20 : 28;
                g2.setFont(new Font("SansSerif", Font.BOLD, vSize));
                g2.drawString(value, 18, vSize > 20 ? 64 : 60);

                // Sub label
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 190));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString(sub, 18, 82);
            }
        };
        card.setOpaque(false);
        card.setBounds(x, y, w, h);
        parent.add(card);
    }

    // ── Avatar + Name cell renderer ────────────────────────────────────
    class AvatarNameRenderer extends JComponent implements TableCellRenderer {
        private String name = "";
        private boolean sel = false;
        private boolean even = true;

        AvatarNameRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean focus, int row, int col) {
            name = value != null ? value.toString() : "";
            sel  = isSelected;
            even = (row % 2 == 0);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            int W = getWidth(), H = getHeight();

            // Row background
            g2.setColor(sel ? ROW_SEL : (even ? WHITE : ROW_ALT));
            g2.fillRect(0, 0, W, H);

            // Bottom row divider
            g2.setColor(new Color(236, 240, 248));
            g2.fillRect(0, H - 1, W, 1);

            // Avatar circle
            int aD = 32, aX = 16, aY = (H - aD) / 2;
            Color ac = AV[Math.abs(name.hashCode()) % AV.length];
            g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 22));
            g2.fillOval(aX - 3, aY - 3, aD + 6, aD + 6); // soft glow ring
            g2.setColor(ac);
            g2.fillOval(aX, aY, aD, aD);
            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            String init = name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase();
            g2.drawString(init, aX + aD / 2 - fm.stringWidth(init) / 2,
                    aY + aD / 2 + fm.getAscent() / 2 - 3);

            // Name text
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.drawString(name, aX + aD + 12, H / 2 + 5);
        }
    }

    // ── 3-dot renderer ────────────────────────────────────────────────
    class DotMenuRenderer extends JComponent implements TableCellRenderer {
        private boolean sel  = false;
        private boolean even = true;
        DotMenuRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean s, boolean f, int row, int col) {
            sel = s; even = (row % 2 == 0); return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int W = getWidth(), H = getHeight();
            g2.setColor(sel ? ROW_SEL : (even ? WHITE : ROW_ALT));
            g2.fillRect(0, 0, W, H);
            g2.setColor(new Color(236, 240, 248)); g2.fillRect(0, H - 1, W, 1);
            // Dot button circle — centred in the 68 px column
            int d = 30, bx = (W - d) / 2, by = (H - d) / 2;
            g2.setColor(new Color(218, 226, 240));
            g2.fillOval(bx, by, d, d);
            g2.setColor(new Color(80, 100, 145));
            int cx = W / 2;
            for (int i = -5; i <= 5; i += 5) g2.fillOval(cx + i - 2, H / 2 - 2, 4, 4);
        }
    }

    // ── Context menu (built from table MouseListener, not a CellEditor) ──
    private JPopupMenu buildContextMenu(String empid, String name) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(WHITE);
        menu.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        menu.add(popupItem("View Details",    TEXT_DARK, e -> showDetails(empid)));
        menu.add(popupItem("Edit Employee",   TEXT_DARK, e -> { dispose(); new UpdateEmployee(empid); }));
        menu.add(popupItem("Leave History",   TEXT_DARK, e -> LeaveHistory.show(this, empid, name)));
        menu.addSeparator();
        menu.add(popupItem("Remove Employee", DANGER,    e -> confirmAndDelete(empid, name)));
        return menu;
    }

    private JMenuItem popupItem(String text, Color fg, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("SansSerif", Font.PLAIN, 12));
        item.setForeground(fg);
        item.setBackground(WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 24));
        item.addActionListener(action);
        return item;
    }

    // ── Filter combo helpers ───────────────────────────────────────────

    /** Query DB for distinct values of a column; prepend "All <col>" as first item. */
    private String[] buildFilterOptions(String column) {
        List<String> items = new ArrayList<>();
        String label = column.equals("designation") ? "All Designations" : "All Education";
        items.add(label);
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT DISTINCT " + column + " FROM employee ORDER BY " + column);
            while (rs.next()) {
                String v = rs.getString(1);
                if (v != null && !v.isBlank()) items.add(v);
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }
        return items.toArray(new String[0]);
    }

    private void styleFilterCombo(JComboBox<String> combo, String tooltip) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        combo.setBackground(WHITE);
        combo.setForeground(TEXT_GRAY);
        combo.setFocusable(false);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        combo.setToolTipText("Filter by " + tooltip);
        combo.setBorder(new LineBorder(BORDER_COL, 1));
    }

    // ── Button helpers ─────────────────────────────────────────────────
    JButton makePrimaryBtn(String text, Color bg, Color hoverBg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    JButton makeSecondaryBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(WHITE); btn.setForeground(TEXT_GRAY);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(BORDER_COL, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(NAVY); btn.setBorder(new LineBorder(NAVY, 1)); }
            public void mouseExited (MouseEvent e) { btn.setForeground(TEXT_GRAY); btn.setBorder(new LineBorder(BORDER_COL, 1)); }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main_class::new);
    }
}
