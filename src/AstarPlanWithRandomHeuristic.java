
public class AstarPlanWithRandomHeuristic extends AstarPlan {

	public AstarPlanWithRandomHeuristic(Tree tree) {
		super(tree);
	}

	@Override
	double heuristic(Node node) {
		return Math.random()*100;
	}

}
