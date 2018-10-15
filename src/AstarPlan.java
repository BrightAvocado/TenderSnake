import java.util.ArrayList;
import java.util.Collections;

import logist.plan.Action;
import logist.plan.Plan;

public abstract class AstarPlan {
	private Tree tree;
	private Plan plan;

	public AstarPlan(Tree tree) {
		this.tree = tree;
		computePlan();
	}

	/**
	 * Compute the Plan according to the A* algo and the heuristic.
	 * It works this way :
	 * For each level, find the node with the highest score (according to one arbitrary heuristic.
	 * The first node who qualifies is added to the path.
	 * Do that again until you read an end node.
	 * The path creates a Plan, and this is what this.plan is set to.
	 */
	private void computePlan() { // TODO: The plan doesn't actually need the tree to be made. Be smarter about it
		ArrayList<Node> path = new ArrayList<Node>();

		// A* algo
		Node chosenNode = this.tree.getRootNode();
		path.add(chosenNode);
		
		int level = 0;
		while (!this.tree.isChildless(chosenNode)) {
			level++;
			ArrayList<Node> directChildren = this.tree.getDirectChildren(chosenNode);
			
			// Find all of the nodes that have the greatest heuristic result
			ArrayList<Node> nodesWithMaxHeuristicResult = new ArrayList<Node>();
			double currentMaxHeuristicResult = 0;
			for (Node node : directChildren) {
				double heuristicResult = this.heuristic(node);
				if (heuristicResult > currentMaxHeuristicResult) {
					currentMaxHeuristicResult = heuristicResult;
					nodesWithMaxHeuristicResult = new ArrayList<Node>();
					nodesWithMaxHeuristicResult.add(node);
				} else if (heuristicResult == currentMaxHeuristicResult) {
					nodesWithMaxHeuristicResult.add(node);
				}
			}
			
			//The chosenNode is the first node in the childrenNode whose heuristic result is the highest
			chosenNode = nodesWithMaxHeuristicResult.get(0);
			path.add(chosenNode);
		}
		
		ArrayList<Action> actionsFromRootNodeToEndNode = new ArrayList<Action>();
		for (Node node : path) {
			actionsFromRootNodeToEndNode.addAll(node.getActionsToGetToThisNode());
		}
		
		
		Plan plan = new Plan(this.tree.getRootNode().getState().getCurrentCity(), actionsFromRootNodeToEndNode);

		
		this.plan = plan;
	}

	/**
	 * This is the heuristic used by the A* algo.
	 * 
	 * @param node
	 * @return an int between 0 and INF representing how "good" this node is. The
	 *         higher the int the better the heuristic
	 */
	abstract double heuristic(Node node);

	public Plan getPlan() {
		return this.plan;
	}
}