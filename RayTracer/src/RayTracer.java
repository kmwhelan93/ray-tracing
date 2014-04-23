import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class RayTracer {
	private static int width;
	private static int height;
	private static BufferedImage b;
	private static WritableRaster r;
	private static int maxSampleRayBounces = 10;// Number of times a sample ray
												// is allowed to bounce in
												// scene; can be tweaked
	// TODO change to real number; set to one for debugging...
	private static int numSampleRays = 1;// Number of sample rays shot into
											// scene after initial intersection;
	private static Color testColor = new Color(0, 0, 0, 255);
	private static int framesNum;
	private static Moveable eye = new Eye(new Vector(0, 0, 0));
	private static Vector forward = new Vector(0, 0, -1);
	private static Vector right = new Vector(1, 0, 0);
	private static Vector up = new Vector(0, 1, 0);
	// private static ArrayList<Light> lights = new ArrayList<Light>();
	private static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private static ArrayList<Moveable> lights = new ArrayList<Moveable>();
	private static ArrayList<Moveable> spheres = new ArrayList<Moveable>();
	private static ArrayList<Plane> planes = new ArrayList<Plane>();

	public static void main(String[] args) throws Exception {

		Scanner scan = new Scanner(new File("test.txt"));
		String filename = "";

		while (scan.hasNextLine()) {
			// Scanner temp = new Scanner(scan.nextLine().trim());
			// if (temp.hasNext()) {
			// String command = temp.next();
			// if (command.equals("png")) {
			// // png
			// width = temp.nextInt();
			// height = temp.nextInt();
			// framesNum = temp.nextInt();
			// filename = temp.nextLine().trim();
			// b = new BufferedImage(width, height,
			// BufferedImage.TYPE_4BYTE_ABGR);
			// r = b.getRaster();
			// } else if (command.equals("eye")) {
			// eye = new Vector(temp.nextDouble(), temp.nextDouble(),
			// temp.nextDouble());
			// } else if (command.equals("forward")) {
			// // do not normalize
			// forward = new Vector(temp.nextDouble(), temp.nextDouble(),
			// temp.nextDouble());
			// // recompute right and up vectors
			// right = forward.crossProduct(up).normalize();
			// up = right.crossProduct(forward).normalize();
			// } else if (command.equals("up")) {
			// double x = temp.nextDouble();
			// double y = temp.nextDouble();
			// double z = temp.nextDouble();
			// Vector v = new Vector(x, y, z);
			// right = forward.crossProduct(v).normalize();
			// up = right.crossProduct(forward).normalize();
			// } else if (command.equals("sun")) {
			// double x = temp.nextDouble();
			// double y = temp.nextDouble();
			// double z = temp.nextDouble();
			// Vector v = new Vector(x, y, z).normalize();
			// Color color = new Color(temp.nextDouble(),
			// temp.nextDouble(), temp.nextDouble());
			// int id = temp.nextInt();
			// Sun sun = new Sun(id, v, color);
			// while (id >= lights.size()) {
			// lights.add(null);
			// }
			// lights.set(id, sun);
			// } else if (command.equals("bulb")) {
			// double x = temp.nextDouble();
			// double y = temp.nextDouble();
			// double z = temp.nextDouble();
			// Color color = new Color(temp.nextDouble(),
			// temp.nextDouble(), temp.nextDouble());
			// int id = temp.nextInt();
			// Vector location = new Vector(x, y, z);
			// Bulb bulb = new Bulb(id, location, color);
			// while (id >= lights.size()) {
			// lights.add(null);
			// }
			// lights.set(id, bulb);
			// } else if (command.equals("sphere")) {
			// Vector v = new Vector(temp.nextDouble(), temp.nextDouble(),
			// temp.nextDouble());
			// double radius = temp.nextDouble();
			// Color color = new Color(temp.nextDouble(),
			// temp.nextDouble(), temp.nextDouble());
			// int id = temp.nextInt();
			// Sphere s = new Sphere(v, radius, color);
			// s.setId(id);
			// // if (id >= spheres.size()) {
			// // spheres.add(new ArrayList<Sphere>());
			// // }
			// obstacles.add(s);
			// } else if (command.equals("plane")) {
			// double A = temp.nextDouble();
			// double B = temp.nextDouble();
			// double C = temp.nextDouble();
			// double D = temp.nextDouble();
			// Color color = new Color(temp.nextDouble(),
			// temp.nextDouble(), temp.nextDouble());
			// Plane plane = new Plane(A, B, C, D, color);
			// obstacles.add(plane);
			// } else if (command.equals("planeT")) {
			// //planeT id filename
			// int id = temp.nextInt();
			// String texture = temp.next();
			// ((Plane)obstacles.get(id)).setTexture(texture);
			// } else if (command.equals("sphereT")) {
			// //sphereT id filename
			// int id = temp.nextInt();
			// String texture = temp.next();
			// ((Sphere)obstacles.get(id)).setTexture(texture);
			// } else if(command.equals("planeB")) {
			// //planeT id filename
			// int id = temp.nextInt();
			// String bump = temp.next();
			// ((Plane)obstacles.get(id)).setBumpMap(bump);
			// } else if(command.equals("sphereB")) {
			// //sphereB id filename
			// int id = temp.nextInt();
			// String bump = temp.next();
			// ((Sphere)obstacles.get(id)).setBumpMap(bump);
			String[] line = scan.nextLine().trim().split(" ");
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
						Double.parseDouble(line[3]), Integer.parseInt(line[4])));
				e.frameNumber = Integer.parseInt(line[4]);
				eye.addCheckpoint(e);
				eye.frameNumber = Integer.parseInt(line[4]);
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
			else if (command.equals("sphere")) {
				Vector v = new Vector(Double.parseDouble(line[1]),
						Double.parseDouble(line[2]),
						Double.parseDouble(line[3]));
				double radius = Double.parseDouble(line[4]);
				Color color = new Color(Double.parseDouble(line[5]),
						Double.parseDouble(line[6]),
						Double.parseDouble(line[7]));
				int id = Integer.parseInt(line[8]);
				Sphere s = new Sphere(v, radius, color);
				s.setId(id);
				s.frameNumber = Integer.parseInt(line[9]);
				if (spheres.size() <= id) {
					spheres.add(s);
				} else {
					spheres.get(id).addCheckpoint(s);
				}
			} else if (command.equals("plane")) {
				double A = Double.parseDouble(line[1]);
				double B = Double.parseDouble(line[2]);
				double C = Double.parseDouble(line[3]);
				double D = Double.parseDouble(line[4]);
				Color color = new Color(Double.parseDouble(line[5]),
						Double.parseDouble(line[6]),
						Double.parseDouble(line[7]));
				Plane plane = new Plane(A, B, C, D, color);
				planes.add(plane);
			} else if (command.equals("planeT")) {
				// planeT id filename
				int id = Integer.parseInt(line[1]);
				String texture = line[2];
				planes.get(id).setTexture(texture);
			} else if (command.equals("sphereT")) {
				// sphereT id filename
//				int id = Integer.parseInt(line[1]);
//				String texture = line[2];
				// spheres.get(id).setTexture(texture);
			}
		}

		for (int i = 0; i < framesNum; i++) {

			obstacles.clear();
			obstacles.addAll(planes);
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
					double closest = Double.POSITIVE_INFINITY;
					Vector closestNormal = null;
					Color closestColor = null;
					Object closestObject = null;
					Vector closestLocation = null;
					// find intersections with spheres

					for (Moveable m : spheres) {
						if (m.moveableOverFrames.size() > 0) {
							Sphere sphere = (Sphere) m.getState(i);
							double intersect = sphere.findIntersection(ray);
							// double intersect = RayTracer.RayIntersectSphere(
							// ray, sphere);
							if (intersect >= 0 && intersect < closest) {
								closest = intersect;
								Vector location = ray.scale(intersect);
								closestNormal = sphere.getNormal(location)
										.normalize();
								closestColor = sphere.getColor(ray.getOrigin()
										.add(ray.getDirection()
												.scale(intersect)));
								closestObject = sphere;
								closestLocation = location;
								if(i == sphere.frameNumber){
								obstacles.add(sphere);}
							}
						}
					}

					for (Obstacle obstacle : obstacles) {
						double intersect = obstacle.findIntersection(ray);
						if (intersect >= 0 && intersect < closest) {
							closest = intersect;
							Vector location = ray.scale(intersect);
							closestNormal = obstacle.getNormal(location)
									.normalize();
							closestColor = obstacle.getColor(ray.getOrigin()
									.add(ray.getDirection().scale(intersect)));
							closestObject = obstacle;
							closestLocation = location;
						}
					}
					if (closestObject != null) {
						// Global illumination algorithm starts here
						int numFactoredSampleRays = numSampleRays;
						Color sumColor = new Color(0, 0, 0, 255);
						Color toColor = new Color(0, 0, 0, 255);
						for (int a = 0; a < numSampleRays; a++) {
//							Ray currentSampleRay = RayTracer
//									.generateRandomRay(closestLocation);
//							int currentSampleNumBounces = 0;
							Color gFactor = new Color(1, 1, 1, 255);
							// FIXME
							// Color gFactor =
							// globalFactor(currentSampleRay,
							// currentSampleNumBounces, i);
							if (gFactor.equals(testColor)) {
								numFactoredSampleRays -= 1;
								continue;
							}
							// might invert Color
							boolean inverted = false;
							if (closestNormal.dotProduct(eye.getState(i)
									.getVector().subtract(closestLocation)) < 0) {
								closestColor = closestColor.invert();
								inverted = true;
							}
							// apply lighting
							// add method that takes in light vector and
							// make a
							// light
							// interface
							for (Moveable m : lights) {
								if (m.moveableOverFrames.size() > 0) {
									Light light = (Light) m.getState(i);
									if (!RayTracer.isLightBlocked(light,
											closestLocation, closestObject, i)) {
										double nDotI = closestNormal
												.normalize()
												.dotProduct(
														light.getDirection(
																closestLocation)
																.normalize());
										if ((nDotI > 0 && !inverted)
												|| (inverted && nDotI < 0)) {
											toColor = toColor.add(closestColor
													.multiplyColors(
															light.getColor())
													.multiply(nDotI));
										}

									}
								}
							}

							toColor.multiplyColors(gFactor);
							sumColor.add(toColor);
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

	public static boolean isLightBlocked(Light light, Vector objectLocation,
			Object objectToColor, int frame) {
		Vector directionToLight = light.getDirection(objectLocation);
		Ray rayToLight = new Ray(objectLocation, directionToLight);
		for (Obstacle obstacle : obstacles) {
			double t = obstacle.findIntersection(rayToLight);
			if (obstacle != objectToColor && t > 0 && t < 1) {
				// for (Moveable m : spheres) {
				// Sphere sphere = (Sphere) m.getState(frame);
				// // double t = RayTracer.RayIntersectSphere(rayToLight,
				// sphere);
				// double t = sphere.findIntersection(rayToLight);
				// if (sphere != objectToColor && t > 0 && t < 1) {
				// return true;
				// }
				// }
				// for (Plane plane : RayTracer.planes) {
				// // double t = RayTracer.RayIntersectPlane(rayToLight, plane);
				// double t = plane.findIntersection(rayToLight);
				// if (plane != objectToColor && t > 0 && t < 1) {
				return true;
			}
		}
		return false;
	}

	// I (Stephen) just cobbled this method together...how should we
	// structure our code?
	public static Vector findIntersection(Ray ray, int frame) {
		obstacles = new ArrayList<Obstacle>();
		double closest = Double.POSITIVE_INFINITY;
		Vector closestNormal = null;
		Color closestColor = null;
		Object closestObject = null;
		Vector closestLocation = new Vector();
		closestLocation.setLight(false);
		// find intersections with spheres
		for (Obstacle obstacle : obstacles) {
			double intersect = obstacle.findIntersection(ray);
			if (intersect >= 0 && intersect < closest) {
				closest = intersect;
				Vector location = ray.scale(intersect);
				closestNormal = obstacle.getNormal(location).normalize();
				closestColor = obstacle.getColor(ray.getOrigin().add(
						ray.getDirection().scale(intersect)));
				closestObject = obstacle;
//				 for (Moveable m : spheres) {
//				 Sphere sphere = (Sphere) m.getState(frame);
//				 // double intersect = RayTracer.RayIntersectSphere(ray,
////				 sphere);
//				 double intersect = sphere.findIntersection(ray);
//				 if (intersect >= 0 && intersect < closest) {
//				 closest = intersect;
//				 Vector location = ray.scale(intersect);
//				 closestNormal = sphere.getNormal(location).normalize();
//				 closestColor = sphere.getColor(ray.getOrigin().add(
//				 ray.getDirection().scale(intersect)));
//				 closestObject = sphere;
				
				 closestLocation = location;
				 closestLocation.setClosestNormal(closestNormal);
				 closestLocation.setClosestObject(closestObject);
				 closestLocation.setLight(false);
				 closestLocation.setColor(closestColor);
				 }
				 }

				// find intersections with planes
				 for (Plane plane : planes) {
				 double intersect = plane.findIntersection(ray);
				 // double intersect = RayTracer.RayIntersectPlane(ray,
//				 plane);
				 if (intersect >= 0 && intersect < closest) {
				 closest = intersect;
//				  closestNormal = plane.getNormal().normalize();
//				  closestColor = plane.getColor();
				 closestObject = plane;
				 closestLocation = ray.scale(intersect);
				 closestLocation.setClosestNormal(closestNormal);
				 closestLocation.setClosestObject(closestObject);
				 closestLocation.setLight(false);
				 closestLocation.setColor(closestColor);
			}
		}

		return closestLocation;
	}

	// Cobbled this method too...see above method comment
	public static Color diffuseLightCalc(int i, Vector closestNormal,
			Color closestColor, Object closestObject, Vector closestLocation,
			int frame) {
		Color toColor = new Color(0, 0, 0, 255);
		// might invert Color
		boolean inverted = false;
		if (closestNormal.dotProduct(eye.getState(frame).getVector()
				.subtract(closestLocation)) < 0) {
			closestColor = closestColor.invert();
			inverted = true;
		}
		// apply lighting
		// add method that takes in light vector and make a
		// light
		// interface
		for (Moveable m : lights) {
			Light light = (Light) m.getState(frame);
			if (!RayTracer.isLightBlocked(light, closestLocation,
					closestObject, i)) {
				double nDotI = closestNormal.normalize().dotProduct(
						light.getDirection(closestLocation).normalize());
				if ((nDotI > 0 && !inverted) || (inverted && nDotI < 0)) {
					toColor = toColor.add(closestColor.multiplyColors(
							light.getColor()).multiply(nDotI));
				}

			}
		}
		return toColor;
	}

	public static Ray generateRandomRay(Vector location) {
		double theta = Math.toRadians(Math.random() * 90);
		double phi = Math.toRadians(Math.random() * 360);
		double x = Math.toDegrees(Math.sin(phi) * Math.cos(theta));
		double y = Math.toDegrees(Math.sin(phi) * Math.sin(theta));
		double z = Math.toDegrees(Math.cos(phi));
		Ray random = new Ray(location, new Vector(x, y, z));
		return random;
	}

	// Method to compute "sample ray factor" of global lighting calculation
	public static Color globalFactor(Ray sampleRay, int numBounces, int frame) {
		numBounces++;
		if (numBounces == maxSampleRayBounces)
			return testColor;
		Vector intersection = findIntersection(sampleRay, frame);
		if (intersection.getLight())
			return intersection.getColor();
		sampleRay = generateRandomRay(intersection);
		Color gFactor = globalFactor(sampleRay, numBounces, frame);// recursively
																	// shoot
																	// rays into
																	// scene
		Color diffuse = diffuseLightCalc(frame,
				intersection.getClosestNormal(), intersection.getColor(),
				intersection.getClosestObject(), intersection, frame);
		gFactor.multiplyColors(diffuse);
		return gFactor;
	}
}
