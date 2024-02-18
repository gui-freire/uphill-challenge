package com.uphill.codechallenge.model;

import java.util.Set;

public class EdgeGraph {
    private Set<WeightedEdge> outgoingEdges;
    private Integer totalWeight;

    public EdgeGraph(Set<WeightedEdge> outgoingEdges, Integer totalWeight) {
        this.outgoingEdges = outgoingEdges;
        this.totalWeight = totalWeight;
    }

    public Set<WeightedEdge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public void setOutgoingEdges(Set<WeightedEdge> outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Integer totalWeight) {
        this.totalWeight = totalWeight;
    }
}
