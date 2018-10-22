import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import logist.plan.Action;
import logist.plan.Plan;

public class BFS {
	protected Tree tree;
	private Plan plan;

	public BFS(Tree tree) {
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
		//HashSet<Node> C = new HashSet<Node>(); //TODO change it to HashSet<Node> (or state ?)

		// BFS algorithm

		// We start at the root node
		Node currentNode = this.tree.getRootNode();
		Q.add(currentNode);
		
		ArrayList<Node> S = new ArrayList<Node>();
		
		while (!Q.isEmpty()) {
			currentNode = Q.get(0);
			Q.remove(0);
			if (this.tree.isGoalNode(currentNode)) {// If n is a goal node
				break;
			}
			S.addAll(this.tree.getDirectChildren(currentNode));
			if (Q.isEmpty()) {
				S.sort(new SortByDistanceToRoot());
				Q.addAll(S);
				S.clear();
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

	public Plan getPlan() {
		return this.plan;
	}
	
	class SortByDistanceToRoot implements Comparator<Node> {

		public int compare(Node a, Node b) {
			if (b.getDistanceToRoot() > a.getDistanceToRoot()) {
				return -1;
			} else if (b.getDistanceToRoot() < a.getDistanceToRoot()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}