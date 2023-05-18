package com.posco.algorithm;

import com.posco.dto.ListRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraAlgorithm {

	public RoadNetwork graph;
	public ArrayList<Integer> visitedNodes = new ArrayList<Integer>();

	private final int inf = Integer.MAX_VALUE;
	private Comparator<Vertex> comparator;
	private PriorityQueue<Vertex> distances;
	private ArrayList<Vertex> setteled;
	public ArrayList<Integer> obstacleList;


	// Constructor
	public DijkstraAlgorithm(RoadNetwork graph, List<ListRequest> oblist) {
		this.graph = graph;
		this.obstacleList = new ArrayList<>();
		this.comparator = new DistanceComparator();
		this.distances = new PriorityQueue<>(comparator);
		this.setteled = new ArrayList<Vertex>();

		for (ListRequest request : oblist) {

			Integer id = FindNearNode.findNearNode(request.getY(), request.getX(), graph);
			obstacleList.add(id);
			for(int i=0;i<graph.numNodes;i++){
				double dist = HaversineDistance.distance(request.getY(), request.getX(), graph.nodes.get(i).latitude, graph.nodes.get(i).longitude);
				if(dist <= 15){
					System.out.println("15미터 이내: "+ FindNearNode.findNearNode(graph.nodes.get(i).latitude, graph.nodes.get(i).longitude,graph));
					obstacleList.add(FindNearNode.findNearNode(graph.nodes.get(i).latitude, graph.nodes.get(i).longitude, graph));
				}
			}
		}

		for (int i = 0; i < graph.numNodes; i++) {
			visitedNodes.add(0);
		}
	}
	public double computeShortestPathCost(int sourceNodeId, int targetNodeId) {
		distances.clear();
		setteled.clear();

		for (int i = 0; i < graph.numNodes; i++) {
			if (i == sourceNodeId)
				distances.add(new Vertex(i, 0, sourceNodeId));
			else if(!obstacleList.contains(i))
				distances.add(new Vertex(i, inf, i, 0));
			setteled.add(null);
		}


		// distances에서 요소를 하나씩 추출하면서 최단 경로를 계산
		while (!distances.isEmpty()) {
			// 추출된 노드의 id와 dist
			Vertex node = new Vertex(distances.poll());
			int id = node.id;
			double dist = node.dist;

			if(targetNodeId != -1 && setteled.get(targetNodeId) != null) {
				return setteled.get(targetNodeId).dist;
			}

			if (setteled.get(id) != null) {
				continue;
			}

			setteled.set(id, node);

			for (Edge edge : graph.outgoingEdges.get(id)) {
				double distToNode = dist + edge.travelTime;

				if(obstacleList.contains(edge.headNode))
					continue;

				if (distToNode < inf && distToNode >= 0) {
					distances.add(new Vertex(edge.headNode, distToNode, id));
				}
			}
		}
		return 0;
	}

	

	// Mark all nodes visited by the previous call to computeShortestPath
	public void setVisitedNodeMark(int mark) {
		for (int i = 0; i < setteled.size(); i++) {
			if (setteled.get(i).dist != inf)
				visitedNodes.set(i, mark);
		}
	}

	// Get list of nodes that make the shortest path
	public ArrayList<Integer> getShortestPath(int source, int target) {
		ArrayList<Integer> path = new ArrayList<Integer>();

		if (setteled.get(target).dist == inf)
			return null;

		path.add(target);
		int parent = setteled.get(target).parent;

		while (parent != source) {
			path.add(0, parent);
			parent = setteled.get(parent).parent;
		}
		path.add(0, source);

		return path;
	}

	public class DijkstraResult {
		ArrayList<Integer> path;
		double cost;

		public DijkstraResult(ArrayList<Integer> path, double cost) {
			this.path = path;
			this.cost = cost;
		}

		public ArrayList<Integer> getPath() {
			return path;
		};

		public double getCost() {
			return cost;
		};
	}
}

class DistanceComparator implements Comparator<Vertex> {

	// 거리순으로 내림차순 정렬
	@Override
	public int compare(Vertex v1, Vertex v2) {

		if (v1.dist < v2.dist) {
			return -1;
		}
		if (v1.dist > v2.dist) {
			return 1;
		}
		return 0;
	}
}

