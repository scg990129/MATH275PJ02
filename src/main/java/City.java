import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class City implements Comparable<City> {
    protected static final double EPSILON = 1e-6;


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
        TreeMap<Character, City> tree = new TreeMap<>(new City.ComparatorCityLabel());
        tree.putAll(cities);
        return tree;
    }

    public static double distanceTo(City city1, City city2) {
        return Math.sqrt(distanceEigenValue(city1, city2));
    }

    /**
     * Calculates the squared distance between two cities.
     * This is useful for comparisons without needing to compute the square root without double handling.
     *
     * @param city1 the first city
     * @param city2 the second city
     * @return the squared distance between the two cities
     */
    public static int distanceEigenValue(City city1, City city2) {
        int dx = city1.x - city2.x;
        int dy = city1.y - city2.y;
        return dx * dx + dy * dy;
    }

    public static int compareDistance(double d1, double d2) {
        return Math.abs(d1 - d2) < EPSILON ? 0 : Double.compare(d1, d2);
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

    public int compareTo(City other) {
        return Character.compare(this.label, other.label);
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof City)) return false;
        City other = (City) obj;
        return this.label == other.label;// && this.x == other.x && this.y == other.y;
    }

    public static class ComparatorCityDistance implements Comparator<City> {

        private City targetCity;

        public ComparatorCityDistance(City targetCity) {
            this.targetCity = targetCity;
        }

        public void setTargetCity(City targetCity) {
            this.targetCity = targetCity;
        }

        @Override
        public int compare(City o1, City o2) {
            int i = Integer.compare(distanceEigenValue(targetCity, o1), distanceEigenValue(targetCity, o2));
            return i != 0 ? i :
                     Character.compare(Character.toUpperCase(o1.label), Character.toUpperCase(o2.label));
        }
    }

    public static class ComparatorCityLabel implements Comparator<Character> {

        @Override
        public int compare(Character o1, Character o2) {
            return Character.compare(Character.toUpperCase(o1), Character.toUpperCase(o2));
        }
    }
}