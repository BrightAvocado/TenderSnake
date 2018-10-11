import logist.topology.Topology.City;

/**
 * This class represents a State for the deliberative solution. We need this
 * because last time, debugging was a pain.
 * 
 * The state of the vehicle can be entirely defined by where it's coming from and where it's going. (really ? No tasks it's carrying ?)
 */
public class State {

	private City fromCity;
	private City toCity;
	
	public State(City fromCity, City toCity) {
		this.fromCity = fromCity;
		this.toCity = toCity;
	}

	@Override
	public boolean equals(Object that) {
		//TODO
		return true;
	}
	
	@Override
	public String toString() {
		//TODO
		return "todo";
	}
}
