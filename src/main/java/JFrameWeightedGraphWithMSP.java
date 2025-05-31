import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class JFrameWeightedGraphWithMSP extends JFrame {
    private static final long serialVersionUID = 1L; // Recommended for JFrame serialization

    private static final int scale = 60;
    private static final int offsetX = 400;
    private static final int offsetY = 500;
    private Map<City, double[]> coordinates = new LinkedHashMap<>();
    private Set<DefaultWeightedEdge> selectedPathWithDFS = new HashSet<>();
    private Set<DefaultWeightedEdge> miniSpanningTree = new HashSet<>();
    private Set<AlgorithmMSP.Edge> availableEdge = new HashSet<>();
    private Graph<Character, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    JGraphXAdapter<Character, DefaultWeightedEdge> jgxAdapter;
    //private Dimension dimension = new Dimension(800, 1000);

    protected AlgorithmMSP algorithmMSP = null;

    public JFrameWeightedGraphWithMSP(Map<Character, City> cities) {
        super("TSP City Graph Visualization - Undirected AND Weight Graph WITH MSP");
        this.algorithmMSP = new AlgorithmMSP(cities.get('A'), cities);


        processCity(cities);

        Set<City> tempSet = new LinkedHashSet<>(cities.values());
        for(City source: cities.values()){
            tempSet.remove(source);
            tempSet.parallelStream()
//                    .filter(Predicate.not(source::equals))
                    .map(target-> new AlgorithmMSP.Edge(source, target))
                    .sequential()
//                    .peek(edge -> miniSpanningTree.add(graph.addEdge(edge.getSource().getLabel(), edge.getTarget().getLabel())))
                    // DefaultWeightedEdge edge = graph.addEdge(sourceCity, targetCity);
                    .forEach(availableEdge::add);

            // DefaultWeightedEdge edge = graph.addEdge(sourceCity, targetCity);
        }

        Set<AlgorithmMSP.Edge> edges = algorithmMSP.getMiniSpanningTreeEdge();
        edges.stream().sequential()
                .filter(Objects::nonNull)
                .peek(availableEdge::remove)
                .map(edge->graph.addEdge(edge.getSource().getLabel(), edge.getTarget().getLabel()))
//                .map(edge->{
//                    DefaultWeightedEdge e = graph.addEdge(edge.getSource().getLabel(), edge.getTarget().getLabel());
//                    graph.setEdgeWeight(e, edge.getDistance());
//                    return e;
//                })
                .filter(Objects::nonNull)
//                .peek(edge->jgxAdapter.getModel().setValue( jgxAdapter.getEdgeToCellMap().get(edge), String.format("%.2f", graph.getEdgeWeight(edge))))
                .forEach(miniSpanningTree::add);
//        availableEdge.removeAll(edges);

        City previousCity = algorithmMSP.root.getCity();
        for(City target: algorithmMSP){
            AlgorithmMSP.Edge edge = new AlgorithmMSP.Edge(previousCity, target);
            previousCity = target;
            availableEdge.remove(edge);
        }
        AlgorithmMSP.Edge edge = new AlgorithmMSP.Edge(previousCity, algorithmMSP.root.getCity());
        availableEdge.remove(edge);


        initUI();
    }

    protected void processCity(Map<Character, City> cities){
        for(City c: cities.values()){
            coordinates.put(c,
                    new double[]{offsetX + c.getX() * scale, offsetY - c.getY() * scale});
            graph.addVertex(c.getLabel());
        }
    }

    protected void initUI() {
        // Create an adapter for JGraphX

        jgxAdapter = new JGraphXAdapter<>(graph);
        Object parent = jgxAdapter.getDefaultParent();
        com.mxgraph.model.mxIGraphModel model = this.jgxAdapter.getModel();
        model.beginUpdate();
        try {
            for (Map.Entry<City, double[]> entry : coordinates.entrySet()) {
                double[] xy = entry.getValue();
                mxICell cell = jgxAdapter.getVertexToCellMap().get(entry.getKey().getLabel());
                com.mxgraph.model.mxGeometry geo = (com.mxgraph.model.mxGeometry) model.getGeometry(cell).clone();
//                cell.setGeometry( new com.mxgraph.model.mxGeometry(xy[0], xy[1], 30, 30));
                geo.setX(xy[0]);
                geo.setY(xy[1]);
                model.setGeometry(cell, geo);
            }

            Map<String, Object> style = new HashMap<>();
            style.put(mxConstants.STYLE_STROKECOLOR,"orange");
            // stylesheet.putCellStyle("CITY_NODE", style);

            // Set edge labels to display weights
            for (DefaultWeightedEdge e : graph.edgeSet()) {
                String label = String.format("%.2f", graph.getEdgeWeight(e));
//                mxICell edgeCell = jgxAdapter.getEdgeToCellMap().get(e);
////                if (list1.contains(e)){
////                    edgeCell.setStyle("strokeColor=orange");
////                } else if (list2.contains(e)) {
////                    edgeCell.setStyle("strokeColor=red");
////                }
//                jgxAdapter.getModel().setValue(edgeCell, label);
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

        // graphComponent.setPreferredSize(dimension); // Set preferred size
        graphComponent.setConnectable(true);                     // Prevent user from drawing new connections
        graphComponent.setToolTips(true);                         // Enable tooltips on cells

        // Apply a layout algorithm
        // mxIGraphLayout layout = new mxCircleLayout(jgxAdapter); // Arrange nodes in a circle
        // Alternative layouts:
        // mxIGraphLayout layout = new mxCompactTreeLayout(jgxAdapter, false, true); // For tree-like graphs
        // mxIGraphLayout layout = new mxFastOrganicLayout(jgxAdapter); // Organic layout for general graphs

        // layout.execute(jgxAdapter.getDefaultParent()); // Apply the layout to the graph

        // Configure the JFrame
        getContentPane().add(graphComponent);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
         // Pack components to their preferred size
        // setPreferredSize(dimension); // Set a default size for the window
        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);            // Make the window visible
    }

}
