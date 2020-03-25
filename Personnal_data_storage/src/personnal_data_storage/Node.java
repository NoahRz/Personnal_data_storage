package personnal_data_storage;

import java.util.ArrayList;

public abstract class Node {
	protected int id;
	
	public Node(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	protected abstract int getAvailableStorage();

	protected abstract ArrayList<Integer> getReachableNodesIds();

	protected abstract int getCapacity();

	protected abstract void addNode(int nodeId);

	protected abstract ArrayList<Data> getData();
	
	protected abstract void displayData();
	
	protected abstract ArrayList<Integer> getDataIds();

	public abstract ArrayList<Data> addOptimizedData(ArrayList<Data> listOfData);
}
