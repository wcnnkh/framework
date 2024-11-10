package io.basc.framework.sequences;

import io.basc.framework.math.LongValue;
import io.basc.framework.math.NumberValue;
import io.basc.framework.util.Range;

public interface LongSequence extends NumberSequence {
	public static final Range<NumberValue> DEFAULT_RANGE = Range.closed(new LongValue(1),
			new LongValue(Long.MAX_VALUE));

	@Override
	default Range<NumberValue> getRange() {
		return DEFAULT_RANGE;
	}

	@Override
	default NumberValue next(NumberValue step, Range<NumberValue> range) throws UnsupportedOperationException {
		long value = nextLong(step.getAsLong(), range);
		return new LongValue(value);
	}

	default long nextLong() {
		return nextLong(1, getRange());
	}

	long nextLong(long step, Range<NumberValue> range) throws UnsupportedOperationException;
}
