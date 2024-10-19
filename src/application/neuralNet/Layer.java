package application.neuralNet;

public class Layer {
	enum Activation {
		ReLU, tanh, sigmoid, LeakeyReLU // for Output Layer only
	}

	private final int numOfUnits;// Number of units of this layer (without bias)
	private final Activation active;// activation function of this layer
	private Matrix input;//input value into this layer(output value from previous layer)
	private Matrix output;//output value to next layer
	private Matrix weight;// weight between next layer;
	private Layer next;
	private double alpha;//for LeakyReLU
	private boolean isOutpuLayer;
	private Matrix gradient;
	private Matrix err;
	public Layer(int numOfUnits, Activation ActivationFunc) {
		this.numOfUnits = numOfUnits;
		this.active = ActivationFunc;
		input = new Matrix(numOfUnits, 1);
		err = new Matrix(numOfUnits, 1);
		output = new Matrix(numOfUnits + 1, 1);
	}

	public int getNumOfUnits() {
		return numOfUnits;
	}
	public void setIsOutputLayer(boolean is) {
		isOutpuLayer = is;
	}
	public void setWeight(Matrix weight) {
		assert ((weight.column == numOfUnits + 1) && (weight.row == next.numOfUnits));
		this.weight = weight;
	}

	public void setNextLayer(Layer next) {
		this.next = next;
	}

	public void setInput(Matrix input, boolean isInputLayer) {
		assert (this.input.row == input.row && input.column == 1);
		for (int i = 0; i < numOfUnits; i++) {
			this.input.setEntry(i, 0, input.getEntry(i, 0));
		}
		if(!isInputLayer) {
			activateInput();
		}else {
			this.output.setEntry(1, 0, 1);
			for (int i = 1; i < this.output.row; i++) {
				this.output.setEntry(i, 0, this.input.getEntry(i - 1, 0));
			}
		}
		if(!isOutpuLayer) {
			feedForward();
		}
	}

	public void feedForward() {
		next.setInput(Matrix.prodMatrix(weight, output), false);
	}
	public void setErr(Matrix err) {
		assert (this.err.row == err.row);
		for(int i = 0; i < this.err.row; i++) {
			this.err.setEntry(i, 0, err.getEntry(i, 0));
		}
	}
	private void activateInput() {
		this.output.setEntry(1, 1, 1);
		for (int i = 1; i < this.output.row; i++) {
			this.output.setEntry(i, 0, activateValue(this.input.getEntry(i - 1, 0)));
		}
	}
	
	private double activateValue(double value) {
		double ret = 0;
		switch (active) {
		case ReLU:
			ret = Math.max(0, value);
			break;
		case sigmoid:
			ret = sigmoid(value);
			break;
		case LeakeyReLU:
			ret = Math.max(value, alpha * value);
			break;
		case tanh:
			ret = Math.tanh(value);
			break;
		}
		return ret;
	}

	private double sigmoid(double value) {
		return 1 / (1 + Math.exp(-value));
	}
}
