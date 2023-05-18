package com.posco.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RoadNetwork extends DefaultHandler {
	
	String region;
	
	public int numNodes;
	public int numEdges;
	
	public ArrayList<ArrayList<Edge>> outgoingEdges;
	public ArrayList<ArrayList<Edge>> incomingEdges;
	
	public ArrayList<Node> nodes;

	public Map<Long, Integer> osmIdToNodeIndex;
	
	public ArrayList<String> roadTypes;
	
	public ArrayList<Long> wayNodes;
	public Map<String, Integer> speeds;
	
	private boolean inWay = false;
	private boolean isHighway = false;
	private String key, valHighway, valOneway;
	
	// Constructor  
	public RoadNetwork(String region) throws NumberFormatException, IOException {
		this.region = region;
				
		numNodes = 0;
		numEdges = 0;
		
		outgoingEdges = new ArrayList<>();
		incomingEdges = new ArrayList<>();
		nodes = new ArrayList<>();
		
		speeds = new HashMap<>();
		speeds.put("motorway", 110);
		speeds.put("trunk", 110);
		speeds.put("primary", 70);
		speeds.put("secondary", 60);
		speeds.put("tertiary", 50);
		speeds.put("motorway_link", 50);
		speeds.put("trunk_link", 50);
		speeds.put("primary_link", 50);
		speeds.put("secondary_link", 50);
		speeds.put("road", 40);
		speeds.put("unclassified", 40);
		speeds.put("residential", 30);
		speeds.put("unsurfaced", 30);
		speeds.put("living_street", 10);
		speeds.put("service", 5);
	}
	
	private void addNode(long osmId, double latitude, double longitude) {
		Node node = new Node(osmId, latitude, longitude);
		
		outgoingEdges.add(new ArrayList<Edge>());
		incomingEdges.add(new ArrayList<Edge>());
		nodes.add(node);
		osmIdToNodeIndex.put(osmId, numNodes);
		numNodes += 1;
	}
	
	private void addEdge(int baseNode, int headNode, double length, double travelTime) {
		Edge outgoingEdge = new Edge(headNode, length, travelTime);
		Edge incomingEdge = new Edge(baseNode, length, travelTime);
		
		outgoingEdges.get(baseNode).add(outgoingEdge);
		incomingEdges.get(headNode).add(incomingEdge);
		numEdges += 1;
	}
	
	public void parseOsmFile(String osmFilepath) {
		osmIdToNodeIndex = new HashMap<>();
		wayNodes = new ArrayList<>();
		roadTypes = new ArrayList<>();
		valOneway = "no";
		
		File xmlDoc = new File(osmFilepath);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();			
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(xmlDoc, this);		  
		}
		catch(Exception e) {
			System.out.println("Problem, " + e.toString());
		} 
	}
	
	// At beginning of tag
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("node")) {
			long osmId = Long.parseLong(attributes.getValue("id"));
			float lat = Float.parseFloat(attributes.getValue("lat"));
			float lon = Float.parseFloat(attributes.getValue("lon"));			
			addNode(osmId, lat, lon);
		}
		else if (qName.equalsIgnoreCase("way")) {
			inWay = true;
		}
		else if (qName.equalsIgnoreCase("nd") && inWay) {
			wayNodes.add(Long.parseLong(attributes.getValue("ref")));
		}
		else if (qName.equalsIgnoreCase("tag") && inWay) {
			key = attributes.getValue("k");
			if (key.equals("highway")) {
				isHighway = true;
				valHighway = attributes.getValue("v");
				if (!roadTypes.contains(valHighway)) {
					roadTypes.add(valHighway);
				}
			}			
			if (key.equals("oneway")) {
				valOneway = attributes.getValue("v");
			}
		}
	}
  
	// At end of tag
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("way") && isHighway && speeds.keySet().contains(valHighway)) {
			int speed = speeds.get(valHighway);
			
			// Insert edges for each two consecutive nodes in the way
			long baseNodeOsmId = wayNodes.get(0);
			int baseNode = osmIdToNodeIndex.get(baseNodeOsmId);
			
			for(int i=1; i<wayNodes.size(); i++) {
				long headNodeOsmId = wayNodes.get(i);
				int headNode = osmIdToNodeIndex.get(headNodeOsmId);
				
				double length = HaversineDistance.distance(nodes.get(baseNode).latitude, nodes.get(baseNode).longitude, nodes.get(headNode).latitude, nodes.get(headNode).longitude);
				double travelTime = length/speed;
				
				if (valOneway.equals("yes") || valOneway.equals("1")) {
					addEdge(baseNode, headNode, length, travelTime);
				}
				
				else if (valOneway.equals("-1")) {
					addEdge(headNode, baseNode, length, travelTime);					
				}
				
				else {
					addEdge(baseNode, headNode, length, travelTime);	
					addEdge(headNode, baseNode, length, travelTime);	
				}				
				
				baseNodeOsmId = headNodeOsmId;
				baseNode = headNode;
			}
			wayNodes.clear();
			valOneway = "no";
			inWay = false;
		}
	}


	public void reduceToLargestConnectedComponent() {
		ArrayList<ArrayList<Edge>> outgoingEdgesCopy = new ArrayList<>();

		for (int i=0; i<numNodes; i++) {
			ArrayList<Edge> edges = outgoingEdges.get(i);
			ArrayList<Edge> edgesCopy = new ArrayList<>();
			for (int j=0; j<edges.size(); j++) {
				int headNode = edges.get(j).headNode;
				double length = edges.get(j).length;
				double travelTime = edges.get(j).travelTime;
				edgesCopy.add(new Edge(headNode, length, travelTime));
			}
			outgoingEdgesCopy.add(edgesCopy);
		}

		for(int i=0; i<numNodes; i++) {
			ArrayList<Edge> inEdges = incomingEdges.get(i);
			ArrayList<Edge> outEdges = outgoingEdges.get(i);
			for(int j=0; j<inEdges.size(); j++) {
				boolean found = false;
				for (int k=0; k<outEdges.size(); k++) {
					if (inEdges.get(j).headNode == outEdges.get(k).headNode) {
						found = true;
						break;
					}
				}
				if (!found)
					outEdges.add(inEdges.get(j));
			}
		}

		int round = 1;
		int rep = 0;
		int largestSize = 0;
		int largestMark = 1;

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(this, new ArrayList<>());

		for (int i=0; i<numNodes; i++) {

			//System.out.println(i);
			// Run Dijkstra from node i if not visited in a previous processing
			// and i has at least a single Edge connection
			if (dijkstra.visitedNodes.get(i) != 0 ||
					dijkstra.graph.outgoingEdges.get(i).size() == 0)
				continue;

			dijkstra.computeShortestPathCost(i, -1);
			dijkstra.setVisitedNodeMark(round);

			rep = 0;
			for (int j=0; j<numNodes; j++) {
				if (dijkstra.visitedNodes.get(j) == round)
					rep += 1;
			}

			if (rep > largestSize) {
				largestSize = rep;
				largestMark = round;
			}

			if (largestSize >= numNodes/2)
				break;
			round += 1;
		}

		// Set back outgoingEdges array to its original content
		outgoingEdges = outgoingEdgesCopy;

		// Set to null all nodes that are not in the Largest Connected Component
		// and calculate offset (number of nodes to remove)
		ArrayList<Integer> nodesNewIndexes = new ArrayList<Integer>();
		int offset = numNodes-largestSize;

		for (int i=0; i<numNodes; i++) {
			if (dijkstra.visitedNodes.get(i) != largestMark) {
				incomingEdges.set(i, null);
				outgoingEdges.set(i, null);
				nodes.set(i, null);
			}
			nodesNewIndexes.add(-1);
		}

		dijkstra = null;

		// Update nodes indexes
		for (int i=numNodes-1; i>0; i--) {
			if (nodes.get(i) == null)
				offset -=1;
			else
				nodesNewIndexes.set(i, i-offset);
		}

		// Remove all nodes that are not in the Largest Connected Component
		incomingEdges.removeAll(Collections.singleton(null));
		outgoingEdges.removeAll(Collections.singleton(null));
		nodes.removeAll(Collections.singleton(null));

		// Update head-nodes indices in the Adjacency Matrix
		// Update number of Nodes and Edges of the reduced Graph
		numNodes = nodes.size();
		numEdges = 0;

		for (int i=0; i<numNodes; i++) {
			ArrayList<Edge> outEdges = outgoingEdges.get(i);
			ArrayList<Edge> inEdges = incomingEdges.get(i);
			numEdges += inEdges.size();

			for (int j=0; j<outEdges.size(); j++) {
				int oldIndex = outEdges.get(j).headNode;
				int newIndex = nodesNewIndexes.get(oldIndex);
				if (newIndex != -1)
					outgoingEdges.get(i).get(j).headNode = newIndex;
			}
			for (int j=0; j<inEdges.size(); j++) {
				int oldIndex = inEdges.get(j).headNode;
				int newIndex = nodesNewIndexes.get(oldIndex);
				if (newIndex != -1)
					incomingEdges.get(i).get(j).headNode = newIndex;
			}
		}
		nodesNewIndexes.clear();
	}

}
