package personnal_data_storage;

import java.util.ArrayList;

public class CommunicationTime { // edge between 2 node
	private ArrayList<Integer> nodesIds;
	private Double time;
	
	public CommunicationTime(Integer nodeId0, Integer nodeId1, Double time) {
		this.nodesIds = new ArrayList<Integer>();
		nodesIds.add(nodeId0);
		nodesIds.add(nodeId1);
//		nodes = new Node[2];
//		nodes[0] = n0;
//		nodes[1] = n1;
		this.time = time;
	}

	public Double getTime() {
		return time;
	}
	
	public ArrayList<Integer> getNodesIds() {
		return nodesIds;
	}
}
