package run.soeasy.framework.util.sequences;

import java.util.concurrent.atomic.AtomicInteger;

import run.soeasy.framework.util.Range;
import run.soeasy.framework.util.math.IntValue;
import run.soeasy.framework.util.math.NumberValue;

public class SimpleIntSequence implements IntSequence {
	private final AtomicInteger value;
	private final NumberValue step;

	public SimpleIntSequence() {
		this(0, 1);
	}

	public SimpleIntSequence(int initialValue, int step) {
		this.value = new AtomicInteger(initialValue);
		this.step = new IntValue(step);
	}

	@Override
	public NumberValue getStep() {
		return step;
	}

	public AtomicInteger getValue() {
		return value;
	}

	@Override
	public int nextInt(int step, Range<NumberValue> range) throws UnsupportedOperationException {
		if (!getRange().contains(range, NumberValue::compareTo)) {
			throw new UnsupportedSequenceRangeException(getRange(), range);
		}
		return value.addAndGet(step);
	}
}
