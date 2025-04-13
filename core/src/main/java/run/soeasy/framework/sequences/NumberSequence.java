package run.soeasy.framework.sequences;

import run.soeasy.framework.core.Range;
import run.soeasy.framework.core.math.IntValue;
import run.soeasy.framework.core.math.NumberValue;

public interface NumberSequence extends Sequence<NumberValue> {
	/**
	 * 默认步长为1
	 */
	public static final NumberValue DEFAULT_STEP = new IntValue(1);

	/**
	 * 范围
	 * 
	 * @return
	 */
	Range<NumberValue> getRange();

	/**
	 * 步长
	 * 
	 * @return
	 */
	default NumberValue getStep() {
		return DEFAULT_STEP;
	}

	@Override
	default NumberValue next() {
		return next(getStep(), getRange());
	}

	NumberValue next(NumberValue step, Range<NumberValue> range) throws UnsupportedOperationException;
}
