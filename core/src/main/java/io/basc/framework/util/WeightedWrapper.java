package io.basc.framework.util;

public class WeightedWrapper<W> extends Wrapper<W> implements Weighted {
	private final int weight;

	public WeightedWrapper(W wrappedTarget, int weight) {
		super(wrappedTarget);
		this.weight = weight;
	}

	@Override
	public int getWeight() {
		return weight;
	}
}
