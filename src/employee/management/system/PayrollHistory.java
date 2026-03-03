package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PayrollHistory extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color NAVY       = new Color( 26,  54, 105);
    private static final Color NAVY_DARK  = new Color( 18,  38,  76);
    private static final Color ACCENT     = new Color( 66, 153, 225);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BG         = new Color(244, 246, 250);
    private static final Color BORDER_COL = new Color(214, 220, 232);
    private static final Color TEXT_DARK  = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY  = new Color(100, 115, 140);
    private static final Color TEXT_DIM   = new Color(160, 170, 185);
    private static final Color C_GREEN    = new Color( 34, 139,  87);
    private static final Color C_GREEN_H  = new Color( 22, 108,  67);
    private static final Color C_AMBER    = new Color(217, 119,   6);
    private static final Color DANGER     = new Color(185,  28,  28);
    private static final Color ROW_ALT    = new Color(250, 251, 254);
    private static final Color ROW_SEL    = new Color(219, 234, 254);
    private static final Color HDR_BG     = new Color(248, 250, 253);

    // Status badge colors
    private static final Color DRAFT_BG     = new Color(241, 245, 249);
    private static final Color DRAFT_FG     = new Color( 71,  85, 105);
    private static final Color PROC_BG      = new Color(219, 234, 254);
    private static final Color PROC_FG      = new Color( 29,  78, 216);
    private static final Color PAID_BG      = new Color(220, 252, 231);
    private static final Color PAID_FG      = new Color( 21, 128,  61);

    // Avatar palette
    private static final Color[] AV = {
        new Color( 99, 102, 241), new Color( 16, 185, 129),
        new Color(245, 158,  11), new Color(239,  68,  68),
        new Color( 59, 130, 246), new Color(168,  85, 247),
        new Color( 20, 184, 166), new Color(249, 115,  22),
    };

    // ── State ──────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable histTable;
    private JComboBox<String> filterMonth, filterStatus;
    private JTextField searchField;
    private JLabel countLabel;

    // Parallel lists for row context
    private final List<String> rowIds     = new ArrayList<>(); // payroll.id
    private final List<String> rowEmpIds  = new ArrayList<>();
    private final List<String> rowNames   = new ArrayList<>();
    private final List<String> rowMonths  = new ArrayList<>();
    private final List<String> rowStatus  = new ArrayList<>();

    PayrollHistory() {
        setTitle("Payroll History — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { goToDashboard(); }
        });

        int[] stats = loadStats();

        // ── Painted background ─────────────────────────────────────────
        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth(), H = getHeight();

                // Page background
                g2.setColor(BG);
                g2.fillRect(0, 0, W, H);

                // Navy header
                g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                g2.fillRect(0, 0, W, 68);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.drawString("Payroll History", 24, 30);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(180, 200, 230));
                g2.drawString("View, manage, and export all payroll records", 24, 50);

                // Toolbar strip
                g2.setColor(WHITE);
                g2.fillRect(0, 172, W, 48);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 172, W, 172);
                g2.drawLine(0, 220, W, 220);

                // Footer strip
                g2.setColor(new Color(237, 240, 247));
                g2.fillRect(0, H - 38, W, 38);
                g2.setColor(BORDER_COL);
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

        // ── Header buttons ─────────────────────────────────────────────
        JButton dashBtn = makeGhostBtn("← Dashboard");
        dashBtn.setBounds(752, 18, 120, 30);
        dashBtn.addActionListener(e -> goToDashboard());
        bg.add(dashBtn);

        JButton processBtn = makeGhostBtn("⚡ Process");
        processBtn.setBounds(882, 18, 100, 30);
        processBtn.addActionListener(e -> { dispose(); new PayrollProcess(); });
        bg.add(processBtn);

        JButton cfgBtn = makeGhostBtn("⚙  Config");
        cfgBtn.setBounds(992, 18, 84, 30);
        cfgBtn.addActionListener(e -> { dispose(); new PayrollConfig(); });
        bg.add(cfgBtn);

        // ── Stat cards (y=76, h=88) ────────────────────────────────────
        // 3 cards: (1100 - 2×22 - 2×16) / 3 = (1100 - 76) / 3 = 341.3 → use 340
        // x: 22, 22+340+16=378, 378+340+16=734
        addStatCard(bg, "Total Records",       String.valueOf(stats[0]),  "All-time payroll runs",     NAVY,    22,  76, 340, 88);
        addStatCard(bg, "Paid This Month",      String.valueOf(stats[1]),  "Completed payments",        C_GREEN, 378, 76, 340, 88);
        addStatCard(bg, "Net Paid This Month",  fmtStat(stats[2]),        "BDT · Net Pay Total",       C_AMBER, 734, 76, 344, 88);

        // ── Toolbar (y=172) ────────────────────────────────────────────
        countLabel = new JLabel("All Records  (" + stats[0] + ")");
        countLabel.setBounds(24, 184, 220, 26);
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        countLabel.setForeground(TEXT_DARK);
        bg.add(countLabel);

        // Search
        searchField = new JTextField("Search by employee or ID...");
        searchField.setBounds(254, 184, 230, 26);
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
                    searchField.setText("Search by employee or ID...");
                }
                searchField.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_COL, 1), BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            }
        });
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { applyFilters(); }
        });
        bg.add(searchField);

        // Month filter
        filterMonth = new JComboBox<>(buildMonthOptions());
        filterMonth.setBounds(498, 184, 160, 26);
        styleCombo(filterMonth, "Filter by Month");
        filterMonth.addActionListener(e -> applyFilters());
        bg.add(filterMonth);

        // Status filter — default index 0 = "All Statuses" (shows everything on open)
        filterStatus = new JComboBox<>(new String[]{"All Statuses","Draft","Processed","Paid"});
        filterStatus.setSelectedIndex(0);
        filterStatus.setBounds(670, 184, 140, 26);
        styleCombo(filterStatus, "Filter by Status");
        filterStatus.addActionListener(e -> applyFilters());
        bg.add(filterStatus);

        // Clear
        JButton clearBtn = makeSecondaryBtn("✕  Clear");
        clearBtn.setBounds(822, 184, 88, 26);
        clearBtn.addActionListener(e -> {
            filterMonth.setSelectedIndex(0);
            filterStatus.setSelectedIndex(0);
            if (!searchField.getForeground().equals(TEXT_GRAY)) searchField.setText("");
            applyFilters();
        });
        bg.add(clearBtn);

        // Export CSV
        JButton exportBtn = makeSecondaryBtn("⬇ Export CSV");
        exportBtn.setBounds(920, 184, 108, 26);
        exportBtn.addActionListener(e -> exportCsv());
        bg.add(exportBtn);

        // ── Table (y=220) ──────────────────────────────────────────────
        String[] cols = {"Employee", "Month", "Basic", "Gross Pay", "Deductions", "Net Pay", "Status", ""};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        histTable = new JTable(tableModel);
        histTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        histTable.setRowHeight(44);
        histTable.setShowGrid(false);
        histTable.setIntercellSpacing(new Dimension(0, 0));
        histTable.setSelectionBackground(ROW_SEL);
        histTable.setSelectionForeground(TEXT_DARK);
        histTable.setFillsViewportHeight(true);
        histTable.setBackground(WHITE);
        histTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        histTable.getTableHeader().setReorderingAllowed(false);
        histTable.getTableHeader().setResizingAllowed(false);

        // Column widths — sum = 1083
        int[] cw = {280, 120, 110, 125, 125, 130, 125, 68};
        for (int i = 0; i < cw.length; i++) {
            TableColumn tc = histTable.getColumnModel().getColumn(i);
            tc.setPreferredWidth(cw[i]);
            if (i == 7) { tc.setMaxWidth(68); tc.setMinWidth(68); }
        }

        applyTableStyle();

        // 3-dot click handler
        histTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = histTable.columnAtPoint(e.getPoint());
                int row = histTable.rowAtPoint(e.getPoint());
                if (col == 7 && row >= 0 && row < rowIds.size()) {
                    histTable.setRowSelectionInterval(row, row);
                    JPopupMenu menu = buildContextMenu(row);
                    Rectangle cell = histTable.getCellRect(row, col, false);
                    int menuX = cell.x + cell.width - menu.getPreferredSize().width;
                    int menuY = cell.y + cell.height;
                    menu.show(histTable, menuX, menuY);
                }
            }
        });
        histTable.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                histTable.setCursor(histTable.columnAtPoint(e.getPoint()) == 7
                        ? new Cursor(Cursor.HAND_CURSOR)
                        : new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JScrollPane scroll = new JScrollPane(histTable);
        scroll.setBounds(0, 220, 1100, 404);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        scroll.getViewport().setBackground(WHITE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bg.add(scroll);

        loadRecords("", "", "");
        setVisible(true);
    }

    // ── Load stats ─────────────────────────────────────────────────────
    private int[] loadStats() {
        int[] s = {0, 0, 0};
        try {
            Conn c = new Conn();
            ResultSet rs;
            rs = c.statement.executeQuery("SELECT COUNT(*) FROM payroll");
            if (rs.next()) s[0] = rs.getInt(1);
            rs = c.statement.executeQuery(
                "SELECT COUNT(*) FROM payroll WHERE status='Paid' AND pay_month=DATE_FORMAT(NOW(),'%Y-%m')");
            if (rs.next()) s[1] = rs.getInt(1);
            rs = c.statement.executeQuery(
                "SELECT ROUND(SUM(net_pay),0) FROM payroll WHERE pay_month=DATE_FORMAT(NOW(),'%Y-%m')");
            if (rs.next()) s[2] = rs.getInt(1);
            c.close();
        } catch (Exception ignored) {}
        return s;
    }

    // ── Load records with filters ──────────────────────────────────────
    private void loadRecords(String search, String month, String status) {
        tableModel.setRowCount(0);
        rowIds.clear(); rowEmpIds.clear(); rowNames.clear();
        rowMonths.clear(); rowStatus.clear();

        try {
            Conn c = new Conn();
            // LEFT JOIN — keeps payroll rows even if employee was deleted/re-created
            StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.empid, COALESCE(e.name, p.empid) AS name, p.pay_month, p.basic_salary, " +
                "p.gross_pay, (p.tax_deduction+p.pf_deduction+p.other_deduction) AS deductions, " +
                "p.net_pay, p.status " +
                "FROM payroll p LEFT JOIN employee e ON p.empid=e.empid");

            List<String> conds = new ArrayList<>();
            if (search != null && !search.isEmpty())
                conds.add("(e.name LIKE '%" + search + "%' OR p.empid LIKE '%" + search + "%')");
            if (month != null && !month.isEmpty())
                conds.add("p.pay_month='" + month + "'");
            if (status != null && !status.isEmpty())
                conds.add("p.status='" + status + "'");

            if (!conds.isEmpty()) sql.append(" WHERE ").append(String.join(" AND ", conds));
            sql.append(" ORDER BY p.pay_month DESC, e.name");

            ResultSet rs = c.statement.executeQuery(sql.toString());
            while (rs.next()) {
                String pid    = String.valueOf(rs.getInt("id"));
                String eid    = rs.getString("empid");
                String name   = rs.getString("name");
                String mon    = rs.getString("pay_month");
                double basic  = rs.getDouble("basic_salary");
                double gross  = rs.getDouble("gross_pay");
                double deduct = rs.getDouble("deductions");
                double net    = rs.getDouble("net_pay");
                String stat   = rs.getString("status");

                rowIds.add(pid); rowEmpIds.add(eid); rowNames.add(name);
                rowMonths.add(mon); rowStatus.add(stat);

                tableModel.addRow(new Object[]{
                    name, formatMonth(mon),
                    fmt(basic), fmt(gross), fmt(deduct), fmt(net),
                    stat, "..."
                });
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }

        applyTableStyle();
        if (countLabel != null)
            countLabel.setText("Payroll Records  (" + rowIds.size() + ")");
    }

    private void applyFilters() {
        String search = (searchField == null || searchField.getForeground().equals(TEXT_GRAY))
                ? "" : searchField.getText().trim();
        String month  = (filterMonth  == null || filterMonth.getSelectedIndex() == 0)
                ? "" : monthToKey((String) filterMonth.getSelectedItem());
        String status = (filterStatus == null || filterStatus.getSelectedIndex() == 0)
                ? "" : (String) filterStatus.getSelectedItem();
        loadRecords(search, month, status);
    }

    // ── Context menu per row ───────────────────────────────────────────
    private JPopupMenu buildContextMenu(int row) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(WHITE);
        menu.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1), BorderFactory.createEmptyBorder(4, 0, 4, 0)));

        String pid    = rowIds.get(row);
        String eid    = rowEmpIds.get(row);
        String name   = rowNames.get(row);
        String month  = rowMonths.get(row);
        String status = rowStatus.get(row);

        menu.add(popupItem("View Payslip", TEXT_DARK,
                e -> PayslipDialog.show(this, eid, month, name)));

        if (!"Paid".equals(status)) {
            menu.add(popupItem("Mark as Paid", C_GREEN,
                    e -> markAsPaid(pid, row)));
        }
        menu.addSeparator();
        menu.add(popupItem("Delete Record", DANGER,
                e -> deleteRecord(pid, row)));
        return menu;
    }

    // ── Mark a record as Paid ──────────────────────────────────────────
    private void markAsPaid(String pid, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Mark this payroll record as Paid?\n\nEmployee: " + rowNames.get(row)
            + "\nMonth: " + formatMonth(rowMonths.get(row)),
            "Mark as Paid", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Conn c = new Conn();
            c.statement.executeUpdate(
                "UPDATE payroll SET status='Paid', paid_on=NOW() WHERE id=" + pid);
            c.close();
            applyFilters();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── Delete a payroll record ────────────────────────────────────────
    private void deleteRecord(String pid, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete payroll record for \"" + rowNames.get(row) + "\" — "
            + formatMonth(rowMonths.get(row)) + "?\n\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Conn c = new Conn();
            c.statement.executeUpdate("DELETE FROM payroll WHERE id=" + pid);
            c.close();
            applyFilters();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── Export visible records to CSV ──────────────────────────────────
    private void exportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("payroll_export.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
            // Header row
            fw.write("Employee,Emp ID,Month,Basic Salary,Gross Pay,Deductions,Net Pay,Status\n");
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                fw.write(
                    "\"" + rowNames.get(r) + "\"," +
                    rowEmpIds.get(r) + "," +
                    rowMonths.get(r) + "," +
                    tableModel.getValueAt(r, 2) + "," +
                    tableModel.getValueAt(r, 3) + "," +
                    tableModel.getValueAt(r, 4) + "," +
                    tableModel.getValueAt(r, 5) + "," +
                    rowStatus.get(r) + "\n"
                );
            }
            JOptionPane.showMessageDialog(this,
                "Exported " + tableModel.getRowCount() + " records to:\n" + fc.getSelectedFile().getAbsolutePath(),
                "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Table styling ──────────────────────────────────────────────────
    private void applyTableStyle() {
        histTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lbl.setForeground(col == 0 ? TEXT_DARK : TEXT_GRAY);
                if (col == 0) lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                lbl.setBackground(sel ? ROW_SEL : (row % 2 == 0 ? WHITE : ROW_ALT));
                lbl.setHorizontalAlignment(col >= 2 && col <= 5 ? RIGHT : LEFT);
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                        BorderFactory.createEmptyBorder(0, 16, 0, 16)));
                return lbl;
            }
        });

        histTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        histTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setBackground(HDR_BG); lbl.setForeground(TEXT_GRAY);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setHorizontalAlignment(col >= 2 && col <= 5 ? RIGHT : LEFT);
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COL),
                        BorderFactory.createEmptyBorder(0, 16, 0, 16)));
                return lbl;
            }
        });

        histTable.getColumnModel().getColumn(0).setCellRenderer(new AvatarNameRenderer());
        histTable.getColumnModel().getColumn(6).setCellRenderer(new PayrollStatusRenderer());
        histTable.getColumnModel().getColumn(7).setCellRenderer(new DotMenuRenderer());
    }

    // ── Avatar name renderer ───────────────────────────────────────────
    class AvatarNameRenderer extends JComponent implements TableCellRenderer {
        private String name = ""; private boolean sel, even;
        AvatarNameRenderer() { setOpaque(true); }
        @Override public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean f, int r, int c) {
            name = v != null ? v.toString() : ""; sel = s; even = (r % 2 == 0); return this;
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            int W = getWidth(), H = getHeight();
            g2.setColor(sel ? ROW_SEL : (even ? WHITE : ROW_ALT));
            g2.fillRect(0, 0, W, H);
            g2.setColor(new Color(236, 240, 248)); g2.fillRect(0, H - 1, W, 1);
            int aD = 28, aX = 12, aY = (H - aD) / 2;
            Color ac = AV[Math.abs(name.hashCode()) % AV.length];
            g2.setColor(ac); g2.fillOval(aX, aY, aD, aD);
            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String init = name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase();
            g2.drawString(init, aX + aD/2 - fm.stringWidth(init)/2, aY + aD/2 + fm.getAscent()/2 - 3);
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(name, aX + aD + 10, H / 2 + 5);
        }
    }

    // ── Status badge renderer ──────────────────────────────────────────
    class PayrollStatusRenderer extends JComponent implements TableCellRenderer {
        private String status = ""; private boolean sel, even;
        PayrollStatusRenderer() { setOpaque(true); }
        @Override public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean f, int r, int c) {
            status = v != null ? v.toString() : ""; sel = s; even = (r % 2 == 0); return this;
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int W = getWidth(), H = getHeight();
            g2.setColor(sel ? ROW_SEL : (even ? WHITE : ROW_ALT));
            g2.fillRect(0, 0, W, H);
            g2.setColor(new Color(236, 240, 248)); g2.fillRect(0, H - 1, W, 1);
            Color bg, fg;
            switch (status) {
                case "Paid":      bg = PAID_BG;  fg = PAID_FG;  break;
                case "Processed": bg = PROC_BG;  fg = PROC_FG;  break;
                default:          bg = DRAFT_BG; fg = DRAFT_FG; break;
            }
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(status), bw = tw + 18, bh = 20;
            int bx = (W - bw) / 2, by = (H - bh) / 2;
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(bx, by, bw, bh, 10, 10));
            g2.setColor(fg);
            g2.drawString(status, bx + 9, by + bh / 2 + fm.getAscent() / 2 - 2);
        }
    }

    // ── 3-dot renderer ─────────────────────────────────────────────────
    class DotMenuRenderer extends JComponent implements TableCellRenderer {
        private boolean sel, even;
        DotMenuRenderer() { setOpaque(true); }
        @Override public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean f, int r, int c) {
            sel = s; even = (r % 2 == 0); return this;
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int W = getWidth(), H = getHeight();
            g2.setColor(sel ? ROW_SEL : (even ? WHITE : ROW_ALT));
            g2.fillRect(0, 0, W, H);
            g2.setColor(new Color(236, 240, 248)); g2.fillRect(0, H - 1, W, 1);
            int d = 28, bx = (W - d) / 2, by = (H - d) / 2;
            g2.setColor(new Color(218, 226, 240)); g2.fillOval(bx, by, d, d);
            g2.setColor(new Color(80, 100, 145));
            int cx = W / 2;
            for (int i = -4; i <= 4; i += 4) g2.fillOval(cx + i - 2, H / 2 - 2, 4, 4);
        }
    }

    // ── Stat card ──────────────────────────────────────────────────────
    private void addStatCard(JPanel parent, String title, String value, String sub,
                             Color accent, int x, int y, int w, int h) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth(), H = getHeight();
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, W, H, 10, 10));
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, W - 1, H - 1, 10, 10));
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, H, 5, 5));
                int iD = 38, iX = W - 54, iY = (H - iD) / 2;
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 14));
                g2.fillOval(iX, iY, iD, iD);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120));
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String icon = title.contains("Record") ? "#"
                            : title.contains("Paid") && title.contains("Net") ? "$"
                            : "✓";
                g2.drawString(icon, iX + iD/2 - fm.stringWidth(icon)/2, iY + iD/2 + fm.getAscent()/2 - 2);
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString(title.toUpperCase(), 16, 22);
                g2.setColor(TEXT_DARK);
                int vSize = value.length() > 7 ? 18 : 26;
                g2.setFont(new Font("SansSerif", Font.BOLD, vSize));
                g2.drawString(value, 16, vSize > 18 ? 60 : 56);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 190));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString(sub, 16, 76);
            }
        };
        card.setOpaque(false);
        card.setBounds(x, y, w, h);
        parent.add(card);
    }

    // ── Helpers ────────────────────────────────────────────────────────
    private String[] buildMonthOptions() {
        List<String> opts = new ArrayList<>();
        opts.add("All Months");
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT DISTINCT pay_month FROM payroll ORDER BY pay_month DESC LIMIT 36");
            while (rs.next()) opts.add(formatMonth(rs.getString("pay_month")));
            c.close();
        } catch (Exception ignored) {}
        return opts.toArray(new String[0]);
    }

    private String monthToKey(String display) {
        String[] names = {"January","February","March","April","May","June",
                          "July","August","September","October","November","December"};
        if (display == null || !display.contains(" ")) return display;
        String[] p = display.split(" ");
        if (p.length < 2) return display;
        int m = 1;
        for (int i = 0; i < names.length; i++) if (names[i].equals(p[0])) { m = i+1; break; }
        return p[1] + "-" + String.format("%02d", m);
    }

    private String formatMonth(String ym) {
        if (ym == null || ym.length() < 7) return ym;
        String[] names = {"January","February","March","April","May","June",
                          "July","August","September","October","November","December"};
        int m = Integer.parseInt(ym.substring(5, 7));
        return names[m - 1] + " " + ym.substring(0, 4);
    }

    private static String fmt(double v)     { return String.format("%,.2f", v); }
    private static String fmtStat(int v)    { return v == 0 ? "0" : String.format("%,d", v); }

    private JMenuItem popupItem(String text, Color fg, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("SansSerif", Font.PLAIN, 12));
        item.setForeground(fg);
        item.setBackground(WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 24));
        item.addActionListener(action);
        return item;
    }

    private void styleCombo(JComboBox<String> c, String tip) {
        c.setFont(new Font("SansSerif", Font.PLAIN, 12));
        c.setBackground(WHITE); c.setForeground(TEXT_GRAY);
        c.setFocusable(false); c.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.setToolTipText(tip); c.setBorder(new LineBorder(BORDER_COL, 1));
    }

    private JButton makeGhostBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBorder(new LineBorder(Color.WHITE, 1)); }
            public void mouseExited (MouseEvent e) { btn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        return btn;
    }

    private JButton makeSecondaryBtn(String text) {
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

    private void goToDashboard() { dispose(); new Main_class(); }
}
