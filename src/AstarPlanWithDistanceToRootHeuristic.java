
public class AstarPlanWithDistanceToRootHeuristic extends AstarPlan {

	public AstarPlanWithDistanceToRootHeuristic(Tree tree) {
		super(tree);
	}
	
	@Override
	double heuristic(Node node) {
		return -node.getDistanceToRoot();
	}

}
