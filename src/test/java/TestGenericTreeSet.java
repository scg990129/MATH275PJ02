import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

import static java.util.Comparator.comparing;

public class TestGenericTreeSet {

    public static void main(String[] args) {


//        City cityA = new City("City A", 'A', 1, 2);
//        City cityC = new City("City C", 'C', 5, 6);
//        City cityD = new City("City D", 'D', -1, -1);
//        City cityE = new City("City E", 'E', -2, -2);
//        City cityB = new City("City B", 'B', 3, 4);
//        City cityK = new City("City K", 'K', -6, -6);
//
//        City cityF = new City("City F", 'F', 7, 8);     // 東北方，遠離其他城市
//        City cityG = new City("City G", 'G', 4, 3);     // 介於 B 和 C 之間
//        City cityH = new City("City H", 'H', -4, -5);   // 貼近 K
//        City cityI = new City("City I", 'I', 0, 0);     // 靠近 A 和 D 的中間
//        City cityJ = new City("City J", 'J', 2, 1);     // 靠近 A 和 B
//        City cityL = new City("City L", 'L', -3, -2);   // 介於 E 和 H 之間
//        City cityM = new City("City M", 'M', 6, 7);     // 介於 C 和 F 之間
//        City cityN = new City("City N", 'N', -7, -8);   // 更遠的西南方
//        City cityO = new City("City O", 'O', 5, 3);     // 靠近 G，但偏東

        City cityA = new City("Central", 'A', 0, 0);


    Map<Character, City> cities = City.getCitiesInstance();
//        treeSet.addAll(cityA, Map.of(
//                'B', new City("Northville", 'B', 2, 8),
//                'C', new City("Eastburg", 'C', 6, 3),
//                'D', new City("Southtown", 'D', 1, -5),
//                'E', new City("Westend", 'E', -4, 3),
//                'F', new City("Lakeside", 'F', -3, -4)
//        ).values().toArray(new City[0]));

        AlgorithmMSP treeSet = new AlgorithmMSP(cityA, cities);

//        double distance = 0;
//        City previousCity = null;
//        for (City city : treeSet) {
//            System.out.println(city);
//            distance += previousCity == null ? 0 : City.distanceTo(previousCity, city);
//            previousCity = city;
//        }
//        distance += City.distanceTo(previousCity, treeSet.getFirst());

        System.out.println("Total cities in the tree set: " + treeSet.size());
        System.out.println("Total distance in the tree set: " + treeSet.getTotalDistance());
        System.out.println("toString(): " + treeSet.toString());


        City.ComparatorCityDistance comparatorCityDistance = new City.ComparatorCityDistance(cityA);

        PriorityQueue <City> priorityQueue = new PriorityQueue<>(comparatorCityDistance);
        priorityQueue.addAll(cities.values());
        while (!priorityQueue.isEmpty()) {
            City city = priorityQueue.poll();
//            comparatorCityDistance.setTargetCity(city);
            System.out.println(city);
        }
    }
}
