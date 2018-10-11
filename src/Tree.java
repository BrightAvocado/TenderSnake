import java.util.ArrayList;
import java.util.Arrays;

import logist.task.TaskSet;
import logist.topology.Topology.City;

public class Tree {
	private ArrayList<ArrayList<Node>> nodes;

	public Tree(State currentState) {
		/*
		 * Node sitting at the top of the tree, parent of all the children, (dutiful
		 * protecter of the realm)
		 */
		Node rootNode = new Node(null, currentState);
		this.nodes.add(new ArrayList<Node>(Arrays.asList(rootNode)));

		// TODO: Populate the tree

		boolean allNodesAtThisLevelAreChildless = rootNode.isChildless();
		int currentLevel = 1;
		while (!allNodesAtThisLevelAreChildless) {
			ArrayList<Node> nodesAtNextLevel = new ArrayList<Node>();

			for (Node node : this.getNodesAtLevel(currentLevel)) {
				ArrayList<Node> childrenOfThisNode = this.generateChildren(node);
				nodesAtNextLevel.addAll(childrenOfThisNode);
			}
			this.nodes.add(nodesAtNextLevel);
			currentLevel++;
			
			//Find out if ALL the child at this level are childless
			allNodesAtThisLevelAreChildless = false;
			for (int i = 0; i < this.getNodesAtLevel(currentLevel).size() && !allNodesAtThisLevelAreChildless;i++) {
				Node node = this.getNodesAtLevel(currentLevel).get(i);
				if (!node.isChildless()) {
					allNodesAtThisLevelAreChildless = true;
				}
			}
		}

	}

	/*
	 * Generate ALL tahe possible children Nodes coming from parentNode and using
	 * the carriedTasks and TaskSet of parentNode to find them
	 */
	private ArrayList<Node> generateChildren(Node parentNode) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Once the tree is fully formed, prune it so that all the children of
	 * impossible path (because too heavy) are dropped
	 */
	private void prune() {
		// TODO
	}

	public ArrayList<Node> getNodesAtLevel(int level) {
		return this.nodes.get(level);
	}
}
