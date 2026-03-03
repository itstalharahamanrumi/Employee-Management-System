package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Reusable line chart panel with:
 * - Smooth Bezier curve
 * - Semi-transparent filled area under line
 * - Dot markers at each data point
 * - Hover tooltip via MouseMotionListener
 */
public class LineChartPanel extends JPanel {

    // ── Palette ────────────────────────────────────────────────────────
    private static final Color BG_CHART  = Color.WHITE;
    private static final Color GRID      = new Color(234, 237, 245);
    private static final Color AXIS_LINE = new Color(200, 208, 224);
    private static final Color TEXT_LBL  = new Color(100, 115, 140);
    private static final Color TEXT_DARK = new Color( 22,  36,  71);
    private static final Color TOOLTIP_BG= new Color( 26,  54, 105);
    private static final Color EMPTY_CLR = new Color(180, 190, 205);

    // ── Data ───────────────────────────────────────────────────────────
    private ChartData data;

    // ── Padding ─────────────────────────────────────────────────────────
    private static final int PAD_L = 72, PAD_R = 20, PAD_T = 38, PAD_B = 54;

    // ── Hover state ────────────────────────────────────────────────────
    private int   hoverIdx = -1;
    private int[] ptX, ptY; // screen coordinates of each data point

    public LineChartPanel(ChartData data) {
        this.data = data;
        setOpaque(true);
        setBackground(BG_CHART);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int prev = hoverIdx;
                hoverIdx = nearestPoint(e.getX(), e.getY());
                if (hoverIdx != prev) repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                hoverIdx = -1; repaint();
            }
        });
    }

    public void setData(ChartData d) { this.data = d; ptX = null; ptY = null; repaint(); }

    // ── Paint ──────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int W = getWidth(), H = getHeight();
        g2.setColor(BG_CHART);
        g2.fillRoundRect(0, 0, W, H, 8, 8);

        if (data == null || data.isEmpty()) {
            drawEmpty(g2, W, H);
            g2.dispose();
            return;
        }

        int cx = PAD_L, cy = PAD_T;
        int cw = W - PAD_L - PAD_R;
        int ch = H - PAD_T - PAD_B;
        if (cw <= 0 || ch <= 0) { g2.dispose(); return; }

        // Title
        if (data.title != null) {
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(data.title, W/2 - fm.stringWidth(data.title)/2, PAD_T - 6);
        }

        double maxVal = maxOf(data.values);
        double yMax   = BarChartPanel.niceMax(maxVal);
        int nTicks = 5;
        double yStep = yMax / nTicks;

        // Grid + Y labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        for (int t = 0; t <= nTicks; t++) {
            double val = t * yStep;
            int y = cy + ch - (int)(val / yMax * ch);
            g2.setColor(GRID);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(cx, y, cx + cw, y);
            g2.setColor(TEXT_LBL);
            String lbl = BarChartPanel.formatY(val, yMax);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lbl, cx - fm.stringWidth(lbl) - 6, y + fm.getAscent()/2 - 1);
        }

        // Axes
        g2.setColor(AXIS_LINE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(cx, cy, cx, cy + ch);
        g2.drawLine(cx, cy + ch, cx + cw, cy + ch);

        int n = data.values.length;
        ptX = new int[n];
        ptY = new int[n];

        float stepX = n > 1 ? (float)cw / (n - 1) : cw;
        for (int i = 0; i < n; i++) {
            ptX[i] = cx + (n == 1 ? cw/2 : (int)(i * stepX));
            ptY[i] = cy + ch - (int)(data.values[i] / yMax * ch);
        }

        // ── Filled area under the line ─────────────────────────────────
        if (n >= 2) {
            Path2D fillPath = buildCurvePath(n);
            fillPath.lineTo(ptX[n-1], cy + ch);
            fillPath.lineTo(ptX[0],   cy + ch);
            fillPath.closePath();

            Color base = data.color;
            g2.setPaint(new GradientPaint(
                    0, cy,      new Color(base.getRed(), base.getGreen(), base.getBlue(), 55),
                    0, cy + ch, new Color(base.getRed(), base.getGreen(), base.getBlue(),  8)));
            g2.fill(fillPath);
        }

        // ── Line curve ─────────────────────────────────────────────────
        if (n >= 2) {
            g2.setColor(data.color);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(buildCurvePath(n));
        }

        // ── Dots ───────────────────────────────────────────────────────
        for (int i = 0; i < n; i++) {
            boolean hover = (i == hoverIdx);
            int r = hover ? 7 : 4;
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.fillOval(ptX[i] - r, ptY[i] - r, r*2, r*2);
            g2.setColor(data.color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(ptX[i] - r, ptY[i] - r, r*2, r*2);
        }

        // ── X-axis labels ──────────────────────────────────────────────
        g2.setColor(TEXT_LBL);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        int labelStep = Math.max(1, n / 10);
        for (int i = 0; i < n; i += labelStep) {
            FontMetrics fm = g2.getFontMetrics();
            String lbl = data.labels[i];
            g2.drawString(lbl, ptX[i] - fm.stringWidth(lbl)/2, cy + ch + 14);
        }

        // ── Hover tooltip ──────────────────────────────────────────────
        if (hoverIdx >= 0 && hoverIdx < n) {
            int hx = ptX[hoverIdx], hy = ptY[hoverIdx];
            String tip = data.labels[hoverIdx] + ": " + BarChartPanel.formatY(data.values[hoverIdx], yMax);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(tip) + 14, th = 22;
            int tx = Math.min(hx - tw/2, W - tw - 4);
            int ty = Math.max(hy - th - 8, 4);
            tx = Math.max(tx, 4);
            g2.setColor(TOOLTIP_BG);
            g2.fill(new RoundRectangle2D.Float(tx, ty, tw, th, 6, 6));
            g2.setColor(Color.WHITE);
            g2.drawString(tip, tx + 7, ty + th/2 + fm.getAscent()/2 - 2);
        }

        // Legend
        if (data.legend1 != null) {
            int lx = W - PAD_R - 80, ly = PAD_T + 2;
            g2.setColor(data.color);
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawLine(lx, ly + 5, lx + 14, ly + 5);
            g2.setColor(TEXT_LBL);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.drawString(data.legend1, lx + 18, ly + 9);
        }

        g2.dispose();
    }

    // ── Smooth bezier path through all points ──────────────────────────
    private Path2D buildCurvePath(int n) {
        Path2D path = new Path2D.Float();
        path.moveTo(ptX[0], ptY[0]);
        for (int i = 1; i < n; i++) {
            float cpX1 = ptX[i-1] + (ptX[i] - ptX[i-1]) / 3f;
            float cpY1 = ptY[i-1];
            float cpX2 = ptX[i-1] + 2f * (ptX[i] - ptX[i-1]) / 3f;
            float cpY2 = ptY[i];
            path.curveTo(cpX1, cpY1, cpX2, cpY2, ptX[i], ptY[i]);
        }
        return path;
    }

    // ── Returns index of nearest data point to mouse (or -1) ──────────
    private int nearestPoint(int mx, int my) {
        if (ptX == null || ptY == null) return -1;
        int best = -1;
        double bestDist = 20 * 20; // 20px threshold squared
        for (int i = 0; i < ptX.length; i++) {
            double d = Math.pow(mx - ptX[i], 2) + Math.pow(my - ptY[i], 2);
            if (d < bestDist) { bestDist = d; best = i; }
        }
        return best;
    }

    private void drawEmpty(Graphics2D g2, int W, int H) {
        g2.setColor(EMPTY_CLR);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String msg = "No data available";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, W/2 - fm.stringWidth(msg)/2, H/2 + fm.getAscent()/2);
        if (data != null && data.title != null) {
            g2.setColor(TEXT_DARK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(data.title, W/2 - fmT.stringWidth(data.title)/2, PAD_T - 6);
        }
    }

    private static double maxOf(double[] arr) {
        if (arr == null || arr.length == 0) return 1;
        double m = arr[0];
        for (double v : arr) if (v > m) m = v;
        return m == 0 ? 1 : m;
    }
}
