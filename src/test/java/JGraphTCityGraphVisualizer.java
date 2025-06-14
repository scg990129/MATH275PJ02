import com.mxgraph.view.mxGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import org.jgrapht.ext.JGraphXAdapter; // Adapter for JGraphX visualization

import com.mxgraph.layout.mxIGraphLayout;      // Interface for layouts
import com.mxgraph.layout.mxCircleLayout;      // Another common layout
import com.mxgraph.swing.mxGraphComponent;     // Swing component to display the graph
import com.mxgraph.util.mxConstants;           // Constants for styling
import com.mxgraph.view.mxStylesheet;          // Stylesheet for customization

import javax.swing.JFrame;                     // For the GUI window
import java.awt.Dimension;                     // For setting window size
import java.util.Hashtable;                    // For stylesheet entries
import java.util.Map;
import java.util.HashMap;

public class JGraphTCityGraphVisualizer extends JFrame {

    private static final long serialVersionUID = 1L; // Recommended for JFrame serialization

    private static final int scale = 60;
    private static final int offsetX = 400;
    private static final int offsetY = 500;
    private Graph<String, DefaultWeightedEdge> graph; // The JGraphT graph

    public JGraphTCityGraphVisualizer(String title, Graph<String, DefaultWeightedEdge> graph) {
        super(title);
        this.graph = graph;
        initUI();
    }

    private void initUI() {

        Map<String, double[]> coordinates = new HashMap<>();
        coordinates.put("A", new double[]{offsetX + 0 * scale, offsetY - 0 * scale});
        coordinates.put("B", new double[]{offsetX + 2 * scale, offsetY - 8 * scale});
        coordinates.put("C", new double[]{offsetX + 6 * scale, offsetY - 3 * scale});
        coordinates.put("D", new double[]{offsetX + 1 * scale, offsetY - (-5) * scale});  // -(-5) = +5
        coordinates.put("E", new double[]{offsetX + (-4) * scale, offsetY - 3 * scale});
        coordinates.put("F", new double[]{offsetX + (-3) * scale, offsetY - (-4) * scale}); // -(-4) = +4



        // Create an adapter for JGraphX
        JGraphXAdapter<String, DefaultWeightedEdge> jgxAdapter = new JGraphXAdapter<>(graph);

//        Object parent = jgxAdapter.getDefaultParent();
        com.mxgraph.model.mxIGraphModel model = jgxAdapter.getModel();
        model.beginUpdate();
        try {
            for (Map.Entry<String, double[]> entry : coordinates.entrySet()) {
                String city = entry.getKey();
                double[] xy = entry.getValue();
                Object cell = jgxAdapter.getVertexToCellMap().get(city);
                com.mxgraph.model.mxGeometry geo = (com.mxgraph.model.mxGeometry) model.getGeometry(cell).clone();
                geo.setX(xy[0]);
                geo.setY(xy[1]);
                model.setGeometry(cell, geo);
            }

            // Set edge labels to display weights
            for (DefaultWeightedEdge e : graph.edgeSet()) {
                String label = String.format("%.2f", graph.getEdgeWeight(e));
                Object edgeCell = jgxAdapter.getEdgeToCellMap().get(e);
                jgxAdapter.getModel().setValue(edgeCell, label);
            }
        } finally {
            model.endUpdate();
        }

        // Customize the graph appearance (optional but helpful for readability)
        mxGraph mxGraph = jgxAdapter;
        mxGraph.setCellsResizable(false); // Make cells not resizable by user
        mxGraph.setHtmlLabels(true);      // Allow HTML labels for edge weights

        // Create a custom stylesheet for edge labels (to show weights)
        mxStylesheet stylesheet = mxGraph.getStylesheet();
        Hashtable<String, Object> edgeStyle = new Hashtable<>();
        edgeStyle.put(mxConstants.STYLE_NOLABEL, "0"); // Show labels by default
        edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#FFFFFF"); // White background for label
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#6482B9");           // Edge color
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");             // Font color for label
        edgeStyle.put(mxConstants.STYLE_FONTSIZE, "10");                   // Font size
        stylesheet.putCellStyle("edgeStyle", edgeStyle);                   // Apply this style globally or to specific edges

        // Set the vertex and edge styles
        jgxAdapter.setStylesheet(stylesheet);

        // Create the graph component (the display area)
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        graphComponent.setPreferredSize(new Dimension(800, 600)); // Set preferred size
        graphComponent.setConnectable(false);                     // Prevent user from drawing new connections
        graphComponent.setToolTips(true);                         // Enable tooltips on cells

        // Apply a layout algorithm
        // mxIGraphLayout layout = new mxCircleLayout(jgxAdapter); // Arrange nodes in a circle
        // Alternative layouts:
        // mxIGraphLayout layout = new mxCompactTreeLayout(jgxAdapter, false, true); // For tree-like graphs
        // mxIGraphLayout layout = new mxFastOrganicLayout(jgxAdapter); // Organic layout for general graphs

        // layout.execute(jgxAdapter.getDefaultParent()); // Apply the layout to the graph

        // Configure the JFrame
        getContentPane().add(graphComponent);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Pack components to their preferred size
        setLocationRelativeTo(null); // Center the window
        setVisible(true);            // Make the window visible
    }

    public static void main(String[] args) {
        // 1. Define the city labels and distance matrix
        String[] cityLabels = {"A", "B", "C", "D", "E", "F"};
        double[][] distanceMatrixData = {
                {0.0000, 8.2462, 6.7082, 5.0990, 5.0000, 5.0000},
                {8.2462, 0.0000, 6.4031, 13.0384, 7.8102, 13.0000},
                {6.7082, 6.4031, 0.0000, 9.4340, 10.0000, 11.4018},
                {5.0990, 13.0384, 9.4340, 0.0000, 9.4340, 4.1231},
                {5.0000, 7.8102, 10.0000, 9.4340, 0.0000, 7.0711},
                {5.0000, 13.0000, 11.4018, 4.1231, 7.0711, 0.0000}
        };

        // 2. Build the JGraphT graph
        Graph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        for (String city : cityLabels) {
            graph.addVertex(city);
        }

        Map<String, Integer> cityIndexMap = new HashMap<>();
        for (int i = 0; i < cityLabels.length; i++) {
            cityIndexMap.put(cityLabels[i], i);
        }

        for (int i = 0; i < cityLabels.length; i++) {
            for (int j = i + 1; j < cityLabels.length; j++) { // j > i for undirected graph
                String sourceCity = cityLabels[i];
                String targetCity = cityLabels[j];
                double distance = distanceMatrixData[i][j];

                DefaultWeightedEdge edge = graph.addEdge(sourceCity, targetCity);
                if (edge != null) {
                    graph.setEdgeWeight(edge, distance);
                }
            }
        }

        // 3. Create and show the GUI
        // This should be done on the Event Dispatch Thread (EDT) for Swing applications.
        javax.swing.SwingUtilities.invokeLater(() -> {
            JGraphTCityGraphVisualizer app = new JGraphTCityGraphVisualizer("TSP City Graph Visualization - Undirected AND Weight Graph", graph);
            app.setVisible(true);
        });
    }
}
