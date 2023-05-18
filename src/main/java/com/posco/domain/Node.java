package com.posco.domain;

public class Node {
    private int idx;
    private double x;
    private double y;

    public Node(int idx, double y, double x) {
        this.idx = idx;
        this.y = y;
        this.x = x;
    }

    public int getIdx() {
        return idx;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    @Override
    public String toString() {
        return "Node{" +
                "idx=" + idx +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
