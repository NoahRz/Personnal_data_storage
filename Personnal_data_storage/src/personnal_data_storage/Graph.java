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
		/**
		 * Add a node to list nodes
		 * */
		this.nodes.add(node);
	}

	public void addCommunicationTime(CommunicationTime ct) { this.communicationTimes.add(ct); }

	private Double getCommunicationTime(Integer nodeId0, Integer nodeId1) {
		/**
		 * return the communication Time between two nodes directly connected
		 * @param nodeId0: Integer
		 * @param nodeId1: Integer
		 * @return : Double
		 * */
		for (CommunicationTime ct : communicationTimes) {
			if(ct.getNodesIds().contains(nodeId0) && ct.getNodesIds().contains(nodeId1)) {
				return ct.getTime();
			}
		}
		return null;
	}

	public Node getNode(int id) {
		/**
		 * return the Node corresponding to the id
		 * @param id: int
		 * @return Node
		 * */
		for (Node node : nodes) {
			if (node.getId() == id) {
				return node;
			}
		}
		return null;	}

	public int getCapacity() {
		/**
		 * return the capacity of the graph
		 * @return : int
		 * */
		int capacity = 0;
		for (Node node : nodes) {
			if (node instanceof SystemNode)
				capacity += node.getCapacity();
		}
		return capacity;
	}

	public int getAvailableStorage() {
		/**
		 * return the available storage
		 * @return : int
		 * */
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
		/**
		 * display the graph (simple display): Node - directly connected Node
		 * */
		for (Node node:nodes) {
			System.out.println("node :" + node.getClass()+ "id : "+ (String)(node.getId() +" " +node.getReachableNodesIds())+ " data : " + node.getDataIds());
		}
	}

	public void linkNodetoNode(int nodeId0, int nodeId1, double weightCommunicationTime){	// WARNING : try to replace user and snode with userId and snodeId
		/**
		 * link a node to another node and create the link between them (communicationTime)
		 * @param nodeId0: int
		 * @param nodeId1: int
		 * @param weightCommunicationTime: double
		 * */
		
		Node node0 = this.getNode(nodeId0);
		Node node1 = this.getNode(nodeId1);
		

		if ((node0 instanceof User) || (node1 instanceof User)){

			if (node0 instanceof User && node0.getReachableNodesIds().isEmpty()) {
				node0.addNode(nodeId1);
				node1.addNode(nodeId0);
				this.addCommunicationTime(new CommunicationTime(nodeId0, nodeId1, weightCommunicationTime));
			}
			if (node1 instanceof User && node1.getReachableNodesIds().isEmpty()) {
				node1.addNode(nodeId0);
				node0.addNode(nodeId1);
				this.addCommunicationTime(new CommunicationTime(nodeId0, nodeId1, weightCommunicationTime));
			}
		}
		else {
			if (!node0.getReachableNodesIds().contains(nodeId1) && !node1.getReachableNodesIds().contains(nodeId1)) {
				node0.addNode(nodeId1);
				node1.addNode(nodeId0);
				this.addCommunicationTime(new CommunicationTime(nodeId0, nodeId1, weightCommunicationTime));
			}
		}
	}

	public void addDataToUser(Data data, User user) {
		/**
		 * add the data to a user
		 * @param data: Data
		 * @param user: User
		 * */
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
		/*
		 * return the most optimized node to store the data interested by only one user
		 * @param data: Data
		 * @param user: User
		 * @return : Node
		 * */
		
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
		
		return this.getMostOptimizedNodeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data);
	}

	public Node getMostOptimizedNodeAlgorithm(ArrayList<Integer> systemNodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentSystemNodeId, Data data) { // user parameter is no more useful
		/*
		 * by using Djikstra algorithm, to find and return the best SystemNode to add the data interested by only one user
		 * @param systemNodesToVisitIds : ArrayList<Integer>
		 * @param communicatingTimeMap : Map<Integer, Double>
		 * @param currentSystemNodeId : Integer
		 * @param data : Data
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
			
			return this.getMostOptimizedNodeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data);
		}
	}

// Question 3
	/**
	 * Purpose: we want to add a data interested by two user
	 * process :
	 * 1) We look for the best node able to store the data which is first interested by only one user
	 * 2) Once found, we look for the shortest path between this node and the other user
	 * 3) The best node able to store the data for these two users can only be among the first node found and nodes in the shortest path
	 * 4) Among those nodes we look for the closest one from the two users and which has enough space to store the data*/

	public HashMap<Node,Double> getMostOptimizedNodeWithTime(Data data, User user){
		/**
		 * return the best node able to store the data and the time connection from the user to the node found
		 * @param data: Data
		 * @param user: User
		 * @return : HashMap<Node,Double>
		 **/

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
		
		return this.getMostOptimizedNodeWithTimeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data);
	}
	
	public HashMap<Node,Double> getMostOptimizedNodeWithTimeAlgorithm(ArrayList<Integer> systemNodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentSystemNodeId, Data data) { // user parameter is not useful
		/*
		 *  return the best node able to store the data and the time connection from the user to the node found by using Djisktra algorithm
		 *@param systemNodesToVisitIds : ArrayList<Integer>
		 *@param communicatingTimeMap : Map<Integer, Double>
		 *@param currentSystemNodeId : Integer
		 *@param data : Data
		 *@return : HashMap<Node,Double>
		 */

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
			
			return this.getMostOptimizedNodeWithTimeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data);
		}
	}

	public ArrayList<Node> getShortestPath(Node startNode, Node endNode) {
		/*
		 * return the shortest path between two nodes
		 * @return : ArrayList<Node>
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
		/**
		 * return the shortest path between two nodes
		 * @param NodesToVisitIds : ArrayList<Integer>
		 * @param communicatingTimeMap : Map<Integer, Double>
		 * @param currentSystemNodeId : Integer
		 * @param paths : Map<Node, ArrayList<Node>>
		 * @param secondToLasts : Map<Node, Node>
		 * @param endNode : Node
		 * @return : ArrayList<Node>
		 */

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

	public Node getMostOptimizedNodeForTwoUsers(Data data, User user0, User user1) {
		/**
		 * return the most optimized node able to store a data interested by two users
		 * @param user0 : User
		 * @param user0 : User
		 * @param data : Data
		 * @return Node
		 * */

		ArrayList<Node> list = new ArrayList<Node>(this.getMostOptimizedNodeWithTime(data, user0).keySet());
		System.out.println(list.get(0).getId());
		Node midNode = (Node) this.getMostOptimizedNodeWithTime(data, user0).keySet().toArray()[0]; // need to have the time to get to midNode -> d1
		ArrayList<Node> shortestPathFromMidNodeToUser1 = this.getShortestPath(midNode, user1); // midNode is also in the arrayList and user2 is also in it
		// shortestPathFromMidNodeToUser1 is a ArrayList<Node>  gathering all the nodes to get to user1.

		Double timeFromUser0ToMidNode = this.getMostOptimizedNodeWithTime(data, user0).get(midNode);
		Double timeFromMidNodeToUser1 = 0.0;
		for(int i = 0; i <shortestPathFromMidNodeToUser1.size()-1; i++) {
			timeFromMidNodeToUser1 = timeFromMidNodeToUser1 + this.getCommunicationTime(shortestPathFromMidNodeToUser1.get(i).getId(), shortestPathFromMidNodeToUser1.get(i+1).getId());
		}

		Double deltaMidNodeMin = Math.abs(timeFromUser0ToMidNode - timeFromMidNodeToUser1);
		ArrayList<Node> nodeToVisit = new ArrayList<Node>();
		for (Node node: shortestPathFromMidNodeToUser1) {
			if (node.getAvailableStorage()>=data.getSize()){
				nodeToVisit.add(node);
			}
		}
		if (nodeToVisit.isEmpty()){
			return midNode;
		}
		for (Node node: nodeToVisit) {
			Node midNode1 = node;
			for(int i=shortestPathFromMidNodeToUser1.indexOf(midNode); i< shortestPathFromMidNodeToUser1.indexOf(midNode1); i++){
				timeFromUser0ToMidNode = timeFromUser0ToMidNode + this.getCommunicationTime(shortestPathFromMidNodeToUser1.get(i).getId(), shortestPathFromMidNodeToUser1.get(i+1).getId());
			}
			timeFromMidNodeToUser1 = 0.0;
			for(int i = shortestPathFromMidNodeToUser1.indexOf(midNode1); i <shortestPathFromMidNodeToUser1.size()-1; i++) {
				timeFromMidNodeToUser1 = timeFromMidNodeToUser1 + this.getCommunicationTime(shortestPathFromMidNodeToUser1.get(i).getId(), shortestPathFromMidNodeToUser1.get(i+1).getId());
			}

			Double deltaMidNode1 = Math.abs(timeFromUser0ToMidNode - timeFromMidNodeToUser1); // d1:time from user1 to midNode1 and d2 : time from user 2 to midNode2 (WARNING : still the same path)
			if (deltaMidNode1 < deltaMidNodeMin) {
				midNode = node;
				deltaMidNodeMin = deltaMidNode1;
			}
		}
		return midNode;
	}

	public void addDataToUserOptimized(ArrayList<Data> data) { //knapsack problem
		/*
		* Algorithm :
		* - find the node directly connected to the user, check if it has enough space to carry all the dad - Yes : finished
		* - No : find the best data (can be several) which can be stored there
		* - once finished, look for the closest Node from the user (Djikstra)
		* - do the same things for this Node (knapsack)
		* - look for the closest from the user (Djikstra)
		* - do the same things for this Node (knapsack)
		* - etc.
		* */
	}

	public void knapsack(SystemNode sNode,ArrayList<Data> data ){
		/**
		 * do the knapsack problem to this snode and return an ArrayList of data which couldn't be added
		 * @param sNode : SystemNode
		 * @param data : ArrayList<Data>
		 */
	}
}
	
	
