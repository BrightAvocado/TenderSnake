import java.util.ArrayList;

import logist.task.TaskSet;
import logist.topology.Topology.City;

public class Tree {
	private ArrayList<ArrayList<Node>> nodes;

	public Tree(State currentState, TaskSet ts) {
		Node rootNode = new Node(currentState, )
	}

	/*
	 * Once the tree is fully formed, prune it so that all the children of
	 * impossible path (because too heavy) are dropped
	 */
	private void prune() {
		// TODO
	}

	public ArrayList<Node> getNodesAtLevel(int level) {
		// TODO
		return null;
	}
}
