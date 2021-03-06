
public class Torus extends Obstacle {
	private Vector center;
	private Color color;
	private double majorRadius;
	private double minorRadius;
	//added constructor
	public Torus(Vector center, Color color, double majorRadius, double minorRadius) {
		this.center = center;
		this.color = color;
		this.majorRadius = majorRadius;
		this.minorRadius = minorRadius;
	}
	
	public Vector getCenter() {
		return center;
	}
	
	public double getMinR() {
		return minorRadius;
	}
	
	public double getMaxR() {
		return majorRadius;
	}
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
//		System.out.println(a + " " + b + " " + c + " " + d + " " + e);
		//compute sub problems...
		double p1 = (2 * Math.pow(c, 3)) - (9 * b * c * d) + (27 * a * d * d) + (27 * b * b * e) - (72 * a * c * e);
		double temp = -4 * Math.pow(((c * c) - (3* b * d) + (12 * a * e)), 3) + Math.pow(p1,2);
		double p2;
		if(temp < 0){
			p2 = Double.POSITIVE_INFINITY;
		} else {
			p2 = p1 + Math.sqrt(temp);	
		}
		double p3 = (((c*c) - (3 * b * d) + (12 * a * e)) / (3 * a * Math.pow(p2/2, 1/3.0))) + Math.pow(p2/2, 1/3.0)/ (3 * a);
		double p4;
		double temp2 = ((b*b)/(4 * a * a)) - ((2*c)/(3*a)) + p3;
		if(temp2 < 0){
			p4 = Double.POSITIVE_INFINITY;
		} else {
			p4 = Math.sqrt(temp2);	
		}
		double p5 = ((b*b)/(2*a*a)) - ((4*c)/(3*a)) - p3;
		double p6 = ( ((b*b*b*(-1))/(a*a*a)) + ((4*b*c)/(a*a)) - ((8*d)/(a))) / (4 * p4);
		//compute solutions...
		double sol1, sol2;
		if(p5-p6 < 0){
			sol1 = Double.POSITIVE_INFINITY;
			sol2 = Double.POSITIVE_INFINITY;
		} else {
			sol1 = ((-1)*(b/(4*a))) - (p4/2) - (Math.sqrt(p5-p6)/2);
			sol2 = ((-1)*(b/(4*a))) - (p4/2) + (Math.sqrt(p5-p6)/2);
		}
		double sol3, sol4;
		if(p5+p6 < 0){
			sol3 = Double.POSITIVE_INFINITY;
			sol4 = Double.POSITIVE_INFINITY;
		} else {
			sol3 = ((-1)*(b/(4*a))) + (p4/2) - (Math.sqrt(p5+p6)/2);
			sol4 = ((-1)*(b/(4*a))) + (p4/2) + (Math.sqrt(p5+p6)/2);
		}
//		if (sol1 < 0)
//			sol1 = Double.POSITIVE_INFINITY;
//		if (sol2 < 0)
//			sol2 = Double.POSITIVE_INFINITY;
//		if (sol3 < 0)
//			sol3 = Double.POSITIVE_INFINITY;
//		if (sol4 < 0)
//			sol4 = Double.POSITIVE_INFINITY;
		double t = Math.min(sol1, sol2);
		t = Math.min(t, sol3);
		t = Math.min(t, sol4);
		return t;
	}

	@Override
	public Vector getNormal(Vector location) {
		Vector q = location.scale(this.majorRadius / Math.sqrt(Math.pow(location.get(0), 2) + Math.pow(location.get(1), 2)));
		Vector n = location.subtract(q);
		n = n.normalize();
		return n;
	}

	@Override
	public Color getColor(Vector pt) {
		return this.color;
	}

	@Override
	public Vector getVector() {
		return this.center;
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
