public abstract class Obstacle extends Moveable {
	// KW: made an abstract class so that it can extend Moveable

	// reflectiveness ranges from 0 to 1. 1 being perfectly reflective and 0 not
	// reflective at all.
	protected double reflectiveness = 0;
	// transparency ranges from 0 to 1. 1 being perfectly transparent and 0 not
	// transparent at all.
	protected double transparency = 0;

	// If intersects, returns how many vectors away
	// the object is returns -1 if no intersect
	public abstract double findIntersection(Ray ray);

	public abstract Vector getNormal(Vector location);

	public abstract Color getColor(Vector pt);

	public void setReflectiveness(double reflectiveness) {
		this.reflectiveness = reflectiveness;
	}

	public double getReflectiveness() {
		return this.reflectiveness;
	}

	public double getTransparency() {
		return transparency;
	}

	public void setTransparency(double transparency) {
		this.transparency = transparency;
	}
	
}
