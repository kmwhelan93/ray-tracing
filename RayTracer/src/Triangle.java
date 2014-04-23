import java.awt.image.BufferedImage;


public class Triangle extends Obstacle {
	private int id;
	private Vector p1;
	private Vector p2;
	private Vector p3;
	private BufferedImage texture;
	private BufferedImage bumpMap;
	
	
	public Triangle(int id, Vector p1, Vector p2, Vector p3) {
		this.id = id;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	
	
	@Override
	public double findIntersection(Ray ray) {
		Vector normal = this.getNormal(null);
		double d =-1*( p1.dotProduct(normal));
		Plane p = new Plane(normal.get(0), normal.get(1), normal.get(2), d, null);
		double t = p.findIntersection(ray);
//		System.out.println("ray: " + ray);
		Vector intersectionPoint = ray.scale(t);
//		System.out.println(intersectionPoint);
//		try {
//		Thread.sleep(10);
//		} catch (Exception e) {
//			
//		}
		// find out if this point is within triangle
		// edges
		Vector e1 = p2.subtract(p1);
		Vector e2 = p3.subtract(p2);
		Vector e3 = p1.subtract(p3);
		// vectors pointing into center of triangle
		Vector a1 = e1.crossProduct(normal);
		Vector a2 = e2.crossProduct(normal);
		Vector a3 = e3.crossProduct(normal);
//		System.out.println("a1: " + a1);
//		System.out.println("a2: " + a2);
//		System.out.println("a3: " + a3);
//		System.out.println(a1.size() + " " + p1.size() + " " + intersectionPoint.size());
		double dp1 = a1.dotProduct(intersectionPoint.subtract(p1));
		double dp2 = a2.dotProduct(intersectionPoint.subtract(p2));
		double dp3 = a3.dotProduct(intersectionPoint.subtract(p3));
		if (dp1 < 0 || dp2 < 0 || dp3 < 0) {
			return -1;
		}
		return t;
	}

	@Override
	public Vector getNormal(Vector location) {
		return p2.subtract(p1).crossProduct(p1.subtract(p3)).normalize();
	}

	@Override
	public Color getColor(Vector pt) {
		if (pt == null) {
			return p1.getColor();
		}
		if (this.texture == null) {
			// gaurad interpolate
			Vector normal = this.getNormal(null);
			Vector e1 = p2.subtract(p1);
			Vector e2 = p3.subtract(p2);
			Vector e3 = p1.subtract(p3);
			// vectors pointing into center of triangle
			Vector a1 = e1.crossProduct(normal).normalize();
			Vector a2 = e2.crossProduct(normal).normalize();
			Vector a3 = e3.crossProduct(normal).normalize();
			// bi says how close pt is to pi
			double b1 = a1.dotProduct(pt.subtract(p1).normalize());
			double b2 = a2.dotProduct(pt.subtract(p2).normalize());
			double b3 = a3.dotProduct(pt.subtract(p3).normalize());
			return p1.getColor().multiply(b1).add(p2.getColor().multiply(b2)).add(p3.getColor().multiply(b3));
		}

		// TODO Courtney if you'd like
		// Also, if you happen to see this, consider making setTexture a method in the Obstacle interface
		// That way you could just have one texture command but its nbd
		return p1.getColor();
	}

	// doesnt make sense
	@Override
	public Vector getVector() {
		// TODO Auto-generated method stub
		return null;
	}

	// doesnt make sense
	@Override
	public double getRadius() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public Color getColor() {
		return this.getColor(null);
	}
	
	@Override
	public String toString() {
		return "Triangle [id=" + id + ", p1=" + p1 + ", p2=" + p2 + ", p3="
				+ p3 + ", texture=" + texture + ", bumpMap=" + bumpMap + "]";
	}



	


}
