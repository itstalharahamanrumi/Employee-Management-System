package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class Splash extends JWindow {

    private int progress = 0;

    // ── Palette — white & navy, matches the Login screen ───────────────
    private static final Color BG          = new Color(255, 255, 255);  // pure white
    private static final Color NAVY        = new Color( 26,  54, 105);  // deep navy (same as Login left)
    private static final Color NAVY_LIGHT  = new Color( 66, 120, 200);  // lighter navy for icon
    private static final Color BORDER_COL  = new Color(220, 225, 235);  // light gray border
    private static final Color CARD_BG     = new Color(247, 249, 253);  // off-white card
    private static final Color TEXT_TITLE  = new Color( 20,  33,  61);  // very dark navy
    private static final Color TEXT_SUB    = new Color(100, 112, 135);  // medium gray
    private static final Color TEXT_TINY   = new Color(170, 178, 195);  // light gray
    private static final Color BAR_TRACK   = new Color(230, 234, 242);  // light track
    private static final Color BAR_FILL    = new Color( 26,  54, 105);  // navy fill

    Splash() {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

                int W = getWidth(), H = getHeight(), cx = W / 2;

                // ── 1. White background ───────────────────────────────
                g2.setColor(BG);
                g2.fillRect(0, 0, W, H);

                // ── 2. Outer border ───────────────────────────────────
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(0, 0, W - 1, H - 1);

                // ── 3. Navy top bar (4px) ─────────────────────────────
                g2.setColor(NAVY);
                g2.fillRect(0, 0, W, 4);

                // ── 4. Centered card ──────────────────────────────────
                int cW = 380, cH = 200, cX = cx - cW / 2, cY = 28;
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(cX, cY, cW, cH, 14, 14));
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(cX, cY, cW, cH, 14, 14));

                // ── 5. App icon — navy rounded square ─────────────────
                int iS = 54, iX = cx - iS / 2, iY = cY + 26;
                // subtle shadow behind icon
                g2.setColor(new Color(26, 54, 105, 18));
                g2.fill(new RoundRectangle2D.Float(iX + 3, iY + 4, iS, iS, 14, 14));
                // icon background
                g2.setColor(NAVY);
                g2.fill(new RoundRectangle2D.Float(iX, iY, iS, iS, 13, 13));
                // white "E"
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("E", cx - fm.stringWidth("E") / 2, iY + 37);

                // ── 6. App title ──────────────────────────────────────
                g2.setColor(TEXT_TITLE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                fm = g2.getFontMetrics();
                String title = "Employee Management System";
                g2.drawString(title, cx - fm.stringWidth(title) / 2, iY + iS + 34);

                // ── 7. Subtitle ───────────────────────────────────────
                g2.setColor(TEXT_SUB);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                fm = g2.getFontMetrics();
                String sub = "Human Resource & Administration";
                g2.drawString(sub, cx - fm.stringWidth(sub) / 2, iY + iS + 54);

                // ── 8. Divider ────────────────────────────────────────
                int divY = iY + iS + 68;
                g2.setColor(BORDER_COL);
                g2.drawLine(cX + 30, divY, cX + cW - 30, divY);

                // ── 9. Version tag ────────────────────────────────────
                g2.setColor(TEXT_TINY);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                String ver = "v1.0.0  ·  Java  ·  Swing  ·  MySQL";
                g2.drawString(ver, cx - fm.stringWidth(ver) / 2, divY + 18);

                // ── 10. Status text + percentage ──────────────────────
                int barY = H - 46;
                g2.setColor(TEXT_SUB);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                fm = g2.getFontMetrics();
                String[] steps = {
                    "Initializing...",
                    "Loading components...",
                    "Connecting to database...",
                    "Fetching records...",
                    "Almost ready...",
                    "Ready."
                };
                String step = steps[Math.min(progress / 17, steps.length - 1)];
                g2.drawString(step, cX, barY - 10);
                String pct = progress + "%";
                g2.setColor(NAVY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                fm = g2.getFontMetrics();
                g2.drawString(pct, cX + cW - fm.stringWidth(pct), barY - 10);

                // ── 11. Progress bar (spans full width) ───────────────
                g2.setColor(BAR_TRACK);
                g2.fillRect(0, barY, W, 3);
                int fillW = (int)(W * progress / 100.0);
                g2.setColor(BAR_FILL);
                g2.fillRect(0, barY, fillW, 3);

                // ── 12. Footer ────────────────────────────────────────
                g2.setColor(TEXT_TINY);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                String copy = "© 2025 Employee Management System";
                g2.drawString(copy, cx - fm.stringWidth(copy) / 2, H - 16);
            }
        };

        panel.setBackground(BG);
        panel.setPreferredSize(new Dimension(520, 310));
        panel.setLayout(null);
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // ── Progress: 0 → 100 over 5 seconds (50ms × 100 ticks) ─────
        Timer progressTimer = new Timer(50, e -> {
            if (progress < 100) { progress++; panel.repaint(); }
        });
        progressTimer.start();

        // ── Open Login after 5 seconds — no ghost window ─────────────
        Timer launchTimer = new Timer(5000, e -> {
            progressTimer.stop();
            setVisible(false);
            dispose();
            SwingUtilities.invokeLater(Login::new);
        });
        launchTimer.setRepeats(false);
        launchTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Splash::new);
    }
}
