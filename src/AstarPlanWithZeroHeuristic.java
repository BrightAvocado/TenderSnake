
public class AstarPlanWithZeroHeuristic extends AstarPlan {	
	public AstarPlanWithZeroHeuristic(Tree tree) {
		super(tree);
	}

	@Override
	double h(Node node) {
		int amountCarriedTasks = node.getState().getCarriedTasks().size();
		int amountTasksToPickUp = node.getState().getTasksToPickUp().size();
		double d = getAverageDistanceBetweenPickup(node);
		
		return amountCarriedTasks*d/2 + amountTasksToPickUp*d;
	}
	
	private double getAverageDistanceBetweenPickup(Node node) {
		int initialAmountTasksToPickUp = this.tree.getRootNode().getState().getTasksToPickUp().size();
		int amountTasksPickedUp = initialAmountTasksToPickUp - node.getState().getTasksToPickUp().size();
		return node.getDistanceToRoot()/amountTasksPickedUp;
	}

}
