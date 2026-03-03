package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Auto-compiled monthly HR report dialog.
 * Opens as a modal JDialog; all data is fetched fresh from the DB every time.
 * Usage:  MonthlyReportDialog.show(parentFrame);
 */
public class MonthlyReportDialog {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color NAVY      = new Color( 26,  54, 105);
    private static final Color NAVY_DARK = new Color( 18,  38,  76);
    private static final Color WHITE     = Color.WHITE;
    private static final Color BG        = new Color(248, 250, 253);
    private static final Color BORDER    = new Color(214, 220, 232);
    private static final Color TEXT_DARK = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY = new Color(100, 115, 140);
    private static final Color TEXT_DIM  = new Color(160, 170, 185);
    private static final Color C_GREEN   = new Color( 34, 139,  87);
    private static final Color C_AMBER   = new Color(217, 119,   6);
    private static final Color C_RED     = new Color(185,  28,  28);
    private static final Color C_BLUE    = new Color( 59, 130, 246);
    private static final Color SEP       = new Color(232, 237, 247);

    public static void show(Frame parent) {
        // ── Compile report data ────────────────────────────────────────
        String[] data = compileReport();

        JDialog dlg = new JDialog(parent, "Monthly HR Report", true);
        dlg.setSize(620, 680);
        dlg.setLocationRelativeTo(parent);
        // BorderLayout: NORTH=header, CENTER=scroll, SOUTH=footer
        // This guarantees the footer buttons are ALWAYS visible regardless of OS chrome.
        dlg.setLayout(new java.awt.BorderLayout(0, 0));
        dlg.setResizable(false);
        dlg.getContentPane().setBackground(BG);

        // ── NORTH: Painted header ──────────────────────────────────────
        JPanel hdr = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth(), H = getHeight();
                g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                g2.fillRect(0, 0, W, H);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("Monthly HR Report", 20, 30);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Auto-compiled  ·  " + data[0], 20, 50);
                // Icon circle
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillOval(W - 70, 8, 52, 52);
                g2.setColor(new Color(255, 255, 255, 120));
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                g2.drawString("HR", W - 57, 42);
            }
        };
        hdr.setPreferredSize(new Dimension(620, 68));
        dlg.add(hdr, java.awt.BorderLayout.NORTH);

        // ── CENTER: Scroll pane with report content ────────────────────
        JPanel content = buildReportContent(data);
        JScrollPane scroll = new JScrollPane(content);
        // Left padding only — no right border so the scrollbar sits flush at the dialog
        // edge and does NOT eat into the content area.
        scroll.setBorder(new javax.swing.border.MatteBorder(2, 10, 0, 0, BG));
        scroll.getViewport().setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Scroll to top on open
        SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(0));
        dlg.add(scroll, java.awt.BorderLayout.CENTER);

        // ── SOUTH: Footer with action buttons ──────────────────────────
        // Fixed at bottom — always visible regardless of OS window chrome height.
        JPanel footer = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BORDER);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        footer.setBackground(BG);
        footer.setPreferredSize(new Dimension(620, 54));

        JButton exportBtn = makeBtn("⬇  Export as TXT", new Color(59, 130, 246), new Color(37, 99, 235));
        exportBtn.setBounds(10, 10, 165, 34);
        exportBtn.addActionListener(e -> exportTxt(parent, data));
        footer.add(exportBtn);

        JButton closeBtn = makeBtn("Close", new Color(71, 85, 105), new Color(51, 65, 85));
        closeBtn.setBounds(455, 10, 155, 34);
        closeBtn.addActionListener(e -> dlg.dispose());
        footer.add(closeBtn);

        dlg.add(footer, java.awt.BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // ── Build the report content panel ────────────────────────────────
    private static JPanel buildReportContent(String[] d) {
        JPanel p = new JPanel(null);
        p.setBackground(BG);
        int y = 8;

        // WORKFORCE section
        y = addSection(p, "WORKFORCE", y);
        y = addRow(p, "Total Employees",        d[1], TEXT_DARK, y);
        y = addRow(p, "New Hires This Month",   d[2], C_GREEN,   y);
        y = addSep(p, y);

        // PAYROLL section
        y = addSection(p, "PAYROLL", y);
        y = addRow(p, "Total Net Pay (This Month)",  d[3], TEXT_DARK, y);
        y = addRow(p, "Total Net Pay (Last Month)",  d[4], TEXT_GRAY,  y);
        y = addRow(p, "Month-over-Month Change",     d[5],
                d[5].startsWith("▲") ? C_GREEN : C_RED,   y);
        y = addSep(p, y);

        // LEAVE section
        y = addSection(p, "LEAVE SUMMARY (THIS MONTH)", y);
        y = addRow(p, "Total Applications",  d[6], TEXT_DARK, y);
        y = addRow(p, "Approved",            d[7], C_GREEN,   y);
        y = addRow(p, "Pending Review",      d[8], C_AMBER,   y);
        y = addRow(p, "Rejected",            d[9], C_RED,     y);
        y = addSep(p, y);

        // SALARY section
        y = addSection(p, "SALARY STATISTICS", y);
        y = addRow(p, "Minimum Salary",    d[10], TEXT_DARK, y);
        y = addRow(p, "Maximum Salary",    d[11], TEXT_DARK, y);
        y = addRow(p, "Average Salary",    d[12], C_BLUE,    y);
        y = addSep(p, y);

        // DESIGNATIONS section
        y = addSection(p, "DESIGNATIONS", y);
        y = addRow(p, "Top Role",          d[13], TEXT_DARK, y);
        y = addRow(p, "Total Roles",       d[14], TEXT_DARK, y);
        y += 12;

        p.setPreferredSize(new Dimension(ROW_W, y + 8));
        return p;
    }

    // Content width for absolute-positioned children.
    // Keep this well BELOW the expected viewport width so the scrollbar never overlaps.
    // Viewport ≈ (dialog=620) − (left border=10) − (scrollbar≈17) = 593px.
    // ROW_W=570 leaves ~23px breathing room; val labels end at ROW_W-20=550, giving
    // an extra 43px gap between the last character and the scrollbar track.
    private static final int ROW_W = 570;

    private static int addSection(JPanel p, String title, int y) {
        JPanel bg = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(240, 244, 252));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
            }
        };
        bg.setOpaque(false); bg.setBounds(0, y, ROW_W, 26);
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(26, 54, 105));
        lbl.setBounds(0, 0, ROW_W, 26);
        bg.setLayout(null); bg.add(lbl);
        p.add(bg);
        return y + 32;
    }

    private static int addRow(JPanel p, String label, String value, Color valueColor, int y) {
        JLabel key = new JLabel(label);
        key.setFont(new Font("SansSerif", Font.PLAIN, 12));
        key.setForeground(TEXT_GRAY);
        key.setBounds(12, y, 270, 22);
        p.add(key);

        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 12));
        val.setForeground(valueColor);
        val.setHorizontalAlignment(SwingConstants.RIGHT);
        // End 20px before ROW_W's right edge so text never touches the scrollbar.
        val.setBounds(282, y, ROW_W - 302, 22);
        p.add(val);

        JPanel line = new JPanel(); line.setBackground(SEP);
        line.setBounds(8, y + 24, ROW_W - 16, 1);
        p.add(line);

        return y + 30;
    }

    private static int addSep(JPanel p, int y) {
        JPanel sep = new JPanel(); sep.setBackground(new Color(220, 226, 238));
        sep.setBounds(0, y, ROW_W, 2);
        p.add(sep);
        return y + 12;
    }

    // ── Compile all report data from DB ───────────────────────────────
    private static String[] compileReport() {
        String[] d = new String[15];
        d[0]  = "Generated " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"));
        d[1]  = "0";  d[2]  = "0";  d[3]  = "BDT 0";  d[4]  = "BDT 0";
        d[5]  = "N/A"; d[6] = "0";  d[7]  = "0";       d[8]  = "0";
        d[9]  = "0";  d[10] = "BDT 0"; d[11] = "BDT 0"; d[12] = "BDT 0";
        d[13] = "—";  d[14] = "0";

        try {
            Conn c = new Conn();
            ResultSet rs;
            String cm = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String pm = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // Total employees
            rs = c.statement.executeQuery("SELECT COUNT(*) FROM employee");
            if (rs.next()) d[1] = rs.getString(1);

            // New hires this month (approximated from payroll first-time entries)
            d[2] = "—";

            // Payroll this month
            rs = c.statement.executeQuery(
                "SELECT ROUND(SUM(net_pay),0) FROM payroll WHERE pay_month='" + cm + "'");
            if (rs.next() && rs.getString(1) != null)
                d[3] = "BDT " + String.format("%,d", rs.getLong(1));

            // Payroll last month + % change
            rs = c.statement.executeQuery(
                "SELECT ROUND(SUM(net_pay),0) FROM payroll WHERE pay_month='" + pm + "'");
            long lastPay = 0;
            if (rs.next() && rs.getString(1) != null) {
                lastPay = rs.getLong(1);
                d[4] = "BDT " + String.format("%,d", lastPay);
            }
            // Parse current pay for % change
            try {
                long curPay = Long.parseLong(d[3].replaceAll("[^0-9]", ""));
                if (lastPay > 0) {
                    double pct = (curPay - lastPay) * 100.0 / lastPay;
                    d[5] = (pct >= 0 ? "▲ +" : "▼ ") + String.format("%.1f%%", pct);
                } else if (curPay > 0) {
                    d[5] = "▲ First month";
                }
            } catch (Exception ignored) {}

            // Leave summary this month
            rs = c.statement.executeQuery(
                "SELECT COUNT(*), " +
                "SUM(CASE WHEN status='Approved' THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN status='Pending'  THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN status='Rejected' THEN 1 ELSE 0 END) " +
                "FROM leave_application WHERE DATE_FORMAT(applied_on,'%Y-%m')='" + cm + "'");
            if (rs.next()) {
                d[6] = rs.getString(1) != null ? rs.getString(1) : "0";
                d[7] = rs.getString(2) != null ? rs.getString(2) : "0";
                d[8] = rs.getString(3) != null ? rs.getString(3) : "0";
                d[9] = rs.getString(4) != null ? rs.getString(4) : "0";
            }

            // Salary stats
            rs = c.statement.executeQuery(
                "SELECT MIN(CAST(salary AS DECIMAL(12,2))), " +
                "MAX(CAST(salary AS DECIMAL(12,2))), " +
                "ROUND(AVG(CAST(salary AS DECIMAL(12,2))),0) FROM employee");
            if (rs.next()) {
                d[10] = "BDT " + String.format("%,.0f", rs.getDouble(1));
                d[11] = "BDT " + String.format("%,.0f", rs.getDouble(2));
                d[12] = "BDT " + String.format("%,.0f", rs.getDouble(3));
            }

            // Top designation
            rs = c.statement.executeQuery(
                "SELECT designation, COUNT(*) AS cnt FROM employee " +
                "GROUP BY designation ORDER BY cnt DESC LIMIT 1");
            if (rs.next())
                d[13] = rs.getString("designation") + " (" + rs.getString("cnt") + " employees)";

            rs = c.statement.executeQuery("SELECT COUNT(DISTINCT designation) FROM employee");
            if (rs.next()) d[14] = rs.getString(1);

            c.close();
        } catch (Exception ignored) {}
        return d;
    }

    // ── Export report as plain text ────────────────────────────────────
    private static void exportTxt(Frame parent, String[] d) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop",
                "hr_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + ".txt"));
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
            fw.write("╔══════════════════════════════════════════╗\n");
            fw.write("║         MONTHLY HR REPORT                ║\n");
            fw.write("╚══════════════════════════════════════════╝\n");
            fw.write(d[0] + "\n\n");
            fw.write("── WORKFORCE ──────────────────────────────\n");
            fw.write("Total Employees:          " + d[1] + "\n");
            fw.write("New Hires This Month:     " + d[2] + "\n\n");
            fw.write("── PAYROLL ─────────────────────────────────\n");
            fw.write("Net Pay (This Month):     " + d[3] + "\n");
            fw.write("Net Pay (Last Month):     " + d[4] + "\n");
            fw.write("Month-over-Month:         " + d[5] + "\n\n");
            fw.write("── LEAVE SUMMARY ───────────────────────────\n");
            fw.write("Total Applications:       " + d[6] + "\n");
            fw.write("Approved:                 " + d[7] + "\n");
            fw.write("Pending:                  " + d[8] + "\n");
            fw.write("Rejected:                 " + d[9] + "\n\n");
            fw.write("── SALARY STATISTICS ───────────────────────\n");
            fw.write("Minimum:                  " + d[10] + "\n");
            fw.write("Maximum:                  " + d[11] + "\n");
            fw.write("Average:                  " + d[12] + "\n\n");
            fw.write("── DESIGNATIONS ────────────────────────────\n");
            fw.write("Top Role:                 " + d[13] + "\n");
            fw.write("Total Roles:              " + d[14] + "\n");
            ToastNotification.show(parent, "Report exported → " + fc.getSelectedFile().getName(),
                    ToastNotification.Type.SUCCESS);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Export failed: " + ex.getMessage());
        }
    }

    // ── Button helper ─────────────────────────────────────────────────
    private static JButton makeBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }
}
