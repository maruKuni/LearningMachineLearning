package application;
import java.util.*;
public class PolynomialRegressor {
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
		eta = 1.0e-3;
	}
	public PolynomialRegressor regress() {
		initialize();
		while(eta >= 1e-6) {
			for(int i = 0; i < 50; i++)
				updateParamater();
			eta *= 0.9;
		}
		return this;
	}
	public ArrayList<Double> getCoef(){
		return coef;
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
		System.out.println("poly:" + ret);
		return ret;
	}
	private void initializeRegularizedPoints() {
		regularizedPoints.addAll(points.stream()
				.map(p -> new Point((p.getX() - xMean)/xStdDeviation, (p.getY() - yMean) / yStdDeviation))
				.toList());
	}
	private double calcError( Point p, int index) {
		final double err = calcPolynomial(p.getX()) - p.getY();
		final double grad = Math.pow(p.getX(), (double)(order - index));
		System.out.println("err:" + err + "\ngrad:" + grad);
		double ret = err * grad;;
		return ret;
	}
	private double calcStep(int index) {
		double ret = 
				regularizedPoints.stream().mapToDouble(p -> calcError(p, index) )
				.sum();
				System.out.println("step:" + ret);
				return ret;
	}
	private void updateParamater() {
		System.out.println(coef);
		for(int i = 0; i <= order; i++) {
			nextCoef.add(coef.get(i) - eta * calcStep(i));
		}
		coef.clear();
		coef.addAll(nextCoef);
		nextCoef.clear();
		System.out.println(coef);
	}
	public double calcRecallPolynomial(double x) {
		return calcPolynomial((x - xMean) / xStdDeviation);
	}
	public double getXMean() {
		return xMean;
	}
	public double getXstd() {
		return xStdDeviation;
	}
	public double getYMean() {
		return yMean;
	}
	public double getYstd() {
		return yStdDeviation;
	}
}
