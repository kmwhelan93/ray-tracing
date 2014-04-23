public abstract class Obstacle extends Moveable{
	// KW: made an abstract class so that it can extend Moveable
	
	
	// If intersects, returns how many vectors away
	// the object is returns -1 if no intersect
	public abstract double findIntersection(Ray ray); 
	public abstract Vector getNormal(Vector location);
	public abstract Color getColor(Vector pt);
}
