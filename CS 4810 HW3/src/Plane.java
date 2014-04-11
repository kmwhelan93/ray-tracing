
public class Plane {
	double A;
	double B;
	double C;
	double D;
	Color color;
	
	public Plane(double A, double B, double C, double D, Color color) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.color = color;
	}
	
	public Vector getNormal() {
		return new Vector(this.A, this.B, this.C);
	}
	
	public double getD() {
		return this.D;
	}
	
	public Color getColor() {
		return this.color;
	}
}
