
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

}
