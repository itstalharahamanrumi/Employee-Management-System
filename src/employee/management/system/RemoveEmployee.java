package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;

public class RemoveEmployee extends JFrame implements ActionListener {

    // ── Palette ───────────────────────────────────────────────────────
    private static final Color NAVY        = new Color( 26,  54, 105);
    private static final Color NAVY_DARK   = new Color( 18,  38,  76);
    private static final Color WHITE       = Color.WHITE;
    private static final Color BG          = new Color(245, 247, 251);
    private static final Color BORDER_NORM = new Color(210, 218, 230);
    private static final Color TEXT_DARK   = new Color( 22,  36,  71);
    private static final Color TEXT_GRAY   = new Color(100, 115, 140);
    private static final Color TEXT_DIM    = new Color(160, 170, 185);
    private static final Color DANGER      = new Color(185,  28,  28); // red
    private static final Color DANGER_H    = new Color(153,  27,  27);
    private static final Color WARN_BG     = new Color(254, 242, 242); // red-tinted bg
    private static final Color WARN_BORDER = new Color(252, 165, 165);
    private static final Color WARN_TEXT   = new Color(153,  27,  27);
    private static final Color PREVIEW_BG  = new Color(248, 250, 253);

    JComboBox<String> choiceEMPID;
    JLabel valName, valDesignation, valPhone, valEmail; // preview labels
    JButton delete, back;

    RemoveEmployee() {
        setTitle("Remove Employee — Employee Management System");
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
                g2.drawString("Remove Employee", 28, 36);
                g2.setColor(new Color(180, 200, 230));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Select an employee to permanently delete their record", 28, 57);

                // Main card
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));
                g2.setColor(BORDER_NORM);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(24, 90, W - 48, H - 140, 12, 12));

                // Section: SELECT EMPLOYEE
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("SELECT EMPLOYEE", 44, 110);
                g2.setColor(new Color(235, 238, 245));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(44, 118, W - 44, 118);

                // Section: PREVIEW
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("EMPLOYEE PREVIEW", 44, 185);
                g2.setColor(new Color(235, 238, 245));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(44, 193, W - 44, 193);

                // Preview inner card (centered, 560px wide for 1100)
                int cardW = 560, cardX = (W - cardW) / 2;
                g2.setColor(PREVIEW_BG);
                g2.fill(new RoundRectangle2D.Float(cardX, 200, cardW, 148, 8, 8));
                g2.setColor(BORDER_NORM);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(cardX, 200, cardW, 148, 8, 8));

                // Warning strip
                g2.setColor(WARN_BG);
                g2.fill(new RoundRectangle2D.Float(36, 358, W - 72, 46, 6, 6));
                g2.setColor(WARN_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(36, 358, W - 72, 46, 6, 6));
                // Red left stripe on warning
                g2.setColor(DANGER);
                g2.fill(new RoundRectangle2D.Float(36, 358, 4, 46, 4, 4));
                // Warning text
                g2.setColor(WARN_TEXT);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.drawString("[!]  This action is permanent and cannot be undone.", 50, 387);

                // Footer
                g2.setColor(new Color(235, 238, 245));
                g2.fillRect(0, H - 40, W, 40);
                g2.setColor(BORDER_NORM);
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

        // ── Centered content block for 1100×700 ───────────────────────
        int blockW = 520, baseX = (1100 - blockW) / 2;

        // ── Employee ID dropdown ───────────────────────────────────────
        choiceEMPID = new JComboBox<>();
        choiceEMPID.setBounds(baseX, 126, blockW, 34);
        choiceEMPID.setBackground(WHITE);
        choiceEMPID.setForeground(TEXT_DARK);
        choiceEMPID.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bg.add(choiceEMPID);

        // ── Preview rows inside the inner card ───────────────────────
        valName        = addPreviewRow(bg, "Name",        216, baseX);
        valDesignation = addPreviewRow(bg, "Designation", 250, baseX);
        valPhone       = addPreviewRow(bg, "Phone",       284, baseX);
        valEmail       = addPreviewRow(bg, "Email",       318, baseX);

        // ── Buttons (centered) ────────────────────────────────────────
        int btnW = 196, btnGap = 24;
        back = new JButton("Back to Dashboard");
        back.setBounds(baseX, 416, btnW, 36);
        styleSecondaryBtn(back);
        back.addActionListener(this);
        bg.add(back);

        delete = new JButton("Delete Employee");
        delete.setBounds(baseX + btnW + btnGap, 416, btnW, 36);
        styleDangerBtn(delete);
        delete.addActionListener(this);
        bg.add(delete);

        // ── Populate and wire up ──────────────────────────────────────
        loadEmpIds();
        if (choiceEMPID.getItemCount() > 0) {
            loadPreview((String) choiceEMPID.getItemAt(0));
        }
        // Auto-refresh preview when a different employee is chosen
        choiceEMPID.addActionListener(ev -> {
            String sel = (String) choiceEMPID.getSelectedItem();
            if (sel != null) loadPreview(sel);
        });

        setVisible(true);
    }

    // ── Add a label+value row to the preview card ─────────────────────
    private JLabel addPreviewRow(JPanel parent, String fieldName, int y, int baseX) {
        JLabel lbl = new JLabel(fieldName);
        lbl.setBounds(baseX + 16, y, 130, 24);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_GRAY);
        parent.add(lbl);

        JLabel val = new JLabel("—");
        val.setBounds(baseX + 164, y, 356, 24);
        val.setFont(new Font("SansSerif", Font.PLAIN, 13));
        val.setForeground(TEXT_DARK);
        parent.add(val);
        return val;
    }

    // ── Load all Employee IDs into dropdown ───────────────────────────
    private void loadEmpIds() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery("SELECT empid FROM employee ORDER BY empid");
            while (rs.next()) { choiceEMPID.addItem(rs.getString("empid")); }
            c.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Query DB and refresh preview labels ───────────────────────────
    private void loadPreview(String empid) {
        if (empid == null || empid.isEmpty()) return;
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery(
                    "SELECT name, designation, phone, email FROM employee WHERE empid = '"
                    + empid + "'");
            if (rs.next()) {
                valName.setText(rs.getString("name"));
                valDesignation.setText(rs.getString("designation"));
                valPhone.setText(rs.getString("phone"));
                valEmail.setText(rs.getString("email"));
            }
            c.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ── Button styles ─────────────────────────────────────────────────
    private void styleDangerBtn(JButton btn) {
        btn.setBackground(DANGER);
        btn.setForeground(WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(DANGER_H); }
            public void mouseExited (MouseEvent e) { btn.setBackground(DANGER); }
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
        if (e.getSource() == delete) {
            String empid = (String) choiceEMPID.getSelectedItem();
            if (empid == null || empid.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No employee selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Confirmation dialog with employee name
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + valName.getText() + "\" (" + empid + ")?\n\n"
                    + "This action is permanent and cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Conn c = new Conn();
                    c.statement.executeUpdate(
                            "DELETE FROM employee WHERE empid = '" + empid + "'");
                    c.close();
                    JOptionPane.showMessageDialog(this,
                            "Employee \"" + valName.getText() + "\" has been deleted.",
                            "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new Main_class();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Database error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Back button
            dispose();
            new Main_class();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RemoveEmployee::new);
    }
}
