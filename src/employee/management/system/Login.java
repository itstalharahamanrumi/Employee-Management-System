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

    // ── Palette ──────────────────────────────────────────────────────────
    private static final Color NAVY        = new Color(  8,  18,  48);  // near-black navy
    private static final Color NAVY_MID    = new Color( 18,  40,  92);  // deep navy
    private static final Color NAVY_MAIN   = new Color( 26,  54, 105);  // main navy
    private static final Color ACCENT      = new Color( 66, 153, 225);  // sky blue
    private static final Color ACCENT_DARK = new Color( 38, 110, 220);  // vivid blue
    private static final Color WHITE       = Color.WHITE;
    private static final Color TEXT_DARK   = new Color( 14,  24,  55);  // very dark
    private static final Color TEXT_GRAY   = new Color( 95, 110, 140);  // medium gray
    private static final Color TEXT_LIGHT  = new Color(155, 170, 200);  // light gray
    private static final Color INPUT_BG    = new Color(250, 251, 254);  // off-white
    private static final Color INPUT_BDR   = new Color(210, 218, 235);  // light border
    private static final Color INPUT_FOCUS = new Color( 66, 153, 225);  // accent focus
    private static final Color DIVIDER     = new Color(230, 236, 248);

    // Left panel width
    private static final int SPLIT = 400;

    Login() {
        setTitle("Employee Management System — Login");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);   // borderless for a modern, premium look

        // ── Full-window background panel ──────────────────────────────────
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

                int W = getWidth(), H = getHeight();

                // ── Right: pure white ──────────────────────────────────────
                g2.setColor(WHITE);
                g2.fillRect(SPLIT, 0, W - SPLIT, H);

                // ── Left: deep navy gradient ───────────────────────────────
                g2.setPaint(new GradientPaint(0, 0, NAVY, 0, H, NAVY_MID));
                g2.fillRect(0, 0, SPLIT, H);

                // ── Left: decorative rings — top right of panel ────────────
                g2.setColor(new Color(100, 160, 255, 10));
                g2.setStroke(new BasicStroke(32f));
                g2.drawOval(SPLIT - 120, -80, 220, 220);
                g2.setColor(new Color(100, 160, 255, 6));
                g2.setStroke(new BasicStroke(48f));
                g2.drawOval(SPLIT - 190, -160, 360, 360);

                // ── Left: decorative rings — bottom left ───────────────────
                g2.setColor(new Color(100, 160, 255, 8));
                g2.setStroke(new BasicStroke(28f));
                g2.drawOval(-80, H - 160, 220, 220);
                g2.setColor(new Color(100, 160, 255, 5));
                g2.setStroke(new BasicStroke(44f));
                g2.drawOval(-160, H - 280, 380, 380);
                g2.setStroke(new BasicStroke(1f));

                // ── Left: small dot cluster (top-left area) ────────────────
                int[] dotX = {30, 54, 78, 30, 54, 78, 30, 54, 78};
                int[] dotY = {24, 24, 24, 48, 48, 48, 72, 72, 72};
                for (int i = 0; i < dotX.length; i++) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillOval(dotX[i], dotY[i], 4, 4);
                }

                // ── Left: icon background glow ─────────────────────────────
                int iS = 80, iX = SPLIT / 2 - iS / 2, iY = 200;
                g2.setColor(new Color(66, 153, 225, 22));
                g2.fillOval(iX - 14, iY - 14, iS + 28, iS + 28);
                g2.setColor(new Color(66, 153, 225, 10));
                g2.fillOval(iX - 28, iY - 28, iS + 56, iS + 56);
                // Icon square
                g2.setPaint(new GradientPaint(iX, iY, ACCENT_DARK, iX + iS, iY + iS, NAVY_MAIN));
                g2.fill(new RoundRectangle2D.Float(iX, iY, iS, iS, 22, 22));
                // Icon border shine
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(iX + 1, iY + 1, iS - 2, iS - 2, 21, 21));
                g2.setStroke(new BasicStroke(1f));
                // "E" letter
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("E", SPLIT / 2 - fm.stringWidth("E") / 2, iY + 54);

                // ── Left: App name ─────────────────────────────────────────
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 17));
                fm = g2.getFontMetrics();
                g2.drawString("Employee", SPLIT / 2 - fm.stringWidth("Employee") / 2, iY + iS + 32);
                g2.drawString("Management System", SPLIT / 2 - fm.stringWidth("Management System") / 2, iY + iS + 54);

                // ── Left: thin divider ─────────────────────────────────────
                int divY = iY + iS + 74;
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawLine(30, divY, SPLIT - 30, divY);

                // ── Left: feature list ─────────────────────────────────────
                String[] features = {
                    "Employee Records & Profiles",
                    "Leave Management System",
                    "Payroll Processing",
                    "Performance Analytics"
                };
                int fy = divY + 26;
                for (String feat : features) {
                    // bullet circle
                    g2.setColor(ACCENT);
                    g2.fillOval(26, fy - 8, 7, 7);
                    // feature text
                    g2.setColor(new Color(200, 215, 240));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    fm = g2.getFontMetrics();
                    g2.drawString(feat, 42, fy);
                    fy += 28;
                }

                // ── Left: version footer ───────────────────────────────────
                g2.setColor(new Color(255, 255, 255, 45));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                g2.drawString("v1.0.0  ·  Java Swing  ·  MySQL",
                        SPLIT / 2 - fm.stringWidth("v1.0.0  ·  Java Swing  ·  MySQL") / 2, H - 18);

                // ── Right: elegant vertical shadow on split edge ───────────
                GradientPaint shadow = new GradientPaint(
                        SPLIT, 0, new Color(0, 0, 30, 22),
                        SPLIT + 22, 0, new Color(0, 0, 0, 0));
                g2.setPaint(shadow);
                g2.fillRect(SPLIT, 0, 22, H);

                // ── Right: subtle top header strip ────────────────────────
                g2.setColor(new Color(245, 247, 252));
                g2.fillRect(SPLIT, 0, W - SPLIT, 3);
            }
        };
        bg.setBounds(0, 0, 1100, 700);
        bg.setLayout(null);
        add(bg);

        // ── Allow window drag (since it's undecorated) ────────────────────
        final Point[] dragPoint = {null};
        bg.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e)  { dragPoint[0] = e.getPoint(); }
            public void mouseReleased(MouseEvent e) { dragPoint[0] = null; }
        });
        bg.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragPoint[0] != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - dragPoint[0].x,
                                loc.y + e.getY() - dragPoint[0].y);
                }
            }
        });

        // ── Form positioning ──────────────────────────────────────────────
        int rightW = 1100 - SPLIT;                  // 700px
        int rX     = SPLIT + (rightW - 360) / 2;   // horizontal center in right panel
        int baseY  = 188;                           // vertical start (centered ~350)

        // ── "Welcome Back" heading ────────────────────────────────────────
        JLabel heading = new JLabel("Welcome Back");
        heading.setBounds(rX, baseY, 380, 38);
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(TEXT_DARK);
        bg.add(heading);

        // ── Subtitle ──────────────────────────────────────────────────────
        JLabel subtitle = new JLabel("Sign in to access the dashboard");
        subtitle.setBounds(rX, baseY + 40, 380, 20);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_GRAY);
        bg.add(subtitle);

        // ── Thin accent line below heading ────────────────────────────────
        JPanel accentLine = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, ACCENT_DARK, getWidth(), 0, new Color(66, 153, 225, 0)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        accentLine.setBounds(rX, baseY + 70, 60, 3);
        bg.add(accentLine);

        // ── USERNAME label + field ────────────────────────────────────────
        JLabel lblUser = new JLabel("USERNAME");
        lblUser.setBounds(rX, baseY + 96, 360, 16);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblUser.setForeground(TEXT_LIGHT);
        lblUser.setBackground(null);
        bg.add(lblUser);

        tusername = new JTextField();
        tusername.setBounds(rX, baseY + 114, 360, 44);
        styleInput(tusername);
        bg.add(tusername);

        // ── PASSWORD label + field ────────────────────────────────────────
        JLabel lblPass = new JLabel("PASSWORD");
        lblPass.setBounds(rX, baseY + 178, 360, 16);
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblPass.setForeground(TEXT_LIGHT);
        bg.add(lblPass);

        tpassword = new JPasswordField();
        tpassword.setBounds(rX, baseY + 196, 360, 44);
        styleInput(tpassword);
        bg.add(tpassword);

        // ── Sign In button (custom painted gradient) ──────────────────────
        login = new JButton("SIGN IN") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hovered ? new Color(50, 130, 235) : ACCENT_DARK;
                Color c2 = hovered ? ACCENT                   : new Color(55, 140, 235);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                // Shine strip at top
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() / 2, 10, 10));
                // Label
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
            }
        };
        login.setBounds(rX, baseY + 262, 360, 46);
        login.setOpaque(false);
        login.setContentAreaFilled(false);
        login.setBorderPainted(false);
        login.setFocusPainted(false);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        login.addActionListener(this);
        bg.add(login);

        // ── Exit link ─────────────────────────────────────────────────────
        back = new JButton("Exit");
        back.setBounds(rX + 130, baseY + 326, 100, 24);
        back.setBackground(WHITE);
        back.setForeground(TEXT_LIGHT);
        back.setFont(new Font("SansSerif", Font.PLAIN, 12));
        back.setBorder(BorderFactory.createEmptyBorder());
        back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(this);
        back.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { back.setForeground(NAVY_MAIN); }
            public void mouseExited(MouseEvent e)  { back.setForeground(TEXT_LIGHT); }
        });
        bg.add(back);

        // Press Enter to login
        getRootPane().setDefaultButton(login);

        setVisible(true);
    }

    // ── Helper: style an input field ─────────────────────────────────────
    private void styleInput(JTextField field) {
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_DARK);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setCaretColor(ACCENT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INPUT_BDR, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBackground(WHITE);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(INPUT_FOCUS, 2, true),
                        BorderFactory.createEmptyBorder(9, 13, 9, 13)));
            }
            public void focusLost(FocusEvent e) {
                field.setBackground(INPUT_BG);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(INPUT_BDR, 1, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
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
                    tpassword.setText("");
                    tpassword.requestFocus();
                    JOptionPane.showMessageDialog(this,
                            "Incorrect username or password.", "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
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

    // ── Shake animation ──────────────────────────────────────────────────
    private void shake(Component c) {
        Point origin = c.getLocation();
        Timer t = new Timer(28, null);
        final int[] count = {0};
        final int[] offsets = {-7, 7, -6, 6, -4, 4, -2, 2, 0};
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
