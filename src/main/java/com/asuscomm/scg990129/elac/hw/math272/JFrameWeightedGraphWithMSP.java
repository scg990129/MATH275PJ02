package com.asuscomm.scg990129.elac.hw.math272;

import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class JFrameWeightedGraphWithMSP extends JFrame {
    private static final long serialVersionUID = 1L; // Recommended for JFrame serialization

    private static final int scale = 60;
    private static final int offsetX = 400;
    private static final int offsetY = 500;
    protected AlgorithmMSP algorithmMSP = null;
    JGraphXAdapter<Character, DefaultWeightedEdge> jgxAdapter;
    private Map<City, double[]> coordinates = new LinkedHashMap<>();
    private Set<DefaultWeightedEdge> selectedPathWithDFS = new HashSet<>();
    private Set<DefaultWeightedEdge> miniSpanningTree = new HashSet<>();
    private Set<AlgorithmMSP.Edge> availableEdge = new HashSet<>();
    //private Dimension dimension = new Dimension(800, 1000);
    private Graph<Character, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class); // SimpleWeightedGraph

    public JFrameWeightedGraphWithMSP(Map<Character, City> cities) {
        super("TSP City Graph Visualization - Undirected AND Weight Graph WITH MSP");
        this.algorithmMSP = new AlgorithmMSP(cities.get('A'), cities);

        processCity(cities);

        Set<City> tempSet = new LinkedHashSet<>(cities.values());
        for (City source : cities.values()) {
            tempSet.remove(source);
            tempSet.stream()
//                    .filter(Predicate.not(source::equals))
                    .map(target -> new AlgorithmMSP.Edge(source, target))
//                    .peek(edge -> miniSpanningTree.add(graph.addEdge(edge.getSource().getLabel(), edge.getTarget().getLabel())))
                    // DefaultWeightedEdge edge = graph.addEdge(sourceCity, targetCity);
                    .forEach(availableEdge::add);
        }

        Set<AlgorithmMSP.Edge> miniSpanningTreeEdge = algorithmMSP.getMiniSpanningTreeEdge();

        City previousCity = null;
        DefaultWeightedEdge defaultWeightedEdge;
        for (City target : algorithmMSP) {
            if (previousCity == null) {
                previousCity = target;
                continue; // Skip the first iteration
            }
            AlgorithmMSP.Edge edge = new AlgorithmMSP.Edge(previousCity, target);

            selectedPathWithDFS.add(defaultWeightedEdge = graph.addEdge(previousCity.getLabel(), target.getLabel()));
            graph.setEdgeWeight(defaultWeightedEdge, edge.getDistance());
            previousCity = target;
            miniSpanningTreeEdge.remove(edge);
            availableEdge.remove(edge);
        }
        AlgorithmMSP.Edge MSPedge = new AlgorithmMSP.Edge(previousCity, algorithmMSP.root.getCity());
        selectedPathWithDFS.add(defaultWeightedEdge = graph.addEdge(previousCity.getLabel(), algorithmMSP.root.getCity().getLabel()));
        graph.setEdgeWeight(defaultWeightedEdge, MSPedge.getDistance());
        miniSpanningTreeEdge.remove(MSPedge);
        availableEdge.remove(MSPedge);

        miniSpanningTreeEdge.stream()
                .filter(Objects::nonNull)
                .sequential()
                .peek(availableEdge::remove)
                .map(edge -> {
                    DefaultWeightedEdge e = graph.addEdge(edge.getSource().getLabel(), edge.getTarget().getLabel());
                    if (e != null) {
                        graph.setEdgeWeight(e, edge.getDistance());
                    }
                    return e;
                })
                .filter(Objects::nonNull)
                .forEach(miniSpanningTree::add);

        availableEdge.forEach(edge -> {
            Optional<DefaultWeightedEdge> oe = Optional.of(graph.addEdge(edge.getSource().getLabel(), edge.getTarget().getLabel()));
            oe.ifPresent(e -> graph.setEdgeWeight(e, edge.getDistance()));
        });
        initUI();
    }

    protected void createLegend() {
        JLabel legend = new JLabel(
                "<html>" +
                        "<b>Legend</b><br>" +
                        "● Vertex: City<br>" +
                        "<span style='color:gray;'>⬤ Dashed gray line</span>: Possible Edge<br>" +
                        "<span style='color:orange;'>→ Orange arrow</span>: MST mini spanning Tree<br>" +
                        "<span style='color:red;'>→ Red arrow</span>: MST Cycle Solution from DFS of mini spanning Tree<br>" +
                        "</html>"
        );

        legend.setOpaque(true);
        legend.setBackground(Color.WHITE);
        legend.setBorder(BorderFactory.createLineBorder(Color.GRAY));
//        legend.setSize(legend.getPreferredSize());
        legend.setBounds(10, 10, 300, 120);
//        legend.setB

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 860));
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        graphComponent.setBounds(0, 0, 800, 860);
        layeredPane.add(graphComponent, Integer.valueOf(0));
        layeredPane.add(legend, Integer.valueOf(1));


        this.getContentPane().add(layeredPane);
    }

    public AlgorithmMSP getAlgorithmMSP() {
        return algorithmMSP;
    }

    protected void processCity(Map<Character, City> cities) {
        for (City c : cities.values()) {
            coordinates.put(c,
                    new double[]{offsetX + c.getX() * scale, offsetY - c.getY() * scale});
            graph.addVertex(c.getLabel());
        }
    }

    protected void initUI() {
        // Create an adapter for JGraphX

        jgxAdapter = new JGraphXAdapter<>(graph);
//        Object parent = jgxAdapter.getDefaultParent();
        com.mxgraph.model.mxIGraphModel model = this.jgxAdapter.getModel();
        model.beginUpdate();
        try {
            for (Map.Entry<City, double[]> entry : coordinates.entrySet()) {
                double[] xy = entry.getValue();
                mxICell cell = jgxAdapter.getVertexToCellMap().get(entry.getKey().getLabel());
                com.mxgraph.model.mxGeometry geo = (com.mxgraph.model.mxGeometry) model.getGeometry(cell).clone();

                geo.setX(xy[0]);
                geo.setY(xy[1]);
                model.setGeometry(cell, geo);
            }

            // Set edge labels to display weights
            for (DefaultWeightedEdge e : graph.edgeSet()) {
                String label = String.format("%.2f", graph.getEdgeWeight(e));
                mxICell edgeCell = jgxAdapter.getEdgeToCellMap().get(e);
                if (this.miniSpanningTree.contains(e)) {
                    edgeCell.setStyle("strokeColor=orange;endArrow=none;strokeWidth=2;dashed=true;dashPattern=5 5");
                } else if (selectedPathWithDFS.contains(e)) {
                    edgeCell.setStyle("strokeColor=red;strokeWidth=4");
                } else {
                    edgeCell.setStyle("strokeWidth=1;endArrow=none;strokeColor=grey;dashed=true;dashPattern=1 1");//STYLE_ENDARROW
                }
//                if (this.miniSpanningTree.contains(edgeCell)){}

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

        // graphComponent.setPreferredSize(dimension); // Set preferred size
        graphComponent.setConnectable(true);                     // Prevent user from drawing new connections
        graphComponent.setToolTips(true);                         // Enable tooltips on cells
//        graphComponent.setConnectable(false);         // no connections
        graphComponent.setDragEnabled(false);         // no drag-and-drop
        graphComponent.setEnabled(false);             // fully non-editable (optional)

        // Apply a layout algorithm
        // mxIGraphLayout layout = new mxCircleLayout(jgxAdapter); // Arrange nodes in a circle
        // Alternative layouts:
        // mxIGraphLayout layout = new mxCompactTreeLayout(jgxAdapter, false, true); // For tree-like graphs
        // mxIGraphLayout layout = new mxFastOrganicLayout(jgxAdapter); // Organic layout for general graphs

        // layout.execute(jgxAdapter.getDefaultParent()); // Apply the layout to the graph

        // Configure the JFrame
        getContentPane().add(graphComponent);
        createLegend(); // Add legend to the graph component
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // Pack components to their preferred size
        // setPreferredSize(dimension); // Set a default size for the window
        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);            // Make the window visible
    }

}
