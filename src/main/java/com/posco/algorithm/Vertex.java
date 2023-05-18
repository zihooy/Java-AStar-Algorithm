package com.posco.algorithm;

public class Vertex {

	public int id;
	public double dist;
	public int parent;
	public double heuristic;

	public Vertex(int id, double dist, int parent) {
		this.id = id;
		this.dist = dist;
		this.parent = parent;
	}

//	public Vertex(Vertex node) {
//		this.id = node.id;
//		this.dist = node.dist;
//		this.parent = node.parent;
//	}
	
	public Vertex(int id, double dist, int parent, double heuristic) {
        this.id = id;
        this.dist = dist;
        this.parent = parent;
        this.heuristic = heuristic;
    }

	public Vertex(Vertex node) {
        this.id = node.id;
        this.dist = node.dist;
        this.parent = node.parent;
        this.heuristic = node.heuristic;
    }
}