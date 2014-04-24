import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Plane extends Obstacle {
	private double A;
	private double B;
	private double C;
	private double D;
	private Color color;
	private BufferedImage texture;
	private BufferedImage bumpMap;

	
	public Plane(int id, double A, double B, double C, double D, Color color, double reflectiveness) {
		this.id = id;
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.color = color;
		this.texture = null;
		this.bumpMap = null;
		this.reflectiveness = reflectiveness;
	}

	
	//Altered for bump mapping
	//Parameter: vector of point on plane being colored
	//Pass null to get actual normal of plane
	public Vector getNormal(Vector pt) {
		if (this.bumpMap == null || pt == null)
			return new Vector(this.A, this.B, this.C);
		double u = Math.abs(pt.dotProduct(this.getUaxis()));
		double v = Math.abs(pt.dotProduct(this.getVaxis()));
		int rgb = this.bumpMap.getRGB(
				(int) ((u * this.bumpMap.getWidth()/4) % this.bumpMap.getWidth()),
				(int) ((v * this.bumpMap.getHeight()/4) % this.bumpMap.getHeight()));
		Color forNormal = new Color(((rgb >> 16) & 0xff) / 255.0,
				((rgb >> 8) & 0xff) / 255.0, ((rgb) & 0xff) / 255.0);
		Vector newNormal = new Vector(forNormal.getRed() * 2 - 1,
				forNormal.getGreen() * 2 - 1, forNormal.getBlue() * 2 - 1);
		return newNormal;
	}

	public double getD() {
		return this.D;
	}

	public double getA() {
		return this.A;
	}

	public void setA(double a) {
		this.A = a;
	}

	public double getB() {
		return this.B;
	}

	public void setB(double b) {
		this.B = b;
	}

	public double getC() {
		return this.C;
	}

	public void setC(double c) {
		this.C = c;
	}

	public void setD(double d) {
		this.D = d;
	}

	// For texture/bump mapping
	public Vector getUaxis() {
		Vector n = this.getNormal(null);
		Vector u = new Vector(n.get(1), n.get(2), -n.get(0));
		return u;
	}


	// For texture/bump mapping
	public Vector getVaxis() {
		Vector v = this.getUaxis().crossProduct(this.getNormal(null));
		return v;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	// Altered for texture mapping
	//Parameter: vector for point on plane being colored
	//Pass null to get original color of plane (not texture color)
	public Color getColor(Vector pt) {
		if (this.texture == null || pt == null)
			return this.color;
		double u = Math.abs(pt.dotProduct(this.getUaxis()));
		double v = Math.abs(pt.dotProduct(this.getVaxis()));
		int rgb = this.texture.getRGB(
				(int) ((u * this.texture.getWidth()) % this.texture.getWidth()),
				(int) ((v * this.texture.getHeight()) % this.texture.getHeight()));
		return new Color(((rgb >> 16) & 0xff) / 255.0,
				((rgb >> 8) & 0xff) / 255.0, ((rgb) & 0xff) / 255.0);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTexture(String filename) {
		try {
			this.texture = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Error reading texture file");
		}
	}

	@Override
	public double findIntersection(Ray ray) {
		double t = (-1 * this.D - this.getNormal(null).dotProduct(
				ray.getOrigin()))
				/ this.getNormal(null).dotProduct(ray.getDirection());
		return t;
	}
	public void setBumpMap(String filename) {
		try {
			this.bumpMap = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Error reading bump map file");
		}
	}

	// doesnt make sense
	@Override
	public Vector getVector() {
		return null;
	}


	// doesnt make sense
	@Override
	public double getRadius() {
		return 0;
	}


	@Override
	public Color getColor() {
		return this.getColor(null);
	}
}
