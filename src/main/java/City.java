import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class City implements Comparable<City> {
    private static final Map<Character, City> cities =
            Map.of(
                    'A', new City("Central", 'A', 0, 0),
                    'B', new City("Northville", 'B', 2, 8),
                    'C', new City("Eastburg", 'C', 6, 3),
                    'D', new City("Southtown", 'D', 1, -5),
                    'E', new City("Westend", 'E', -4, 3),
                    'F', new City("Lakeside", 'F', -3, -4)
            );
    private final String name;
    private final char label;
    private final int x;
    private final int y;
    public City(String name, char label, int x, int y) {
        this.name = name;
        this.label = Character.toUpperCase(label);
        this.x = x;
        this.y = y;
    }

    public static Map<Character, City> getCitiesInstance() {
        return new TreeMap<>(cities);
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getLabel() {
        return this.label;
    }

    public int getLabelNo() {
        return Character.toLowerCase(this.label) - 97;
    }

    @Override
    public String toString() {
        return String.format("%s %c (%d, %d)", name, label, x, y);
    }

    public static double distanceTo(City city1, City city2) {
        return Math.sqrt(distanceEigenvalue(city1, city2));
    }

    public static int distanceEigenvalue(City city1, City city2){
        int dx = (city1.x - city2.x);
        int dy = (city1.y - city2.y);
        return dx * dx + dy * dy;
    }

    public int compareTo(City other) {
        return Character.compare(this.label, other.label);
    }

    public static class ComparatorCityDistance implements Comparator<City> {

        private final City defaultCity;

        public ComparatorCityDistance(City defaultCity) {
            this.defaultCity = defaultCity;
        }

        @Override
        public int compare(City o1, City o2) {
            int i = Integer.compare(distanceEigenvalue(defaultCity, o1), distanceEigenvalue(defaultCity, o2));
            return i != 0 ? i : Character.compare(Character.toUpperCase(o1.label), Character.toUpperCase(o2.label));
        }
    }

    public class ComparatorCityLabel implements Comparator<Character> {

        @Override
        public int compare(Character o1, Character o2) {
            return Character.compare(Character.toUpperCase(o1), Character.toUpperCase(o2));
        }
    }
}
