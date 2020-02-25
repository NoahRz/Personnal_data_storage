package personnal_data_storage;

public class CommunicationTime { // edge between 2 node
	private Node[] nodes;
	private int time;
	
	public CommunicationTime(Node n0, Node n1, int time) {
		nodes = new Node[2];
		nodes[0] = n0;
		nodes[1] = n1;
		this.time = time;
	}

	public int getTime() {
		return time;
	}
	
	public Node[] getNodes() {
		return nodes;
	}
}
