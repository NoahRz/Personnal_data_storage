package personnal_data_storage;

import java.util.ArrayList;

public class Main {
	
	public static void test0() {
		Graph graph = new Graph();
		SystemNode sn0 = new SystemNode(0, 3); // index and capacity
		SystemNode sn1 = new SystemNode(3,3);
		User user1 = new User(1);
		Data data1 = new Data(2, 2);
		Data data2 = new Data(4, 2);
		Data data3 = new Data(5,2);
		
		graph.addNode(user1);
		
		graph.addNode(sn0);
		graph.addNode(sn1);
		
		graph.addData(data1);
		graph.addData(data2);
		
		graph.linkNodetoNode(user1.getId(), sn0.getId(), 1);
		graph.linkNodetoNode(sn0.getId(), sn1.getId(), 1);
		
		graph.addDataToUser(data1, user1);
		graph.addDataToUser(data2, user1);
		graph.addDataToUser(data3, user1);
		
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
