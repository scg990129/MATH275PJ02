import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class JFrameTSPDistanceMatrix extends JFrame {

    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel model;
    DefaultTableCellRenderer rightRenderer = new DefaultDoubleTableCellRenderer();

    public JFrameTSPDistanceMatrix(Object[][] tableData, String[] columnNames) {
        super("TSP Distance Matrix");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(800, 600);
        setLocationRelativeTo(null);

//        for (int i = 0; i < tableData.length; i++) {
//            for (int j = 0; j < tableData[i].length; j++) {
//                if (tableData[i][j] instanceof Double) {
//                    tableData[i][j] = String.format("%4.4f", tableData[i][j]);
////                            String.valueOf(tableData[i][j]);
//                }
//            }
//        }

        this.model = new ReadOnlyTableModel(tableData, columnNames);
        JTable table = new JTable(model);
        // Beautify the table
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Set renderer for all numeric columns
        for (int col = 1; col < table.getColumnCount(); col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(rightRenderer);
        }

        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 210));
        add(scrollPane);

        this.pack();
//        setResizable(false);
        setVisible(true);
    }

}
