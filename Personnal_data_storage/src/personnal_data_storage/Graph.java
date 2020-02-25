package personnal_data_storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
//	private ArrayList<User> users;
//	private ArrayList<SystemNode> systemNodes;
	
	private ArrayList<Node> nodes;
	private ArrayList<CommunicationTime> communicationTimes;
	private ArrayList<Data> data;
	
	
	public Graph() {
//		this.users = new ArrayList<User>();
//		this.systemNodes = new ArrayList<SystemNode>();
		this.nodes = new ArrayList<Node>();
		this.data = new ArrayList<Data>();
		this.communicationTimes = new ArrayList<CommunicationTime>();
	}
	
	public void addData(Data data, User user) { 
		// Do I have to check if the data and the user are already in the graph ? 
		// Do I have to check if the data base is full ? 
		if (this.getAvailableStorage() == 0) {
			System.out.println("The data base is full, please add a new system node or delete some data");
		}
		else {
			// Dijkstra
			this.addingData(data, user);
			//System.out.println("Data added succesfully");		*** pas forcement
		}
	}
	
	public void addingData(Data data, User user) { //Dijsktra
		
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
		
		//SystemNode systemNodeCurrent = (SystemNode) this.getNode(user.getReachableSystemNodeId());
		Integer currentSystemNodeId = user.getReachableSystemNodeId();
		systemNodesToVisitIds.remove(currentSystemNodeId);
		
		this.addingDataAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data);
	}

	public void addingDataAlgorithm(ArrayList<Integer> systemNodesToVisitIds, Map<Integer, Double> communicatingTimeMap,
			Integer currentSystemNodeId, Data data) {
		
		if((this.getNode(currentSystemNodeId)).getAvailableStorage() >= data.getSize()) {
			(this.getNode(currentSystemNodeId)).getDataIds().add(data.getId());
		}
		
		else if(systemNodesToVisitIds.isEmpty()) {  // si on arrive ici ca ne veut pas dire qu'il n'y a plus d'espace dans la base de données mais que aucun noeud system ne peut contenir la donnée. 
			System.out.println("there is not enough space in any System node available, please add a new one or delete some data");
		}
		
		else {
			
			if((this.getNode(currentSystemNodeId)).getAvailableStorage() >= data.getSize()) {
				(this.getNode(currentSystemNodeId)).getDataIds().add(data.getId());
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
				
				this.addingDataAlgorithm(systemNodesToVisitIds, communicatingTimeMap, currentSystemNodeId, data);
				
			}
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
			capacity += ((SystemNode) node).getCapacity();
		}
		return capacity;
	}
	
	public int getAvailableStorage() {
		int availableStorage =0;
		for (Node node : nodes) {
			availableStorage += ((SystemNode) node).getAvailableStorage();
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
}
