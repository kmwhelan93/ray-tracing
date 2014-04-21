import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sphere {
	private Vector center;
	private Color color;
	private BufferedImage texture;
	private double radius;
	private int id;

	// getters & setters
	public Vector getCenter() {
		return center;
	}

	public void setCenter(Vector center) {
		this.center = center;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	//TODO combine these getColor methods
//	public Color getColor() {
//		return this.color;
//	}
	//New get color method to add in texture mapping
	//Parameters: vector representation of point on sphere we're coloring
	public Color getColor(Vector pt) {
		if (texture == null || pt == null)
			return this.color;
		//vn: unit length vector pointing to "north pole" of sphere (+z)
		Vector vn = new Vector(0,1,0);
		//ve: unit length vector pointing to "equator" of sphere (+x)
		Vector ve = new Vector(1,0,0);
		//vp: normalized vector from center of sphere to point that's being colored
		Vector vp1 = new Vector(pt.get(0)-center.get(0), pt.get(1)-center.get(1), pt.get(2)-center.get(2));
		Vector vp = vp1.normalize();
		double phi = Math.acos(Math.toRadians(-vn.dotProduct(vp)));
		double v = phi / Math.PI;
		double theta = Math.acos(Math.toRadians(vp.dotProduct(ve) / Math.sin(Math.toRadians(phi)))) / (2*Math.PI);
		double u = 1 - theta;
		if (vn.crossProduct(ve).dotProduct(vp) > 0)
			u = theta;
		int rgb = texture.getRGB((int)(u * texture.getWidth()), (int)(v * texture.getHeight()));
		return new Color(((rgb >> 16) & 0xff)/255.0,
				((rgb >> 8) & 0xff)/255.0, ((rgb) & 0xff)/255.0);
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
	
	public void setTexture(String filename) {
		try {
			texture = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Error reading texture file");
		}
	}
	
	public void setTexture(BufferedImage t) {
		texture = t;
	}
	
	public BufferedImage getTexture() {
		return texture;
	}
	
	// Constructors
	public Sphere(Vector center, double radius, Color color) {
		this.center = center;
		this.radius = radius;
		this.color = color;
		this.texture = null;
	}
	
	public Vector getNormal(Vector location) {
		return location.subtract(this.center).normalize();
	}
}
