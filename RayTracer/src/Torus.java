
public class Torus extends Obstacle {
	private Vector center;
	private Color color;
	private double majorRadius;
	private double minorRadius;
	@Override
	public double findIntersection(Ray ray) {
		double pz = ray.getOrigin().get(2);
		double dz = ray.getDirection().get(2);
		//compute coefficients...
		double a = Math.pow(ray.getDirection().dotProduct(ray.getDirection()), 2);
		double b = 4 * ray.getDirection().dotProduct(ray.getDirection()) * ray.getOrigin().dotProduct(ray.getDirection());
		double c = 4 * Math.pow(ray.getOrigin().dotProduct(ray.getDirection()), 2)
				+ 2 * ray.getDirection().dotProduct(ray.getDirection())
				* ((ray.getOrigin().dotProduct(ray.getOrigin())) - Math.pow(minorRadius, 2) - Math.pow(majorRadius, 2))
				+ 4 * Math.pow(majorRadius, 2) * dz;
		double d = 4 * ray.getOrigin().dotProduct(ray.getDirection())
				* (ray.getOrigin().dotProduct(ray.getOrigin()) - Math.pow(minorRadius, 2) - Math.pow(majorRadius, 2))
				+ 8 * Math.pow(majorRadius, 2) * pz * dz;
		double e = Math.pow(ray.getOrigin().dotProduct(ray.getOrigin()) - Math.pow(minorRadius, 2) - Math.pow(majorRadius, 2), 2)
				+ 4 * Math.pow(majorRadius, 2) * Math.pow(pz, 2)
				- 4 * Math.pow(majorRadius, 2) * Math.pow(minorRadius, 2);
		//compute sub problems...
		double p1 = (2 * Math.pow(c, 3)) - (9 * b * c * d) + (27 * b * b * e) - (72 * a * c * e);
		double p2 = p1 + Math.sqrt(-4 * Math.pow(((c * c) - (3* b * d) + (12 * a * e)), 3));
		double p3 = ((c*c) - (3 * b * d) + (12 * a * e) / (3 * a * Math.pow(p2/2, .333))) + Math.pow(p2/2, .333)/ (3 * a);
		double p4 = Math.sqrt(((b*b)/(4 * a * a)) - ((2*c)/(3*a)) + p3);
		double p5 = ((b*b)/(2*a*a)) - ((4*c)/(3*a)) - p3;
		double p6 = ( ((b*b*b*(-1))/(a*a*a)) + ((4*b*c)/(a*a)) - ((8*d)/(a))) / (4 * p4);
		//compute solutions...
		double sol1 = ((-1)*(b/(4*a))) - (p4/2) - (Math.sqrt(p5-p6)/2);
		double sol2 = ((-1)*(b/(4*a))) - (p4/2) + (Math.sqrt(p5-p6)/2);
		double sol3 = ((-1)*(b/(4*a))) + (p4/2) - (Math.sqrt(p5+p6)/2);
		double sol4 = ((-1)*(b/(4*a))) + (p4/2) + (Math.sqrt(p5+p6)/2);
		double t = Math.min(sol1, sol2);
		t = Math.min(t, sol3);
		t = Math.min(t, sol4);
		return t;
	}

	@Override
	public Vector getNormal(Vector location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getColor(Vector pt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector getVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getRadius() {
		return this.majorRadius;
	}

	@Override
	public Color getColor() {
		return this.color;
	}

}
