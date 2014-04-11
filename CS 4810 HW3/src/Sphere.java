public class Sphere {
	private Vector center;
	private Color color;
	private double radius;

	// getters & setters
	public Vector getCenter() {
		return center;
	}

	public void setCenter(Vector center) {
		this.center = center;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	// Constructors
	public Sphere(Vector center, double radius, Color color) {
		this.center = center;
		this.radius = radius;
		this.color = color;
	}
	
	public Vector getNormal(Vector location) {
		return location.subtract(this.center).normalize();
	}
}
