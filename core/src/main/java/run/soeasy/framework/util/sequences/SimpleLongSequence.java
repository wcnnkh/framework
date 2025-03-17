package run.soeasy.framework.util.sequences;

import java.util.concurrent.atomic.AtomicLong;

import run.soeasy.framework.util.Range;
import run.soeasy.framework.util.math.LongValue;
import run.soeasy.framework.util.math.NumberValue;

public class SimpleLongSequence implements LongSequence {
	private final AtomicLong value;
	private final NumberValue step;

	public SimpleLongSequence() {
		this(0, 1);
	}

	public SimpleLongSequence(long initialValue, long step) {
		this.value = new AtomicLong(initialValue);
		this.step = new LongValue(step);
	}

	@Override
	public NumberValue getStep() {
		return step;
	}

	public AtomicLong getValue() {
		return value;
	}

	@Override
	public long nextLong(long step, Range<NumberValue> range) throws UnsupportedOperationException {
		if (!getRange().contains(range, NumberValue::compareTo)) {
			throw new UnsupportedSequenceRangeException(getRange(), range);
		}
		return value.addAndGet(step);
	}
}
