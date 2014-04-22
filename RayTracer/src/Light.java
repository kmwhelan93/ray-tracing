
public abstract class Light {
	protected Color color;
	protected int id;
	public abstract Vector getDirection(Vector objectLocation);
	public Color getColor() {
		return this.color;
	}

}
