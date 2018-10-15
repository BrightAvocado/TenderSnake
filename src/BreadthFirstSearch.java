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
		
		for(int i = 0; i<levelSize;i++){
			//first ignore any nodes that are worse than the current best
			Node node = levelNodes.get(i);
			if (node.getDistanceToRoot() > bestDistance){
				killNodeChildren(node);
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
		Node rootNode = tree.getRootNode();
			
		//move up from node until root is found, and create a list of actions in reverse	
		ArrayList<Action> actionList = _node.getActionsToGetToThisNode();			
		
		Collections.reverse(actionList);
				
		Plan plan = new Plan(rootNode.getState().getCurrentCity(),actionList);
		
		return plan;
	}
	
	private void killNodeChildren(Node node){
		//TODO: remove nodes from all next levels of tree that are not worth investigating (i.e. when parent's distance is > bestDistance)
		int level = node.getTreeLevel();
		ArrayList<Node> nodeList = tree.getNodesAtLevel(level+1);
		//recursively remove children from Tree (if there are children)
		if(!tree.isChildless(node)){
			for(Node subNode : nodeList){
				if(subNode.getParent() == node)
				{
					//if the child node's parent is the node we are currently removing, 
					//then use this function to recursively remove it and it's children
					killNodeChildren(subNode);
				}
			}
		}
		//also then remove parent
		int nodeIndex = nodeList.indexOf(node);
		tree.removeNode(level+1, nodeIndex);
	}
	
	public Plan getBestPlan(){
		return bestPlan;
	}	
}
