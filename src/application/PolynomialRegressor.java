package application;
import java.util.*;
public class PolynomialRegressor implements Regressor{
	public enum IterativeMethod{
		Adam,
	};
	public enum GradientDescent{
		None,
		SGD,
		MiniBatch
	};
	private ArrayList<Double> coef;
	private ArrayList<Double> nextCoef;
	private final ArrayList<Point> points;
	private  ArrayList<Point> regularizedPoints;
	private final Random rnd;
	private final int order;
	private double eta;
	private final double xStdDeviation;
	private final double xMean;
	private final double yStdDeviation;
	private final double yMean;
	private int batch_size;
	
	private GradientDescent GradDesc;
	private IterativeMethod iter;
	private long MAX_ITER;
	public PolynomialRegressor(ArrayList<Point > points, int order) {
		this.points = points;
		xMean = points.stream()
					.mapToDouble(p -> p.getX())
					.average()
					.getAsDouble();
		xStdDeviation = Math.sqrt(points.stream()
					.mapToDouble(p -> p.getX())
					.map(x -> (x - xMean) * (x - xMean))
					.average()
					.getAsDouble());
		yMean = points.stream()
				.mapToDouble(p -> p.getY())
				.average()
				.getAsDouble();
		yStdDeviation = Math.sqrt(points.stream()
				.mapToDouble(p -> p.getY())
				.map(y -> (y - yMean) * (y - yMean))
				.average()
				.getAsDouble());
		coef = new ArrayList<Double>();
		nextCoef = new ArrayList<Double>();
		regularizedPoints = new ArrayList<Point>();
		initializeRegularizedPoints();
		rnd = new Random(System.currentTimeMillis());
		this.order = order;
		eta = 2.0e-3;
		MAX_ITER = 10000;
		iter =  IterativeMethod.Adam;
	}
	public void regress() {
		initialize();
		calcWithAdam();
		return ;
	}
	public ArrayList<Double> getCoef(){
		return coef;
	}
	public PolynomialRegressor setIterativeMethod(IterativeMethod method) {
		this.iter = method;
		return this;
	}
	public PolynomialRegressor setGradientDecsent(GradientDescent gd) {
		this.GradDesc  = gd;
		return this;
	}
	public PolynomialRegressor setBathcSize(int size) {
		this.batch_size = size;
		return this;
	}
	public PolynomialRegressor setMaxIter(long max) {
		this.MAX_ITER = max;
		return this;
	}
	private void initialize() {
		for(int i = 0; i <= order; i++) {
			coef.add(rnd.nextGaussian());
		}
	}
	public double calcPolynomial(double x) {
		double ret = 0;
		for(double c: coef) {
			ret = ret * x + c;
		}
		return ret;
	}
	private void initializeRegularizedPoints() {
		regularizedPoints.addAll(points.stream()
				.map(p -> new Point((p.getX() - xMean)/xStdDeviation, (p.getY() - yMean) / yStdDeviation))
				.toList());
	}
	private double calcGradient( Point p, int index) {
		final double err = calcPolynomial(p.getX()) - p.getY();
		final double grad = Math.pow(p.getX(), (double)(order - index));
		double ret = err * grad;;
		return ret;
	}
	private double calcStep(int index) {
		double ret = 
				regularizedPoints.stream().mapToDouble(p -> calcGradient(p, index) )
				.sum();
				return ret;
	}
	private void updateParamater() {
		for(int i = 0; i <= order; i++) {
			nextCoef.add(coef.get(i) - eta * calcStep(i));
		}
		coef.clear();
		coef.addAll(nextCoef);
		nextCoef.clear();
	}
	public double calcRecall(double x) {
		return yStdDeviation * calcPolynomial((x - xMean) / xStdDeviation) + yMean;
	}
	private double calcMeanSquareError() {
		double ret = points.stream()
					.mapToDouble(p -> Math.pow((p.getY() - calcRecall(p.getX())), 2))
					.average()
					.getAsDouble();
		return ret;
	}
	private double calcBatchGrad(final int index) {
		double ret = regularizedPoints.stream()
				.limit(batch_size)
				.mapToDouble(p -> calcGradient(p, index))
				.sum();
		return ret;
	}
	private void calcWithAdam() {
		long t = 1;
		final double alpha = 1e-3;
		final double beta_1 = 9e-1;
		final double beta_2 = 0.999;
		final double epsilon = 1e-7;
		double m[] = new double[order + 1];
		double v[] = new double[order + 1];
		double mhat, vhat;
		for(int i = 0; i <= order; i++) {
			m[i] = 0;
			v[i] = 0;
		}
		while(t <= MAX_ITER) {
			nextCoef.clear();
			Collections.shuffle(regularizedPoints);
			for(int i = 0; i <= order; i++) {
				double tmpGrad = 0;
				switch (GradDesc) {
				case MiniBatch:
					tmpGrad = calcBatchGrad(i);
					break;
				case None:
					tmpGrad = calcStep(i);
					break;
				case SGD:
					tmpGrad = calcGradient(regularizedPoints.get(0), i);
					break;
				}
				m[i] = beta_1 * m[i] + (1 - beta_1) * tmpGrad;
				v[i] = beta_2 * v[i] +(1 - beta_2) * tmpGrad * tmpGrad;
				mhat = m[i] / (1.0 - Math.pow(beta_1, t));
				vhat = v[i] / (1.0 - Math.pow(beta_2, t));
				
				nextCoef.add(coef.get(i) - alpha * mhat / (Math.sqrt(vhat) + epsilon));
			}
			coef.clear();
			coef.addAll(nextCoef);
			t++;
		}
	}
}
