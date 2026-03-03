package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;

/**
 * Performance Analytics Dashboard — 5-tab view built entirely with Graphics2D charts.
 * Tabs: Overview | Monthly Payroll | Leave Trends | Salary Distribution | Designations
 */
public class PerformanceDashboard extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color NAVY      = new Color( 26,  54, 105);
    private static final Color NAVY_DARK = new Color( 18,  38,  76);
    private static final Color ACCENT    = new Color( 66, 153, 225);
    private static final Color WHITE     = Color.WHITE;
    private static final Color BG        = new Color(244, 246, 250);
    private static final Color BORDER    = new Color(214, 220, 232);
    private static final Color TEXT_DARK = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY = new Color(100, 115, 140);
    private static final Color TEXT_DIM  = new Color(160, 170, 185);
    private static final Color TAB_BG    = new Color(240, 243, 250);

    // Chart accent colors
    private static final Color C_BLUE   = new Color( 66, 153, 225);
    private static final Color C_GREEN  = new Color( 34, 139,  87);
    private static final Color C_AMBER  = new Color(217, 119,   6);
    private static final Color C_RED    = new Color(185,  28,  28);
    private static final Color C_PURPLE = new Color(124,  58, 237);
    private static final Color C_TEAL   = new Color( 20, 184, 166);

    // ── Tab management ─────────────────────────────────────────────────
    private static final String[] TAB_NAMES = {
        "Overview", "Payroll Cost", "Leave Trends", "Salary Dist.", "Designations"
    };
    private final JButton[] tabBtns = new JButton[TAB_NAMES.length];
    private final JPanel[]  tabPanels = new JPanel[TAB_NAMES.length];
    private int activeTab = 0;

    PerformanceDashboard() {
        setTitle("Performance Analytics — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); new Main_class(); }
        });

        // ── Painted background ─────────────────────────────────────────
        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth(), H = getHeight();

                g2.setColor(BG); g2.fillRect(0, 0, W, H);

                // Navy header
                g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                g2.fillRect(0, 0, W, 68);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.drawString("Performance Analytics", 24, 30);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(180, 200, 230));
                g2.drawString("Visual insights: payroll costs, leave trends, salary distribution & headcount", 24, 50);

                // Tab bar background
                g2.setColor(WHITE);
                g2.fillRect(0, 68, W, 44);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 68, W, 68);
                g2.drawLine(0, 112, W, 112);

                // Footer
                g2.setColor(new Color(237, 240, 247));
                g2.fillRect(0, H - 38, W, 38);
                g2.setColor(BORDER); g2.drawLine(0, H - 38, W, H - 38);
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                FontMetrics fm = g2.getFontMetrics();
                String copy = "© 2025 Employee Management System  ·  v1.0.0";
                g2.drawString(copy, W/2 - fm.stringWidth(copy)/2, H - 14);
            }
        };
        bg.setBounds(0, 0, 1100, 700);
        bg.setLayout(null);
        add(bg);

        // ── Header back button ─────────────────────────────────────────
        JButton backBtn = makeGhostBtn("← Dashboard");
        backBtn.setBounds(968, 18, 110, 30);
        backBtn.addActionListener(e -> { dispose(); new Main_class(); });
        bg.add(backBtn);

        // ── Tab buttons (y=72) ─────────────────────────────────────────
        int tx = 16;
        for (int i = 0; i < TAB_NAMES.length; i++) {
            final int idx = i;
            JButton tb = makeTabBtn(TAB_NAMES[i]);
            tb.setBounds(tx, 76, 158, 32);
            tb.addActionListener(e -> switchTab(idx));
            bg.add(tb);
            tabBtns[i] = tb;
            tx += 168;
        }
        styleActiveTab(0);

        // ── Content panels (all at y=112, h=550) ──────────────────────
        for (int i = 0; i < TAB_NAMES.length; i++) {
            JPanel p = new JPanel(null);
            p.setBounds(0, 112, 1100, 550);
            p.setBackground(BG);
            p.setVisible(i == 0);
            bg.add(p);
            tabPanels[i] = p;
        }

        // ── Load & build charts ────────────────────────────────────────
        buildAllTabs();

        setVisible(true);
    }

    // ── Switch active tab ──────────────────────────────────────────────
    private void switchTab(int idx) {
        for (JPanel p : tabPanels) p.setVisible(false);
        tabPanels[idx].setVisible(true);
        activeTab = idx;
        styleActiveTab(idx);
    }

    private void styleActiveTab(int active) {
        for (int i = 0; i < tabBtns.length; i++) {
            JButton b = tabBtns[i];
            if (i == active) {
                b.setBackground(NAVY);
                b.setForeground(WHITE);
                b.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            } else {
                b.setBackground(WHITE);
                b.setForeground(TEXT_GRAY);
                b.setBorder(new LineBorder(BORDER, 1));
            }
        }
    }

    // ── Build all tab content ──────────────────────────────────────────
    private void buildAllTabs() {
        buildOverviewTab();
        buildPayrollTab();
        buildLeaveTab();
        buildSalaryTab();
        buildDesigTab();
    }

    // ── Tab 0: Overview (2×2 mini charts) ─────────────────────────────
    private void buildOverviewTab() {
        JPanel p = tabPanels[0];

        // Section title strip
        addSectionTitle(p, "Performance Overview  —  Key HR Metrics at a Glance", 16, 10, 700, 24);

        ChartData payroll = loadPayrollData(12);
        ChartData leave   = loadLeaveData(12);
        ChartData salary  = loadSalaryDistribution();
        ChartData desig   = loadDesignationData();

        // Top-left: Payroll Cost (line)
        LineChartPanel lc = new LineChartPanel(
                payroll != null ? payroll :
                new ChartData(new String[]{}, new double[]{}, "Monthly Payroll Cost (BDT)", null, C_BLUE));
        lc.setBounds(8, 40, 530, 244);
        styleMiniChart(lc); p.add(lc);

        // Top-right: Leave Trends (grouped bar)
        BarChartPanel lt = new BarChartPanel(
                leave != null ? leave :
                new ChartData(new String[]{}, new double[]{}, new double[]{},
                              "Leave Trends", "Approved", "Rejected", C_GREEN, C_RED),
                BarChartPanel.Mode.GROUPED);
        lt.setBounds(546, 40, 542, 244);
        styleMiniChart(lt); p.add(lt);

        // Bottom-left: Salary Distribution (horizontal bar)
        BarChartPanel sd = new BarChartPanel(
                salary != null ? salary :
                new ChartData(new String[]{}, new double[]{}, "Salary Distribution", null, C_PURPLE),
                BarChartPanel.Mode.HORIZONTAL);
        sd.setBounds(8, 296, 530, 244);
        styleMiniChart(sd); p.add(sd);

        // Bottom-right: Designations (horizontal bar)
        BarChartPanel dg = new BarChartPanel(
                desig != null ? desig :
                new ChartData(new String[]{}, new double[]{}, "Headcount by Designation", null, C_TEAL),
                BarChartPanel.Mode.HORIZONTAL);
        dg.setBounds(546, 296, 542, 244);
        styleMiniChart(dg); p.add(dg);
    }

    // ── Tab 1: Payroll Cost (full line chart) ──────────────────────────
    private void buildPayrollTab() {
        JPanel p = tabPanels[1];
        addSectionTitle(p, "Monthly Payroll Cost (BDT Net Pay)", 16, 8, 600, 24);

        // Period selector
        JComboBox<String> period = makePeriodCombo();
        period.setBounds(888, 8, 190, 26);
        p.add(period);

        ChartData data = loadPayrollData(12);
        LineChartPanel chart = new LineChartPanel(
                data != null ? data :
                new ChartData(new String[]{}, new double[]{}, "Monthly Payroll Cost (BDT)", "Net Pay", C_BLUE));
        chart.setBounds(8, 42, 1080, 490);
        chart.setBackground(WHITE);
        chart.setBorder(new LineBorder(BORDER, 1));
        p.add(chart);

        period.addActionListener(e -> {
            int months = period.getSelectedIndex() == 0 ? 12 : period.getSelectedIndex() == 1 ? 24 : 36;
            ChartData d = loadPayrollData(months);
            chart.setData(d != null ? d : new ChartData(new String[]{}, new double[]{},
                    "Monthly Payroll Cost (BDT)", "Net Pay", C_BLUE));
        });
    }

    // ── Tab 2: Leave Trends ────────────────────────────────────────────
    private void buildLeaveTab() {
        JPanel p = tabPanels[2];
        addSectionTitle(p, "Monthly Leave Trends — Approved vs Rejected", 16, 8, 700, 24);

        ChartData data = loadLeaveData(12);
        BarChartPanel chart = new BarChartPanel(
                data != null ? data :
                new ChartData(new String[]{}, new double[]{}, new double[]{},
                              "Leave Trends", "Approved", "Rejected", C_GREEN, C_RED),
                BarChartPanel.Mode.GROUPED);
        chart.setBounds(8, 42, 1080, 490);
        chart.setBackground(WHITE);
        chart.setBorder(new LineBorder(BORDER, 1));
        p.add(chart);
    }

    // ── Tab 3: Salary Distribution ─────────────────────────────────────
    private void buildSalaryTab() {
        JPanel p = tabPanels[3];
        addSectionTitle(p, "Salary Distribution — Employee Count per Range", 16, 8, 700, 24);

        ChartData data = loadSalaryDistribution();
        BarChartPanel chart = new BarChartPanel(
                data != null ? data :
                new ChartData(new String[]{}, new double[]{}, "Salary Distribution", "Employees", C_PURPLE),
                BarChartPanel.Mode.HORIZONTAL);
        chart.setBounds(8, 42, 1080, 490);
        chart.setBackground(WHITE);
        chart.setBorder(new LineBorder(BORDER, 1));
        p.add(chart);
    }

    // ── Tab 4: Designation Breakdown ───────────────────────────────────
    private void buildDesigTab() {
        JPanel p = tabPanels[4];
        addSectionTitle(p, "Headcount by Designation — Employee Distribution across Roles", 16, 8, 800, 24);

        ChartData data = loadDesignationData();
        BarChartPanel chart = new BarChartPanel(
                data != null ? data :
                new ChartData(new String[]{}, new double[]{}, "Headcount by Designation", "Count", C_TEAL),
                BarChartPanel.Mode.HORIZONTAL);
        chart.setBounds(8, 42, 1080, 490);
        chart.setBackground(WHITE);
        chart.setBorder(new LineBorder(BORDER, 1));
        p.add(chart);
    }

    // ── Data loaders ───────────────────────────────────────────────────
    private ChartData loadPayrollData(int months) {
        List<String> lbls = new ArrayList<>();
        List<Double>  vals = new ArrayList<>();
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT pay_month, ROUND(SUM(net_pay),0) AS total " +
                "FROM payroll GROUP BY pay_month ORDER BY pay_month DESC LIMIT " + months);
            while (rs.next()) {
                lbls.add(0, shortMonth(rs.getString("pay_month")));
                vals.add(0, rs.getDouble("total"));
            }
            c.close();
        } catch (Exception ignored) {}
        if (lbls.isEmpty()) return null;
        return new ChartData(
            lbls.toArray(new String[0]),
            toDoubleArray(vals),
            "Monthly Payroll Cost (BDT)", "Net Pay", C_BLUE);
    }

    private ChartData loadLeaveData(int months) {
        List<String> lbls = new ArrayList<>();
        List<Double>  app = new ArrayList<>(), rej = new ArrayList<>();
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT DATE_FORMAT(applied_on,'%Y-%m') AS month, " +
                "SUM(CASE WHEN status='Approved' THEN 1 ELSE 0 END) AS approved, " +
                "SUM(CASE WHEN status='Rejected' THEN 1 ELSE 0 END) AS rejected " +
                "FROM leave_application GROUP BY month ORDER BY month DESC LIMIT " + months);
            while (rs.next()) {
                lbls.add(0, shortMonth(rs.getString("month")));
                app.add(0, rs.getDouble("approved"));
                rej.add(0, rs.getDouble("rejected"));
            }
            c.close();
        } catch (Exception ignored) {}
        if (lbls.isEmpty()) return null;
        return new ChartData(
            lbls.toArray(new String[0]),
            toDoubleArray(app), toDoubleArray(rej),
            "Monthly Leave Trends", "Approved", "Rejected",
            C_GREEN, C_RED);
    }

    private ChartData loadSalaryDistribution() {
        List<String> lbls = new ArrayList<>();
        List<Double>  vals = new ArrayList<>();
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT CASE " +
                "  WHEN CAST(salary AS UNSIGNED) < 15000 THEN '< 15K' " +
                "  WHEN CAST(salary AS UNSIGNED) < 25000 THEN '15-25K' " +
                "  WHEN CAST(salary AS UNSIGNED) < 40000 THEN '25-40K' " +
                "  ELSE '40K+' END AS bucket, COUNT(*) AS cnt " +
                "FROM employee GROUP BY bucket ORDER BY MIN(CAST(salary AS UNSIGNED))");
            while (rs.next()) {
                lbls.add(rs.getString("bucket"));
                vals.add(rs.getDouble("cnt"));
            }
            c.close();
        } catch (Exception ignored) {}
        if (lbls.isEmpty()) return null;
        return new ChartData(
            lbls.toArray(new String[0]),
            toDoubleArray(vals),
            "Salary Distribution", "Employees", C_PURPLE);
    }

    private ChartData loadDesignationData() {
        List<String> lbls = new ArrayList<>();
        List<Double>  vals = new ArrayList<>();
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT designation, COUNT(*) AS cnt FROM employee " +
                "GROUP BY designation ORDER BY cnt DESC LIMIT 10");
            while (rs.next()) {
                lbls.add(rs.getString("designation"));
                vals.add(rs.getDouble("cnt"));
            }
            c.close();
        } catch (Exception ignored) {}
        if (lbls.isEmpty()) return null;
        return new ChartData(
            lbls.toArray(new String[0]),
            toDoubleArray(vals),
            "Headcount by Designation", "Employees", C_TEAL);
    }

    // ── Helpers ────────────────────────────────────────────────────────
    private static String shortMonth(String ym) {
        if (ym == null || ym.length() < 7) return ym != null ? ym : "";
        String[] names = {"Jan","Feb","Mar","Apr","May","Jun",
                          "Jul","Aug","Sep","Oct","Nov","Dec"};
        int m = Integer.parseInt(ym.substring(5, 7));
        return names[m-1] + " '" + ym.substring(2, 4);
    }

    private static double[] toDoubleArray(List<Double> list) {
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }

    private void addSectionTitle(JPanel p, String text, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        lbl.setBounds(x, y, w, h);
        p.add(lbl);
    }

    private void styleMiniChart(JComponent c) {
        c.setBackground(WHITE);
        c.setBorder(new LineBorder(BORDER, 1));
    }

    private JComboBox<String> makePeriodCombo() {
        JComboBox<String> c = new JComboBox<>(
            new String[]{"Last 12 Months", "Last 24 Months", "Last 36 Months"});
        c.setFont(new Font("SansSerif", Font.PLAIN, 11));
        c.setBackground(WHITE);
        c.setFocusable(false);
        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.setBorder(new LineBorder(BORDER, 1));
        return c;
    }

    private JButton makeTabBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(WHITE);
        btn.setForeground(TEXT_GRAY);
        return btn;
    }

    private JButton makeGhostBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBorder(new LineBorder(WHITE, 1)); }
            public void mouseExited (MouseEvent e) { btn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        return btn;
    }
}
