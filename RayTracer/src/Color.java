public class Color {
	public static int MAX_ALPHA = 1;
	// fields
	// red, green and blue will map as follows: x <= 0 -> 0, 0 < x < 1 -> 255*x, x >= 1 -> 255
	private double red;
	private double green;
	private double blue;
	private double alpha;
	
	// getters and setters
	public double getRed() {
		return red;
	}
	public void setRed(double red) {
		this.red = red;
	}
	public double getGreen() {
		return green;
	}
	public void setGreen(double green) {
		this.green = green;
	}
	public double getBlue() {
		return blue;
	}
	public void setBlue(double blue) {
		this.blue = blue;
	}
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	// Constructors
	public Color(double red, double green, double blue) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1;
	}
	public Color() {
		super();
		this.red = 1;
		this.green = 1;
		this.blue = 1;
		this.alpha = 1;
	}
	public Color(String hexColorCode) {
		this.red = Integer.valueOf(hexColorCode.substring(1,3), 16);
		this.green = Integer.valueOf(hexColorCode.substring(3,5), 16);
		this.blue = Integer.valueOf(hexColorCode.substring(5,7), 16);
		if (hexColorCode.length() == 9) {
			this.alpha = Integer.valueOf(hexColorCode.substring(7,9), 16);
		} else {
			this.alpha = 1;
		}
	}
	public Color(double[] colorArray) {
		this.red = colorArray[0];
		this.green = colorArray[1];
		this.blue = colorArray[2];
		this.alpha = colorArray[3];
	}
	
	public Color(double red, double green, double blue, double alpha) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	public Color (Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.alpha = color.getAlpha();
	}
	// Methods
	public double[] getColorArray() {
		double[] retVal = {scale(red), scale(green), scale(blue), scale(alpha)};
		return retVal;
	}
	public double scale(double x) {
		if (x <= 0) {
			return 0;
		} else if (x >= 1) {
			return 255;
		}
		return x*255;
	}
	@Override
	public String toString() {
		return "Color [red=" + red + ", green=" + green + ", blue=" + blue
				+ ", alpha=" + alpha + "]";
	}
	public Color associativeOver(Color topColor) {
		// "this" will be bottom color
		double finalAlpha = topColor.getAlpha() + (255 - topColor.getAlpha()) / 255 * this.getAlpha();
		double finalRed = (topColor.getRed() * topColor.getAlpha() + (255 - topColor.getAlpha()) / 255 * this.getAlpha() * this.getRed()) / finalAlpha;
		double finalGreen = (topColor.getGreen() * topColor.getAlpha() + (255 - topColor.getAlpha()) / 255 * this.getAlpha() * this.getGreen()) / finalAlpha;
		double finalBlue = (topColor.getBlue() * topColor.getAlpha() + (255 - topColor.getAlpha()) / 255 * this.getAlpha() * this.getBlue()) / finalAlpha;
		return new Color(finalRed, finalGreen, finalBlue, finalAlpha);
	}
	public Color add(Color adder) {
		return new Color(this.getRed() + adder.getRed(), 
				this.getGreen() + adder.getGreen(),
				this.getBlue() + adder.getBlue(),
				1);
	}
	public Color subtract(Color subtractor) {
		return new Color(this.getRed() - subtractor.getRed(), 
				this.getGreen() - subtractor.getGreen(),
				this.getBlue() - subtractor.getBlue(),
				this.getAlpha());
	}
	public Color multiply(double multiplier) {
		return new Color(this.getRed() * multiplier,
				this.getGreen() * multiplier,
				this.getBlue() * multiplier,
				this.getAlpha());
	}
	public Color divide(double divisor) {
		return new Color(this.getRed() / divisor,
				this.getGreen() / divisor,
				this.getBlue() / divisor,
				this.getAlpha());
	}
	public Color absVal() {
		return new Color(Math.abs(this.getRed()),
				Math.abs(this.getGreen()),
				Math.abs(this.getBlue()),
				Math.abs(this.getAlpha()));
	}
	public Color invertIfNegative() {
		return new Color(this.getRed() < 0 ? this.getRed() + 255 : this.getRed(),
				this.getGreen() < 0 ? this.getGreen() + 255 : this.getGreen(),
				this.getBlue() < 0 ? this.getBlue() + 255 : this.getBlue(),
				this.getAlpha() == 0 ? 1 : this.getAlpha());
	}
	
	public Color invert() {
		return new Color (-1*this.getRed(), -1* this.getGreen(), -1* this.getBlue(), this.getAlpha());
	}
	public Color multiplyAlpha(double multiplier) {
		return new Color(this.getRed(), 
				this.getGreen(),
				this.getBlue(),
				this.getAlpha() * multiplier);
	}
	public Color multiplyColors(Color c) {
		return new Color (this.getRed() * c.getRed(),
				this.getGreen()*c.getGreen(),
				this.getBlue()*c.getBlue(),
				this.getAlpha());
	}
	public Color clampToRange() {
		Color result = new Color(this);
		if (result.getRed() > 1) {
			result.setRed(1);
		}
		if (result.getRed() < 0) {
			result.setRed(0);
		}
		if (result.getGreen() > 1) {
			result.setGreen(1);
		}
		if (result.getGreen() < 0) {
			result.setGreen(0);
		}
		if (result.getBlue() > 1) {
			result.setBlue(1);
		}
		if (result.getBlue() < 0) {
			result.setBlue(0);
		}
		if (result.getAlpha() > 1) {
			result.setAlpha(1);
		}
		if (result.getAlpha() < 0) {
			result.setAlpha(0);
		}
		return result;
	}
	
	
	
	
}
