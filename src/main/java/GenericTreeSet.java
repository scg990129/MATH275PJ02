
import java.util.*;

public class GenericTreeSet extends AbstractSet<City> implements Iterable<City>{

    protected LinkedList<Node> data = new LinkedList<>();
    protected Node root;

    @Override
    public Iterator<City> iterator() { // Depth-First Search (DFS)
        return root == null? Collections.emptyIterator() : root.iterator();
    }

    public boolean addAll(City root, City... cities) {
        if (this.root == null) {
            data.add(this.root = new Node(root));
        }

        LinkedHashSet<City> temp = new LinkedHashSet<>(Arrays.asList(cities));

        while (!data.isEmpty()) {
            City miniCity = null;
            Node miniNode = null;
            double minDistance = Double.MAX_VALUE;
            for(Node c : data) {
                Optional<City> tempNode = temp.parallelStream().min(new City.ComparatorCityDistance(c.getCity()));
                if  (tempNode.isPresent() && City.distanceTo(tempNode.get(), c.getCity()) < minDistance) {
                    minDistance = City.distanceTo(tempNode.get(), c.getCity());
                    miniCity = tempNode.get();
                    miniNode = c;
                }
            }
            if (miniCity == null) {
                return false; // No city found to connect to
            }
            miniNode.addEdge(miniCity);
            temp.remove(miniCity);
        }

        return true;
    }

    @Override
    public int size() {
        return data.size() + (root == null ? 0 : 1);
    }

    protected class Node implements Iterable<City> {

        @Override
        public Iterator<City> iterator() {
            return new NodeIterator();
        }

        City city;
        AbstractSet<Node> edges;

        public Node(City city) {
            this.city = city;
            edges = new LinkedHashSet<>();
        }

        public boolean addEdge(City city) {
            Node node = new Node(city);
            data.add(node);
            return edges.add(node);
        }

        public City getCity() { return city; }

        public class NodeIterator implements Iterator<City> {

            protected Iterator<Node> edgeIterator = null;
            protected Iterator<City> innerIterator = null;
            protected Node currentNode = null;

            @Override
            public boolean hasNext() {
                return edgeIterator == null || edgeIterator.hasNext() || (innerIterator != null && innerIterator.hasNext());
            }

            @Override
            public City next() {
                City city;
                if (edgeIterator == null) {
                    city = Node.this.city;
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
}
