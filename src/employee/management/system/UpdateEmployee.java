package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;

public class UpdateEmployee extends JFrame implements ActionListener {

    // ── Palette — same as all other screens ──────────────────────────
    private static final Color NAVY         = new Color( 26,  54, 105);
    private static final Color NAVY_DARK    = new Color( 18,  38,  76);
    private static final Color WHITE        = Color.WHITE;
    private static final Color BG           = new Color(245, 247, 251);
    private static final Color INPUT_BG     = new Color(248, 250, 253); // editable
    private static final Color INPUT_RO     = new Color(238, 241, 247); // read-only
    private static final Color BORDER_NORM  = new Color(210, 218, 230);
    private static final Color BORDER_FOCUS = new Color( 66, 153, 225);
    private static final Color TEXT_DARK    = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY    = new Color(100, 115, 140);
    private static final Color TEXT_DIM     = new Color(160, 170, 185);
    private static final Color BTN_AMBER    = new Color(217, 119,   6);
    private static final Color BTN_AMBER_H  = new Color(180,  98,   0);

    // ── Fields ───────────────────────────────────────────────────────
    // Read-only (loaded from DB, cannot be changed)
    JTextField roName, roDob, roEmpId;
    // Editable
    JTextField tfname, taddress, tphone, temail, tsalary, tdesignation;
    JComboBox<String> comboEducation;
    JButton btnUpdate, btnBack;

    String empId;

    UpdateEmployee(String empId) {
        this.empId = empId;
        setTitle("Update Employee — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); new Main_class(); }
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

                // Page background
                g2.setColor(BG);
                g2.fillRect(0, 0, W, H);

                // Navy header
                g2.setPaint(new GradientPaint(0, 0, NAVY, W, 0, NAVY_DARK));
                g2.fillRect(0, 0, W, 80);
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                g2.drawString("Update Employee Details", 28, 36);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Shaded fields are locked — Name, Date of Birth and Employee ID cannot be changed", 28, 57);

                // Form card
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));
                g2.setColor(new Color(218, 224, 235));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));

                // Section label
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("EMPLOYEE INFORMATION", 44, 110);
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

        // ── Form: 2-column layout, same x/y as AddEmployee ───────────
        // Left col x=42, w=360 | Right col x=518, w=360

        // Row 1 — Name (locked) | Father's Name (editable)
        addLabel(bg, "Name  [read-only]",  42,  130); roName  = addReadOnly(bg, 42,  150, 360);
        addLabel(bg, "Father's Name",      518, 130); tfname  = addInput(   bg, 518, 150, 360);

        // Row 2 — Date of Birth (locked) | Salary (editable)
        addLabel(bg, "Date of Birth  [read-only]", 42,  222); roDob   = addReadOnly(bg, 42,  242, 360);
        addLabel(bg, "Salary (BDT)",               518, 222); tsalary = addInput(   bg, 518, 242, 360);

        // Row 3 — Address (editable) | Phone (editable)
        addLabel(bg, "Address",      42,  314); taddress = addInput(bg, 42,  334, 360);
        addLabel(bg, "Phone Number", 518, 314); tphone   = addInput(bg, 518, 334, 360);

        // Row 4 — Email (editable) | Education (JComboBox)
        addLabel(bg, "Email Address", 42, 406); temail = addInput(bg, 42, 426, 360);
        addLabel(bg, "Education",    518, 406);
        String[] eduItems = {"-- Select --", "CSE", "BBA", "LLB", "CE", "TE", "ME", "EEE", "English"};
        comboEducation = new JComboBox<>(eduItems);
        comboEducation.setBounds(518, 426, 360, 36);
        comboEducation.setBackground(WHITE);
        comboEducation.setForeground(TEXT_DARK);
        comboEducation.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboEducation.setBorder(new LineBorder(BORDER_NORM, 1));
        bg.add(comboEducation);

        // Row 5 — Designation (editable) | Employee ID (locked badge)
        addLabel(bg, "Designation",            42,  498); tdesignation = addInput(   bg, 42,  518, 360);
        addLabel(bg, "Employee ID  [read-only]", 518, 498); roEmpId      = addReadOnly(bg, 518, 518, 360);

        // ── Buttons ───────────────────────────────────────────────────
        btnBack = new JButton("← Back to Dashboard");
        btnBack.setBounds(42, 590, 210, 36);
        styleSecondaryBtn(btnBack);
        btnBack.addActionListener(this);
        bg.add(btnBack);

        btnUpdate = new JButton("Save Changes");
        btnUpdate.setBounds(644, 590, 216, 36);
        stylePrimaryBtn(btnUpdate, BTN_AMBER, BTN_AMBER_H);
        btnUpdate.addActionListener(this);
        bg.add(btnUpdate);

        getRootPane().setDefaultButton(btnUpdate);

        // ── Pre-fill form from DB ─────────────────────────────────────
        loadEmployeeData();
        setVisible(true);
    }

    // ── Load current employee data into fields ────────────────────────
    private void loadEmployeeData() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT * FROM employee WHERE empid = '" + empId + "'");
            if (rs.next()) {
                roName.setText(rs.getString("name"));
                tfname.setText(rs.getString("fname"));
                roDob.setText(rs.getString("dob"));
                tsalary.setText(rs.getString("salary"));
                taddress.setText(rs.getString("address"));
                tphone.setText(rs.getString("phone"));
                temail.setText(rs.getString("email"));
                tdesignation.setText(rs.getString("designation"));
                roEmpId.setText(rs.getString("empid"));
                // Match education in JComboBox (case-insensitive)
                String edu = rs.getString("education");
                for (int i = 0; i < comboEducation.getItemCount(); i++) {
                    if (comboEducation.getItemAt(i).equalsIgnoreCase(edu)) {
                        comboEducation.setSelectedIndex(i);
                        break;
                    }
                }
            }
            c.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not load employee data: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Action handler ────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdate) {
            String fname       = tfname.getText().trim();
            String salary      = tsalary.getText().trim();
            String address     = taddress.getText().trim();
            String phone       = tphone.getText().trim();
            String email       = temail.getText().trim();
            String education   = (String) comboEducation.getSelectedItem();
            String designation = tdesignation.getText().trim();

            // Validation
            if (fname.isEmpty() || salary.isEmpty() || address.isEmpty()
                    || phone.isEmpty() || email.isEmpty() || designation.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all editable fields before saving.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (education == null || education.startsWith("--")) {
                JOptionPane.showMessageDialog(this,
                        "Please select an Education qualification.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // SQL UPDATE
            try {
                Conn c = new Conn();
                String query = "UPDATE employee SET "
                        + "fname='"       + fname       + "', "
                        + "salary='"      + salary      + "', "
                        + "address='"     + address     + "', "
                        + "phone='"       + phone       + "', "
                        + "email='"       + email       + "', "
                        + "education='"   + education   + "', "
                        + "designation='" + designation + "' "
                        + "WHERE empid='" + empId       + "'";
                c.statement.executeUpdate(query);
                c.close();
                JOptionPane.showMessageDialog(this,
                        "Employee record updated successfully!",
                        "Updated", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new Main_class();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            // Back button → return to Dashboard
            dispose();
            new Main_class();
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────

    private void addLabel(JPanel parent, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 360, 18);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_GRAY);
        parent.add(lbl);
    }

    /** White editable field with blue focus border */
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

    /** Gray non-editable field — visually distinct from editable fields */
    private JTextField addReadOnly(JPanel parent, int x, int y, int w) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, w, 36);
        tf.setBackground(INPUT_RO);
        tf.setForeground(TEXT_GRAY);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setEditable(false);
        tf.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_NORM, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateEmployee("EMP001"));
    }
}
