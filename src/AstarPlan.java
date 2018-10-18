import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import logist.plan.Action;
import logist.plan.Plan;

public abstract class AstarPlan {
	protected Tree tree;
	private Plan plan;

	public AstarPlan(Tree tree) {
		this.tree = tree;
		this.plan = null;
	}

	/**
	 * Compute the Plan according to the A* algo and the heuristic. It works this
	 * way : For each level, find the node with the highest score (according to one
	 * arbitrary heuristic. The first node who qualifies is added to the path. Do
	 * that again until you read an end node. The path creates a Plan, and this is
	 * what this.plan is set to.
	 */
	public void computePlan() {
		ArrayList<Node> Q = new ArrayList<Node>();
		/*
		 * Map with the ALREADY CONSIDERED STATES as keys and their distance to Root as
		 * value
		 */
		HashMap<State, Double> C = new HashMap<State, Double>();

		// A* algo

		// We start at the root node
		Node currentNode = this.tree.getRootNode();
		Q.add(currentNode);

		while (!Q.isEmpty()) {
			currentNode = Q.get(0);
			Q.remove(0);
			if (this.tree.isGoalNode(currentNode)) {// If n is a goal node
				break;
			}
			State currentState = currentNode.getState();
			if (!C.containsKey(currentState) || C.get(currentState) > currentNode.getDistanceToRoot()) {
				C.put(currentNode.getState(), currentNode.getDistanceToRoot());
				ArrayList<Node> S = this.tree.getDirectChildren(currentNode);
				Q.addAll(S);
				Q.sort(new SortByHeuristic(this));
			}
		}

		ArrayList<Action> actionsFromRootNodeToEndNode = new ArrayList<Action>();

		while (currentNode.getParent() != null) {
			actionsFromRootNodeToEndNode.addAll(0, currentNode.getActionsToGetToThisNode());
			currentNode = currentNode.getParent();
		}

		Plan plan = new Plan(this.tree.getRootNode().getState().getCurrentCity(), actionsFromRootNodeToEndNode);

		this.plan = plan;
	}

	/**
	 * This is the heuristic of the A* algo
	 * @param node
	 * @return the cost to go from the root node to node
	 */
	abstract double h(Node node);

	private double f(Node node) {
		return this.g(node) + this.h(node);
	}

	/**
	 * 
	 * @param node
	 * @return the cost to go from the root node to node
	 */
	private double g(Node node) {
		return node.getDistanceToRoot();
	}

	public Plan getPlan() {
		return this.plan;
	}

	class SortByHeuristic implements Comparator<Node> {
		private AstarPlan astarPlan;

		public SortByHeuristic(AstarPlan astarPlan) {
			this.astarPlan = astarPlan;
		}

		public int compare(Node a, Node b) {
			if (this.astarPlan.f(b) > this.astarPlan.f(a)) {
				return -1;
			} else if (this.astarPlan.f(b) < this.astarPlan.f(a)) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}