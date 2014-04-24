import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sphere extends Obstacle {
	private Vector center;
	private Color color;
	private BufferedImage texture;
	private BufferedImage bumpMap;
	private double radius;
	private int id;

	// getters & setters
	public Vector getCenter() {
		return this.center;
	}

	public void setCenter(Vector center) {
		this.center = center;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	// New get color method to add in texture mapping
	// Parameters: vector representation of point on sphere we're coloring, 
	// 		pass null for original color
	public Color getColor(Vector pt) {
		if (this.texture == null || pt == null)
			return this.color;
		// vn: unit length vector pointing to "north pole" of sphere (+z)
		Vector vn = new Vector(0, 1, 0);
		// ve: unit length vector pointing to "equator" of sphere (+x)
		Vector ve = new Vector(1, 0, 0);
		// vp: normalized vector from center of sphere to point that's being
		// colored
		Vector vp1 = new Vector(pt.get(0) - this.center.get(0), pt.get(1)
				- this.center.get(1), pt.get(2) - this.center.get(2));
		Vector vp = vp1.normalize();
		double phi = Math.acos(Math.toRadians(-vn.dotProduct(vp)));
		double v = phi / Math.PI;
		double theta = Math.acos(Math.toRadians(vp.dotProduct(ve)
				/ Math.sin(Math.toRadians(phi))))
				/ (2 * Math.PI);
		double u = 1 - theta;
		if (vn.crossProduct(ve).dotProduct(vp) > 0)
			u = theta;
		int rgb = this.texture.getRGB(
				(int) ((u * this.texture.getWidth() * 5) % this.texture
						.getWidth()),
				(int) ((v * this.texture.getHeight() * 50) % this.texture
						.getHeight()));
		return new Color(((rgb >> 16) & 0xff) / 255.0,
				((rgb >> 8) & 0xff) / 255.0, ((rgb) & 0xff) / 255.0);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getRadius() {
		return this.radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public BufferedImage getTexture() {
		return this.texture;
	}
	public void setTexture(String filename) {
		try {
			this.texture = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Error reading texture file");
		}
	}

	public void setTexture(BufferedImage t) {
		this.texture = t;
	}
	
	public BufferedImage getBumpMap() {
		return this.bumpMap;
	}

	public void setBumpMap(String filename) {
		try {
			this.bumpMap = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Error reading bump map file");
		}
	}

	public void setBumpMap(BufferedImage b) {
		this.bumpMap = b;
	}

	// Constructors
	public Sphere(Vector center, double radius, Color color, double reflectiveness) {
		this.center = center;
		this.radius = radius;
		this.color = color;
		this.texture = null;
		this.bumpMap = null;
		this.reflectiveness = reflectiveness;
	}

	// Altered for bump mapping
	public Vector getNormal(Vector location) {
		if (this.bumpMap == null || location == null)
			return location.subtract(this.center).normalize();
		// vn: unit length vector pointing to "north pole" of sphere (+z)
		Vector vn = new Vector(0, 1, 0);
		// ve: unit length vector pointing to "equator" of sphere (+x)
		Vector ve = new Vector(1, 0, 0);
		// vp: normalized vector from center of sphere to point that's being
		// colored
		Vector vp1 = new Vector(location.get(0) - this.center.get(0),
				location.get(1) - this.center.get(1), location.get(2)
						- this.center.get(2));
		Vector vp = vp1.normalize();
		double phi = Math.acos(Math.toRadians(-vn.dotProduct(vp)));
		double v = phi / Math.PI;
		double theta = Math.acos(Math.toRadians(vp.dotProduct(ve)
				/ Math.sin(Math.toRadians(phi))))
				/ (2 * Math.PI);
		double u = 1 - theta;
		if (vn.crossProduct(ve).dotProduct(vp) > 0)
			u = theta;
		int rgb = this.bumpMap.getRGB(
				(int) ((u * this.bumpMap.getWidth() * 5) % this.bumpMap
						.getWidth()),
				(int) ((v * this.bumpMap.getHeight() * 50) % this.bumpMap
						.getHeight()));
		Color forNormal = new Color(((rgb >> 16) & 0xff) / 255.0,
				((rgb >> 8) & 0xff) / 255.0, ((rgb) & 0xff) / 255.0);
		//Normal determined from rgb colors
		//r = (x+1)/2, g = (y+1)/2, b = (z+1)/2
		Vector newNormal = new Vector(forNormal.getRed() * 2 - 1,
				forNormal.getGreen() * 2 - 1, forNormal.getBlue() * 2 - 1);
		return newNormal;
	}

	@Override
	public double findIntersection(Ray ray) {
		double a = ray.getDirection().dotProduct(ray.getDirection());
		double b = 2 * ray.getOrigin().subtract(this.getCenter())
				.dotProduct(ray.getDirection());
		double c = ray.getOrigin().subtract(this.getCenter())
				.dotProduct(ray.getOrigin().subtract(this.getCenter()))
				- this.getRadius() * this.getRadius();
		double presqrt = b * b - 4 * a * c;
		if (presqrt < 0) {
			return -1;
		}
		double sqrt = Math.sqrt(presqrt);
		double smaller = (-1 * b - sqrt) / (2 * a);
		if (smaller > 0) {
			return smaller;
		}
		double bigger = (-1 * b + sqrt) / (2 * a);
		if (bigger > 0) {
			return bigger;
		}
		return -1;
	}

	public Vector getVector() {
		return this.getCenter();
	}

	@Override
	public Color getColor() {
		return this.getColor(null);
	}

}
