package personnal_data_storage;

import java.util.ArrayList;

public class SystemNode extends Node{
	
	private int capacity;
	private ArrayList<Data> data;
	private ArrayList<Integer> reachableNodesIds; //Node (user or system node)
	
	public SystemNode(int id, int capacity) {
		super(id);
		this.capacity = capacity;
		this.data = new ArrayList<Data>();
		this.reachableNodesIds = new ArrayList<Integer>();
	}

	public int getCapacity() {
		return capacity;
	}

	public int getAvailableStorage() {
		int availableStorage = capacity;
		for (Data dataElement: data) {
			availableStorage -= dataElement.getSize();
		}
		return availableStorage;
	}
	
	protected ArrayList<Data> getData(){
		return data;
	}

	public ArrayList<Integer> getReachableNodesIds() {
		return reachableNodesIds;
	}

	@Override
	protected void addNode(int nodeId) {
		// TODO Auto-generated method stub
		this.reachableNodesIds.add(nodeId);
		System.out.println("b");
	}

	@Override
	protected void displayData() {
		// TODO Auto-generated method stub
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (Data d:data) {
			liste.add(d.getId());
		}
		System.out.println(liste);
	}

	@Override
	protected ArrayList<Integer> getDataIds() {
		// TODO Auto-generated method stub
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (Data d:data) {
			liste.add(d.getId());
		}
		return liste;
	}
	
}
