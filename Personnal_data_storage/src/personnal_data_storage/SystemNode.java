package personnal_data_storage;

import java.util.ArrayList;

public class SystemNode {
	private int id;
	private int capacity;
	private ArrayList<Integer> dataIds;
	private ArrayList<Integer> reachableNodeId; //user or other system node
	private Graph graph;
	
	public SystemNode(int id, int capacity, Graph graph) {
		this.id = id;
		this.capacity = capacity;
		this.dataIds = new ArrayList<Integer>();
		this.reachableNodeId = new ArrayList<Integer>();
		this.graph = graph;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getAvailableStorage() {
		int availableStorage = capacity;
		for (Integer dataId: dataIds) {
			availableStorage -= graph.getData(dataId).getSize();
		}
		return availableStorage;
	}

	public int getId() {
		return id;
	}
	
}
