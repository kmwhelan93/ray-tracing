
public class CollisionPoint {
	private Obstacle obstacle;
	private Vector location;
	private Vector incomingSightVector;
	public Obstacle getObstacle() {
		return obstacle;
	}
	public void setObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}
	public Vector getLocation() {
		return location;
	}
	public void setLocation(Vector location) {
		this.location = location;
	}
	public Vector getIncomingSightVector() {
		return incomingSightVector;
	}
	public void setIncomingSightVector(Vector incomingSightVector) {
		this.incomingSightVector = incomingSightVector;
	}
	public CollisionPoint(Obstacle obstacle, Vector location,
			Vector incomingSightVector) {
		super();
		this.obstacle = obstacle;
		this.location = location;
		this.incomingSightVector = incomingSightVector;
	}
	
	
	
}
