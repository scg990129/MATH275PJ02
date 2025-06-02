import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AlgorithmMSP implements Iterable<City> {

    protected Node root;
    protected Map<City, Node> visitedCities = new LinkedHashMap<>();
    protected double totalDistance = 0.0f;

    // Prim-Based MSP
    public AlgorithmMSP(City startCity, Map <Character, City> availableCities) {
        visitedCities.put(startCity, this.root = new Node(startCity));

        PriorityQueue <Edge> priorityQueue = new PriorityQueue<>();
        Set<City> unvisitedCities = new HashSet<>();
        availableCities.values().stream().filter(Predicate.not(startCity::equals)) // Predicate.not(
                .map( city -> new Edge(startCity, city))
                .peek(priorityQueue::add)
                .map(Edge::getTarget).forEach(unvisitedCities::add);

        initialMinimumSpanningTree(priorityQueue, unvisitedCities);
    }

    protected void initialMinimumSpanningTree(PriorityQueue<Edge> priorityQueue, Set<City> unvisitedCities) {
        if (unvisitedCities == null || unvisitedCities.isEmpty()) { // base case
            return; // No more edges or cities to visit
        }

        // recursive case
        Edge edge = priorityQueue.poll();
        City target = edge.getTarget();
        if (!unvisitedCities.removeIf(target::equals)) {
            initialMinimumSpanningTree(priorityQueue, unvisitedCities);
        } else {
            Node node = new Node(target);
            visitedCities.put(target, node);
            visitedCities.get(edge.getSource()).getEdges().add(node);
            unvisitedCities.stream().map(city -> new Edge(target, city))
                    .forEach(priorityQueue::add);
            initialMinimumSpanningTree(priorityQueue, unvisitedCities);
        }
    }

    public Set<Edge> getMiniSpanningTreeEdge(){
        Set<Edge> edges = new LinkedHashSet<>();
        visitedCities.values().stream()
                .flatMap(source->source.edges.stream().map(target->new Edge(source.getCity(),target.getCity())))
                .sequential().forEach(edges::add);

        return edges;
    }

    public int size() {
        return visitedCities.size() + (root == null ? 0 : 1);
    }

    public double getTotalDistance(){
        return totalDistance <= 0.0f ? calculateTotalDistance() : totalDistance;
    }

    private double calculateTotalDistance() {
        double totalDistance = 0.0f;
        City previousCity = null;
        for(City city : this) {
            totalDistance += previousCity != null? City.distanceTo(previousCity, city) : 0.0f;
            previousCity = city;
        }
        totalDistance += City.distanceTo(previousCity, root.getCity()); // Return to start city
        // Connect the last city to the first
        return totalDistance;
    }

    @Override
    public Iterator<City> iterator() {
        return root == null? Collections.emptyIterator() : root.iterator();
    }

    public static class Edge implements Comparable<Edge> {
        private final City source;
        private final City target;
        private final int distanceEigenValue;

        public Edge(City source, City target) {
            this.source = source;
            this.target = target;
            this.distanceEigenValue = City.distanceEigenValue(source, target);
        }

        public City getSource() {
            return source;
        }

        public City getTarget() {
            return target;
        }

        public int getDistanceEigenValue() {
            return distanceEigenValue;
        }

        public double getDistance() {
            return City.distanceTo(source, target);
        }

        @Override
        public int compareTo(Edge other) {
            int distanceComparison = Integer.compare(this.getDistanceEigenValue(), other.getDistanceEigenValue());
            return distanceComparison != 0 ?
                    distanceComparison: this.equals(other) ? 0 :
                    this.source.getLabel() != other.source.getLabel() ?
                            Character.compare(this.source.getLabel(), other.source.getLabel()) :
                            Integer.compare(
                                    Math.abs(this.source.getLabel() - this.target.getLabel()),
                                    Math.abs(other.source.getLabel() - other.target.getLabel())
                            );
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge edge)) return false;
            return (Objects.equals(source, edge.source) && Objects.equals(target, edge.target)) ||
                   (Objects.equals(source, edge.target) && Objects.equals(target, edge.source));
        }
    }

    protected class Node implements Iterable<City> {

        City city;
        Set<Node> edges;

        public Node(City city) {
            this.city = city;
            edges = new LinkedHashSet<>();
        }

        public Set<Node> getEdges(){
            return edges;
        }

        public City getCity() { return city; }

        public Set<Edge> getEdgesAsSet() {
            return edges.stream()
                    .map(node -> new Edge(this.city, node.getCity()))
                    .collect(Collectors.toSet());
        }

        @Override
        public Iterator<City> iterator() {
            return new Node.NodeIterator();
        }

        public class NodeIterator implements Iterator<City> {

            protected Iterator<Node> edgeIterator = null;
            protected Iterator<City> innerIterator = null;
//            protected City rootCity = Node.this.city;

            @Override
            public boolean hasNext() {
//                return rootCity != null ||
                        return edgeIterator == null || edgeIterator.hasNext() || (innerIterator != null && innerIterator.hasNext());
            }

            @Override
            public City next() {
//                if(rootCity != null) {
//                    City root = rootCity;
//                    rootCity = null; // Set to null to indicate that the root has been returned
//                    return root;
//                }
                if (edgeIterator == null) {
                    edgeIterator = Node.this.edges.iterator();
                    return city;
                }

                if (innerIterator == null || !innerIterator.hasNext()) {
                    if (edgeIterator.hasNext()) {
                        Node nextNode = edgeIterator.next();
                        innerIterator = nextNode.iterator();
                    } else {
                        throw new NoSuchElementException();
                    }
                }
                if (innerIterator.hasNext()) {
                    return innerIterator.next();
                }

                throw new NoSuchElementException();
            }
        }
    }

    public String toString() {
        return String.format("[%s] with return %s",
                StreamSupport.stream(this.spliterator(), false)
                        .map(String::valueOf).collect(Collectors.joining(", ")),
                this.root == null ? "null" : this.root.getCity()
        );
    }
}

