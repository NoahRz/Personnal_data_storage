package personnal_data_storage;

import java.util.ArrayList;

public class User extends Node{
	private ArrayList<Integer> dataIds;
	private int reachableSystemNodeId = -1 ; // can only communicate with one SystemNode
	// we put -1 because Integer has 0 as default value and we want to start id at 0.
	
	public User(int id) {
		super(id);
		this.dataIds = new ArrayList<Integer>();
	}

	protected ArrayList<Integer> getDataIds() {
		return dataIds;
	}


	public int getReachableSystemNodeId() {
		return reachableSystemNodeId;
	}

	@Override
	protected int getAvailableStorage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected ArrayList<Integer> getReachableNodesIds() {
		/**
		 * return the reachableNodeIds in a list
		 * @return ArrayList<Integer>
		 **/

		// TODO Auto-generated method stub
		ArrayList<Integer> list = new ArrayList<Integer>();
		if (reachableSystemNodeId == -1) {  // if it's -1, that means that it's empty
			return list;
		}
		else {
			list.add(reachableSystemNodeId);
			return list;
		}	
	}
	
	@Override
	protected int getCapacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addSystemNode(int id) {
		// TODO Auto-generated method stub
		this.reachableSystemNodeId = id;
	}

	@Override
	protected void addNode(int nodeId) {
		// TODO Auto-generated method stub
		this.reachableSystemNodeId = nodeId;
	}

	@Override
	protected ArrayList<Data> getData() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	protected void displayData() {
		// TODO Auto-generated method stub
	}

	public void addDataId(Integer dataId) {
		// TODO Auto-generated method stub
		this.dataIds.add(dataId);
	}

	protected ArrayList<Data> addOptimizedData(ArrayList<Data> listOfData, User user) {
		return null;
	}
}
