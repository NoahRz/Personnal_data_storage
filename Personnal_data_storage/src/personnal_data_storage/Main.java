package personnal_data_storage;

public class Main {
	
	public static void test0() {
		Graph graph = new Graph();
		SystemNode sn0 = new SystemNode(0, 10, graph);
		User user1 = new User(1, 0);
		Data data2 = new Data(2, 1);
		
		graph.addNode(sn0);
		graph.addData(data2);
		graph.addData(data2, user1);
		
		graph.displayGraph();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test0();
	}
}
