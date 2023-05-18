package com.posco.dto;

import java.util.List;

public class ArrestRequest {

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    private List<ListRequest> obstacleList;


    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public List<ListRequest> getObstacleList() { return obstacleList; }
}
