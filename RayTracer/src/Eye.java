import java.util.ArrayList;


public class Eye extends Moveable{
	
	Vector vector;

	
	public Eye (Vector vector){
		super();
		this.vector = vector;
	}


	@Override
	public Vector getVector() {
return this.vector;
	}


	@Override
	public Color getColor() {
		return new Color(0,0,0,0);
	}


	@Override
	public double getRadius() {
		return -1;
	}


}
