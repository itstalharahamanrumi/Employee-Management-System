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

public class LeaveManagement extends JFrame {

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
    private static final Color C_RED      = new Color(185,  28,  28);
    private static final Color DANGER     = new Color(185,  28,  28);
    private static final Color ROW_ALT    = new Color(250, 251, 254);
    private static final Color ROW_SEL    = new Color(219, 234, 254);
    private static final Color HDR_BG     = new Color(248, 250, 253);

    // Status badge colors
    private static final Color PENDING_BG  = new Color(255, 247, 230);
    private static final Color PENDING_FG  = new Color(146,  64,  14);
    private static final Color APPROVED_BG = new Color(220, 252, 231);
    private static final Color APPROVED_FG = new Color( 21, 128,  61);
    private static final Color REJECTED_BG = new Color(254, 226, 226);
    private static final Color REJECTED_FG = new Color(153,  27,  27);

    // Avatar palette
    private static final Color[] AV = {
        new Color( 99, 102, 241), new Color( 16, 185, 129),
        new Color(245, 158,  11), new Color(239,  68,  68),
        new Color( 59, 130, 246), new Color(168,  85, 247),
        new Color( 20, 184, 166), new Color(249, 115,  22),
    };

    // ── State ──────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable leaveTable;
    private JComboBox<String> filterStatus;
    private JComboBox<String> filterType;
    private JLabel countLabel;

    // Per-row data (parallel to tableModel rows)
    private final List<Integer> leaveIds    = new ArrayList<>();
    private final List<String>  leaveEmpIds = new ArrayList<>();
    private final List<String>  leaveNames  = new ArrayList<>();
    private final List<String>  leaveStatuses = new ArrayList<>();

    LeaveManagement() {
        setTitle("Leave Management — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); new Main_class(); }
        });

        int[] stats = loadStats();

        // ── Background panel ──────────────────────────────────────────
        // Layout:
        //   [0  – 68 ] Navy header
        //   [76 – 170] 3 stat cards
        //   [178– 226] Toolbar strip
        //   [230– 624] Table scroll
        //   [648– 700] Footer
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
                g2.fillRect(0, 0, W, 68);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                g2.drawString("Leave Management", 24, 30);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Review, approve and reject employee leave requests", 24, 50);

                // White toolbar strip
                g2.setColor(WHITE);
                g2.fillRect(0, 178, W, 48);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, 178, W, 178);
                g2.drawLine(0, 226, W, 226);

                // Footer
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

        // ── Header buttons — right side of navy bar (never overlap title) ──
        // "+ New Leave" quick shortcut (rightmost)
        JButton hdrNewLeave = makePrimaryBtn("+ New Leave", C_GREEN, C_GREEN_H);
        hdrNewLeave.setBounds(930, 18, 130, 30);
        hdrNewLeave.addActionListener(e -> { dispose(); new LeaveApplication(); });
        bg.add(hdrNewLeave);

        // "← Dashboard" ghost button — left of "+ New Leave"
        JButton backBtn = new JButton("← Dashboard");
        backBtn.setBounds(810, 18, 110, 30);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
        backBtn.setForeground(WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { dispose(); new Main_class(); });
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { backBtn.setBorder(new LineBorder(WHITE, 1)); }
            public void mouseExited (MouseEvent e) { backBtn.setBorder(new LineBorder(new Color(170, 195, 235), 1)); }
        });
        bg.add(backBtn);

        // ── Stat cards ────────────────────────────────────────────────
        // 3 cards: w=340 each, gaps=20, left margin=20
        // 20 + 340 + 20 + 340 + 20 + 340 + 20 = 1100
        addStatCard(bg, "Pending",          String.valueOf(stats[0]), "Awaiting Review",      C_AMBER,  20, 76, 340, 94);
        addStatCard(bg, "Approved (Month)", String.valueOf(stats[1]), "Approved This Month",  C_GREEN,  380, 76, 340, 94);
        addStatCard(bg, "Rejected (Month)", String.valueOf(stats[2]), "Rejected This Month",  C_RED,    740, 76, 340, 94);

        // ── Toolbar ───────────────────────────────────────────────────
        countLabel = new JLabel("All Requests");
        countLabel.setBounds(24, 190, 200, 24);
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        countLabel.setForeground(TEXT_DARK);
        bg.add(countLabel);

        filterStatus = new JComboBox<>(new String[]{"All Statuses", "Pending", "Approved", "Rejected"});
        filterStatus.setBounds(240, 191, 170, 28);
        styleFilterCombo(filterStatus);
        filterStatus.addActionListener(e -> loadLeaveData());
        bg.add(filterStatus);

        filterType = new JComboBox<>(buildTypeOptions());
        filterType.setBounds(420, 191, 210, 28);
        styleFilterCombo(filterType);
        filterType.addActionListener(e -> loadLeaveData());
        bg.add(filterType);

        JButton clearBtn = makeSecondaryBtn("✕  Clear");
        clearBtn.setBounds(640, 191, 90, 28);
        clearBtn.addActionListener(e -> {
            filterStatus.setSelectedIndex(0);
            filterType.setSelectedIndex(0);
        });
        bg.add(clearBtn);

        // Export CSV button (Phase 14-C)
        JButton exportCsvBtn = makeSecondaryBtn("⬇ Export CSV");
        exportCsvBtn.setBounds(742, 191, 118, 28);
        exportCsvBtn.addActionListener(e -> CsvExporter.export(this, tableModel, "leave_records"));
        bg.add(exportCsvBtn);

        // "+ New Leave" is already in the header — no duplicate needed here

        // ── Table ─────────────────────────────────────────────────────
        String[] cols = {"Employee", "Leave Type", "From", "To", "Days", "Status", "Applied On", ""};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        leaveTable = new JTable(tableModel);
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leaveTable.setRowHeight(44);
        leaveTable.setShowGrid(false);
        leaveTable.setIntercellSpacing(new Dimension(0, 0));
        leaveTable.setSelectionBackground(ROW_SEL);
        leaveTable.setSelectionForeground(TEXT_DARK);
        leaveTable.setFillsViewportHeight(true);
        leaveTable.setBackground(WHITE);
        leaveTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        leaveTable.getTableHeader().setReorderingAllowed(false);
        leaveTable.getTableHeader().setResizingAllowed(false);

        // Columns sum to 1083 px = (scroll width 1100) − (scrollbar 17 px).
        // When scrollbar is hidden: 17 px neat right gap.
        // When scrollbar appears: fills exactly — nothing clips.
        // Employee=295, Type=182, From=108, To=108, Days=64, Status=124, AppliedOn=134, Actions=68 → 1083
        int[] cw = {295, 182, 108, 108, 64, 124, 134, 68};
        for (int i = 0; i < cw.length; i++) {
            TableColumn tc = leaveTable.getColumnModel().getColumn(i);
            tc.setPreferredWidth(cw[i]);
            if (i == 7) { tc.setMaxWidth(68); tc.setMinWidth(68); }
        }

        applyTableStyle();

        // 3-dot MouseListener
        leaveTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = leaveTable.columnAtPoint(e.getPoint());
                int row = leaveTable.rowAtPoint(e.getPoint());
                // col 7 = Actions (3-dot)
                if (col == 7 && row >= 0 && row < leaveIds.size()) {
                    leaveTable.setRowSelectionInterval(row, row);
                    showContextMenu(row, e);
                }
            }
        });
        leaveTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = leaveTable.columnAtPoint(e.getPoint());
                leaveTable.setCursor(col == 7
                        ? new Cursor(Cursor.HAND_CURSOR)
                        : new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JScrollPane scroll = new JScrollPane(leaveTable);
        scroll.setBounds(0, 227, 1100, 419);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        scroll.getViewport().setBackground(WHITE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bg.add(scroll);

        loadLeaveData();
        setVisible(true);
    }

    // ── Load leave data ────────────────────────────────────────────────
    private void loadLeaveData() {
        tableModel.setRowCount(0);
        leaveIds.clear();
        leaveEmpIds.clear();
        leaveNames.clear();
        leaveStatuses.clear();

        String statusFilter = filterStatus.getSelectedIndex() == 0
                ? null : (String) filterStatus.getSelectedItem();
        String typeFilter = filterType.getSelectedIndex() == 0
                ? null : (String) filterType.getSelectedItem();
        // Extract type id from display label like "Annual Leave  (max 30 days/yr)"
        Integer typeId = null;
        if (typeFilter != null) {
            try {
                Conn cc = new Conn();
                String tname = typeFilter.contains("(") ? typeFilter.substring(0, typeFilter.indexOf("(")).trim() : typeFilter;
                ResultSet tr = cc.statement.executeQuery(
                    "SELECT id FROM leave_type WHERE name='" + tname + "'");
                if (tr.next()) typeId = tr.getInt(1);
                cc.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        try {
            Conn c = new Conn();
            StringBuilder sql = new StringBuilder(
                "SELECT la.id, la.empid, e.name, lt.name AS ltype,"
                + " la.from_date, la.to_date, la.days, la.status, la.applied_on"
                + " FROM leave_application la"
                + " JOIN employee e ON la.empid=e.empid"
                + " JOIN leave_type lt ON la.leave_type=lt.id"
                + " WHERE 1=1");
            if (statusFilter != null) sql.append(" AND la.status='").append(statusFilter).append("'");
            if (typeId != null)       sql.append(" AND la.leave_type=").append(typeId);
            sql.append(" ORDER BY la.applied_on DESC");

            ResultSet rs = c.statement.executeQuery(sql.toString());
            while (rs.next()) {
                int    lid    = rs.getInt("id");
                String eid    = rs.getString("empid");
                String ename  = rs.getString("name");
                String status = rs.getString("status");
                leaveIds.add(lid);
                leaveEmpIds.add(eid);
                leaveNames.add(ename);
                leaveStatuses.add(status);
                tableModel.addRow(new Object[]{
                    ename,
                    rs.getString("ltype"),
                    rs.getString("from_date"),
                    rs.getString("to_date"),
                    rs.getString("days"),
                    status,
                    rs.getString("applied_on") != null
                        ? rs.getString("applied_on").substring(0, 16) : "",
                    "..."
                });
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }

        applyTableStyle();
        countLabel.setText("All Requests  (" + leaveIds.size() + ")");
    }

    // ── Load summary stats ─────────────────────────────────────────────
    private int[] loadStats() {
        int[] s = {0, 0, 0};
        try {
            Conn c = new Conn();
            ResultSet rs;
            rs = c.statement.executeQuery(
                "SELECT COUNT(*) FROM leave_application WHERE status='Pending'");
            if (rs.next()) s[0] = rs.getInt(1);
            rs = c.statement.executeQuery(
                "SELECT COUNT(*) FROM leave_application WHERE status='Approved'"
                + " AND MONTH(applied_on)=MONTH(NOW()) AND YEAR(applied_on)=YEAR(NOW())");
            if (rs.next()) s[1] = rs.getInt(1);
            rs = c.statement.executeQuery(
                "SELECT COUNT(*) FROM leave_application WHERE status='Rejected'"
                + " AND MONTH(applied_on)=MONTH(NOW()) AND YEAR(applied_on)=YEAR(NOW())");
            if (rs.next()) s[2] = rs.getInt(1);
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }
        return s;
    }

    // ── Build leave type filter options ───────────────────────────────
    private String[] buildTypeOptions() {
        List<String> items = new ArrayList<>();
        items.add("All Leave Types");
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT name, max_days FROM leave_type ORDER BY id");
            while (rs.next())
                items.add(rs.getString("name") + "  (max " + rs.getInt("max_days") + " days/yr)");
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }
        return items.toArray(new String[0]);
    }

    // ── Context menu (3-dot) ──────────────────────────────────────────
    private void showContextMenu(int row, MouseEvent e) {
        int    lid    = leaveIds.get(row);
        String empid  = leaveEmpIds.get(row);
        String name   = leaveNames.get(row);
        String status = leaveStatuses.get(row);

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(WHITE);
        menu.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));

        if ("Pending".equals(status)) {
            menu.add(popupItem("Approve",  C_GREEN, ev -> showReviewDialog(lid, name, "Approve")));
            menu.add(popupItem("Reject",   DANGER,  ev -> showReviewDialog(lid, name, "Reject")));
            menu.addSeparator();
        }
        menu.add(popupItem("View Details",      TEXT_DARK, ev -> showDetailDialog(lid)));
        menu.add(popupItem("Leave History",     TEXT_DARK, ev -> LeaveHistory.show(this, empid, name)));

        Rectangle cell = leaveTable.getCellRect(row, 7, false);
        menu.show(leaveTable, cell.x + cell.width - menu.getPreferredSize().width, cell.y + cell.height);
    }

    // ── Approve / Reject dialog ────────────────────────────────────────
    private void showReviewDialog(int leaveId, String empName, String action) {
        boolean isApprove = "Approve".equals(action);
        String title = isApprove ? "Approve Leave Request" : "Reject Leave Request";
        Color  btnColor = isApprove ? C_GREEN : DANGER;
        Color  btnHover = isApprove ? C_GREEN_H : new Color(153, 20, 20);

        JDialog dlg = new JDialog(this, title, true);
        dlg.setSize(460, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(null);
        dlg.setResizable(false);
        dlg.getContentPane().setBackground(WHITE);

        // Header stripe
        JPanel hdr = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, NAVY, getWidth(), 0, NAVY_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                g2.drawString(title, 18, 26);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Employee: " + empName, 18, 44);
            }
        };
        hdr.setBounds(0, 0, 460, 58);
        dlg.add(hdr);

        JLabel remarkLbl = new JLabel(isApprove ? "Remarks (optional):" : "Reason for rejection (required):");
        remarkLbl.setBounds(20, 74, 420, 18);
        remarkLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        remarkLbl.setForeground(TEXT_GRAY);
        dlg.add(remarkLbl);

        JTextArea remarkArea = new JTextArea();
        remarkArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        remarkArea.setForeground(TEXT_DARK);
        remarkArea.setLineWrap(true);
        remarkArea.setWrapStyleWord(true);
        JScrollPane remarkScroll = new JScrollPane(remarkArea);
        remarkScroll.setBounds(20, 96, 420, 100);
        remarkScroll.setBorder(new LineBorder(BORDER_COL, 1));
        dlg.add(remarkScroll);

        JButton confirmBtn = makePrimaryBtn(isApprove ? "Approve" : "Reject", btnColor, btnHover);
        confirmBtn.setBounds(130, 220, 140, 36);
        confirmBtn.addActionListener(e -> {
            String remarks = remarkArea.getText().trim();
            if (!isApprove && remarks.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                    "Rejection reason is required.", "Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String newStatus = isApprove ? "Approved" : "Rejected";
            try {
                Conn c = new Conn();
                c.statement.executeUpdate(
                    "UPDATE leave_application SET status='" + newStatus
                    + "', reviewed_on=NOW(), remarks='"
                    + remarks.replace("'", "''") + "'"
                    + " WHERE id=" + leaveId);
                c.close();
                dlg.dispose();
                // 14-D: Show toast confirming balance updated
                if (isApprove) {
                    ToastNotification.show(this,
                        "Leave approved for " + empName + ". Balance updated automatically.",
                        ToastNotification.Type.SUCCESS);
                } else {
                    ToastNotification.show(this,
                        "Leave request rejected for " + empName + ".",
                        ToastNotification.Type.WARNING);
                }
                loadLeaveData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dlg.add(confirmBtn);

        JButton cancelBtn = makeSecondaryBtn("Cancel");
        cancelBtn.setBounds(290, 220, 140, 36);
        cancelBtn.addActionListener(e -> dlg.dispose());
        dlg.add(cancelBtn);

        dlg.setVisible(true);
    }

    // ── View detail dialog ─────────────────────────────────────────────
    private void showDetailDialog(int leaveId) {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT la.*, e.name, lt.name AS ltype"
                + " FROM leave_application la"
                + " JOIN employee e ON la.empid=e.empid"
                + " JOIN leave_type lt ON la.leave_type=lt.id"
                + " WHERE la.id=" + leaveId);
            if (!rs.next()) { c.close(); return; }

            String[][] fields = {
                {"Employee",   rs.getString("name")},
                {"Emp ID",     rs.getString("empid")},
                {"Leave Type", rs.getString("ltype")},
                {"From",       rs.getString("from_date")},
                {"To",         rs.getString("to_date")},
                {"Days",       rs.getString("days") + " working day(s)"},
                {"Reason",     rs.getString("reason")},
                {"Status",     rs.getString("status")},
                {"Applied On", rs.getString("applied_on") != null
                               ? rs.getString("applied_on").substring(0, 16) : ""},
                {"Remarks",    rs.getString("remarks") != null ? rs.getString("remarks") : "—"},
            };
            c.close();

            JDialog dlg = new JDialog(this, "Leave Request Details", true);
            dlg.setSize(460, 460);
            dlg.setLocationRelativeTo(this);
            dlg.setLayout(null);
            dlg.setResizable(false);
            dlg.getContentPane().setBackground(WHITE);

            JPanel hdr = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setPaint(new GradientPaint(0, 0, NAVY, getWidth(), 0, NAVY_DARK));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                    g2.drawString("Leave Request  #" + leaveId, 18, 30);
                    g2.setColor(new Color(180, 200, 230));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    g2.drawString("Full details of this leave application", 18, 50);
                }
            };
            hdr.setBounds(0, 0, 460, 62);
            dlg.add(hdr);

            int fy = 74;
            for (String[] f : fields) {
                JLabel key = new JLabel(f[0]);
                key.setBounds(20, fy, 130, 20);
                key.setFont(new Font("SansSerif", Font.BOLD, 11));
                key.setForeground(TEXT_GRAY);
                dlg.add(key);

                // Special badge for Status field
                if ("Status".equals(f[0])) {
                    JLabel badge = new JLabel(f[1]);
                    badge.setBounds(160, fy, 100, 20);
                    badge.setFont(new Font("SansSerif", Font.BOLD, 11));
                    Color bg, fg;
                    switch (f[1]) {
                        case "Approved": bg = APPROVED_BG; fg = APPROVED_FG; break;
                        case "Rejected": bg = REJECTED_BG; fg = REJECTED_FG; break;
                        default:         bg = PENDING_BG;  fg = PENDING_FG;  break;
                    }
                    badge.setBackground(bg); badge.setForeground(fg);
                    badge.setOpaque(true);
                    badge.setHorizontalAlignment(SwingConstants.CENTER);
                    badge.setBorder(BorderFactory.createLineBorder(fg, 1));
                    dlg.add(badge);
                } else {
                    JLabel val = new JLabel(f[1] != null ? f[1] : "—");
                    val.setBounds(160, fy, 280, 20);
                    val.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    val.setForeground(TEXT_DARK);
                    dlg.add(val);
                }

                JPanel sep = new JPanel();
                sep.setBackground(new Color(236, 240, 248));
                sep.setBounds(20, fy + 22, 420, 1);
                dlg.add(sep);
                fy += 34;
            }

            JButton closeBtn = makeSecondaryBtn("Close");
            closeBtn.setBounds(160, fy + 8, 140, 32);
            closeBtn.addActionListener(e -> dlg.dispose());
            dlg.add(closeBtn);

            dlg.setVisible(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ── Table styling ─────────────────────────────────────────────────
    private void applyTableStyle() {
        // Default renderer
        leaveTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lbl.setForeground(TEXT_GRAY);
                lbl.setBackground(sel ? ROW_SEL : (row % 2 == 0 ? WHITE : ROW_ALT));
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                        BorderFactory.createEmptyBorder(0, 14, 0, 14)));
                return lbl;
            }
        });

        // Header renderer
        leaveTable.getTableHeader().setPreferredSize(new Dimension(0, 42));
        leaveTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setBackground(HDR_BG);
                lbl.setForeground(TEXT_GRAY);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                lbl.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COL),
                        BorderFactory.createEmptyBorder(0, 14, 0, 14)));
                return lbl;
            }
        });

        // Column 0: Avatar + Name renderer
        leaveTable.getColumnModel().getColumn(0).setCellRenderer(new AvatarNameRenderer());
        // Column 5: Status badge renderer
        leaveTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        // Column 7: 3-dot renderer
        leaveTable.getColumnModel().getColumn(7).setCellRenderer(new DotMenuRenderer());
    }

    // ── Renderers ─────────────────────────────────────────────────────

    class AvatarNameRenderer extends JComponent implements TableCellRenderer {
        private String name = ""; private boolean sel, even;
        AvatarNameRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean s, boolean f, int row, int col) {
            name = v != null ? v.toString() : ""; sel = s; even = (row % 2 == 0); return this;
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            int W = getWidth(), H = getHeight();
            g2.setColor(sel ? ROW_SEL : (even ? WHITE : ROW_ALT));
            g2.fillRect(0, 0, W, H);
            g2.setColor(new Color(236, 240, 248)); g2.fillRect(0, H - 1, W, 1);
            int aD = 30, aX = 14, aY = (H - aD) / 2;
            Color ac = AV[Math.abs(name.hashCode()) % AV.length];
            g2.setColor(ac); g2.fillOval(aX, aY, aD, aD);
            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String init = name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase();
            g2.drawString(init, aX + aD/2 - fm.stringWidth(init)/2, aY + aD/2 + fm.getAscent()/2 - 3);
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString(name, aX + aD + 10, H / 2 + 5);
        }
    }

    class StatusBadgeRenderer extends DefaultTableCellRenderer {
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
            lbl.setBackground(bg);
            lbl.setForeground(fg);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 248)),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)));
            return lbl;
        }
    }

    class DotMenuRenderer extends JComponent implements TableCellRenderer {
        private boolean sel, even;
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
            // Circle button — centred horizontally with 8 px padding each side
            int d = 30, bx = (W - d) / 2, by = (H - d) / 2;
            g2.setColor(new Color(218, 226, 240)); g2.fillOval(bx, by, d, d);
            g2.setColor(new Color(80, 100, 145));
            int cx = W / 2;
            for (int i = -5; i <= 5; i += 5) g2.fillOval(cx + i - 2, H / 2 - 2, 4, 4);
        }
    }

    // ── Stat card ──────────────────────────────────────────────────────
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
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, W, H, 10, 10));
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, W - 1, H - 1, 10, 10));
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, H, 5, 5));

                int iD = 40, iX = W - 56, iY = (H - iD) / 2;
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 16));
                g2.fillOval(iX, iY, iD, iD);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 130));
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                String icon = title.startsWith("Pending") ? "!" : title.startsWith("Approved") ? "✓" : "✗";
                g2.drawString(icon, iX + iD/2 - fm.stringWidth(icon)/2, iY + iD/2 + fm.getAscent()/2 - 3);

                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString(title.toUpperCase(), 16, 22);

                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 30));
                g2.drawString(value, 16, 64);

                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString(sub, 16, 82);
            }
        };
        card.setOpaque(false);
        card.setBounds(x, y, w, h);
        parent.add(card);
    }

    // ── Helpers ────────────────────────────────────────────────────────
    private static final Color TEXT_DARK2 = new Color(22, 36, 71);

    private JMenuItem popupItem(String text, Color fg, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("SansSerif", Font.PLAIN, 12));
        item.setForeground(fg);
        item.setBackground(WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 24));
        item.addActionListener(action);
        return item;
    }

    private void styleFilterCombo(JComboBox<String> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cb.setBackground(WHITE);
        cb.setForeground(TEXT_GRAY);
        cb.setFocusable(false);
        cb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cb.setBorder(new LineBorder(BORDER_COL, 1));
    }

    private JButton makePrimaryBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
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
}
