import java.util.HashSet;

import logist.task.Task;

public class Node {
	private Node parent;
	private final State state;
	private final int carriedWeight;
	private final double distanceToRoot;

	public Node(Node parent, State state) {
		this.parent = parent;
		this.state = state;

		if (this.parent != null) {// If the Node is NOT the rootNode
			// Compute the tasks that have been REMOVED between this Node and its parent
			HashSet<Task> removedTasks = new HashSet<Task>();
			for (Task parentTask : this.parent.state.getCarriedTasks()) {
				if (!this.state.getCarriedTasks().contains(parentTask)) {
					removedTasks.add(parentTask);
				}
			}

			// Compute the tasks that have been ADDED between this Node and its parent
			HashSet<Task> addedTasks = new HashSet<Task>();
			for (Task task : this.state.getCarriedTasks()) {
				if (!this.parent.state.getCarriedTasks().contains(task)) {
					addedTasks.add(task);
				}
			}

			// Compute the carriedWeight of this Node
			int weightChange = 0;
			for (Task addedTask : addedTasks) {
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

		} else {
			this.carriedWeight = 0;
			this.distanceToRoot = 0;
		}
	}

	public boolean isChildless() {
		return this.state.getTaskSet().isEmpty() && this.state.getCarriedTasks().isEmpty();
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
}
