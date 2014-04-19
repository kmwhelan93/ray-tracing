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
	private static ArrayList<ArrayList<Vertex>> suns = new ArrayList<ArrayList<Vertex>>();
	private static ArrayList<BusStop> sunStops = new ArrayList<BusStop>();
	private static ArrayList<ArrayList<Vertex>> bulbs = new ArrayList<ArrayList<Vertex>>();
	private static ArrayList<BusStop> bulbStops = new ArrayList<BusStop>();
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
					Vertex sun = new Vertex(v, color);
					sun.setId(id);
					if (id >= suns.size()) {
						suns.add(new ArrayList<Vertex>());
					}
					suns.get(id).add(sun);
				} else if (command.equals("bulb")) {
					double x = temp.nextDouble();
					double y = temp.nextDouble();
					double z = temp.nextDouble();
					Color color = new Color(temp.nextDouble(),
							temp.nextDouble(), temp.nextDouble());
					int id = temp.nextInt();
					Vertex bulb = new Vertex(x, y, z, color);
					bulb.setId(id);
					if (id >= bulbs.size()) {
						bulbs.add(new ArrayList<Vertex>());
					}
					bulbs.get(id).add(bulb);
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
				} else if (command.equals("sun")) {
					sunStops.add(new BusStop(temp.nextInt(), temp.nextInt(),
							temp.nextDouble(), temp.nextDouble(), temp
									.nextDouble(), temp.nextDouble(), temp
									.nextDouble(), temp.nextDouble(), temp
									.nextInt()));
				} else if (command.equals("bulb")) {
					bulbStops.add(new BusStop(temp.nextInt(), temp.nextInt(),
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
			if (sunStops.size() > 0) {
				for (BusStop s : sunStops) {
					double t = ((double) i - s.getStartFrame())
							/ (s.getEndFrame() - s.getStartFrame());
					Vector sunStart = suns.get(s.getId())
							.get(suns.get(s.getId()).size() - 1).getVector();
					Color colorStart = suns.get(s.getId())
							.get(suns.get(s.getId()).size() - 1).getColor();
					Vector v = new Vector(s.getVector().get(0)
							- sunStart.get(0), s.getVector().get(1)
							- sunStart.get(1), s.getVector().get(2)
							- sunStart.get(2));
					Vector newSun = new Vector(sunStart.get(0) + v.get(0) * t,
							sunStart.get(1) + v.get(1) * t, sunStart.get(2)
									+ v.get(2) * t);

					Color newColor = new Color(colorStart.getRed() * (1 - t)
							+ s.getColor().getRed() * t, colorStart.getGreen()
							* (1 - t) + s.getColor().getGreen() * t,
							colorStart.getBlue() * (1 - t)
									+ s.getColor().getBlue() * t);

					if (s.getStartFrame() <= i && s.getEndFrame() >= i) {

						Vertex sun = new Vertex(newSun, newColor);
						sun.setId(s.getId());
						suns.get(sun.getId()).add(sun);
					} else {
						Vertex sun = new Vertex(sunStart, colorStart);
						sun.setId(s.getId());
						suns.get(sun.getId()).add(sun);
					}
				}
			} else if (suns.size() > 0) {
				Vector sunVect = suns.get(0).get(0).getVector();
				Color color = suns.get(0).get(0).getColor();
				Vertex sun = new Vertex(sunVect, color);
				sun.setId(0);
				suns.get(sun.getId()).add(sun);

			}

			if (bulbStops.size() > 0) {
				for (BusStop b : bulbStops) {
					double t = ((double) i - b.getStartFrame())
							/ (b.getEndFrame() - b.getStartFrame());
					Vector bulbStart = bulbs.get(b.getId())
							.get(bulbs.get(b.getId()).size() - 1).getVector();
					Color colorStart = bulbs.get(b.getId())
							.get(bulbs.get(b.getId()).size() - 1).getColor();
					Vector v = new Vector(b.getVector().get(0)
							- bulbStart.get(0), b.getVector().get(1)
							- bulbStart.get(1), b.getVector().get(2)
							- bulbStart.get(2));
					Vector newBulb = new Vector(
							bulbStart.get(0) + v.get(0) * t, bulbStart.get(1)
									+ v.get(1) * t, bulbStart.get(2) + v.get(2)
									* t);
					Color newColor = new Color(colorStart.getRed() * (1 - t)
							+ b.getColor().getRed() * t, colorStart.getGreen()
							* (1 - t) + b.getColor().getGreen() * t,
							colorStart.getBlue() * (1 - t)
									+ b.getColor().getBlue() * t);
					if (b.getStartFrame() <= i && b.getEndFrame() >= i) {
						Vertex bulb = new Vertex(newBulb, newColor);
						bulb.setId(b.getId());
						bulbs.get(bulb.getId()).add(bulb);
					} else {

						Vertex bulb = new Vertex(bulbStart, colorStart);
						bulb.setId(b.getId());
						bulbs.get(bulb.getId()).add(bulb);
					}
				}
			} else if (bulbs.size() > 0) {
				Vector bulbVect = bulbs.get(0).get(0).getVector();
				Color color = bulbs.get(0).get(0).getColor();
				Vertex bulb = new Vertex(bulbVect, color);
				bulb.setId(0);
				bulbs.get(bulb.getId()).add(bulb);

			}

			if (sphereStops.size() > 0) {
				for (BusStop s : sphereStops) {
					double t = ((double) i - s.getStartFrame())
							/ (s.getEndFrame() - s.getStartFrame());
					Vector sphereStart = spheres.get(s.getId())
							.get(spheres.get(s.getId()).size() - 1).getCenter();
					Color colorStart = spheres.get(s.getId())
							.get(spheres.get(s.getId()).size() - 1).getColor();
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
						sphere.setId(s.getId());
						spheres.get(sphere.getId()).add(sphere);
					} else {

						Sphere sphere = new Sphere(sphereStart, spheres
								.get(s.getId()).get(0).getRadius(), colorStart);
						sphere.setId(s.getId());
						spheres.get(sphere.getId()).add(sphere);
					}
				}
			} else if (spheres.size() > 0) {
				Vector center = spheres.get(0).get(0).getCenter();
				double radius = spheres.get(0).get(0).getRadius();
				Color color = spheres.get(0).get(0).getColor();
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
							closestColor = sphere.getColor();
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
							Color gFactor = globalFactor(currentSampleRay,
									currentSampleNumBounces, i);
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
							for (ArrayList<Vertex> sunList : suns) {
								Vertex sun = sunList.get(i);
								if (!RayTracer.isSunBlocked(sun,
										closestLocation, closestObject, i)) {
									double nDotI = closestNormal
											.normalize()
											.dotProduct(
													sun.getVector().normalize());
									if ((nDotI > 0 && !inverted)
											|| (inverted && nDotI < 0)) {
										toColor = toColor.add(closestColor
												.multiplyColors(sun.getColor())
												.multiply(nDotI));
									}

								}
							}
							for (ArrayList<Vertex> bulbList : bulbs) {
								Vertex bulb = bulbList.get(i);
								if (!RayTracer.isBulbBlocked(bulb,
										closestLocation, closestObject, i)) {
									double nDotI = closestNormal
											.normalize()
											.dotProduct(
													bulb.getVector()
															.subtract(
																	closestLocation)
															.normalize());
									if ((nDotI > 0 && !inverted)
											|| (inverted && nDotI < 0)) {
										toColor = toColor
												.add(closestColor
														.multiplyColors(
																bulb.getColor())
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

	public static boolean isSunBlocked(Vertex sun, Vector location, Object o,
			int frame) {
		Vector directionToSun = sun.getVector();
		Ray rayToSun = new Ray(location, directionToSun);
		for (ArrayList<Sphere> sphereList : spheres) {
			Sphere sphere = sphereList.get(frame);
			if (sphere != o
					&& RayTracer.RayIntersectSphere(rayToSun, sphere) > 0) {
				return true;
			}
		}
		for (Plane plane : RayTracer.planes) {
			if (plane != o && RayTracer.RayIntersectPlane(rayToSun, plane) > 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBulbBlocked(Vertex bulb, Vector location, Object o,
			int frame) {
		Vector directionToBulb = bulb.getVector().subtract(location);
		Ray rayToBulb = new Ray(location, directionToBulb);
		// double tToBulb = 1;
		for (ArrayList<Sphere> sphereList : spheres) {
			Sphere sphere = sphereList.get(frame);
			double t = RayTracer.RayIntersectSphere(rayToBulb, sphere);
			if (sphere != o && t > 0 && t < 1) {
				return true;
			}
		}
		for (Plane plane : RayTracer.planes) {
			double t = RayTracer.RayIntersectPlane(rayToBulb, plane);
			if (plane != o && t > 0 && t < 1) {
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
		Vector closestLocation = null;
		// find intersections with spheres
		for (ArrayList<Sphere> sphereList : spheres) {
			Sphere sphere = sphereList.get(frame);
			double intersect = RayTracer.RayIntersectSphere(ray, sphere);
			if (intersect >= 0 && intersect < closest) {
				closest = intersect;
				Vector location = ray.scale(intersect);
				closestNormal = sphere.getNormal(location).normalize();
				closestColor = sphere.getColor();
				closestObject = sphere;
				closestLocation = location;
				closestLocation.setClosestNormal(closestNormal);
				closestLocation.setClosestObject(closestObject);
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
			}
		}
		// find intersections with lights
		for (ArrayList<Vertex> sunList : suns) {
			Vertex sun = sunList.get(frame);
			double intersect = RayTracer.RayIntersectVertex(ray, sun);
			if (intersect >= 0 && intersect < closest) {
				closest = intersect;
				closestNormal = sun.getNormal().normalize();
				closestColor = sun.getColor();
				closestObject = sun;
				closestLocation = ray.scale(intersect);
				closestLocation.setLight(true);
				closestLocation.setColor(closestColor);
				closestLocation.setClosestNormal(closestNormal);
				closestLocation.setClosestObject(closestObject);
			}
		}
		for (ArrayList<Vertex> bulbList : bulbs) {
			Vertex bulb = bulbList.get(frame);
			double intersect = RayTracer.RayIntersectVertex(ray, bulb);
			if (intersect >= 0 && intersect < closest) {
				closest = intersect;
				closestNormal = bulb.getNormal().normalize();
				closestColor = bulb.getColor();
				closestObject = bulb;
				closestLocation = ray.scale(intersect);
				closestLocation.setLight(true);
				closestLocation.setClosestNormal(closestNormal);
				closestLocation.setClosestObject(closestObject);
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
		for (ArrayList<Vertex> sunList : suns) {
			Vertex sun = sunList.get(i);
			if (!RayTracer.isSunBlocked(sun, closestLocation, closestObject, i)) {
				double nDotI = closestNormal.normalize().dotProduct(
						sun.getVector().normalize());
				if ((nDotI > 0 && !inverted) || (inverted && nDotI < 0)) {
					toColor = toColor.add(closestColor.multiplyColors(
							sun.getColor()).multiply(nDotI));
				}

			}
		}
		for (ArrayList<Vertex> bulbList : bulbs) {
			Vertex bulb = bulbList.get(i);
			if (!RayTracer.isBulbBlocked(bulb, closestLocation, closestObject,
					i)) {
				double nDotI = closestNormal.normalize().dotProduct(
						bulb.getVector().subtract(closestLocation).normalize());
				if ((nDotI > 0 && !inverted) || (inverted && nDotI < 0)) {
					toColor = toColor.add(closestColor.multiplyColors(
							bulb.getColor()).multiply(nDotI));
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
