import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class Tree {
	// Nodes that constitute the tree. It can be uncomputed, and therefore null
	private ArrayList<ArrayList<Node>> nodes;
	private final Node rootNode;
	private final int capacity;

	public Tree(State currentState, int capacity, boolean computeNodes) {
		this.capacity = capacity;
		/*
		 * Node sitting at the top of the tree, parent of all the children, (dutiful
		 * protector of the realm)
		 */
		this.rootNode = new Node(null, currentState, 0);
		this.nodes = new ArrayList<ArrayList<Node>>();
		this.nodes.add(new ArrayList<Node>(Arrays.asList(this.rootNode)));
		
		// Populate the tree under the rootNode
		if (computeNodes) {

			int currentLevel = 0;

			boolean allNodesAtThisLevelAreChildless = this.isChildless(this.rootNode);
			while (!allNodesAtThisLevelAreChildless) {
				ArrayList<Node> nodesAtNextLevel = new ArrayList<Node>();

				for (Node node : this.getNodesAtLevel(currentLevel)) {
					ArrayList<Node> childrenOfThisNode = this.generateChildren(node, capacity, currentLevel);
					nodesAtNextLevel.addAll(childrenOfThisNode);
				}
				this.nodes.add(nodesAtNextLevel);
				currentLevel++;

				// Find out if ALL the child at this level are childless
				allNodesAtThisLevelAreChildless = true;
				for (int i = 0; i < this.getNodesAtLevel(currentLevel).size() && allNodesAtThisLevelAreChildless; i++) {
					Node node = this.getNodesAtLevel(currentLevel).get(i);
					if (!this.isChildless(node)) {
						allNodesAtThisLevelAreChildless = false;
					}
				}
			}
		}
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode and
	 * using the carriedTasks and TaskSet of parentNode to find them
	 */
	public ArrayList<Node> generateChildren(Node parentNode, int capacity, int currentLevel) {

		ArrayList<Node> children = new ArrayList<Node>();
		
		children.addAll(generateChildrenIssuedFromDeliveries(parentNode, capacity, currentLevel));
		children.addAll(generateChildrenIssuedFromTasksToPickUp(parentNode, capacity, currentLevel));

		return children;
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode and
	 * using the TaskSet to find them
	 */
	private ArrayList<Node> generateChildrenIssuedFromTasksToPickUp(Node parentNode, int capacity, int currentLevel) {
		TaskSet parentTasksToPickUp = parentNode.getState().getTasksToPickUp();
		HashSet<Task> parentCarriedTasks = parentNode.getState().getCarriedTasks();

		ArrayList<Node> children = new ArrayList<Node>();

		for (Task parentTaskToPickUp : parentTasksToPickUp) {
			// The action that's being made is "go to that task's pickup city and pick up
			// the task"

			City currentCity = parentNode.getState().getCurrentCity();
			City taskCity = parentTaskToPickUp.pickupCity;
		    ArrayList<City> path = currentCity.pathTo(taskCity);

		    for(Task carriedTask : parentCarriedTasks){

			    if (path.contains(carriedTask.deliveryCity)){
			    	//create only delivery Task.
			    	//remove delivery Task from parentCarriedTasks
			    	//see final step at bottom of this function :)
			    }
			    	
		    }
		    
			TaskSet childTasksToPickUp = parentTasksToPickUp.clone();
			childTasksToPickUp.remove(parentTaskToPickUp);

			HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();
			childCarriedTasks.add(parentTaskToPickUp);

			City currentCity = parentTaskToPickUp.pickupCity;

			State childState = new State(currentCity, childTasksToPickUp, childCarriedTasks);

			// ONLY the child Nodes who carriedWeight DOES NOT exceed the capacity are added
			Node childNode = new Node(parentNode, childState, currentLevel);
			if (childNode.getCarriedWeight() <= capacity) {
				children.add(childNode);
			}
		}
		//add children from generate children issued from deliveries here instead of calling it separately. This way the carriedTasksList changes aren't lost
		return children;
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode and
	 * using the carriedTasks to find them
	 */
	private ArrayList<Node> generateChildrenIssuedFromDeliveries(Node parentNode, int capacity, int currentLevel) {		
		TaskSet parentTasksToPickUp = parentNode.getState().getTasksToPickUp();
		HashSet<Task> parentCarriedTasks = parentNode.getState().getCarriedTasks();

		ArrayList<Node> children = new ArrayList<Node>();
		
		for (Task parentCarriedTask : parentCarriedTasks) {
			// The action that's being made is "go to that task's delivery city
			// and deliver
			// the task"
			HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();
			childCarriedTasks.remove(parentCarriedTask);

			State childState = new State(parentCarriedTask.deliveryCity, parentTasksToPickUp, childCarriedTasks);

			// ONLY the child Nodes whose carriedWeight DOES NOT exceed the
			// capacity are added
			children.add(new Node(parentNode, childState, currentLevel));
		}
		return children;
	}

	public ArrayList<ArrayList<Node>> getNodes() {
		return this.nodes;
	}

	/**
	 * The root node is the nood from which all the other nodes come from
	 * 
	 * @return the root node of this tree.
	 */
	public Node getRootNode() {
		return this.rootNode;
	}

	public ArrayList<Node> getNodesAtLevel(int level) {
		return this.nodes.get(level);
	}

	/* removes a specified node from the tree. Useful for pruning or removing
	worthless options while searching
	It is my understanding that, because I have passed in the tree to the BFS
	class as an instance variable,
	the original tree will be unaffected, and instead only the local copy
	will be changed. */
	public void removeNode(int nodeIndex, int currentLevel) {
		nodes.get(currentLevel).remove(nodeIndex);
	}

	/**
	 * A Direct Child of a Node node is a node whose parent is this node. It is
	 * therefore located of the level directly below this node.
	 * 
	 * @param node Node to which we need to find the DIRECT children of
	 * @return an ArrayList<Node> with all the NEWLY GENERATED direct children. The
	 *         list is empty if there is no children. null if node isn't on the
	 *         level specified
	 */
	public ArrayList<Node> getDirectChildren(Node node) {
		return this.generateChildren(node, this.capacity, node.getTreeLevel());
	}

	/**
	 * 
	 * @return true if the node cannot generate any child (in the context of this
	 *         tree), false otherwise
	 */
	public boolean isChildless(Node node) {
		ArrayList<Node> children = this.generateChildren(node, this.capacity, node.getTreeLevel());
		if (children.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void addLevel(ArrayList<Node> newLevelNodes)
	{		
		nodes.add(newLevelNodes);
	}
	
	public int getCapacity(){
		return this.capacity;
	}
}
