import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class RayTracer {
	private static int width;
	private static int height;
	private static BufferedImage b;
	private static WritableRaster r;
	private static int maxSampleRayBounces = 1;// Number of times a sample ray
												// is allowed to bounce in
												// scene; can be tweaked
	private static int numSampleRays = 10;// Number of sample rays shot into
											// scene after initial intersection;
	private static Color zeroColor = new Color(0, 0, 0, 255);
	private static int framesNum;
	private static Moveable eye = new Eye(new Vector(2, 0, 0));
	private static Vector forward = new Vector(-1, 0, 0);
	private static Vector right = new Vector(0, 1, 0);
	private static Vector up = new Vector(0, 0, 1);
	// private static ArrayList<Light> lights = new ArrayList<Light>();
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Light> lights = new ArrayList<Light>();
	private static ArrayList<Vector> vertices = new ArrayList<Vector>();
	

	public static void main(String[] args) throws Exception {
		
		
		Scanner scan = new Scanner(new File("test.txt"));
		String filename = "";

		while (scan.hasNextLine()) {
			String nextLine = scan.nextLine();
			// For what good did you remove the scanner??????
			Scanner lineReader = new Scanner(nextLine);
			if (lineReader.hasNext()) {
				lineReader.next();
				String[] line = nextLine.trim().split(" ");
				String command = line[0];
				if (command.equals("png")) {
					// png
					width = Integer.parseInt(line[1]);
					height = Integer.parseInt(line[2]);
					framesNum = Integer.parseInt(line[3]);
					filename = line[4].trim();
					b = new BufferedImage(width, height,
							BufferedImage.TYPE_4BYTE_ABGR);
					r = b.getRaster();
				} else if (command.equals("eye")) {
					Eye e = new Eye(new Vector(Double.parseDouble(line[1]),
							Double.parseDouble(line[2]),
							Double.parseDouble(line[3])));
					e.frameNumber = Integer.parseInt(line[4]);
					eye.addCheckpoint(e);
				} else if (command.equals("forward")) {
					// do not normalize
					forward = new Vector(Double.parseDouble(line[1]),
							Double.parseDouble(line[2]),
							Double.parseDouble(line[3]));
					// recompute right and up vectors
					right = forward.crossProduct(up).normalize();
					up = right.crossProduct(forward).normalize();
				} else if (command.equals("up")) {
					double x = Double.parseDouble(line[1]);
					double y = Double.parseDouble(line[2]);
					double z = Double.parseDouble(line[3]);
					Vector v = new Vector(x, y, z);
					right = forward.crossProduct(v).normalize();
					up = right.crossProduct(forward).normalize();
				} else if (command.equals("sun")) {
					double x = Double.parseDouble(line[1]);
					double y = Double.parseDouble(line[2]);
					double z = Double.parseDouble(line[3]);
					Vector v = new Vector(x, y, z).normalize();
					Color color = new Color(Double.parseDouble(line[4]),
							Double.parseDouble(line[5]),
							Double.parseDouble(line[6]));
					int id = Integer.parseInt(line[7]);
					Sun sun = new Sun(id, v, color);
					sun.frameNumber = Integer.parseInt(line[8]);
					if (lights.size() <= id) {
						lights.add(sun);
					} else {
						lights.get(id).addCheckpoint(sun);
					}
				} else if (command.equals("bulb")) {
					double x = Double.parseDouble(line[1]);
					double y = Double.parseDouble(line[2]);
					double z = Double.parseDouble(line[3]);
					Color color = new Color(Double.parseDouble(line[4]),
							Double.parseDouble(line[5]),
							Double.parseDouble(line[6]));
					int id = Integer.parseInt(line[7]);
					Vector location = new Vector(x, y, z);
					Bulb bulb = new Bulb(id, location, color);
					bulb.frameNumber = Integer.parseInt(line[8]);
					if (lights.size() <= id) {
						lights.add(bulb);
					} else {
						lights.get(id).addCheckpoint(bulb);
					}
				}
				// temp.close();
				// }
				else if (command.equals("sphere") || command.equals("sphereB")
						|| command.equals("sphereT")) {
					Vector v = new Vector(Double.parseDouble(line[1]),
							Double.parseDouble(line[2]),
							Double.parseDouble(line[3]));
					double radius = Double.parseDouble(line[4]);
					Color color = new Color(Double.parseDouble(line[5]),
							Double.parseDouble(line[6]),
							Double.parseDouble(line[7]));
					int id = Integer.parseInt(line[8]);
					Sphere s = new Sphere(v, radius, color,
							Double.parseDouble(line[10]),
							Double.parseDouble(line[11]));
					s.setId(id);
					s.frameNumber = Integer.parseInt(line[9]);
					if (command.equals("sphereB")) {
						s.setBumpMap(line[12]);
					} else if (command.equals("sphereT")) {
						s.setTexture(line[12]);
					}
					// to see if this is the first sphere of this id
					// the rest are the same sphere at different states
					if (obstacles.size() <= id) {
						obstacles.add(s);
					} else {
						obstacles.get(id).addCheckpoint(s);
					}
				} else if (command.equals("plane") || command.equals("planeB")
						|| command.equals("planeT")) {
					int id = Integer.parseInt(line[1]);
					double A = Double.parseDouble(line[2]);
					double B = Double.parseDouble(line[3]);
					double C = Double.parseDouble(line[4]);
					double D = Double.parseDouble(line[5]);
					Color color = new Color(Double.parseDouble(line[6]),
							Double.parseDouble(line[7]),
							Double.parseDouble(line[8]));
					double reflectiveness = Double.parseDouble(line[9]);
					double transparency = Double.parseDouble(line[10]);
					Plane plane = new Plane(id, A, B, C, D, color,
							reflectiveness, transparency);
					if (command.equals("planeB")) {
						plane.setBumpMap(line[8]);
					} else if (command.equals("planeT")) {
						plane.setTexture(line[8]);
					}
					obstacles.add(plane);
				} else if (command.equals("vertex")) {
					double x = lineReader.nextDouble();
					double y = lineReader.nextDouble();
					double z = lineReader.nextDouble();
					double r = lineReader.nextDouble();
					double g = lineReader.nextDouble();
					double b = lineReader.nextDouble();
					Color color = new Color(r, g, b);
					RayTracer.vertices.add(new Vector(x, y, z, color));
				} else if (command.equals("triangle")) {
					int id;
					Vector p1;
					Vector p2;
					Vector p3;
					double reflectiveness;
					double transparency;
					if (!nextLine.contains(":")) {
						id = lineReader.nextInt();
						p1 = RayTracer.getVertex(lineReader.nextInt());
						p2 = RayTracer.getVertex(lineReader.nextInt());
						p3 = RayTracer.getVertex(lineReader.nextInt());
						reflectiveness = lineReader.nextDouble();
						transparency = lineReader.nextDouble();
					} else {
						HashMap<String, Double> map = RayTracer
								.hashLine(lineReader);
						id = map.get("id").intValue();
						p1 = RayTracer.getVertex(map.get("p1").intValue());
						p2 = RayTracer.getVertex(map.get("p2").intValue());
						p3 = RayTracer.getVertex(map.get("p3").intValue());
						reflectiveness = map.get("reflectiveness");
						transparency = map.get("transparency");

					}
					Triangle triangle = new Triangle(id, p1, p2, p3,
							reflectiveness, transparency);
					RayTracer.obstacles.add(triangle);
				} else if (command.equals("cylinder")) {

					int id;
					Color color;
					double reflectiveness;
					double transparency;
					double degrees;
					double axisx;
					double axisy;
					double axisz;
					if (!nextLine.contains(":")) {
						id = lineReader.nextInt();
						color = new Color(lineReader.nextDouble(), lineReader.nextDouble(), lineReader.nextDouble());
						reflectiveness = lineReader.nextDouble();
						transparency = lineReader.nextDouble();
						degrees = lineReader.nextDouble();
						axisx = lineReader.nextDouble();
						axisy = lineReader.nextDouble();
						axisz = lineReader.nextDouble();
					} else {
						HashMap<String, Double> map = RayTracer
								.hashLine(lineReader);
						id = map.get("id").intValue();
						color = new Color(map.get("r"), map.get("g"), map.get("b"));
						reflectiveness = map.get("reflectiveness");
						transparency = map.get("transparency");
						degrees = map.get("degrees");
						axisx = map.get("axisx");
						axisy = map.get("axisy");
						axisz = map.get("axisz");
					}
					Vector rotateAxis = new Vector(axisx, axisy, axisz);
					Matrix rotate = RayTracer.rotate(degrees, rotateAxis);
					
					Cylinder cylinder = new Cylinder(id, 1, color, rotate,
							reflectiveness, transparency);
					RayTracer.obstacles.add(cylinder);
				
				}
			}
		}

		for (int i = 0; i < framesNum; i++) {
			Color clearColor = new Color(0, 0, 0, 0);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					r.setPixel(x, y, clearColor.getColorArray());
				}
			}

			// draw image, given everything provided
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					double s = (2 * col - width)
							/ (double) Math.max(width, height);
					double t = (height - 2 * row)
							/ (double) Math.max(width, height);
					Vector direction = forward.add(right.scale(s)).add(
							up.scale(t));
					Ray ray = new Ray(eye.getState(i).getVector(), new Vector(
							direction));

					CollisionPoint collisionPoint = RayTracer
							.findClosestIntersection(ray, i, null);

					if (collisionPoint != null) {
						// Global illumination algorithm starts here
						int numFactoredSampleRays = numSampleRays;
						Color sumColor = new Color(0, 0, 0, 255);
						Color toColor = new Color(0, 0, 0, 255);
						toColor = RayTracer.diffuseLightCalc(collisionPoint, i,
								0);
						for (int a = 0; a < numSampleRays; a++) {
							Ray currentSampleRay = RayTracer
									.generateRandomRay(collisionPoint
											.getLocation());
							int currentSampleNumBounces = 0;
							// Color gFactor = new Color(1, 1, 1, 255);
							// FIXME
							Color gFactor = globalFactor(currentSampleRay,
									currentSampleNumBounces, i);
							if (gFactor == null) {

							} else {
								if (gFactor.equals(zeroColor)) {
									numFactoredSampleRays -= 1;
									continue;
								}
								toColor.multiplyColors(gFactor);
								sumColor.add(toColor);
							}

						}
						sumColor.divide(numFactoredSampleRays);
						r.setPixel(col, row, toColor.getColorArray());
					}
				}

			}

			scan.close();
			String newFilename = "generatedimgs/"
					+ filename.replace(".png", (i + 1) + ".png");
			System.out.println("Drawing " + newFilename.substring(14) + "...");

			ImageIO.write(b, "png", new File(newFilename));
		}
	}

	public static double RayIntersectVertex(Ray ray, Vertex lightSource) {
		Vector lightSourceVector = new Vector(lightSource, false);
		Vector test = ray.getOrigin().subtract(lightSourceVector)
				.divide(ray.getDirection());
		if (test.get(0) == test.get(1) && test.get(0) == test.get(2)
				&& test.get(1) == test.get(2))
			return test.get(0);
		return Double.POSITIVE_INFINITY;
	}

	// returns 0 to 1 where 0 is not blocked and 1 is entirely blocked
	public static double amountLightBlocked(Light light, Vector objectLocation,
			Object objectToColor, int frame) {
		Vector directionToLight = light.getDirection(objectLocation);
		Ray rayToLight = new Ray(objectLocation, directionToLight);
		double totalBlockage = 0;
		for (Obstacle obstacle : obstacles) {
			double t = ((Obstacle) obstacle.getState(frame))
					.findIntersection(rayToLight);
			if (obstacle != objectToColor && t > 0 && t < 1) {
				totalBlockage += 1 - obstacle.transparency;
			}
		}
		return totalBlockage;
	}

	// KW: Fixed this method
	public static CollisionPoint findClosestIntersection(Ray ray, int frame,
			Obstacle pastObstacle) {
		double closest = Double.POSITIVE_INFINITY;
		CollisionPoint collisionPoint = null;
		for (Obstacle obstacle : obstacles) {
			obstacle = (Obstacle) obstacle.getState(frame);
			double intersect = obstacle.findIntersection(ray);
			if (intersect >= 0 && intersect < closest
					&& !obstacle.equals(pastObstacle)) {
				closest = intersect;
				Vector location = ray.scale(intersect);
				collisionPoint = new CollisionPoint(obstacle, location,
						ray.getDirection());
			}
		}
		return collisionPoint;
	}

	// THIS METHOD SHOULD DO ALL LIGHTING CALCULATIONS
	// IE IT IS RECURSIVE AND WILL EVENTUALLY RETURN THE COLOR OF A SINGLE
	// COLLISIONPOINT
	public static Color diffuseLightCalc(CollisionPoint collisionPoint,
			int frame, int depth) {
		Color toColor = new Color(0, 0, 0, 255);
		// might invert Color
		boolean inverted = false;
		Vector normal = collisionPoint.getObstacle().getNormal(
				collisionPoint.getLocation());
		Color closestColor = collisionPoint.getObstacle().getColor(
				collisionPoint.getLocation());
		if (normal.dotProduct(eye.getState(frame).getVector()
				.subtract(collisionPoint.getLocation())) < 0) {
			closestColor = closestColor.invert();

			inverted = true;
		}
		// apply lighting, use light interface...
		for (Moveable m : lights) {
			Light light = (Light) m.getState(frame);
			double amountLightBlocked = RayTracer.amountLightBlocked(light,
					collisionPoint.getLocation(), collisionPoint.getObstacle(),
					frame);
			if (amountLightBlocked < 1) {
				double nDotI = normal.normalize().dotProduct(
						light.getDirection(collisionPoint.getLocation())
								.normalize());
				if ((nDotI > 0 && !inverted) || (inverted && nDotI < 0)) {
					toColor = toColor.add(closestColor.multiplyColors(
							light.getColor()).multiply(nDotI).multiply(1 - amountLightBlocked));
				}
			}
		}
		// return combination of diffuse color here plus color from reflection
		Color finalColor = toColor.multiply(1
				- collisionPoint.getObstacle().getReflectiveness()
				- collisionPoint.getObstacle().getTransparency());
		if (collisionPoint.getObstacle().getReflectiveness() > 0 && depth < 40) {
			Vector reflected = RayTracer.getReflectionVector(
					collisionPoint.getIncomingSightVector(), normal);
			Ray newSight = new Ray(collisionPoint.getLocation(), reflected);
			CollisionPoint nextIntersection = RayTracer
					.findClosestIntersection(newSight, frame,
							collisionPoint.getObstacle());

			if (nextIntersection != null) {
				Color reflectedColor = diffuseLightCalc(nextIntersection,
						frame, depth + 1);
				finalColor = finalColor.add(reflectedColor
						.multiply(collisionPoint.getObstacle().reflectiveness));
			}
		}
		if (collisionPoint.getObstacle().getTransparency() > 0 && depth < 40) {
			Ray transparencyRay = new Ray(collisionPoint.getLocation(),
					collisionPoint.getIncomingSightVector());
			CollisionPoint nextIntersection = RayTracer
					.findClosestIntersection(transparencyRay, frame,
							collisionPoint.getObstacle());
			if (nextIntersection != null) {
				Color transparentColor = diffuseLightCalc(nextIntersection,
						frame, depth + 1);
				finalColor = finalColor.add(transparentColor.multiply(
						collisionPoint.getObstacle().getTransparency()));
			}
		}
		return finalColor;
	}

	public static Ray generateRandomRay(Vector location) {
		double theta = Math.toRadians(Math.random() * 90);
		double phi = Math.toRadians(Math.random() * 360);
		double x = Math.toDegrees(Math.sin(phi) * Math.cos(theta));
		double y = Math.toDegrees(Math.sin(phi) * Math.sin(theta));
		double z = Math.toDegrees(Math.cos(phi));
		Ray random = new Ray(location, new Vector(x, y, z, 1));
		return random;
	}

	// Method to compute "sample ray factor" of global lighting calculation
	public static Color globalFactor(Ray sampleRay, int numBounces, int frame) {
		numBounces++;
		if (numBounces == maxSampleRayBounces)
			return zeroColor;
		if (sampleRay.getOrigin().size() == 0)
			return zeroColor;
		CollisionPoint collisionPoint = findClosestIntersection(sampleRay,
				frame, null);
		if (collisionPoint == null) {
			return zeroColor;
		} else {
			// KW: removed checking if it was light -- it will never be light!
			Color diffuse = diffuseLightCalc(collisionPoint, frame, 0);
			sampleRay = generateRandomRay(collisionPoint.getLocation());
			Color gFactor = globalFactor(sampleRay, numBounces, frame);// recursively
																		// shoot
																		// rays
																		// into
																		// scene
			gFactor.multiplyColors(diffuse);
			return gFactor;
		}
	}

	public static Vector getReflectionVector(Vector incidentVector,
			Vector normal) {
		normal = normal.normalize();
		return incidentVector.subtract(normal.scale(2).scale(
				incidentVector.dotProduct(normal)));
	}

	public static Vector getVertex(int index) {
		if (index >= 0) {
			return RayTracer.vertices.get(index);
		}
		return new Vector(RayTracer.vertices.get(RayTracer.vertices.size()
				+ index));
	}

	public static HashMap<String, Double> hashLine(Scanner s) {
		HashMap<String, Double> retVal = new HashMap<String, Double>();
		while (s.hasNext()) {
			String[] nextKeyValuePair = s.next().split(":");
			retVal.put(nextKeyValuePair[0],
					Double.parseDouble(nextKeyValuePair[1]));
		}
		return retVal;
	}
	
	public static double convertToRadians(double degrees) {
		return (degrees / 180) * Math.PI;
	}
	
	public static Matrix rotate(double degrees, Vector axis) {
		axis = axis.normalize();
		double radians = RayTracer.convertToRadians(degrees);
		double c = Math.cos(radians);
		double s = Math.sin(radians);
		double x = axis.get(0);
		double y = axis.get(1);
		double z = axis.get(2);
		double[][] rotate = {
				{ x * x * (1 - c) + c, x * y * (1 - c) - z * s,
						x * z * (1 - c) + y * s},
				{ y * x * (1 - c) + z * s, y * y * (1 - c) + c,
						y * z * (1 - c) - x * s},
				{ x * z * (1 - c) - y * s, y * z * (1 - c) + x * s,
						z * z * (1 - c) + c}};
		return new Matrix(rotate);
	}
	
	public static Matrix inverse(Matrix m) {
		// see http://www.mathsisfun.com/algebra/matrix-inverse-minors-cofactors-adjugate.html
		Matrix intermediate = new Matrix(m);
		// matrix of minors
		// saving these for later
		double det1 = 0;
		double det2 = 0;
		double det3 = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				ArrayList<Integer> rows = new ArrayList<Integer>();
				rows.add(0);
				rows.add(1);
				rows.add(2);
				rows.remove(new Integer(i));
				ArrayList<Integer> cols = new ArrayList<Integer>();
				cols.add(0);
				cols.add(1);
				cols.add(2);
				cols.remove(new Integer(j));
				double result = m.matrix[rows.get(0)][cols.get(0)] * 
						m.matrix[rows.get(1)][cols.get(1)] -
						m.matrix[rows.get(0)][cols.get(1)]*m.matrix[rows.get(1)][cols.get(0)];
				intermediate.matrix[i][j] = result;
				if (i == 0) {
					if (j == 0)
						det1 = result;
					if (j == 1)
						det2 = result;
					if (j == 2)
						det3 = result;
				}
			}
		}
		// matrix of cofactors
		int count = 0;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (count % 2 != 0) {
					intermediate.matrix[row][col] = -1*intermediate.matrix[row][col];
				}
				count++;
			}
		}
		// adjugate (also called adjoint)
		intermediate = intermediate.transpose();
		
		// multiply by 1/Determinant
		double det = m.get(0, 0) * det1
				- m.get(0, 1) * det2
				+ m.get(0, 2) * det3;
		intermediate = intermediate.scale(1/det);
		return intermediate;
	}
}
