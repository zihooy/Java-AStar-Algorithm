package com.posco.algorithm;

import com.posco.dto.ListRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class CStarAlgorithm {
	public RoadNetwork graph;
	public ArrayList<Integer> visitedNodes = new ArrayList<>();

	private final int inf = Integer.MAX_VALUE;
	private Comparator<Vertex> comparator;
	private PriorityQueue<Vertex> distances;
	private ArrayList<Vertex> settled;
	public ArrayList<Integer> obstacleList;


	public CStarAlgorithm(RoadNetwork graph, List<ListRequest> oblist) {
		this.graph = graph;
		comparator = new DistanceComparator();
		distances = new PriorityQueue<>(comparator);
		settled = new ArrayList<>();
		obstacleList = new ArrayList<>();

		for (ListRequest request:oblist) {

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
		settled.clear();

		for (int i = 0; i < graph.numNodes; i++) {
			if (i == sourceNodeId)
				distances.add(new Vertex(i, 0, sourceNodeId, 0));
			else if(!obstacleList.contains(i))
				distances.add(new Vertex(i, inf, i, 0));
			settled.add(null);
		}

		// distances에서 요소를 하나씩 추출하면서 최단 경로를 계산
		while (!distances.isEmpty()) {
			
			Vertex node = distances.poll();
			int id = node.id;
			double dist = node.dist;

			if (node.parent == -1)
				System.out.println(id);

			if (settled.get(id) != null)
				continue;

			settled.set(id, node);

			// 현재 노드가 목적지 노드(targetNodeId)인 경우, 경로를 반환하기 위해 path에 경로를 추가하고 path를 반환
			if (targetNodeId != -1 && settled.get(targetNodeId) != null) {
				return settled.get(targetNodeId).dist;
			}

			// 현재 node 와 연결된 모든 edge 를 확인
			ArrayList<Edge> edges = graph.outgoingEdges.get(id);

			for (Edge edge : edges) {
				double distToNode = dist + edge.travelTime;
				int currentNodeId = edge.headNode;
				double heuristic = (HaversineDistance.distance(graph.nodes.get(currentNodeId).latitude,
						graph.nodes.get(currentNodeId).longitude, graph.nodes.get(targetNodeId).latitude,
						graph.nodes.get(targetNodeId).longitude) / 2);

				if (obstacleList.contains(edge.headNode)) {
					continue;
				}

				if (distToNode < inf && distToNode >= 0) {
					distances.add(new Vertex(edge.headNode, distToNode, id, heuristic));
				}
			}
		}
		return 0;
	}

	public ArrayList<Integer> getShortestPath(int source, int target) {
		ArrayList<Integer> path = new ArrayList<>();

		if (settled.get(target).dist == inf)
			return null;

		path.add(target);
		int parent = settled.get(target).parent;

		while (parent != source) {
			path.add(0, parent);
			parent = settled.get(parent).parent;
		}
		path.add(0, source);

		return path;
	}

	public double calculateHeuristic(int currentNodeId, int targetNodeId) {
		// 현재 노드와 목적지 노드의 좌표를 가져온다
		double currentNodeX = graph.nodes.get(currentNodeId).latitude;
		double currentNodeY = graph.nodes.get(currentNodeId).longitude;
		double targetNodeX = graph.nodes.get(targetNodeId).latitude;
		double targetNodeY = graph.nodes.get(targetNodeId).longitude;

		// 유클리드 거리 계산
		double distance = Math.sqrt(Math.pow(targetNodeX - currentNodeX, 2) + Math.pow(targetNodeY - currentNodeY, 2));

		return distance;
	}

	public class AStarResult {
		ArrayList<Integer> path;
		double cost;

		public AStarResult(ArrayList<Integer> path, double cost) {
			this.path = path;
			this.cost = cost;
		}

		public ArrayList<Integer> getPath() {
			return path;
		}

		public double getCost() {
			return cost;
		}
	}

	class DistanceComparator implements Comparator<Vertex> {
		public int compare(Vertex v1, Vertex v2) {
			if (v1.dist + v1.heuristic < v2.dist + v2.heuristic) {
				return -1;
			}
			if (v1.dist + v1.heuristic > v2.dist + v2.heuristic) {
				return 1;
			}
			return 0;
		}
	}

}
