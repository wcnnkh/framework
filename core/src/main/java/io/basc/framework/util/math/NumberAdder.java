package io.basc.framework.util.math;

public abstract class NumberAdder extends NumberValue {
	private static final long serialVersionUID = 1L;

	public abstract void increment(NumberValue delta);

	public abstract void decrement(NumberValue delta);

}
