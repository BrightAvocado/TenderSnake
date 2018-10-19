import java.util.ArrayList;
import java.util.Collections;
import logist.plan.Action;
import logist.plan.Plan;

public class BreadthFirstSearch {

	private static double bestDistance = Double.MAX_VALUE; //initialize bestDistance as the max possible distance such that any successful path trumps it.
	private Node bestNode=null;
	private Plan bestPlan; 
	private Tree tree;
	
	/*
	Create New BFS Object
	Inputs are vehicle and tree
	Assumptions include that the tree only contains valid actions/states
	*/
	public BreadthFirstSearch(Tree _tree){	
		tree = _tree;
	}
	
	public boolean determineMasterPlan(){
		
		//iterate through as many levels of the tree as possible
		boolean potentialChoicesExist = true;
		boolean solutionFound = false;
		int currentLevel = 0; 
				
		while(potentialChoicesExist){
			if(potentialChoicesExist)
			{
				ArrayList<Node> newNodes = new ArrayList<Node>();
				for(Node node : tree.getNodesAtLevel(currentLevel)){
					newNodes.addAll(tree.generateChildren(node, tree.getCapacity(), currentLevel));
				}
				//Generate Children of Surviving Nodes Here:
				tree.addLevel(newNodes);
			}
			potentialChoicesExist = checkLevel(currentLevel);
			currentLevel++; //go down one level (will only matter if checkLevel returns true (that there are more paths to check)
		}
		
		//see if a solution has been found
		if (bestNode != null){
			solutionFound = true;
			bestPlan = createNodePlan(bestNode);
		}
		return solutionFound;
	}
	
	private boolean checkLevel(int level){
		ArrayList<Node> levelNodes = tree.getNodesAtLevel(level);
		
		int levelSize = levelNodes.size();
		boolean morePathsExist = false;
		
		for(int i = levelSize-1; i>=0;i--){
			//first ignore any nodes that are worse than the current best
			Node node = levelNodes.get(i);
			if (node.getDistanceToRoot() > bestDistance){
				//killNodeChildren(node);
				killNode(level,i);
			}
			else if (tree.isChildless(node)){
				//if node is better than current best AND has no children, node becomes new best node
				bestDistance = node.getDistanceToRoot();
				bestNode = node;
			}
			else{
				morePathsExist = true; //if there is at least one node that has a smaller distance AND has children, continue searching
			}
		}		
		return morePathsExist;
	}
	
	private Plan createNodePlan(Node _node){		

		Node currentNode = _node;
		ArrayList<Action> actionsFromRootNodeToEndNode = new ArrayList<Action>();

		while (currentNode.getParent() != null) {
			actionsFromRootNodeToEndNode.addAll(0, currentNode.getActionsToGetToThisNode());
			currentNode = currentNode.getParent();
		}

		Plan plan = new Plan(this.tree.getRootNode().getState().getCurrentCity(), actionsFromRootNodeToEndNode);
		
		return plan;
	}
	
	private void killNode(int index, int level){		
		tree.removeNode(level, index);
	}
	
	public Plan getBestPlan(){
		return bestPlan;
	}	
}
