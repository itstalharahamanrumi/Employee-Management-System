package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class Splash extends JWindow {

    private int progress = 0;

    // ── Palette ──────────────────────────────────────────────────────────
    private static final Color BG_DARK   = new Color(  8,  16,  42);   // near-black navy
    private static final Color BG_MID    = new Color( 18,  36,  82);   // deep navy
    private static final Color BG_NAVY   = new Color( 26,  54, 105);   // main navy
    private static final Color ACCENT    = new Color( 66, 153, 225);   // sky blue
    private static final Color ACCENT2   = new Color( 38, 110, 230);   // vivid blue
    private static final Color WHITE     = Color.WHITE;
    private static final Color CARD_BG   = new Color(255, 255, 255);
    private static final Color TEXT_HEAD = new Color( 12,  24,  58);   // very dark navy
    private static final Color TEXT_SUB  = new Color( 90, 108, 148);   // muted navy-gray
    private static final Color TEXT_TINY = new Color(160, 175, 210);
    private static final Color BAR_TRACK = new Color(220, 228, 245);
    private static final Color SEP       = new Color(225, 231, 245);

    Splash() {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

                int W = getWidth(), H = getHeight(), cx = W / 2, cy = H / 2;

                // ── 1. Dark navy gradient background ──────────────────────
                g2.setPaint(new GradientPaint(0, 0, BG_DARK, W, H, BG_MID));
                g2.fillRect(0, 0, W, H);

                // ── 2. Decorative rings — top right ───────────────────────
                g2.setColor(new Color(66, 153, 225, 12));
                g2.setStroke(new BasicStroke(40f));
                g2.drawOval(W - 260, -130, 350, 350);
                g2.setColor(new Color(66, 153, 225, 7));
                g2.setStroke(new BasicStroke(60f));
                g2.drawOval(W - 340, -210, 520, 520);

                // ── 3. Decorative rings — bottom left ─────────────────────
                g2.setColor(new Color(100, 160, 255, 10));
                g2.setStroke(new BasicStroke(35f));
                g2.drawOval(-140, H - 220, 300, 300);
                g2.setColor(new Color(100, 160, 255, 6));
                g2.setStroke(new BasicStroke(50f));
                g2.drawOval(-220, H - 340, 480, 480);
                g2.setStroke(new BasicStroke(1f));

                // ── 4. Card shadow ────────────────────────────────────────
                int cW = 480, cH = 320, cX = cx - cW / 2, cY = cy - cH / 2 - 16;
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fill(new RoundRectangle2D.Float(cX + 8, cY + 10, cW, cH, 24, 24));
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fill(new RoundRectangle2D.Float(cX + 4, cY + 5, cW, cH, 24, 24));

                // ── 5. Card body ──────────────────────────────────────────
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(cX, cY, cW, cH, 22, 22));

                // ── 6. Card top accent bar ────────────────────────────────
                g2.setPaint(new GradientPaint(cX, 0, ACCENT2, cX + cW, 0, ACCENT));
                g2.fill(new RoundRectangle2D.Float(cX, cY, cW, 4, 22, 22));
                g2.fillRect(cX, cY + 2, cW, 6);  // flatten bottom of accent

                // ── 7. App icon ───────────────────────────────────────────
                int iS = 72, iX = cx - iS / 2, iY = cY + 34;
                // Glow rings
                g2.setColor(new Color(38, 110, 230, 20));
                g2.fillOval(iX - 10, iY - 10, iS + 20, iS + 20);
                g2.setColor(new Color(38, 110, 230, 10));
                g2.fillOval(iX - 20, iY - 20, iS + 40, iS + 40);
                // Icon background gradient
                g2.setPaint(new GradientPaint(iX, iY, ACCENT2, iX + iS, iY + iS, BG_NAVY));
                g2.fill(new RoundRectangle2D.Float(iX, iY, iS, iS, 18, 18));
                // White "E"
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 30));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("E", cx - fm.stringWidth("E") / 2, iY + 48);

                // ── 8. App title ──────────────────────────────────────────
                g2.setColor(TEXT_HEAD);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                fm = g2.getFontMetrics();
                String title = "Employee Management System";
                g2.drawString(title, cx - fm.stringWidth(title) / 2, iY + iS + 38);

                // ── 9. Subtitle row ───────────────────────────────────────
                g2.setColor(TEXT_SUB);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                fm = g2.getFontMetrics();
                String sub = "HR Management  ·  Payroll  ·  Analytics";
                g2.drawString(sub, cx - fm.stringWidth(sub) / 2, iY + iS + 60);

                // ── 10. Divider inside card ───────────────────────────────
                int divY = iY + iS + 78;
                g2.setColor(SEP);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(cX + 40, divY, cX + cW - 40, divY);

                // ── 11. Status text + percentage ──────────────────────────
                int barY = cY + cH - 38;
                String[] steps = {
                    "Initializing system...",
                    "Loading UI components...",
                    "Connecting to database...",
                    "Fetching employee records...",
                    "Preparing dashboard...",
                    "Ready to launch."
                };
                String step = steps[Math.min(progress / 17, steps.length - 1)];
                g2.setColor(TEXT_SUB);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                fm = g2.getFontMetrics();
                g2.drawString(step, cX + 28, barY - 8);
                String pct = progress + "%";
                g2.setColor(ACCENT2);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                fm = g2.getFontMetrics();
                g2.drawString(pct, cX + cW - 28 - fm.stringWidth(pct), barY - 8);

                // ── 12. Progress bar ──────────────────────────────────────
                int barX = cX + 28, barW = cW - 56;
                // Track
                g2.setColor(BAR_TRACK);
                g2.fill(new RoundRectangle2D.Float(barX, barY, barW, 7, 7, 7));
                // Fill with gradient
                int fillW = (int)(barW * progress / 100.0);
                if (fillW > 4) {
                    g2.setPaint(new GradientPaint(barX, 0, ACCENT2, barX + fillW, 0, ACCENT));
                    g2.fill(new RoundRectangle2D.Float(barX, barY, fillW, 7, 7, 7));
                    // Bright tip
                    g2.setColor(new Color(200, 230, 255, 180));
                    g2.fill(new RoundRectangle2D.Float(barX + fillW - 6, barY + 1, 5, 5, 5, 5));
                }

                // ── 13. Footer (below card, on dark bg) ───────────────────
                g2.setColor(new Color(255, 255, 255, 55));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                String copy = "© 2025 Employee Management System  ·  v1.0.0";
                g2.drawString(copy, cx - fm.stringWidth(copy) / 2, cY + cH + 38);

                // ── 14. Top accent line (full width) ──────────────────────
                g2.setPaint(new GradientPaint(0, 0, BG_NAVY, W / 2, 0, ACCENT));
                g2.fillRect(0, 0, W / 2, 3);
                g2.setPaint(new GradientPaint(W / 2, 0, ACCENT, W, 0, BG_NAVY));
                g2.fillRect(W / 2, 0, W / 2, 3);
            }
        };

        panel.setBackground(BG_DARK);
        panel.setPreferredSize(new Dimension(1100, 700));
        panel.setLayout(null);
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // ── Progress: 0 → 100 over 5 seconds (50ms × 100 ticks) ──────────
        Timer progressTimer = new Timer(50, e -> {
            if (progress < 100) { progress++; panel.repaint(); }
        });
        progressTimer.start();

        // ── Open Login after 5 seconds ────────────────────────────────────
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
