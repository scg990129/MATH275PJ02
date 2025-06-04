package com.asuscomm.scg990129.elac.hw.math272;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AlgorithmNearestNeighbor {

    protected City startCity;
    protected AbstractSequentialList<City> cityList;
    protected double totalDistance = 0.0f;

    public AlgorithmNearestNeighbor(City startCity, Map<Character, City> cityList) {
        this.startCity = startCity;
//        SortedSet<City> cities = new TreeSet<>(new City.ComparatorCityDistance(this.startCity));
        LinkedList<City> availableCities = new LinkedList<>();

        cityList.values().stream().filter(Predicate.not(this.startCity::equals))
                .forEach(availableCities::add);

        this.cityList = getPath(this.startCity, availableCities);
        City previousCity = this.startCity;
        for(City city: this.cityList) {
            this.totalDistance += City.distanceTo(previousCity, city);
            previousCity = city;
        }
        this.totalDistance += City.distanceTo(previousCity, this.startCity); // Return to start city
    }

    protected AbstractSequentialList<City> getPath(City previousCity, Collection<City> availableCities) {
        if (availableCities.size() == 1) { // base case
            return new LinkedList<>(availableCities);
        }
        // Recursive case
        City selectedCity = null;

        AbstractSequentialList<City> availableCitiesNext = new LinkedList<>();
        Comparator<City> comparator = new City.ComparatorCityDistance(previousCity);
        availableCities = availableCities.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
        for (Iterator<City> i = availableCities.iterator(); i.hasNext(); ) {
            City tempCity = i.next();
            if (selectedCity == null || comparator.compare(tempCity, selectedCity) < 0) {
                if (selectedCity != null) {
                    availableCitiesNext.add(selectedCity);
                    i.remove();
                }
                selectedCity = tempCity;
            }else {
                availableCitiesNext.add(tempCity);
            }
        }
//            SortedSet<City> nextAvailableCities = new TreeSet<>(new City.ComparatorCityDistance(tempCity));
//            nextAvailableCities.addAll(availableCities);
//            nextAvailableCities.remove(tempCity); // Remove current city from available cities
//            AbstractSequentialList<City> subPath = getPath(tempCity, nextAvailableCities);
//            subPath.addFirst(tempCity);
//            if (path.isEmpty() || City.distanceTo(previousCity, subPath.getFirst()) < City.distanceTo(previousCity, path.getFirst())) {
//                path = new LinkedList<>(subPath);
//            }
        AbstractSequentialList<City> answer = getPath(selectedCity,  availableCitiesNext);
        answer.addFirst(selectedCity);
        return answer;
    }

    public City getStartnReturnCity(){
        return startCity;
    }

    public synchronized AbstractSequentialList<City> getPath() {
        LinkedList<City> temp = new LinkedList<>(cityList);
        temp.addLast(startCity);
         return temp;
    }

    public String toString() {
        return String.format("AlgorithmNearestNeighbor:\nStart City: %s\n Path: %s\n(total distance: %.4f)",
                startCity, cityList,
                totalDistance
        );
    }
}
