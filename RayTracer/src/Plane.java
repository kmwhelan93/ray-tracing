import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Plane {
	double A;
	double B;
	double C;
	double D;
	Color color;
	BufferedImage texture;
	int id;
	
	public Plane(double A, double B, double C, double D, Color color) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.color = color;
		this.texture = null;
	}
	
	public Vector getNormal() {
		return new Vector(this.A, this.B, this.C);
	}
	
	public double getD() {
		return this.D;
	}
	
	public double getA() {
		return A;
	}

	public void setA(double a) {
		A = a;
	}

	public double getB() {
		return B;
	}

	public void setB(double b) {
		B = b;
	}

	public double getC() {
		return C;
	}

	public void setC(double c) {
		C = c;
	}

	public void setD(double d) {
		D = d;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	//TODO combine these getColor methods
	public Color getColor() {
		return this.color;
	}

	public Color getColor(double x, double y, double depth) {
		if (texture == null)
			return this.color;
		int rgb = texture.getRGB((int)(x % texture.getWidth()), (int)(y % texture.getHeight()));
		return new Color((rgb >> 16) & 0xff,
				(rgb >> 8) & 0xff, (rgb) & 0xff);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setTexture(String filename) {
		try {
			texture = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("Error reading texture file");
		}
	}
}
