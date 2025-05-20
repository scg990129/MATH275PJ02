import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class AppPj02 {

    public static double[][] distance;
    public static Map<String, City> cities;

    public static void main(String[] args) {
        System.out.println("Option 2 of the Course Project: \nCombinatorial Optimization - \nThe Traveling Salesperson Problem (TSP)");
        System.out.println("We have the following cities:");
        System.out.println(cities = City.getCitiesInstance());

        System.out.println("The distance between the cities is given by the following matrix:");
        distance = new double[cities.size()][cities.size()];

        for (String keyRow : cities.keySet()) {
            City row = cities.get(keyRow);
            for (String keyCol : cities.keySet()) {
                City col = cities.get(keyCol);
                distance[row.getLabelNo()][col.getLabelNo()] = row.distanceTo(col);
            }
        }

        for (int i = 0; i < distance.length; i++) {
            System.out.printf("%s ", cities.keySet().toArray()[i]);
            for (int j = 0; j < distance[i].length; j++) {
                System.out.printf("%8.4f ", distance[i][j]);
            }
            System.out.println();
        }

        SwingUtilities.invokeLater(AppPj02::createDistanceGIU);

    }

    public static void createDistanceGIU() {
        String[] citiesTitle = new String[cities.size()];
        for (String key : cities.keySet()) {
            citiesTitle[cities.get(key).getLabelNo()] = String.format("%s (%s)", cities.get(key).getName(), key);
        }

        // Prepare column headers (first column is empty for row headers)
        String[] columnNames = new String[cities.size() + 1];
        columnNames[0] = "";
        System.arraycopy(citiesTitle, 0, columnNames, 1, cities.size());

        Object[][] tableData = new Object[citiesTitle.length][citiesTitle.length + 1];
        for (int i = 0; i < citiesTitle.length; i++) {
            tableData[i][0] = citiesTitle[i]; // row header
            for (int j = 0; j < citiesTitle.length; j++) {
                tableData[i][j + 1] = distance[i][j];
            }
        }

        JFrame frame = new JFrameTSPDistanceMatrix(tableData, columnNames);
    }

}
