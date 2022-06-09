package application.neuralNet;

import application.*;
import java.util.*;

public class NeuralNetworkRegressor implements Regressor {
	private final ArrayList<Point> points;
	private ArrayList<Point> regularizedPoints;
	private final Random rnd;
	private double eta;
	private final double xStdDeviation;
	private final double xMean;
	private final double yStdDeviation;
	private final double yMean;
	private int batch_size;

	public NeuralNetworkRegressor(ArrayList<Point> points) {
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
		regularizedPoints = new ArrayList<Point>();
		initializeRegularizePoint();
		rnd = new Random(System.currentTimeMillis());
	}

	@Override
	public double calcRecall(double x) {
		return 0;
	}

	@Override
	public void regress() {

	}
	private void initializeRegularizePoint() {
		regularizedPoints.addAll(points.stream()
								  .map(p -> new Point((p.getX() - xMean) / xStdDeviation, (p.getY() - yMean) / yStdDeviation))
								  .toList());
	}
}
