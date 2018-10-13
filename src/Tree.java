import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class Tree {
	private ArrayList<ArrayList<Node>> nodes;

	public Tree(State currentState, int capacity) {
		/*
		 * Node sitting at the top of the tree, parent of all the children, (dutiful
		 * protecter of the realm)
		 */
		this.nodes = new ArrayList<ArrayList<Node>>();

		// Create and add the rootNode to the tree
		Node rootNode = new Node(null, currentState);
		this.nodes.add(new ArrayList<Node>(Arrays.asList(rootNode)));

		// Populate the tree under the rootNode
		boolean allNodesAtThisLevelAreChildless = rootNode.isChildless();
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
				if (!node.isChildless()) {
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
		// TODO Auto-generated method stub
		TaskSet parentTasksToPickUp = parentNode.getState().getTasksToPickUp();
		TaskSet parentCarriedTasks = parentNode.getState().getCarriedTasks();

		ArrayList<Node> children = new ArrayList<Node>();

		for (Task parentTaskToPickUp : parentTasksToPickUp) {
			// The action that's being made is "go to that task's pickup city and pick up
			// the task"

			TaskSet childTasksToPickUp = parentTasksToPickUp.clone();
			childTasksToPickUp.remove(parentTaskToPickUp);
			
			TaskSet childCarriedTasks = parentCarriedTasks.clone();
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
		TaskSet parentCarriedTasks = parentNode.getState().getCarriedTasks();

		ArrayList<Node> children = new ArrayList<Node>();

		for (Task parentCarriedTask : parentCarriedTasks) {
			// The action that's being made is "go to that task's delivery city and deliver
			// the task"
			TaskSet childCarriedTasks = parentCarriedTasks.clone();
			childCarriedTasks.remove(parentCarriedTask);

			State childState = new State(parentCarriedTask.deliveryCity, parentTasksToPickUp, childCarriedTasks);

			// ONLY the child Nodes whose carriedWeight DOES NOT exceed the capacity are added
			Node childNode = new Node(parentNode, childState);
			if (childNode.getCarriedWeight() <= capacity) {
				children.add(new Node(parentNode, childState));
			}
		}

		return children;
	}

	/*
	 * Not very necessary, but right now, if there are several tasks that can be
	 * picked up in one city, each "pick up" would be one node. A neat thing to do
	 * would be to "compress" all these actions in one city into only ONE node. To
	 * do that, go through the tree looking for nodes who are in the same city as
	 * their parent. Is it really worth it though since I'd have to look through the
	 * tree ? Maybe it should be implemented directly when building the tree.
	 */
	private void compress() {
		// TODO
	}

	public ArrayList<Node> getNodesAtLevel(int level) {
		return this.nodes.get(level);
	}

}
