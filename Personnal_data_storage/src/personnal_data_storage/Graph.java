package personnal_data_storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {	
	private ArrayList<Node> nodes;
	private ArrayList<CommunicationTime> communicationTimes;

	public Graph() {
		this.nodes = new ArrayList<Node>();
		this.communicationTimes = new ArrayList<CommunicationTime>();
	}
	
	public void addNode(Node node) { // trying to add a node to a user
		/**
		 * Add a node to list nodes
		 * */
		this.nodes.add(node);
	}

	public void addCommunicationTime(CommunicationTime ct) { this.communicationTimes.add(ct); }

	public Double getCommunicationTime(Integer nodeId0, Integer nodeId1) {
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

	public void displayGraph() {
		/**
		 * display the graph (simple display): Node - directly connected Node
		 * */
		for (Node node:nodes) {
			System.out.println("node :" + node.getClass()+ ", id : "+ node.getId() +", Reachable node ids : "+ node.getReachableNodesIds()+ ", data : " + node.getDataIds() + ", capacity :" + node.getCapacity());
		}
	}

	public void linkNodetoNode(int nodeId0, int nodeId1, double weightCommunicationTime){
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

	/**
	 * Question 2
	 * the purpose is to add a bunch of data to the graph which is only interesting for one user
	 * process:
	 * 1) look for the systemNode which is the closest to the user and which has enough place to store the data
	 * 2) add the data to the systemNode found and the user (id)
	 * */
	public void addDataToUser(Data data, User user) {
		/**
		 * add the data to a user
		 * @param data: Data
		 * @param user: User
		 * */
		if (this.getAvailableStorage() == 0) {
			System.out.println("The data base is full, please add a new system node or delete some data");
		}
		else {
			// Dijkstra
			Node node = this.getMostOptimizedNode(data, user);
			if (node != null){
				node.getData().add(data);
				user.addDataId(data.getId());
				System.out.println("Data id=" + data.getId()+ " added succesfully");
			}
			else {
				System.out.println("There is not enough space in any System node available to add the data id=" + data.getId()+", please add a new one or delete some data");
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
			if (!(node instanceof User)){ // we don't add users because we cannot add data to them
				systemNodesToVisitIds.add(node.getId());
			}
		}
		
		Map<Integer,Double> communicatingTimeMap = new HashMap<Integer,Double>();
		// map gathering id of system node and the shortest time to communicate from the user (id, communicatingTime)

		//initializing the hashMap
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
		 * Using Dijkstra algorithm to find and return the best SystemNode to add the data interested by only one user
		 * @param systemNodesToVisitIds : ArrayList<Integer>
		 * @param communicatingTimeMap : Map<Integer, Double>
		 * @param currentSystemNodeId : Integer
		 * @param data : Data
		 * */

		if((this.getNode(currentSystemNodeId)).getAvailableStorage() >= data.getSize()) {
			return (this.getNode(currentSystemNodeId));
		}
		else if(systemNodesToVisitIds.isEmpty()) {
			// if we end up here it means that there is no systemNode which has enough place to store the data
			return null;
		}
		else {
			//update the communication time from the user to each unvisited system node next to the current system node
			for (Integer systemNodeNeighbourId: this.getNode(currentSystemNodeId).getReachableNodesIds()) {
				if (systemNodesToVisitIds.contains(systemNodeNeighbourId)) { // we check  all the unvisited neighbour systemNode
					Double communicatingTime = communicatingTimeMap.get(currentSystemNodeId) + this.getCommunicationTime(currentSystemNodeId, systemNodeNeighbourId);
					if (communicatingTime < communicatingTimeMap.get(systemNodeNeighbourId)) {
						communicatingTimeMap.replace(systemNodeNeighbourId, communicatingTime);
					}
				}
			}
			//find the closest unvisited system node from the user (in time)
			Double minTime = Double.POSITIVE_INFINITY;
			Integer closestSystemNodeId = systemNodesToVisitIds.get(0);
			
			for(Integer systemNodeId:systemNodesToVisitIds) {
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
	 * Purpose: we want to add a data interested by two users
	 * process :
	 * 1) We look for the best node able to store the data which is first interested by only one user
	 * 2) Once found, we look for the shortest path between this node and the other user.
	 * 3) The best node able to store the data for these two users can only be among the first node found and nodes in the shortest path
	 * 4) Among those nodes we look for the closest one from the two users and which has enough space to store the data
	 * */

	public HashMap<Node,Double> getMostOptimizedNodeWithTime(Data data, User user){
		/**
		 * return the best node able to store the data and the time connection from the user to the node found
		 * @param data: Data
		 * @param user: User
		 * @return : HashMap<Node,Double> Double is the shortest communication time between the node and the systemNode found
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

		Integer currentSystemNodeId = user.getReachableSystemNodeId();

		for (Integer snId : systemNodesToVisitIds) {
			if (snId == currentSystemNodeId){
				communicatingTimeMap.put(currentSystemNodeId, this.getCommunicationTime(user.getId(),currentSystemNodeId));
			}else {
				communicatingTimeMap.put(snId, Double.POSITIVE_INFINITY);
			}
		}
		
		systemNodesToVisitIds.remove(currentSystemNodeId);
		
		return this.getMostOptimizedNodeWithTimeAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data);
	}
	
	public HashMap<Node,Double> getMostOptimizedNodeWithTimeAlgorithm(ArrayList<Integer> systemNodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentSystemNodeId, Data data) {
		/*
		 * return the closest node able to store the data and the time connection from the user to the node found by using Dijkstra algorithm
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
		else if(systemNodesToVisitIds.isEmpty()) {
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
			
			for(Integer systemNodeId:systemNodesToVisitIds) {
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
		 * @param startNode : Node
		 * @param endNode: Node
		 * @return : ArrayList<Node>
		 */

		ArrayList<Integer> nodesToVisitIds = new ArrayList<Integer>();
		// Array of system node to visit's id
		for (Node node1 : nodes) {
			nodesToVisitIds.add(node1.getId()); // there are also all the users in the ArrayList
		}
		
		Map<Integer,Double> communicatingTimeMap = new HashMap<Integer,Double>();  
		// map gathering id of system node and the shortest time to communicate from the startNode (id, communicatingTime)

		for (Integer snId : nodesToVisitIds) {
			if(snId != startNode.getId()) {
				communicatingTimeMap.put(snId, Double.POSITIVE_INFINITY);
			}
			else{
				communicatingTimeMap.put(snId,0.0);
			}
		}

		Map<Node, ArrayList<Node>> paths = new HashMap<Node, ArrayList<Node>>();
		// Map gathering the shortest path to get to each Node expect the start node
		// ex: paths = {"Node1" : [Node2, Node3], "Node2":[Node3], ...} (exept the startNode)
		Map<Node, Node> secondToLasts = new HashMap<Node, Node>();
		// map gathering the node and the second to last node before this node (exept the startNode)
		// ex : {Node0 : secondTolastNode0, Node1 : secondTolastNode1 , ...}
		for (Node node1 : nodes) {
			if (node1 != startNode) {
				paths.put(node1, null);
				secondToLasts.put(node1, null);
			}
		}

		Integer currentNodeId = startNode.getId();
		nodesToVisitIds.remove(currentNodeId);

		return this.getShortestPathAlgorithm(nodesToVisitIds, communicatingTimeMap, currentNodeId, paths, secondToLasts,  endNode);
	}
	
	public ArrayList<Node> getShortestPathAlgorithm(ArrayList<Integer> nodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentNodeId, Map<Node, ArrayList<Node>> paths, Map<Node, Node> secondToLasts,  Node endNode){
		/**
		 * return the shortest path between two nodes
		 * @param nodesToVisitIds : ArrayList<Integer>
		 * @param communicatingTimeMap : Map<Integer, Double>
		 * @param currentNodeId : Integer
		 * @param paths : Map<Node, ArrayList<Node>>
		 * @param secondToLasts : Map<Node, Node>  ex: {Node:Last Node, ...}
		 * @param endNode : Node
		 * @return : ArrayList<Node>
		 */

		if((this.getNode(currentNodeId)) == endNode) { // we reach the endNode
			ArrayList<Node> pathToGetToEndNode = paths.get(this.getNode(currentNodeId));
			pathToGetToEndNode.add(endNode);
			return pathToGetToEndNode;
		}
		else if(nodesToVisitIds.isEmpty()) { //we've visited all the nodes
			return null;	
		}
		else {
			//update the communication time from the user to each unvisited  node next to the current  node
			for (Integer NodeNeighbourId: this.getNode(currentNodeId).getReachableNodesIds()) {
				if (nodesToVisitIds.contains(NodeNeighbourId)) {
					Double communicatingTime = communicatingTimeMap.get(currentNodeId) + this.getCommunicationTime(currentNodeId, NodeNeighbourId);
					if (communicatingTime < communicatingTimeMap.get(NodeNeighbourId)) {
						communicatingTimeMap.replace(NodeNeighbourId, communicatingTime);
						secondToLasts.replace(this.getNode(NodeNeighbourId), this.getNode(currentNodeId));
						// we add this currentNode to the neighbour Node's second to last because we've visited this current node before visiting
						// the neighbour.
					}
				}
			}
			
			//find the closest unvisited  node from the user (in time)
			Double minTime = Double.POSITIVE_INFINITY;
			Integer closestNodeId = null;
			
			for(Integer NodeId:nodesToVisitIds) {
				if (communicatingTimeMap.get(NodeId) < minTime) {
					minTime = communicatingTimeMap.get(NodeId);
					closestNodeId =  NodeId;
				}
			}
			// recursion
			nodesToVisitIds.remove(closestNodeId);

			// update the path to get to the closest Node from the user
			if (paths.get(secondToLasts.get(this.getNode(closestNodeId))) != null) {
				// if the path to get to the closestNode's second to last is not null

				ArrayList<Node> pathList = new ArrayList<Node>();

				for(Node prevNode: paths.get(secondToLasts.get(this.getNode(closestNodeId)))) {
					pathList.add(prevNode);
					// we add the path to get to the closestNode's second to last
				}

				pathList.add(secondToLasts.get(this.getNode(closestNodeId)));//then we add the the closestNode's second to last

				paths.replace(this.getNode(closestNodeId), pathList);
			}
			else {
				// we just add the closest Node's second to last
				ArrayList<Node> pathList = new ArrayList<Node>();
				pathList.add(secondToLasts.get(this.getNode(closestNodeId)));
				paths.replace(this.getNode(closestNodeId), pathList);
			}

			currentNodeId = closestNodeId;

			return this.getShortestPathAlgorithm(nodesToVisitIds, communicatingTimeMap, currentNodeId, paths, secondToLasts,  endNode);
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


		HashMap<Node,Double> hashMapMidNode = this.getMostOptimizedNodeWithTime(data, user0);
		Node midNode = (Node) hashMapMidNode.keySet().toArray()[0]; // midNode is the closest node from the two users and  has enough space to store the data

		ArrayList<Node> shortestPathFromMidNodeToUser1 = this.getShortestPath(midNode, user1);
		// shortestPathFromMidNodeToUser1 is a ArrayList<Node>  gathering all the nodes from midNode to user1.
		// midNode and user1 are also in the ArrayList

		Double timeFromUser0ToMidNode = hashMapMidNode.get(midNode);
		Double timeFromMidNodeToUser1 = 0.0;

		// we calculate the time to get to User1 from midNode by taking the shortest path
		for(int i = 0; i<shortestPathFromMidNodeToUser1.size()-1; i++) {
			timeFromMidNodeToUser1 = timeFromMidNodeToUser1 + this.getCommunicationTime(shortestPathFromMidNodeToUser1.get(i).getId(), shortestPathFromMidNodeToUser1.get(i+1).getId());
		}

		Double deltaMidNodeMin = Math.abs(timeFromUser0ToMidNode - timeFromMidNodeToUser1);
		ArrayList<Node> nodeToVisit = new ArrayList<Node>(); // nodeToVisit is an Array of node which has enough capacity to store the data
		for (Node node: shortestPathFromMidNodeToUser1) {
			if (node.getAvailableStorage()>=data.getSize()){
				nodeToVisit.add(node);
			}
		}

		if (nodeToVisit.isEmpty()){
			return midNode;
		}

		// we look for the node which is as close to user0 than user1
		for (Node CurrentNode: nodeToVisit) {

			// we calculate the time to get from the user0 to the currentNode = the time between user0 and MidNode + time between MidNode and CurrentNode
			for(int i=shortestPathFromMidNodeToUser1.indexOf(midNode); i< shortestPathFromMidNodeToUser1.indexOf(CurrentNode); i++){
				timeFromUser0ToMidNode = timeFromUser0ToMidNode + this.getCommunicationTime(shortestPathFromMidNodeToUser1.get(i).getId(), shortestPathFromMidNodeToUser1.get(i+1).getId());
			}
			// we calculate the time to get from the CurrentNode to User1
			timeFromMidNodeToUser1 = 0.0;
			for(int i = shortestPathFromMidNodeToUser1.indexOf(CurrentNode); i <shortestPathFromMidNodeToUser1.size()-1; i++) {
				timeFromMidNodeToUser1 = timeFromMidNodeToUser1 + this.getCommunicationTime(shortestPathFromMidNodeToUser1.get(i).getId(), shortestPathFromMidNodeToUser1.get(i+1).getId());
			}
			// if the CurrentNode is closer to the user0 and the user1 than the current midNode, it is the new midNode
			Double deltaMidNode1 = Math.abs(timeFromUser0ToMidNode - timeFromMidNodeToUser1);
			if (deltaMidNode1 < deltaMidNodeMin) {
				midNode = CurrentNode;
				deltaMidNodeMin = deltaMidNode1;
			}
		}
		return midNode;
	}

	/*
	* Question 4
	* Purpose : add a bunch of data interested by only one user by using knapsack
	* Process:
	* 1) find the closest system node to the user
	* 2) once found, try to add all the data by using knapsack
	* 3) if there is some data left (couldn't have been added in the systemNode)
	* 4) find the next closest systemNode and repeat the process.
	* */
	public int addDataUsingKnapSack(ArrayList<Data> listOfData, User user) { //Dijsktra
		/*
		 * try to add all the data in the the closest system node from the user using knapsack
		 * @param ArrayList<Data>: Data
		 * @param user: User
		 * */

		ArrayList<Integer> systemNodesToVisitIds = new ArrayList<Integer>();
		// Array of system node to visit's id
		for (Node node : nodes) {
			if (!(node instanceof User)){ // we don't add users because we cannot add data to them
				systemNodesToVisitIds.add(node.getId());
			}
		}

		Map<Integer,Double> communicatingTimeMap = new HashMap<Integer,Double>();
		// map gathering id of system node and the shortest time to communicate from the user (id, communicatingTime)

		//initializing the hashMap
		for (Integer snId : systemNodesToVisitIds) {
			communicatingTimeMap.put(snId, Double.POSITIVE_INFINITY);
		}

		Integer currentSystemNodeId = user.getReachableSystemNodeId();
		systemNodesToVisitIds.remove(currentSystemNodeId);


		return this.addDataUsingKnapSackAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, listOfData, user);
	}

	public int addDataUsingKnapSackAlgorithm(ArrayList<Integer> systemNodesToVisitIds, Map<Integer,
			Double> communicatingTimeMap, Integer currentSystemNodeId, ArrayList<Data> listOfData, User user) {

		ArrayList<Data> unaddedData = this.getNode(currentSystemNodeId).addOptimizedData(listOfData,user); // knapsack
		if (unaddedData.isEmpty()) { // if it's true, we successfully added all the data
			return 1;
		}
		else { // we look for the next closest System node able to store the data.
			if(systemNodesToVisitIds.isEmpty()) {
				// if we end up here that means that there sis no systemNode which has enough place to store the data
				return 0;
			}
			else {
				//update the communication time from the user to each unvisited system node next to the current system node
				for (Integer systemNodeNeighbourId: this.getNode(currentSystemNodeId).getReachableNodesIds()) {
					if (systemNodesToVisitIds.contains(systemNodeNeighbourId)) { // we check for all unvisited neighbour systemNode
						Double communicatingTime = communicatingTimeMap.get(currentSystemNodeId) + this.getCommunicationTime(currentSystemNodeId, systemNodeNeighbourId);
						if (communicatingTime < communicatingTimeMap.get(systemNodeNeighbourId)) {
							communicatingTimeMap.replace(systemNodeNeighbourId, communicatingTime);
						}
					}
				}
				//find the closest unvisited system node from the user (in time)
				Double minTime = Double.POSITIVE_INFINITY;
				Integer closestSystemNodeId = systemNodesToVisitIds.get(0);

				for(Integer systemNodeId:systemNodesToVisitIds) {
					if (communicatingTimeMap.get(systemNodeId) < minTime) {
						minTime = communicatingTimeMap.get(systemNodeId);
						closestSystemNodeId =  systemNodeId;
					}
				}
				// recursion
				systemNodesToVisitIds.remove(closestSystemNodeId);
				currentSystemNodeId = closestSystemNodeId;

				return this.addDataUsingKnapSackAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, unaddedData,user);
			}
		}

	}
}
	
	
