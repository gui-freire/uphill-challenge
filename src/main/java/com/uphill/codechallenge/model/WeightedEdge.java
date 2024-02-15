package com.uphill.codechallenge.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class WeightedEdge extends DefaultWeightedEdge {

    public WeightedEdge() {
        super();
    }

    public double getWeight() {
        return super.getWeight();
    }

    public String getSource() {
        return (String) super.getSource();
    }

    public String getTarget() {
        return (String) super.getTarget();
    }
}
