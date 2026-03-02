package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.MultipleGradientPaint.CycleMethod;

public class Splash extends JWindow {

    private int progress = 0;

    // ── Colour palette ─────────────────────────────────────────────────
    private static final Color BG_TOP       = new Color(10,  20,  45);
    private static final Color BG_BOTTOM    = new Color(18,  45,  85);
    private static final Color ACCENT_1     = new Color(0,  140, 255);
    private static final Color ACCENT_2     = new Color(0,  220, 190);
    private static final Color TEXT_WHITE   = new Color(240, 245, 255);
    private static final Color TEXT_MUTED   = new Color(120, 170, 220);
    private static final Color TEXT_DIM     = new Color( 70, 110, 160);
    private static final Color GLOW         = new Color(0,  140, 255,  35);
    private static final Color CARD_BG      = new Color(255, 255, 255,  12);
    private static final Color BAR_TRACK    = new Color(255, 255, 255,  25);

    Splash() {

        // ── Custom painted panel ──────────────────────────────────────
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);

                int W = getWidth(), H = getHeight(), cx = W / 2;

                // ── 1. Background gradient ────────────────────────────
                g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, H, BG_BOTTOM));
                g2.fillRect(0, 0, W, H);

                // ── 2. Decorative glow orbs ───────────────────────────
                g2.setColor(GLOW);
                g2.fillOval(-120, -120, 420, 420);
                g2.fillOval(W - 200, H - 200, 380, 380);
                g2.fillOval(cx - 180, 40, 360, 260);

                // ── 3. Thin top accent line ───────────────────────────
                g2.setPaint(new LinearGradientPaint(cx - 200, 0, cx + 200, 0,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(0,0,0,0), ACCENT_1, new Color(0,0,0,0)}));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(cx - 200, 3, cx + 200, 3);

                // ── 4. Central card background ────────────────────────
                int cardX = cx - 220, cardY = 55, cardW = 440, cardH = 240;
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 20, 20));

                // ── 5. App icon — circle with "E" ─────────────────────
                int iconD = 74, iconX = cx - iconD / 2, iconY = 78;
                // outer glow ring
                g2.setPaint(new RadialGradientPaint(
                        cx, iconY + iconD / 2f, iconD,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 140, 255, 60), new Color(0, 140, 255, 0)}));
                g2.fillOval(cx - iconD, iconY - iconD / 2, iconD * 2, iconD * 2);
                // solid icon circle
                g2.setPaint(new GradientPaint(iconX, iconY, ACCENT_1, iconX + iconD, iconY + iconD, ACCENT_2));
                g2.fillOval(iconX, iconY, iconD, iconD);
                // "E" letter
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("E", cx - fm.stringWidth("E") / 2, iconY + 50);

                // ── 6. App title ──────────────────────────────────────
                g2.setColor(TEXT_WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 26));
                fm = g2.getFontMetrics();
                String title = "Employee Management System";
                g2.drawString(title, cx - fm.stringWidth(title) / 2, iconY + iconD + 42);

                // ── 7. Divider line under title ───────────────────────
                int divY = iconY + iconD + 54;
                g2.setPaint(new LinearGradientPaint(cx - 120, divY, cx + 120, divY,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(0,0,0,0), new Color(255,255,255,50), new Color(0,0,0,0)}));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(cx - 120, divY, cx + 120, divY);

                // ── 8. Tagline ────────────────────────────────────────
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                fm = g2.getFontMetrics();
                String tag = "Manage  •  Track  •  Organize";
                g2.drawString(tag, cx - fm.stringWidth(tag) / 2, divY + 22);

                // ── 9. Three feature pills ────────────────────────────
                String[] pills = {"Add Employee", "View Records", "Update & Remove"};
                Color[]  pillC = {new Color(0,140,255,60), new Color(0,220,190,60), new Color(120,80,220,60)};
                int pillW = 120, pillH = 24, pillGap = 12;
                int totalPillW = pills.length * pillW + (pills.length - 1) * pillGap;
                int px = cx - totalPillW / 2;
                int py = divY + 42;
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                for (int i = 0; i < pills.length; i++) {
                    g2.setColor(pillC[i]);
                    g2.fill(new RoundRectangle2D.Float(px, py, pillW, pillH, pillH, pillH));
                    g2.setColor(TEXT_MUTED);
                    fm = g2.getFontMetrics();
                    g2.drawString(pills[i], px + (pillW - fm.stringWidth(pills[i])) / 2, py + 16);
                    px += pillW + pillGap;
                }

                // ── 10. Progress bar ──────────────────────────────────
                int barW = 380, barH = 5, barX = cx - barW / 2, barY = H - 68;
                // track
                g2.setColor(BAR_TRACK);
                g2.fill(new RoundRectangle2D.Float(barX, barY, barW, barH, barH, barH));
                // fill
                int fillW = Math.max(barH, (int)(barW * progress / 100.0));
                g2.setPaint(new GradientPaint(barX, barY, ACCENT_1, barX + fillW, barY, ACCENT_2));
                g2.fill(new RoundRectangle2D.Float(barX, barY, fillW, barH, barH, barH));
                // glowing dot at end of bar
                if (fillW > barH) {
                    int dotX = barX + fillW;
                    g2.setPaint(new RadialGradientPaint(dotX, barY + barH / 2f, 8,
                            new float[]{0f, 1f},
                            new Color[]{new Color(0, 220, 190, 200), new Color(0, 220, 190, 0)}));
                    g2.fillOval(dotX - 8, barY - 5, 16, 16);
                }

                // ── 11. Loading status text ───────────────────────────
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                fm = g2.getFontMetrics();
                String[] statuses = {
                    "Initializing...", "Loading modules...", "Connecting to database...",
                    "Loading resources...", "Almost ready...", "Starting up..."
                };
                String status = statuses[Math.min(progress / 17, statuses.length - 1)];
                g2.drawString(status, cx - fm.stringWidth(status) / 2, H - 48);

                // ── 12. Bottom footer ─────────────────────────────────
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("v1.0.0", barX, H - 18);
                String copy = "© 2025  Employee Management System";
                fm = g2.getFontMetrics();
                g2.drawString(copy, cx - fm.stringWidth(copy) / 2, H - 18);

                // ── 13. Bottom accent line ────────────────────────────
                g2.setPaint(new LinearGradientPaint(cx - 200, H - 2, cx + 200, H - 2,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(0,0,0,0), ACCENT_2, new Color(0,0,0,0)}));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(cx - 200, H - 2, cx + 200, H - 2);
            }
        };

        panel.setPreferredSize(new Dimension(620, 380));
        panel.setLayout(null);
        add(panel);
        pack();
        setLocationRelativeTo(null);    // centre on screen
        setVisible(true);

        // ── Animated progress bar: 0 → 100 over ~5 seconds ───────────
        // 5000ms / 50ms per tick = 100 ticks → progress++ each tick = 100%
        Timer progressTimer = new Timer(50, e -> {
            if (progress < 100) {
                progress++;
                panel.repaint();
            }
        });
        progressTimer.start();

        // ── Launch Login after 5 seconds ──────────────────────────────
        Timer launchTimer = new Timer(5000, e -> {
            progressTimer.stop();
            dispose();
            new Login();
        });
        launchTimer.setRepeats(false);
        launchTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Splash::new);
    }
}
