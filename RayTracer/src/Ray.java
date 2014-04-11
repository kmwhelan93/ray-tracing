
public class Ray {
	private Vector origin;
	private Vector direction;
	
	// getters & setters
	public Vector getOrigin() {
		return origin;
	}

	public void setOrigin(Vector origin) {
		this.origin = origin;
	}

	public Vector getDirection() {
		return direction;
	}

	public void setDirection(Vector direction) {
		this.direction = direction;
	}
	
	// constructors
	public Ray(Vector origin, Vector direction) {
		this.origin = origin;
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "Ray [origin=" + origin + ", direction=" + direction + "]";
	}
	
	public Vector scale(double t) {
		return this.origin.add(this.direction.scale(t));
	}

	
}
