package personnal_data_storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
	private ArrayList<User> users;
	private ArrayList<SystemNode> systemNodes;
	private ArrayList<Data> data;
	
	
	public Graph() {
		this.users = new ArrayList<User>();
		this.systemNodes = new ArrayList<SystemNode>();
		this.data = new ArrayList<Data>();
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
			System.out.println("Data added succesfully");
		}
	}
	
	public void addingData(Data data2, User user) { //Dijsktra
		ArrayList<SystemNode> systemNodesToVisit = systemNodes;
		Map<Integer,Double> communicatingTime = new HashMap<Integer,Double>();
		
		for (SystemNode sn : systemNodes) {
			communicatingTime.put(sn.getId(), Double.POSITIVE_INFINITY);
		}
		SystemNode systemNodeCurrent = this.getSystemNode(user.getReachableSystemNodeId());
		systemNodesToVisit.remove(systemNodeCurrent);
		this.addingDataAlgorithm(systemNodesToVisit, communicatingTime, systemNodeCurrent);
	}

	public void addingDataAlgorithm(ArrayList<SystemNode> systemNodesToVisit, Map<Integer, Double> communicatingTime,
			SystemNode systemNodeCurrent) {
		if(systemNodesToVisit.isEmpty()) {  // si on arrive ici ca ne veut pas dire qu'il n'y a plus d'espace dans la base de données mais que aucun noeud system ne peut contenir la donnée. 
			System.out.println("there is not enough space in any System node available, please add a new one or delete some data");
		}
		else {
			
		}
	}

	private SystemNode getSystemNode(int reachableSystemNodeId) {
		for (SystemNode sn : systemNodes) {
			if (sn.getId() == reachableSystemNodeId) {
				return sn;
			}
		}
		return null;	}

	public int getCapacity() {
		int capacity = 0;
		for (SystemNode sn : systemNodes) {
			capacity += sn.getCapacity();
		}
		return capacity;
	}
	
	public int getAvailableStorage() {
		int availableStorage =0;
		for (SystemNode sn : systemNodes) {
			availableStorage += sn.getAvailableStorage();
		}
		return availableStorage; 
	}

	public Data getData(Integer dataId) {
		for (Data data : data) {
			if (data.getId() == dataId) {
				return data;
			}
		}
		return null;
	}
}
