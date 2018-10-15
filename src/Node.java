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

	public Node(Node parent, State state) {
		this.parent = parent;
		this.state = state;

		if (this.parent != null) {// If the Node is NOT the rootNode
			// Find the ONE task that has been REMOVED between this Node and its parent, IF
			// there is one
			Task removedTask = null;
			for (Task parentTask : this.parent.state.getCarriedTasks()) {
				if (!this.state.getCarriedTasks().contains(parentTask)) {
					removedTask = parentTask;
					break;
				}
			}

			// Find the ONE task that has been ADDED between this Node and its parent, IF
			// there is one
			Task addedTask = null;
			for (Task task : this.state.getCarriedTasks()) {
				if (!this.parent.state.getCarriedTasks().contains(task)) {
					addedTask = task;
					break;
				}
			}

			// Compute the carriedWeight of this Node
			int weightChange = 0;
			if (addedTask != null) {
				weightChange += addedTask.weight;
			} else {
				weightChange -= removedTask.weight;
			}
			this.carriedWeight = this.parent.carriedWeight + weightChange;

			// Compute the distantToRoot of this Node
			double distanceParentToRoot = this.parent.getDistanceToRoot();
			double distanceThisToParent = this.parent.state.getCurrentCity().distanceTo(this.state.getCurrentCity());
			this.distanceToRoot = distanceThisToParent + distanceParentToRoot;

			// Compute the actionsToGetToThisNode of this Node
			/*
			 * Between this Node and its parent, EITHER a Task has been added (pick up) OR a
			 * Task has been removed (delivery). It HAS to be one of those two
			 */
			ArrayList<Action> actionsToGetToThisNode = new ArrayList<Action>();
			if (removedTask != null) { // Delivery
				// Go where the task to deliver needs to be delivered
				City parentCity = this.parent.state.getCurrentCity();
				for (City city : parentCity.pathTo(removedTask.deliveryCity)) {
					actionsToGetToThisNode.add(new Move(city));
				}

				// Deliver the task
				actionsToGetToThisNode.add(new Delivery(removedTask));
			} else if (addedTask != null) { // Pickup
				// Go where the task to pick up needs to be picked up
				City parentCity = this.parent.state.getCurrentCity();
				for (City city : parentCity.pathTo(addedTask.pickupCity)) {
					actionsToGetToThisNode.add(new Move(city));
				}

				// Pick up the task
				actionsToGetToThisNode.add(new Pickup(addedTask));
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
}
