package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class NumberAdder extends NumberValue {
	private static final long serialVersionUID = 1L;
	private NumberValue value;

	public NumberAdder() {
		this(new IntValue(0));
	}

	public NumberAdder(NumberValue value) {
		this.value = value;
	}

	@Override
	public NumberAdder abs() {
		return new NumberAdder(this.value.abs());
	}

	@Override
	public NumberValue getAsNumber() {
		return value;
	}

	@Override
	public NumberAdder add(NumberValue value) {
		return new NumberAdder(this.value.add(value));
	}

	public void decrement() {
		decrement(MINUS_ONE);
	}

	public void decrement(int delta) {
		decrement(new IntValue(delta));
	}

	public void decrement(long delta) {
		decrement(new LongValue(delta));
	}

	public void decrement(NumberValue delta) {
		this.value = this.value.subtract(delta);
	}

	@Override
	public NumberAdder divide(NumberValue value) {
		return new NumberAdder(this.value.divide(value));
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		return this.value.getAsBigDecimal();
	}

	@Override
	public BigInteger getAsBigInteger() {
		return this.value.getAsBigInteger();
	}

	@Override
	public String getAsString() {
		return this.value.getAsString();
	}

	public void increment() {
		increment(ONE);
	}

	public void increment(int delta) {
		increment(new IntValue(delta));
	}

	public void increment(long delta) {
		increment(new LongValue(delta));
	}

	public void increment(NumberValue delta) {
		this.value = this.value.add(delta);
	}

	@Override
	public NumberAdder multiply(NumberValue value) {
		return new NumberAdder(this.value.multiply(value));
	}

	@Override
	public NumberAdder pow(NumberValue value) {
		return new NumberAdder(this.value.pow(value));
	}

	@Override
	public NumberAdder remainder(NumberValue value) {
		return new NumberAdder(this.value.remainder(value));
	}

	@Override
	public NumberAdder subtract(NumberValue value) {
		return new NumberAdder(this.value.subtract(value));
	}
}
