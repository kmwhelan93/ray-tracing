
public class Plane {
	double A;
	double B;
	double C;
	double D;
	Color color;
	int id;
	
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

	public Color getColor() {
		return this.color;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
