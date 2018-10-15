import java.util.ArrayList;

import logist.plan.Plan;

public class AstarPlan {
	private Tree tree;

	public AstarPlan(Tree tree) {
		this.tree = tree;
	}

	public Plan getPlan() {
		return null;
		// TODO
	}
	
	/*
	 * Returns an ArrayList<Node>.
	 * Its first element is the root node of this.tree.
	 * Its last element is childNode.
	 * The elements inbetween are the nodes necessary to be travelled
	 * through in order to reach childNode.
	 */
	private ArrayList<Node> getPathFromChild(Node childNode) {
		ArrayList<Node> path = new ArrayList<Node>();
		path.add(childNode);
		
		Node currentNode = childNode;
		while (currentNode.getParent() != null) {
			currentNode = currentNode.getParent();
			path.add(0, currentNode);
		}
		
		return path;
	}
}