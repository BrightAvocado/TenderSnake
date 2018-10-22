import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class Tree {
	// Nodes that constitute the tree. It can be uninitialized, and therefore null
	private ArrayList<ArrayList<Node>> nodes;
	private final Node rootNode;
	private final int capacity;

	public Tree(State currentState, int capacity, boolean computeNodes) {
		this.capacity = capacity;

		/*
		 * Node sitting at the top of the tree, parent of all the children,
		 * (dutiful protector of the realm)
		 */
		this.rootNode = new Node(null, currentState, 0);
		this.nodes = new ArrayList<ArrayList<Node>>();
		this.nodes.add(new ArrayList<Node>(Arrays.asList(this.rootNode)));

		// Populate the tree under the rootNode
		if (computeNodes) {

			int currentLevel = 0;

			boolean allNodesAtThisLevelAreChildless = this.isGoalNode(this.rootNode);
			while (!allNodesAtThisLevelAreChildless) {
				ArrayList<Node> nodesAtNextLevel = new ArrayList<Node>();

				for (Node node : this.getNodesAtLevel(currentLevel)) {
					ArrayList<Node> childrenOfThisNode = this.generateChildren(node, capacity, currentLevel);
					nodesAtNextLevel.addAll(childrenOfThisNode);
				}
				this.nodes.add(nodesAtNextLevel);
				currentLevel++;

				// Find out if ALL the child at this level are child-less
				allNodesAtThisLevelAreChildless = true;
				for (int i = 0; i < this.getNodesAtLevel(currentLevel).size() && allNodesAtThisLevelAreChildless; i++) {
					Node node = this.getNodesAtLevel(currentLevel).get(i);
					if (!this.isGoalNode(node)) {
						allNodesAtThisLevelAreChildless = false;
					}
				}

				// DEBUG
				System.out.println(currentLevel);
			}
		}
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode
	 * and using the carriedTasks and TaskSet of parentNode to find them
	 */
	public ArrayList<Node> generateChildren(Node parentNode, int capacity, int currentLevel) {

		ArrayList<Node> children = new ArrayList<Node>();
		children.addAll(generateChildrenIssuedFromDeliveries(parentNode, currentLevel + 1));
		children.addAll(generateChildrenIssuedFromTasksToPickUp(parentNode, capacity, currentLevel + 1));

		return children;
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode
	 * and using the TaskSet to find them. These includes the nodes who consist
	 * of solely picking up a task, AND those who deliver tasks and pick up a
	 * task in the same city.
	 */
	private ArrayList<Node> generateChildrenIssuedFromTasksToPickUp(Node parentNode, int capacity, int currentLevel) {
		// Each delivery cities, with the tasks that need to be delivered there
		HashMap<City, ArrayList<Task>> deliveryCities2Tasks = new HashMap<City, ArrayList<Task>>();
		HashSet<Task> parentCarriedTasks = parentNode.getState().getCarriedTasks();
		TaskSet parentTasksToPickUp = parentNode.getState().getTasksToPickUp();

		// Initialize and populate deliveryCities2Tasks
		for (Task parentCarriedTask : parentCarriedTasks) {
			if (deliveryCities2Tasks.containsKey(parentCarriedTask.deliveryCity)) {
				deliveryCities2Tasks.get(parentCarriedTask.deliveryCity).add(parentCarriedTask);
			} else {
				ArrayList<Task> tasks = new ArrayList<Task>();
				tasks.add(parentCarriedTask);
				deliveryCities2Tasks.put(parentCarriedTask.deliveryCity, tasks);
			}
		}

		ArrayList<Node> children = new ArrayList<Node>();

		for (Task parentTaskToPickUp : parentTasksToPickUp) {
			// The action that's being made is "go to that task's pickup city
			// and pick up
			// the task"

			// Handle the transfer of the picked up task from the tasksToPickUp
			// to
			// carriedTasks
			TaskSet childTasksToPickUp = parentTasksToPickUp.clone();
			@SuppressWarnings("unchecked")
			HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();

			/*
			 * check all cities on path for any overlap with deliveries. take
			 * care of those deliveries all at once
			 */
			ArrayList<City> pathCities = (ArrayList<City>) parentNode.getState().getCurrentCity()
					.pathTo(parentTaskToPickUp.pickupCity);
			
			
			// Handle the delivery of tasks IF THERE ARE some that needs to be
			// delivered anywhere on the path to the delivery city
			for (City pathCity : pathCities) {
				if (deliveryCities2Tasks.get(pathCity) != null) {
					for (Task parentTaskToDeliverInThePickupCity : deliveryCities2Tasks.get(pathCity)) {
						childCarriedTasks.remove(parentTaskToDeliverInThePickupCity); 
					}
				}
			}

			childTasksToPickUp.remove(parentTaskToPickUp);
			childCarriedTasks.add(parentTaskToPickUp);

			State childState = new State(parentTaskToPickUp.pickupCity, childTasksToPickUp, childCarriedTasks);
			Node childNode = new Node(parentNode, childState, currentLevel);

			// ONLY the child Nodes whose carriedWeight DOES NOT exceed the
			// capacity are added
			if (childNode.getCarriedWeight() <= this.capacity) {
				children.add(new Node(parentNode, childState, currentLevel));
			}
		}

		return children;
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode
	 * and using the carriedTasks to find them. Those are the NODES who ONLY
	 * consists of deliveries
	 */
	private ArrayList<Node> generateChildrenIssuedFromDeliveries(Node parentNode, int currentLevel) {
		HashSet<Task> parentCarriedTasks = parentNode.getState().getCarriedTasks();

		// Each delivery cities, with the tasks that need to be delivered there
		HashMap<City, ArrayList<Task>> deliveryCities2Tasks = new HashMap<City, ArrayList<Task>>();

		// Initialize and populate deliveryCities2Tasks
		for (Task parentCarriedTask : parentCarriedTasks) {
			if (deliveryCities2Tasks.containsKey(parentCarriedTask.deliveryCity)) {
				deliveryCities2Tasks.get(parentCarriedTask.deliveryCity).add(parentCarriedTask);
			} else {
				ArrayList<Task> tasks = new ArrayList<Task>();
				tasks.add(parentCarriedTask);
				deliveryCities2Tasks.put(parentCarriedTask.deliveryCity, tasks);
			}
		}

		ArrayList<Node> children = new ArrayList<Node>();

		for (ArrayList<Task> tasksToDeliver : deliveryCities2Tasks.values()) {
			/* 
			 * The action that's being made is "go to that task's delivery city and deliver the task"
			 */
			@SuppressWarnings("unchecked")
			HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();
			childCarriedTasks.removeAll(tasksToDeliver);

			State childState = new State(tasksToDeliver.get(0).deliveryCity, parentNode.getState().getTasksToPickUp(),
					childCarriedTasks);
			children.add(new Node(parentNode, childState, currentLevel));
		}

		return children;
	}

	public ArrayList<ArrayList<Node>> getNodes() {
		return this.nodes;
	}

	/**
	 * The root node is the node from which all the other nodes come from
	 * @return the root node of this tree.
	 */
	public Node getRootNode() {
		return this.rootNode;
	}

	public ArrayList<Node> getNodesAtLevel(int level) {
		return this.nodes.get(level);
	}

	/*
	 * removes a specified node from the tree. Useful for pruning or removing
	 * worthless options while searching It is my understanding that, because I
	 * have passed in the tree to the BFS class as an instance variable, the
	 * original tree will be unaffected, and instead only the local copy will be
	 * changed.
	 */
	public void removeNode(int nodeIndex, int currentLevel) {
		nodes.get(currentLevel).remove(nodeIndex);
	}

	/**
	 * A Direct Child of a Node node is a node whose parent is this node. It is
	 * therefore located of the level directly below this node.
	 * 
	 * @param node
	 *            Node to which we need to find the DIRECT children of
	 * @return an ArrayList<Node> with all the NEWLY GENERATED direct children.
	 *         The list is empty if there is no children. null if node isn't on
	 *         the level specified
	 */
	public ArrayList<Node> getDirectChildren(Node node) {
		return this.generateChildren(node, this.capacity, node.getTreeLevel());
	}

	public boolean isGoalNode(Node node) {
		State state = node.getState();
		return state.getCarriedTasks().isEmpty() && state.getTasksToPickUp().isEmpty();
	}

	public int getCapacity() {
		return this.capacity;
	}

	public void addLevel(ArrayList<Node> newLevelNodes) {
		nodes.add(newLevelNodes);
	}
}
