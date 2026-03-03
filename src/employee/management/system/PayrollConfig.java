package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;

public class PayrollConfig extends JFrame {

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

    // ── Fields ─────────────────────────────────────────────────────────
    private JTextField taxField, pfField, houseField, medField;

    PayrollConfig() {
        setTitle("Payroll Configuration — Employee Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { goBack(); }
        });

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
                g2.drawString("Payroll Configuration", 24, 30);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(180, 200, 230));
                g2.drawString("Configure tax rates, provident fund, and allowance percentages", 24, 50);

                // White form card
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(250, 100, 600, 430, 12, 12));
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(250, 100, 599, 429, 12, 12));

                // Card inner header
                g2.setPaint(new GradientPaint(250, 100, new Color(248, 250, 253), 850, 100, WHITE));
                g2.fill(new RoundRectangle2D.Float(250, 100, 600, 70, 12, 12));
                g2.fillRect(250, 130, 600, 40);
                g2.setColor(new Color(232, 237, 247));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(250, 170, 850, 170);

                // Card title
                g2.setColor(TEXT_DARK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                g2.drawString("Rate Settings", 274, 130);
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.drawString("Values are stored as percentages (e.g. enter 10 for 10%)", 274, 152);

                // Row labels — Row 1 (y=190)
                g2.setColor(TEXT_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.drawString("INCOME TAX RATE (%)", 274, 195);
                g2.drawString("PROVIDENT FUND RATE (%)", 524, 195);

                // Row labels — Row 2 (y=285)
                g2.drawString("HOUSE ALLOWANCE RATE (%)", 274, 285);
                g2.drawString("MEDICAL ALLOWANCE RATE (%)", 524, 285);

                // Divider before buttons
                g2.setColor(new Color(232, 237, 247));
                g2.drawLine(270, 395, 830, 395);

                // Info note
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("Changes apply to all future payroll processing. Existing records are not affected.", 274, 415);

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
        JButton backBtn = makeGhostBtn("← Back");
        backBtn.setBounds(984, 18, 90, 30);
        backBtn.addActionListener(e -> goBack());
        bg.add(backBtn);

        // ── Row 1 inputs (y=207) ───────────────────────────────────────
        taxField = makeRateField();
        taxField.setBounds(274, 207, 220, 32);
        bg.add(taxField);

        pfField = makeRateField();
        pfField.setBounds(524, 207, 220, 32);
        bg.add(pfField);

        // ── Row 2 inputs (y=297) ───────────────────────────────────────
        houseField = makeRateField();
        houseField.setBounds(274, 297, 220, 32);
        bg.add(houseField);

        medField = makeRateField();
        medField.setBounds(524, 297, 220, 32);
        bg.add(medField);

        // ── Buttons (y=425) ───────────────────────────────────────────
        JButton cancelBtn = makeSecondaryBtn("Cancel");
        cancelBtn.setBounds(402, 425, 110, 36);
        cancelBtn.addActionListener(e -> goBack());
        bg.add(cancelBtn);

        JButton saveBtn = makePrimaryBtn("Save Configuration", C_GREEN, C_GREEN_H);
        saveBtn.setBounds(528, 425, 170, 36);
        saveBtn.addActionListener(e -> saveConfig());
        bg.add(saveBtn);

        // ── Load current config ────────────────────────────────────────
        loadConfig();

        setVisible(true);
    }

    // ── Load rates from DB (convert decimal fraction → display %) ─────
    private void loadConfig() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.statement.executeQuery("SELECT config_key, config_value FROM payroll_config");
            while (rs.next()) {
                double pct = rs.getDouble("config_value") * 100.0;
                String val = String.valueOf(pct % 1 == 0 ? (int) pct : pct);
                switch (rs.getString("config_key")) {
                    case "tax_rate":     taxField.setText(val);   break;
                    case "pf_rate":      pfField.setText(val);    break;
                    case "house_rate":   houseField.setText(val); break;
                    case "medical_rate": medField.setText(val);   break;
                }
            }
            c.close();
        } catch (Exception ex) {
            taxField.setText("10"); pfField.setText("5");
            houseField.setText("10"); medField.setText("5");
        }
    }

    // ── Validate + save rates to DB ────────────────────────────────────
    private void saveConfig() {
        try {
            double tax   = parseRate(taxField,   "Income Tax");
            double pf    = parseRate(pfField,    "Provident Fund");
            double house = parseRate(houseField, "House Allowance");
            double med   = parseRate(medField,   "Medical Allowance");

            Conn c = new Conn();
            String upd = "INSERT INTO payroll_config (config_key,config_value) VALUES ('%s','%s')"
                       + " ON DUPLICATE KEY UPDATE config_value='%s'";
            c.statement.executeUpdate(String.format(upd, "tax_rate",     tax,   tax));
            c.statement.executeUpdate(String.format(upd, "pf_rate",      pf,    pf));
            c.statement.executeUpdate(String.format(upd, "house_rate",   house, house));
            c.statement.executeUpdate(String.format(upd, "medical_rate", med,   med));
            c.close();

            JOptionPane.showMessageDialog(this,
                "Configuration saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseRate(JTextField f, String label) {
        try {
            double v = Double.parseDouble(f.getText().trim());
            if (v < 0 || v > 100) throw new IllegalArgumentException(label + " must be between 0 and 100.");
            return v / 100.0;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + ": enter a valid number (e.g. 10 for 10%).");
        }
    }

    private void goBack() { dispose(); new PayrollHistory(); }

    // ── Helpers ────────────────────────────────────────────────────────
    private JTextField makeRateField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setForeground(TEXT_DARK);
        f.setBackground(new Color(248, 250, 253));
        f.setCaretColor(NAVY);
        f.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(ACCENT, 2),
                        BorderFactory.createEmptyBorder(0, 9, 0, 9)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(BORDER_COL, 1),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            }
        });
        return f;
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

    private JButton makePrimaryBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(WHITE);
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
