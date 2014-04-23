
public abstract class Light extends Moveable{
	protected Color color;
	protected int id;
	public abstract Vector getDirection(Vector objectLocation);
	public Color getColor() {
		return this.color;
	}
	public abstract Vector getVector();

}
