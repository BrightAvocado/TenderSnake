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

			// Find the ONE task that has been ADDED (picked up) between this
			// Node and its
			// parent, IF
			// there is one
			Task addedTask = null;
			for (Task task : this.state.getCarriedTasks()) {
				if (!this.parent.state.getCarriedTasks().contains(task)) {
					addedTask = task;
					break;
				}
			}

			// Find ALL the tasks that have been REMOVED (delivered) between
			// this Node and
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
			 * Between this Node and its parent, EITHER ONE Task has been added
			 * (pick up) and NONE has been removed (delivered) OR ONE Task has
			 * been added (pick up) and some have been removed (delivered) OR NO
			 * Task has been added (pick up) and some have been removed
			 * (delivered) It HAS to be one of those three
			 */
			
			ArrayList<Action> actionsToGetToThisNode = new ArrayList<Action>();
			City parentCity = this.parent.state.getCurrentCity();
			
			if(!removedTasks.isEmpty()){
				//either deliver tasks on your way to the pickup city
					actionsToGetToThisNode.addAll(getDeliveryActions(parentCity, this.state.getCurrentCity(), removedTasks));
			} else{
				//or go direct to the pickup city
				for (City city : parentCity.pathTo(addedTask.pickupCity)) {
					actionsToGetToThisNode.add(new Move(city));
				}
			}

			if (addedTask != null) { // Pickup
				actionsToGetToThisNode.add(new Pickup(addedTask));
			} 
			
			this.actionsToGetToThisNode = actionsToGetToThisNode;

		} else {
			this.carriedWeight = 0;
			this.distanceToRoot = 0;
			this.actionsToGetToThisNode = new ArrayList<Action>();
		}
	}

	private ArrayList<Action> getDeliveryActions(City origin, City destination, ArrayList<Task> removedTasks) {

		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<City> pathCities = (ArrayList<City>) origin.pathTo(destination);
		
		for (City pathCity : pathCities) {
			boolean moved = false;
			for (Task removedTask : removedTasks) {
				
				if(!pathCities.contains(removedTask.deliveryCity)) //for debugging
				{
					System.out.println("Problem with task "+removedTask.id+", delivery from: "+origin.toString()+" to: "+destination.toString());
				}
				if (!moved){
					actions.add(new Move(pathCity));
				}
				moved = true;
				if (removedTask.deliveryCity == pathCity) {
					actions.add(new Delivery(removedTask));
				}
			}
		}

		return actions;
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
		return (this.state == node.state) && (this.treeLevel == node.treeLevel); // Is
																					// this
																					// really
																					// a
																					// good
																					// way
																					// to
																					// check
																					// equality
																					// ?
	}
}
