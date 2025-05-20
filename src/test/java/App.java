
import org.jgrapht.Graph;
import org.jgrapht.alg.tour.TwoOptHeuristicTSP;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Create a weighted undirected graph
        Graph<String, DefaultEdge> graph =
                new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);

        // Add vertices (cities)
        String[] cities = {"A", "B", "C", "D"};
        for (String city : cities) graph.addVertex(city);

        // Add weighted edges (distances)
        graph.setEdgeWeight(graph.addEdge("A", "B"), 10);
        graph.setEdgeWeight(graph.addEdge("A", "C"), 15);
        graph.setEdgeWeight(graph.addEdge("A", "D"), 20);
        graph.setEdgeWeight(graph.addEdge("B", "C"), 35);
        graph.setEdgeWeight(graph.addEdge("B", "D"), 25);
        graph.setEdgeWeight(graph.addEdge("C", "D"), 30);

        // TSP Solver: TwoOptHeuristicTSP
        var tsp = new TwoOptHeuristicTSP<String, DefaultEdge>();
        List<String> tour = tsp.getTour(graph).getVertexList();

        System.out.println("Tour: " + tour);

        // Visualization
        SwingUtilities.invokeLater(() -> showGraph(graph, tour));
    }

    // Visualization using JGraphXAdapter and Swing
    private static void showGraph(Graph<String, DefaultEdge> graph, List<String> tour) {
        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<>(graph);
        mxCircleLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        JFrame frame = new JFrame("TSP Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        mxGraphComponent component = new mxGraphComponent(graphAdapter);
        frame.add(component, BorderLayout.CENTER);
        frame.setVisible(true);

        // Optionally, highlight the TSP tour
        // (Advanced: Draw colored edges for the best tour)
    }
}
