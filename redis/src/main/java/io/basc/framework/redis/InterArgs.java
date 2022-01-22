package io.basc.framework.redis;

import java.io.Serializable;

import io.basc.framework.lang.Nullable;

public class InterArgs implements Serializable {
	private static final long serialVersionUID = 1L;
	private Aggregate aggregate;
	private double[] weights;

	public InterArgs aggregate(Aggregate aggregate) {
		this.aggregate = aggregate;
		return this;
	}

	public Aggregate getAggregate() {
		return aggregate;
	}

	@Nullable
	public double[] getWeights() {
		return weights;
	}

	public InterArgs weights(double... weights) {
		this.weights = weights;
		return this;
	}
}