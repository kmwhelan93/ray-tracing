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
	private static Vector eye = new Vector(0, 0, 0);
	private static ArrayList<BusStop> eyeStops = new ArrayList<BusStop>();
	private static ArrayList<Vector> eyes = new ArrayList<Vector>();
	private static Vector forward = new Vector(0, 0, -1);
	private static Vector right = new Vector(1, 0, 0);
	private static Vector up = new Vector(0, 1, 0);
	private static ArrayList<Light> lights = new ArrayList<Light>();
	private static ArrayList<BusStop> lightStops = new ArrayList<BusStop>();
	private static ArrayList<ArrayList<Sphere>> spheres = new ArrayList<ArrayList<Sphere>>();
	private static ArrayList<BusStop> sphereStops = new ArrayList<BusStop>();
	private static ArrayList<Plane> planes = new ArrayList<Plane>();
	private static ArrayList<BusStop> planeStops = new ArrayList<BusStop>();

	public static void main(String[] args) throws Exception {

		Scanner scan = new Scanner(new File("test1.txt"));
		String filename = "";

		while (scan.hasNextLine()) {
			Scanner temp = new Scanner(scan.nextLine().trim());
			if (temp.hasNext()) {
				String command = temp.next();
				if (command.equals("png")) {
					// png
					width = temp.nextInt();
					height = temp.nextInt();
					framesNum = temp.nextInt();
					filename = temp.nextLine().trim();
					b = new BufferedImage(width, height,
							BufferedImage.TYPE_4BYTE_ABGR);
					r = b.getRaster();
				} else if (command.equals("eye")) {
					eye = new Vector(temp.nextDouble(), temp.nextDouble(),
							temp.nextDouble());
				} else if (command.equals("forward")) {
					// do not normalize
					forward = new Vector(temp.nextDouble(), temp.nextDouble(),
							temp.nextDouble());
					// recompute right and up vectors
					right = forward.crossProduct(up).normalize();
					up = right.crossProduct(forward).normalize();
				} else if (command.equals("up")) {
					double x = temp.nextDouble();
					double y = temp.nextDouble();
					double z = temp.nextDouble();
					Vector v = new Vector(x, y, z);
					right = forward.crossProduct(v).normalize();
					up = right.crossProduct(forward).normalize();
				} else if (command.equals("sun")) {
					double x = temp.nextDouble();
					double y = temp.nextDouble();
					double z = temp.nextDouble();
					Vector v = new Vector(x, y, z).normalize();
					Color color = new Color(temp.nextDouble(),
							temp.nextDouble(), temp.nextDouble());
					int id = temp.nextInt();
					Sun sun = new Sun(id, v, color);
					while (id >= lights.size()) {
						lights.add(null);
					}
					lights.set(id, sun);
				} else if (command.equals("bulb")) {
					double x = temp.nextDouble();
					double y = temp.nextDouble();
					double z = temp.nextDouble();
					Color color = new Color(temp.nextDouble(),
							temp.nextDouble(), temp.nextDouble());
					int id = temp.nextInt();
					Vector location = new Vector(x, y, z);
					Bulb bulb = new Bulb(id, location, color);
					while (id >= lights.size()) {
						lights.add(null);
					}
					lights.set(id, bulb);
				} else if (command.equals("sphere")) {
					Vector v = new Vector(temp.nextDouble(), temp.nextDouble(),
							temp.nextDouble());
					double radius = temp.nextDouble();
					Color color = new Color(temp.nextDouble(),
							temp.nextDouble(), temp.nextDouble());
					int id = temp.nextInt();
					Sphere s = new Sphere(v, radius, color);
					s.setId(id);
					if (id >= spheres.size()) {
						spheres.add(new ArrayList<Sphere>());
					}
					spheres.get(id).add(s);
				} else if (command.equals("plane")) {
					double A = temp.nextDouble();
					double B = temp.nextDouble();
					double C = temp.nextDouble();
					double D = temp.nextDouble();
					Color color = new Color(temp.nextDouble(),
							temp.nextDouble(), temp.nextDouble());
					Plane plane = new Plane(A, B, C, D, color);
					planes.add(plane);
				} else if (command.equals("planeT")) {
					//planeT id filename
					int id = temp.nextInt();
					String texture = temp.next();
					planes.get(id).setTexture(texture);
				} else if (command.equals("sphereT")) {
					//sphereT id filename
					int id = temp.nextInt();
					String texture = temp.next();
					spheres.get(id).get(0).setTexture(texture);
				}
			}
			temp.close();
		}

		scan = new Scanner(new File("test2.txt"));

		while (scan.hasNextLine()) {
			Scanner temp = new Scanner(scan.nextLine().trim());
			if (temp.hasNext()) {
				String command = temp.next();
				if (command.equals("eye")) {
					eyeStops.add(new BusStop(temp.nextInt(), temp.nextInt(),
							temp.nextDouble(), temp.nextDouble(), temp
									.nextDouble()));
				} else if (command.equals("sun") || command.equals("bulb")) {
					lightStops.add(new BusStop(temp.nextInt(), temp.nextInt(),
							temp.nextDouble(), temp.nextDouble(), temp
									.nextDouble(), temp.nextDouble(), temp
									.nextDouble(), temp.nextDouble(), temp
									.nextInt()));
				} else if (command.equals("sphere")) {
					sphereStops.add(new BusStop(temp.nextInt(), temp.nextInt(),
							temp.nextDouble(), temp.nextDouble(), temp
									.nextDouble(), temp.nextDouble(), temp
									.nextDouble(), temp.nextDouble(), temp
									.nextInt()));
				}
			}
			temp.close();
		}

		Vector eyeStart = new Vector(eye.get(0), eye.get(1), eye.get(2));

		for (int i = 1; i <= framesNum; i++) {

			for (BusStop e : eyeStops) {
				double t = ((double) i - e.getStartFrame())
						/ (e.getEndFrame() - e.getStartFrame());
				Vector v = new Vector(e.getVector().get(0) - eyeStart.get(0), e
						.getVector().get(1) - eyeStart.get(1), e.getVector()
						.get(2) - eyeStart.get(2));
				eyes.add(new Vector(eyeStart.get(0) + v.get(0) * t, eyeStart
						.get(1) + v.get(1) * t, eyeStart.get(2) + v.get(2) * t));
			}
			

			if (sphereStops.size() > 0) {
				for (BusStop s : sphereStops) {
					double t = ((double) i - s.getStartFrame())
							/ (s.getEndFrame() - s.getStartFrame());
					Vector sphereStart = spheres.get(s.getId())
							.get(spheres.get(s.getId()).size() - 1).getCenter();
					Color colorStart = spheres.get(s.getId())
							.get(spheres.get(s.getId()).size() - 1).getColor(null);
					Vector v = new Vector(s.getVector().get(0)
							- sphereStart.get(0), s.getVector().get(1)
							- sphereStart.get(1), s.getVector().get(2)
							- sphereStart.get(2));
					Vector newSphere = new Vector(sphereStart.get(0) + v.get(0)
							* t, sphereStart.get(1) + v.get(1) * t,
							sphereStart.get(2) + v.get(2) * t);
					Color newColor = new Color(colorStart.getRed() * (1 - t)
							+ s.getColor().getRed() * t, colorStart.getGreen()
							* (1 - t) + s.getColor().getGreen() * t,
							colorStart.getBlue() * (1 - t)
									+ s.getColor().getBlue() * t);
					if (s.getStartFrame() <= i && s.getEndFrame() >= i) {
						Sphere sphere = new Sphere(newSphere, spheres
								.get(s.getId()).get(0).getRadius(), newColor);
						sphere.setId(spheres
								.get(s.getId()).get(0).getId());
						sphere.setTexture(spheres
								.get(s.getId()).get(0).getTexture());
						spheres.get(sphere.getId()).add(sphere);
					} else {

						Sphere sphere = new Sphere(sphereStart, spheres
								.get(s.getId()).get(0).getRadius(), colorStart);
						sphere.setId(spheres
								.get(s.getId()).get(0).getId());
						sphere.setTexture(spheres
								.get(s.getId()).get(0).getTexture());
						spheres.get(sphere.getId()).add(sphere);
					}
				}
			} else if (spheres.size() > 0) {
				Vector center = spheres.get(0).get(0).getCenter();
				double radius = spheres.get(0).get(0).getRadius();
				Color color = spheres.get(0).get(0).getColor(null);
				Sphere sphere = new Sphere(center, radius, color);
				sphere.setId(0);
				spheres.get(sphere.getId()).add(sphere);

			}
		}
		for (int i = 0; i < framesNum; i++) {
			Color clearColor = new Color(0, 0, 0, 0);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					r.setPixel(x, y, clearColor.getColorArray());
				}
			}

			if (eyes.size() > 0) {
				eye = eyes.get(i);
			}
			// for (ArrayList<Vertex> sunList : suns) {
			// for (Vertex sun : sunList) {
			// sun = sunList.get(i);
			// }
			// }
			// for (ArrayList<Vertex> bulbList : bulbs) {
			// for (Vertex bulb : bulbList) {
			// bulb = bulbList.get(i);
			// }
			// }
			// draw image, given everything provided
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					double s = (2 * col - width)
							/ (double) Math.max(width, height);
					double t = (height - 2 * row)
							/ (double) Math.max(width, height);
					Vector direction = forward.add(right.scale(s)).add(
							up.scale(t));
					Ray ray = new Ray(eye, new Vector(direction));
					double closest = Double.POSITIVE_INFINITY;
					Vector closestNormal = null;
					Color closestColor = null;
					Object closestObject = null;
					Vector closestLocation = null;
					// find intersections with spheres
					for (ArrayList<Sphere> sphereList : spheres) {
						Sphere sphere = sphereList.get(i);
						double intersect = RayTracer.RayIntersectSphere(ray,
								sphere);
						if (intersect >= 0 && intersect < closest) {
							closest = intersect;
							Vector location = ray.scale(intersect);
							closestNormal = sphere.getNormal(location)
									.normalize();
							closestColor = sphere.getColor(ray.getOrigin().add(ray.getDirection().scale(intersect)));
							closestObject = sphere;
							closestLocation = location;
						}
					}
					// find intersections with planes
					for (Plane plane : planes) {
						double intersect = RayTracer.RayIntersectPlane(ray,
								plane);
						if (intersect >= 0 && intersect < closest) {
							closest = intersect;
							closestNormal = plane.getNormal().normalize();
							closestColor = plane.getColor();
							closestObject = plane;
							closestLocation = ray.scale(intersect);
						}
					}
					if (closestObject != null) {
						// Global illumination algorithm starts here
						int numFactoredSampleRays = numSampleRays;
						Color sumColor = new Color(0, 0, 0, 255);
						Color toColor = new Color(0, 0, 0, 255);
						for (int a = 0; a < numSampleRays; a++) {
							Ray currentSampleRay = RayTracer
									.generateRandomRay(closestLocation);
							int currentSampleNumBounces = 0;
							Color gFactor = new Color(1, 1, 1, 255);
							// FIXME
							//Color gFactor = globalFactor(currentSampleRay,
								//	currentSampleNumBounces, i);
							if (gFactor.equals(testColor)) {
								numFactoredSampleRays -= 1;
								continue;
							}
							// might invert Color
							boolean inverted = false;
							if (closestNormal.dotProduct(eye
									.subtract(closestLocation)) < 0) {
								closestColor = closestColor.invert();
								inverted = true;
							}
							// apply lighting
							// add method that takes in light vector and make a
							// light
							// interface
							for (Light light : lights) {
								if (!RayTracer.isLightBlocked(light, closestLocation, closestObject, i)) {
									double nDotI = closestNormal
											.normalize()
											.dotProduct(
													light.getDirection(closestLocation).normalize());
									if ((nDotI > 0 && !inverted)
											|| (inverted && nDotI < 0)) {
										toColor = toColor.add(closestColor
												.multiplyColors(light.getColor())
												.multiply(nDotI));
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
			String newFilename = filename.replace(".png", (i + 1) + ".png");
			System.out.println("Drawing " + newFilename + "...");

			ImageIO.write(b, "png", new File(newFilename));
		}
	}

	public static double RayIntersectSphere(Ray ray, Sphere sphere) {
		double a = ray.getDirection().dotProduct(ray.getDirection());
		double b = 2 * ray.getOrigin().subtract(sphere.getCenter())
				.dotProduct(ray.getDirection());
		double c = ray.getOrigin().subtract(sphere.getCenter())
				.dotProduct(ray.getOrigin().subtract(sphere.getCenter()))
				- sphere.getRadius() * sphere.getRadius();
		double presqrt = b * b - 4 * a * c;
		if (presqrt < 0) {
			return -1;
		}
		double sqrt = Math.sqrt(presqrt);
		double smaller = (-1 * b - sqrt) / (2 * a);
		if (smaller > 0) {
			return smaller;
		}
		double bigger = (-1 * b + sqrt) / (2 * a);
		if (bigger > 0) {
			return bigger;
		}
		return -1;
	}

	public static double RayIntersectPlane(Ray ray, Plane plane) {
		double t = (-1 * plane.getD() - plane.getNormal().dotProduct(
				ray.getOrigin()))
				/ plane.getNormal().dotProduct(ray.getDirection());
		return t;
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



	
	
	public static boolean isLightBlocked(Light light, Vector objectLocation, Object objectToColor, int frame) {
		Vector directionToLight = light.getDirection(objectLocation);
		Ray rayToLight = new Ray(objectLocation, directionToLight);
		for (ArrayList<Sphere> sphereList : spheres) {
			Sphere sphere = sphereList.get(frame);
			double t = RayTracer.RayIntersectSphere(rayToLight, sphere);
			if (sphere != objectToColor && t > 0 && t < 1) {
				return true;
			}
		}
		for (Plane plane : RayTracer.planes) {
			double t = RayTracer.RayIntersectPlane(rayToLight, plane);
			if (plane != objectToColor && t > 0 && t < 1) {
				return true;
			}
		}
		return false;
	}

	// I (Stephen) just cobbled this method together...how should we
	// structure our code?
	public static Vector findIntersection(Ray ray, int frame) {
		double closest = Double.POSITIVE_INFINITY;
		Vector closestNormal = null;
		Color closestColor = null;
		Object closestObject = null;
		Vector closestLocation = new Vector();
		closestLocation.setLight(false);
		// find intersections with spheres
		for (ArrayList<Sphere> sphereList : spheres) {
			Sphere sphere = sphereList.get(frame);
			double intersect = RayTracer.RayIntersectSphere(ray, sphere);
			if (intersect >= 0 && intersect < closest) {
				closest = intersect;
				Vector location = ray.scale(intersect);
				closestNormal = sphere.getNormal(location).normalize();
				closestColor = sphere.getColor(ray.getOrigin().add(ray.getDirection().scale(intersect)));
				closestObject = sphere;
				closestLocation = location;
				closestLocation.setClosestNormal(closestNormal);
				closestLocation.setClosestObject(closestObject);
				closestLocation.setLight(false);
				closestLocation.setColor(closestColor);
			}
		}
		// find intersections with planes
		for (Plane plane : planes) {
			double intersect = RayTracer.RayIntersectPlane(ray, plane);
			if (intersect >= 0 && intersect < closest) {
				closest = intersect;
				closestNormal = plane.getNormal().normalize();
				closestColor = plane.getColor();
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
			Color closestColor, Object closestObject, Vector closestLocation) {
		Color toColor = new Color(0, 0, 0, 255);
		// might invert Color
		boolean inverted = false;
		if (closestNormal.dotProduct(eye.subtract(closestLocation)) < 0) {
			closestColor = closestColor.invert();
			inverted = true;
		}
		// apply lighting
		// add method that takes in light vector and make a
		// light
		// interface
		for (Light light : lights) {
			if (!RayTracer.isLightBlocked(light, closestLocation, closestObject, i)) {
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
		Color gFactor = globalFactor(sampleRay, numBounces, frame);//recursively shoot rays into scene
		Color diffuse = diffuseLightCalc(frame, intersection.getClosestNormal(), intersection.getColor(),
				intersection.getClosestObject(), intersection);
		gFactor.multiplyColors(diffuse);
		return gFactor;
	}
}
