package com.uphill.codechallenge.service;

import com.uphill.codechallenge.model.WeightedEdge;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.concurrent.AsSynchronizedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements NodeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeServiceImpl.class);

    private Graph<String, WeightedEdge> edgeGraph;

    public NodeServiceImpl() {
        this.edgeGraph = new AsSynchronizedGraph.Builder().build(new DefaultDirectedWeightedGraph<>(WeightedEdge.class));
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
            WeightedEdge edge;
            if (edgeGraph.containsEdge(initialNode, finalNode)) {
                edgeGraph.removeEdge(initialNode, finalNode);
            }
            edge = edgeGraph.addEdge(initialNode, finalNode);
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
            WeightedEdge edge = edgeGraph.removeEdge(initialNode, finalNode);
            if (Objects.isNull(edge)) {
                return "ERROR: NODE NOT FOUND";
            }
            return "EDGE REMOVED";
        } catch (Exception e) {
            return "ERROR: NODE NOT FOUND";
        }
    }

    @Override
    public String shortestPath(String initialNode, String finalNode) {
        DijkstraShortestPath<String, WeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(edgeGraph);
        double result = dijkstraShortestPath.getPathWeight(initialNode, finalNode);
        if (result == Double.POSITIVE_INFINITY) {
            return Integer.toString(Integer.MAX_VALUE);
        }
        return Integer.toString((int) result);
    }

    @Override
    public String closerThan(String node, Integer weight) {
        Set<WeightedEdge> edges = edgeGraph.outgoingEdgesOf(node);
        List<String> response = iterateGraph(new ArrayList<>(edges), 0, weight);

        Set<String> orderedResponseSet = new ConcurrentSkipListSet<>(response);

        StringJoiner stringJoiner = new StringJoiner(",");
        orderedResponseSet.stream().forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private List<String> iterateGraph(List<WeightedEdge> edges, int totalWeight, int limitWeight) {
        List<String> response = new ArrayList<>();
        for (WeightedEdge edge : edges) {
            if ((totalWeight + edge.getWeight()) <= limitWeight) {
                double newWeight = totalWeight + edge.getWeight();
                response.add(edge.getTarget());
                if (newWeight < limitWeight) {
                    response.addAll(iterateGraph(new ArrayList<>(edgeGraph.outgoingEdgesOf(edge.getTarget())), (int) newWeight, limitWeight));
                }
            }
        }
        return response;
    }
}
