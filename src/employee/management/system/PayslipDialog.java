package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.sql.ResultSet;

/**
 * Professional payslip viewer.
 * Call:  PayslipDialog.show(parentFrame, empid, "2024-03", empName)
 */
public class PayslipDialog {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color NAVY      = new Color( 26,  54, 105);
    private static final Color NAVY_DARK = new Color( 18,  38,  76);
    private static final Color WHITE     = Color.WHITE;
    private static final Color BG        = new Color(244, 246, 250);
    private static final Color BORDER    = new Color(214, 220, 232);
    private static final Color TEXT_DARK = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY = new Color(100, 115, 140);
    private static final Color TEXT_DIM  = new Color(160, 170, 185);
    private static final Color C_GREEN   = new Color( 34, 139,  87);
    private static final Color LINE      = new Color(232, 237, 247);

    // Avatar palette
    private static final Color[] AV = {
        new Color( 99, 102, 241), new Color( 16, 185, 129),
        new Color(245, 158,  11), new Color(239,  68,  68),
        new Color( 59, 130, 246), new Color(168,  85, 247),
        new Color( 20, 184, 166), new Color(249, 115,  22),
    };

    public static void show(Frame parent, String empid, String payMonth, String empNameHint) {
        // ── Load data from DB ──────────────────────────────────────────
        String[]  empInfo   = new String[5]; // name, designation, phone, email, address (not used for payslip)
        double[]  earnings  = new double[4]; // basic, house, medical, bonus
        double[]  deductions = new double[2]; // tax, pf
        String[]  meta      = new String[2]; // status, processed_on
        String    empName   = empNameHint;

        try {
            Conn c = new Conn();

            // Employee details
            ResultSet rs = c.statement.executeQuery(
                "SELECT name, designation, empid FROM employee WHERE empid='" + empid + "'");
            if (rs.next()) {
                empInfo[0] = rs.getString("name");
                empInfo[1] = rs.getString("designation");
                empInfo[2] = rs.getString("empid");
                empName = empInfo[0];
            }

            // Payroll record
            rs = c.statement.executeQuery(
                "SELECT basic_salary, house_allowance, medical_allow, bonus, " +
                "tax_deduction, pf_deduction, net_pay, status, " +
                "DATE_FORMAT(processed_on,'%d %b %Y') AS proc_date " +
                "FROM payroll WHERE empid='" + empid + "' AND pay_month='" + payMonth + "'");
            if (rs.next()) {
                earnings[0]   = rs.getDouble("basic_salary");
                earnings[1]   = rs.getDouble("house_allowance");
                earnings[2]   = rs.getDouble("medical_allow");
                earnings[3]   = rs.getDouble("bonus");
                deductions[0] = rs.getDouble("tax_deduction");
                deductions[1] = rs.getDouble("pf_deduction");
                meta[0]       = rs.getString("status");
                meta[1]       = rs.getString("proc_date");
            } else {
                JOptionPane.showMessageDialog(parent,
                    "No payroll record found for " + empName + " — " + payMonth + ".",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            c.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Error loading payslip: " + ex.getMessage());
            return;
        }

        // Computed totals
        final double gross      = earnings[0] + earnings[1] + earnings[2] + earnings[3];
        final double totalDeduct = deductions[0] + deductions[1];
        final double netPay     = gross - totalDeduct;
        final String finalName  = empName;
        final String[] fi       = empInfo;
        final double[] earn     = earnings;
        final double[] ded      = deductions;
        final String[] mt       = meta;
        final String payMonthFmt = formatMonthDisplay(payMonth);

        // ── Dialog setup ───────────────────────────────────────────────
        JDialog dlg = new JDialog(parent, "Payslip — " + empName + " — " + payMonthFmt, true);
        dlg.setSize(620, 710);
        dlg.setLocationRelativeTo(parent);
        dlg.setLayout(null);
        dlg.setResizable(false);
        dlg.getContentPane().setBackground(BG);

        // ── Slip panel (all painted with Graphics2D) ───────────────────
        JPanel slip = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth();

                // ── White slip card ────────────────────────────────────
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, W, getHeight(), 8, 8));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, W - 1, getHeight() - 1, 8, 8));

                // ── Company header ─────────────────────────────────────
                g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                g2.fill(new RoundRectangle2D.Float(0, 0, W, 80, 8, 8));
                g2.fillRect(0, 40, W, 40); // flatten bottom of rounded corners

                // Company name
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("EMPLOYEE MANAGEMENT SYSTEM", 24, 28);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(180, 200, 230));
                g2.drawString("Monthly Payslip  ·  " + payMonthFmt, 24, 50);
                // Status badge
                String stat = mt[0] != null ? mt[0] : "Draft";
                Color statBg = "Paid".equals(stat) ? new Color(34, 197, 94)
                             : "Processed".equals(stat) ? new Color(59, 130, 246)
                             : new Color(148, 163, 184);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                FontMetrics fm = g2.getFontMetrics();
                int sw = fm.stringWidth(stat) + 14;
                g2.setColor(statBg);
                g2.fill(new RoundRectangle2D.Float(W - sw - 20, 18, sw, 20, 10, 10));
                g2.setColor(WHITE);
                g2.drawString(stat, W - sw - 20 + 7, 32);

                // ── Employee details box ───────────────────────────────
                int ey = 94;
                g2.setColor(new Color(248, 250, 253));
                g2.fill(new RoundRectangle2D.Float(16, ey, W - 32, 70, 8, 8));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(16, ey, W - 33, 69, 8, 8));

                // Avatar circle
                Color ac = AV[Math.abs(finalName.hashCode()) % AV.length];
                g2.setColor(ac);
                g2.fillOval(28, ey + 13, 44, 44);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                FontMetrics fm2 = g2.getFontMetrics();
                String init = finalName.isEmpty() ? "?" : String.valueOf(finalName.charAt(0)).toUpperCase();
                g2.drawString(init, 50 - fm2.stringWidth(init)/2, ey + 43);

                // Name + designation
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString(fi[0] != null ? fi[0] : finalName, 84, ey + 26);
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString(fi[1] != null ? fi[1] : "", 84, ey + 44);

                // Emp ID + processed date (right side)
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Emp ID: " + (fi[2] != null ? fi[2] : empid), W/2 + 20, ey + 26);
                g2.drawString("Pay Month: " + payMonthFmt, W/2 + 20, ey + 44);
                if (mt[1] != null && !mt[1].isEmpty())
                    g2.drawString("Processed: " + mt[1], W/2 + 20, ey + 62);

                int curY = ey + 86;

                // ── Earnings section ───────────────────────────────────
                curY = drawSection(g2, "EARNINGS", W, curY);
                String[][] earnRows = {
                    {"Basic Salary",       fmt(earn[0])},
                    {"House Allowance",    fmt(earn[1])},
                    {"Medical Allowance",  fmt(earn[2])},
                    {"Festival Bonus",     fmt(earn[3])},
                };
                for (String[] row : earnRows) curY = drawRow(g2, row[0], row[1], W, curY, false);
                g2.setColor(LINE); g2.fillRect(24, curY, W - 48, 1); curY += 6;
                curY = drawRow(g2, "GROSS PAY", fmt(gross), W, curY, true);
                curY += 8;

                // ── Deductions section ─────────────────────────────────
                curY = drawSection(g2, "DEDUCTIONS", W, curY);
                String[][] dedRows = {
                    {"Income Tax",        fmt(ded[0])},
                    {"Provident Fund",    fmt(ded[1])},
                };
                for (String[] row : dedRows) curY = drawRow(g2, row[0], row[1], W, curY, false);
                g2.setColor(LINE); g2.fillRect(24, curY, W - 48, 1); curY += 6;
                curY = drawRow(g2, "TOTAL DEDUCTIONS", fmt(totalDeduct), W, curY, true);
                curY += 12;

                // ── Net pay highlighted box ────────────────────────────
                g2.setColor(new Color(220, 252, 231));
                g2.fill(new RoundRectangle2D.Float(16, curY, W - 32, 52, 8, 8));
                g2.setColor(new Color(34, 197, 94, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(16, curY, W - 33, 51, 8, 8));
                g2.setColor(C_GREEN);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.drawString("NET PAY", 30, curY + 22);
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Take-home salary after all deductions", 30, curY + 40);
                g2.setColor(new Color(22, 100, 60));
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                String netStr = "BDT " + fmt(netPay);
                FontMetrics fmNet = g2.getFontMetrics();
                g2.drawString(netStr, W - 24 - fmNet.stringWidth(netStr), curY + 34);
            }

            // ── Helper: draws a labelled section header ────────────────
            private int drawSection(Graphics2D g2, String title, int W, int y) {
                g2.setColor(new Color(248, 250, 253));
                g2.fillRect(16, y, W - 32, 26);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(16, y, W - 33, 25);
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString(title, 28, y + 17);
                return y + 32;
            }

            // ── Helper: draws one label/value row ──────────────────────
            private int drawRow(Graphics2D g2, String label, String value, int W, int y, boolean bold) {
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, 12));
                g2.drawString(label, 28, y + 16);
                g2.setColor(bold ? TEXT_DARK : TEXT_GRAY);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("BDT " + value, W - 24 - fm.stringWidth("BDT " + value), y + 16);
                if (!bold) {
                    g2.setColor(LINE); g2.fillRect(24, y + 22, W - 48, 1);
                }
                return y + (bold ? 24 : 26);
            }

            // ── fmt helper accessible inside anonymous class ───────────
            private String fmt(double v) { return String.format("%,.2f", v); }
        };
        slip.setOpaque(false);
        slip.setBounds(10, 10, 600, 615);
        dlg.add(slip);

        // ── Print button ───────────────────────────────────────────────
        JButton printBtn = new JButton("🖨  Print Payslip") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(22, 108, 67)
                          : getModel().isRollover() ? new Color(22, 108, 67)
                          : C_GREEN);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), getWidth()/2 - fm.stringWidth(getText())/2,
                        getHeight()/2 + fm.getAscent()/2 - 2);
            }
        };
        printBtn.setBounds(10, 634, 180, 36);
        printBtn.setContentAreaFilled(false);
        printBtn.setFocusPainted(false);
        printBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printBtn.setBorderPainted(false);
        printBtn.setForeground(WHITE);
        printBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        printBtn.addActionListener(e -> {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setJobName("Payslip_" + empid + "_" + payMonth);
            pj.setPrintable((g, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                double sx = pageFormat.getImageableWidth()  / slip.getWidth();
                double sy = pageFormat.getImageableHeight() / slip.getHeight();
                double scale = Math.min(sx, sy);
                g2d.scale(scale, scale);
                slip.printAll(g2d);
                return Printable.PAGE_EXISTS;
            });
            if (pj.printDialog()) {
                try { pj.print(); }
                catch (PrinterException pe) {
                    JOptionPane.showMessageDialog(dlg, "Print failed: " + pe.getMessage());
                }
            }
        });
        dlg.add(printBtn);

        JButton closeBtn = new JButton("Close") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(22, 36, 71) : new Color(71, 85, 105));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), getWidth()/2 - fm.stringWidth(getText())/2,
                        getHeight()/2 + fm.getAscent()/2 - 2);
            }
        };
        closeBtn.setBounds(450, 634, 160, 36);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setBorderPainted(false);
        closeBtn.setForeground(WHITE);
        closeBtn.addActionListener(e -> dlg.dispose());
        dlg.add(closeBtn);

        dlg.setVisible(true);
    }

    // ── Helpers ────────────────────────────────────────────────────────
    private static String formatMonthDisplay(String ym) {
        if (ym == null || ym.length() < 7) return ym;
        String[] names = {"January","February","March","April","May","June",
                          "July","August","September","October","November","December"};
        int m = Integer.parseInt(ym.substring(5, 7));
        return names[m - 1] + " " + ym.substring(0, 4);
    }
}
