
import java.util.*;
import java.util.function.Predicate;

public class AlgorithmNearestNeighbor {

    protected City startCity;
    protected AbstractSequentialList<City> cityList;
    protected double totalDistance = 0.0f;

    public AlgorithmNearestNeighbor( City startCity, Map<Character, City> cityList) {
        this.startCity = startCity;
        SortedSet<City> cities = new TreeSet<>(new City.ComparatorCityDistance(this.startCity));

        cityList.values().stream().parallel().filter(Predicate.not(this.startCity::equals))
                .forEach(cities::add);
        this.cityList = getPath(cities);
        City previousCity = this.startCity;
        for(City city: this.cityList) {
            this.totalDistance += City.distanceTo(previousCity, city);
            previousCity = city;
        }
        this.totalDistance += City.distanceTo(previousCity, this.startCity); // Return to start city
    }

    protected AbstractSequentialList<City> getPath(SortedSet<City> avalibleCities){
        if (avalibleCities.size() == 1){ // base case
            return new LinkedList<>(avalibleCities);
        }
        // Recursive case
        City selectedCity = avalibleCities.removeFirst();
        SortedSet<City> nextAvailableCities = new TreeSet<>(new City.ComparatorCityDistance(selectedCity));
        nextAvailableCities.addAll(avalibleCities);
        AbstractSequentialList<City> path = getPath(nextAvailableCities);
        path.addFirst(selectedCity);
        return path;
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
