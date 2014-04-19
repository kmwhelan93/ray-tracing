import java.text.DecimalFormat;
import java.util.Arrays;


public class Vector extends Matrix {
	
	boolean isLight = false;
	Color lightColor;
	
	public Vector(double... values) {
		matrix = new double[values.length][1];
		for (int i = 0; i < values.length; i++) {
			matrix[i][0] = values[i];
		}
	}
	public Vector(Matrix m) {
		this.matrix = m.getMatrix();
	}
	public Vector(Vertex v, boolean includeW) {
		int size = 3;
		if (includeW)
			size++;	
		matrix = new double[size][1];
		this.set(0, v.getX());
		this.set(1, v.getY());
		this.set(2, v.getZ());
		if (includeW) 
			this.set(3, v.getW());
	}
	public Vector(int size) {
		matrix = new double[size][1];
	}
	public double get(int index) {
		return this.matrix[index][0];
	}
	public void set(int index, double value) {
		this.matrix[index][0] = value;
	}
	
	public void setLight(boolean value) {
		this.isLight = value;
	}
	
	public Color getColor() {
		return this.lightColor;
	}
	public void setColor(Color color) {
		this.lightColor = color;
	}
	
	public boolean getLight() {
		return this.isLight;
	}
	
	public double dotProduct(Vector v) {
		double sum = 0;
		for (int i = 0; i < this.size(); i++) {
			sum += v.get(i)*this.get(i);
		}
		return sum;
	}
	public Vector crossProduct(Vector v) {
		double x = this.get(1)*v.get(2) - this.get(2)*v.get(1);
		double y = -(this.get(0) * v.get(2) - this.get(2) * v.get(0));
		double z = this.get(0) * v.get(1) - this.get(1) * v.get(0);
		return new Vector(x, y, z);
	}
	public int size() {
		return matrix.length;
	}
	public double magnitude() {
		double sum = 0;
		for (int i = 0; i < this.size(); i++) {
			sum += this.get(i) * this.get(i);
		}
		return Math.sqrt(sum);
	}
	public Vector normalize() {
		Vector result = new Vector(this.size());
		double magnitude = this.magnitude();
		for (int i = 0; i < this.size(); i++) {
			result.set(i, this.get(i) / magnitude);
		}
		return result;
	}
	public Vector add(Vector v) {
		Vector result = new Vector(v.size());
		for (int i = 0; i < this.size(); i ++) {
			result.set(i, this.get(i) + v.get(i));
		}
		return result;
	}
	
	public Vector subtract(Vector v) {
		Vector result = new Vector(v.size());
		/* removed because its annoying
		 * if (this.size() != v.size()) {
			throw new Exception();
		}*/
		for (int i = 0; i < this.size(); i ++) {
			result.set(i, this.get(i) - v.get(i));
		}
		return result;
	}
	
	public Vector divide(Vector v) {
		Vector result = new Vector(v.size());
		for (int i = 0; i < this.size(); i ++) {
			result.set(i, this.get(i) / v.get(i));
		}
		return result;
	}
	
	public Vector scale(double factor) {
		Vector result = new Vector(this.size());
		for (int i = 0; i < this.size(); i++) {
			result.set(i, factor * this.get(i));
		}
		return result;
	}
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00");
		String result = "[" + df.format(this.get(0));
		
		for (int i = 1; i < this.size(); i++) {
			result += ", " + df.format(this.get(i));
		}
		result += "]";
		return result;
	}
	
	public Vector reverse() {
		return this.scale(-1);
	}
	
}
