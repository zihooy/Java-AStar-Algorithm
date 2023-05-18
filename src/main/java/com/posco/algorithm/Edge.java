package com.posco.algorithm;

public class Edge {
	public int headNode;
	public double length;
	public double travelTime;
	
	public Edge(int headNode, double length, double travelTime) {
		this.headNode = headNode;
		this.length = length;
		this.travelTime = travelTime;	
	}
}
