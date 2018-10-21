import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
		 * Node sitting at the top of the tree, parent of all the children,
		 * (dutiful protector of the realm)
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

		TaskSet parentTasksToPickUp = parentNode.getState().getTasksToPickUp();
		HashSet<Task> parentCarriedTasks = parentNode.getState().getCarriedTasks();
		HashSet<Task> parentCarriedTasksClone = parentNode.getState().getCarriedTasks();

		// quickly get the cities that have pickups
		ArrayList<City> deliveryCities = new ArrayList<City>();
		ArrayList<ArrayList<Task>> tasksPerCity = new ArrayList<ArrayList<Task>>();
		for (Task carriedTask : parentCarriedTasks) {
			City deliveryCity = carriedTask.deliveryCity;
			if (!deliveryCities.contains(deliveryCity)) {
				deliveryCities.add(deliveryCity);
				ArrayList<Task> tasksHere = new ArrayList<Task>();
				tasksHere.add(carriedTask);
				tasksPerCity.add(tasksHere);
			} else {
				tasksPerCity.get(deliveryCities.indexOf(deliveryCity)).add(carriedTask);
			}


		// Each delivery cities, with the tasks that need to be delivered there
		HashMap<City, ArrayList<Task>> deliveryCities2Tasks = new HashMap<City, ArrayList<Task>>();

		// Initiliaze and populate deliveryCities2Tasks
		for (Task parentCarriedTask : parentCarriedTasks) {
			if (deliveryCities2Tasks.containsKey(parentCarriedTask.deliveryCity)) {
				deliveryCities2Tasks.get(parentCarriedTask.deliveryCity).add(parentCarriedTask);
			} else {
				ArrayList<Task> tasks = new ArrayList<Task>();
				tasks.add(parentCarriedTask);
				deliveryCities2Tasks.put(parentCarriedTask.deliveryCity, tasks);
			}
		}

		for (Task parentTaskToPickUp : parentTasksToPickUp) {
			// The action that's being made is "go to that task's pickup city
			// and pick up
			// the task"


			// Handle the transfer of the picked up task from the tasksToPickUp to
			// carriedTasks
			TaskSet childTasksToPickUp = parentTasksToPickUp.clone();
			childTasksToPickUp.remove(parentTaskToPickUp);

			// extract info needed here
			City taskCity = parentTaskToPickUp.pickupCity;
			City currentCity = parentTaskToPickUp.pickupCity;
			ArrayList<City> path = (ArrayList<City>) currentCity.pathTo(taskCity);

			HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();

			// Handle the delivery of tasks IF THERE ARE some that needs to be delivered in
			// the parentTaskToPickUp's pickupCity
			if (deliveryCities2Tasks.get(parentTaskToPickUp.pickupCity) != null) {
				for (Task parentTaskToDeliverInThePickupCity : deliveryCities2Tasks
						.get(parentTaskToPickUp.pickupCity)) {
					childCarriedTasks.remove(parentTaskToDeliverInThePickupCity);
				}
			}


			if (!parentCarriedTasks.isEmpty()) {
				if (!path.isEmpty()) { // if there are cities in between here
										// and current pickup point
					boolean nodeCreatedForTask = false;
					for (City pathCity : path) { // see if there's a drop off
													// city in the way

						// start by checking the closest cities then work
						// outwards
						if (deliveryCities.contains(pathCity)) {
							// create only delivery Task.
							// remove delivery Task from parentCarriedTasks so
							// only
							// one node is created for it
							boolean check = childCarriedTasks
									.removeAll(tasksPerCity.get(deliveryCities.indexOf(pathCity)));
							/* if check is ever false, there's a problem */

							if (!check) {
								System.out.println("PROBLEM");
							}

							State childState = new State(pathCity, childTasksToPickUp, childCarriedTasks);
							Node childNode = new Node(parentNode, childState, currentLevel);
							children.add(childNode);

							parentCarriedTasksClone.removeAll(tasksPerCity.get(deliveryCities.indexOf(pathCity)));
							deliveryCities.set(deliveryCities.indexOf(pathCity), null);

							nodeCreatedForTask = true;

							break;
						}
					}
					if (!nodeCreatedForTask) { 
						// in case the check for drop-off cities on route doesn't work out
						// nominal behavior here
						// ONLY the child Nodes who carriedWeight DOES NOT
						// exceed the capacity are added
						childTasksToPickUp.remove(parentTaskToPickUp);
						childCarriedTasks.add(parentTaskToPickUp);

						State childState = new State(parentTaskToPickUp.pickupCity, childTasksToPickUp,
								childCarriedTasks);
						Node childNode = new Node(parentNode, childState, currentLevel);

						if (childNode.getCarriedWeight() <= capacity) {
							children.add(childNode);
						}
					}
				} else { // if path is empty (e.g. pickup task in current city)
					
					//TODO: Determine if this conditional is worth keeping
					if (deliveryCities.contains(currentCity)) {
						/*
						 * create only delivery Task.remove delivery Task from
						 * parentCarriedTasks so only one node is created for it
						 */
						
						//if there are any deliveries meant for the current city that were missed somehow, deal with them here
						//otherwise this just deals with the node like normal
						boolean check = childCarriedTasks
								.removeAll(tasksPerCity.get(deliveryCities.indexOf(currentCity)));
						/*
						 * if check is ever false, there's a problem
						 */

						State childState = new State(currentCity, childTasksToPickUp, childCarriedTasks);
						Node childNode = new Node(parentNode, childState, currentLevel);
						children.add(childNode);

						parentCarriedTasksClone.removeAll(tasksPerCity.get(deliveryCities.indexOf(currentCity)));

					} else {
						// nominal behavior here
						// ONLY the child Nodes who carriedWeight DOES NOT
						// exceed the capacity are added
						childTasksToPickUp.remove(parentTaskToPickUp);
						childCarriedTasks.add(parentTaskToPickUp);

						State childState = new State(parentTaskToPickUp.pickupCity, childTasksToPickUp,
								childCarriedTasks);
						Node childNode = new Node(parentNode, childState, currentLevel);

						if (childNode.getCarriedWeight() <= capacity) {
							children.add(childNode);
						}
					}
				}
			} else {
				/*
				 * nominal behavior here (applies when the parent is not
				 * currently holding any tasks) ONLY the child Nodes who
				 * carriedWeight DOES NOT exceed the capacity are added
				 */

				childTasksToPickUp.remove(parentTaskToPickUp);
				childCarriedTasks.add(parentTaskToPickUp);

				State childState = new State(parentTaskToPickUp.pickupCity, childTasksToPickUp, childCarriedTasks);
				Node childNode = new Node(parentNode, childState, currentLevel);
			}
		}
	}

	
	for(Task parentCarriedTask:parentCarriedTasksClone)
	{

		// The action that's being made is "go to that task's delivery city
		// and deliver
		// the task"
		HashSet<Task> childCarriedTasks = (HashSet<Task>) parentCarriedTasks.clone();
		//childCarriedTasks.removeAll(tasksToDeliver);

		//State childState = new State(tasksToDeliver.get(0).deliveryCity, parentNode.getState().getTasksToPickUp(),
		//		childCarriedTasks);

		// ONLY the child Nodes whose carriedWeight DOES NOT exceed the
		// capacity are added
		//children.add(new Node(parentNode, childState, currentLevel));
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

	/**
	 * 
	 * @return true if the node cannot generate any child (in the context of
	 *         this tree), false otherwise
	 */
	// TODO: Maybe make this more efficient than running generateChildren...
	public boolean isChildless(Node node) {
		ArrayList<Node> children = this.generateChildren(node, this.capacity, node.getTreeLevel());
		if (children.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isGoalNode(Node node) {
		State state = node.getState();
		return state.getCarriedTasks().isEmpty() && state.getTasksToPickUp().isEmpty();
	}

	public void addLevel(ArrayList<Node> newLevelNodes) {
		nodes.add(newLevelNodes);
	}

	public int getCapacity() {
		return this.capacity;
	}
}
