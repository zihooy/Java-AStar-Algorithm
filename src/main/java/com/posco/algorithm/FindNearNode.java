package com.posco.algorithm;

import java.util.Comparator;
import java.util.PriorityQueue;

public class FindNearNode {
	public static Integer findNearNode(double lat, double lon, RoadNetwork graph) {
		double minDist = Integer.MAX_VALUE;
		int id = 0;
		for (int i = 0; i < graph.nodes.size(); i++) {
			double dist = HaversineDistance.distance(lat, lon, graph.nodes.get(i).latitude,
					graph.nodes.get(i).longitude);
			if (minDist > dist) {
				minDist = dist;
				id = i;
			}
		}
		return id;
	}

	public static PriorityQueue<NearNode> find(double lat, double lon, RoadNetwork graph) {
		PriorityQueue<NearNode> nearNodes = new PriorityQueue<>(Comparator.comparing((o)->o.dist));
		for (int i = 0; i < graph.nodes.size(); i++) {
			double dist = HaversineDistance.distance(lat, lon, graph.nodes.get(i).latitude, graph.nodes.get(i).longitude);
			nearNodes.add(new NearNode(i, dist));
		}
		return nearNodes;
	}

	static class NearNode{
		private int id;
		private double dist;

		public NearNode(int id, double dist) {
			this.id = id;
			this.dist = dist;
		}

		public int getId() {
			return id;
		}

		public double getDist() {
			return dist;
		}
	}


}