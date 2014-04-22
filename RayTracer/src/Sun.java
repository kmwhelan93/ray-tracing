public class Sun extends Light {
	private Vector direction;

	@Override
	public Vector getDirection(Vector objectLocation) {
		// TODO Auto-generated method stub
		Vector scaled = direction.normalize();
		return scaled.scale(100000); // scaled by 100000 so we can treat bulbs
										// and sunlight the same: sun is just a
										// bulb that is really far away
	}
	
	public Sun(int id, Vector direction, Color color) {
		this.id = id;
		this.direction = direction;
		this.color = color;
	}
	
	

}
