
public class Vertex implements Comparable<Vertex> {
	//fields
	private double x;
	private double y;
	private double z;
	private double w;
	private Color color;
	private int direction;
	private double width;
	private Vector normal;
	private int id;
	
	// getters and setters
	public Vector getVector() {
		return new Vector(x, y, z);
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	public double getW() {
		return w;
	}
	public void setW(double w) {
		this.w = w;
	}
	public Color getColor() {
		return this.color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	// Constructors
	
	public Vector getNormal() {
		return normal;
	}
	public void setNormal(Vector normal) {
		this.normal = normal;
	}
	public Vertex(double x, double y, double z, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
		this.color = color;
	}
	public Vertex(double x, double y, double z, Color color, Vector normal) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
		this.color = color;
		this.normal = normal;
	}
	public Vertex(double x, double y, double z, double w, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.color = color;
	}
	public Vertex(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
		this.color = new Color();
	}
	public Vertex(double x, double y, double z, double w) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.color = new Color();
	}

	public Vertex(double x, double y, Color color, int direction) {
		super();
		this.x = x;
		this.y = y;
		this.color = color;
		this.direction = direction;
	}
	public Vertex(Vertex v) {
		this.x = v.getX();
		this.y = v.getY();
		this.z = v.getZ();
		this.w = v.getW();
		this.color = new Color(v.getColor());
		this.direction = v.getDirection();
		this.width = v.getWidth();
		this.normal = v.getNormal();
	}
	public Vertex(double x, double y, double z, Color color, double width) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
		this.color = color;
		this.width = width;
	}
	public Vertex(double x, double y, double z, double w, Color color, double width) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.color = color;
		this.width = width;
	}
	public Vertex(Vector v) {
		this.x = v.get(0);
		this.y = v.get(1);
		this.z = v.get(2);
		if (v.size() == 4) {
			this.w = v.get(3);
		} else {
			this.w = 1;
		}
		this.color = new Color();
	}
	public Vertex(Vector v, Color color) {
		this.x = v.get(0);
		this.y = v.get(1);
		this.z = v.get(2);
		if (v.size() == 4) {
			this.w = v.get(3);
		} else {
			this.w = 1;
		}
		this.color = new Color(color);
	}
	public Vertex(Vector v, Color color, Vector normal) {
		this.x = v.get(0);
		this.y = v.get(1);
		this.z = v.get(2);
		if (v.size() == 4) {
			this.w = v.get(3);
		} else {
			this.w = 1;
		}
		this.color = new Color(color);
		this.normal = new Vector(normal);
	}
	// Methods
	public double distanceTo(Vertex v) {
		return Math.pow((this.x - v.getX()) * (this.x - v.getX()) + (this.y - v.getY()) * (this.y - v.getY()
				+ (this.z - v.getZ()) * (this.z - v.getZ())), .5);
	}
	@Override
	public String toString() {
		return "Vertex [x=" + x + ", y=" + y + ", z=" + this.z + ", color:" + color + "]";
	}
	@Override
	public int compareTo(Vertex v) {
		return (int) (this.getX() - v.getX());
	}
	
}
