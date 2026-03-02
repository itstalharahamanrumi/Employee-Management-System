package employee.management.system;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.ResultSet;

public class Login extends JFrame implements ActionListener {

    JTextField     tusername;
    JPasswordField tpassword;
    JButton        login, back;

    // ── Palette ─────────────────────────────────────────────────────────
    private static final Color LEFT_BG      = new Color( 26,  54, 105); // deep navy
    private static final Color LEFT_DARK    = new Color( 18,  38,  76); // darker navy
    private static final Color ACCENT       = new Color( 66, 153, 225); // sky blue
    private static final Color WHITE        = Color.WHITE;
    private static final Color TEXT_DARK    = new Color( 30,  40,  55); // near black
    private static final Color TEXT_GRAY    = new Color(100, 110, 130); // medium gray
    private static final Color INPUT_BG     = new Color(248, 249, 252); // off-white
    private static final Color INPUT_BORDER = new Color(210, 215, 225); // light border
    private static final Color INPUT_FOCUS  = new Color( 66, 153, 225); // accent on focus
    private static final Color BTN_HOVER    = new Color( 45, 120, 200); // darker on hover

    Login() {
        setTitle("Employee Management System — Login");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ── Background panel (paints the split layout) ─────────────────
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                int W = getWidth(), H = getHeight();
                int split = 200; // left panel width

                // ── Right white panel ──────────────────────────────────
                g2.setColor(WHITE);
                g2.fillRect(split, 0, W - split, H);

                // ── Left navy panel ────────────────────────────────────
                g2.setPaint(new GradientPaint(0, 0, LEFT_BG, 0, H, LEFT_DARK));
                g2.fillRect(0, 0, split, H);

                // ── Left: decorative circle ────────────────────────────
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-60, H - 180, 240, 240);
                g2.fillOval(30, -60, 160, 160);

                // ── Left: icon square ──────────────────────────────────
                int iX = split / 2 - 28, iY = 70, iS = 56;
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fill(new RoundRectangle2D.Float(iX - 4, iY - 4, iS + 8, iS + 8, 16, 16));
                g2.setColor(ACCENT);
                g2.fill(new RoundRectangle2D.Float(iX, iY, iS, iS, 12, 12));
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("E", split / 2 - fm.stringWidth("E") / 2, iY + 38);

                // ── Left: app name ─────────────────────────────────────
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                fm = g2.getFontMetrics();
                g2.drawString("Employee", split / 2 - fm.stringWidth("Employee") / 2, iY + iS + 26);
                g2.drawString("Management", split / 2 - fm.stringWidth("Management") / 2, iY + iS + 44);
                g2.drawString("System", split / 2 - fm.stringWidth("System") / 2, iY + iS + 62);

                // ── Left: divider ──────────────────────────────────────
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(24, iY + iS + 78, split - 24, iY + iS + 78);

                // ── Left: version ──────────────────────────────────────
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                g2.drawString("v1.0.0", split / 2 - fm.stringWidth("v1.0.0") / 2, H - 20);

                // ── Right: vertical shadow on left edge ────────────────
                GradientPaint shadow = new GradientPaint(
                        split, 0, new Color(0, 0, 0, 18),
                        split + 18, 0, new Color(0, 0, 0, 0));
                g2.setPaint(shadow);
                g2.fillRect(split, 0, 18, H);
            }
        };
        bg.setBounds(0, 0, 600, 400);
        bg.setLayout(null);
        add(bg);

        // ── Right panel content ─────────────────────────────────────────
        int rX = 220; // right content start x

        // "Welcome Back" heading
        JLabel heading = new JLabel("Welcome Back");
        heading.setBounds(rX, 44, 340, 36);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(TEXT_DARK);
        bg.add(heading);

        // Subtitle
        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setBounds(rX, 80, 340, 20);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_GRAY);
        bg.add(subtitle);

        // ── Username ─────────────────────────────────────────────────────
        JLabel lblUser = new JLabel("USERNAME");
        lblUser.setBounds(rX, 124, 340, 16);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblUser.setForeground(TEXT_GRAY);
        bg.add(lblUser);

        tusername = new JTextField();
        tusername.setBounds(rX, 142, 320, 38);
        styleInput(tusername, INPUT_BORDER);
        bg.add(tusername);

        // ── Password ──────────────────────────────────────────────────────
        JLabel lblPass = new JLabel("PASSWORD");
        lblPass.setBounds(rX, 200, 340, 16);
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblPass.setForeground(TEXT_GRAY);
        bg.add(lblPass);

        tpassword = new JPasswordField();
        tpassword.setBounds(rX, 218, 320, 38);
        styleInput(tpassword, INPUT_BORDER);
        bg.add(tpassword);

        // ── Login button ─────────────────────────────────────────────────
        login = new JButton("SIGN IN");
        login.setBounds(rX, 282, 320, 42);
        login.setBackground(new Color(26, 54, 105));
        login.setForeground(WHITE);
        login.setFont(new Font("SansSerif", Font.BOLD, 13));
        login.setBorder(BorderFactory.createEmptyBorder());
        login.setFocusPainted(false);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        login.addActionListener(this);
        // hover effect
        login.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e)  { login.setBackground(ACCENT); }
            public void mouseExited(MouseEvent e)   { login.setBackground(new Color(26, 54, 105)); }
        });
        bg.add(login);

        // ── Exit link ─────────────────────────────────────────────────────
        back = new JButton("Exit");
        back.setBounds(rX + 110, 340, 100, 24);
        back.setBackground(WHITE);
        back.setForeground(TEXT_GRAY);
        back.setFont(new Font("SansSerif", Font.PLAIN, 11));
        back.setBorder(BorderFactory.createEmptyBorder());
        back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(this);
        back.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { back.setForeground(new Color(26, 54, 105)); }
            public void mouseExited(MouseEvent e)  { back.setForeground(TEXT_GRAY); }
        });
        bg.add(back);

        // Press Enter to login
        getRootPane().setDefaultButton(login);

        setVisible(true);
    }

    // ── Helper: style an input field cleanly ────────────────────────────
    private void styleInput(JTextField field, Color borderColor) {
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_DARK);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setCaretColor(new Color(26, 54, 105));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        // highlight border on focus
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(INPUT_FOCUS, 2, true),
                        BorderFactory.createEmptyBorder(7, 11, 7, 11)));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            String username = tusername.getText().trim();
            String password = new String(tpassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                shake(login);
                JOptionPane.showMessageDialog(this, "Please enter username and password.",
                        "Login", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Conn conn = new Conn();
                String query = "select * from login where username='" + username
                             + "' and password='" + password + "'";
                ResultSet rs = conn.statement.executeQuery(query);
                if (rs.next()) {
                    dispose();
                    new Main_class();
                } else {
                    shake(login);
                    JOptionPane.showMessageDialog(this,
                            "Wrong username or password.", "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                    tpassword.setText("");
                    tpassword.requestFocus();
                }
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Cannot connect to database.\nCheck MySQL is running.",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.exit(0);
        }
    }

    // ── Shake animation on wrong password ──────────────────────────────
    private void shake(Component c) {
        Point origin = c.getLocation();
        Timer t = new Timer(30, null);
        final int[] count = {0};
        final int[] offsets = {-6, 6, -5, 5, -3, 3, -1, 1, 0};
        t.addActionListener(ev -> {
            if (count[0] < offsets.length) {
                c.setLocation(origin.x + offsets[count[0]++], origin.y);
            } else {
                c.setLocation(origin);
                t.stop();
            }
        });
        t.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
