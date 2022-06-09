package application.neuralNet;

import java.util.*;

public class Matrix {
	private double entry[];
	final int row;
	final int column;
	private final Random rnd;
	static enum InitializeType{
		Uniform,
		XavierUniform,
		XavierNormal,
		He,
		Identity,
	}
	public Matrix(int row, int column) {
		rnd = new Random(System.currentTimeMillis());
		this.row = row;
		this.column = column;
		entry = new double[row * column];
	}
	public static Matrix getIdentityMatrix(int order) {
		Matrix ret = new Matrix(order, order);
		for(int i = 0; i < order; i++) {
			ret.setEntry(i, i, 1);
		}
		return ret;
	}
	public void setEntry(int row, int column, double value) {
		this.entry[row * this.column + column] = value;
	}
	public double getEntry(int row, int column) {
		return entry[row * this.column + column];
	}
	public Matrix getTranspose() {
		Matrix ret = new Matrix(this.column, this.row);
		for(int i = 0; i < ret.row; i++) {
			for(int j = 0; j < ret.column; j++) {
				ret.setEntry(i, j, this.getEntry(j, i));
			}
		}
		return ret;
	}
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('[');
		for(int i = 0; i < this.row; i++) {
			buf.append('[');
			for(int j = 0; j < this.column; j++) {
				buf.append(String.format("%.6f", this.getEntry(i, j)));
				buf.append((j == this.column - 1)? ']' : ',');
			}
			buf.append('\n');
		}
		buf.deleteCharAt(buf.length() - 1);
		buf.append(']');
		return buf.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if(this.column != ((Matrix)obj).column || this.row != ((Matrix)obj).row) {
			return false;
		}
		for(int i = 0; i < this.row ; i++) {
			for(int j = 0; j < this.column; j++) {
				if(this.getEntry(i, j) != ((Matrix)obj).getEntry(i, j)) {
					return false;
				}
			}
		}
		return true;
	}
	public void initializeMatrix(InitializeType type) {
		switch(type) {
		case He:
			setHeNormal();
			return;
		case Uniform:
			setUniform();
			return ;
		case XavierNormal:
			setXavierNormal();
			return ;
		case XavierUniform:
			setXavierUniform();
			return ;
		case Identity:
			setIdentity();
			return ;
		}
	}
	public static Matrix prodMatrix(Matrix left, Matrix right) {
		double sum;
		assert (left.column == right.row);
		Matrix ret = new Matrix(left.row, right.column);
		for(int i = 0; i < ret.row; i++) {
			for(int j = 0; j < ret.column; j++) {
				sum = 0;
				for(int k = 0; k < left.column; k++) {
					sum += left.getEntry(i, k) * right.getEntry(k, j);
				}
				ret.setEntry(i, j, sum);
			}
		}
		return ret;
	}
	public static Matrix hadamardProdMatrix(Matrix left, Matrix right) {
		assert ((left.column == right.column) && (left.row == right.row));
		Matrix ret = new Matrix(left.column, left.row);
		for(int i = 0; i < left.row; i++) {
			for(int j = 0; j < left.column; j++) {
				ret.setEntry(i, j, left.getEntry(i, j) * right.getEntry(i, j));
			}
		}
		return ret;
	}
	public static Matrix concatH(Matrix left, Matrix right) {
		assert (left.row == right.row);
		Matrix ret = new Matrix(left.row, left.column + right.column);
		for(int i = 0; i < left.row; i++) {
			for(int j = 0; j < ret.column; j++) {
				ret.setEntry(i, j, (j < left.column)? left.getEntry(i, j) : right.getEntry(i, j - left.column));
			}
		}
		return ret;
	}
	public static Matrix concatV(Matrix up, Matrix bottom) {
		assert (up.column == bottom.column);
		Matrix ret = new Matrix(up.row + bottom.row, up.column);
		for(int i = 0; i < ret.row; i++) {
			for(int j = 0; j < ret.column; j++) {
				ret.setEntry(i, j, (i < up.row)? up.getEntry(i, j):bottom.getEntry(i - up.row, j));
			}
		}
		return ret;
	}
	private void setIdentity() {
		assert (this.column == this.row);
		Arrays.fill(entry, 0);
		for(int i = 0; i < this.column; i++) {
			setEntry(i, i, 1);
		}
	}
	private void setHeNormal() {
		final double stdDev = Math.sqrt(2 / this.row);
		for(int i = 0; i < this.row; i++) {
			for(int j = 0; j < this.column; j++) {
				this.setEntry(i, j, rnd.nextGaussian() * stdDev);
			}
		}
	}
	private void setUniform() {
		for(int i = 0; i < this.row; i++) {
			for(int j = 0; j < this.column; j++) {
				this.setEntry(i, j, rnd.nextDouble() - 0.5);
			}
		}
	}
	private void setXavierNormal() {
		final double stdDev = Math.sqrt(2 / (this.column + this.row));
		for(int i = 0; i < this.row; i++) {
			for(int j = 0; j < this.column; j++) {
				this.setEntry(i, j, rnd.nextGaussian() * stdDev);
			}
		}
	}
	private void setXavierUniform() {
		final double bound = Math.sqrt(6 / (this.row + this.column));
		for(int i = 0; i < this.row; i++) {
			for(int j = 0; j < this.column; j++) {
				this.setEntry(i, j, bound * (rnd.nextDouble() * 2 - 1));
			}
		}
	}
	public static Matrix addMatrix(Matrix left, Matrix right) {
		assert ((left.column == right.column) && (left.row == right.row));
		Matrix ret = new Matrix(left.row, left.column);
		for(int i = 0; i < ret.row; i++) {
			for(int j = 0; j < ret.column; j++) {
				ret.setEntry(i, j, left.getEntry(i, j) + right.getEntry(i, j));
			}
		}
		return ret;
	}
	public double[] toArray() {
		double[] ret = new double[this.entry.length];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = this.entry[i];
		}
		return ret;
	}
	public static Matrix toVector(double entry[]) {
		Matrix ret = new Matrix(entry.length, 1);
		return ret;
	}
	public Matrix getSubMatrix(int srow, int scol, int drow, int dcol) {
		assert(srow >= 0 && scol >= 0 && drow < this.row  && dcol < this.column);
		assert ((srow < drow) && (scol < dcol));
		Matrix ret = new Matrix(drow - srow + 1, dcol - scol + 1);
		for(int i = srow; i <= srow; i++) {
			for(int j = scol; j <= scol; j++) {
				ret.setEntry(i - srow, j - scol, this.getEntry(i, j));
			}
		}
		return ret;
	}
}
