import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;


import javax.swing.*;

public class AppPj02 {

    private static final String logDirectory = "logs";
    private static final Logger logger = Logger.getLogger(AppPj02.class.getName());
    private static final FormattedLogger formattedLogger = new FormattedLogger(logger);
    private static final double epsilon = 1e-6;

    public static double[][] adjacencyDistanceMatrix;
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
        adjacencyDistanceMatrix = new double[cities.size()][cities.size()];

        for (Character keyRow : cities.keySet()) {
            City row = cities.get(keyRow);
            for (Character keyCol : cities.keySet()) {
                City col = cities.get(keyCol);
                adjacencyDistanceMatrix[row.getLabelNo()][col.getLabelNo()] = City.distanceTo(row, col);
            }
        }

        StringBuilder sb = new StringBuilder().append('\n');
        for (int i = 0; i < adjacencyDistanceMatrix.length; i++) {
//            System.out.printf("%s ", cities.keySet().toArray()[i]);
            sb.append(String.format("%s ", cities.keySet().toArray()[i]));
            for (int j = 0; j < adjacencyDistanceMatrix[i].length; j++) {
                sb.append(String.format("%8.4f ", adjacencyDistanceMatrix[i][j]));
//                System.out.printf("%8.4f ", distance[i][j]);
            }
            sb.append('\n');
        }
        formattedLogger.logf(sb.toString());

        formattedLogger.logf("The distance matrix is shown in the GUI.");
        SwingUtilities.invokeLater(AppPj02::createDistanceGIU);
        SwingUtilities.invokeLater(() -> {});
        javax.swing.SwingUtilities.invokeLater(() -> {
                    JFrameWeightedGraph app = new JFrameWeightedGraph(cities, adjacencyDistanceMatrix);});
//            JGraphTCityGraphVisualizer app = new JGraphTCityGraphVisualizer("TSP City Graph Visualization - Undirected AND Weight Graph", graph);
//            app.setVisible(true);
//        });

        formattedLogger.logf("Create Adjacency list");
        for(City city : cities.values()) {
            SortedSet<City> adj = new TreeSet<>(new City.ComparatorCityDistance(city));
            for(City city1 : cities.values()) {
                if (city != city1)
                    adj.add(city1);
            }
            sortedAdjacency.put(city, adj);
        }

        sb.setLength(0);
        sb.append( "Adjacency list:\n");
        for (City city : sortedAdjacency.keySet()) {
            sb.append(String.format("Vertex %20s: -> ", city));
            sb.append(String.format("[%s]\n", String.join( ", ",
                    sortedAdjacency.get(city).stream()
                            .filter(city1 -> city1 != city)
                            .map(s->String.format("%20s ", s))
                            .toArray(String[]::new)
                    )));
        }
        formattedLogger.logf(sb.toString());

        formattedLogger.logf("TSP algorithms: \n");
        formattedLogger.warningf("1. Brute Force Algorithm\n");

        SequencedSet<CitiesPathList> combinations = generatePermutations(new LinkedHashMap<>(cities));
        sb.setLength(0);
        List<CitiesPathList> bestCombination = new LinkedList<>();
        AtomicReference<Double> bestDistance = new AtomicReference<>();
        List<CitiesPathList> bestCombinationWithReturn = new LinkedList<>();
        AtomicReference<Double> bestDistanceWithReturn = new AtomicReference<>();
        getBestCombinationForBruteForce(combinations,
                bestDistance, bestCombination,
                bestDistanceWithReturn, bestCombinationWithReturn);
        combinations.stream()
                .map(p->String.format("path: %s (distance: %02.4f, with return (%s): %02.4f)",
                        p,p.getTotalDistance(),p.getFirst().getLabel(),p.getTotalDistanceWithReturn() ))
                .forEach(s -> sb.append(s).append('\n'));

        formattedLogger.infof("Brute Force Algorithm: : %d paths combinations of %d cities: \n%s\n",
                combinations.size(), cities.size(), sb.toString().trim());
        StringBuilder stringBuilder = new StringBuilder();
        for (CitiesPathList combination : bestCombinationWithReturn) {
            stringBuilder.append(String.format("%s (return to: %c): %6.4f\n",
                    combination.toString(), combination.getFirst().getLabel(),
                    bestDistanceWithReturn.get()
            ));
        }
        // D F E B C A
        formattedLogger.infof("\nBrute Force Algorithm: The best %d paths is (with distance: %6.4f):\n%s\n" +
                        "The best %d cycles for start and return: \n%s",
                bestCombination.size(), bestDistance.get().doubleValue(),
                bestCombination.toString().replace(",", ",\n"),
                bestCombinationWithReturn.size(), stringBuilder.toString().trim()
        );

        formattedLogger.infof("2. Sorted Adjacency link:%n%s",sortedAdjacency.toString().replace("],","],\n"));

//        CitiesPathList greedyPath = generatePermutationsNearestNeighborGreedy(sortedAdjacency, new LinkedHashMap<>(cities));
//        formattedLogger.infof("Double-Ended Nearest Neighbor Greedy Algorithm: %s (distance / with return: %6.4f / %6.4f)\n",
//                greedyPath.toString().replace(",", ",\n"),
//                        greedyPath.getTotalDistance(), greedyPath.getTotalDistanceWithReturn()
//                );

        AlgorithmNearestNeighbor algorithmNearestNeighbor = new AlgorithmNearestNeighbor(
                cities.get('A'), new LinkedHashMap<>(cities));
        formattedLogger.infof("3. Standard Nearest Neighbor Algorithm (Greedy)\n%s\n",
                algorithmNearestNeighbor.toString());

        // JFrameWeightedGraphWithMSP
        JFrameWeightedGraphWithMSP frameWeightedGraphWithMSP = new JFrameWeightedGraphWithMSP(cities);
        formattedLogger.infof("4. Prim-Based MST Approximation Algorithm%nCycle: %s%nTotal Distance: %02.4f", frameWeightedGraphWithMSP.getAlgorithmMSP(),frameWeightedGraphWithMSP.getAlgorithmMSP().getTotalDistance() );

    }

    public static void getBestCombinationForBruteForce(SequencedSet<CitiesPathList> permutations,
                                                       AtomicReference<Double> bestDistance, List<CitiesPathList> bestCombination,
                                                       AtomicReference<Double> bestDistanceWithReturn, List<CitiesPathList> bestCombinationWithReturn) {
        Optional<Double> bestDistanceTemp = Optional.empty();
        Optional<Double> bestDistanceWithReturnTemp = Optional.empty();

        for (CitiesPathList combination : permutations) {
            double totalDistance = combination.getTotalDistance();
            double totalDistanceWithReturn = combination.getTotalDistanceWithReturn();

            int doubleCompare = City.compareDistance(totalDistance, bestDistanceTemp.orElse(Double.MAX_VALUE));
            if (bestCombination.isEmpty() || doubleCompare <= 0) {
                if(doubleCompare != 0)
                    bestCombination.clear();

                bestCombination.add(combination);
                bestDistanceTemp = Optional.of(totalDistance);
            }
            doubleCompare = City.compareDistance(totalDistanceWithReturn, bestDistanceWithReturnTemp.orElse(Double.MAX_VALUE));
            if (bestCombinationWithReturn.isEmpty() || doubleCompare <= 0) {
                if(doubleCompare != 0){
                    bestCombinationWithReturn.clear();
                }

                bestCombinationWithReturn.add(combination);
                bestDistanceWithReturnTemp = Optional.of(totalDistanceWithReturn);
            }
        }
        bestDistance.set(bestDistanceTemp.orElse(Double.MAX_VALUE));
        bestDistanceWithReturn.set(bestDistanceWithReturnTemp.orElse(Double.MAX_VALUE));
    }

    // double-ended nearest neighbor heuristic.
    @Deprecated
    public static CitiesPathList generatePermutationsNearestNeighborGreedy(Map<City, SortedSet<City>> sortedAdjacency, SequencedMap<Character, City> availableCitiesList){
        SequencedMap<Character, City> temp = new LinkedHashMap<>(availableCitiesList);
        City city1 = null, city2 = null ;
        int bestDistance = Integer.MAX_VALUE, tempDistance;
        for(City city: sortedAdjacency.keySet()){
            City tempCity2 = sortedAdjacency.get(city).getFirst();
            tempDistance = City.distanceEigenValue(city, tempCity2);
            if (city1 == null || tempDistance < bestDistance) {
                bestDistance = tempDistance;
                city1 = city;
                city2 = tempCity2;
            }
        }
        temp.remove(city1 == null ? Character.MAX_VALUE :city1.getLabel());
        temp.remove(city2 == null ? Character.MAX_VALUE :city2.getLabel());

        // formattedLogger.infof("first 2 city: %c %c (DistanceEigenvalue: %d)\n", city1.getLabel(), city2.getLabel(), bestDistance);

        return generatePermutationsNearestNeighborGreedy(new CitiesPathList().addCity(city1).addCity(city2), temp);
    }

    @Deprecated
    public static CitiesPathList generatePermutationsNearestNeighborGreedy(CitiesPathList inputCities, SequencedMap<Character, City> availableCitiesList){
        if (availableCitiesList.isEmpty()) {
            return inputCities;
        }

        City firstCity = inputCities.getFirst();
        City lastCity = inputCities.getLast();
        int bestDistanceEigenvalue = Integer.MAX_VALUE;
        City selectedCity = null, targetCity = null;
        for(City temp: availableCitiesList.values()){
            int distance1 = City.distanceEigenValue(firstCity, temp);
            int distance2 = City.distanceEigenValue(lastCity, temp);
            // System.out.printf("Double %d", Double.compare(Double.MIN_VALUE , Double.MAX_VALUE));
            if ( distance1 < bestDistanceEigenvalue) {
                selectedCity = temp;
                targetCity = firstCity;
                bestDistanceEigenvalue = distance1;
            }
            if (distance2 < bestDistanceEigenvalue) {
                selectedCity = temp;
                targetCity = lastCity;
                bestDistanceEigenvalue = distance2;
            }
        }

//        if (targetCity == firstCity) {
//            formattedLogger.infof("add first: %s (DistanceEigenvalue: %d)\n", selectedCity.getLabel(), bestDistanceEigenvalue);
//            inputCities.addFirst(selectedCity);
//        }else {
//            formattedLogger.infof("add last: %s (DistanceEigenvalue: %d)\n", selectedCity.getLabel(), bestDistanceEigenvalue);
//            inputCities.addLast(selectedCity);
//        }
        availableCitiesList.remove(selectedCity == null? Character.MAX_VALUE : selectedCity.getLabel());

        return generatePermutationsNearestNeighborGreedy(inputCities, availableCitiesList);
    }

    public static SequencedSet<CitiesPathList> generatePermutations(SequencedMap<Character, City> availableCitiesList) {
        if (availableCitiesList.size() == 1) {
            CitiesPathList result = new CitiesPathList(availableCitiesList.values());
            SequencedSet<CitiesPathList> set = new LinkedHashSet<>();
            set.add(result);
            return set;
        }
        SequencedSet<CitiesPathList> result = new LinkedHashSet<>();
        Character[] labels = availableCitiesList.keySet().toArray(Character[]::new);
        for(Character c: labels){
            SequencedMap<Character, City> availableCitiesListCopy = new LinkedHashMap<>(availableCitiesList);
            City city = availableCitiesListCopy.remove(c);
            for(CitiesPathList path : generatePermutations(availableCitiesListCopy)){
                path.add(city);
                result.add(path);
            }
        }
        return result;
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
                tableData[i][j + 1] = adjacencyDistanceMatrix[i][j];
            }
        }

        frameTSPDistanceMatrix = new JFrameTSPDistanceMatrix(tableData, columnNames);
    }

}
