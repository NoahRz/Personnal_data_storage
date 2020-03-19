package personnal_data_storage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class SystemNode extends Node{
	
	private int capacity;
	private ArrayList<Data> data;
	private ArrayList<Integer> reachableNodesIds; //Node (user or system node)
	private ArrayList<HashMap<Data, Boolean>> resultPathKnapSack = new ArrayList<>();


	public SystemNode(int id, int capacity) {
		super(id);
		this.capacity = capacity;
		this.data = new ArrayList<Data>();
		this.reachableNodesIds = new ArrayList<Integer>();
	}

	public int getCapacity() {
		return capacity;
	}

	public int getAvailableStorage() {
		int availableStorage = capacity;
		for (Data dataElement: data) {
			availableStorage -= dataElement.getSize();
		}
		return availableStorage;
	}
	
	protected ArrayList<Data> getData(){
		return data;
	}

	public ArrayList<Integer> getReachableNodesIds() {
		return reachableNodesIds;
	}

	@Override
	protected void addNode(int nodeId) {
		// TODO Auto-generated method stub
		this.reachableNodesIds.add(nodeId);
		System.out.println("b");
	}

	@Override
	protected void displayData() {
		// TODO Auto-generated method stub
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (Data d:data) {
			liste.add(d.getId());
		}
		System.out.println(liste);
	}

	@Override
	protected ArrayList<Integer> getDataIds() {
		// TODO Auto-generated method stub
		ArrayList<Integer> liste = new ArrayList<Integer>();
		for (Data d:data) {
			liste.add(d.getId());
		}
		return liste;
	}


	public void addOptimizedData(ArrayList<Data> listOfData){
		/**
		 * add a bunch of data by using knapsack problem
		 * @param listOfData : ArrayList<Data>
		 * */
		int n = listOfData.size();
		ArrayList<Data> newListOfData = new ArrayList<Data>();
		newListOfData.add(null);
		for (Data d: listOfData){
			newListOfData.add(d);
		}

		this.knapsack(newListOfData, n, capacity, null);

		int max = 0;
		int indexMax = 0; // index of the hashMap which has the most "true"
		for (HashMap<Data, Boolean> path : resultPathKnapSack){
			int cpt= 0;
			for (Data key : path.keySet()){
				if (path.get(key)){ // equals to == true
					cpt ++;
				}
			}
			if (cpt>=max){
				max = cpt;
				indexMax = resultPathKnapSack.indexOf(path);
			}
		}

		HashMap<Data, Boolean> dataToAdd = resultPathKnapSack.get(indexMax);
		for(Data key : dataToAdd.keySet()){
			if (dataToAdd.get(key)){ // // equals to == true
				this.getData().add(key);
			}
		}
	}
	public int knapsack(ArrayList<Data> listOfData, int n, int capacity, HashMap<Data, Boolean> prev){
		/**
		 * return the best combination of data to add in this system node using knapsack problem
		 * @param listOfData : ArrayList<Data>
		 * @param n : int, the current index of data in the listOfData
		 * @param capacity : int the capacity of this systemNode
		 * @param prev : HashMap<Data, Boolean> gathering all the data to get to the current data and show if the data has been added in the systemNode (true or false)
		 * */

		if (prev == null){
				prev = new HashMap<Data, Boolean>();
			}
			if (n== 0 || capacity==0){
				resultPathKnapSack.add(prev);
				return 0;
			}
			else if (listOfData.get(n).getSize()>capacity){
				HashMap<Data, Boolean> prev0 = prev;
				prev0.put(listOfData.get(n), false);
				return this.knapsack(listOfData, n-1, capacity, prev0);
			}
			else{
				HashMap<Data, Boolean> prev1 = prev;
				prev1.put(listOfData.get(n), false);
				int temp1 = this.knapsack(listOfData, n-1, capacity, prev1);

				HashMap<Data, Boolean> prev2 = prev;
				prev2.put(listOfData.get(n), true);
				int temp2 = this.knapsack(listOfData, n-1, capacity - listOfData.get(n).getSize(), prev2);
				return Math.max(temp1, temp2);
			}

	}
	
}
