package employee.management.system;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;

public class AddEmployee extends JFrame implements ActionListener {

    // ── Palette — same as Login, Splash, Main_class ────────────────────
    private static final Color NAVY         = new Color( 26,  54, 105);
    private static final Color NAVY_DARK    = new Color( 18,  38,  76);
    private static final Color WHITE        = Color.WHITE;
    private static final Color BG           = new Color(245, 247, 251);
    private static final Color INPUT_BG     = new Color(248, 250, 253);
    private static final Color BORDER_NORM  = new Color(210, 218, 230);
    private static final Color BORDER_FOCUS = new Color( 66, 153, 225);
    private static final Color TEXT_DARK    = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY    = new Color(100, 115, 140);
    private static final Color TEXT_DIM     = new Color(160, 170, 185);
    private static final Color BTN_GREEN    = new Color( 34, 139,  87);
    private static final Color BTN_GREEN_H  = new Color( 22, 108,  67);
    private static final Color BADGE_BG     = new Color(232, 245, 239);
    private static final Color BADGE_FG     = new Color( 22, 101,  52);

    // ── Auto-generated Employee ID ────────────────────────────────────
    private final String empId = String.format("EMP%06d", new Random().nextInt(999999));

    JTextField tname, tfname, taddress, tphone, temail, tsalary, tdesignation;
    JButton btnAdd, btnBack;
    JDateChooser tdob;
    JComboBox<String> boxEducation;

    AddEmployee() {
        setTitle("Add New Employee — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); new Main_class(); }
        });

        // ── Full-window background panel ──────────────────────────────
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
                g2.fillRect(0, 0, W, 80);

                // Header text
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                g2.drawString("Add New Employee", 28, 36);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Fill in all fields to add a record to the database", 28, 57);

                // Back arrow in header (top-right)
                g2.setColor(new Color(255, 255, 255, 90));
                g2.fillRoundRect(W - 146, 22, 118, 36, 8, 8);

                // Form card
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));
                g2.setColor(new Color(218, 224, 235));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));

                // Section label inside card
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("EMPLOYEE INFORMATION", 44, 110);

                // Divider below section label
                g2.setColor(new Color(235, 238, 245));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(44, 118, W - 44, 118);

                // Footer bar
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

        // ── Form fields: 2-column layout ─────────────────────────────
        // Left col: x=42, w=360   |   Right col: x=518, w=360
        // Rows: label y, field y+20, row stride = 92px

        // Row 1 — Name | Father's Name
        addLabel(bg, "Name",          42,  130); tname   = addInput(bg, 42,  150, 360);
        addLabel(bg, "Father's Name", 518, 130); tfname  = addInput(bg, 518, 150, 360);

        // Row 2 — Date of Birth | Salary
        addLabel(bg, "Date of Birth",  42, 222);
        tdob = new JDateChooser();
        tdob.setBounds(42, 242, 360, 36);
        tdob.setBackground(INPUT_BG);
        tdob.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bg.add(tdob);

        addLabel(bg, "Salary (BDT)", 518, 222); tsalary = addInput(bg, 518, 242, 360);

        // Row 3 — Address | Phone
        addLabel(bg, "Address",      42,  314); taddress = addInput(bg, 42,  334, 360);
        addLabel(bg, "Phone Number", 518, 314); tphone   = addInput(bg, 518, 334, 360);

        // Row 4 — Email | Education
        addLabel(bg, "Email Address", 42, 406); temail = addInput(bg, 42, 426, 360);

        addLabel(bg, "Education", 518, 406);
        String[] eduItems = {"-- Select Qualification --", "CSE", "BBA", "LLB", "CE", "TE", "ME", "EEE", "English"};
        boxEducation = new JComboBox<>(eduItems);
        boxEducation.setBounds(518, 426, 360, 36);
        boxEducation.setBackground(WHITE);
        boxEducation.setForeground(TEXT_DARK);
        boxEducation.setFont(new Font("SansSerif", Font.PLAIN, 13));
        boxEducation.setBorder(new LineBorder(BORDER_NORM, 1));
        bg.add(boxEducation);

        // Row 5 — Designation | Employee ID (read-only badge)
        addLabel(bg, "Designation",               42,  498); tdesignation = addInput(bg, 42, 518, 360);
        addLabel(bg, "Employee ID (Auto-Generated)", 518, 498);

        // Green badge showing the auto-generated ID
        JPanel idBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BADGE_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(BADGE_FG.getRed(), BADGE_FG.getGreen(), BADGE_FG.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.setColor(BADGE_FG);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(empId, getWidth() / 2 - fm.stringWidth(empId) / 2,
                        getHeight() / 2 + fm.getAscent() / 2 - 2);
            }
        };
        idBadge.setOpaque(false);
        idBadge.setBounds(518, 518, 360, 36);
        bg.add(idBadge);

        // ── Buttons ───────────────────────────────────────────────────
        btnBack = new JButton("← Back to Dashboard");
        btnBack.setBounds(42, 590, 200, 36);
        styleSecondaryBtn(btnBack);
        btnBack.addActionListener(this);
        bg.add(btnBack);

        btnAdd = new JButton("Add Employee");
        btnAdd.setBounds(656, 590, 222, 36);
        stylePrimaryBtn(btnAdd, BTN_GREEN, BTN_GREEN_H);
        btnAdd.addActionListener(this);
        bg.add(btnAdd);

        getRootPane().setDefaultButton(btnAdd); // Enter key fires Add
        setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void addLabel(JPanel parent, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 360, 18);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_GRAY);
        parent.add(lbl);
    }

    private JTextField addInput(JPanel parent, int x, int y, int w) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, w, 36);
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_DARK);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setCaretColor(NAVY);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_NORM, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_FOCUS, 2),
                        BorderFactory.createEmptyBorder(0, 9, 0, 9)));
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(new CompoundBorder(
                        new LineBorder(BORDER_NORM, 1),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            }
        });
        parent.add(tf);
        return tf;
    }

    private void stylePrimaryBtn(JButton btn, Color bg, Color hover) {
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
    }

    private void styleSecondaryBtn(JButton btn) {
        btn.setBackground(BG);
        btn.setForeground(TEXT_GRAY);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(BORDER_NORM, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(NAVY); btn.setBorder(new LineBorder(NAVY, 1)); }
            public void mouseExited (MouseEvent e) { btn.setForeground(TEXT_GRAY); btn.setBorder(new LineBorder(BORDER_NORM, 1)); }
        });
    }

    // ── Action handler ────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {

            String name        = tname.getText().trim();
            String fname       = tfname.getText().trim();
            String address     = taddress.getText().trim();
            String phone       = tphone.getText().trim();
            String email       = temail.getText().trim();
            String salary      = tsalary.getText().trim();
            String designation = tdesignation.getText().trim();
            String education   = (String) boxEducation.getSelectedItem();
            String dob         = ((JTextField) tdob.getDateEditor().getUiComponent()).getText().trim();

            // ── Validation ────────────────────────────────────────────
            if (name.isEmpty() || fname.isEmpty() || address.isEmpty()
                    || phone.isEmpty() || email.isEmpty()
                    || salary.isEmpty() || designation.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all text fields before saving.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (dob.isEmpty() || dob.replaceAll("[^0-9]", "").isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select a Date of Birth from the calendar.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (education == null || education.startsWith("--")) {
                JOptionPane.showMessageDialog(this,
                        "Please select an Education qualification.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ── Insert into database ──────────────────────────────────
            try {
                Conn c = new Conn();
                String query = "INSERT INTO employee VALUES('"
                        + name        + "', '"
                        + fname       + "', '"
                        + address     + "', '"
                        + phone       + "', '"
                        + email       + "', '"
                        + education   + "', '"
                        + dob         + "', '"
                        + salary      + "', '"
                        + designation + "', '"
                        + empId       + "')";
                c.statement.execute(query);
                c.close();
                JOptionPane.showMessageDialog(this,
                        "Employee \"" + name + "\" added successfully!\nEmployee ID: " + empId,
                        "Employee Added", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new Main_class();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            // Back button
            dispose();
            new Main_class();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddEmployee::new);
    }
}
