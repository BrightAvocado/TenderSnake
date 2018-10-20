import java.util.ArrayList;

import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Delivery;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.topology.Topology.City;

public class Node {
	private Node parent;
	private final State state;
	private final int carriedWeight;
	private final double distanceToRoot;
	private final ArrayList<Action> actionsToGetToThisNode;
	private final int treeLevel;

	public Node(Node parent, State state, int treeLevel) {
		this.parent = parent;
		this.state = state;
		this.treeLevel = treeLevel;

		if (this.parent != null) {// If the Node is NOT the rootNode

			// Find the ONE task that has been ADDED (picked up) between this Node and its
			// parent, IF
			// there is one
			Task addedTask = null;
			for (Task task : this.state.getCarriedTasks()) {
				if (!this.parent.state.getCarriedTasks().contains(task)) {
					addedTask = task;
					break;
				}
			}

			// Find ALL the tasks that have been REMOVED (delivered) between this Node and
			// its parent, IF
			// there are some
			ArrayList<Task> removedTasks = new ArrayList<Task>();
			for (Task parentTask : this.parent.state.getCarriedTasks()) {
				if (!this.state.getCarriedTasks().contains(parentTask)) {
					removedTasks.add(parentTask);
				}
			}

			// Compute the carriedWeight of this Node
			int weightChange = 0;
			if (addedTask != null) {
				weightChange += addedTask.weight;
			}
			for (Task removedTask : removedTasks) {
				weightChange -= removedTask.weight;
			}
			this.carriedWeight = this.parent.carriedWeight + weightChange;

			// Compute the distantToRoot of this Node
			double distanceParentToRoot = this.parent.getDistanceToRoot();
			double distanceThisToParent = this.parent.state.getCurrentCity().distanceTo(this.state.getCurrentCity());
			this.distanceToRoot = distanceThisToParent + distanceParentToRoot;

			// Compute the actionsToGetToThisNode of this Node
			/*
			 * Between this Node and its parent, EITHER ONE Task has been added (pick up)
			 * and NONE has been removed (delivered) OR ONE Task has been added (pick up)
			 * and some have been removed (delivered) OR NO Task has been added (pick up)
			 * and some have been removed (delivered) It HAS to be one of those three
			 */
			ArrayList<Action> actionsToGetToThisNode = new ArrayList<Action>();
			if (addedTask != null) { // Pickup

				// Go where the task to pick up needs to be picked up
				City parentCity = this.parent.state.getCurrentCity();
				for (City city : parentCity.pathTo(addedTask.pickupCity)) {
					actionsToGetToThisNode.add(new Move(city));
				}

				// Deliver all the tasks that can be delivered
				for (Task removedTask : removedTasks) {
					if (removedTask.deliveryCity == addedTask.pickupCity) {
						actionsToGetToThisNode.add(new Delivery(removedTask));
					}
				}

				// Pick up the task
				actionsToGetToThisNode.add(new Pickup(addedTask));

			} else if (!removedTasks.isEmpty()) { // Delivery
				// Go where the task to deliver needs to be delivered
				City parentCity = this.parent.state.getCurrentCity();
				// It is assumed that all the removedTasks have the same delivery city
				for (City city : parentCity.pathTo(removedTasks.get(0).deliveryCity)) {
					actionsToGetToThisNode.add(new Move(city));
				}

				// Deliver ALL the tasks
				for (Task removedTask : removedTasks) {
					actionsToGetToThisNode.add(new Delivery(removedTask));
				}
			}
			this.actionsToGetToThisNode = actionsToGetToThisNode;

		} else {
			this.carriedWeight = 0;
			this.distanceToRoot = 0;
			this.actionsToGetToThisNode = new ArrayList<Action>();
		}
	}

	public Node getParent() {
		return this.parent;
	}

	public int getCarriedWeight() {
		return this.carriedWeight;
	}

	public double getDistanceToRoot() {
		return this.distanceToRoot;
	}

	public State getState() {
		return this.state;
	}

	public ArrayList<Action> getActionsToGetToThisNode() {
		return this.actionsToGetToThisNode;
	}

	public int getTreeLevel() {
		return treeLevel;
	}

	@Override
	public boolean equals(Object that) {
		if (!(that instanceof Node)) {
			return false;
		}
		Node node = (Node) that;
		return (this.state == node.state) && (this.treeLevel == node.treeLevel); // Is this really a good way to check
																					// equality ?
	}
}
