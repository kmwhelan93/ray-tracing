
public class BusStop {
	
	private int startFrame;
	private int endFrame;
	private Vector vector;
	private int id;
	private Color color;
	
	public BusStop(int startFrame, int endFrame, double x, double y, double z){
		this.startFrame = startFrame;
		this.endFrame = endFrame;
		this.vector = new Vector(x, y, z);
	}	
	public BusStop(int startFrame, int endFrame, double x, double y, double z, int id){
		this.startFrame = startFrame;
		this.endFrame = endFrame;
		this.vector = new Vector(x, y, z);
		this.id = id;
	}
	public BusStop(int startFrame, int endFrame, double x, double y, double z, double r, double g, double b, int id){
		this.startFrame = startFrame;
		this.endFrame = endFrame;
		this.vector = new Vector(x, y, z);
		this.color = new Color(r,g,b);
		this.id = id;
	}

	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getStartFrame() {
		return startFrame;
	}

	public void setStartFrame(int startFrame) {
		this.startFrame = startFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}

	public Vector getVector() {
		return vector;
	}

	public void setVector(Vector vector) {
		this.vector = vector;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	

}
