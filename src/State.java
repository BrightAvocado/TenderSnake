import logist.task.TaskSet;
import logist.topology.Topology.City;

/**
 * This class represents a State for the deliberative solution. We need this
 * because last time, debugging was a pain.
 * 
 * The state of the vehicle can be entirely defined by where it's coming from
 * and where it can go
 */
public class State {

	private City currentCity;
	private TaskSet tasksToPickUp; // Tasks that haven't been picked up yet
	private TaskSet carriedTasks; // Tasks that are currently being carried by the vehicle who's in this state

	public State(City currentCity, TaskSet taskSet, TaskSet carriedTasks) {
		this.currentCity = currentCity;
		this.tasksToPickUp = taskSet;
		this.carriedTasks = carriedTasks;
	}

	@Override
	public boolean equals(Object that) {
		// TODO
		return false;
	}

	@Override
	public String toString() {
		return this.currentCity + " " + this.tasksToPickUp + " " + this.carriedTasks;
	}

	public City getCurrentCity() {
		return this.currentCity;
	}

	public TaskSet getCarriedTasks() {
		return this.carriedTasks;
	}

	public TaskSet getTasksToPickUp() {
		return this.tasksToPickUp;
	}

}
