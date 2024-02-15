package com.uphill.codechallenge.service;

public interface NodeService {
    String addNode(String node);

    String addEdge(String initialNode, String finalNode, int weight);

    String removeNode(String initialNode);

    String removeEdge(String initialNode, String finalNode);

    String shortestPath(String initialNode, String finalNode);

    String closerThan(String node, Integer weight);
}
