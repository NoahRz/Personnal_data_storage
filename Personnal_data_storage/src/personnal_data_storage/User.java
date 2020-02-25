package personnal_data_storage;

import java.util.ArrayList;

public class User {
	private int id;
	private ArrayList<Integer> dataIds;
	private int reachableSystemNodeId; // can only communicate with one SystemNode
	
	public User(int id, int reachableSystemNodeId) {
		this.id = id;
		this.dataIds = new ArrayList<Integer>();
		this.reachableSystemNodeId = reachableSystemNodeId;
	}

	public ArrayList<Integer> getDataIds() {
		return dataIds;
	}

	public int getId() {
		return id;
	}

	public int getReachableSystemNodeId() {
		return reachableSystemNodeId;
	}
	
}
