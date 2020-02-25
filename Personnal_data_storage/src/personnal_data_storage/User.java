package personnal_data_storage;

import java.util.ArrayList;

public class User extends Node{
	private ArrayList<Integer> dataIds;
	private int reachableSystemNodeId; // can only communicate with one SystemNode
	
	public User(int id,  int reachableSystemNodeId) {
		super(id);
		this.dataIds = new ArrayList<Integer>();
		this.reachableSystemNodeId = reachableSystemNodeId;
	}

	public ArrayList<Integer> getDataIds() {
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
		// TODO Auto-generated method stub
		return null;
	}

}
