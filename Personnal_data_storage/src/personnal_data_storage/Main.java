package personnal_data_storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

	public static void question2(ArrayList<Data> data) {
		/**
		 * Add a bunch of data one by one to a user in the graph 
		 * param data: ArrayList of Data*/
		
		//Initialisation
		Graph graph = new Graph();
		SystemNode sn0 = new SystemNode(0, 3); // index and capacity
		SystemNode sn1 = new SystemNode(3,3);
		User user1 = new User(1);
		
		graph.addNode(user1);	
		graph.addNode(sn0);
		graph.addNode(sn1);
		
		
		graph.linkNodetoNode(user1.getId(), sn0.getId(), 1);
		graph.linkNodetoNode(sn0.getId(), sn1.getId(), 1);
		
		// sort the array of data by id
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Data d: data) {
			ids.add(d.getId());
		}
		Collections.sort(ids);
		
		for(Integer id:ids) {
			for(Data d:data) {
				if (d.getId()==id) {
					graph.addData(d);
					graph.addDataToUser(d, user1);
				}
			}
		}
		graph.displayGraph();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//test0();
		
		//Question 2  Add a bunch of data from arrayList one by one to a user in the graph 
		Data data1 = new Data(2, 2);
		Data data2 = new Data(4, 2); // notice the the order of data by id (not ascending order)
		Data data3 = new Data(5,2);
		ArrayList<Data> data = new ArrayList<>(Arrays.asList(data3, data2, data1));
		question2(data);
	}
}
