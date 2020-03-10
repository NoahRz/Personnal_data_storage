package personnal_data_storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Graph {	
	private ArrayList<Node> nodes;
	private ArrayList<CommunicationTime> communicationTimes;
	private ArrayList<Data> data;
	
	
	public Graph() {
		this.nodes = new ArrayList<Node>();
		this.data = new ArrayList<Data>();
		this.communicationTimes = new ArrayList<CommunicationTime>();
	}
	
	public void addData(Data data) {
		this.data.add(data);
	}
	
	public void addNode(Node node) { // trying to add a node to a user
		this.nodes.add(node);
		
	}
	
	public void linkNodetoNode(int nodeId1, int nodeId2, double weightCommunicationTime){	// WARNING : try to replace user and snode with userId and snodeId
		/** link a node to another node and create the link between them (communicationTime)
		 * */
		
		Node node1 = this.getNode(nodeId1);
		Node node2 = this.getNode(nodeId2);
		
		System.out.println("0");
		
		if ((node1 instanceof User) || (node2 instanceof User)){
			System.out.println("01");
			
			System.out.println(node1 instanceof User && node1.getReachableNodesIds().isEmpty());

			System.out.println(node2 instanceof User && node2.getReachableNodesIds().isEmpty());
			
			System.out.println(node1 instanceof User);
			System.out.println(node1.getReachableNodesIds().isEmpty());

			if (node1 instanceof User && node1.getReachableNodesIds().isEmpty()) {
				System.out.println("1");
				node1.addNode(nodeId2);
				node2.addNode(nodeId1);
				this.addCommunicationTime(new CommunicationTime(nodeId1, nodeId2, weightCommunicationTime));
			}
			if (node2 instanceof User && node2.getReachableNodesIds().isEmpty()) {
				System.out.println("2");
				node2.addNode(nodeId1);
				node2.addNode(nodeId1);
				this.addCommunicationTime(new CommunicationTime(nodeId1, nodeId2, weightCommunicationTime));
			}
		}
		else {
			System.out.println("02");
			if (!node1.getReachableNodesIds().contains(nodeId2) && !node2.getReachableNodesIds().contains(nodeId2)) {
				System.out.println("3");
				node1.addNode(nodeId2);
				node2.addNode(nodeId1);
				this.addCommunicationTime(new CommunicationTime(nodeId1, nodeId2, weightCommunicationTime));
			}
		}
	}
	
	public void linkNodetoNode(SystemNode snode, SystemNode snode1, double weightCommunicationTime) {
		
	}
	
	public void addCommunicationTime(CommunicationTime ct) {
		this.communicationTimes.add(ct);
	}
	
	public void addDataToUser(Data data, User user) { 
		// Do I have to check if the data and the user are already in the graph ? 
		// Do I have to check if the data base is full ? 
		if (this.getAvailableStorage() == 0) {
			System.out.println("The data base is full, please add a new system node or delete some data");
		}
		else {
			// Dijkstra
			Node node = this.getMostOptimizedNode(data, user);
			if (node != null){
				node.getData().add(data);
				user.addDataId(data.getId());
				System.out.println("Data added succesfully");
			}
			else {
				System.out.println("There is not enough space in any System node available, please add a new one or delete some data");
			}
		}
	}
	
	public Node getMostOptimizedNode(Data data, User user) { //Dijsktra
		
		ArrayList<Integer> systemNodesToVisitIds = new ArrayList<Integer>(); 
		// Array of system node to visit's id
		for (Node node : nodes) {
			if (!(node instanceof User)){
				systemNodesToVisitIds.add(node.getId());
			}
		}
		
		Map<Integer,Double> communicatingTimeMap = new HashMap<Integer,Double>();  
		// map gathering id of system node and the shortest time to communicate from the user (id, communicatingTime)
		
		for (Integer snId : systemNodesToVisitIds) {
			communicatingTimeMap.put(snId, Double.POSITIVE_INFINITY);
		}
		
		Integer currentSystemNodeId = user.getReachableSystemNodeId();
		systemNodesToVisitIds.remove(currentSystemNodeId);
		
		return this.getMostOptimizedNodeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data, user);
	}

	public Node getMostOptimizedNodeAlgorithm(ArrayList<Integer> systemNodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentSystemNodeId, Data data, User user) {
		/** Use Djikstra algorithm to find the best SystemNode to add the data
		 * */
		
		if((this.getNode(currentSystemNodeId)).getAvailableStorage() >= data.getSize()) {
			
			return (this.getNode(currentSystemNodeId));
		}
		
		else if(systemNodesToVisitIds.isEmpty()) {  // si on arrive ici ca ne veut pas dire qu'il n'y a plus d'espace dans la base de données mais que aucun noeud system ne peut contenir la donnée. 
			return null;
			
		}
		
		else {
			//update the communication time from the user to each unvisited system node next to the current system node
			
			for (Integer systemNodeNeighbourId: this.getNode(currentSystemNodeId).getReachableNodesIds()) {
				if (systemNodesToVisitIds.contains(systemNodeNeighbourId)) {
					Double communicatingTime = communicatingTimeMap.get(currentSystemNodeId) + this.getCommunicationTime(currentSystemNodeId, systemNodeNeighbourId);
					if (communicatingTime < communicatingTimeMap.get(systemNodeNeighbourId)) {
						communicatingTimeMap.replace(systemNodeNeighbourId, communicatingTime);
					}
				}
			}
			//find the closest unvisited system node from the user (in time)
			Double minTime = Double.POSITIVE_INFINITY;
			Integer closestSystemNodeId = systemNodesToVisitIds.get(0);
			
			for(Integer systemNodeId:systemNodesToVisitIds) { // To optimize, don't check the index 0
				if (communicatingTimeMap.get(systemNodeId) < minTime) {
					minTime = communicatingTimeMap.get(systemNodeId);
					closestSystemNodeId =  systemNodeId;
				}
			}
			// recursion
			systemNodesToVisitIds.remove(closestSystemNodeId);
			currentSystemNodeId = closestSystemNodeId;
			
			return this.getMostOptimizedNodeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data, user);
		}
	}
	
	private Double getCommunicationTime(Integer nodeId0, Integer nodeId1) {
		for (CommunicationTime ct : communicationTimes) {
			if(ct.getNodesIds().contains(nodeId0) && ct.getNodesIds().contains(nodeId1)) {
				return ct.getTime();
			}
		}
		return null;
	}

	private Node getNode(int id) {
		for (Node node : nodes) {
			if (node.getId() == id) {
				return node;
			}
		}
		return null;	}

	public int getCapacity() {
		int capacity = 0;
		for (Node node : nodes) {
			if (node instanceof SystemNode)
			capacity += node.getCapacity();
		}
		return capacity;
	}
	
	public int getAvailableStorage() {
		int availableStorage =0;
		for (Node node : nodes) {
			availableStorage += node.getAvailableStorage();
		}
		return availableStorage; 
	}

	public Data getData(Integer dataId) { //unused
		for (Data data : data) {
			if (data.getId() == dataId) {
				return data;
			}
		}
		return null;
	}
	public void displayGraph() {
		for (Node node:nodes) {
			System.out.println("node :" + node.getClass()+ "id : "+ (String)(node.getId() +" " +node.getReachableNodesIds())+ " data : " + node.getDataIds());
		}
	}
	
// Question 3
	public Node getMostOptimizedNodeForTwoUsers(Data data, User user1, User user2) {
		System.out.println(this.getMostOptimizedNodeWithTime(data, user1));
		ArrayList<Node> list = new ArrayList<Node>(this.getMostOptimizedNodeWithTime(data, user1).keySet());
		System.out.println(list.get(0).getId());
		Node midNode = (Node) this.getMostOptimizedNodeWithTime(data, user1).keySet().toArray()[0]; // need to have the time to get to midNode -> d1
		ArrayList<Node> shortestPathFromMidNodeToUser2 = this.getShortestPath(midNode, user2); // midNode is also in the arrayList and user2 is also in it
		// shortestPathFromMidNodeToUser2 is a ArrayList<Node>  gathering all the nodes to get to user2.
		
		
		Double timeFromUser1ToMidNode = this.getMostOptimizedNodeWithTime(data, user1).get(midNode);
		Double timeFromMidNodeToUser2 = 0.0;
		for(int i = 0; i <shortestPathFromMidNodeToUser2.size()-1; i++) {
			timeFromMidNodeToUser2 = timeFromMidNodeToUser2 + this.getCommunicationTime(shortestPathFromMidNodeToUser2.get(i).getId(), shortestPathFromMidNodeToUser2.get(i+1).getId());
		}
		
		Double deltaMidNodeMin = Math.abs(timeFromMidNodeToUser2 - timeFromMidNodeToUser2);
		ArrayList<Node> nodeToVisit = new ArrayList<Node>();
		for (Node node: shortestPathFromMidNodeToUser2) {
			if (node.getAvailableStorage()>=data.getSize()){
				nodeToVisit.add(node);
			}
		}
		if (nodeToVisit.isEmpty()){
			return midNode;
		}
		for (Node node: nodeToVisit) {
			Node midNode1 = node;
			for(int i=shortestPathFromMidNodeToUser2.indexOf(midNode); i< shortestPathFromMidNodeToUser2.indexOf(midNode1); i++){
				timeFromUser1ToMidNode = timeFromUser1ToMidNode + this.getCommunicationTime(shortestPathFromMidNodeToUser2.get(i).getId(), shortestPathFromMidNodeToUser2.get(i+1).getId());
			}
			timeFromMidNodeToUser2 = 0.0;
			for(int i = shortestPathFromMidNodeToUser2.indexOf(midNode1); i <shortestPathFromMidNodeToUser2.size()-1; i++) {
				timeFromMidNodeToUser2 = timeFromMidNodeToUser2 + this.getCommunicationTime(shortestPathFromMidNodeToUser2.get(i).getId(), shortestPathFromMidNodeToUser2.get(i+1).getId());
			}
			
			
			Double deltaMidNode1 = Math.abs(timeFromUser1ToMidNode - timeFromMidNodeToUser2); // d1:time from user1 to midNode1 and d2 : time from user 2 to midNode2 (WARNING : still the same path)
			if (deltaMidNode1 < deltaMidNodeMin) {
				midNode = node;
				deltaMidNodeMin = deltaMidNode1;
			}
		}
		return midNode;
	}
	
	public HashMap<Node,Double> getMostOptimizedNodeWithTime(Data data, User user){
		ArrayList<Integer> systemNodesToVisitIds = new ArrayList<Integer>(); 
		// Array of system node to visit's id
		for (Node node : nodes) {
			if (!(node instanceof User)){
				systemNodesToVisitIds.add(node.getId());
			}
		}
		
		Map<Integer,Double> communicatingTimeMap = new HashMap<Integer,Double>();  
		// map gathering id of system node and the shortest time to communicate from the user (id, communicatingTime)
		
		for (Integer snId : systemNodesToVisitIds) {
			communicatingTimeMap.put(snId, Double.POSITIVE_INFINITY);
		}
		
		Integer currentSystemNodeId = user.getReachableSystemNodeId();
		systemNodesToVisitIds.remove(currentSystemNodeId);
		
		return this.getMostOptimizedNodeWithTimeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data, user);
	}
	
	public HashMap<Node,Double> getMostOptimizedNodeWithTimeAlgorithm(ArrayList<Integer> systemNodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentSystemNodeId, Data data, User user) {
		/** Use Djikstra algorithm to find the best SystemNode to add the data
		 * 
		 * return a HashMap with the best SystemNode and the connection time between it and the user
		 * */
		
		if((this.getNode(currentSystemNodeId)).getAvailableStorage() >= data.getSize()) {
			
			HashMap<Node, Double>  res = new HashMap<Node,Double>();
			res.put(this.getNode(currentSystemNodeId), communicatingTimeMap.get(currentSystemNodeId));
			return res;
		}
		
		else if(systemNodesToVisitIds.isEmpty()) {  // si on arrive ici ca ne veut pas dire qu'il n'y a plus d'espace dans la base de données mais que aucun noeud system ne peut contenir la donnée. 
			return null;
			
		}
		
		else {
			//update the communication time from the user to each unvisited system node next to the current system node
			
			for (Integer systemNodeNeighbourId: this.getNode(currentSystemNodeId).getReachableNodesIds()) {
				if (systemNodesToVisitIds.contains(systemNodeNeighbourId)) {
					Double communicatingTime = communicatingTimeMap.get(currentSystemNodeId) + this.getCommunicationTime(currentSystemNodeId, systemNodeNeighbourId);
					if (communicatingTime < communicatingTimeMap.get(systemNodeNeighbourId)) {
						communicatingTimeMap.replace(systemNodeNeighbourId, communicatingTime);
					}
				}
			}
			//find the closest unvisited system node from the user (in time)
			Double minTime = Double.POSITIVE_INFINITY;
			Integer closestSystemNodeId = systemNodesToVisitIds.get(0);
			
			for(Integer systemNodeId:systemNodesToVisitIds) { // To optimize, don't check the index 0
				if (communicatingTimeMap.get(systemNodeId) < minTime) {
					minTime = communicatingTimeMap.get(systemNodeId);
					closestSystemNodeId =  systemNodeId;
				}
			}
			// recursion
			systemNodesToVisitIds.remove(closestSystemNodeId);
			currentSystemNodeId = closestSystemNodeId;
			
			return this.getMostOptimizedNodeWithTimeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data, user);
		}
	}

	public ArrayList<Node> getShortestPath(Node startNode, Node endNode) {
		/**
		 * return the shortest path between node and user (from node to user)
		 * return : ArrayList of Node
		 */
		
		ArrayList<Integer> NodesToVisitIds = new ArrayList<Integer>(); 
		// Array of system node to visit's id
		for (Node node1 : nodes) {
					NodesToVisitIds.add(node1.getId()); // there are also all the users, but not the startNode
		}
		
		Map<Integer,Double> communicatingTimeMap = new HashMap<Integer,Double>();  
		// map gathering id of system node and the shortest time to communicate from the user (id, communicatingTime)
		
		
		
		for (Integer snId : NodesToVisitIds) {
			if(snId != startNode.getId()) {
				communicatingTimeMap.put(snId, Double.POSITIVE_INFINITY);
			}
			else {
				communicatingTimeMap.put(snId, 0.0);
			}
		}
				
		Map<Node, ArrayList<Node>> paths = new HashMap<Node, ArrayList<Node>>();
		// Map gathering the shortest path to get to each Node expect the start node
		// ex: paths = {"Node1" : [Node2, Node3], "Node2":[Node3], ...} (exept the startNode)
		Map<Node, Node> secondToLasts = new HashMap<Node, Node>();
		// map gathering the node and the second to last node before this node (exept the startNode)
		for (Node node1 : nodes) {
			if (node1 != startNode) {
				paths.put(node1, null);
				secondToLasts.put(node1, null);
			}
		}

		Integer currentSystemNodeId = startNode.getId();
		NodesToVisitIds.remove(currentSystemNodeId);
		
		return this.getShortestPathAlgorithm(NodesToVisitIds, communicatingTimeMap, currentSystemNodeId, paths, secondToLasts,  endNode);
	}
	
	public ArrayList<Node> getShortestPathAlgorithm(ArrayList<Integer> NodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentSystemNodeId, Map<Node, ArrayList<Node>> paths, Map<Node, Node> secondToLasts,  Node endNode){
		/** Use Djikstra algorithm to find the best SystemNode to add the data
		 * */
		
		
		if((this.getNode(currentSystemNodeId)) == endNode) {
			
			ArrayList<Node> pathToGetToEndNode = paths.get(this.getNode(currentSystemNodeId));
			pathToGetToEndNode.add(endNode);
			return pathToGetToEndNode;
		}
		
		else if(NodesToVisitIds.isEmpty()) {  // si on arrive ici ca ne veut pas dire qu'il n'y a plus d'espace dans la base de données mais que aucun noeud system ne peut contenir la donnée. 
			return null;	
		}
		
		else {
			//update the communication time from the user to each unvisited system node next to the current system node
			
			for (Integer systemNodeNeighbourId: this.getNode(currentSystemNodeId).getReachableNodesIds()) {
				if (NodesToVisitIds.contains(systemNodeNeighbourId)) {
					Double communicatingTime = communicatingTimeMap.get(currentSystemNodeId) + this.getCommunicationTime(currentSystemNodeId, systemNodeNeighbourId);
					if (communicatingTime < communicatingTimeMap.get(systemNodeNeighbourId)) {
						communicatingTimeMap.replace(systemNodeNeighbourId, communicatingTime);
						secondToLasts.replace(this.getNode(systemNodeNeighbourId), this.getNode(currentSystemNodeId));
					}
				}
			}
			
			//find the closest unvisited system node from the user (in time)
			Double minTime = Double.POSITIVE_INFINITY;
			Integer closestSystemNodeId = null;
			
			for(Integer systemNodeId:NodesToVisitIds) { // To optimize, don't check the index 0
				if (communicatingTimeMap.get(systemNodeId) < minTime) {
					minTime = communicatingTimeMap.get(systemNodeId);
					closestSystemNodeId =  systemNodeId;
				}
			}
			// recursion
			NodesToVisitIds.remove(closestSystemNodeId);
			
			
			
			if (paths.get(secondToLasts.get(this.getNode(closestSystemNodeId))) != null) {
				
				ArrayList<Node> pathList = new ArrayList<Node>();
				pathList.add(secondToLasts.get(this.getNode(closestSystemNodeId)));
				
				for(Node prevNode: paths.get(secondToLasts.get(this.getNode(closestSystemNodeId)))) {
					
					pathList.add(prevNode);
				}
				paths.replace(this.getNode(closestSystemNodeId), pathList);
			}
			else {
				ArrayList<Node> pathList = new ArrayList<Node>();
				pathList.add(secondToLasts.get(this.getNode(closestSystemNodeId)));
				paths.replace(this.getNode(closestSystemNodeId), pathList);
			}
			
			currentSystemNodeId = closestSystemNodeId;
			
			
			return this.getShortestPathAlgorithm(NodesToVisitIds, communicatingTimeMap, currentSystemNodeId, paths, secondToLasts,  endNode);
		}
	}
}
	
	
