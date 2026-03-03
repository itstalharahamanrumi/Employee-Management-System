package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class PayrollProcess extends JFrame {

    // ── Column indices ─────────────────────────────────────────────────
    private static final int COL_EMP   = 0;
    private static final int COL_BASIC = 1;
    private static final int COL_HOUSE = 2;
    private static final int COL_MED   = 3;
    private static final int COL_BONUS = 4;
    private static final int COL_GROSS = 5;
    private static final int COL_TAX   = 6;
    private static final int COL_PF    = 7;
    private static final int COL_NET   = 8;

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
    private static final Color ROW_ALT    = new Color(250, 251, 254);
    private static final Color ROW_SEL    = new Color(219, 234, 254);
    private static final Color HDR_BG     = new Color(248, 250, 253);
    private static final Color ROW_DONE   = new Color(248, 250, 253);
    private static final Color ROW_DONE_T = new Color(140, 155, 175);

    // ── Avatar palette ─────────────────────────────────────────────────
    private static final Color[] AV = {
        new Color( 99, 102, 241), new Color( 16, 185, 129),
        new Color(245, 158,  11), new Color(239,  68,  68),
        new Color( 59, 130, 246), new Color(168,  85, 247),
        new Color( 20, 184, 166), new Color(249, 115,  22),
    };

    // ── State ──────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable payTable;
    private JComboBox<String> monthCombo;
    private JLabel infoLabel;

    private final List<String>  empIds       = new ArrayList<>();
    private final List<String>  empNames     = new ArrayList<>();
    private final List<Boolean> processedRows = new ArrayList<>();
    private boolean isRecalculating = false;

    // Config rates loaded from DB
    private double taxRate  = 0.10;
    private double pfRate   = 0.05;
    private double houseRate = 0.10;
    private double medRate  = 0.05;

    // Rate labels updated after loadConfig()
    private JLabel rateTaxLbl, ratePfLbl, rateHouseLbl, rateMedLbl;

    PayrollProcess() {
        setTitle("Process Monthly Payroll — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { goBack(); }
        });

        loadConfig();

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
                g2.drawString("Process Monthly Payroll", 24, 30);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(180, 200, 230));
                g2.drawString("Auto-calculate earnings & deductions for all employees. Edit bonus per employee before processing.", 24, 50);

                // White month/config strip
                g2.setColor(WHITE);
                g2.fillRect(0, 76, W, 46);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 76, W, 76);
                g2.drawLine(0, 122, W, 122);

                // "Pay Month:" label
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.drawString("PAY MONTH:", 24, 104);

                // Rate label heading
                g2.drawString("CURRENT RATES:", 298, 104);

                // Action bar above footer
                g2.setColor(WHITE);
                g2.fillRect(0, 618, W, 44);
                g2.setColor(BORDER_COL);
                g2.drawLine(0, 618, W, 618);

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
        JButton histBtn = makeGhostBtn("← History");
        histBtn.setBounds(790, 18, 100, 30);
        histBtn.addActionListener(e -> goBack());
        bg.add(histBtn);

        JButton cfgBtn = makeGhostBtn("⚙  Config");
        cfgBtn.setBounds(900, 18, 96, 30);
        cfgBtn.addActionListener(e -> { dispose(); new PayrollConfig(); });
        bg.add(cfgBtn);

        // ── Month selector (y=88) ─────────────────────────────────────
        monthCombo = new JComboBox<>(buildMonths());
        monthCombo.setBounds(114, 88, 170, 28);
        monthCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        monthCombo.setBackground(WHITE);
        monthCombo.setForeground(TEXT_DARK);
        monthCombo.setFocusable(false);
        monthCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        monthCombo.setBorder(new LineBorder(BORDER_COL, 1));
        monthCombo.addActionListener(e -> loadEmployees());
        bg.add(monthCombo);

        // ── Rate badges ────────────────────────────────────────────────
        rateTaxLbl   = makeRateBadge("Tax: "     + pct(taxRate));
        ratePfLbl    = makeRateBadge("PF: "      + pct(pfRate));
        rateHouseLbl = makeRateBadge("House: "   + pct(houseRate));
        rateMedLbl   = makeRateBadge("Medical: " + pct(medRate));
        rateTaxLbl.setBounds(298,  88, 130, 24);
        ratePfLbl.setBounds(438,   88, 110, 24);
        rateHouseLbl.setBounds(558, 88, 130, 24);
        rateMedLbl.setBounds(698,  88, 140, 24);
        bg.add(rateTaxLbl); bg.add(ratePfLbl);
        bg.add(rateHouseLbl); bg.add(rateMedLbl);

        // ── Table setup ────────────────────────────────────────────────
        String[] cols = {
            "Employee", "Basic (BDT)", "House Allow", "Medical",
            "Bonus*", "Gross Pay", "Income Tax", "PF", "Net Pay"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == COL_BONUS
                        && r < processedRows.size()
                        && !processedRows.get(r);
            }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        tableModel.addTableModelListener(e -> {
            if (isRecalculating) return;
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == COL_BONUS) {
                recalculateRow(e.getFirstRow());
            }
        });

        payTable = new JTable(tableModel);
        payTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payTable.setRowHeight(40);
        payTable.setShowGrid(false);
        payTable.setIntercellSpacing(new Dimension(0, 0));
        payTable.setSelectionBackground(ROW_SEL);
        payTable.setSelectionForeground(TEXT_DARK);
        payTable.setFillsViewportHeight(true);
        payTable.setBackground(WHITE);
        payTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        payTable.getTableHeader().setReorderingAllowed(false);
        payTable.getTableHeader().setResizingAllowed(false);

        // Column widths — sum = 1083
        int[] cw = {230, 110, 110, 105, 105, 115, 100, 90, 118};
        for (int i = 0; i < cw.length; i++) {
            TableColumn tc = payTable.getColumnModel().getColumn(i);
            tc.setPreferredWidth(cw[i]);
        }

        applyTableStyle();

        JScrollPane scroll = new JScrollPane(payTable);
        scroll.setBounds(0, 122, 1100, 496);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        scroll.getViewport().setBackground(WHITE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bg.add(scroll);

        // ── Action bar (y=618) ─────────────────────────────────────────
        infoLabel = new JLabel(" ");
        infoLabel.setBounds(24, 629, 560, 24);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_GRAY);
        bg.add(infoLabel);

        JButton processBtn = makePrimaryBtn("⚡  Process Payroll for Selected Month", C_GREEN, C_GREEN_H);
        processBtn.setBounds(596, 625, 340, 34);
        processBtn.addActionListener(e -> processPayroll());
        bg.add(processBtn);

        // ── Load data ──────────────────────────────────────────────────
        loadEmployees();
        setVisible(true);
    }

    // ── Load config rates from DB ──────────────────────────────────────
    private void loadConfig() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery("SELECT config_key, config_value FROM payroll_config");
            while (rs.next()) {
                double v = rs.getDouble("config_value");
                switch (rs.getString("config_key")) {
                    case "tax_rate":     taxRate   = v; break;
                    case "pf_rate":      pfRate    = v; break;
                    case "house_rate":   houseRate = v; break;
                    case "medical_rate": medRate   = v; break;
                }
            }
            c.close();
        } catch (Exception ignored) {}
    }

    // ── Load employees for selected month ──────────────────────────────
    private void loadEmployees() {
        if (monthCombo == null) return;
        tableModel.setRowCount(0);
        empIds.clear();
        empNames.clear();
        processedRows.clear();

        String selMonth = monthToKey((String) monthCombo.getSelectedItem());

        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT e.empid, e.name, e.salary, " +
                "p.basic_salary, p.house_allowance, p.medical_allow, p.bonus, " +
                "p.gross_pay, p.tax_deduction, p.pf_deduction, p.net_pay, p.status " +
                "FROM employee e " +
                "LEFT JOIN payroll p ON e.empid=p.empid AND p.pay_month='" + selMonth + "' " +
                "ORDER BY e.name");

            while (rs.next()) {
                String eid  = rs.getString("empid");
                String name = rs.getString("name");
                empIds.add(eid);
                empNames.add(name);

                boolean done = rs.getString("status") != null;
                processedRows.add(done);

                double basic, house, medical, bonus, gross, tax, pf, net;
                if (done) {
                    basic   = rs.getDouble("basic_salary");
                    house   = rs.getDouble("house_allowance");
                    medical = rs.getDouble("medical_allow");
                    bonus   = rs.getDouble("bonus");
                    gross   = rs.getDouble("gross_pay");
                    tax     = rs.getDouble("tax_deduction");
                    pf      = rs.getDouble("pf_deduction");
                    net     = rs.getDouble("net_pay");
                } else {
                    try { basic = Double.parseDouble(rs.getString("salary")); }
                    catch (Exception ex) { basic = 0; }
                    house   = basic * houseRate;
                    medical = basic * medRate;
                    bonus   = 0;
                    gross   = basic + house + medical + bonus;
                    tax     = gross * taxRate;
                    pf      = gross * pfRate;
                    net     = gross - tax - pf;
                }

                tableModel.addRow(new Object[]{
                    name, fmt(basic), fmt(house), fmt(medical), fmt(bonus),
                    fmt(gross), fmt(tax), fmt(pf), fmt(net)
                });
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }

        applyTableStyle();
        updateInfoLabel();
    }

    // ── Recalculate one row when bonus changes ─────────────────────────
    private void recalculateRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        isRecalculating = true;
        try {
            double basic   = parseD(tableModel.getValueAt(row, COL_BASIC));
            double bonus   = parseD(tableModel.getValueAt(row, COL_BONUS));
            double house   = basic * houseRate;
            double medical = basic * medRate;
            double gross   = basic + house + medical + bonus;
            double tax     = gross * taxRate;
            double pf      = gross * pfRate;
            double net     = gross - tax - pf;

            tableModel.setValueAt(fmt(house),   row, COL_HOUSE);
            tableModel.setValueAt(fmt(medical), row, COL_MED);
            tableModel.setValueAt(fmt(gross),   row, COL_GROSS);
            tableModel.setValueAt(fmt(tax),     row, COL_TAX);
            tableModel.setValueAt(fmt(pf),      row, COL_PF);
            tableModel.setValueAt(fmt(net),     row, COL_NET);
        } finally {
            isRecalculating = false;
        }
        updateInfoLabel();
    }

    // ── Bulk-insert unprocessed rows into payroll ──────────────────────
    private void processPayroll() {
        long pending = processedRows.stream().filter(b -> !b).count();
        if (pending == 0) {
            JOptionPane.showMessageDialog(this,
                "All employees for this month are already processed.",
                "Nothing to process", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Process payroll for " + pending + " employee(s) — " + monthCombo.getSelectedItem() + "?\n\n"
            + "Applied rates:  Tax " + pct(taxRate) + "  |  PF " + pct(pfRate)
            + "  |  House " + pct(houseRate) + "  |  Medical " + pct(medRate),
            "Confirm Processing", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String selMonth = monthToKey((String) monthCombo.getSelectedItem());
        int done = 0;
        try {
            Conn c = new Conn();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (processedRows.get(row)) continue;

                String eid     = empIds.get(row);
                double basic   = parseD(tableModel.getValueAt(row, COL_BASIC));
                double house   = parseD(tableModel.getValueAt(row, COL_HOUSE));
                double medical = parseD(tableModel.getValueAt(row, COL_MED));
                double bonus   = parseD(tableModel.getValueAt(row, COL_BONUS));
                double gross   = parseD(tableModel.getValueAt(row, COL_GROSS));
                double tax     = parseD(tableModel.getValueAt(row, COL_TAX));
                double pf      = parseD(tableModel.getValueAt(row, COL_PF));
                double net     = parseD(tableModel.getValueAt(row, COL_NET));

                c.statement.executeUpdate(
                    "INSERT IGNORE INTO payroll " +
                    "(empid,pay_month,basic_salary,house_allowance,medical_allow,bonus," +
                    "gross_pay,tax_deduction,pf_deduction,net_pay,status,processed_on) VALUES ('" +
                    eid + "','" + selMonth + "'," +
                    basic + "," + house + "," + medical + "," + bonus + "," +
                    gross + "," + tax + "," + pf + "," + net +
                    ",'Processed',NOW())");
                done++;
            }
            c.close();

            JOptionPane.showMessageDialog(this,
                "✔  " + done + " employee(s) processed successfully for " + monthCombo.getSelectedItem() + ".",
                "Payroll Processed", JOptionPane.INFORMATION_MESSAGE);
            loadEmployees();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Update the info label below the table ─────────────────────────
    private void updateInfoLabel() {
        if (infoLabel == null) return;
        long pending = processedRows.stream().filter(b -> !b).count();
        long done    = processedRows.stream().filter(b ->  b).count();
        String month = monthCombo != null ? (String) monthCombo.getSelectedItem() : "";
        if (pending == 0 && done > 0)
            infoLabel.setText("✔  All " + done + " employees already processed for " + month + ".");
        else if (pending > 0)
            infoLabel.setText(pending + " pending  ·  " + done + " already processed  ·  "
                + "* Click a Bonus cell to edit before processing.");
        else
            infoLabel.setText("No employees found.");
    }

    // ── Table renderers ────────────────────────────────────────────────
    private void applyTableStyle() {
        payTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                boolean done = row < processedRows.size() && processedRows.get(row);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                if (done) {
                    lbl.setForeground(ROW_DONE_T);
                    lbl.setBackground(sel ? ROW_SEL : ROW_DONE);
                } else {
                    lbl.setForeground(col == COL_NET
                            ? new Color(22, 100, 60)
                            : (col == COL_EMP ? TEXT_DARK : TEXT_GRAY));
                    lbl.setFont(col == COL_NET || col == COL_EMP
                            ? new Font("SansSerif", Font.BOLD, 12)
                            : new Font("SansSerif", Font.PLAIN, 12));
                    lbl.setBackground(sel ? ROW_SEL : (row % 2 == 0 ? WHITE : ROW_ALT));
                }
                int align = col == COL_EMP ? SwingConstants.LEFT : SwingConstants.RIGHT;
                lbl.setHorizontalAlignment(align);
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                // Highlight Bonus column for editable rows
                if (!done && col == COL_BONUS && !sel) {
                    lbl.setBackground(new Color(255, 253, 240));
                    lbl.setBorder(new CompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                            new CompoundBorder(new LineBorder(new Color(253, 224, 132), 1),
                                BorderFactory.createEmptyBorder(0, 9, 0, 9))));
                }
                return lbl;
            }
        });

        payTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        payTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setBackground(HDR_BG); lbl.setForeground(TEXT_GRAY);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setHorizontalAlignment(col == COL_EMP ? LEFT : RIGHT);
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COL),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                if (col == COL_BONUS) {
                    lbl.setForeground(new Color(146, 64, 14));
                    lbl.setText("BONUS* (edit)");
                }
                return lbl;
            }
        });

        // Bonus column editor — JTextField, right-aligned
        JTextField bonusEditor = new JTextField();
        bonusEditor.setHorizontalAlignment(JTextField.RIGHT);
        bonusEditor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bonusEditor.setBorder(new CompoundBorder(
                new LineBorder(ACCENT, 2), BorderFactory.createEmptyBorder(0, 6, 0, 6)));
        payTable.getColumnModel().getColumn(COL_BONUS)
                .setCellEditor(new DefaultCellEditor(bonusEditor));
    }

    // ── Build month list from Jan 2024 → current month ────────────────
    private String[] buildMonths() {
        String[] names = {"January","February","March","April","May","June",
                          "July","August","September","October","November","December"};
        Calendar cal = Calendar.getInstance();
        int curYear  = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH); // 0-based

        List<String> months = new ArrayList<>();
        for (int y = 2024; y <= curYear; y++) {
            int maxM = (y == curYear) ? curMonth : 11;
            for (int m = 0; m <= maxM; m++) {
                months.add(names[m] + " " + y);
            }
        }
        // Reverse so most recent is first
        java.util.Collections.reverse(months);
        return months.toArray(new String[0]);
    }

    // ── Convert "January 2024" → "2024-01" ────────────────────────────
    private String monthToKey(String display) {
        if (display == null) return "";
        String[] names = {"January","February","March","April","May","June",
                          "July","August","September","October","November","December"};
        String[] parts = display.split(" ");
        if (parts.length < 2) return "";
        String year = parts[1];
        int m = 1;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(parts[0])) { m = i + 1; break; }
        }
        return year + "-" + String.format("%02d", m);
    }

    // ── Number helpers ─────────────────────────────────────────────────
    private static String fmt(double v)   { return String.format("%,.2f", v); }
    private static String pct(double v)   { return String.format("%.0f%%", v * 100); }

    private static double parseD(Object o) {
        if (o == null) return 0;
        try { return Double.parseDouble(o.toString().replace(",", "")); }
        catch (Exception e) { return 0; }
    }

    // ── Rate badge label ───────────────────────────────────────────────
    private JLabel makeRateBadge(String text) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 243, 255));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(NAVY);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(false);
        return lbl;
    }

    // ── Button helpers ─────────────────────────────────────────────────
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

    private JButton makePrimaryBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void goBack() { dispose(); new PayrollHistory(); }
}
