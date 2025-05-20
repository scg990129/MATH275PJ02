import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;


import javax.swing.*;

public class AppPj02 {

    private static final String logDirectory = "logs";
    private static final Logger logger = Logger.getLogger(AppPj02.class.getName());
    private static final FormattedLogger formattedLogger = new FormattedLogger(logger);

    public static double[][] distance;
    public static Map<Character, City> cities;
    public static Map<City, SortedSet<City>> sortedAdjacency = new TreeMap<>();
    public static JFrame frameTSPDistanceMatrix;

    static {
        try {
            Path directoryPath = Paths.get(logDirectory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            FileHandler fh = new FileHandler(String.format("%s/%s%s.log", logDirectory,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS-"))
                    , AppPj02.class.getName() ), true); // true = append mode
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            logger.warning("Failed to set up file handler for logger: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        formattedLogger.logf("Option 2 of the Course Project: \nCombinatorial Optimization - \nThe Traveling Salesperson Problem (TSP)");
        formattedLogger.logf("We have the following cities: \n%s", String.format("%s",cities = City.getCitiesInstance()).replace("),", "),\n"));

        formattedLogger.logf("The distance between the cities is given by the following matrix:");
        distance = new double[cities.size()][cities.size()];

        for (Character keyRow : cities.keySet()) {
            City row = cities.get(keyRow);
            for (Character keyCol : cities.keySet()) {
                City col = cities.get(keyCol);
                distance[row.getLabelNo()][col.getLabelNo()] = City.distanceTo(row, col);
            }
        }

        StringBuilder sb = new StringBuilder().append('\n');
        for (int i = 0; i < distance.length; i++) {
//            System.out.printf("%s ", cities.keySet().toArray()[i]);
            sb.append(String.format("%s ", cities.keySet().toArray()[i]));
            for (int j = 0; j < distance[i].length; j++) {
                sb.append(String.format("%8.4f ", distance[i][j]));
//                System.out.printf("%8.4f ", distance[i][j]);
            }
            sb.append('\n');
        }
        formattedLogger.logf(sb.toString());

        formattedLogger.logf("The distance matrix is shown in the GUI.");
        SwingUtilities.invokeLater(AppPj02::createDistanceGIU);

        formattedLogger.logf("Create Adjacency list");
        for(City city : cities.values()) {
            SortedSet<City> adj = new TreeSet<>(new City.ComparatorCityDistance(city).reversed());
            for(City city1 : cities.values()) {
                if (city != city1)
                    adj.add(city1);
            }
            sortedAdjacency.put(city, adj);
        }

        sb.setLength(0);
        sb.append( "Adjacency list:\n");
        for (City city : sortedAdjacency.keySet()) {
            sb.append(String.format("%20s: ", city));

            sb.append(String.format("%s \n", String.join( "<<",
                    sortedAdjacency.get(city).stream()
                            .filter(city1 -> city1 != city)
                            .map(s->String.format("%20s ", s))
                            .toArray(String[]::new)
                    )));
        }
        formattedLogger.logf(sb.toString());

        formattedLogger.warningf("TSP algorithms: \n");
        formattedLogger.warningf("1. Brute Force Algorithm\n");
        LinkedHashSet<City> citiesSet = new LinkedHashSet<>(cities.values());

        // for(char label = 'A'; )


//        formattedLogger.warningf("2. Nearest Neighbor Algorithm (Greedy)\n");
//
//
//        formattedLogger.warningf("1. Nearest Neighbor Algorithm (Greedy)\n");
//        formattedLogger.warningf("2. Nearest Insertion Algorithm\n");
//        formattedLogger.warningf("3. Farthest Insertion Algorithm\n");
//        formattedLogger.warningf("4. Minimum Spanning Tree Algorithm\n");
//        formattedLogger.warningf("5. Brute Force Algorithm\n");

    }


    public static void createDistanceGIU() {
        String[] citiesTitle = new String[cities.size()];
        for (Character key : cities.keySet()) {
            citiesTitle[cities.get(key).getLabelNo()] = String.format("%s (%c)", cities.get(key).getName(), key);
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

        frameTSPDistanceMatrix = new JFrameTSPDistanceMatrix(tableData, columnNames);
    }

}
