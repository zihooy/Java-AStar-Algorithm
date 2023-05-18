package com.posco.algorithm;

import com.posco.algorithm.FindNearNode.NearNode;
import com.posco.domain.Node;
import com.posco.dto.ListRequest;
import com.posco.dto.ResultResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {

    private final RoadNetwork graph;

    @Autowired
    public AlgorithmService() throws IOException {
        String osmFilepath = "src/main/java/com/posco/gangnam.osm";
        String region = "gangnam";

        graph = new RoadNetwork(region);
        graph.parseOsmFile(osmFilepath);
        graph.reduceToLargestConnectedComponent();
    }

    public ResultResponse findWithAStar(double startX, double startY, double endX, double endY, List<ListRequest> oblist) {
        Integer startId = FindNearNode.findNearNode(startY, startX, graph);
        Integer endId = FindNearNode.findNearNode(endY, endX, graph);
        AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(graph, oblist); // 추가

        double cost = aStarAlgorithm.computeShortestPathCost(startId, endId);
        ArrayList<Integer> shortestPath = aStarAlgorithm.getShortestPath(startId, endId);

        List<Node> nodes = shortestPath.stream()
                .map(idx -> graph.nodes.get(idx))
                .map(n -> new Node((int) n.osmId, n.latitude, n.longitude))
                .collect(Collectors.toList());

        //코스트를 출력해랴
        System.out.println(cost);
        //모든노드 출력 foreach
        for (Node node : nodes) {
            System.out.println(node.toString());
        }
        return new ResultResponse(cost, nodes);
    }

    private double getPlusDistance(double startX, double startY, double endX, double endY, Integer startId,
                                   Integer endId) {
        double startNearLat = graph.nodes.get(startId).latitude;
        double startNearLon = graph.nodes.get(startId).longitude;
        double endNearLat = graph.nodes.get(endId).longitude;
        double endNearLon = graph.nodes.get(endId).longitude;
        return plusDistance(startY, startX, startNearLat, startNearLon) + plusDistance(endY, endX, endNearLat, endNearLon);
    }


    public ResultResponse findWithDijkstra(double startX, double startY, double endX, double endY, List<ListRequest> oblist) {
        Integer startId = FindNearNode.findNearNode(startY, startX, graph);
        Integer endId = FindNearNode.findNearNode(endY, endX, graph);

        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph, oblist); // 추가
        double cost = dijkstraAlgorithm.computeShortestPathCost(startId, endId);
        ArrayList<Integer> shortestPath = dijkstraAlgorithm.getShortestPath(startId, endId);

        List<Node> nodes = shortestPath.stream()
                .map(idx -> graph.nodes.get(idx))
                .map(n -> new Node((int) n.osmId, n.latitude, n.longitude))
                .collect(Collectors.toList());

        //코스트를 출력해랴
        System.out.println("Dijkstra");
        System.out.println(cost);
        //모든노드 출력 foreach
        for (Node node : nodes) {
            System.out.println(node.toString());
        }
        return new ResultResponse(cost, nodes);
    }

    public ResultResponse findWithBStar(double startX, double startY, double endX, double endY, List<ListRequest> oblist) {
        PriorityQueue<NearNode> startNodes = FindNearNode.find(startY, startX, graph);
        PriorityQueue<NearNode> endNodes = FindNearNode.find(endY, endX, graph);

        double minCost = Double.MAX_VALUE;
        ArrayList<Integer> shortestPath = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            NearNode startNode = startNodes.poll();
            AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(graph, oblist);
            for (int j = 0; j < 5; j++) {
                NearNode endNode = endNodes.poll();
                double cost = aStarAlgorithm.computeShortestPathCost(startNode.getId(), endNode.getId());
                if (cost < minCost) {
                    shortestPath = aStarAlgorithm.getShortestPath(startNode.getId(), endNode.getId());
                    minCost = cost;
                }
            }
        }

        List<Node> nodes = shortestPath.stream()
                .map(idx -> graph.nodes.get(idx))
                .map(n -> new Node((int) n.osmId, n.latitude, n.longitude))
                .collect(Collectors.toList());


        //코스트를 출력해랴
        System.out.println(minCost);
        //모든노드 출력 foreach
        for (Node node : nodes) {
            System.out.println(node.toString());
        }
        return new ResultResponse(minCost, nodes);
    }

    public ResultResponse findWithCStar(double startX, double startY, double endX, double endY, List<ListRequest> oblist) {
        PriorityQueue<NearNode> startNodes = FindNearNode.find(startY, startX, graph);
        PriorityQueue<NearNode> endNodes = FindNearNode.find(endY, endX, graph);

        double minCost = Double.MAX_VALUE;
        ArrayList<Integer> shortestPath = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            NearNode startNode = startNodes.poll();
            CStarAlgorithm aStarAlgorithm = new CStarAlgorithm(graph, oblist);
            for (int j = 0; j < 5; j++) {
                NearNode endNode = endNodes.poll();
                double cost = aStarAlgorithm.computeShortestPathCost(startNode.getId(), endNode.getId());
                if (cost < minCost) {
                    shortestPath = aStarAlgorithm.getShortestPath(startNode.getId(), endNode.getId());
                    minCost = cost;
                }
            }
        }

        List<Node> nodes = shortestPath.stream()
                .map(idx -> graph.nodes.get(idx))
                .map(n -> new Node((int) n.osmId, n.latitude, n.longitude))
                .collect(Collectors.toList());

        //코스트를 출력해랴
        System.out.println(minCost);
        //모든노드 출력 foreach
        for (Node node : nodes) {
            System.out.println(node.toString());
        }
        return new ResultResponse(minCost, nodes);
    }

    public List<Node> findAll(){
        return graph.nodes.stream()
                .map(n -> new Node((int) n.osmId, n.latitude, n.longitude))
                .collect(Collectors.toList());
    }

    private double plusDistance(double latA, double logA, double latB, double lonA){
        return HaversineDistance.distance(latA, logA, latB, lonA);
    }
}
