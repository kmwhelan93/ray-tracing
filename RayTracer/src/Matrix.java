import java.text.DecimalFormat;


public class Matrix {
	protected double[][] matrix;
	
	// constructors
	public Matrix() {
		// do nothing
	}
	public Matrix(double[][] matrix) {
		this.matrix = matrix;
	}
	public Matrix(int rows, int cols) {
		this.matrix = new double[rows][cols];
	}
	public Matrix(Matrix m) {
		this.matrix = new double[m.matrix.length][m.matrix[0].length];
		for (int i = 0; i < m.matrix.length; i++) {
			for (int j = 0; j < m.matrix[i].length; j++) {
				this.matrix[i][j] = m.matrix[i][j];
			}
		}
	}
	
	// getters and setters
	public double get(int row, int col) {
		return matrix[row][col];
	}
	public void set(int row, int col, double val) {
		matrix[row][col] = val;
	}
	public double[][] getMatrix() {
		return matrix;
	}
	
	
	// methods
	public int rows() {
		return matrix.length;
	}
	public int cols() {
		if (matrix.length == 0)
			return 0;
		return matrix[0].length;
	}
	public Matrix multiply(Matrix m) {
		Matrix result = new Matrix(this.rows(), m.cols());
		for (int row = 0; row < result.rows(); row++) {
			for (int col = 0; col < result.cols(); col++) {
				double sum = 0;
				for (int i = 0; i < this.cols(); i++) {
					sum += this.get(row, i) * m.get(i, col);
				}
				result.set(row, col, sum);
			}
		}
		
		return result;
	}
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00");
		String result = "[";
		for (int i = 0; i < this.rows(); i++) {
			result += "[" + df.format(this.get(i, 0));
			for (int j = 1; j < this.cols(); j++) {
				result += ", " + df.format(this.get(i, j));
			}
			result += "]";
			if (i != this.rows() - 1)
				result += "\n";
		}
		result += "]";
		return result;
	}
	
	
	
	// static Methods
	public static Matrix getIdentity(int rows) {
		Matrix m = new Matrix(rows, rows);
		for (int i = 0; i < rows; i++) {
			m.set(i, i, 1);
		}
		return m;
	}
	
	public Matrix transpose() {
		Matrix retVal = new Matrix(this);
		for (int row = 0; row < this.rows(); row++) {
			for (int col = 0; col < this.cols(); col++) {
				retVal.matrix[row][col] = this.matrix[col][row];
			}
		}
		return retVal;
	}
	
	public Matrix scale(double factor) {
		Matrix retVal = new Matrix(this.rows(), this.cols());
		for (int i = 0; i < this.rows(); i++) {
			for (int j = 0; j < this.cols(); j++) {
				retVal.set(i, j, this.matrix[i][j]*factor);
			}
		}
		return retVal;
	}
}
