public interface Obstacle {
	// If intersects, returns how many vectors away
	// the object is returns -1 if no intersect
	public abstract double findIntersection(Ray ray); 
	public abstract Vector getNormal(Vector location);
	public abstract Color getColor(Vector pt);
}
