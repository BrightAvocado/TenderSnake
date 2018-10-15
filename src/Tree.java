import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import logist.task.Task;
import logist.task.TaskSet;

public class Tree {
	private final ArrayList<ArrayList<Node>> nodes;
	private final int capacity;

	public Tree(State currentState, int capacity) {
		this.capacity = capacity;
		/*
		 * Node sitting at the top of the tree, parent of all the children, (dutiful
		 * protecter of the realm)
		 */
		this.nodes = new ArrayList<ArrayList<Node>>();

		// Create and add the rootNode to the tree
		Node rootNode = new Node(null, currentState);
		this.nodes.add(new ArrayList<Node>(Arrays.asList(rootNode)));

		// Populate the tree under the rootNode
		boolean allNodesAtThisLevelAreChildless = this.isChildless(rootNode);
		int currentLevel = 0;
		while (!allNodesAtThisLevelAreChildless) {
			ArrayList<Node> nodesAtNextLevel = new ArrayList<Node>();

			for (Node node : this.getNodesAtLevel(currentLevel)) {
				ArrayList<Node> childrenOfThisNode = this.generateChildren(node, capacity);
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

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode and
	 * using the carriedTasks and TaskSet of parentNode to find them
	 */
	private ArrayList<Node> generateChildren(Node parentNode, int capacity) {

		ArrayList<Node> children = new ArrayList<Node>();

		children.addAll(generateChildrenIssuedFromDeliveries(parentNode, capacity));
		children.addAll(generateChildrenIssuedFromTasksToPickUp(parentNode, capacity));

		return children;
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode and
	 * using the TaskSet to find them
	 */
	private ArrayList<Node> generateChildrenIssuedFromTasksToPickUp(Node parentNode, int capacity) {
		TaskSet parentTasksToPickUp = parentNode.getState().getTasksToPickUp();
		HashSet<Task> parentCarriedTasks = parentNode.getState().getCarriedTasks();

		ArrayList<Node> children = new ArrayList<Node>();

		for (Task parentTaskToPickUp : parentTasksToPickUp) {
			// The action that's being made is "go to that task's pickup city and pick up
			// the task"

			TaskSet childTasksToPickUp = parentTasksToPickUp.clone();
			childTasksToPickUp.remove(parentTaskToPickUp);

			HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();
			childCarriedTasks.add(parentTaskToPickUp);

			State childState = new State(parentTaskToPickUp.pickupCity, childTasksToPickUp, childCarriedTasks);

			// ONLY the child Nodes who carriedWeight DOES NOT exceed the capacity are added
			Node childNode = new Node(parentNode, childState);
			if (childNode.getCarriedWeight() <= capacity) {
				children.add(new Node(parentNode, childState));
			}
		}

		return children;
	}

	/*
	 * Generate ALL the possible DIRECT children Nodes coming from parentNode and
	 * using the carriedTasks to find them
	 */
	private ArrayList<Node> generateChildrenIssuedFromDeliveries(Node parentNode, int capacity) {
		TaskSet parentTasksToPickUp = parentNode.getState().getTasksToPickUp();
		HashSet<Task> parentCarriedTasks = parentNode.getState().getCarriedTasks();

		ArrayList<Node> children = new ArrayList<Node>();

		for (Task parentCarriedTask : parentCarriedTasks) {
			// The action that's being made is "go to that task's delivery city and deliver
			// the task"
			HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();
			childCarriedTasks.remove(parentCarriedTask);

			State childState = new State(parentCarriedTask.deliveryCity, parentTasksToPickUp, childCarriedTasks);

			// ONLY the child Nodes whose carriedWeight DOES NOT exceed the capacity are
			// added
			Node childNode = new Node(parentNode, childState);
			if (childNode.getCarriedWeight() <= capacity) {
				children.add(new Node(parentNode, childState));
			}
		}

		return children;
	}

	public ArrayList<Node> getNodesAtLevel(int level) {
		return this.nodes.get(level);
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
		return this.getNodesAtLevel(0).get(0);
	}

	/**
	 * A Direct Child of a Node node is a node whose parent is this node. 
	 * It is therefore located of the level directly below this node.
	 * @param node Node to which we need to find the DIRECT children of
	 * @return an ArrayList<Node> with all the NEWLY GENERATED direct children. The list is empty if there is no children. 
	 * null if node isn't on the level specified
	 */
	public ArrayList<Node> getDirectChildren(Node node) {
		return this.generateChildren(node, this.capacity);
	}

	/**
	 * 
	 * @return true if the node cannot generate any child (in the context of this tree), false otherwise
	 */
	public boolean isChildless(Node node) {
		ArrayList<Node> children = this.generateChildren(node, this.capacity);
		if (children.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
