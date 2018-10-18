import logist.task.Task;

public class AstarPlanWithUnderestimatingHeuristic extends AstarPlan {
	private double minDistanceToTravelToDeliver;

	public AstarPlanWithUnderestimatingHeuristic(Tree tree) {
		super(tree);

		double minDistanceToTravelToDeliver = Double.MAX_VALUE;
		for (Task task : tree.getRootNode().getState().getTasksToPickUp()) {
			if (task.deliveryCity.distanceTo(task.pickupCity) < minDistanceToTravelToDeliver) {
				minDistanceToTravelToDeliver = task.deliveryCity.distanceTo(task.pickupCity);
			}
		}
		this.minDistanceToTravelToDeliver = minDistanceToTravelToDeliver;
	}

	@Override
	double h(Node node) {
		int amountCarriedTasks = node.getState().getCarriedTasks().size();
		int amountTasksToPickUp = node.getState().getTasksToPickUp().size();
		double d = this.minDistanceToTravelToDeliver;

		return amountCarriedTasks * d + amountTasksToPickUp * d * 2;
	}

}
