package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.*;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.*;
import java.net.URL;
import java.util.*;

public class SampleController implements Initializable{
	private static final double haba = 5;
	private static final double CANVAS_WIDTH = 630;
	private static final double CANVAS_HEIGHT = 540;
	private ArrayList<Point> points;
	private GraphicsContext gc;
	@FXML
	private Canvas regCanvas;
	
	@FXML
	protected void handleRegressPressed(ActionEvent e) {
		System.out.println("regress");
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
		gc.setFill(Color.BLACK);
		points.add(new Point(x, y));
		gc.fillOval(x - haba / 2 + CANVAS_WIDTH / 2, y - haba / 2 + CANVAS_HEIGHT / 2, haba, haba);
		System.out.println("x:" + x + "\t y:"+ y);
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		points = new ArrayList<Point>();
		gc = regCanvas.getGraphicsContext2D();
		regCanvas.getTransforms().add(new Affine(1, 0, 0, 0, -1, 540));
		clearCoordinate();
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
}
