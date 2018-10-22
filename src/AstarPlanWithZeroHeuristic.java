
public class AstarPlanWithZeroHeuristic extends AstarPlan {	
	public AstarPlanWithZeroHeuristic(Tree tree) {
		super(tree);
	}

	@Override
	double h(Node node) {
		return 0;
	}
}
