package run.soeasy.framework.sequences;

import run.soeasy.framework.core.Range;

public abstract class FixedLengthStringSequence implements StringSequence {

	@Override
	public final String next(Range<Integer> lengthRange) throws UnsupportedOperationException {
		if (!getLengthRange().contains(lengthRange, Integer::compare)) {
			throw new UnsupportedSequenceRangeException(getLengthRange(), lengthRange);
		}
		return next();
	}

	@Override
	public abstract String next();
}
