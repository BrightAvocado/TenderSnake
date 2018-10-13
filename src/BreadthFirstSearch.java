import java.util.ArrayList;
import java.util.List;

import logist.topology.Topology.City;
import logist.plan.Action;
import logist.plan.Plan;
import logist.simulation.Vehicle;

public class BreadthFirstSearch {

	private static double bestDistance = Double.MAX_VALUE; //initialize bestDistance as the max possible distance such that any successful path trumps it.
	private List<Action> cityActions = new ArrayList<Action>();
	private Plan bestPlan; 
	private Tree tree;
	private Vehicle vehicle;
	
	/*
	Create New BFS Object
	Inputs are vehicle and tree
	Assumptions include that the tree only contains valid actions/states
	*/
	
	public BreadthFirstSearch(Vehicle _vehicle, Tree _tree){
		//TODO		
		tree = _tree;
		vehicle = _vehicle;
	}
	
	private void determineMasterPlan(){
		
		//iterate through as many levels of the tree as possible
		
		
	}
	
	private void checkLevel(int level){
		ArrayList<Node> levelNodes = tree.getNodesAtLevel(level);
		
		foreach(Node node : levelNodes){
			//first ignore any nodes that are worse than the current best
			if (node.distanceToRoot > bestDistance){
				killNodeChildren(node);
			}
		}
	}
	
	private void setBestPlan(){
		//when goal condition is set, set the best plan
		
	}
	
	private void killNodeChildren(Node node){
		// remove nodes from all next levels of tree that are not worth investigating (i.e. when parent's distance is > bestDistance)
		
	}
	
	public Plan getBestPlan(){
		return bestPlan;
	}	
}
