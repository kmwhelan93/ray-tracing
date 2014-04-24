
public class Cylinder extends Obstacle {
	Vector p1;
	Vector p2;
	double radius;
	Color color;
	Matrix rotate;
	
	public Cylinder(int id, double radius, Color color, Matrix rotate, double reflectiveness, double transparency) {
		this.id = id;
		this.radius = radius;
		this.reflectiveness = reflectiveness;
		this.transparency = transparency;
		this.color = color;
		this.rotate = rotate;
	}
	@Override
	public double findIntersection(Ray ray) {
		// E + tD
		Vector D = ray.getDirection();
		Vector E = ray.getOrigin();
		// First untransform D and E
		
		
		
		double a = D.get(0)*D.get(0) + D.get(1)*D.get(1);
		double b = 2*E.get(0)*D.get(0) + 2*E.get(1)*D.get(1);
		double c = E.get(0)*E.get(0) + E.get(1)*E.get(1) - 1;
		double sqrtPart = Math.sqrt(b*b-4*a*c);
		double smaller = (-1*b-sqrtPart) / (2*a);
		double bigger = (-1*b+sqrtPart) / (2*a);
		if (smaller > 0)
			return smaller;
		if (bigger > 0)
			return bigger;
		return -1;
	}
	@Override
	public Vector getNormal(Vector location) {
		return new Vector(location.get(0), location.get(1), 0).normalize();
	}
	@Override
	public Color getColor(Vector pt) {
		return this.color;
	}
	@Override
	public Vector getVector() {
		return null;
	}
	@Override
	public double getRadius() {
		return 1;
	}
	@Override
	public Color getColor() {
		return this.color;
	}
}
