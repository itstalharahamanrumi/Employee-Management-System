package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Small, non-intrusive toast notification that auto-dismisses after 3.5 seconds.
 * Appears bottom-right of the parent window.
 * Usage:  ToastNotification.show(parentWindow, "message", ToastNotification.Type.SUCCESS);
 */
public class ToastNotification extends JWindow {

    public enum Type { SUCCESS, WARNING, ERROR, INFO }

    private final Color bgColor, fgColor, accentColor;

    /** Show a toast on the EDT; no return value needed. */
    public static void show(Window parent, String message, Type type) {
        SwingUtilities.invokeLater(() -> {
            try {
                ToastNotification toast = new ToastNotification(parent, message, type);
                toast.setVisible(true);
                // Auto-dismiss after 3500 ms
                Timer dismiss = new Timer(3500, e -> toast.dispose());
                dismiss.setRepeats(false);
                dismiss.start();
            } catch (Exception ignored) {
                // If toast creation fails, silently skip
            }
        });
    }

    private ToastNotification(Window parent, String message, Type type) {
        super(parent);

        switch (type) {
            case SUCCESS:
                bgColor    = new Color(220, 252, 231);
                fgColor    = new Color( 22, 101,  52);
                accentColor= new Color( 34, 197,  94);
                break;
            case WARNING:
                bgColor    = new Color(255, 247, 230);
                fgColor    = new Color(146,  64,  14);
                accentColor= new Color(217, 119,   6);
                break;
            case ERROR:
                bgColor    = new Color(254, 226, 226);
                fgColor    = new Color(153,  27,  27);
                accentColor= new Color(239,  68,  68);
                break;
            default: // INFO
                bgColor    = new Color(219, 234, 254);
                fgColor    = new Color( 29,  78, 216);
                accentColor= new Color( 59, 130, 246);
                break;
        }

        // ── Content panel (custom-painted) ─────────────────────────────
        JPanel content = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Rounded background
                g2.setColor(bgColor);
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 10, 10));
                // Accent border
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 10, 10));
                // Left accent strip
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Float(1, 1, 5, getHeight()-2, 5, 5));
            }
        };
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(370, 50));

        // Icon label
        String icon = type == Type.SUCCESS ? "✔" : type == Type.WARNING ? "⚠" : type == Type.ERROR ? "✖" : "ℹ";
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        iconLbl.setForeground(accentColor);
        iconLbl.setBounds(14, 0, 20, 50);
        content.add(iconLbl);

        // Message label
        JLabel msgLbl = new JLabel("<html><body style='width:295px'>" + message + "</body></html>");
        msgLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        msgLbl.setForeground(fgColor);
        msgLbl.setBounds(38, 5, 320, 40);
        content.add(msgLbl);

        setContentPane(content);
        pack();

        // Position at bottom-right of parent
        if (parent != null) {
            Rectangle pb = parent.getBounds();
            setLocation(pb.x + pb.width - getWidth() - 18,
                        pb.y + pb.height - getHeight() - 48);
        }
    }
}
