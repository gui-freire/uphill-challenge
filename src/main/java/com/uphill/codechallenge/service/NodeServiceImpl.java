package com.uphill.codechallenge.service;

import com.uphill.codechallenge.model.EdgeGraph;
import com.uphill.codechallenge.model.WeightedEdge;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NodeServiceImpl implements NodeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeServiceImpl.class);

    private final AsSynchronizedGraph<String, WeightedEdge> edgeGraph;

    public NodeServiceImpl() {
        this.edgeGraph = new AsSynchronizedGraph.Builder<String, WeightedEdge>().setFair().build(new DirectedWeightedMultigraph<>(WeightedEdge.class));
    }

    @Override
    public synchronized String addNode(String node) {
        if (edgeGraph.addVertex(node)) {
            return "NODE ADDED";
        } else {
            return "ERROR: NODE ALREADY EXISTS";
        }
    }

    @Override
    public synchronized String addEdge(String initialNode, String finalNode, int weight) {
        try {
            WeightedEdge edge = edgeGraph.addEdge(initialNode, finalNode);
            edgeGraph.setEdgeWeight(edge, weight);
            return "EDGE ADDED";
        } catch (Exception e) {
            return "ERROR: NODE NOT FOUND";
        }
    }

    @Override
    public synchronized String removeNode(String node) {
        if (edgeGraph.removeVertex(node)) {
            return "NODE REMOVED";
        } else {
            return "ERROR: NODE NOT FOUND";
        }
    }

    @Override
    public synchronized String removeEdge(String initialNode, String finalNode) {
        try {
            if (edgeGraph.containsVertex(initialNode)) {
                edgeGraph.removeAllEdges(initialNode, finalNode);
                return "EDGE REMOVED";
            }
            return "ERROR: NODE NOT FOUND";
        } catch (Exception e) {
            return "ERROR: NODE NOT FOUND";
        }
    }

    @Override
    public synchronized String shortestPath(String initialNode, String finalNode) {
        DijkstraShortestPath<String, WeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(edgeGraph);
        double result = dijkstraShortestPath.getPathWeight(initialNode, finalNode);
        if (result == Double.POSITIVE_INFINITY) {
            return Integer.toString(Integer.MAX_VALUE);
        }
        return Integer.toString((int) result);
    }

    @Override
    public synchronized String closerThan(String node, Integer weight) {
        Set<WeightedEdge> edges = edgeGraph.outgoingEdgesOf(node);
        List<String> response = iterateGraph(new ArrayList<>(edges), 0d, weight, node);
        response.sort(Comparator.comparing(String::toLowerCase));

        Set<String> orderedResponseSet = new LinkedHashSet<>(response);
        List<String> orderedResponse = new ArrayList<>(orderedResponseSet);

        StringJoiner stringJoiner = new StringJoiner(",");
        orderedResponse.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private List<String> iterateGraph(List<WeightedEdge> edges, double totalWeight, int limitWeight, String sourceNode) {
        List<String> response = new ArrayList<>();
        List<EdgeGraph> newEdges = new ArrayList<>();
        edges.sort(Comparator.comparing(WeightedEdge::getWeight));
        edges.forEach(weightedEdge -> {
            double newWeight = totalWeight + weightedEdge.getWeight();
            if ((newWeight < limitWeight) && !Objects.equals(weightedEdge.getTarget(), sourceNode)) {
                response.add(weightedEdge.getTarget());
                newEdges.add(new EdgeGraph(edgeGraph.outgoingEdgesOf(weightedEdge.getTarget()), (int) newWeight));
                response.addAll(iterateGraph(new ArrayList<>(edgeGraph.outgoingEdgesOf(weightedEdge.getTarget())), newWeight, limitWeight, sourceNode));
            }
        });

        for (EdgeGraph graph: newEdges) {
            response.addAll(iterateGraph(new ArrayList<>(graph.getOutgoingEdges()), graph.getTotalWeight(), limitWeight, sourceNode));
        }
        return response;
    }
}
