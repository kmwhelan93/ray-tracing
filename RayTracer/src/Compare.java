import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Compare {
	public static void main (String[] args) throws IOException {
		String image1 = args[0];
		String image2 = args[1];
		String destination = args[2];
		BufferedImage img1 = null;
		BufferedImage img2 = null;
		WritableRaster r1 = null;
		WritableRaster r2 = null;
		try {
			img1 = ImageIO.read(new File(image1));
			img2 = ImageIO.read(new File(image2));
			r1 = img1.getRaster();
			r2 = img2.getRaster();
		} catch(Exception e) {
			
		}
		
		BufferedImage outputB = new BufferedImage(r1.getWidth(), r1.getHeight(),  BufferedImage.TYPE_4BYTE_ABGR);
		WritableRaster rout = outputB.getRaster();
		
		for (int x = 0; x < rout.getWidth(); x++) {
			for (int y = 0; y < rout.getHeight(); y++) {
				double[] d1 = new double[4];
				double[] d2 = new double[4];
				Color diff = new Color(r1.getPixel(x, y, d1)).subtract(new Color(r2.getPixel(x, y, d2)));
				rout.setPixel(x, y, diff.invertIfNegative().getColorArray());
			}
		}
		
		ImageIO.write(outputB, "png", new File(destination));
	}
}
