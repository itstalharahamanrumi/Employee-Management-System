package employee.management.system;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class LeaveApplication extends JFrame {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color NAVY        = new Color( 26,  54, 105);
    private static final Color NAVY_DARK   = new Color( 18,  38,  76);
    private static final Color WHITE       = Color.WHITE;
    private static final Color BG          = new Color(245, 247, 251);
    private static final Color INPUT_BG    = new Color(248, 250, 253);
    private static final Color BORDER_NORM = new Color(210, 218, 230);
    private static final Color BORDER_FOCUS= new Color( 66, 153, 225);
    private static final Color TEXT_DARK   = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY   = new Color(100, 115, 140);
    private static final Color TEXT_DIM    = new Color(160, 170, 185);
    private static final Color C_GREEN     = new Color( 34, 139,  87);
    private static final Color C_GREEN_H   = new Color( 22, 108,  67);
    private static final Color C_AMBER     = new Color(217, 119,   6);
    private static final Color DANGER      = new Color(185,  28,  28);

    // ── Widgets ────────────────────────────────────────────────────────
    private JComboBox<String> empCombo;
    private JComboBox<String> typeCombo;
    private JDateChooser fromDate, toDate;
    private JLabel daysLabel, balanceLbl;
    private JTextArea reasonArea;

    // row maps: display text → DB id / empid
    private final LinkedHashMap<String, String>  empDisplayToId  = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> typeDisplayToId = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Integer> typeMaxDays    = new LinkedHashMap<>();

    LeaveApplication() {
        setTitle("Submit Leave Request — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { goBack(); }
        });

        // ── Background panel ──────────────────────────────────────────
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
                g2.fillRect(0, 0, W, 80);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                g2.drawString("Submit Leave Request", 28, 36);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Fill in all fields — working days are calculated automatically (Mon–Fri)", 28, 57);

                // White form card
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));
                g2.setColor(new Color(218, 224, 235));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));

                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("LEAVE REQUEST DETAILS", 44, 110);
                g2.setColor(new Color(235, 238, 245));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(44, 118, W - 44, 118);

                // Footer
                g2.setColor(new Color(235, 238, 245));
                g2.fillRect(0, H - 40, W, 40);
                g2.setColor(new Color(218, 224, 235));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, H - 40, W, H - 40);
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

        // ── Row 1: Employee | Leave Type ──────────────────────────────
        // Left col: x=42 w=360 | Right col: x=518 w=360
        addLabel(bg, "Employee", 42, 132);
        empCombo = new JComboBox<>();
        styleCombo(empCombo, 42, 152, 460);
        bg.add(empCombo);

        addLabel(bg, "Leave Type", 518, 132);
        typeCombo = new JComboBox<>();
        styleCombo(typeCombo, 518, 152, 360);
        bg.add(typeCombo);

        // ── Row 2: From Date | To Date ────────────────────────────────
        addLabel(bg, "From Date", 42, 224);
        fromDate = new JDateChooser();
        fromDate.setBounds(42, 244, 360, 36);
        fromDate.setBackground(INPUT_BG);
        fromDate.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bg.add(fromDate);

        addLabel(bg, "To Date", 518, 224);
        toDate = new JDateChooser();
        toDate.setBounds(518, 244, 360, 36);
        toDate.setBackground(INPUT_BG);
        toDate.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bg.add(toDate);

        // ── Row 3: Working Days badge | Leave Balance info ────────────
        addLabel(bg, "Working Days (auto-calculated)", 42, 316);

        daysLabel = new JLabel("0 days");
        daysLabel.setBounds(42, 336, 200, 36);
        daysLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        daysLabel.setForeground(NAVY);
        bg.add(daysLabel);

        addLabel(bg, "Leave Balance for Selected Type", 518, 316);
        balanceLbl = new JLabel("Select leave type to see balance");
        balanceLbl.setBounds(518, 336, 360, 36);
        balanceLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        balanceLbl.setForeground(TEXT_GRAY);
        bg.add(balanceLbl);

        // ── Row 4: Reason (full-width textarea) ───────────────────────
        addLabel(bg, "Reason for Leave", 42, 408);

        reasonArea = new JTextArea();
        reasonArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        reasonArea.setForeground(TEXT_DARK);
        reasonArea.setBackground(INPUT_BG);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Declare scroll first so the focus listener can reference it
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBounds(42, 428, 1016, 100);
        reasonScroll.setBorder(new LineBorder(BORDER_NORM, 1));
        reasonScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        reasonArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { reasonScroll.setBorder(new LineBorder(BORDER_FOCUS, 2)); }
            public void focusLost (FocusEvent e) { reasonScroll.setBorder(new LineBorder(BORDER_NORM,  1)); }
        });
        bg.add(reasonScroll);

        // ── Buttons ───────────────────────────────────────────────────
        JButton backBtn = makeSecondaryBtn("← Back");
        backBtn.setBounds(42, 566, 160, 36);
        backBtn.addActionListener(e -> goBack());
        bg.add(backBtn);

        JButton submitBtn = makePrimaryBtn("Submit Leave Request", C_GREEN, C_GREEN_H);
        submitBtn.setBounds(656, 566, 310, 36);
        submitBtn.addActionListener(e -> submitLeave());
        bg.add(submitBtn);

        getRootPane().setDefaultButton(submitBtn);

        // ── Date change listeners → recalculate days ──────────────────
        fromDate.addPropertyChangeListener("date", e -> recalcDays());
        toDate.addPropertyChangeListener("date",   e -> recalcDays());
        typeCombo.addActionListener(e -> updateBalance());

        // ── Populate combos from DB ───────────────────────────────────
        loadEmployees();
        loadLeaveTypes();

        setVisible(true);
    }

    // ── DB loaders ────────────────────────────────────────────────────

    private void loadEmployees() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT empid, name FROM employee ORDER BY name");
            while (rs.next()) {
                String eid  = rs.getString("empid");
                String name = rs.getString("name");
                String display = eid + "  –  " + name;
                empDisplayToId.put(display, eid);
                empCombo.addItem(display);
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadLeaveTypes() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT id, name, max_days FROM leave_type ORDER BY id");
            while (rs.next()) {
                int    id      = rs.getInt("id");
                String name    = rs.getString("name");
                int    maxDays = rs.getInt("max_days");
                String display = name + "  (max " + maxDays + " days/yr)";
                typeDisplayToId.put(display, id);
                typeMaxDays.put(id, maxDays);
                typeCombo.addItem(display);
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ── Recalculate working days ───────────────────────────────────────
    private void recalcDays() {
        java.util.Date f = fromDate.getDate();
        java.util.Date t = toDate.getDate();
        int days = calcWorkingDays(f, t);
        if (days < 0) {
            daysLabel.setText("Invalid range");
            daysLabel.setForeground(DANGER);
        } else {
            daysLabel.setText(days + (days == 1 ? " day" : " days"));
            daysLabel.setForeground(days == 0 ? TEXT_GRAY : NAVY);
        }
        updateBalance();
    }

    private int calcWorkingDays(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) return 0;
        if (to.before(from)) return -1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        int count = 0;
        while (!cal.getTime().after(to)) {
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            if (dow != Calendar.SATURDAY && dow != Calendar.SUNDAY) count++;
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return count;
    }

    // ── Update leave balance display ───────────────────────────────────
    private void updateBalance() {
        String selType = (String) typeCombo.getSelectedItem();
        if (selType == null || !typeDisplayToId.containsKey(selType)) {
            balanceLbl.setText("Select leave type to see balance");
            balanceLbl.setForeground(TEXT_GRAY);
            return;
        }
        String selEmp = (String) empCombo.getSelectedItem();
        String empid  = selEmp != null ? empDisplayToId.get(selEmp) : null;
        int    typeId = typeDisplayToId.get(selType);
        int    maxDays = typeMaxDays.getOrDefault(typeId, 0);

        if (empid == null) {
            balanceLbl.setText("Max allowed: " + maxDays + " days/year");
            balanceLbl.setForeground(TEXT_GRAY);
            return;
        }
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT COALESCE(SUM(days),0) FROM leave_application"
                + " WHERE empid='" + empid + "' AND leave_type=" + typeId
                + " AND status='Approved'"
                + " AND YEAR(from_date)=YEAR(CURDATE())");
            int used = rs.next() ? rs.getInt(1) : 0;
            int rem  = maxDays - used;
            c.close();
            balanceLbl.setText("Used: " + used + " / " + maxDays + " days   |   Remaining: " + rem);
            balanceLbl.setForeground(rem <= 0 ? DANGER : (rem <= 3 ? C_AMBER : C_GREEN));
        } catch (Exception ex) {
            balanceLbl.setText("Max allowed: " + maxDays + " days/year");
            balanceLbl.setForeground(TEXT_GRAY);
        }
    }

    // ── Submit ────────────────────────────────────────────────────────
    private void submitLeave() {
        String selEmp  = (String) empCombo.getSelectedItem();
        String selType = (String) typeCombo.getSelectedItem();
        java.util.Date fd = fromDate.getDate();
        java.util.Date td = toDate.getDate();
        String reason  = reasonArea.getText().trim();

        if (selEmp == null || selType == null) {
            warn("Please select an employee and leave type.");
            return;
        }
        if (fd == null || td == null) {
            warn("Please select both From Date and To Date.");
            return;
        }
        if (td.before(fd)) {
            warn("To Date cannot be earlier than From Date.");
            return;
        }
        int days = calcWorkingDays(fd, td);
        if (days == 0) {
            warn("Selected range has 0 working days (weekend only).\nPlease choose different dates.");
            return;
        }
        if (reason.isEmpty()) {
            warn("Please enter a reason for the leave request.");
            return;
        }

        String empid  = empDisplayToId.get(selEmp);
        int    typeId = typeDisplayToId.get(selType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromStr = sdf.format(fd);
        String toStr   = sdf.format(td);

        // Business rule: no duplicate pending request for same employee + type
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                "SELECT COUNT(*) FROM leave_application"
                + " WHERE empid='" + empid + "' AND leave_type=" + typeId
                + " AND status='Pending'");
            if (rs.next() && rs.getInt(1) > 0) {
                c.close();
                warn("This employee already has a Pending request for the same leave type.\n"
                   + "Please wait for it to be reviewed before submitting a new one.");
                return;
            }

            String sql = "INSERT INTO leave_application"
                + " (empid, leave_type, from_date, to_date, days, reason)"
                + " VALUES ('" + empid + "', " + typeId + ", '"
                + fromStr + "', '" + toStr + "', " + days
                + ", '" + reason.replace("'", "''") + "')";
            c.statement.executeUpdate(sql);
            c.close();

            JOptionPane.showMessageDialog(this,
                "Leave request submitted successfully!\n"
                + "Days requested: " + days + " working day(s)\n"
                + "Status: Pending — awaiting review",
                "Submitted", JOptionPane.INFORMATION_MESSAGE);
            goBack();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() { dispose(); new LeaveManagement(); }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    // ── UI helpers ────────────────────────────────────────────────────

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 460, 18);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_GRAY);
        p.add(lbl);
    }

    private void styleCombo(JComboBox<String> cb, int x, int y, int w) {
        cb.setBounds(x, y, w, 36);
        cb.setBackground(WHITE);
        cb.setForeground(TEXT_DARK);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBorder(new LineBorder(BORDER_NORM, 1));
    }

    private JButton makePrimaryBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton makeSecondaryBtn(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BG); btn.setForeground(TEXT_GRAY);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(BORDER_NORM, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(NAVY); btn.setBorder(new LineBorder(NAVY, 1)); }
            public void mouseExited (MouseEvent e) { btn.setForeground(TEXT_GRAY); btn.setBorder(new LineBorder(BORDER_NORM, 1)); }
        });
        return btn;
    }
}
