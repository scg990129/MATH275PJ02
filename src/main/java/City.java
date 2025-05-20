import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class City implements Comparable<City> {
    private static final Map<String, City> cities =
            Map.of(
                    "A", new City("Central", "A", 0, 0),
                    "B", new City("Northville", "B", 2, 8),
                    "C", new City("Eastburg", "C", 6, 3),
                    "D", new City("Southtown", "D", 1, -5),
                    "E", new City("Westend", "E", -4, 3),
                    "F", new City("Lakeside", "F", -3, -4)
            );
    private final String name;
    private final String label;
    private final int x;
    private final int y;
    public City(String name, String label, int x, int y) {
        this.name = name;
        this.label = label;
        this.x = x;
        this.y = y;
    }

    public static Map<String, City> getCitiesInstance() {
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

    public String getLabel() {
        return this.label;
    }

    public int getLabelNo() {
        return this.label.toLowerCase().charAt(0) - 97;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%d, %d)", name, label, x, y);
    }

    public double distanceTo(City other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public int compareTo(City other) {
        return CharSequence.compare(this.label, other.label);
    }

    public static class ComparatorCityDistance implements Comparator<City> {

        private final City defaultCity;

        public ComparatorCityDistance(City defaultCity) {
            this.defaultCity = defaultCity;
        }

        @Override
        public int compare(City o1, City o2) {
            int i = Double.compare(defaultCity.distanceTo(o2), defaultCity.distanceTo(o1));
            return i != 0 ? i : CharSequence.compare(o1.label, o2.label);
        }
    }
}
