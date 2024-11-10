package io.basc.framework.sequences;

import io.basc.framework.math.IntValue;
import io.basc.framework.math.NumberValue;
import io.basc.framework.util.Range;

public interface IntSequence extends NumberSequence {
	public static final Range<NumberValue> DEFAULT_RANGE = Range.closed(new IntValue(1),
			new IntValue(Integer.MAX_VALUE));

	@Override
	default Range<NumberValue> getRange() {
		return DEFAULT_RANGE;
	}

	@Override
	default NumberValue next(NumberValue step, Range<NumberValue> range) throws UnsupportedOperationException {
		int value = nextInt(step.getAsInt(), range);
		return new IntValue(value);
	}

	default int nextInt() {
		return nextInt(getStep().getAsInt(), getRange());
	}

	int nextInt(int step, Range<NumberValue> range) throws UnsupportedOperationException;
}
