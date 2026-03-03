package employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for exporting a DefaultTableModel to a CSV file.
 * The last column is automatically skipped if its header is empty (3-dot actions column).
 */
public class CsvExporter {

    /**
     * Opens a JFileChooser, exports the model, then shows a success toast.
     *
     * @param parent       Parent window (for dialog + toast positioning)
     * @param model        Table model to export
     * @param suggestedName Base filename (timestamp + .csv appended automatically)
     */
    public static void export(Window parent, DefaultTableModel model, String suggestedName) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export as CSV");
        fc.setSelectedFile(new File(desktopDir(), suggestedName + "_" + timestamp() + ".csv"));

        Component comp = (parent instanceof Component) ? (Component) parent : null;
        if (fc.showSaveDialog(comp) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().endsWith(".csv"))
            file = new File(file.getAbsolutePath() + ".csv");

        // Determine how many columns to export (skip trailing empty-header cols)
        int colCount = model.getColumnCount();
        while (colCount > 0 && (model.getColumnName(colCount - 1) == null
                               || model.getColumnName(colCount - 1).isEmpty())) {
            colCount--;
        }

        try (FileWriter fw = new FileWriter(file)) {
            // Header row
            StringBuilder row = new StringBuilder();
            for (int c = 0; c < colCount; c++) {
                if (c > 0) row.append(",");
                row.append(escape(model.getColumnName(c)));
            }
            fw.write(row + "\n");

            // Data rows
            for (int r = 0; r < model.getRowCount(); r++) {
                row.setLength(0);
                for (int c = 0; c < colCount; c++) {
                    if (c > 0) row.append(",");
                    Object val = model.getValueAt(r, c);
                    row.append(escape(val != null ? val.toString() : ""));
                }
                fw.write(row + "\n");
            }

            final File finalFile = file;
            ToastNotification.show(parent,
                "Exported " + model.getRowCount() + " records → " + finalFile.getName(),
                ToastNotification.Type.SUCCESS);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(comp,
                "Export failed: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── CSV value escaping ─────────────────────────────────────────────
    private static String escape(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n"))
            return "\"" + v.replace("\"", "\"\"") + "\"";
        return v;
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
    }

    private static File desktopDir() {
        return new File(System.getProperty("user.home"), "Desktop");
    }
}
