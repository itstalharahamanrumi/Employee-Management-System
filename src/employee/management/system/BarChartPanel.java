package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Reusable bar-chart panel (vertical, grouped-vertical, or horizontal).
 * All rendering done with Graphics2D — no external libraries.
 */
public class BarChartPanel extends JPanel {

    public enum Mode { VERTICAL, GROUPED, HORIZONTAL }

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color BG_CHART  = Color.WHITE;
    private static final Color GRID      = new Color(234, 237, 245);
    private static final Color AXIS_LINE = new Color(200, 208, 224);
    private static final Color TEXT_LBL  = new Color(100, 115, 140);
    private static final Color TEXT_VAL  = new Color( 22,  36,  71);
    private static final Color TEXT_TITLE= new Color( 22,  36,  71);
    private static final Color EMPTY_CLR = new Color(180, 190, 205);

    // ── Data ───────────────────────────────────────────────────────────
    private ChartData data;
    private Mode      mode;

    // Padding (left, right, top, bottom)
    private int padL, padR, padT, padB;

    // ── Constructors ───────────────────────────────────────────────────
    public BarChartPanel(ChartData data, Mode mode) {
        this.data = data;
        this.mode = mode;
        setOpaque(true);
        setBackground(BG_CHART);
        padT = 38; padR = 20;
        padL = (mode == Mode.HORIZONTAL) ? 125 : 72;
        padB = (mode == Mode.HORIZONTAL) ?  24 :  56;
    }

    public void setData(ChartData d) { this.data = d; repaint(); }

    // ── Paint ──────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int W = getWidth(), H = getHeight();

        // Card background + border
        g2.setColor(BG_CHART);
        g2.fillRoundRect(0, 0, W, H, 8, 8);

        if (data == null || data.isEmpty()) {
            drawEmpty(g2, W, H);
            g2.dispose();
            return;
        }

        switch (mode) {
            case VERTICAL:  paintVertical(g2, W, H);  break;
            case GROUPED:   paintGrouped (g2, W, H);  break;
            case HORIZONTAL:paintHorizontal(g2, W, H);break;
        }
        g2.dispose();
    }

    // ── Vertical bar chart ─────────────────────────────────────────────
    private void paintVertical(Graphics2D g2, int W, int H) {
        int cx = padL, cy = padT;
        int cw = W - padL - padR;
        int ch = H - padT - padB;
        if (cw <= 0 || ch <= 0) return;

        double maxVal = maxOf(data.values);
        double yMax   = niceMax(maxVal);
        int    nTicks = 5;
        double yStep  = yMax / nTicks;

        // Title
        drawTitle(g2, data.title, W, padT);

        // Grid + Y-axis labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        for (int t = 0; t <= nTicks; t++) {
            double val = t * yStep;
            int    y   = cy + ch - (int)(val / yMax * ch);
            g2.setColor(GRID);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(cx, y, cx + cw, y);
            g2.setColor(TEXT_LBL);
            String lbl = formatY(val, yMax);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, cx - fm.stringWidth(lbl) - 6, y + fm.getAscent() / 2 - 1);
        }

        // Axes
        g2.setColor(AXIS_LINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx, cy, cx, cy + ch);
        g2.drawLine(cx, cy + ch, cx + cw, cy + ch);

        // Bars
        int n = data.labels.length;
        float groupW = (float) cw / n;
        float barW   = groupW * 0.62f;

        for (int i = 0; i < n; i++) {
            double val  = data.values[i];
            int    barH = (int)(val / yMax * ch);
            int    bx   = cx + (int)(i * groupW + (groupW - barW) / 2);
            int    by   = cy + ch - barH;

            // Bar fill + rounded top
            g2.setColor(data.color);
            g2.fill(new RoundRectangle2D.Float(bx, by, barW, barH + 4, 5, 5));
            // Square off the bottom half of the rounded rect
            if (barH > 6)
                g2.fillRect(bx, by + 4, (int)barW, barH - 4);

            // Value label
            if (barH > 14) {
                g2.setColor(TEXT_VAL);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                String vLbl = formatY(val, yMax);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(vLbl, bx + (int)barW/2 - fm.stringWidth(vLbl)/2, by - 3);
            }

            // X-axis label
            g2.setColor(TEXT_LBL);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String xLbl = data.labels[i];
            FontMetrics fm = g2.getFontMetrics();
            int lx = bx + (int)barW / 2 - fm.stringWidth(xLbl) / 2;
            g2.drawString(xLbl, lx, cy + ch + 14);
        }

        // Legend
        if (data.legend1 != null) drawLegend1(g2, data.legend1, data.color, W, H);
    }

    // ── Grouped bar chart ──────────────────────────────────────────────
    private void paintGrouped(Graphics2D g2, int W, int H) {
        int cx = padL, cy = padT;
        int cw = W - padL - padR;
        int ch = H - padT - padB;
        if (cw <= 0 || ch <= 0) return;

        double max1 = maxOf(data.values);
        double max2 = data.values2 != null ? maxOf(data.values2) : 0;
        double yMax = niceMax(Math.max(max1, max2));
        int nTicks  = 5;
        double yStep = yMax / nTicks;

        drawTitle(g2, data.title, W, padT);

        // Grid + Y labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        for (int t = 0; t <= nTicks; t++) {
            double val = t * yStep;
            int y = cy + ch - (int)(val / yMax * ch);
            g2.setColor(GRID);
            g2.drawLine(cx, y, cx + cw, y);
            g2.setColor(TEXT_LBL);
            String lbl = formatY(val, yMax);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, cx - fm.stringWidth(lbl) - 6, y + fm.getAscent()/2 - 1);
        }

        g2.setColor(AXIS_LINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx, cy, cx, cy + ch);
        g2.drawLine(cx, cy + ch, cx + cw, cy + ch);

        int n = data.labels.length;
        float groupW = (float) cw / n;
        float gap    = 2f;
        float barW   = (groupW * 0.72f - gap) / 2;

        for (int i = 0; i < n; i++) {
            float gx = cx + i * groupW + groupW * 0.14f;

            // Bar 1
            double v1  = data.values[i];
            int    h1  = (int)(v1 / yMax * ch);
            float  bx1 = gx;
            int    by1 = cy + ch - h1;
            if (h1 > 0) {
                g2.setColor(data.color);
                g2.fill(new RoundRectangle2D.Float(bx1, by1, barW, h1 + 3, 4, 4));
                if (h1 > 4) g2.fillRect((int)bx1, by1 + 3, (int)barW, h1 - 3);
            }

            // Bar 2
            if (data.values2 != null) {
                double v2  = data.values2[i];
                int    h2  = (int)(v2 / yMax * ch);
                float  bx2 = gx + barW + gap;
                int    by2 = cy + ch - h2;
                if (h2 > 0) {
                    g2.setColor(data.color2 != null ? data.color2 : Color.GRAY);
                    g2.fill(new RoundRectangle2D.Float(bx2, by2, barW, h2 + 3, 4, 4));
                    if (h2 > 4) g2.fillRect((int)bx2, by2 + 3, (int)barW, h2 - 3);
                }
            }

            // X label
            g2.setColor(TEXT_LBL);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            String xLbl = data.labels[i];
            FontMetrics fm = g2.getFontMetrics();
            float midX = gx + (data.values2 != null ? (barW * 2 + gap) / 2f : barW / 2f);
            g2.drawString(xLbl, (int)midX - fm.stringWidth(xLbl)/2, cy + ch + 14);
        }

        // Legend
        if (data.legend1 != null && data.legend2 != null)
            drawLegend2(g2, data.legend1, data.color, data.legend2, data.color2, W, H);
    }

    // ── Horizontal bar chart ───────────────────────────────────────────
    private void paintHorizontal(Graphics2D g2, int W, int H) {
        int cx = padL, cy = padT;
        int cw = W - padL - padR;
        int ch = H - padT - padB;
        if (cw <= 0 || ch <= 0) return;

        drawTitle(g2, data.title, W, padT);

        int n = data.labels.length;
        if (n == 0) { drawEmpty(g2, W, H); return; }

        double xMax    = niceMax(maxOf(data.values));
        int    nTicks  = 5;
        double xStep   = xMax / nTicks;

        // Vertical grid + X-axis labels at bottom
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        for (int t = 0; t <= nTicks; t++) {
            double val = t * xStep;
            int x = cx + (int)(val / xMax * cw);
            g2.setColor(GRID);
            g2.drawLine(x, cy, x, cy + ch);
            g2.setColor(TEXT_LBL);
            String lbl = formatY(val, xMax);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, x - fm.stringWidth(lbl)/2, cy + ch + 12);
        }

        // Axes
        g2.setColor(AXIS_LINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx, cy, cx, cy + ch);
        g2.drawLine(cx, cy + ch, cx + cw, cy + ch);

        float slotH = (float) ch / n;
        float barH  = slotH * 0.55f;

        for (int i = 0; i < n; i++) {
            double val = data.values[i];
            int    bw  = (int)(val / xMax * cw);
            int    by  = cy + (int)(i * slotH + (slotH - barH) / 2);

            // Bar
            if (bw > 0) {
                g2.setColor(data.color);
                g2.fill(new RoundRectangle2D.Float(cx, by, bw + 4, barH, 5, 5));
                if (bw > 5)
                    g2.fillRect(cx, by, bw, (int)barH);
            }

            // Category label (left side)
            g2.setColor(TEXT_LBL);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String lbl = data.labels[i];
            FontMetrics fm = g2.getFontMetrics();
            // Truncate if too wide
            while (fm.stringWidth(lbl) > padL - 10 && lbl.length() > 4)
                lbl = lbl.substring(0, lbl.length() - 1);
            g2.drawString(lbl, cx - fm.stringWidth(lbl) - 8,
                    by + (int)(barH/2) + fm.getAscent()/2 - 2);

            // Value at end of bar
            g2.setColor(TEXT_VAL);
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            String vLbl = formatY(val, xMax);
            g2.drawString(vLbl, cx + bw + 6, by + (int)(barH/2) + g2.getFontMetrics().getAscent()/2 - 2);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────
    private void drawTitle(Graphics2D g2, String title, int W, int padT) {
        if (title == null || title.isEmpty()) return;
        g2.setColor(TEXT_TITLE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, W/2 - fm.stringWidth(title)/2, padT - 6);
    }

    private void drawEmpty(Graphics2D g2, int W, int H) {
        g2.setColor(EMPTY_CLR);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String msg = "No data available";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, W/2 - fm.stringWidth(msg)/2, H/2 + fm.getAscent()/2);
        if (data != null && data.title != null) drawTitle(g2, data.title, W, padT);
    }

    private void drawLegend1(Graphics2D g2, String l1, Color c1, int W, int H) {
        int lx = W - padR - 100, ly = padT + 2;
        g2.setColor(c1); g2.fillRect(lx, ly, 10, 10);
        g2.setColor(TEXT_LBL); g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.drawString(l1, lx + 14, ly + 9);
    }

    private void drawLegend2(Graphics2D g2, String l1, Color c1, String l2, Color c2, int W, int H) {
        int ly = H - padB + 22;
        int lx1 = W/2 - 80;
        g2.setColor(c1); g2.fillRoundRect(lx1, ly, 10, 10, 3, 3);
        g2.setColor(TEXT_LBL); g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.drawString(l1, lx1 + 13, ly + 9);
        int lx2 = lx1 + 80;
        if (c2 != null) g2.setColor(c2); g2.fillRoundRect(lx2, ly, 10, 10, 3, 3);
        g2.setColor(TEXT_LBL);
        g2.drawString(l2 != null ? l2 : "", lx2 + 13, ly + 9);
    }

    private static double maxOf(double[] arr) {
        if (arr == null || arr.length == 0) return 1;
        double m = arr[0];
        for (double v : arr) if (v > m) m = v;
        return m == 0 ? 1 : m;
    }

    static double niceMax(double raw) {
        if (raw <= 0) return 10;
        double exp   = Math.pow(10, Math.floor(Math.log10(raw)));
        double norm  = raw / exp;
        double nice  = norm <= 1.5 ? 1.5 : norm <= 3 ? 3 : norm <= 7 ? 7 : 10;
        return nice * exp;
    }

    static String formatY(double v, double max) {
        if (max >= 1_000_000) return String.format("%.1fM", v / 1_000_000);
        if (max >= 10_000)   return String.format("%.0fK", v / 1_000);
        if (v == (long)v)    return String.valueOf((long)v);
        return String.format("%.1f", v);
    }
}
