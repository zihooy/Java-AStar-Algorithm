package com.posco.dto;

import com.posco.domain.Node;
import java.util.List;

public class ArrestNodeResponse {
    private String color;
    private double cost;
    private String algorithm;
    private List<Node> nodes;

    public ArrestNodeResponse(String color, double cost, String algorithm, List<Node> nodes) {
        this.color = color;
        this.cost = cost;
        this.algorithm = algorithm;
        this.nodes = nodes;
    }

    public String getColor() {
        return color;
    }

    public double getCost() {
        return cost;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}

