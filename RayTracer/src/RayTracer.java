//this is a change
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class RayTracer {

	/**
	 * @param args
	 * @throws IOException
	 */
	private static int width;
	private static int height;
	private static BufferedImage b;
	private static WritableRaster r;
	private static Vector eye = new Vector(0, 0, 0);
	private static Vector forward = new Vector(0, 0, -1);
	private static Vector right = new Vector(1, 0, 0);
	private static Vector up = new Vector(0, 1, 0);
	private static ArrayList<Vertex> suns = new ArrayList<Vertex>();
	private static ArrayList<Vertex> bulbs = new ArrayList<Vertex>();
	private static ArrayList<Sphere> spheres = new ArrayList<Sphere>();
	private static ArrayList<Plane> planes = new ArrayList<Plane>();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(new File(args[0]));

		String filename = "";

		while (scan.hasNextLine()) {
			Scanner temp = new Scanner(scan.nextLine().trim());
			if (temp.hasNext()) {
				String command = temp.next();
				if (command.equals("png")) {
					// png
					width = temp.nextInt();
					height = temp.nextInt();
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
					Vertex sun = new Vertex(v, color);
					suns.add(sun);
				} else if (command.equals("bulb")) {
					double x = temp.nextDouble();
					double y = temp.nextDouble();
					double z = temp.nextDouble();
					Color color = new Color(temp.nextDouble(),
							temp.nextDouble(), temp.nextDouble());
					Vertex bulb = new Vertex(x, y, z, color);
					bulbs.add(bulb);
				} else if (command.equals("sphere")) {
					Vector v = new Vector(temp.nextDouble(), temp.nextDouble(),
							temp.nextDouble());
					double radius = temp.nextDouble();
					Color color = new Color(temp.nextDouble(),
							temp.nextDouble(), temp.nextDouble());
					Sphere s = new Sphere(v, radius, color);
					spheres.add(s);
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

		}

		// draw image, given everything provided
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				double s = (2 * col - width) / (double) Math.max(width, height);
				double t = (height - 2 * row)
						/ (double) Math.max(width, height);
				Vector direction = forward.add(right.scale(s)).add(up.scale(t));
				Ray ray = new Ray(eye, new Vector(direction));
				double closest = Double.POSITIVE_INFINITY;
				Vector closestNormal = null;
				Color closestColor = null;
				Object closestObject = null;
				Vector closestLocation = null;
				for (Sphere sphere : spheres) {
					double intersect = RayTracer.RayIntersectSphere(ray, sphere);
					if (intersect >= 0 && intersect < closest) {
						closest = intersect;
						Vector location = ray.scale(intersect);
						closestNormal = sphere.getNormal(location).normalize();
						closestColor = sphere.getColor();
						closestObject = sphere;
						closestLocation = location;
					}
				}
				for (Plane plane : planes) {
					double intersect = RayTracer.RayIntersectPlane(ray, plane);
					if (intersect >= 0 && intersect < closest) {
						closest = intersect;
						closestNormal = plane.getNormal().normalize();
						closestColor = plane.getColor();
						closestObject = plane;
						closestLocation = ray.scale(intersect);
					}
				}
				if (closestObject != null) {
					Color toColor = new Color(0, 0, 0, 255);
					// might invert Color
					boolean inverted = false;
					if (closestNormal.dotProduct(eye.subtract(closestLocation)) < 0) {
						closestColor = closestColor.invert();
						inverted = true;
					}

					// apply lighting
					// add method that takes in light vector and make a light interface
					for (Vertex sun : suns) {
						if (!RayTracer.isSunBlocked(sun, closestLocation,
								closestObject)) {
							double nDotI = closestNormal.normalize()
									.dotProduct(sun.getVector().normalize());
							if ((nDotI > 0 && !inverted) || (inverted && nDotI < 0)) {
								toColor = toColor.add(closestColor
										.multiplyColors(sun.getColor())
										.multiply(nDotI));
							}

						}
					}
					for (Vertex bulb : bulbs) {
						if (!RayTracer.isBulbBlocked(bulb, closestLocation,
								closestObject)) {
							double nDotI = closestNormal.normalize()
									.dotProduct(
											bulb.getVector()
													.subtract(closestLocation)
													.normalize());
							if ((nDotI > 0 && !inverted) || (inverted && nDotI < 0)) {
								toColor = toColor.add(closestColor
										.multiplyColors(bulb.getColor())
										.multiply(nDotI));
							}
						}
					}
					r.setPixel(col, row, toColor.getColorArray());
				}
			}
		}

		ImageIO.write(b, "png", new File(filename));
	}

	public static double RayIntersectSphere(Ray ray, Sphere sphere) {
		// origin + c*direction
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

	public static boolean isSunBlocked(Vertex sun, Vector location, Object o) {
		Vector directionToSun = sun.getVector();
		Ray rayToSun = new Ray(location, directionToSun);
		for (Sphere sphere : RayTracer.spheres) {
			if (sphere != o && RayTracer.RayIntersectSphere(rayToSun, sphere) > 0) {
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

	public static boolean isBulbBlocked(Vertex bulb, Vector location, Object o) {
		Vector directionToBulb = bulb.getVector().subtract(location);
		Ray rayToBulb = new Ray(location, directionToBulb);
		double tToBulb = 1;
		for (Sphere sphere : RayTracer.spheres) {
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
}
