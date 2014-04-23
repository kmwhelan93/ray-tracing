
public class Bulb extends Light {
	private Vector location;
	
	public Bulb(int id, Vector location, Color color) {
		this.id = id;
		this.location = location;
		this.color = color;
	}

	@Override
	public Vector getDirection(Vector objectLocation) {
		return this.location.subtract(objectLocation);
	}

	@Override
	public Vector getVector() {
		return this.location;
	}

	@Override
	public double getRadius() {
		// TODO Auto-generated method stub
		return -1;
	}

}
