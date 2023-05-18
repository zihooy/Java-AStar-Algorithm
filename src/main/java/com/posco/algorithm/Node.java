package com.posco.algorithm;

public class Node {
	// The OSM id of the node.
	public long osmId;

	public double latitude;
	public double longitude;
	
	public Node(long osmId, double latitude, double longitude) {
		this.osmId = osmId;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return (String.valueOf(latitude) + "," + String.valueOf(longitude));		
	}
}
