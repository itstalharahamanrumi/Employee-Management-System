package employee.management.system;

import java.awt.Color;

/**
 * Immutable data container for chart panels.
 * labels  — X-axis category labels
 * values  — primary series (Y values)
 * values2 — optional secondary series for grouped charts (may be null)
 */
public class ChartData {

    public final String[] labels;
    public final double[] values;
    public final double[] values2;   // null for non-grouped
    public final String   title;
    public final String   legend1;
    public final String   legend2;   // null for non-grouped
    public final Color    color;
    public final Color    color2;    // null for non-grouped

    /** Single-series constructor */
    public ChartData(String[] labels, double[] values,
                     String title, String legend1, Color color) {
        this.labels  = labels;   this.values  = values;  this.values2 = null;
        this.title   = title;    this.legend1 = legend1; this.legend2 = null;
        this.color   = color;    this.color2  = null;
    }

    /** Two-series constructor (grouped bar chart) */
    public ChartData(String[] labels, double[] values, double[] values2,
                     String title, String legend1, String legend2,
                     Color color, Color color2) {
        this.labels  = labels;  this.values  = values;  this.values2 = values2;
        this.title   = title;   this.legend1 = legend1; this.legend2 = legend2;
        this.color   = color;   this.color2  = color2;
    }

    public boolean isEmpty() {
        return labels == null || labels.length == 0 || values == null || values.length == 0;
    }
}
