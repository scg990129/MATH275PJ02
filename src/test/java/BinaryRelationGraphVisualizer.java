import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;

import javax.swing.*;

public class BinaryRelationGraphVisualizer {
    public static void main(String[] args) {
        DefaultDirectedGraph<Integer, DefaultEdge> graph =
                new DefaultDirectedGraph<>(DefaultEdge.class);

        // Add vertices
//        int[] vertices = {2,4,8,16,32, 64};
        Integer[] vertices = {3,5,6,7,10,14,20,30,60};

        for (int v : vertices) {
            graph.addVertex(v);
        }

        // Add edges
        graph.addEdge(3,6);
        graph.addEdge(3,3);
        graph.addEdge(3,30);
        graph.addEdge(3,60);
        graph.addEdge(5,5);
        graph.addEdge(5,10);
        graph.addEdge(5,20);
        graph.addEdge(5,30);
        graph.addEdge(5,60);
        graph.addEdge(6,6);
        graph.addEdge(6,30);
        graph.addEdge(6,60);
        graph.addEdge(7,7);
        graph.addEdge(7,14);
        graph.addEdge(10,10);
        graph.addEdge(10,20);
        graph.addEdge(10,30);
        graph.addEdge(10,60);
        graph.addEdge(14,14);
        graph.addEdge(20,20);
        graph.addEdge(20,60);
        graph.addEdge(30,30);
        graph.addEdge(30,60);
        graph.addEdge(60,60);




        // Visualization
        JGraphXAdapter<Integer, DefaultEdge> graphAdapter = new JGraphXAdapter<>(graph);
//        graphAdapter.setLabelsVisible(false);
        mxCircleLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        JFrame frame = new JFrame("Binary Relation Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new mxGraphComponent(graphAdapter));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}
