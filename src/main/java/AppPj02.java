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
            sb.append(String.format("%20s: ", city));

            sb.append(String.format("%s \n", String.join( "<<",
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
        Optional<Double> bestDistance = Optional.empty();
        List<CitiesPathList> bestCombinationWithReturn = new LinkedList<>();
        Optional<Double> bestDistanceWithReturn = Optional.empty();
        for (CitiesPathList combination : combinations) {
            double totalDistance = combination.getTotalDistance();
            double totalDistanceWithReturn = combination.getTotalDistanceWithReturn();

            int doubleCompare = Double.compare(totalDistance, bestDistance.orElse(Double.MAX_VALUE));
            if (bestCombination.isEmpty() || doubleCompare <= 0) {
                if(doubleCompare != 0)
                    bestCombination.clear();

                bestCombination.add(combination);
                bestDistance = Optional.of(totalDistance);
            }
            doubleCompare = Double.compare(totalDistanceWithReturn, bestDistanceWithReturn.orElse(Double.MAX_VALUE));
            if (bestCombinationWithReturn.isEmpty() || doubleCompare <= 0) {
                if(doubleCompare != 0)
                    bestCombinationWithReturn.clear();

                bestCombinationWithReturn.add(combination);
                bestDistanceWithReturn = Optional.of(totalDistanceWithReturn);
            }

            sb.append(String.format("%s (return city: %c): %6.4f (with return: %6.4f)\n",
                    combination, combination.getFirst().getLabel(),
                    totalDistance, totalDistanceWithReturn
            ));
        }
        formattedLogger.infof("%d path combinations of %d cities: \n%s\n",
                combinations.size(), cities.size(), sb.toString().trim());
        StringBuilder stringBuilder = new StringBuilder();
        for (CitiesPathList combination : bestCombinationWithReturn) {
            stringBuilder.append(String.format("%s (return to: %c): %6.4f\n",
                    combination.toString(), combination.getFirst().getLabel(),
                    bestDistanceWithReturn.get().doubleValue()
            ));
        }
        formattedLogger.infof("\nThe best %d path is (with distance: %6.4f):\n%s\n" +
                        "The best %d path for start and return: \n%s",
                bestCombination.size(), bestDistance.get().doubleValue(),
                bestCombination.toString().replace(",", ",\n"),
                bestCombinationWithReturn.size(), stringBuilder.toString().trim()
        );

        formattedLogger.infof("2. Nearest Neighbor Algorithm (Greedy)");
        formattedLogger.infof("\n%s",sortedAdjacency.toString().replace("],","],\n"));

        CitiesPathList greedyPath = generatePermutationsNearestNeighborGreedy(sortedAdjacency, new LinkedHashMap<>(cities));
        formattedLogger.infof("Greedy path: %s (distance / with return: %6.4f / %6.4f)\n",
                greedyPath.toString().replace(",", ",\n"),
                        greedyPath.getTotalDistance(), greedyPath.getTotalDistanceWithReturn()
                );
//
//
//        formattedLogger.warningf("1. Nearest Neighbor Algorithm (Greedy)\n");
//        formattedLogger.warningf("2. Nearest Insertion Algorithm\n");
//        formattedLogger.warningf("3. Farthest Insertion Algorithm\n");
//        formattedLogger.warningf("4. Minimum Spanning Tree Algorithm\n");
//        formattedLogger.warningf("5. Brute Force Algorithm\n");

    }

    public static CitiesPathList generatePermutationsNearestNeighborGreedy(Map<City, SortedSet<City>> sortedAdjacency, SequencedMap<Character, City> availableCitiesList){
        SequencedMap<Character, City> temp = new LinkedHashMap<>(availableCitiesList);
        City city1 = null, city2 = null ;
        int bestDistance = Integer.MAX_VALUE, tempDistance;
        for(City city: sortedAdjacency.keySet()){
            City tempCity2 = sortedAdjacency.get(city).getFirst();
            tempDistance = City.distanceEigenvalue(city, tempCity2);
            if (city1 == null || city2 == null || tempDistance < bestDistance) {
                bestDistance = tempDistance;
                city1 = city;
                city2 = tempCity2;
            }
        }
        temp.remove(city1 == null ? Character.MAX_VALUE :city1.getLabel());
        temp.remove(city2 == null ? Character.MAX_VALUE :city2.getLabel());

        formattedLogger.infof("first 2 city: %c %c (DistanceEigenvalue: %d)\n", city1.getLabel(), city2.getLabel(), bestDistance);

        return generatePermutationsNearestNeighborGreedy(new CitiesPathList().addCity(city1).addCity(city2), temp);
    }

    public static CitiesPathList generatePermutationsNearestNeighborGreedy(CitiesPathList inputCities, SequencedMap<Character, City> availableCitiesList){
        if (availableCitiesList.isEmpty()) {
            return inputCities;
        }

        City firstCity = inputCities.getFirst();
        City lastCity = inputCities.getLast();
        int bestDistanceEigenvalue = Integer.MAX_VALUE;
        City selectedCity = null, targetCity = null;
        for(City temp: availableCitiesList.values()){
            int distance1 = City.distanceEigenvalue(firstCity, temp);
            int distance2 = City.distanceEigenvalue(lastCity, temp);
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

        if (targetCity == firstCity) {
            formattedLogger.infof("add first: %s (DistanceEigenvalue: %d)\n", selectedCity.getLabel(), bestDistanceEigenvalue);
            inputCities.addFirst(selectedCity);
        }else {
            formattedLogger.infof("add last: %s (DistanceEigenvalue: %d)\n", selectedCity.getLabel(), bestDistanceEigenvalue);
            inputCities.addLast(selectedCity);
        }
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
            for(CitiesPathList t : generatePermutations(availableCitiesListCopy)){
                t.add(city);
                result.add(t);
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
                tableData[i][j + 1] = distance[i][j];
            }
        }

        frameTSPDistanceMatrix = new JFrameTSPDistanceMatrix(tableData, columnNames);
    }

}
