import java.util.HashSet;

import logist.task.Task;
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
	private TaskSet taskSet;
	private HashSet<Task> carriedTasks;

	public State(City currentCity, TaskSet taskSet, HashSet<Task> carriedTasks) {
		this.currentCity = currentCity;
		this.taskSet = taskSet;
		this.carriedTasks = carriedTasks;
	}

	@Override
	public boolean equals(Object that) {
		//TODO
		return false;
	}

	@Override
	public String toString() {
		return this.currentCity + " " + this.taskSet + " " + this.carriedTasks;
	}
	
	public City getCurrentCity() {
		return this.currentCity;
	}
	
	public HashSet<Task> getCarriedTasks() {
		return this.carriedTasks;
	}
	
	public TaskSet getTaskSet() {
		return this.taskSet;
	}

}
