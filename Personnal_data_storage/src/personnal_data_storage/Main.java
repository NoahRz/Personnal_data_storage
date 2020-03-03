package personnal_data_storage;

import java.util.ArrayList;

public class Main {
	
	public static void test0() {
		Graph graph = new Graph();
		SystemNode sn0 = new SystemNode(0, 10);
		User user1 = new User(1);
		Data data1 = new Data(2, 1);
		
		graph.addNode(user1);
		graph.addNode(sn0);
		
		graph.addData(data1);
		
		graph.linkNodetoNode(user1.getId(), sn0.getId(), 1);
		
		graph.addDataToUser(data1, user1);
		
		System.out.println(new ArrayList<Integer>());
		System.out.println(user1.getReachableNodesIds());
		System.out.println(user1.getReachableSystemNodeId());
		graph.displayGraph();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test0();
	}
}
