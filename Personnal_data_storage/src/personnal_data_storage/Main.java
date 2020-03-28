package personnal_data_storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

	public static Graph graph(){
		/**
		 * return a graph ex: graph : User1 - sn0 - sn1 - user2
		 * without any data stored in any SystemNode
		 * @return graph
		 */
		Graph graph = new Graph();
		SystemNode sn0 = new SystemNode(0, 3); // index and capacity
		SystemNode sn1 = new SystemNode(3,3);

		User user1 = new User(1);
		User user2 = new User(6);

		graph.addNode(user1);
		graph.addNode(user2);

		graph.addNode(sn0);
		graph.addNode(sn1);

		graph.linkNodetoNode(user1.getId(), sn0.getId(), 1);
		graph.linkNodetoNode(sn0.getId(), sn1.getId(), 1);
		graph.linkNodetoNode(sn1.getId(), user2.getId(), 1);

		return graph;
	}

	public static void question2() {
		/**
		 * Add a bunch of data one by one to a user in the graph 
		 * param data: ArrayList of Data*/
		//Initialisation
		Data data1 = new Data(2, 2);
		Data data2 = new Data(4, 2);
		Data data3 = new Data(5,2);
		ArrayList<Data> listOfData = new ArrayList<>(Arrays.asList(data3, data2, data1)); // notice the the order of data by id (not ascending order)

		Graph graph = graph();
		User user1 = (User)graph.getNode(1);

		System.out.println("data to add intereseted by user id:" + user1.getId());
		for (Data d: listOfData){
			System.out.println(" data id="+d.getId() + " size: "+ d.getSize());
		}
		System.out.println("\n graph before adding the data :\n");
		graph.displayGraph();
		System.out.println("\n");

		ArrayList<Integer> ids = new ArrayList<Integer>(); // ArrayList of data's ids
		for (Data d: listOfData) {
			ids.add(d.getId());
		}
		// sort the ArrayList of data's ids
		Collections.sort(ids);
		
		for(Integer id:ids) {
			for(Data d:listOfData) {
				if (d.getId()==id) {
					graph.addDataToUser(d, user1);
				}
			}
		}
		System.out.println("\n graph after adding the data :\n");
		graph.displayGraph();
	}
	
	public static void question3() {
		/*
		 * add a data interested by two user
		 * graph : User1 - sn0 - sn1 - user2
		 * */
		Graph graph = graph();
		User user1 = (User) graph.getNode(1);
		User user2 = (User) graph.getNode(6);
		Data data1 = new Data(2, 2);
		Data data2 = new Data(4, 2);
		graph.addDataToUser(data1, user1);
		System.out.println("data id="+data2.getId() + " interested by user id=" +user1.getId() +" and user id=" + user2.getId() );
		System.out.println("\ngraph before adding the data interested by two users:\n");
		graph.displayGraph();

		Node mostOptimizedNodeForTwoUsers = graph.getMostOptimizedNodeForTwoUsers(data2, user1,user2);
		if (mostOptimizedNodeForTwoUsers != null) {
			System.out.println("\nmidNode id:" + mostOptimizedNodeForTwoUsers.getId());
			user1.addDataId(data2.getId());
			user2.addDataId(data2.getId());
			mostOptimizedNodeForTwoUsers.getData().add(data2);
			System.out.println("\ngraph after adding the data interested by two users:\n");
			graph.displayGraph();
		}else{
			System.out.println("there is not enough storage");
		}

}

	public static void question4(){ //knapsack problem
		/*
		* Optimization using Multiple knapsack problem
		* from a bunch of data interested by one user, find the best place to add the data to maximize the SystemNodes.
		* the data have to be added the closest to the user
		* graph : User1 - sn0 - sn1 - user2
		* */

		Data data1 = new Data(2, 1);
		Data data2 = new Data(4, 2); // notice the the order of data by id (not ascending order)
		Data data3 = new Data(5,3);
		Data data4 = new Data(7,1);
		Data data5 = new Data(8,1);
		Data data6 = new Data(9,1);
		ArrayList<Data> listOfData = new ArrayList<>(Arrays.asList(data3, data2, data1));

		Graph graph = graph();
		User user1 = (User)graph.getNode(1);

		System.out.println("data to add intereseted by user id:" + user1.getId());
		for (Data d: listOfData){
			System.out.println(" data id="+d.getId() + " size: "+ d.getSize());
		}

		System.out.println("\ngraph before adding data using knapsack problem :\n");
		graph.displayGraph();
		graph.addDataUsingKnapSack(listOfData,user1);
		System.out.println("\ngraph after adding data using knapsack problem :\n");
		graph.displayGraph();

	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//question2();

		//question3();

		question4(); // Knapsack problem

	}

}
