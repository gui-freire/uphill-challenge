package com.uphill.codechallenge.service;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeServiceTest {

    private NodeService service;

    @Test
    public void testAddNode() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";
        int randomNum = ThreadLocalRandom.current().nextInt(0, 10);
        for (int i = 0; i < randomNum; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }
    }

    @Test
    public void testAddNodeThatAlreadyExists() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";
        int randomNum = ThreadLocalRandom.current().nextInt(0, 10);
        for (int i = 0; i < randomNum; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }
        // tries to add again the nodes
        for (int i = 0; i < randomNum; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("ERROR: NODE ALREADY EXISTS", response);
        }
    }

    @Test
    public void testAddEdges() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";
        for (int i = 0; i < 12; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("EDGE ADDED", response);
        }
    }

    @Test
    public void testAddEdgesWithNodesThatDontExist() {
        service = new NodeServiceImpl();
        String response = "";
        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("ERROR: NODE NOT FOUND", response);
        }
    }

    @Test
    public void testRemoveEdges() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";
        for (int i = 0; i < 12; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("EDGE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.removeEdge(edge.getInitialNode(), edge.getTargetNode());
            assertEquals("EDGE REMOVED", response);
        }
    }

    @Test
    public void testRemoveEdgesThatDontExist() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";
        for (int i = 0; i < 12; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("EDGE ADDED", response);
        }

        response = service.removeEdge("Node0", "Node11");
        assertEquals("EDGE REMOVED", response);
    }

    @Test
    public void testRemoveEdgesWithNodesThatDontExist() {
        service = new NodeServiceImpl();
        String response = "";

        response = service.removeEdge("Node0", "Node11");
        assertEquals("ERROR: NODE NOT FOUND", response);
    }

    @Test
    public void testShortestPath() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";

        for (int i = 0; i < 12; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("EDGE ADDED", response);
        }

        response = service.shortestPath("Node3", "Node9");
        assertEquals("35", response);
    }

    @Test
    public void testShortestPathNoDirectPath() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";

        for (int i = 0; i < 12; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("EDGE ADDED", response);
        }

        response = service.shortestPath("Node3", "Node5");
        assertEquals("2147483647", response);
    }

    @Test
    public void testCloserThan() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";

        for (int i = 0; i < 12; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("EDGE ADDED", response);
        }

        response = service.closerThan("Node2", 20);
        assertEquals("Node10,Node3,Node4,Node6", response);
    }

    @Test
    public void testCloserThanWithAllGraph() {
        service = new NodeServiceImpl();
        String response = "";
        String nodeName = "Node%d";

        for (int i = 0; i < 12; i++) {
            response = service.addNode(String.format(nodeName, i));
            assertEquals("NODE ADDED", response);
        }

        for (TestWeightedEdge edge : createEdges()) {
            response = service.addEdge(edge.getInitialNode(), edge.getTargetNode(), edge.getWeight());
            assertEquals("EDGE ADDED", response);
        }

        response = service.closerThan("Node0", 100);
        assertEquals("Node1,Node10,Node11,Node2,Node3,Node4,Node5,Node6,Node7,Node8,Node9", response);
    }


    private List<TestWeightedEdge> createEdges() {
        List<TestWeightedEdge> edges = new ArrayList<>();
        edges.add(new TestWeightedEdge("Node0", "Node1", 1));
        edges.add(new TestWeightedEdge("Node0", "Node2", 2));
        edges.add(new TestWeightedEdge("Node0", "Node2", 3));
        edges.add(new TestWeightedEdge("Node1", "Node5", 6));
        edges.add(new TestWeightedEdge("Node2", "Node3", 5));
        edges.add(new TestWeightedEdge("Node2", "Node4", 6));
        edges.add(new TestWeightedEdge("Node3", "Node6", 9));
        edges.add(new TestWeightedEdge("Node3", "Node10", 2));
        edges.add(new TestWeightedEdge("Node3", "Node10", 14));
        edges.add(new TestWeightedEdge("Node3", "Node10", 24));
        edges.add(new TestWeightedEdge("Node6", "Node7", 13));
        edges.add(new TestWeightedEdge("Node6", "Node8", 14));
        edges.add(new TestWeightedEdge("Node6", "Node8", 15));
        edges.add(new TestWeightedEdge("Node7", "Node9", 16));
        edges.add(new TestWeightedEdge("Node10", "Node7", 17));
        edges.add(new TestWeightedEdge("Node10", "Node11", 21));

        return edges;
    }

    private class TestWeightedEdge {
        private String initialNode;
        private String targetNode;
        private Integer weight;

        public TestWeightedEdge(String initialNode, String targetNode, Integer weight) {
            this.initialNode = initialNode;
            this.targetNode = targetNode;
            this.weight = weight;
        }

        public String getInitialNode() {
            return initialNode;
        }

        public void setInitialNode(String initialNode) {
            this.initialNode = initialNode;
        }

        public String getTargetNode() {
            return targetNode;
        }

        public void setTargetNode(String targetNode) {
            this.targetNode = targetNode;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }
    }
}
