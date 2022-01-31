package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.scene.transform.*;
import java.net.URL;
import java.util.*;

import application.PolynomialRegressor.GradientDescent;
import application.PolynomialRegressor.IterativeMethod;
import javafx.scene.control.*;
public class SampleController implements Initializable{
	private static final double haba = 5;
	private static final double CANVAS_WIDTH = 630;
	private static final double CANVAS_HEIGHT = 540;
	private ArrayList<Point> points;
	private GraphicsContext gc;
	private PolynomialRegressor pRegressor;
	private final ToggleGroup group = new ToggleGroup();
	@FXML
	private Canvas regCanvas;
	@FXML
	private RadioButton linear;
	@FXML
	private RadioButton polynomial;
	@FXML
	private TextField orderInputField;
	@FXML
	private ComboBox<String> Iterative;
	@FXML
	private ComboBox<String> GradDesc;
	@FXML
	private TextField BatchSize;
	@FXML
	private TextField numIterate;
	@FXML
	protected void handleRegressPressed(ActionEvent e) {
		System.out.println("regress");
		clearCoordinate();
		drawAxis();
		redrawPoint();
		if(group.getSelectedToggle().toString().matches(".*poly.*")) {
			// polynomial regression
			polynomialRegression();
		}else {
			// linear regression
			linearRegression();
		}
	}
	@FXML
	protected void handleClearPressed(ActionEvent e) {
		System.out.println("clear");
		clearCoordinate();
		points.clear();
		drawAxis();
	}
	@FXML
	protected void handleMouseClicked(MouseEvent e) {
		final double x = e.getX() - CANVAS_WIDTH / 2;
		final double y = e.getY() - CANVAS_HEIGHT / 2;
		Point p = new Point(x, y);
		gc.setFill(Color.BLACK);
		points.add(p);
		drawPoint(p);
		System.out.println("x:" + x + "\t y:"+ y);
	}
	@FXML 
	protected void comboBoxSelected(ActionEvent e) {
		System.out.println(GradDesc.getSelectionModel().getSelectedItem().toString());
		if(GradDesc.getSelectionModel().getSelectedItem().matches("ミニバッチ法")) {
			BatchSize.setDisable(false);
		}else {
			BatchSize.setDisable(true);
		}
	}
	private void handleRadioButtonSelected(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
		if(group.getSelectedToggle() != null) {
			if(group.getSelectedToggle().toString().matches(".*poly.*")) {
				nonPolynomialDisabled(false);
			}else {
				nonPolynomialDisabled(true);
			}
		}
	}
	private void nonPolynomialDisabled(boolean noPoly) {
		orderInputField.setDisable(noPoly);
		Iterative.setDisable(noPoly);
		GradDesc.setDisable(noPoly);
		numIterate.setDisable(noPoly);
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		points = new ArrayList<Point>();
		orderInputField.setDisable(true);
		gc = regCanvas.getGraphicsContext2D();
		regCanvas.getTransforms().add(new Affine(1, 0, 0, 0, -1, 540));
		clearCoordinate();
		linear.setToggleGroup(group);
		polynomial.setToggleGroup(group);
		linear.setSelected(true);
		BatchSize.setDisable(true);
		nonPolynomialDisabled(true);
		Iterative.getSelectionModel().select(0);
		GradDesc.getSelectionModel().select(0);
		group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) 
				-> handleRadioButtonSelected(ov, old_toggle, new_toggle));
		drawAxis();
	}
	private void drawAxis() {
		gc.setFill(Color.BLACK);
		gc.setStroke(Color.BLACK);
		//draw X axis
		gc.strokeLine(0, CANVAS_HEIGHT / 2, CANVAS_WIDTH, CANVAS_HEIGHT / 2);
		//draw Y axis
		gc.strokeLine(CANVAS_WIDTH / 2, 0, CANVAS_WIDTH / 2, CANVAS_HEIGHT);
		//draw origin
		gc.setFont(Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.ITALIC,15));
		gc.fillText("O", CANVAS_WIDTH /2 - 10,  CANVAS_HEIGHT / 2);
	}
	private Paramater calcParameter() {
		Paramater ret;
		final double sumXsquare = points.stream()
										.mapToDouble(p -> p.getX() * p.getX())
										.sum();
		final double sumX = points.stream()
								.mapToDouble(p -> p.getX())
								.sum();
		final double numOfPoints = (double)points.size();
		final double sumCrossProduct = points.stream()
											.mapToDouble(p -> p.getX() * p.getY())
											.sum();
		final double sumY = points.stream()
								.mapToDouble(p -> p.getY())
								.sum();
		final double det = sumXsquare * numOfPoints - sumX * sumX;
		final double a = (sumCrossProduct * numOfPoints - sumX * sumY) / det;
		final double b = (sumY * sumXsquare - sumCrossProduct * sumX) / det;
		ret = new Paramater(a, b);
		return ret;
	}
	private void clearCoordinate() {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
	}
	private class Paramater{
		double a, b;
		public Paramater(double a, double b) {
			this.a = a;
			this.b = b;
		}
	}
	private void redrawPoint() {
		points.stream()
				.forEach(this::drawPoint);
	}
	private void drawPoint(Point p) {
		gc.setFill(Color.BLACK);
		gc.fillOval(p.getX() - haba / 2 + CANVAS_WIDTH / 2, 
				p.getY() - haba / 2 + CANVAS_HEIGHT / 2, haba, haba);
	}
	private void plotPoint(Point p) {
		gc.setFill(Color.BLACK);
		gc.fillOval(p.getX() - 1 / 2 + CANVAS_WIDTH / 2, 
				p.getY() - 1 / 2 + CANVAS_HEIGHT / 2, 1, 1);
	}
	private void linearRegression() {
		final Paramater param = calcParameter();
		double sx = CANVAS_WIDTH / 2;
		double sy = param.b + CANVAS_HEIGHT / 2;
		double ex = sx;
		double ey = sy;
		while(ex <= CANVAS_WIDTH && ey <= CANVAS_HEIGHT) {
			ex += 100;
			ey += 100 * param.a;
		}
		while(sx >= 0 && sy >= 0) {
			sx -=100;
			sy -= 100 * param.a;
		}
		gc.setStroke(Color.BLUE);
		System.out.println("a:" + param.a + "\nb:" + param.b);
		System.out.println("sx:" + sx + "\nsy:" + sy +"\nex:"+ex+"\ney:"+ ey);
		gc.strokeLine(sx , sy , ex  , ey );
	}
	private void polynomialRegression() {
		final int order;
		final long numIterate;
		int miniBatch;
		GradientDescent gd;
		IterativeMethod iter;
		try {
			 order = Integer.parseInt(orderInputField.getText());
			 numIterate = Long.parseLong(this.numIterate.getText());
		}catch(NumberFormatException e) {
			e.printStackTrace();
			new Alert(Alert.AlertType.INFORMATION).showAndWait();
			return ;
		}
		gd = getGD();
		iter = getIM();
		if(gd == GradientDescent.MiniBatch) {
			miniBatch = Integer.parseInt(BatchSize.getText());
			pRegressor = new PolynomialRegressor(points, order)
					.setBathcSize(miniBatch)
					.setGradientDecsent(gd)
					.setIterativeMethod(iter)
					.setMaxIter(numIterate);
		}else {
			pRegressor = new PolynomialRegressor(points, order)
					.setGradientDecsent(gd)
					.setIterativeMethod(iter)
					.setMaxIter(numIterate);
		}
		pRegressor.regress();
		for(double x = -CANVAS_WIDTH / 2; x < CANVAS_WIDTH / 2; x+=1.0e-1) {
			double y = pRegressor.calcRecall(x);
			plotPoint(new Point(x, y ));
		}
	}
	private GradientDescent getGD() {
		String item = GradDesc.getSelectionModel().getSelectedItem().toString();
		if(item.equals("最急降下法")) {
			return GradientDescent.None;
		}else if(item.equals("ミニバッチ法")) {
			return GradientDescent.MiniBatch;
		}else {
			return GradientDescent.SGD;
		}
	}
	private IterativeMethod getIM() {
		String item = Iterative.getSelectionModel().getSelectedItem().toString();
		if(item.equals("Adam")) {
			return IterativeMethod.Adam;
		}
		return null;
	}
}
