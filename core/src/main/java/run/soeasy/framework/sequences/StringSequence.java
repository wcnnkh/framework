package run.soeasy.framework.sequences;

import run.soeasy.framework.core.Range;

public interface StringSequence extends Sequence<String> {

	Range<Integer> getLengthRange();

	@Override
	default String next() {
		return next(getLengthRange());
	}

	String next(Range<Integer> lengthRange) throws UnsupportedOperationException;
}
