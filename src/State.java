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
	private TaskSet ts;
	private HashSet<Task> carriedTasks;

	public State(City currentCity, TaskSet ts, HashSet<Task> carriedTasks) {
		this.currentCity = currentCity;
		this.ts = ts;
		this.carriedTasks = carriedTasks;
	}

	@Override
	public boolean equals(Object that) {
		//TODO
		return false;
	}

	@Override
	public String toString() {
		//TODO
		return "todo";
	}
}
