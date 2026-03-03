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

/**
 * Shows a modal dialog with full leave history for one employee.
 * Call LeaveHistory.show(parentFrame, empid, empName) from anywhere.
 */
public class LeaveHistory {

    private static final Color NAVY        = new Color( 26,  54, 105);
    private static final Color NAVY_DARK   = new Color( 18,  38,  76);
    private static final Color WHITE       = Color.WHITE;
    private static final Color BG          = new Color(248, 250, 253);
    private static final Color BORDER_COL  = new Color(214, 220, 232);
    private static final Color TEXT_DARK   = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY   = new Color(100, 115, 140);
    private static final Color C_GREEN     = new Color( 34, 139,  87);
    private static final Color C_AMBER     = new Color(217, 119,   6);
    private static final Color C_RED       = new Color(185,  28,  28);
    private static final Color ROW_ALT     = new Color(250, 251, 254);
    private static final Color ROW_SEL     = new Color(219, 234, 254);
    private static final Color HDR_BG      = new Color(248, 250, 253);

    private static final Color PENDING_BG  = new Color(255, 247, 230);
    private static final Color PENDING_FG  = new Color(146,  64,  14);
    private static final Color APPROVED_BG = new Color(220, 252, 231);
    private static final Color APPROVED_FG = new Color( 21, 128,  61);
    private static final Color REJECTED_BG = new Color(254, 226, 226);
    private static final Color REJECTED_FG = new Color(153,  27,  27);

    private static final Color[] AV = {
        new Color( 99, 102, 241), new Color( 16, 185, 129),
        new Color(245, 158,  11), new Color(239,  68,  68),
        new Color( 59, 130, 246), new Color(168,  85, 247),
        new Color( 20, 184, 166), new Color(249, 115,  22),
    };

    /** Opens the leave history dialog for the given employee. */
    public static void show(Frame parent, String empid, String empName) {
        JDialog dlg = new JDialog(parent, "Leave History — " + empName, true);
        dlg.setSize(820, 560);
        dlg.setLocationRelativeTo(parent);
        dlg.setLayout(null);
        dlg.setResizable(false);

        // ── Painted background ────────────────────────────────────────
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int W = getWidth(), H = getHeight();

                g2.setColor(BG); g2.fillRect(0, 0, W, H);

                // Navy header
                g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                g2.fillRect(0, 0, W, 62);

                // Avatar circle
                Color ac = AV[Math.abs(empName.hashCode()) % AV.length];
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(16, 10, 44, 44);
                g2.setColor(ac);
                g2.fillOval(18, 12, 40, 40);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                String init = empName.isEmpty() ? "?" : String.valueOf(empName.charAt(0)).toUpperCase();
                g2.drawString(init, 38 - fm.stringWidth(init)/2, 40);

                // Name + ID
                g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                g2.setColor(WHITE);
                g2.drawString(empName, 72, 28);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(180, 200, 230));
                g2.drawString("Employee ID: " + empid + "  ·  Leave History", 72, 48);

                // Balance section label
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("LEAVE BALANCE (CURRENT YEAR)", 16, 82);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(16, 88, W - 16, 88);

                // History section label
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("APPLICATION HISTORY", 16, 158);
                g2.drawLine(16, 164, W - 16, 164);
            }
        };
        bg.setBounds(0, 0, 820, 560);
        bg.setLayout(null);
        dlg.setContentPane(bg);

        // ── Balance mini-cards (y=92, h=56) ──────────────────────────
        // Load leave types & usage for this employee
        List<String>  typeNames = new ArrayList<>();
        List<Integer> maxDays   = new ArrayList<>();
        List<Integer> usedDays  = new ArrayList<>();
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT lt.id, lt.name, lt.max_days,"
                + " COALESCE(SUM(CASE WHEN la.status='Approved' AND YEAR(la.from_date)=YEAR(CURDATE())"
                + " THEN la.days ELSE 0 END), 0) AS used"
                + " FROM leave_type lt"
                + " LEFT JOIN leave_application la ON lt.id=la.leave_type AND la.empid='" + empid + "'"
                + " GROUP BY lt.id, lt.name, lt.max_days ORDER BY lt.id");
            while (rs.next()) {
                typeNames.add(rs.getString("name"));
                maxDays.add(rs.getInt("max_days"));
                usedDays.add(rs.getInt("used"));
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }

        // Paint balance cards inline — horizontal row
        int numCards = typeNames.size();
        if (numCards > 0) {
            int cw = Math.min(150, (820 - 32) / numCards);
            int gap = ((820 - 32) - numCards * cw) / Math.max(1, numCards - 1);
            for (int i = 0; i < numCards; i++) {
                final int idx = i;
                final String tname = typeNames.get(i);
                final int max  = maxDays.get(i);
                final int used = usedDays.get(i);
                final int rem  = max - used;

                JPanel card = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                        int W = getWidth(), H = getHeight();
                        g2.setColor(WHITE);
                        g2.fill(new RoundRectangle2D.Float(0, 0, W, H, 8, 8));
                        g2.setColor(BORDER_COL);
                        g2.setStroke(new BasicStroke(1f));
                        g2.draw(new RoundRectangle2D.Float(0, 0, W - 1, H - 1, 8, 8));

                        // Type name
                        g2.setColor(TEXT_GRAY);
                        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                        String shortName = tname.length() > 12 ? tname.substring(0, 11) + "…" : tname;
                        g2.drawString(shortName.toUpperCase(), 8, 14);

                        // Remaining days (big)
                        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                        g2.setColor(rem <= 0 ? C_RED : (rem <= 3 ? C_AMBER : C_GREEN));
                        g2.drawString(String.valueOf(rem), 8, 38);

                        // "/" max
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                        g2.setColor(TEXT_GRAY);
                        FontMetrics fm = g2.getFontMetrics();
                        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                        int remW = g2.getFontMetrics().stringWidth(String.valueOf(rem));
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                        g2.drawString("/ " + max, 8 + remW + 2, 38);

                        // "remaining" label
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                        g2.setColor(TEXT_GRAY);
                        g2.drawString("remaining", 8, 52);
                    }
                };
                int cx = 16 + i * (cw + gap);
                card.setOpaque(false);
                card.setBounds(cx, 92, cw, 58);
                bg.add(card);
            }
        }

        // ── History table (y=168) ─────────────────────────────────────
        String[] cols = {"Leave Type", "From", "To", "Days", "Status", "Applied On", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT lt.name AS ltype, la.from_date, la.to_date, la.days,"
                + " la.status, la.applied_on, la.remarks"
                + " FROM leave_application la"
                + " JOIN leave_type lt ON la.leave_type=lt.id"
                + " WHERE la.empid='" + empid + "'"
                + " ORDER BY la.applied_on DESC");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("ltype"),
                    rs.getString("from_date"),
                    rs.getString("to_date"),
                    rs.getString("days"),
                    rs.getString("status"),
                    rs.getString("applied_on") != null
                        ? rs.getString("applied_on").substring(0, 16) : "",
                    rs.getString("remarks") != null ? rs.getString("remarks") : "—"
                });
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }

        JTable histTable = new JTable(model);
        histTable.setRowHeight(38);
        histTable.setShowGrid(false);
        histTable.setIntercellSpacing(new Dimension(0, 0));
        histTable.setBackground(WHITE);
        histTable.setSelectionBackground(ROW_SEL);
        histTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        histTable.getTableHeader().setReorderingAllowed(false);

        // Col widths: 145+90+90+55+100+130+196 = 806 (scroll=788)
        int[] cw = {145, 90, 90, 55, 100, 130, 196};
        for (int i = 0; i < cw.length; i++)
            histTable.getColumnModel().getColumn(i).setPreferredWidth(cw[i]);

        // Default cell renderer
        histTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
                lbl.setForeground(TEXT_GRAY);
                lbl.setBackground(sel ? ROW_SEL : (row % 2 == 0 ? WHITE : ROW_ALT));
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return lbl;
            }
        });

        // Header renderer
        histTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        histTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setBackground(HDR_BG);
                lbl.setForeground(TEXT_GRAY);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COL),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return lbl;
            }
        });

        // Status badge renderer for col 4
        histTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                String status = value != null ? value.toString() : "";
                Color bg, fg;
                switch (status) {
                    case "Approved": bg = sel ? ROW_SEL : APPROVED_BG; fg = APPROVED_FG; break;
                    case "Rejected": bg = sel ? ROW_SEL : REJECTED_BG; fg = REJECTED_FG; break;
                    default:         bg = sel ? ROW_SEL : PENDING_BG;  fg = PENDING_FG;  break;
                }
                lbl.setBackground(bg); lbl.setForeground(fg);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                        BorderFactory.createEmptyBorder(0, 4, 0, 4)));
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(histTable);
        scroll.setBounds(0, 168, 820, 344);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COL));
        scroll.getViewport().setBackground(WHITE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bg.add(scroll);

        // ── Close button ──────────────────────────────────────────────
        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(340, 520, 140, 30);
        closeBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        closeBtn.setForeground(TEXT_GRAY);
        closeBtn.setBackground(WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new LineBorder(BORDER_COL, 1));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dlg.dispose());
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(NAVY); closeBtn.setBorder(new LineBorder(NAVY, 1)); }
            public void mouseExited (MouseEvent e) { closeBtn.setForeground(TEXT_GRAY); closeBtn.setBorder(new LineBorder(BORDER_COL, 1)); }
        });
        bg.add(closeBtn);

        dlg.setVisible(true);
    }
}
