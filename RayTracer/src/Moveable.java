import java.util.ArrayList;

public abstract class Moveable {

	// ALSO: note that I (KW) made Planes Moveable. This is so all obstacles can be treated
	// the same way. Since Planes have an empty moveableOverFrames, this shouldn't be a problem.
	ArrayList<Moveable> moveableOverFrames;
	int frameNumber;
	int id;

	public Moveable() {
		moveableOverFrames = new ArrayList<Moveable>();
		moveableOverFrames.add(this);
	}

	public Moveable(Moveable m, int frameNumber) {
		moveableOverFrames = new ArrayList<Moveable>();
		moveableOverFrames.add(this);
		moveableOverFrames.add(m);
		this.frameNumber = frameNumber;
	}
	// Note: Triangles and Spheres don't have this
	public abstract Vector getVector();

	// Only Spheres have this
	public abstract double getRadius();

	public void addCheckpoint(Moveable m) {
		moveableOverFrames.add(m);
	}
	
	public abstract Color getColor();

	Moveable getState(int frameNumber) {
		if (moveableOverFrames.size() > 1) {
			for (int i = 0; i < moveableOverFrames.size() - 1; i++) {
				if (frameNumber >= moveableOverFrames.get(i).frameNumber
						&& frameNumber <= moveableOverFrames.get(i + 1).frameNumber) {
					
//					System.out.println("FRAME: " + frameNumber);
//					System.out.println("FIRST: "
//							+ moveableOverFrames.get(i).getVector());
//					System.out.println("SECOND: "
//							+ moveableOverFrames.get(i + 1).getVector());

					double framePoint = (double) frameNumber
							/ ((double) moveableOverFrames.get(i + 1).frameNumber - moveableOverFrames
									.get(i).frameNumber);
					Vector v = new Vector(
							((moveableOverFrames.get(i + 1).getVector().get(0) - moveableOverFrames
									.get(i).getVector().get(0)) * framePoint)
									+ moveableOverFrames.get(i).getVector()
											.get(0),
							((moveableOverFrames.get(i + 1).getVector().get(1) - moveableOverFrames
									.get(i).getVector().get(1)) * framePoint)
									+ moveableOverFrames.get(i).getVector()
											.get(1),
							((moveableOverFrames.get(i + 1).getVector().get(2) - moveableOverFrames
									.get(i).getVector().get(2)) * framePoint)
									+ moveableOverFrames.get(i).getVector()
											.get(2));

//					System.out.println("NEW VECTOR: " + v + "\n");

					double red = ((moveableOverFrames.get(i + 1).getColor()
							.getRed() - moveableOverFrames.get(i).getColor()
							.getRed()) * framePoint)
							+ moveableOverFrames.get(i).getColor().getRed();
					double green = ((moveableOverFrames.get(i + 1).getColor()
							.getGreen() - moveableOverFrames.get(i).getColor()
							.getGreen()) * framePoint)
							+ moveableOverFrames.get(i).getColor().getGreen();
					double blue = ((moveableOverFrames.get(i + 1).getColor()
							.getBlue() - moveableOverFrames.get(i).getColor()
							.getBlue()) * framePoint)
							+ moveableOverFrames.get(i).getColor().getBlue();
					double alpha = ((moveableOverFrames.get(i + 1).getColor()
							.getAlpha() - moveableOverFrames.get(i).getColor()
							.getAlpha()) * framePoint)
							+ moveableOverFrames.get(i).getColor().getAlpha();
					double[] colorArray = { red, green, blue, alpha };
					Color color = new Color(colorArray);

					if (this instanceof Eye) {
						return new Eye(v);
					} else if (this instanceof Sun) {
						return new Sun(this.id, v, color);
					} else if (this instanceof Bulb) {
						return new Bulb(this.id, v, color);
					} else if (this instanceof Sphere) {
						double radius = ((moveableOverFrames.get(i + 1)
								.getRadius() - moveableOverFrames.get(i)
								.getRadius()) * framePoint)
								+ moveableOverFrames.get(i).getRadius();
						return new Sphere(v, radius, color);
					}

				}

			}
		}
		return this;

	}
}
