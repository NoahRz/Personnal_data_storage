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


	public ArrayList<Data> addOptimizedData(ArrayList<Data> listOfData, User user){
		/**
		 * add a bunch of data by using knapsack problem
		 * @param listOfData : ArrayList<Data>
		 * */
		int n = listOfData.size(); // n : size of the original listofData
		ArrayList<Data> newListOfData = new ArrayList<Data>();
		newListOfData.add(new Data(-1, -1)); // add this data at the beginning so that the last index is equal to the size of the list
		for (Data d: listOfData){
			newListOfData.add(d);
		}
		this.knapsack(newListOfData, n, capacity, null);

		System.out.println("result path knapsack :" + this.resultPathKnapSack);
		System.out.println("size reresult path knapsack :" + this.resultPathKnapSack.size());

		int max = 0;
		int indexMax = 0; // index of the hashMap which has the most "true"
		for (HashMap<Data, Boolean> path : this.resultPathKnapSack){
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
		ArrayList<Data> unAddedData = new ArrayList<Data>(); // array of data couldn't have been added
		HashMap<Data, Boolean> dataToAdd = resultPathKnapSack.get(indexMax);

		System.out.println("data to add :" + dataToAdd);
		for(Data key : dataToAdd.keySet()){
			if (dataToAdd.get(key)){ // // equals to == true
				this.getData().add(key);
				user.addDataId(key.getId());
			}
			else{
				unAddedData.add(key);
			}
		}

		this.resultPathKnapSack= new ArrayList<HashMap<Data, Boolean>>();
		return unAddedData;
	}
	public void knapsack(ArrayList<Data> listOfData, int n, int capacity, HashMap<Data, Boolean> prev){
		/**
		 * return the best combination of data to add in this system node using knapsack problem
		 * @param listOfData : ArrayList<Data>
		 * @param n : int, the current index of data in the listOfData
		 * @param capacity : int the capacity of this systemNode
		 * @param prev : HashMap<Data, Boolean> gathering all the data to get to the current data and show if the data has been added in the systemNode (true or false)
		 *             ex: {prev1 : True, prev2 : False, ..., current: True}
		 * */

		// we check for all the combination like if we were going through a binary tree (Yes/No)
		// resultPathKnapSack gathers all the path in the binary tree

		if (prev == null){
				prev = new HashMap<>();
		}
		if (n==0){ // means there we check fot all the data, we don't check if the current capacity = 0 because we want the entire possibility
			// for example: if the capacity is equal to 0 but we didn't check all the data, on veut quand même dire qu'on a pas ajouté les autres données {data : false}
			// donc on continue de parcourir la liste et on aura les data suivant qui ne seaont pas mis
			this.resultPathKnapSack.add(prev); // add the path (binary tree) to a resultPathKnapSack which gather all the paths (possibility)
			//return 0; // don't think it's necessary to return an integer
		}
		else if (listOfData.get(n).getSize()>capacity){
			HashMap<Data, Boolean> prev0 = new HashMap<>(); // create a new branch from the previous path
			for (Data d:prev.keySet()){ // add the prev path to this new path (branch)
				prev0.put(d, prev.get(d));
			}
			prev0.put(listOfData.get(n), false); // we don't add the data in the systemNode
			//return this.knapsack(listOfData, n-1, capacity, prev0);
			this.knapsack(listOfData, n-1, capacity, prev0);
			System.out.println("prev0 :"+ prev0);

		}
		else{
			HashMap<Data, Boolean> prev1 = new HashMap<>();// create a new branch from the previous path
			for (Data d:prev.keySet()){
				prev1.put(d, prev.get(d));
			}
			prev1.put(listOfData.get(n), false); // we don't add the data in the systemNode
			//int temp1 = this.knapsack(listOfData, n-1, capacity, prev1);
			System.out.println("prev1 :"+ prev1);
			this.knapsack(listOfData, n-1, capacity, prev1);

			HashMap<Data, Boolean> prev2 = new HashMap<>();// create a new branch from the previous path
			for (Data d:prev.keySet()){
				prev2.put(d, prev.get(d));
			}
			prev2.put(listOfData.get(n), true); // we add the data in the systemNode
			//int temp2 = this.knapsack(listOfData, n-1, capacity - listOfData.get(n).getSize(), prev2); //update the new capacity
			System.out.println("prev2 :"+ prev2 + " n :" + (n-1) +" capacity : + "+ (capacity - listOfData.get(n).getSize()));
			this.knapsack(listOfData, n-1, capacity - listOfData.get(n).getSize(), prev2); //update the new capacity
			//return Math.max(temp1, temp2);
			//return 0;
		}

	}

	public ArrayList<HashMap<Data, Boolean>> getResultPathKnapSack() {
		return resultPathKnapSack;
	}
}
