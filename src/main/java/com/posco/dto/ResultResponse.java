package com.posco.dto;

import com.posco.domain.Node;
import java.util.List;

//추가
public class ResultResponse {

    private double cost;
    private List<Node> nodes;

    public ResultResponse(double cost, List<Node> nodes) {
        this.cost = cost;
        this.nodes = nodes;
    }

    public double getCost() {
        return cost;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
